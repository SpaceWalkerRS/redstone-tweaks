package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstonetweaks.settings.Settings;

@Mixin(PistonHandler.class)
public class PistonHandlerMixin {
	
	@Shadow @Final private boolean retracted;
	@Shadow @Final private BlockPos posTo;
	
	private boolean sticky;
	
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
	
	@ModifyConstant(method = "tryMove", constant = @Constant(intValue = 12))
	private int pushLimit(int oldPushLimit) {
		return sticky ? Settings.StickyPiston.PUSH_LIMIT.get() : Settings.NormalPiston.PUSH_LIMIT.get();
	}
}
