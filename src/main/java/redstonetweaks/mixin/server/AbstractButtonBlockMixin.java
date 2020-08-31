package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

@Mixin(AbstractButtonBlock.class)
public abstract class AbstractButtonBlockMixin {
	
	@Shadow boolean wooden;
	
	@Shadow public abstract void powerOn(BlockState blockState, World world, BlockPos blockPos);
	@Shadow protected abstract void playClickSound(PlayerEntity player, WorldAccess world, BlockPos pos, boolean powered);
	@Shadow public abstract void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
	
	@ModifyConstant(method = "getPressTicks", constant = @Constant(intValue = 30))
	private int onGetPressTicksModify30(int oldValue) {
		return WOODEN_BUTTON.get(FALLING_DELAY);
	}
	
	@ModifyConstant(method = "getPressTicks", constant = @Constant(intValue = 20))
	private int onGetPressTicksModify20(int oldValue) {
		return STONE_BUTTON.get(FALLING_DELAY);
	}
	
	// This code is executed if a button is pressed but not powered.
	// Since it sets a return value, the vanilla code is not executed
	// if the button has activation delay.
	@Inject(method = "onUse", cancellable = true, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/AbstractButtonBlock;powerOn(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
	private void onOnUseInjectBeforePowerOn(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		int delay = wooden ? WOODEN_BUTTON.get(RISING_DELAY) : STONE_BUTTON.get(RISING_DELAY);
		if (delay > 0) {
			TickPriority priority = wooden ? WOODEN_BUTTON.get(RISING_TICK_PRIORITY) : STONE_BUTTON.get(RISING_TICK_PRIORITY);
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, priority);
			
			cir.setReturnValue(ActionResult.SUCCESS);
			cir.cancel();
		}
	}
	
	@Redirect(method = "powerOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onPowerOnRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int delay, BlockState state, World world, BlockPos pos) {
		if (delay == 0) {
			if (!world.isClient()) {
				scheduledTick(world.getBlockState(pos), (ServerWorld)world, pos, world.getRandom());
			}
		} else {
			TickPriority priority = wooden ? WOODEN_BUTTON.get(FALLING_TICK_PRIORITY) : STONE_BUTTON.get(FALLING_TICK_PRIORITY);
			tickScheduler.schedule(pos, object, delay, priority);
		}
		
	}
	
	@ModifyConstant(method = "getWeakRedstonePower", constant = @Constant(intValue = 15))
	private int onGetWeakRedstonePowerModify15(int oldValue) {
		return wooden ? WOODEN_BUTTON.get(WEAK_POWER) : STONE_BUTTON.get(WEAK_POWER);
	}
	
	@ModifyConstant(method = "getStrongRedstonePower", constant = @Constant(intValue = 15))
	private int onGetStrongRedstonePowerModify15(int oldValue) {
		return wooden ? WOODEN_BUTTON.get(STRONG_POWER) : STONE_BUTTON.get(STRONG_POWER);
	}
	
	@Inject(method = "scheduledTick", cancellable = true, at = @At(value = "HEAD"))
	private void onScheduledTickInjectAtHead(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (!state.get(Properties.POWERED)) {
			powerOn(state, world, pos);
	        playClickSound(null, world, pos, true);
	        
	        ci.cancel();
		}
	}
}
