package redstonetweaks.world.common;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstonetweaks.helper.AbstractBlockHelper;
import redstonetweaks.packet.UnfinishedEventPacket;
import redstonetweaks.world.server.UnfinishedEvent;
import redstonetweaks.world.server.UnfinishedEvent.Source;

public abstract class UnfinishedEventScheduler {
	
	protected final World world;
	
	protected final ObjectLinkedOpenHashSet<UnfinishedEvent> unfinishedEvents;
	
	public UnfinishedEventScheduler(World world) {
		this.world = world;
		this.unfinishedEvents = new ObjectLinkedOpenHashSet<>();
	}
	
	protected boolean continueEvent(UnfinishedEvent event) {
		BlockState state = world.getBlockState(event.pos);
		
		switch (event.source) {
		case BLOCK:
			if (state.isOf(event.block)) {
				return ((AbstractBlockHelper)event.block).continueEvent(world, state, event.pos, event.type);
			}
			break;
		case BLOCK_ENTITY:
			break;
		case ENTITY:
			break;
		default:
			break;
		}
		
		return false;
	}
	
	public void schedule(Source source, BlockState state, BlockPos pos, int type, double viewDistance) {
		unfinishedEvents.add(new UnfinishedEvent(source, pos, state, type, viewDistance));
	}
	
	public void schedule(Source source, BlockState state, BlockPos pos, int type) {
		schedule(source, state, pos, type, -1);
	}
	
	public boolean hasScheduledEvents() {
		return !unfinishedEvents.isEmpty();
	}

	public abstract void onUnfinishedEventPacketReceived(UnfinishedEventPacket unfinishedEventPacket);
}
