package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CoralBlockBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.WorldAccess;
import redstonetweaks.helper.TickSchedulerHelper;

@Mixin(CoralBlockBlock.class)
public abstract class CoralBlockBlockMixin extends Block {
	
	public CoralBlockBlockMixin(Settings settings) {
		super(settings);
	}

	@Shadow public abstract void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
	
	@Redirect(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onGetStateForNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T block, int delay, BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		delay = CORAL_BLOCK.get(DELAY_MIN) + world.getRandom().nextInt(getDelayRange());
		TickSchedulerHelper.schedule(world, state, tickScheduler, pos, block, delay, CORAL_BLOCK.get(TICK_PRIORITY));
	}
	
	@Redirect(method = "getPlacementState", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onGetPlacementStateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int delay, ItemPlacementContext ctx) {
		delay = CORAL_BLOCK.get(DELAY_MIN) + ctx.getWorld().getRandom().nextInt(CORAL_BLOCK.get(DELAY_MAX) - CORAL_BLOCK.get(DELAY_MIN));
		if (delay > 0) {
			tickScheduler.schedule(ctx.getBlockPos(), object, delay, CORAL_BLOCK.get(TICK_PRIORITY));
		}
	}
	
	private int getDelayRange() {
		int max = CORAL_BLOCK.get(DELAY_MAX);
		int min = CORAL_BLOCK.get(DELAY_MIN);
		
		return min > max ? min : max - min;
	}
}
