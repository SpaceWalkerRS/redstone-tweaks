package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import redstonetweaks.settings.Settings;

@Mixin(PistonHandler.class)
public abstract class PistonHandlerMixin {
	
	@Shadow @Final private World world;
	@Shadow @Final private boolean retracted;
	@Shadow @Final private BlockPos posTo;
	@Shadow @Final private Direction motionDirection;
	
	private boolean sticky;
	
	private boolean onTryMoveIsBlockSticky0IsStickyPiston;
	private Direction onCanMoveAdjacentBlockFacing;
	
	@Shadow private static native boolean isBlockSticky(Block block);
	@Shadow private static native boolean isAdjacentBlockStuck(Block block, Block block2);
	
	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void onInitInjectAtReturn(World world, BlockPos pos, Direction dir, boolean retracted, CallbackInfo ci) {
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof PistonBlock) {
			sticky = state.isOf(Blocks.STICKY_PISTON);
		} else if (state.isOf(Blocks.MOVING_PISTON)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof PistonBlockEntity) {
				PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
				if (pistonBlockEntity.isSource()) {
					BlockState pushedState = pistonBlockEntity.getPushedBlock();
					if (pushedState.getBlock() instanceof PistonBlock) {
						sticky = pushedState.isOf(Blocks.STICKY_PISTON);
					}
				}
			}
		}
	}
	
	@Inject(method = "isBlockSticky", cancellable = true, at = @At(value = "HEAD"))
	private static void onIsBlockStickyInjectAtHead(Block block, CallbackInfoReturnable<Boolean> cir) {
		if (Settings.StickyPiston.SUPER_STICKY.get() && block == Blocks.STICKY_PISTON) {
			cir.setReturnValue(true);
			cir.cancel();
		}
	}
	
	@Inject(method = "tryMove", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 0, shift = Shift.BEFORE, target = "Lnet/minecraft/block/piston/PistonHandler;isBlockSticky(Lnet/minecraft/block/Block;)Z"))
	private void onTryMoveInjectBeforeIsBlockSticky0(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir, BlockState state) {
		onTryMoveIsBlockSticky0IsStickyPiston = isSticky(state, motionDirection.getOpposite());
	}
	
	@Redirect(method = "tryMove", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/block/piston/PistonHandler;isBlockSticky(Lnet/minecraft/block/Block;)Z"))
	private boolean onTryMoveRedirectIsBlockSticky0(Block block) {
		return onTryMoveIsBlockSticky0IsStickyPiston;
	}
	
	@ModifyConstant(method = "tryMove", constant = @Constant(intValue = 12))
	private int pushLimit(int oldPushLimit) {
		return sticky ? Settings.StickyPiston.PUSH_LIMIT.get() : Settings.NormalPiston.PUSH_LIMIT.get();
	}
	
	@Inject(method = "canMoveAdjacentBlock", at = @At(value = "HEAD"))
	private void onCanMoveAdjacentBlockInjectAtHead(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		BlockState state = world.getBlockState(pos);
		if (Settings.StickyPiston.SUPER_STICKY.get() && state.isOf(Blocks.STICKY_PISTON)) {
			onCanMoveAdjacentBlockFacing = state.get(Properties.FACING);
		} else {
			onCanMoveAdjacentBlockFacing = null;
		}
	}
	
	@Redirect(method = "canMoveAdjacentBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"))
	private Direction[] onCanMoveAdjacentBlockRedirectValues() {
		return onCanMoveAdjacentBlockFacing == null ? Direction.values() : new Direction[] {onCanMoveAdjacentBlockFacing};
	}
	
	// Check if the block is sticky in the given direction
	private static boolean isSticky(BlockState state, Direction direction) {
		if (state.isOf(Blocks.SLIME_BLOCK) || state.isOf(Blocks.HONEY_BLOCK)) {
			return true;
		}
		if (Settings.StickyPiston.SUPER_STICKY.get()) {
			return state.isOf(Blocks.STICKY_PISTON) && state.get(Properties.FACING) == direction;
		}
		return false;
	}
}
