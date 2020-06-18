package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

import redstonetweaks.setting.Settings;

@Mixin(AbstractButtonBlock.class)
public abstract class AbstractButtonBlockMixin {
	
	@Shadow boolean wooden;
	@Shadow @Final static BooleanProperty POWERED;
	
	@Shadow public abstract void method_21845(BlockState blockState, World world, BlockPos blockPos);
	@Shadow protected abstract void playClickSound(PlayerEntity player, IWorld world, BlockPos pos, boolean powered);
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 30))
	private int getWoodenButtonDelay(int oldDelay) {
		return (int)Settings.woodenButtonDelay.get();
	}
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 20))
	private int getStoneButtonDelay(int oldDelay) {
		return (int)Settings.stoneButtonDelay.get();
	}
	
	// This code is executed if a button is pressed but not powered.
	// Since it sets a return value, the vanilla code is not executed
	// if the button has activation delay.
	@Inject(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractButtonBlock;method_21845(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", shift = Shift.BEFORE), cancellable = true)
	private void onUseNotPowered(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		int buttonOnDelay = this.wooden ? (int)Settings.woodenButtonOnDelay.get() : (int)Settings.stoneButtonOnDelay.get();
		if (buttonOnDelay > 0) {
			world.getBlockTickScheduler().schedule(pos, (Block)(Object) this, buttonOnDelay, TickPriority.EXTREMELY_HIGH);
			cir.setReturnValue(ActionResult.SUCCESS);
			cir.cancel();
		}
	}
	
	// If the button is not powered when it gets ticked, method_21845 is called.
	// This method updates the POWERED state and schedules another tick for
	// the button to depower.
	@Inject(method = "scheduledTick", at = @At(value = "RETURN", target = "Lnet/minecraft/block/AbstractButtonBlock;scheduledTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"))
	private void scheduledTickNotPowered(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (!(Boolean) state.get(POWERED)) {
			this.method_21845(state, world, pos);
	        this.playClickSound((PlayerEntity)null, world, pos, true);
		}
	}
}
