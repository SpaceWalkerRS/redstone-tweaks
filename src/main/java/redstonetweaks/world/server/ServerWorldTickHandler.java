package redstonetweaks.world.server;

import java.util.Iterator;
import java.util.function.BooleanSupplier;

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import redstonetweaks.interfaces.RTIMinecraftServer;
import redstonetweaks.interfaces.RTIWorld;
import redstonetweaks.interfaces.RTIServerWorld;
import redstonetweaks.interfaces.RTIServerTickScheduler;
import redstonetweaks.interfaces.RTIServerChunkManager;
import redstonetweaks.packet.TaskSyncPacket;
import redstonetweaks.packet.TickPausePacket;
import redstonetweaks.packet.TickStatusPacket;
import redstonetweaks.packet.WorldSyncPacket;
import redstonetweaks.packet.DoWorldTicksPacket;
import redstonetweaks.packet.WorldTimeSyncPacket;
import redstonetweaks.setting.Settings;
import redstonetweaks.world.common.WorldTickHandler;

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
			int interval = Settings.Global.SHOW_PROCESSING_ORDER.get();
			
			if (interval > 0 || tickInProgress()) {
				if (interval == 0 || server.getTicks() % interval == 0) {
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
			
			inWorldTick = true;
			
			world.tick(shouldKeepTicking);
			
			inWorldTick = false;
		}
		if (Settings.BugFixes.MC172213.get()) {
			for (ServerWorld world : server.getWorlds()) {
				((RTIServerWorld)world).tickTimeAccess();
			}
		}
		
		ticks--;
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
			if (currentWorld != null) {
				inWorldTick = true;
				
				tickWorld(shouldKeepTicking);
				
				inWorldTick = false;
			} else {
				shouldUpdateStatus = true;
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
		if (Settings.BugFixes.MC172213.get()) {
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
		ServerUnfinishedEventScheduler unfinishedEventScheduler = ((RTIServerWorld)currentWorld).getUnfinishedEventScheduler();
		
		boolean hasNeighborUpdates = neighborUpdateScheduler.hasScheduledNeighborUpdates();
		boolean hasScheduledEvents = unfinishedEventScheduler.hasScheduledEvents();
		
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
