package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import redstonetweaks.block.piston.PistonSettings;
import redstonetweaks.helper.PistonHelper;
import redstonetweaks.interfaces.mixin.RTIPistonBlockEntity;

@Mixin(PistonHeadBlock.class)
public abstract class PistonHeadBlockMixin {
	
	@Shadow protected abstract boolean method_26980(BlockState blockState, BlockState blockState2);
	
	@Redirect(method = "onBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonHeadBlock;method_26980(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)Z"))
	private boolean onOnBreakRedirectMethod_26980(PistonHeadBlock pistonHeadBlock, BlockState pistonHead, BlockState behindState, World world, BlockPos pos, BlockState state, PlayerEntity player) {
		return !looseHead(pistonHead) && (PistonHelper.isExtendingBackwards(world, pos.offset(pistonHead.get(Properties.FACING).getOpposite()), behindState, pistonHead.get(Properties.FACING)) || method_26980(pistonHead, behindState));
	}
	
	@Redirect(method = "onStateReplaced", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonHeadBlock;method_26980(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)Z"))
	private boolean onOnStateReplacedRedirectMethod_26980(PistonHeadBlock pistonHeadBlock, BlockState pistonHead, BlockState behindState, BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		return !moved && !looseHead(pistonHead) && method_26980(pistonHead, behindState);
	}
	
	@Inject(method = "canPlaceAt", cancellable = true, at = @At(value = "HEAD"))
	private void onCanPlaceAtInjectAtHead(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (looseHead(state)) {
			cir.setReturnValue(true);
			cir.cancel();
		}
	}
	
	@Inject(method = "canPlaceAt", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/PistonHeadBlock;method_26980(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)Z"))
	private void onCanPlaceAtInjectBeforeMethod_26980(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState neighborState) {
		if (neighborState.isOf(Blocks.MOVING_PISTON)) {
			boolean canPlace = true;
			
			Direction facing = state.get(Properties.FACING);
			
			if (neighborState.get(Properties.FACING) != facing) {
				BlockEntity blockEntity = world.getBlockEntity(pos.offset(facing.getOpposite()));
				
				if (blockEntity instanceof PistonBlockEntity) {
					BlockState movedState = ((RTIPistonBlockEntity)blockEntity).getMovedMovingState();
					
					canPlace = PistonHelper.isPiston(movedState) && movedState.get(Properties.EXTENDED) && movedState.get(Properties.FACING) == facing;
				}
			}
			
			cir.setReturnValue(canPlace);
			cir.cancel();
		}
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;canPlaceAt(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onNeighborUpdateRedirectCanPlaceAt(BlockState pistonHead, WorldView world, BlockPos pos) {
		return PistonHelper.isPiston(world.getBlockState(pos.offset(pistonHead.get(Properties.FACING).getOpposite())));
	}
	
	private boolean looseHead(BlockState state) {
		return PistonSettings.looseHead(PistonHelper.isStickyHead(state));
	}
}
