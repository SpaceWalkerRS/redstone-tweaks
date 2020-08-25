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

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

import redstonetweaks.helper.AbstractBlockHelper;
import redstonetweaks.helper.RedstoneDiodeHelper;
import redstonetweaks.helper.ServerWorldHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.world.server.UnfinishedEvent.Source;

@Mixin(AbstractRedstoneGateBlock.class)
public abstract class AbstractRedstoneGateBlockMixin extends Block implements AbstractBlockHelper {

	private boolean isReceivingPower;
	private boolean shouldBePowered;
	
	public AbstractRedstoneGateBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Shadow protected abstract boolean hasPower(World world, BlockPos pos, BlockState state);
	@Shadow protected abstract int getUpdateDelayInternal(BlockState state);
	
	@Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractRedstoneGateBlock;hasPower(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"))
	private boolean onScheduledTickRedirectHasPower(AbstractRedstoneGateBlock gate, World world, BlockPos pos, BlockState state) {
		isReceivingPower = hasPower(world, pos, state);
		shouldBePowered = state.get(Properties.POWERED) ? !REPEATER.get(FALLING_LAZY) && isReceivingPower : REPEATER.get(RISING_LAZY) || isReceivingPower;
		
		return shouldBePowered;
	}
	
	@Inject(method = "scheduledTick", at = @At(value = "INVOKE", ordinal = 0, shift = Shift.AFTER, target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onScheduledTickInjectAfterSetBlockState0(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (isReceivingPower) {
			if (((WorldHelper)world).shouldSeparateUpdates()) {
				((ServerWorldHelper)world).getUnfinishedEventScheduler().schedule(Source.BLOCK, state, pos, 0);
			} else {
				world.getBlockTickScheduler().schedule(pos, state.getBlock(), state.get(Properties.DELAY) * REPEATER.get(RISING_DELAY), REPEATER.get(RISING_TICK_PRIORITY));
			}
		}
	}
	
	@Inject(method = "scheduledTick", cancellable = true, at = @At(value = "INVOKE", shift = Shift.BEFORE, ordinal = 1, target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onScheduledTickInjectBeforeSetBlockState1(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (!shouldBePowered) {
			ci.cancel();
		}
	}
	
	@Inject(method = "scheduledTick", cancellable = true, at = @At(value = "INVOKE", shift = Shift.AFTER, ordinal = 1, target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onScheduledTickInjectAfterSetBlockState1(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (!isReceivingPower) {
			if (((WorldHelper)world).shouldSeparateUpdates()) {
				((ServerWorldHelper)world).getUnfinishedEventScheduler().schedule(Source.BLOCK, state, pos, 0);
			} else {
				world.getBlockTickScheduler().schedule(pos, state.getBlock(), state.get(Properties.DELAY) * REPEATER.get(FALLING_DELAY), REPEATER.get(FALLING_TICK_PRIORITY));
			}
		}
		
		ci.cancel();
	}
	
	@Inject(method = "getStrongRedstonePower", at = @At(value = "HEAD"), cancellable = true)
	private void onGetStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		int power;
		if (state.isOf(Blocks.COMPARATOR)) {
			power = state.getWeakRedstonePower(world, pos, direction);
		} else {
			power = state.get(Properties.POWERED) && state.get(Properties.HORIZONTAL_FACING) == direction ? REPEATER.get(STRONG_POWER) : 0;
		}
		cir.setReturnValue(power);
		cir.cancel();
	}
	
	@Redirect(method = "updatePowered", at = @At(value = "FIELD", target = "Lnet/minecraft/world/TickPriority;HIGH:Lnet/minecraft/world/TickPriority;"))
	private TickPriority updatePoweredRedirectPriorityHigh() {
		return REPEATER.get(RISING_TICK_PRIORITY);
	}
	
	@Redirect(method = "updatePowered", at = @At(value = "FIELD", target = "Lnet/minecraft/world/TickPriority;EXTREMELY_HIGH:Lnet/minecraft/world/TickPriority;"))
	private TickPriority updatePoweredRedirectPriorityExtremelyHigh(World world, BlockPos pos, BlockState state) {
		if (BUG_FIXES.get(MC54711) && ((RedstoneDiodeHelper)this).isInputBugOccurring(world, pos, state)) {
			return REPEATER.get(RISING_TICK_PRIORITY);
		}
		return REPEATER.get(FACING_DIODE_TICK_PRIORITY);
	}
	
	@Redirect(method = "updatePowered", at = @At(value = "FIELD", target = "Lnet/minecraft/world/TickPriority;VERY_HIGH:Lnet/minecraft/world/TickPriority;"))
	private TickPriority updatePoweredRedirectPriorityVeryHigh() {
		return REPEATER.get(FALLING_TICK_PRIORITY);
	}
	
	@Redirect(method = "getPower", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onGetPowerRedirectGetBlockState(World world1, BlockPos backPos, World world, BlockPos pos, BlockState state) {
		return WorldHelper.getStateForPower(world, backPos, state.get(Properties.HORIZONTAL_FACING).getOpposite());
	}
	
	@ModifyConstant(method = "getInputLevel", constant = @Constant(intValue = 15))
	private int onGetInputLevelModifyRedstoneBlockPower(int oldPower) {
		return COMPARATOR.get(REDSTONE_BLOCKS_POWER_SIDES) ? REDSTONE_BLOCK.get(WEAK_POWER) : 0;
	}
	
	@ModifyConstant(method = "getOutputLevel", constant = @Constant(intValue = 15))
	private int getWeakRedstonePower(int oldValue) {
		return REPEATER.get(WEAK_POWER);
	}
	
	@Override
	public boolean continueEvent(World world, BlockState state, BlockPos pos, int type) {
		if (type == 0) {
			if (isReceivingPower != state.get(Properties.POWERED)) {
				int delay = isReceivingPower ? REPEATER.get(RISING_DELAY) : REPEATER.get(FALLING_DELAY);
				TickPriority priority = isReceivingPower ? REPEATER.get(RISING_TICK_PRIORITY) : REPEATER.get(FALLING_TICK_PRIORITY);
				
				world.getBlockTickScheduler().schedule(pos, state.getBlock(), state.get(Properties.DELAY) * delay, priority);
			}
		}
		
		return false;
	}
}
