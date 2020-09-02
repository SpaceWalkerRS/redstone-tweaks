package redstonetweaks.world.server;

import static redstonetweaks.setting.SettingsManager.*;

import java.util.Iterator;
import java.util.function.BooleanSupplier;

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import redstonetweaks.helper.MinecraftServerHelper;
import redstonetweaks.helper.ServerChunkManagerHelper;
import redstonetweaks.helper.ServerTickSchedulerHelper;
import redstonetweaks.helper.ServerWorldHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.packet.TaskSyncPacket;
import redstonetweaks.packet.TickStatusPacket;
import redstonetweaks.packet.WorldSyncPacket;
import redstonetweaks.packet.DoWorldTicksPacket;
import redstonetweaks.packet.WorldTimeSyncPacket;
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
			int interval = GLOBAL.get(SHOW_PROCESSING_ORDER);
			
			if (interval > 0 || isTickingWorlds()) {
				if (interval == 0 || server.getTicks() % interval == 0) {
					tickStepByStep(shouldKeepTicking);
					broadcastChunkData();
				}
			} else {
				tickWorldsNormally(shouldKeepTicking);
			}
			
			if (ticks == 0) {
				pause();
			}
		}
	}
	
	private void tickWorldsNormally(BooleanSupplier shouldKeepTicking) {
		for (ServerWorld world : server.getWorlds()) {
			if (server.getTicks() % 20 == 0) {
				server.getPlayerManager().sendToDimension(new WorldTimeUpdateS2CPacket(world.getTime(), world.getTimeOfDay(), world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)), world.getRegistryKey());
			}
			
			world.tick(shouldKeepTicking);
		}
		if (BUG_FIXES.get(MC172213)) {
			for (ServerWorld world : server.getWorlds()) {
				((ServerWorldHelper)world).tickTimeAccess();
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
				tickWorld(shouldKeepTicking);
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
		
		setCurrentWorld(server.getWorlds().iterator().next());
		setCurrentTask(Task.NONE);
		
		for (ServerWorld world : server.getWorlds()) {
			((ServerWorldHelper)world).getNeighborUpdateScheduler().resetTickTime();
		}
		
		shouldUpdateStatus = true;
	}
	
	private void endTick() {
		if (BUG_FIXES.get(MC172213)) {
			for (ServerWorld world : server.getWorlds()) {
				((ServerWorldHelper)world).tickTimeAccess();
			}
		}
		
		setCurrentTask(Task.NONE);
		updateStatus();
		shouldUpdateStatus = true;
		
		ticks--;
	}
	
	private void tickWorld(BooleanSupplier shouldKeepTicking) {
		ServerNeighborUpdateScheduler neighborUpdateScheduler = ((ServerWorldHelper)currentWorld).getNeighborUpdateScheduler();
		ServerUnfinishedEventScheduler unfinishedEventScheduler = ((ServerWorldHelper)currentWorld).getUnfinishedEventScheduler();
		
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
	
	@Override
	protected void setStatus(Status newStatus) {
		status = newStatus;
		syncStatus();
	}
	
	private void updateStatus() {
		setStatus(status.next());
	}
	
	private void nextWorld() {
		Iterator<ServerWorld> worlds = server.getWorlds().iterator();
		while (worlds.hasNext()) {
			if (worlds.next().equals(currentWorld)) {
				setCurrentWorld(worlds.hasNext() ? worlds.next() : null);
				break;
			}
		}
	}
	
	@Override
	public void setCurrentWorld(World world) {
		currentWorld = world;
		if (currentWorld != null) {
			profiler = currentWorld.getProfiler();
		}
		
		syncCurrentWorld();
	}
	
	@Override
	protected void setCurrentTask(Task task) {
		currentTask = task;
		syncCurrentTask();
	}
	
	private void nextTask() {
		setCurrentTask(currentTask.next());
	}
	
	public long getWorldTime() {
		return server.getWorlds().iterator().next().getTime();
	}
	
	private void tickWorldBorder() {
		profiler.push("world border");
		
		currentWorld.getWorldBorder().tick();
		
		profiler.pop();
	}
	
	private void processWeather() {
		profiler.push("weather");
		
		((ServerWorldHelper)currentWorld).processWeather();
		
		profiler.pop();
	}
	
	private void processTime() {
		((ServerWorldHelper)currentWorld).processTime();
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
				
				((ServerTickSchedulerHelper)currentWorld.getBlockTickScheduler()).startTicking();
			}
			shouldSwitchTask = !((ServerTickSchedulerHelper)currentWorld.getBlockTickScheduler()).tryContinueTicking();
		}
		
		profiler.pop();
	}
	
	private void tickFluids() {
		profiler.push("tickPending");
		
		if (!currentWorld.isDebugWorld()) {
			if (shouldSwitchTask) {
				shouldSwitchTask = false;
				
				((ServerTickSchedulerHelper)currentWorld.getFluidTickScheduler()).startTicking();
			}
			shouldSwitchTask = !((ServerTickSchedulerHelper)currentWorld.getFluidTickScheduler()).tryContinueTicking();
		}
		
		profiler.pop();
	}
	
	private void tickRaidManager() {
		profiler.push("raid");
		
		((ServerWorldHelper)currentWorld).tickRaidManager();
		
		profiler.pop();
	}
	
	private void processBlockEvents() {
		profiler.push("blockEvents");
		
		if (shouldSwitchTask) {
			shouldSwitchTask = false;
			
			((ServerWorldHelper)currentWorld).startProcessingBlockEvents();
		}
		shouldSwitchTask = !((ServerWorldHelper)currentWorld).tryContinueProcessingBlockEvents();
		
		profiler.pop();
	}
	
	private void tickEntities() {
		profiler.push("entities");
		
		((ServerWorldHelper)currentWorld).tickEntities(profiler);
		
		profiler.pop();
	}
	
	private void tickBlockEntities() {
		if (shouldSwitchTask) {
			shouldSwitchTask = false;
			
			((WorldHelper)currentWorld).startTickingBlockEntities(true);
		}
		shouldSwitchTask = !((WorldHelper)currentWorld).tryContinueTickingBlockEntities();
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
			((ServerChunkManagerHelper)chunkManager).broadcastChunkData();
		}
	}
	
	private void syncPause() {
		DoWorldTicksPacket packet = new DoWorldTicksPacket(doWorldTicks());
		((MinecraftServerHelper)server).getPacketHandler().sendPacket(packet);
	}
	
	private void syncStatus() {
		TickStatusPacket packet = new TickStatusPacket(status);
		((MinecraftServerHelper)server).getPacketHandler().sendPacket(packet);
	}
	
	private void syncCurrentWorld() {
		WorldSyncPacket packet = new WorldSyncPacket(currentWorld);
		((MinecraftServerHelper)server).getPacketHandler().sendPacket(packet);
	}
	
	private void syncClientWorldTime() {
		WorldTimeSyncPacket packet = new WorldTimeSyncPacket(getWorldTime());
		((MinecraftServerHelper)server).getPacketHandler().sendPacket(packet);
	}
	
	private void syncCurrentTask() {
		TaskSyncPacket packet = new TaskSyncPacket(currentTask);
		((MinecraftServerHelper)server).getPacketHandler().sendPacket(packet);
	}
}
