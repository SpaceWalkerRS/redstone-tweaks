package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;

import redstonetweaks.helper.PressurePlateHelper;
import redstonetweaks.setting.SettingsPack;

@Mixin(AbstractPressurePlateBlock.class)
public abstract class AbstractPressurePlateBlockMixin {
	
	@Shadow protected abstract void updatePlateState(World world, BlockPos pos, BlockState blockState, int rsOut);
	@Shadow protected abstract int getRedstoneOutput(BlockState state);
	
	// When the pressure plate is ticked, it should call updatePlateState
	// regardless of its current redstone output, in case the plate
	// has activation delay and the plate is ticked to power on.
	@ModifyConstant(method = "scheduledTick", constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO))
	private int onScheduledTickModifyCompareValue(int oldValue) {
		return -1;
	}
	
	@Redirect(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractPressurePlateBlock;updatePlateState(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)V"))
	public void onEntityCollisionRedirectUpdatePlateState(AbstractPressurePlateBlock pressurePlate, World world, BlockPos pos, BlockState state, int i) {
		SettingsPack settings = BLOCK_TO_SETTINGS_PACK.get(state.getBlock());
		
		int delay = settings.get(RISING_DELAY);
		if (delay > 0) {
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, settings.get(RISING_TICK_PRIORITY));
		} else {
			updatePlateState(world, pos, state, i);
		}
	}
	
	@Redirect(method = "updatePlateState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void updatePlateStateRedirectScheduleTick(TickScheduler<?> tickScheduler, BlockPos pos, T Object, int oldDelay, World world, BlockPos blockPos, BlockState state) {
		SettingsPack settings = BLOCK_TO_SETTINGS_PACK.get(state.getBlock());
		
		int delay = settings.get(FALLING_DELAY);
		TickPriority priority = settings.get(FALLING_TICK_PRIORITY);
		
		world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, priority);
	}
	
	@Redirect(method = "getWeakRedstonePower", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractPressurePlateBlock;getRedstoneOutput(Lnet/minecraft/block/BlockState;)I"))
	private int onGetWeakRedstonePowerRedirectGetRedstoneOutput(AbstractPressurePlateBlock plate, BlockState state) {
		return ((PressurePlateHelper)this).getWeakPower(state);
	}
	
	@Redirect(method = "getStrongRedstonePower", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractPressurePlateBlock;getRedstoneOutput(Lnet/minecraft/block/BlockState;)I"))
	private int onGetStrongRedstonePowerRedirectGetRedstoneOutput(AbstractPressurePlateBlock plate, BlockState state) {
		return ((PressurePlateHelper)this).getStrongPower(state);
	}
}
