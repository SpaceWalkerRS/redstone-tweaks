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
	
	public AbstractRedstoneGateBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Shadow public abstract void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
	@Shadow protected abstract boolean hasPower(World world, BlockPos pos, BlockState state);
	@Shadow protected abstract int getUpdateDelayInternal(BlockState state);
	
	@Inject(method = "scheduledTick", cancellable = true, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/AbstractRedstoneGateBlock;hasPower(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"))
	private void onScheduledTickInjectBeforeHasPower(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		boolean powered = state.get(Properties.POWERED);
		boolean lazy = powered ? REPEATER.get(FALLING_LAZY) : REPEATER.get(RISING_LAZY);
		boolean isReceivingPower = hasPower(world, pos, state);
		boolean shouldBePowered = lazy ? !powered : isReceivingPower;
		
		if (powered != shouldBePowered) {
			world.setBlockState(pos, state.with(Properties.POWERED, shouldBePowered), 2);
			
			if (shouldBePowered != isReceivingPower) {
				if (((WorldHelper)world).shouldSeparateUpdates()) {
					((ServerWorldHelper)world).getUnfinishedEventScheduler().schedule(Source.BLOCK, state, pos, 0);
				} else {
					updatePoweredOnScheduledTick(world, pos, state, random, !powered);
				}
			}
		}
	}
	
	@Redirect(method = "getStrongRedstonePower", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getWeakRedstonePower(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)I"))
	private int onGetStrongRedstonePowerRedirectGetWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (state.isOf(Blocks.COMPARATOR)) {
			return state.getWeakRedstonePower(world, pos, direction);
		}
		return state.get(Properties.POWERED) && state.get(Properties.HORIZONTAL_FACING) == direction ? REPEATER.get(STRONG_POWER) : 0;
	}
	
	@Inject(method = "updatePowered", cancellable =  true, at = @At(value = "FIELD", shift = Shift.BEFORE, target = "Lnet/minecraft/world/TickPriority;HIGH:Lnet/minecraft/world/TickPriority;"))
	private void updatePoweredInjectBeforePriorityHigh(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
		if (getUpdateDelayInternal(state) == 0) {
			if (!world.isClient()) {
				scheduledTick(state, (ServerWorld)world, pos, world.getRandom());
			}
			
			ci.cancel();
		}
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
			updatePoweredOnScheduledTick((ServerWorld)world, pos, state, world.getRandom(), state.get(Properties.POWERED));
		}
		
		return false;
	}
	
	private void updatePoweredOnScheduledTick(ServerWorld world, BlockPos pos, BlockState state, Random random, boolean powered) {
		int delay = powered ? REPEATER.get(FALLING_DELAY) : REPEATER.get(RISING_DELAY);
		
		if (delay == 0) {
			scheduledTick(world.getBlockState(pos), world, pos, random);
		} else { 
			TickPriority priority = powered ? REPEATER.get(FALLING_TICK_PRIORITY) : REPEATER.get(RISING_TICK_PRIORITY);
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), state.get(Properties.DELAY) * delay, priority);
		}
	}
}
