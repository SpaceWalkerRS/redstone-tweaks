package redstone.tweaks.mixin.common.movable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.IPistonStructureResolver;
import redstone.tweaks.interfaces.mixin.PistonOverrides;

@Mixin(PistonStructureResolver.class)
public class PistonStructureResolverMixin implements IPistonStructureResolver {

	@Shadow private boolean extending;

	private PistonOverrides source;

	@Inject(
		method = "<init>",
		at = @At(
			value = "TAIL"
		)
	)
	private void rtInit(Level level, BlockPos pos, Direction facing, boolean extending, CallbackInfo ci) {
		BlockState sourceState = level.getBlockState(pos);
		Block sourceBlock = sourceState.getBlock();

		if (sourceBlock instanceof PistonOverrides) {
			init((PistonOverrides)sourceBlock);
		}
	}

	@ModifyConstant(
		method = "addBlockLine",
		constant = @Constant(
			intValue = 12
		)
	)
	private int rtTweakPushLimit(int limit) {
		return Tweaks.Piston.moveLimit(extending, source.isSticky());
	}

	@Override
	public void init(PistonOverrides source) {
		this.source = source;
	}
}
