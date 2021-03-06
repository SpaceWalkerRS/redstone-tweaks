package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;

import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.interfaces.mixin.RTIAbstractBlockState;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(DetectorRailBlock.class)
public class DetectorRailBlockMixin {
	
	@Inject(
			method = "getWeakRedstonePower",
			cancellable = true,
			at = @At(
					value = "HEAD"
			)
	)
	private void onGetWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(getPowerOutput(world, pos, state, dir, false));
		cir.cancel();
	}
	
	@Inject(
			method = "getStrongRedstonePower",
			cancellable = true,
			at = @At(
					value = "HEAD"
			)
	)
	private void onGetStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(getPowerOutput(world, pos, state, dir, true));
		cir.cancel();
	}
	
	@Redirect(
			method = "updatePoweredStatus", 
			at = @At(
					value = "INVOKE", 
					target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"
			)
	)
	private <T> void onUpdatePoweredStatusRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos, T block, int oldDelay, World world, BlockPos blockPos, BlockState state) {
		int delay = Tweaks.DetectorRail.DELAY.get();
		TickPriority tickPriority = Tweaks.DetectorRail.TICK_PRIORITY.get();
		
		BlockPos belowPos = pos.down();
		RTIAbstractBlockState belowState = (RTIAbstractBlockState)world.getBlockState(belowPos);
		
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, belowState.delayOverride(delay), belowState.tickPriorityOverride(tickPriority));
	}
	
	private int getPowerOutput(BlockView world, BlockPos pos, BlockState state, Direction dir, boolean strong) {
		if (!state.get(Properties.POWERED) || (strong && dir != Direction.DOWN)) {
			return 0;
		}
		
		int power = strong ? Tweaks.DetectorRail.POWER_STRONG.get() : Tweaks.DetectorRail.POWER_WEAK.get();
		
		BlockPos belowPos = pos.down();
		RTIAbstractBlockState belowState = (RTIAbstractBlockState)world.getBlockState(belowPos);
		
		return strong ? belowState.strongPowerOverride(power) : belowState.weakPowerOverride(power);
	}
}
