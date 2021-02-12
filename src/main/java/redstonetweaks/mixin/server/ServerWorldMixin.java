package redstonetweaks.mixin.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.block.Block;
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

import redstonetweaks.helper.WorldHelper;
import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.interfaces.mixin.RTIServerWorld;
import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.setting.Tweaks;
import redstonetweaks.world.common.WorldTickHandler;
import redstonetweaks.world.common.WorldTickOptions;
import redstonetweaks.world.server.ServerNeighborUpdateScheduler;
import redstonetweaks.world.server.ServerIncompleteActionScheduler;
import redstonetweaks.world.server.ServerWorldTickHandler;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements RTIWorld, RTIServerWorld  {
	
	// This is to prevent the game from freezing due to repeater clocks while microTickMode is enabled
	// The value should be large enough that vanilla builds are unaffected
	private static final int BLOCK_EVENT_LIMIT = 100000;
	
	@Shadow @Final private MinecraftServer server;
	@Shadow @Final private boolean shouldTickTime;
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
	
	private ServerNeighborUpdateScheduler neighborUpdateScheduler;
	private ServerIncompleteActionScheduler incompleteActionScheduler;
	private boolean isProcessingBlockEvents;
	private int processedBlockEvents;
	private ArrayList<BlockEvent> blockEventList;
	private boolean shouldTickEntities;
	private Iterator<Entity> entitiesIt;
	
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
		neighborUpdateScheduler = new ServerNeighborUpdateScheduler((ServerWorld)(Object)this);
		incompleteActionScheduler = new ServerIncompleteActionScheduler((ServerWorld)(Object)this);
		blockEventList = new ArrayList<>();
	}
	
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;tickTime()V"))
	private void onTickRedirectTickTime(ServerWorld world) {
		handleTickTime();
	}
	
	@Redirect(method = "tickTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/timer/Timer;processEvents(Ljava/lang/Object;J)V"))
	private <T> void onTickTimeRedirectProcessEvents(Timer<T> timer, T server, long time) {
		if (!Tweaks.BugFixes.MC172213.get()) {
			timer.processEvents(server, time);
		}
	}
	
	@Inject(method = "addSyncedBlockEvent", cancellable = true, at = @At(value = "HEAD"))
	private void onAddSyncedBlockEventInjectAtHead(BlockPos pos, Block block, int type, int data, CallbackInfo ci) {
		if (Tweaks.Global.INSTANT_BLOCK_EVENTS.get()) {
			if (processBlockEvent(new BlockEvent(pos, block, type, data))) {
				server.getPlayerManager().sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), 64.0D, getRegistryKey(), new BlockEventS2CPacket(pos, block, type, data));
			}
			
			ci.cancel();
		}
	}
	
	@Redirect(method = "addSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;add(Ljava/lang/Object;)Z"))
	private <T> boolean onAddSyncedBlockEventRedirectAdd(ObjectLinkedOpenHashSet<T> set, T event) {
		if (set.add(event) && isProcessingBlockEvents && Tweaks.Global.RANDOMIZE_BLOCK_EVENTS.get()) {
			blockEventList.add((BlockEvent)event);
		}
		
		return true;
	}
	
	@Inject(method = "processSyncedBlockEvents", at = @At(value = "HEAD"))
	private void onProcessSyncedBlockEventsInjectAtHead(CallbackInfo ci) {
		isProcessingBlockEvents = true;
		processedBlockEvents = 0;
		
		if (Tweaks.Global.RANDOMIZE_BLOCK_EVENTS.get()) {
			blockEventList.ensureCapacity(syncedBlockEventQueue.size());
			
			for (BlockEvent event : syncedBlockEventQueue) {
				blockEventList.add(event);
			}
		}
	}
	
	@Redirect(method = "processSyncedBlockEvents", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;isEmpty()Z"))
	private boolean onProcessSyncedBlockEventsRedirectIsEmpty(ObjectLinkedOpenHashSet<BlockEvent> set) {
		return processedBlockEvents > BLOCK_EVENT_LIMIT || set.isEmpty();
	}
	
	@Redirect(method = "processSyncedBlockEvents", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;removeFirst()Ljava/lang/Object;"))
	private Object onProcessSyncedBlockEventsRedirectRemoveFirst(ObjectLinkedOpenHashSet<BlockEvent> set) {
		if (Tweaks.Global.RANDOMIZE_BLOCK_EVENTS.get()) {
			int randomIndex = random.nextInt(set.size());
			int lastIndex = blockEventList.size() - 1;
			
			Collections.swap(blockEventList, randomIndex, lastIndex);
			
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
		
		if (Tweaks.Global.RANDOMIZE_BLOCK_EVENTS.get()) {
			blockEventList.clear();
		}
	}
	
	@Inject(method = "processBlockEvent", at = @At(value = "RETURN"))
	private void onProcessBlockEventInjectAtReturn(BlockEvent event, CallbackInfoReturnable<Boolean> cir) {
		// When the world is ticking step by step the block event handler is removed
		// by the unfinished event
		if (((RTIWorld)this).immediateNeighborUpdates()) {
			((RTIWorld)this).removeBlockEventHandler(event.getPos());
		}
		
		processedBlockEvents++;
	}
	
	@Override
	public WorldTickHandler getWorldTickHandler() {
		return ((RTIMinecraftServer)server).getWorldTickHandler();
	}
	
	@Override
	public void tickTimeAccess() {
		if (shouldTickTime) {
	        long l = worldProperties.getTime() + 1l;
	        worldProperties.setTime(l);
	        if (worldProperties.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
	           setTimeOfDay(worldProperties.getTimeOfDay() + 1l);
	        }
	    }
	}
	
	@Override
	public boolean hasBlockEvent(BlockPos pos, int... types) {
		for (BlockEvent event : syncedBlockEventQueue) {
			if (event.getPos().equals(pos)) {
				if (types.length == 0) {
					return true;
				}
				
				for (int type : types) {
					if (event.getType() == type) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	@Override
	public ServerNeighborUpdateScheduler getNeighborUpdateScheduler() {
		return neighborUpdateScheduler;
	}
	
	@Override
	public ServerIncompleteActionScheduler getIncompleteActionScheduler() {
		return incompleteActionScheduler;
	}
	
	@Override
	public boolean normalWorldTicks() {
		ServerWorldTickHandler worldTickHandler = ((RTIMinecraftServer)getServer()).getWorldTickHandler();
		return worldTickHandler.doWorldTicks() && (!(worldTickHandler.tickInProgress() || Tweaks.Global.WORLD_TICK_OPTIONS.get().getMode() == WorldTickOptions.Mode.STEP_BY_STEP) || WorldHelper.stepByStepFilter(this));
	}
	
	@Override
	public boolean immediateNeighborUpdates() {
		boolean hasScheduledNeighborUpdates = getNeighborUpdateScheduler().hasScheduledUpdates();
		return normalWorldTicks() || !(hasScheduledNeighborUpdates || Tweaks.Global.SHOW_NEIGHBOR_UPDATES.get());
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
					clearWeatherTime--;
					
					thunderTime = isThundering ? 0 : 1;
					rainTime = raining ? 0 : 1;
					
					isThundering = false;
					raining = false;
				} else {
					if (thunderTime > 0) {
						thunderTime--;
						
						if (thunderTime == 0) {
							isThundering = !isThundering;
						}
					} else if (isThundering) {
						thunderTime = random.nextInt(12000) + 3600;
					} else {
						thunderTime = random.nextInt(168000) + 12000;
					}
					
					if (rainTime > 0) {
						rainTime--;
						
						if (rainTime == 0) {
							raining = !raining;
						}
					} else if (raining) {
						rainTime = random.nextInt(12000) + 12000;
					} else {
						rainTime = random.nextInt(168000) + 12000;
					}
				}
				
				worldProperties.setClearWeatherTime(clearWeatherTime);
				worldProperties.setThunderTime(thunderTime);
				worldProperties.setRainTime(rainTime);
				
				worldProperties.setThundering(isThundering);
				worldProperties.setRaining(raining);
			}
			
			thunderGradientPrev = thunderGradient;
			if (properties.isThundering()) {
				thunderGradient = thunderGradient + 0.01F;
			} else {
				thunderGradient = thunderGradient - 0.01F;
			}
			thunderGradient = MathHelper.clamp(thunderGradient, 0.0F, 1.0F);

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
				
				setTimeOfDay(l - l % 24000L);
			}
			
			this.wakeSleepingPlayers();
			if (getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
				resetWeather();
			}
		}
		
		calculateAmbientDarkness();
	}
	
	private void handleTickTime() {
		if (Tweaks.BugFixes.MC172213.get()) {
			if (shouldTickTime) {
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
		
		if (Tweaks.Global.RANDOMIZE_BLOCK_EVENTS.get()) {
			blockEventList.ensureCapacity(syncedBlockEventQueue.size());
			
			for (BlockEvent event : syncedBlockEventQueue) {
				blockEventList.add(event);
			}
		}
	}
	
	@Override
	public boolean tryContinueProcessingBlockEvents() {
		if (isProcessingBlockEvents) {
			if (processedBlockEvents > BLOCK_EVENT_LIMIT || syncedBlockEventQueue.isEmpty()) {
				isProcessingBlockEvents = false;
				
				if (Tweaks.Global.RANDOMIZE_BLOCK_EVENTS.get()) {
					blockEventList.clear();
				}
				
				return false;
			} else {
				BlockEvent blockEvent;
				
				if (Tweaks.Global.RANDOMIZE_BLOCK_EVENTS.get()) {
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
		}
		
		return false;
	}
	
	@Override
	public void startTickingEntities(Profiler profiler) {
		boolean chunksLoaded = !players.isEmpty() || !getForcedChunks().isEmpty();
		if (chunksLoaded) {
			resetIdleTimeout();
		}
		
		shouldTickEntities = chunksLoaded || idleTimeout++ < 300;
		
		if (shouldTickEntities) {
			if (enderDragonFight != null) {
				enderDragonFight.tick();
			}

			inEntityTick = true;
			
			entitiesIt = entitiesById.values().iterator();
		}
	}
	
	@Override
	public boolean tryContinueTickingEntities(Profiler profiler) {
		if (inEntityTick && shouldTickEntities) {
			boolean keepTicking = true;
			
			while (keepTicking) {
				Entity entity;
				while (true) {
					if (!entitiesIt.hasNext()) {
						inEntityTick = false;
						
						for (Entity entityToLoad : entitiesToLoad) {
							loadEntityUnchecked(entityToLoad);
						}
						
						return false;
					}
					
					entity = entitiesIt.next();
					Entity vehicle = entity.getVehicle();
					
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
					
					if (vehicle == null) {
						break;
					}
					
					if (vehicle.removed || !vehicle.hasPassenger(entity)) {
						entity.stopRiding();
						
						break;
					}
				}
				
				profiler.push("tick");
				
				if (!entity.removed && !(entity instanceof EnderDragonPart)) {
					tickEntity((entityTotick) -> tickEntity(entityTotick), entity);
				}
				
				profiler.swap("remove");
				
				if (entity.removed) {
					removeEntityFromChunk(entity);
					unloadEntity(entity);
					
					entitiesIt.remove();
				}
				
				profiler.pop();
				
				if (incompleteActionScheduler.hasScheduledActions()) {
					keepTicking = false;
				}
				
				if (neighborUpdateScheduler.hasScheduledUpdates()) {
					keepTicking = false;
				}
			}
			
			return true;
		}
		
		return false;
	}
}
