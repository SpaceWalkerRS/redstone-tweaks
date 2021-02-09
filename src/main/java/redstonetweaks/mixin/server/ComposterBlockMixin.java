package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.setting.Tweaks;

@Mixin(ComposterBlock.class)
public abstract class ComposterBlockMixin {
	
	@Shadow public abstract void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
	
	@Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onOnBlockAddedRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T block, int delay, BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, Tweaks.Composter.DELAY.get(), Tweaks.Composter.TICK_PRIORITY.get());
	}
	
	@Redirect(method = "addToComposter", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private static <T> void onAddToComposterRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T block, int delay, BlockState state, WorldAccess world, BlockPos pos, ItemStack item) {
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, Tweaks.Composter.DELAY.get(), Tweaks.Composter.TICK_PRIORITY.get());
	}
}
