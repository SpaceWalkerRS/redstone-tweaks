package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;

@Mixin(LecternBlock.class)
public abstract class LecternBlockMixin {
	
	@Shadow private native static void setPowered(World world, BlockPos pos, BlockState state, boolean powered);
	
	@Inject(method = "setPowered(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", cancellable = true, at = @At(value = "HEAD"))
	private static void onSetPoweredInjectAtHead(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
		int delay = LECTERN.get(RISING_DELAY);
		if (delay > 0) {
			TickPriority priority = LECTERN.get(RISING_TICK_PRIORITY);
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, priority);
			world.syncWorldEvent(1043, pos, 0);
			
			ci.cancel();
		}
	}
	
	@Redirect(method = "setPowered(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private static <T> void onSetPoweredRedirectSchedule0(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int delay, World world, BlockPos pos, BlockState state) {
		scheduleDepower(world, pos, state);
	}
	
	@Inject(method = "scheduledTick", cancellable = true, at = @At(value = "HEAD"))
	private void onScheduledTickInjectAtHead(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (!state.get(Properties.POWERED)) {
			setPowered(world, pos, state, false);
			scheduleDepower(world, pos, state);
			
			ci.cancel();
		}
	}
	
	@ModifyConstant(method = "getWeakRedstonePower", constant = @Constant(intValue = 15))
	private int onGetWeakRedstonePowerModify15(int oldValue) {
		return LECTERN.get(WEAK_POWER);
	}
	
	@ModifyConstant(method = "getStrongRedstonePower", constant = @Constant(intValue = 15))
	private int onGetStrongRedstonePowerModify15(int oldValue) {
		return LECTERN.get(STRONG_POWER);
	}
	
	private static void scheduleDepower(World world, BlockPos pos, BlockState state) {
		int delay = LECTERN.get(FALLING_DELAY);
		if (delay == 0) {
			if (!world.isClient()) {
				state.scheduledTick((ServerWorld)world, pos, world.getRandom());
			}
		} else {
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, LECTERN.get(FALLING_TICK_PRIORITY));
		}
	}
}
