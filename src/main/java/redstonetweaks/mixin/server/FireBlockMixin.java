package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;

import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.setting.settings.Tweaks;
import redstonetweaks.util.RTMathHelper;

@Mixin(FireBlock.class)
public class FireBlockMixin {
	
	@Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerTickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onScheduledTickRedirectSchedule(ServerTickScheduler<T> tickScheduler, BlockPos pos1, T block, int delay, BlockState state, ServerWorld world, BlockPos pos, Random random) {
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, getDelay(world), Tweaks.Fire.TICK_PRIORITY.get());
	}
	
	@Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onOnBlockAddedRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T block, int delay, BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, getDelay(world), Tweaks.Fire.TICK_PRIORITY.get());
	}
	
	private int getDelay(World world) {
		return RTMathHelper.randomInt(world.getRandom(), Tweaks.Fire.DELAY_MIN.get(), Tweaks.Fire.DELAY_MAX.get());
	}
}
