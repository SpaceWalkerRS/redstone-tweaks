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
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import redstonetweaks.helper.PistonHelper;
import redstonetweaks.interfaces.RTIPistonBlockEntity;

@Mixin(PistonHeadBlock.class)
public abstract class PistonHeadBlockMixin {
	
	@Shadow protected abstract boolean method_26980(BlockState blockState, BlockState blockState2);
	
	@Redirect(method = "onStateReplaced", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonHeadBlock;method_26980(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)Z"))
	private boolean onOnStateRepacedRedirectMethod_26980(PistonHeadBlock pistonHead, BlockState blockState1, BlockState blockState2, BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		return method_26980(blockState1, blockState2) && !(moved && PistonHelper.movableWhenExtended(state.get(Properties.PISTON_TYPE) == PistonType.STICKY));
	}
	
	@Inject(method = "canPlaceAt", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/PistonHeadBlock;method_26980(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)Z"))
	private void onCanPlaceAtInjectBeforeMethod_26980(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState neighborState) {
		if (neighborState.isOf(Blocks.MOVING_PISTON)) {
			boolean canPlace = true;
			
			Direction facing = state.get(Properties.FACING);
			
			if (neighborState.get(Properties.FACING) != facing) {
				BlockEntity blockEntity = world.getBlockEntity(pos.offset(facing.getOpposite()));
				
				if (blockEntity instanceof PistonBlockEntity) {
					BlockState movedState = ((RTIPistonBlockEntity)blockEntity).getMovedState();
					
					canPlace = movedState.getBlock() instanceof PistonBlock && movedState.get(Properties.EXTENDED) && movedState.get(Properties.FACING) == facing;
				}
			}
			
			cir.setReturnValue(canPlace);
			cir.cancel();
		}
	}
}
