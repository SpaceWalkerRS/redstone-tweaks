package redstonetweaks.world.server;

import static redstonetweaks.setting.SettingsManager.*;

import java.util.TreeSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import redstonetweaks.helper.MinecraftServerHelper;
import redstonetweaks.packet.NeighborUpdateSchedulerPacket;
import redstonetweaks.packet.NeighborUpdateVisualizerPacket;
import redstonetweaks.packet.RedstoneTweaksPacket;
import redstonetweaks.world.common.NeighborUpdateScheduler;

public class ServerNeighborUpdateScheduler extends NeighborUpdateScheduler {
	
	private final TreeSet<ScheduledNeighborUpdate> scheduledNeighborUpdates = new TreeSet<>();
	private final ServerWorld world;
	
	private long tickTime;
	
	// The neighbor update that is currently being executed
	private ScheduledNeighborUpdate currentUpdate = null;
	
	// Since the block updates are intercepted inside the World class, we don't know
	// the location of the block that causes the block updates. So we keep that position
	// updated by injecting into classes of the different blocks that cause block updates.
	// By default the position is set to null. That way it can never display a wrong
	// source position.
	private BlockPos currentSourcePos = null;
	
	public ServerNeighborUpdateScheduler(ServerWorld world) {
		this.world = world;
		
		this.tickTime = 0L;
	}
	
	public void tick() {
		tickTime++;
		
		if (!GLOBAL.get(SHOW_NEIGHBOR_UPDATES)) {
			clearUpdates();
		} else if (scheduledNeighborUpdates.isEmpty()) {
			clearCurrentUpdate();
		} else {
			currentUpdate = scheduledNeighborUpdates.pollFirst();
			
			doNeighborUpdate();
			syncNeighborUpdateVisualizer();
		}
	}
	
	public void resetTickTime() {
		tickTime = 0L;
	}
	
	public void clearUpdates() {
		while (!scheduledNeighborUpdates.isEmpty()) {
			currentUpdate = scheduledNeighborUpdates.pollFirst();
			doNeighborUpdate();
		}
		clearCurrentUpdate();
	}
	
	private void doNeighborUpdate() {
		BlockState state = world.getBlockState(currentUpdate.pos);
		Block sourceBlock = currentUpdate.sourcePos == null ? Blocks.AIR : world.getBlockState(currentUpdate.sourcePos).getBlock();
		
		switch (currentUpdate.updateType) {
		case BLOCK_UPDATE:
			try {
				state.neighborUpdate(world, currentUpdate.pos, sourceBlock, currentUpdate.notifierPos, false);
			} catch (Throwable var8) {
				CrashReport crashReport = CrashReport.create(var8, "Exception while updating neighbours");
				CrashReportSection crashReportSection = crashReport.addElement("Block being updated");
				crashReportSection.add("Source block type", () -> {
					try {
						return String.format("ID #%s (%s // %s)", Registry.BLOCK.getId(sourceBlock), sourceBlock.getTranslationKey(), sourceBlock.getClass().getCanonicalName());
					} catch (Throwable var2) {
						return "ID #" + Registry.BLOCK.getId(sourceBlock);
					}
				});
				CrashReportSection.addBlockInfo(crashReportSection, currentUpdate.pos, state);
				throw new CrashException(crashReport);
			}
			break;
		case COMPARATOR_UPDATE:
			if (state.isOf(Blocks.COMPARATOR)) {
				state.neighborUpdate(world, currentUpdate.pos, sourceBlock, currentUpdate.notifierPos, false);
			}
			break;
		case STATE_UPDATE:
			if (currentUpdate.flags >= 0 && currentUpdate.depth >= 0) {
				BlockState notifierState = world.getBlockState(currentUpdate.notifierPos);
				BlockState newState = state.getStateForNeighborUpdate(currentUpdate.direction, notifierState, world, currentUpdate.pos, currentUpdate.notifierPos);
				Block.replace(state, newState, world, currentUpdate.pos, currentUpdate.flags, currentUpdate.depth);
			}
			break;
		default:
			break;
		}
	}
	
	private void clearCurrentUpdate() {
		if (currentUpdate != null) {
			currentUpdate = null;
			
			syncNeighborUpdateVisualizer();
			syncClientNeighborUpdateScheduler();
		}
	}
	
	public void schedule(BlockPos pos, BlockPos notifierPos, Direction direction, ScheduledNeighborUpdate.UpdateType updateType) {
		schedule(pos, notifierPos, direction, -1, -1, updateType);
	}
	
	public void schedule(BlockPos pos, BlockPos notifierPos, Direction direction, int flags, int depth, ScheduledNeighborUpdate.UpdateType updateType) {
		boolean isEmpty = !hasScheduledNeighborUpdates();
		long time = GLOBAL.get(SHOW_PROCESSING_ORDER) > 0 ? tickTime : world.getTime();
		
		scheduledNeighborUpdates.add(new ScheduledNeighborUpdate(pos, notifierPos, currentSourcePos, direction, flags | 2, depth, updateType, time));
		
		if (isEmpty) {
			syncClientNeighborUpdateScheduler();
		}
	}
	
	public void setCurrentSourcePos(BlockPos newSourcePos) {
		currentSourcePos = newSourcePos;
	}
	
	public void clearCurrentSourcePos() {
		setCurrentSourcePos(null);
	}
	
	@Override
	public boolean hasScheduledNeighborUpdates() {
		return !scheduledNeighborUpdates.isEmpty();
	}
	
	private void syncNeighborUpdateVisualizer() {
		BlockPos sourcePos = currentUpdate == null ? null : currentUpdate.sourcePos;
		NeighborUpdateVisualizerPacket packet = new  NeighborUpdateVisualizerPacket(currentUpdate, sourcePos);
		sendPacket(packet);
	}
	
	private void syncClientNeighborUpdateScheduler() {
		NeighborUpdateSchedulerPacket packet = new NeighborUpdateSchedulerPacket(!scheduledNeighborUpdates.isEmpty());
		sendPacket(packet);
	}
	
	private void sendPacket(RedstoneTweaksPacket packet) {
		((MinecraftServerHelper)world.getServer()).getPacketHandler().sendPacketToDimension(packet, world.getRegistryKey());
	}
}
