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
import net.minecraft.world.World;

import redstonetweaks.helper.WorldHelper;
import redstonetweaks.setting.types.DirectionalBooleanSetting;

@Mixin(TntBlock.class)
public abstract class TntBlockMixin extends AbstractBlock {
	
	@Shadow public static void primeTnt(World world, BlockPos pos) {}
	
	public TntBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onOnBlockAddedRedirectGetReceivedPower(World world1, BlockPos blockPos, BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		return WorldHelper.isPowered(world, pos, state, false, getQC(), randQC());
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onNeighborUpdateRedirectGetReceivedPower(World world1, BlockPos blockPos, BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		return WorldHelper.isPowered(world, pos, state, false, getQC(), randQC());
	}

	@Inject(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/TntBlock;primeTnt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", shift = Shift.BEFORE), cancellable = true)
	private void onOnBlockAddedInjectBeforePrimeTnt(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
		if (world.getBlockTickScheduler().isTicking(pos, state.getBlock())) {
			ci.cancel();
		} else {
			int delay = redstonetweaks.setting.Settings.TNT.DELAY.get();
			if (delay > 0) {
				world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, redstonetweaks.setting.Settings.TNT.TICK_PRIORITY.get());
				ci.cancel();
			}
		}
	}
	
	@Inject(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/TntBlock;primeTnt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", shift = Shift.BEFORE), cancellable = true)
	private void onNeighborUpdateInjectBeforePrimeTnt(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify, CallbackInfo ci) {
		if (world.getBlockTickScheduler().isTicking(pos, state.getBlock())) {
			ci.cancel();
		} else {
			int delay = redstonetweaks.setting.Settings.TNT.DELAY.get();
			if (delay > 0) {
				world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, redstonetweaks.setting.Settings.TNT.TICK_PRIORITY.get());
				ci.cancel();
			}
		}
	}
	
	@ModifyArg(method = "onDestroyedByExplosion", index = 0, at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
	private int onOnDestroyedByExplosionOnNextIntModifyBound(int bound) {
		return bound < 1 ? 1 : bound;
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (redstonetweaks.setting.Settings.TNT.LAZY.get() || WorldHelper.isPowered(world, pos, state, true, getQC(), randQC())) {
			primeTnt(world, pos);
			world.removeBlock(pos, false);
		}
	}
	
	private DirectionalBooleanSetting getQC() {
		return redstonetweaks.setting.Settings.TNT.QC;
	}
	
	private boolean randQC() {
		return redstonetweaks.setting.Settings.TNT.RANDOMIZE_QC.get();
	}
}
