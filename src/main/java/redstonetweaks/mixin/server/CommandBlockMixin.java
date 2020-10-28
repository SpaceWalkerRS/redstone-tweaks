package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;

import redstonetweaks.helper.WorldHelper;
import redstonetweaks.setting.Settings;

@Mixin(CommandBlock.class)
public class CommandBlockMixin {
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onNeighborUpdateRedirectGetReceivedPower(World world1, BlockPos blockPos, BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		return world.isReceivingRedstonePower(pos) || WorldHelper.isQCPowered(world, pos, state, false, Settings.CommandBlock.QC, Settings.CommandBlock.RANDOMIZE_QC.get());
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos, T block, int oldDelay) {
		tickScheduler.schedule(pos, block, Settings.CommandBlock.DELAY.get(), Settings.CommandBlock.TICK_PRIORITY.get());
	}
	
	@Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerTickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onScheduledTickRedirectSchedule(ServerTickScheduler<T> tickScheduler, BlockPos pos, T block, int oldDelay) {
		tickScheduler.schedule(pos, block, Settings.CommandBlock.DELAY.get(), Settings.CommandBlock.TICK_PRIORITY.get());
	}
	
	@Redirect(method = "onPlaced", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onOnPlacedRedirectGetReceivedPower(World world1, BlockPos blockPos, World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		return world.isReceivingRedstonePower(pos) || WorldHelper.isQCPowered(world, pos, state, false, Settings.CommandBlock.QC, Settings.CommandBlock.RANDOMIZE_QC.get());
	}
}
