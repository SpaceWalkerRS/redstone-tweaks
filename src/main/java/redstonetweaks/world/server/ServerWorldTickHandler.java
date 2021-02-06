package redstonetweaks.world.server;

import java.util.Iterator;
import java.util.function.BooleanSupplier;

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;

import redstonetweaks.helper.WorldHelper;
import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.interfaces.mixin.RTIServerChunkManager;
import redstonetweaks.interfaces.mixin.RTIServerTickScheduler;
import redstonetweaks.interfaces.mixin.RTIServerWorld;
import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.packet.types.DoWorldTicksPacket;
import redstonetweaks.packet.types.TaskSyncPacket;
import redstonetweaks.packet.types.TickPausePacket;
import redstonetweaks.packet.types.TickStatusPacket;
import redstonetweaks.packet.types.WorldSyncPacket;
import redstonetweaks.packet.types.WorldTimeSyncPacket;
import redstonetweaks.setting.Tweaks;
import redstonetweaks.world.common.WorldTickHandler;
import redstonetweaks.world.common.WorldTickOptions;

public class ServerWorldTickHandler extends WorldTickHandler {
	
	private final MinecraftServer server;
	
	private long ticks;
	private boolean shouldUpdateStatus;
	
	public ServerWorldTickHandler(MinecraftServer server) {
		super();
		this.server = server;
		this.ticks = Long.MAX_VALUE;
		this.shouldUpdateStatus = true;
	}
	
	public void pause() {
		doWorldTicks = false;
		
		syncPause();
	}
	
	public void resume() {
		advance(Long.MAX_VALUE);
	}
	
	public void advance(long count) {
		doWorldTicks = true;
		ticks = count;
		
		syncPause();
	}
	
	public void tick(BooleanSupplier shouldKeepTicking) {
		if (doWorldTicks()) {
			boolean stepByStep = Tweaks.Global.WORLD_TICK_OPTIONS.get().getMode() == WorldTickOptions.Mode.STEP_BY_STEP;
			
			if (stepByStep || tickInProgress()) {
				if (!stepByStep || server.getTicks() % Tweaks.Global.WORLD_TICK_OPTIONS.get().getInterval() == 0) {
					tickStepByStep(shouldKeepTicking);
				}
				
				broadcastChunkData();
			} else {
				tickWorldsNormally(shouldKeepTicking);
			}
			
			if (ticks == 0) {
				pause();
			}
		} else {
			broadcastChunkData();
		}
	}
	
	private void tickWorldsNormally(BooleanSupplier shouldKeepTicking) {
		for (ServerWorld world : server.getWorlds()) {
			if (server.getTicks() % 20 == 0) {
				server.getPlayerManager().sendToDimension(new WorldTimeUpdateS2CPacket(world.getTime(), world.getTimeOfDay(), world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)), world.getRegistryKey());
			}
			
			tickWorldNormally(world, shouldKeepTicking);
		}
		if (Tweaks.BugFixes.MC172213.get()) {
			for (ServerWorld world : server.getWorlds()) {
				((RTIServerWorld)world).tickTimeAccess();
			}
		}
		
		ticks--;
	}
	
	private void tickWorldNormally(ServerWorld world, BooleanSupplier shouldKeepTicking) {
		inWorldTick = true;
		
		world.tick(shouldKeepTicking);
		
		inWorldTick = false;
	}
	
	private void tickStepByStep(BooleanSupplier shouldKeepTicking) {
		if (shouldUpdateStatus) {
			updateStatus();
			shouldUpdateStatus = false;
		}
		
		switch (status) {
		case START_TICK:
			startTick();
			break;
		case TICKING_WORLDS:
			if (currentWorld == null) {
				shouldUpdateStatus = true;
			} else {
				if (WorldHelper.stepByStepFilter(currentWorld)) {
					tickWorldNormally((ServerWorld)currentWorld, shouldKeepTicking);
					
					switchWorld();
				} else {
					inWorldTick = true;
					
					tickWorld(shouldKeepTicking);
					
					inWorldTick = false;
				}
			}
			break;
		case END_TICK:
			endTick();
		default:
			shouldUpdateStatus = true;
			break;
		}
	}
	
	private void startTick() {
		syncClientWorldTime();
		
		initCurrentWorld();
		clearCurrentTask();
		
		for (ServerWorld world : server.getWorlds()) {
			if (world.getTime() % 20 == 0) {
				server.getPlayerManager().sendToDimension(new WorldTimeUpdateS2CPacket(world.getTime(), world.getTimeOfDay(), world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)), world.getRegistryKey());
			}
			
			((RTIServerWorld)world).getNeighborUpdateScheduler().resetTickTime();
		}
		
		shouldUpdateStatus = true;
	}
	
	private void endTick() {
		if (Tweaks.BugFixes.MC172213.get()) {
			for (ServerWorld world : server.getWorlds()) {
				((RTIServerWorld)world).tickTimeAccess();
			}
		}
		
		clearCurrentTask();
		updateStatus();
		shouldUpdateStatus = true;
		
		ticks--;
	}
	
	private void tickWorld(BooleanSupplier shouldKeepTicking) {
		ServerNeighborUpdateScheduler neighborUpdateScheduler = ((RTIServerWorld)currentWorld).getNeighborUpdateScheduler();
		ServerIncompleteActionScheduler unfinishedEventScheduler = ((RTIServerWorld)currentWorld).getIncompleteActionScheduler();
		
		boolean hasNeighborUpdates = neighborUpdateScheduler.hasScheduledNeighborUpdates();
		boolean hasScheduledEvents = unfinishedEventScheduler.hasScheduledActions();
		
		neighborUpdateScheduler.tick();
		if (hasNeighborUpdates) {
		} else if (hasScheduledEvents) {
			unfinishedEventScheduler.tick();
		} else if (doTasks) {
			if (shouldSwitchTask) {
				nextTask();
			}
			
			switch (currentTask) {
			case TICK_WORLD_BORDER:
				tickWorldBorder();
				break;
			case PROCESS_WEATHER:
				processWeather();
				break;
			case TICK_TIME:
				processTime();
				break;
			case TICK_CHUNK_SOURCE:
				tickChunkSource(shouldKeepTicking);
				break;
			case TICK_BLOCKS:
				tickBlocks();
				break;
			case TICK_FLUIDS:
				tickFluids();
				break;
			case TICK_RAIDS:
				tickRaidManager();
				break;
			case PROCESS_BLOCK_EVENTS:
				processBlockEvents();
				break;
			case TICK_ENTITIES:
				tickEntities();
				break;
			case TICK_BLOCK_ENTITIES:
				tickBlockEntities();
				break;
			case SWITCH_WORLD:
				switchWorld();
				break;
			default:
				break;
			}
		}
	}
	
	private void updateStatus() {
		setStatus(status.next());
		syncStatus();
	}
	
	private void nextWorld() {
		Iterator<ServerWorld> worlds = server.getWorlds().iterator();
		while (worlds.hasNext()) {
			if (worlds.next() == currentWorld) {
				setCurrentWorld(worlds.hasNext() ? worlds.next() : null);
				syncCurrentWorld();
				break;
			}
		}
	}
	
	private void initCurrentWorld() {
		setCurrentWorld(server.getWorlds().iterator().next());
		syncCurrentWorld();
	}
	
	private void nextTask() {
		setCurrentTask(currentTask.next());
		syncCurrentTask();
	}
	
	private void clearCurrentTask() {
		setCurrentTask(Task.NONE);
		syncCurrentTask();
	}
	
	public long getWorldTime() {
		return server.getOverworld().getTime();
	}
	
	private void tickWorldBorder() {
		profiler.push("world border");
		
		currentWorld.getWorldBorder().tick();
		
		profiler.pop();
	}
	
	private void processWeather() {
		profiler.push("weather");
		
		((RTIServerWorld)currentWorld).processWeather();
		
		profiler.pop();
	}
	
	private void processTime() {
		((RTIServerWorld)currentWorld).processTime();
	}
	
	private void tickChunkSource(BooleanSupplier shouldKeepTicking) {
		profiler.push("chunkSource");
		
		((ServerWorld)currentWorld).getChunkManager().tick(shouldKeepTicking);
		
		profiler.pop();
	}
	
	private void tickBlocks() {
		profiler.push("tickPending");
		
		if (!currentWorld.isDebugWorld()) {
			if (shouldSwitchTask) {
				shouldSwitchTask = false;
				
				((RTIServerTickScheduler)currentWorld.getBlockTickScheduler()).startTicking();
			}
			shouldSwitchTask = !((RTIServerTickScheduler)currentWorld.getBlockTickScheduler()).tryContinueTicking();
		}
		
		profiler.pop();
	}
	
	private void tickFluids() {
		profiler.push("tickPending");
		
		if (!currentWorld.isDebugWorld()) {
			if (shouldSwitchTask) {
				shouldSwitchTask = false;
				
				((RTIServerTickScheduler)currentWorld.getFluidTickScheduler()).startTicking();
			}
			shouldSwitchTask = !((RTIServerTickScheduler)currentWorld.getFluidTickScheduler()).tryContinueTicking();
		}
		
		profiler.pop();
	}
	
	private void tickRaidManager() {
		profiler.push("raid");
		
		((RTIServerWorld)currentWorld).tickRaidManager();
		
		profiler.pop();
	}
	
	private void processBlockEvents() {
		profiler.push("blockEvents");
		
		if (shouldSwitchTask) {
			shouldSwitchTask = false;
			
			((RTIServerWorld)currentWorld).startProcessingBlockEvents();
		}
		shouldSwitchTask = !((RTIServerWorld)currentWorld).tryContinueProcessingBlockEvents();
		
		profiler.pop();
	}
	
	private void tickEntities() {
		profiler.push("entities");
		
		((RTIServerWorld)currentWorld).tickEntities(profiler);
		
		profiler.pop();
	}
	
	private void tickBlockEntities() {
		if (shouldSwitchTask) {
			shouldSwitchTask = false;
			
			((RTIWorld)currentWorld).startTickingBlockEntities(true);
		}
		shouldSwitchTask = !((RTIWorld)currentWorld).tryContinueTickingBlockEntities();
	}
	
	private void switchWorld() {
		nextWorld();
		if (currentWorld == null) {
			shouldUpdateStatus = true;
		}
	}
	
	private void broadcastChunkData() {
		for (ServerWorld world : server.getWorlds()) {
			ServerChunkManager chunkManager = world.getChunkManager();
			((RTIServerChunkManager)chunkManager).broadcastChunkData();
		}
	}
	
	private void syncPause() {
		DoWorldTicksPacket packet = new DoWorldTicksPacket(doWorldTicks());
		((RTIMinecraftServer)server).getPacketHandler().sendPacket(packet);
	}
	
	private void syncStatus() {
		TickStatusPacket packet = new TickStatusPacket(status);
		((RTIMinecraftServer)server).getPacketHandler().sendPacket(packet);
	}
	
	private void syncCurrentWorld() {
		WorldSyncPacket packet = new WorldSyncPacket(currentWorld);
		((RTIMinecraftServer)server).getPacketHandler().sendPacket(packet);
	}
	
	private void syncClientWorldTime() {
		WorldTimeSyncPacket packet = new WorldTimeSyncPacket(getWorldTime());
		((RTIMinecraftServer)server).getPacketHandler().sendPacket(packet);
	}
	
	private void syncCurrentTask() {
		TaskSyncPacket packet = new TaskSyncPacket(currentTask);
		((RTIMinecraftServer)server).getPacketHandler().sendPacket(packet);
	}
	
	public void onTickPausePacketReceived(TickPausePacket packet) {
		if (packet.event == TickPausePacket.PAUSE) {
			if (doWorldTicks()) {
				pause();
			} else {
				resume();
			}
		} else
		if (packet.event == TickPausePacket.ADVANCE) {
			if (!doWorldTicks()) {
				advance(1);
			}
		}
	}
}
