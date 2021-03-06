package redstonetweaks.mixin.server;

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

import net.minecraft.block.AbstractBlock;
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
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;

import redstonetweaks.helper.BlockHelper;
import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.interfaces.mixin.RTIAbstractBlockState;
import redstonetweaks.interfaces.mixin.RTIBlock;
import redstonetweaks.interfaces.mixin.RTIRedstoneDiode;
import redstonetweaks.interfaces.mixin.RTIServerWorld;
import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.setting.settings.Tweaks;
import redstonetweaks.world.common.UpdateOrder;

@Mixin(AbstractRedstoneGateBlock.class)
public abstract class AbstractRedstoneGateBlockMixin extends AbstractBlock implements RTIBlock {
	
	@Shadow public abstract void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
	@Shadow protected abstract boolean hasPower(World world, BlockPos pos, BlockState state);
	@Shadow protected abstract int getUpdateDelayInternal(BlockState state);
	@Shadow protected abstract int getOutputLevel(BlockView world, BlockPos pos, BlockState state);
	
	public AbstractRedstoneGateBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Inject(
			method = "scheduledTick",
			cancellable = true,
			at = @At(
					value = "INVOKE", 
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/AbstractRedstoneGateBlock;hasPower(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"
			)
	)
	private void onScheduledTickInjectBeforeHasPower(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		boolean powered = state.get(Properties.POWERED);
		boolean lazy = powered ? Tweaks.Repeater.LAZY_FALLING_EDGE.get() : Tweaks.Repeater.LAZY_RISING_EDGE.get();
		boolean isReceivingPower = hasPower(world, pos, state);
		boolean shouldBePowered = lazy ? !powered : isReceivingPower;
		
		if (powered != shouldBePowered) {
			BlockState newState = state.with(Properties.POWERED, shouldBePowered);
			world.setBlockState(pos, newState, 2);
			
			if (shouldBePowered != isReceivingPower) {
				if (Tweaks.Global.SPONTANEOUS_EXPLOSIONS.get()) {
					WorldHelper.createSpontaneousExplosion(world, pos);
				} else if (((RTIWorld)world).immediateNeighborUpdates()) {
					scheduleTickOnScheduledTick(world, pos, newState, random);
				} else {
					((RTIServerWorld)world).getIncompleteActionScheduler().scheduleBlockAction(pos, 0, newState.getBlock());
				}
			}
		}
		
		ci.cancel();
	}
	
	@Inject(
			method = "getStrongRedstonePower",
			cancellable = true,
			at = @At(
					value = "HEAD"
			)
	)
	private void onGetStrongRedstonePowerInjectAtHead(BlockState state, BlockView world, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(getPowerOutput(world, pos, state, dir, true));
		cir.cancel();
	}
	
	@Inject(
			method = "getWeakRedstonePower",
			cancellable = true,
			at = @At(
					value = "HEAD"
			)
	)
	private void onGetWeakRedstonePowerInjectAtHead(BlockState state, BlockView world, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(getPowerOutput(world, pos, state, dir, false));
		cir.cancel();
	}
	
	@Redirect(
			method = "updatePowered",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/TickScheduler;isTicking(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;)Z"
			)
	)
	private <T> boolean onUpdatePoweredRedirectIsTicking(TickScheduler<T> tickScheduler, BlockPos pos, T block, World world, BlockPos blockPos, BlockState state) {
		if (!Tweaks.Repeater.MICRO_TICK_MODE.get()) {
			BlockPos belowPos = pos.down();
			RTIAbstractBlockState belowState = (RTIAbstractBlockState)world.getBlockState(belowPos);
			
			if (!belowState.forceMicroTickMode()) {
				return tickScheduler.isTicking(pos, block);
			}
		}
		
		return world.isClient() || ((RTIServerWorld)world).hasBlockEvent(pos, (Block)block);
	}
	
	@Redirect(
			method = "updatePowered",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/world/TickPriority;HIGH:Lnet/minecraft/world/TickPriority;"
			)
	)
	private TickPriority updatePoweredRedirectPriorityHigh() {
		return Tweaks.Repeater.TICK_PRIORITY_RISING_EDGE.get();
	}
	
	@Redirect(
			method = "updatePowered",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/world/TickPriority;EXTREMELY_HIGH:Lnet/minecraft/world/TickPriority;"
			)
	)
	private TickPriority updatePoweredRedirectPriorityExtremelyHigh(World world, BlockPos pos, BlockState state) {
		if (Tweaks.BugFixes.MC54711.get() && ((RTIRedstoneDiode)this).isChainBugOccurring(world, pos, state)) {
			return Tweaks.Repeater.TICK_PRIORITY_RISING_EDGE.get();
		}
		
		return Tweaks.Repeater.TICK_PRIORITY_FACING_DIODE.get();
	}
	
	@Redirect(
			method = "updatePowered",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/world/TickPriority;VERY_HIGH:Lnet/minecraft/world/TickPriority;"
			)
	)
	private TickPriority updatePoweredRedirectPriorityVeryHigh() {
		return Tweaks.Repeater.TICK_PRIORITY_FALLING_EDGE.get();
	}
	
	@Redirect(
			method = "updatePowered",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;ILnet/minecraft/world/TickPriority;)V"
			)
	)
	private <T> void onUpdatePoweredRedirectSchedule(TickScheduler<T> scheduler, BlockPos pos, T block, int delay, TickPriority tickPriority, World world, BlockPos blockPos, BlockState state) {
		BlockPos belowPos = pos.down();
		RTIAbstractBlockState belowState = (RTIAbstractBlockState)world.getBlockState(belowPos);
		
		delay = belowState.delayOverride(delay);
		
		if (Tweaks.Repeater.MICRO_TICK_MODE.get() || belowState.forceMicroTickMode()) {
			if (!world.isClient()) {
				((ServerWorld)world).addSyncedBlockEvent(pos, state.getBlock(), delay, 0);
			}
		} else {
			TickSchedulerHelper.scheduleBlockTick(world, pos, state, delay, belowState.tickPriorityOverride(tickPriority));
		}
	}
	
	@ModifyConstant(
			method = "getPower",
			constant = @Constant(
					intValue = 15
			)
	)
	private int onGetPowerModify15(int oldValue) {
		// This makes sure the diode does not check for wires behind it
		return 0;
	}
	
	@Redirect(
			method = "getInputLevel",
			at = @At(
					value = "INVOKE",
					ordinal = 1,
					target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"
			)
	)
	private boolean onGetInputLevelRedirectIsOf1(BlockState state, Block redstoneWire) {
		return false;
	}
	
	@ModifyConstant(
			method = "getInputLevel",
			constant = @Constant(
					intValue = 15
			)
	)
	private int onGetInputLevelModifyRedstoneBlockPower(int oldPower) {
		return Tweaks.Comparator.REDSTONE_BLOCKS_VALID_SIDE_INPUT.get() ? Tweaks.RedstoneBlock.POWER_WEAK.get() : 0;
	}
	
	@Inject(
			method = "updateTarget",
			cancellable = true,
			at = @At(
					value = "HEAD"
			)
	)
	private void onUpdateTargetInjectAtHead(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
		UpdateOrder updateOrder = state.isOf(Blocks.COMPARATOR) ? Tweaks.Comparator.BLOCK_UPDATE_ORDER.get() : Tweaks.Repeater.BLOCK_UPDATE_ORDER.get();
		((RTIWorld)world).dispatchBlockUpdates(pos, state.get(Properties.HORIZONTAL_FACING).getOpposite(), state.getBlock(), updateOrder);
		
		ci.cancel();
	}
	
	@ModifyConstant(
			method = "getOutputLevel",
			constant = @Constant(
					intValue = 15
			)
	)
	private int getWeakRedstonePower(int oldValue) {
		return Tweaks.Repeater.POWER_WEAK.get();
	}
	
	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		return BlockHelper.microTickModeBlockEvent(state, world, pos, type, data);
	}
	
	@Override
	public boolean continueAction(World world, BlockPos pos, int type) {
		if (type == 0) {
			scheduleTickOnScheduledTick((ServerWorld)world, pos, world.getBlockState(pos), world.getRandom());
		}
		
		return false;
	}
	
	private int getPowerOutput(BlockView world, BlockPos pos, BlockState state, Direction dir, boolean strong) {
		if (dir != state.get(Properties.HORIZONTAL_FACING) || !state.get(Properties.POWERED)) {
			return 0;
		}
		
		int power = ((RTIRedstoneDiode)this).getPowerOutput(world, pos, state, strong);
		
		BlockPos belowPos = pos.down();
		BlockState belowState = world.getBlockState(belowPos);
		
		return strong ? ((RTIAbstractBlockState)belowState).strongPowerOverride(power) : ((RTIAbstractBlockState)belowState).weakPowerOverride(power);
	}
	
	private void scheduleTickOnScheduledTick(ServerWorld world, BlockPos pos, BlockState state, Random random) {
		BlockPos belowPos = pos.down();
		RTIAbstractBlockState belowState = (RTIAbstractBlockState)world.getBlockState(belowPos);
		
		boolean powered = state.get(Properties.POWERED);
		int delay = belowState.delayOverride(powered ? Tweaks.Repeater.DELAY_FALLING_EDGE.get() : Tweaks.Repeater.DELAY_RISING_EDGE.get());
		
		if (Tweaks.Repeater.MICRO_TICK_MODE.get() || belowState.forceMicroTickMode()) {
			if (!((RTIServerWorld)world).hasBlockEvent(pos, state.getBlock())) {
				world.addSyncedBlockEvent(pos, state.getBlock(), delay, 0);
			}
		} else {
			TickPriority priority = belowState.tickPriorityOverride(powered ? Tweaks.Repeater.TICK_PRIORITY_FALLING_EDGE.get() : Tweaks.Repeater.TICK_PRIORITY_RISING_EDGE.get());
			
			TickSchedulerHelper.scheduleBlockTick(world, pos, state, delay, priority);
		}
	}
}
