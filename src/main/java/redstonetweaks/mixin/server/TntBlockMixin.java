package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TntBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.setting.settings.Tweaks;
import redstonetweaks.setting.types.DirectionToBooleanSetting;

@Mixin(TntBlock.class)
public abstract class TntBlockMixin extends AbstractBlock {
	
	@Shadow public static void primeTnt(World world, BlockPos pos) {}
	
	public TntBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onOnBlockAddedRedirectGetReceivedPower(World world1, BlockPos blockPos, BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		return WorldHelper.isPowered(world, pos, false, getQC(), randQC());
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onNeighborUpdateRedirectGetReceivedPower(World world1, BlockPos blockPos, BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		return WorldHelper.isPowered(world, pos, false, getQC(), randQC());
	}

	@Inject(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/TntBlock;primeTnt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", shift = Shift.BEFORE), cancellable = true)
	private void onOnBlockAddedInjectBeforePrimeTnt(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
		if (!world.getBlockTickScheduler().isTicking(pos, state.getBlock())) {
			int delay = Tweaks.TNT.DELAY.get();
			TickPriority priority = Tweaks.TNT.TICK_PRIORITY.get();
			
			TickSchedulerHelper.scheduleBlockTick(world, pos, state, delay, priority);
		}
		
		ci.cancel();
	}
	
	@Inject(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/TntBlock;primeTnt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", shift = Shift.BEFORE), cancellable = true)
	private void onNeighborUpdateInjectBeforePrimeTnt(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify, CallbackInfo ci) {
		if (!world.getBlockTickScheduler().isTicking(pos, state.getBlock())) {
			int delay = Tweaks.TNT.DELAY.get();
			TickPriority priority = Tweaks.TNT.TICK_PRIORITY.get();
			
			TickSchedulerHelper.scheduleBlockTick(world, pos, state, delay, priority);
		}
		
		ci.cancel();
	}
	
	@ModifyArg(method = "onDestroyedByExplosion", index = 0, at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
	private int onOnDestroyedByExplosionOnNextIntModifyBound(int bound) {
		return bound < 1 ? 1 : bound;
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (Tweaks.TNT.LAZY.get() || WorldHelper.isPowered(world, pos, true, getQC(), randQC())) {
			primeTnt(world, pos);
			world.removeBlock(pos, false);
		}
	}
	
	private DirectionToBooleanSetting getQC() {
		return Tweaks.TNT.QC;
	}
	
	private boolean randQC() {
		return Tweaks.TNT.RANDOMIZE_QC.get();
	}
}
