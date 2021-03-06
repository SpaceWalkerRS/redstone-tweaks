package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.interfaces.mixin.RTIAbstractBlockState;
import redstonetweaks.interfaces.mixin.RTIBlock;
import redstonetweaks.interfaces.mixin.RTIServerWorld;
import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.setting.settings.Tweaks;
import redstonetweaks.world.common.UpdateOrder;

@Mixin(AbstractButtonBlock.class)
public abstract class AbstractButtonBlockMixin extends WallMountedBlock implements RTIBlock {
	
	@Shadow boolean wooden;
	
	protected AbstractButtonBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Shadow protected abstract int getPressTicks();
	@Shadow public abstract void powerOn(BlockState blockState, World world, BlockPos blockPos);
	@Shadow protected abstract void playClickSound(PlayerEntity player, WorldAccess world, BlockPos pos, boolean powered);
	@Shadow public abstract void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
	
	@Inject(
			method = "getPressTicks",
			cancellable = true,
			at = @At(
					value = "HEAD"
			)
	)
	private void onGetPressTicksInjectAtHead(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(wooden ? Tweaks.WoodenButton.DELAY_FALLING_EDGE.get() : Tweaks.StoneButton.DELAY_FALLING_EDGE.get());
		cir.cancel();
	}
	
	// This code is executed if a button is pressed but not powered.
	@Inject(
			method = "onUse", 
			cancellable = true, 
			at = @At(
					value = "INVOKE", 
					shift = Shift.BEFORE, 
					target = "Lnet/minecraft/block/AbstractButtonBlock;powerOn(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"
			)
	)
	private void onOnUseInjectBeforePowerOn(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if (world.getBlockTickScheduler().isTicking(pos, state.getBlock())) {
			cir.setReturnValue(ActionResult.FAIL);
			cir.cancel();
		} else {
			int delay = wooden ? Tweaks.WoodenButton.DELAY_RISING_EDGE.get() : Tweaks.StoneButton.DELAY_RISING_EDGE.get();
			TickPriority tickPriority = wooden ? Tweaks.WoodenButton.TICK_PRIORITY_RISING_EDGE.get() : Tweaks.StoneButton.TICK_PRIORITY_RISING_EDGE.get();
			
			TickSchedulerHelper.scheduleBlockTick(world, pos, state, delay, tickPriority);
			
			cir.setReturnValue(ActionResult.SUCCESS);
			cir.cancel();
		}
	}
	
	@Redirect(
			method = "powerOn", 
			at = @At(
					value = "INVOKE", 
					target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"
			)
	)
	private <T> void onPowerOnRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int delay, BlockState state, World world, BlockPos pos) {
		if (((RTIWorld)world).immediateNeighborUpdates()) {
			depower(world, pos, world.getBlockState(pos));
		} else if (!world.isClient()) {
			((RTIServerWorld)world).getIncompleteActionScheduler().scheduleBlockAction(pos, 0, state.getBlock());
		}
	}
	
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
	
	@Inject(
			method = "scheduledTick", 
			cancellable = true, 
			at = @At(
					value = "HEAD"
			)
	)
	private void onScheduledTickInjectAtHead(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (!state.get(Properties.POWERED)) {
			powerOn(state, world, pos);
	        playClickSound(null, world, pos, true);
	        
	        ci.cancel();
		}
	}
	
	@Redirect(
			method = "tryPowerWithProjectiles", 
			at = @At(
					value = "INVOKE", 
					target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"
			)
	)
	private <T> void onTryPowerWithProjectilesRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int delay, BlockState state, World world, BlockPos pos) {
		depower(world, pos, world.getBlockState(pos));
	}
	
	@Inject(
			method = "updateNeighbors", 
			cancellable = true, 
			at = @At(
					value = "HEAD"
			)
	)
	private void onUpdateNeighborsInjectAtHead(BlockState state, World world, BlockPos pos, CallbackInfo ci) {
		((RTIWorld)world).dispatchBlockUpdates(pos, getDirection(state).getOpposite(), state.getBlock(), updateOrder());
		
		ci.cancel();
	}
	
	@Override
	public boolean continueAction(World world, BlockPos pos, int type) {
		BlockState state = world.getBlockState(pos);
		
		if (state.isOf((Block)(Object)this)) {
			depower(world, pos, state);
		}
		
		return false;
	}
	
	private void depower(World world, BlockPos pos, BlockState state) {
		int delay = getPressTicks();
		TickPriority tickPriority = wooden ? Tweaks.WoodenButton.TICK_PRIORITY_FALLING_EDGE.get() : Tweaks.StoneButton.TICK_PRIORITY_FALLING_EDGE.get();
		
		BlockPos attachedToPos = pos.offset(getDirection(state).getOpposite());
		RTIAbstractBlockState attachedToState = (RTIAbstractBlockState)world.getBlockState(attachedToPos);
		
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, attachedToState.delayOverride(delay), attachedToState.tickPriorityOverride(tickPriority));
	}
	
	private int getPowerOutput(BlockView world, BlockPos pos, BlockState state, Direction dir, boolean strong) {
		if (!state.get(Properties.POWERED)) {
			return 0;
		}
		
		Direction facing = getDirection(state);
		
		if (strong && dir != facing) {
			return 0;
		}
		
		int power = strong ? strongPower() : weakPower();
		
		BlockPos attachedToPos = pos.offset(facing.getOpposite());
		RTIAbstractBlockState attachedToState = (RTIAbstractBlockState)world.getBlockState(attachedToPos);
		
		return strong ? attachedToState.strongPowerOverride(power) : attachedToState.weakPowerOverride(power);
	}
	
	private int weakPower() {
		return wooden ? Tweaks.WoodenButton.POWER_WEAK.get() : Tweaks.StoneButton.POWER_WEAK.get();
	}
	
	private int strongPower() {
		return wooden ? Tweaks.WoodenButton.POWER_STRONG.get() : Tweaks.StoneButton.POWER_STRONG.get();
	}
	
	private UpdateOrder updateOrder() {
		return wooden ? Tweaks.WoodenButton.BLOCK_UPDATE_ORDER.get() : Tweaks.StoneButton.BLOCK_UPDATE_ORDER.get();
	}
}
