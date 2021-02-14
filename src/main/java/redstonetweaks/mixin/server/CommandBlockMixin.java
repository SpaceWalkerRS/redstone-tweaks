package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;

import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(CommandBlock.class)
public class CommandBlockMixin {
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onNeighborUpdateRedirectGetReceivedPower(World world1, BlockPos blockPos, BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		return WorldHelper.isPowered(world, pos, state, false, Tweaks.CommandBlock.QC, Tweaks.CommandBlock.RANDOMIZE_QC.get());
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos blockPos, T obj, int oldDelay, BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, Tweaks.CommandBlock.DELAY.get(), Tweaks.CommandBlock.TICK_PRIORITY.get());
	}
	
	@Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerTickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onScheduledTickRedirectSchedule(ServerTickScheduler<T> tickScheduler, BlockPos blockPos, T obj, int oldDelay, BlockState state, ServerWorld world, BlockPos pos, Random random) {
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, Tweaks.CommandBlock.DELAY.get(), Tweaks.CommandBlock.TICK_PRIORITY.get());
	}
	
	@Redirect(method = "onPlaced", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onOnPlacedRedirectGetReceivedPower(World world1, BlockPos blockPos, World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		return WorldHelper.isPowered(world, pos, state, false, Tweaks.CommandBlock.QC, Tweaks.CommandBlock.RANDOMIZE_QC.get());
	}
}
