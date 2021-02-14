package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(LeverBlock.class)
public abstract class LeverBlockMixin extends WallMountedBlock {
	
	public LeverBlockMixin(net.minecraft.block.AbstractBlock.Settings settings) {
		super(settings);
	}

	@Shadow public abstract BlockState method_21846(BlockState blockState, World world, BlockPos blockPos);
	
	// If the player has added activation delay to the lever,
	// the lever should not update its state immediately,
	// but rather schedule a tick with the appropriate delay.
	// Since a return value is set, the vanilla code is not
	// run if the lever does have activation delay.
	@Inject(method = "onUse", at = @At(value = "HEAD"), cancellable = true)
	private void onOnUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if (world.getBlockTickScheduler().isTicking(pos, state.getBlock())) {
			cir.setReturnValue(ActionResult.FAIL);
			cir.cancel();
		} else {
			boolean powered = state.get(Properties.POWERED);
			
			int delay = getDelay(powered);
			TickPriority priority = getTickPriority(powered);
			
			TickSchedulerHelper.scheduleBlockTick(world, pos, state, delay, priority);
			
			cir.setReturnValue(ActionResult.SUCCESS);
			cir.cancel();
		}
	}
	
	@ModifyConstant(method = "getWeakRedstonePower", constant = @Constant(intValue = 15))
	private int onGetWeakRedstonePower(int oldValue) {
		return Tweaks.Lever.POWER_WEAK.get();
	}
	
	@ModifyConstant(method = "getStrongRedstonePower", constant = @Constant(intValue = 15))
	private int onGetStrongRedstonePower(int oldValue) {
		return Tweaks.Lever.POWER_STRONG.get();
	}
	
	@Inject(method = "updateNeighbors", cancellable = true, at = @At(value = "HEAD"))
	private void onUpdateNeighborsInjectAtHead(BlockState state, World world, BlockPos pos, CallbackInfo ci) {
		((RTIWorld)world).dispatchBlockUpdates(pos, getDirection(state).getOpposite(), state.getBlock(), Tweaks.Lever.BLOCK_UPDATE_ORDER.get());
		
		ci.cancel();
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		BlockState blockState = method_21846(state, world, pos);
		float pitch = blockState.get(Properties.POWERED) ? 0.6F : 0.5F;
		
		world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, pitch);
	}
	
	private int getDelay(boolean powered) {
		return powered ? Tweaks.Lever.DELAY_FALLING_EDGE.get() : Tweaks.Lever.DELAY_RISING_EDGE.get();
	}
	
	private TickPriority getTickPriority(boolean powered) {
		return powered ? Tweaks.Lever.TICK_PRIORITY_FALLING_EDGE.get() : Tweaks.Lever.TICK_PRIORITY_RISING_EDGE.get();
	}
}
