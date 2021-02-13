package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.TargetBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import redstonetweaks.block.entity.PowerBlockEntity;
import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.setting.Tweaks;

@Mixin(TargetBlock.class)
public abstract class TargetBlockMixin extends AbstractBlock implements BlockEntityProvider {
	
	public TargetBlockMixin(Settings settings) {
		super(settings);
	}
	
	@ModifyConstant(method = "trigger", constant = @Constant(intValue = 20))
	private static int onTriggerPersistentProjectileDelay(int oldValue) {
		return Tweaks.TargetBlock.DELAY_PERSISTENT_PROJECTILE.get();
	}
	
	@ModifyConstant(method = "trigger", constant = @Constant(intValue = 8))
	private static int onTriggerDefaultDelay(int oldValue) {
		return Tweaks.TargetBlock.DELAY_DEFAULT.get();
	}
	
	@ModifyConstant(method = "calculatePower", constant = @Constant(doubleValue = 15.0D))
	private static double onCalculatePowerModify15(double oldValue) {
		return Tweaks.Global.POWER_MAX.get();
	}
	
	@Inject(method = "setPower", cancellable = true, at = @At(value = "HEAD"))
	private static void onSetPowerInjectAtHead(WorldAccess world, BlockState state, int power, BlockPos pos, int delay, CallbackInfo ci) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (blockEntity instanceof PowerBlockEntity) {
			((PowerBlockEntity)blockEntity).setPower(power);
		}
		
		BlockState newState = state.with(Properties.POWER, Math.min(power, 15));
		world.setBlockState(pos, newState, 3);
		
		updateNeighborsOnPowerChange(world, pos, state);
		
		TickSchedulerHelper.scheduleBlockTick(world, pos, newState, delay, Tweaks.TargetBlock.TICK_PRIORITY.get());
		
		ci.cancel();
	}
	
	@Inject(method = "scheduledTick", at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onScheduledTickInjectBeforeSetBlockState(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (blockEntity instanceof PowerBlockEntity) {
			((PowerBlockEntity)blockEntity).setPower(0);
		}
	}
	
	@Inject(method = "scheduledTick", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onScheduledTickInjectAfterSetBlockState(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		updateNeighborsOnPowerChange(world, pos, state);
	}
	
	@Inject(method = "getWeakRedstonePower", cancellable = true, at = @At(value = "HEAD"))
	private void onGetWeakRedstonePowerInjectAtHead(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof PowerBlockEntity) {
			PowerBlockEntity powerBlockEntity = ((PowerBlockEntity)blockEntity);
			
			powerBlockEntity.ensureCorrectPower(state);
			
			cir.setReturnValue(powerBlockEntity.getPower());
			cir.cancel();
		}
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return (Tweaks.TargetBlock.EMITS_STRONG_POWER.get()) ? getWeakRedstonePower(state, world, pos, direction) : 0;
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new PowerBlockEntity();
	}
	
	private static void updateNeighborsOnPowerChange(WorldAccess world, BlockPos pos, BlockState state) {
		if (Tweaks.TargetBlock.EMITS_STRONG_POWER.get()) {
			((RTIWorld)world).dispatchBlockUpdates(pos, null, state.getBlock(), Tweaks.TargetBlock.BLOCK_UPDATE_ORDER.get());
		} else {
			world.updateNeighbors(pos, state.getBlock());
		}
	}
}
