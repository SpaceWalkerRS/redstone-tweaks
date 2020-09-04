package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Npc;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.raid.RaidManager;
import net.minecraft.world.GameRules;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.timer.Timer;

import redstonetweaks.helper.MinecraftServerHelper;
import redstonetweaks.helper.ServerWorldHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.world.server.ServerNeighborUpdateScheduler;
import redstonetweaks.world.server.ServerUnfinishedEventScheduler;
import redstonetweaks.world.server.ServerWorldTickHandler;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements WorldHelper, ServerWorldHelper  {

	@Shadow @Final private MinecraftServer server;
	@Shadow @Final private boolean field_25143;
	@Shadow @Final private ServerWorldProperties worldProperties;
	@Shadow @Final private ObjectLinkedOpenHashSet<BlockEvent> syncedBlockEventQueue;
	@Shadow @Final private List<ServerPlayerEntity> players;
	@Shadow @Final private RaidManager raidManager;
	@Shadow @Final private EnderDragonFight enderDragonFight;
	@Shadow @Final private Int2ObjectMap<Entity> entitiesById;
	@Shadow @Final private Queue<Entity> entitiesToLoad;
	@Shadow private boolean allPlayersSleeping;
	@Shadow private int idleTimeout;
	@Shadow boolean inEntityTick;
	
	private ServerNeighborUpdateScheduler serverNeighborUpdateScheduler;
	private ServerUnfinishedEventScheduler unfinishedEventScheduler;
	private boolean isProcessingBlockEvents = false;
	private ArrayList<BlockEvent> blockEventList = new ArrayList<>();
	
	protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryKey, DimensionType dimensionType, Supplier<Profiler> supplier, boolean bl, boolean bl2, long l) {
		super(properties, registryKey, dimensionType, supplier, bl, bl2, l);
	}
	
	@Shadow protected abstract void tickTime();
	@Shadow public abstract void setTimeOfDay(long l);
	@Shadow protected abstract void wakeSleepingPlayers();
	@Shadow protected abstract void resetWeather();
	@Shadow public abstract LongSet getForcedChunks();
	@Shadow public abstract void resetIdleTimeout();
	@Shadow protected abstract void loadEntityUnchecked(Entity entity);
	@Shadow public abstract void tickEntity(Entity entity);
	@Shadow protected abstract void removeEntityFromChunk(Entity entity);
	@Shadow public abstract void unloadEntity(Entity entity);
	@Shadow protected abstract boolean processBlockEvent(BlockEvent event);
	
	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void onInitInjectAtReturn(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> registryKey, DimensionType dimensionType, WorldGenerationProgressListener worldGenerationProgressListener, ChunkGenerator chunkGenerator, boolean bl, long l, List<Spawner> list, boolean bl2, CallbackInfo ci) {
		serverNeighborUpdateScheduler = new ServerNeighborUpdateScheduler((ServerWorld)(Object)this);
		unfinishedEventScheduler = new ServerUnfinishedEventScheduler((ServerWorld)(Object)this);
	}
	
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;tickTime()V"))
	private void onTickRedirectTickTime(ServerWorld world) {
		handleTickTime();
	}
	
	@Redirect(method = "tickTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/timer/Timer;processEvents(Ljava/lang/Object;J)V"))
	private <T> void onTickTimeRedirectProcessEvents(Timer<T> timer, T server, long time) {
		if (!BUG_FIXES.get(MC172213)) {
			timer.processEvents(server, time);
		}
	}
	
	@Redirect(method = "addSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;add(Ljava/lang/Object;)Z"))
	private <T> boolean onAddSyncedBlockEventRedirectAdd(ObjectLinkedOpenHashSet<T> set, T event) {
		if (set.add(event) && isProcessingBlockEvents && GLOBAL.get(RANDOMIZE_BLOCK_EVENTS)) {
			blockEventList.add((BlockEvent)event);
		}
		return true;
	}
	
	@Inject(method = "processSyncedBlockEvents", at = @At(value = "HEAD"))
	private void onProcessSyncedBlockEventsInjectAtHead(CallbackInfo ci) {
		isProcessingBlockEvents = true;
		
		if (GLOBAL.get(RANDOMIZE_BLOCK_EVENTS)) {
			blockEventList.ensureCapacity(syncedBlockEventQueue.size());
			for (BlockEvent event : syncedBlockEventQueue) {
				blockEventList.add(event);
			}
		}
	}
	
	@Redirect(method = "processSyncedBlockEvents", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;isEmpty()Z"))
	private boolean onProcessSyncedblockEventsRedirectIsEmpty(ObjectLinkedOpenHashSet<BlockEvent> set) {
		return set.isEmpty();
	}
	
	@Redirect(method = "processSyncedBlockEvents", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;removeFirst()Ljava/lang/Object;"))
	private Object onProcessSyncedBlockEventsRedirectRemoveFirst(ObjectLinkedOpenHashSet<BlockEvent> set) {
		if (GLOBAL.get(RANDOMIZE_BLOCK_EVENTS)) {
			int index = random.nextInt(set.size());
			int lastIndex = blockEventList.size() - 1;
			Collections.swap(blockEventList, index, lastIndex);
			BlockEvent event = blockEventList.remove(lastIndex);
			set.remove(event);
			return event;
		} else {
			return set.removeFirst();
		}
	}
	
	@Inject(method = "processSyncedBlockEvents", at = @At(value = "RETURN"))
	private void onProcessSyncedBlockEventsInjectAtReturn(CallbackInfo ci) {
		isProcessingBlockEvents = false;
		
		if (GLOBAL.get(RANDOMIZE_BLOCK_EVENTS)) {
			blockEventList.clear();
		}
	}
	
	@Override
	public void tickTimeAccess() {
		if (field_25143) {
	        long l = worldProperties.getTime() + 1l;
	        worldProperties.setTime(l);
	        if (worldProperties.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
	           setTimeOfDay(worldProperties.getTimeOfDay() + 1l);
	        }
	    }
	}
	
	@Override
	public boolean hasBlockEvent(BlockPos pos) {
		for (BlockEvent event : syncedBlockEventQueue) {
			if (event.getPos().equals(pos)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isProcessingBlockEvents() {
		return isProcessingBlockEvents;
	}
	
	@Override
	public ServerNeighborUpdateScheduler getNeighborUpdateScheduler() {
		return serverNeighborUpdateScheduler;
	}
	
	@Override
	public ServerUnfinishedEventScheduler getUnfinishedEventScheduler() {
		return unfinishedEventScheduler;
	}
	
	@Override
	public boolean tickWorldsNormally() {
		ServerWorldTickHandler worldTickHandler = ((MinecraftServerHelper)getServer()).getWorldTickHandler();
		return worldTickHandler.doWorldTicks() && !(worldTickHandler.isTickingWorlds() || GLOBAL.get(SHOW_PROCESSING_ORDER) > 0);
	}
	
	@Override
	public boolean updateNeighborsNormally() {
		boolean hasScheduledNeighborUpdates = getNeighborUpdateScheduler().hasScheduledNeighborUpdates();
		return tickWorldsNormally() || !(hasScheduledNeighborUpdates || GLOBAL.get(SHOW_NEIGHBOR_UPDATES));
	}
	
	@Override
	public void processWeather() {
		boolean isRaining = this.isRaining();
		if (getDimension().hasSkyLight()) {
			if (getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
				int clearWeatherTime = worldProperties.getClearWeatherTime();
				int thunderTime = worldProperties.getThunderTime();
				int rainTime = worldProperties.getRainTime();
				boolean isThundering = properties.isThundering();
				boolean raining = properties.isRaining();
				if (clearWeatherTime > 0) {
					--clearWeatherTime;
					thunderTime = isThundering ? 0 : 1;
					rainTime = raining ? 0 : 1;
					isThundering = false;
					raining = false;
				} else {
					if (thunderTime > 0) {
						--thunderTime;
						if (thunderTime == 0) {
							isThundering = !isThundering;
						}
					} else if (isThundering) {
						thunderTime = random.nextInt(12000) + 3600;
					} else {
						thunderTime = random.nextInt(168000) + 12000;
					}

					if (rainTime > 0) {
						--rainTime;
						if (rainTime == 0) {
							raining = !raining;
						}
					} else if (raining) {
						rainTime = random.nextInt(12000) + 12000;
					} else {
						rainTime = random.nextInt(168000) + 12000;
					}
				}

				worldProperties.setThunderTime(thunderTime);
				worldProperties.setRainTime(rainTime);
				worldProperties.setClearWeatherTime(clearWeatherTime);
				worldProperties.setThundering(isThundering);
				worldProperties.setRaining(raining);
			}

			thunderGradientPrev = thunderGradient;
			if (properties.isThundering()) {
				thunderGradient = (float)(thunderGradient + 0.01d);
			} else {
				thunderGradient = (float)(thunderGradient - 0.01d);
			}

			thunderGradient = MathHelper.clamp(thunderGradient, 0.0f, 1.0f);
			rainGradientPrev = rainGradient;
			if (properties.isRaining()) {
				rainGradient = (float)(rainGradient + 0.01d);
			} else {
				rainGradient = (float)(rainGradient - 0.01d);
			}

			rainGradient = MathHelper.clamp(rainGradient, 0.0f, 1.0f);
		}

		if (rainGradientPrev != rainGradient) {
			server.getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED, rainGradient), getRegistryKey());
		}

		if (thunderGradientPrev != thunderGradient) {
			server.getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED, thunderGradient), getRegistryKey());
		}

		if (isRaining != isRaining()) {
			if (isRaining) {
				server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_STOPPED, 0.0f));
			} else {
				server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_STARTED, 0.0f));
			}

			server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED, rainGradient));
			server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED, thunderGradient));
		}

		if (allPlayersSleeping && players.stream().noneMatch((serverPlayerEntity) -> {
			return !serverPlayerEntity.isSpectator() && !serverPlayerEntity.isSleepingLongEnough();
		})) {
			allPlayersSleeping = false;
			if (getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
				long l = properties.getTimeOfDay() + 24000l;
				setTimeOfDay(l - l % 24000l);
			}

			this.wakeSleepingPlayers();
			if (getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
				resetWeather();
			}
		}

		calculateAmbientDarkness();
	}
	
	private void handleTickTime() {
		if (BUG_FIXES.get(MC172213)) {
			if (field_25143) {
				worldProperties.getScheduledEvents().processEvents(server, worldProperties.getTime());
			}
		} else {
			tickTime();
		}
	}
	
	@Override
	public void processTime() {
		handleTickTime();
	}
	
	@Override
	public void tickRaidManager() {
		raidManager.tick();
	}
	
	@Override
	public void startProcessingBlockEvents() {
		isProcessingBlockEvents = true;
		if (GLOBAL.get(RANDOMIZE_BLOCK_EVENTS)) {
			blockEventList.ensureCapacity(syncedBlockEventQueue.size());
			for (BlockEvent event : syncedBlockEventQueue) {
				blockEventList.add(event);
			}
		}
	}
	
	@Override
	public boolean tryContinueProcessingBlockEvents() {
		if (isProcessingBlockEvents) {
			if (syncedBlockEventQueue.isEmpty()) {
				isProcessingBlockEvents = false;
				if (GLOBAL.get(RANDOMIZE_BLOCK_EVENTS)) {
					blockEventList.clear();
				}
				
				return false;
			} else {
				BlockEvent blockEvent;
				if (GLOBAL.get(RANDOMIZE_BLOCK_EVENTS)) {
					int index = random.nextInt(syncedBlockEventQueue.size());
					int lastIndex = blockEventList.size() - 1;
					Collections.swap(blockEventList, index, lastIndex);
					blockEvent = blockEventList.remove(lastIndex);
					syncedBlockEventQueue.remove(blockEvent);
				} else {
					blockEvent = syncedBlockEventQueue.removeFirst();
				}
				
				if (processBlockEvent(blockEvent)) {
					server.getPlayerManager().sendToAround(null, blockEvent.getPos().getX(), blockEvent.getPos().getY(), blockEvent.getPos().getZ(), 64.0D, getRegistryKey(), new BlockEventS2CPacket(blockEvent.getPos(), blockEvent.getBlock(), blockEvent.getType(), blockEvent.getData()));
				}
				
				return true;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public void tickEntities(Profiler profiler) {
		boolean chunksLoaded = !players.isEmpty() || !getForcedChunks().isEmpty();
		if (chunksLoaded) {
			resetIdleTimeout();
		}

		if (chunksLoaded || idleTimeout++ < 300) {
			if (enderDragonFight != null) {
				enderDragonFight.tick();
			}

			inEntityTick = true;
			@SuppressWarnings("rawtypes")
			ObjectIterator objectIterator = entitiesById.int2ObjectEntrySet().iterator();

			label164: while (true) {
				Entity entity;
				while (true) {
					if (!objectIterator.hasNext()) {
						inEntityTick = false;

						Entity entity3;
						while ((entity3 = entitiesToLoad.poll()) != null) {
							loadEntityUnchecked(entity3);
						}
						
						break label164;
					}

					@SuppressWarnings({ "unchecked", "rawtypes" })
					Entry<Entity> entry = (Entry) objectIterator.next();
					entity = entry.getValue();
					Entity entity2 = entity.getVehicle();
					if (!server.shouldSpawnAnimals() && (entity instanceof AnimalEntity || entity instanceof WaterCreatureEntity)) {
						entity.remove();
					}

					if (!server.shouldSpawnNpcs() && entity instanceof Npc) {
						entity.remove();
					}

					profiler.push("checkDespawn");
					if (!entity.removed) {
						entity.checkDespawn();
					}

					profiler.pop();
					if (entity2 == null) {
						break;
					}

					if (entity2.removed || !entity2.hasPassenger(entity)) {
						entity.stopRiding();
						break;
					}
				}

				profiler.push("tick");
				if (!entity.removed && !(entity instanceof EnderDragonPart)) {
					tickEntity(((ServerWorld)(Object)this)::tickEntity, entity);
				}

				profiler.pop();
				profiler.push("remove");
				if (entity.removed) {
					removeEntityFromChunk(entity);
					objectIterator.remove();
					unloadEntity(entity);
				}

				profiler.pop();
			}
		}

		profiler.pop();
	}
}
