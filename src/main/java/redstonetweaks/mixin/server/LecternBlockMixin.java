package redstonetweaks.mixin.server;

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
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;

import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(LecternBlock.class)
public abstract class LecternBlockMixin {
	
	@Shadow private native static void setPowered(World world, BlockPos pos, BlockState state);
	
	@Redirect(method = "setPowered(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private static <T> void onSetPoweredRedirectSchedule0(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int delay, World world, BlockPos pos, BlockState state) {
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, Tweaks.Lectern.DELAY_FALLING_EDGE.get(), Tweaks.Lectern.TICK_PRIORITY_FALLING_EDGE.get());
	}
	
	@Inject(method = "scheduledTick", cancellable = true, at = @At(value = "HEAD"))
	private void onScheduledTickInjectAtHead(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (!state.get(Properties.POWERED)) {
			setPowered(world, pos, state);
			
			ci.cancel();
		}
	}
	
	@ModifyConstant(method = "getWeakRedstonePower", constant = @Constant(intValue = 15))
	private int onGetWeakRedstonePowerModify15(int oldValue) {
		return Tweaks.Lectern.POWER_WEAK.get();
	}
	
	@ModifyConstant(method = "getStrongRedstonePower", constant = @Constant(intValue = 15))
	private int onGetStrongRedstonePowerModify15(int oldValue) {
		return Tweaks.Lectern.POWER_STRONG.get();
	}
}
