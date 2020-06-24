package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.leverOffDelay;
import static redstonetweaks.setting.Settings.leverOnDelay;
import static redstonetweaks.setting.Settings.leverSignal;

import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeverBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

@Mixin(LeverBlock.class)
public abstract class LeverBlockMixin {

	@Shadow @Final public static BooleanProperty POWERED;
	
	@Shadow public abstract BlockState method_21846(BlockState blockState, World world, BlockPos blockPos);
	
	// If the player has added activation delay to the lever,
	// the lever should not update its state immediately,
	// but rather schedule a tick with the appropriate delay.
	// Since a return value is set, the vanilla code is not
	// run if the lever does have activation delay.
	@Inject(method = "onUse", at = @At(value = "HEAD"), cancellable = true)
	private void onOnUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		int delay = state.get(POWERED) ? leverOffDelay.get() : leverOnDelay.get();
		if (delay > 0) {
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, TickPriority.EXTREMELY_HIGH);
			cir.setReturnValue(ActionResult.SUCCESS);
			cir.cancel();
		}
	}
	
	@ModifyConstant(method = "getWeakRedstonePower", constant = @Constant(intValue = 15))
	private int onGetWeakRedstonePower(int oldValue) {
		return leverSignal.get();
	}
	
	@ModifyConstant(method = "getStrongRedstonePower", constant = @Constant(intValue = 15))
	private int onGetStrongRedstonePower(int oldValue) {
		return leverSignal.get();
	}
	
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		BlockState blockState = this.method_21846(state, world, pos);
        	float pitch = blockState.get(POWERED) ? 0.6F : 0.5F;
        	world.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, pitch);
	}
}
