package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.block.FrostedIceBlock;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import redstonetweaks.helper.TickSchedulerHelper;

@Mixin(FrostedIceBlock.class)
public abstract class FrostedIceBlockMixin {
	
	@Shadow public abstract void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
	
	@Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerTickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onScheduledTickRedirectSchedule(ServerTickScheduler<T> tickScheduler, BlockPos pos1, T block, int delay, BlockState state, ServerWorld world, BlockPos pos, Random random) {
		delay = MathHelper.nextInt(random, FROSTED_ICE.get(DELAY_MIN), FROSTED_ICE.get(DELAY_MAX));
		TickSchedulerHelper.schedule(world, state, tickScheduler, pos, block, delay, FROSTED_ICE.get(TICK_PRIORITY));
	}
}
