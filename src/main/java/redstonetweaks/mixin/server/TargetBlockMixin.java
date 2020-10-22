package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.TargetBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.WorldAccess;

import redstonetweaks.block.AnaloguePowerComponentBlockEntity;
import redstonetweaks.setting.Settings;

@Mixin(TargetBlock.class)
public class TargetBlockMixin implements BlockEntityProvider {
	
	@ModifyConstant(method = "trigger", constant = @Constant(intValue = 20))
	private static int onTriggerPersistentProjectileDelay(int oldValue) {
		return Settings.TargetBlock.DELAY_PERSISTENT_PROJECTILE.get();
	}
	
	@ModifyConstant(method = "trigger", constant = @Constant(intValue = 8))
	private static int onTriggerDefaultDelay(int oldValue) {
		return Settings.TargetBlock.DELAY_DEFAULT.get();
	}
	
	@ModifyConstant(method = "calculatePower", constant = @Constant(doubleValue = 15.0D))
	private static double onCalculatePowerModify15(double oldValue) {
		return Settings.Global.POWER_MAX.get();
	}
	
	@Inject(method = "setPower", at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/world/WorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private static void onSetPowerInjectBeforeSetBlockState(WorldAccess world, BlockState state, int power, BlockPos pos, int delay, CallbackInfo ci) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof AnaloguePowerComponentBlockEntity) {
			((AnaloguePowerComponentBlockEntity)blockEntity).setPower(power);
		}
	}
	
	@SuppressWarnings("unchecked")
	@ModifyArg(method = "setPower", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;with(Lnet/minecraft/state/property/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"))
	private static <T extends Comparable<T>, V extends T> V onSetPowerModifyPower(V oldValue) {
		return (V)(Integer)Math.min((Integer)oldValue, 15);
	}
	
	@Redirect(method = "setPower", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private static <T> void onSetPowerRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int delay1, WorldAccess world, BlockState state, int power, BlockPos pos, int delay) {
		if (delay > 0) {
			tickScheduler.schedule(pos, object, delay, Settings.TargetBlock.TICK_PRIORITY.get());
		} else if (world instanceof ServerWorld) {
			state.scheduledTick((ServerWorld)world, pos, world.getRandom());
		}
	}
	
	@Inject(method = "scheduledTick", at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onScheduledTickInjectBeforeSetBlockState(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof AnaloguePowerComponentBlockEntity) {
			((AnaloguePowerComponentBlockEntity)blockEntity).setPower(0);
		}
	}
	
	@Inject(method = "getWeakRedstonePower", cancellable = true, at = @At(value = "HEAD"))
	private void onGetWeakRedstonePowerInjectAtHead(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof AnaloguePowerComponentBlockEntity) {
			int power = ((AnaloguePowerComponentBlockEntity)blockEntity).getPower();
			
			if (power > 0 ) {
				cir.setReturnValue(power);
				cir.cancel();
			}
		}
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new AnaloguePowerComponentBlockEntity();
	}
}
