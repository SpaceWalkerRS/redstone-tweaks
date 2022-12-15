package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockEventData;
import net.minecraft.world.level.block.Block;

import redstone.tweaks.interfaces.mixin.ILevel;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements ILevel {

	@Shadow private ObjectLinkedOpenHashSet<BlockEventData> blockEvents;

	@Override
	public boolean hasBlockEvent(BlockPos pos, Block block) {
		for (BlockEventData blockEvent : blockEvents) {
			if (blockEvent.pos().equals(pos) && blockEvent.block() == block) {
				return true;
			}
		}

		return false;
	}
}
