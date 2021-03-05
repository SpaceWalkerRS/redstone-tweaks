package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CactusBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(CactusBlock.class)
public class CactusBlockMixin {
	
	@Redirect(
			method = "getStateForNeighborUpdate",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"
			)
	)
	private <T> void onGetStateForNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T block, int delay, BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, Tweaks.Cactus.DELAY.get(), Tweaks.Cactus.TICK_PRIORITY.get());
	}
	
	@Redirect(
			method = "scheduledTick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/world/ServerWorld;breakBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"
			)
	)
	private boolean onScheduledTickRedirectBreakBlock(ServerWorld serverWorld, BlockPos blockPos, boolean drop, BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (Tweaks.Cactus.NO_U.get() && isSupported(world, pos)) {
			for (Direction dir : Direction.Type.HORIZONTAL) {
				BlockPos side = pos.offset(dir);
				BlockState neighborBlock = world.getBlockState(side);
				FluidState neighborFluid = world.getFluidState(side);
				
				if (neighborBlock.getMaterial().isSolid() && !neighborFluid.isIn(FluidTags.LAVA)) {
					world.breakBlock(side, drop);
				}
			}
			
			BlockPos abovePos = pos.up();
			BlockState aboveState = world.getBlockState(abovePos);
			
			if (aboveState.getMaterial().isLiquid()) {
				world.breakBlock(abovePos, drop);
			}
		} else {
			world.breakBlock(blockPos, drop);
		}
		
		return true;
	}
	
	private boolean isSupported(World world, BlockPos pos) {
		BlockPos belowPos = pos.down();
		Block belowBlock = world.getBlockState(belowPos).getBlock();
		
		return belowBlock == Blocks.CACTUS || belowBlock == Blocks.SAND || belowBlock == Blocks.RED_SAND;
	}
}
