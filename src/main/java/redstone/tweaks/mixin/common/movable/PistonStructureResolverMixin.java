package redstone.tweaks.mixin.common.movable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.IPistonStructureResolver;
import redstone.tweaks.interfaces.mixin.PistonOverrides;

@Mixin(PistonStructureResolver.class)
public abstract class PistonStructureResolverMixin implements IPistonStructureResolver {

	@Shadow @Final private Level level;
	@Shadow @Final private boolean extending;
	@Shadow @Final private BlockPos startPos;
	@Shadow @Final private Direction pushDirection;

	private PistonOverrides source;

	@Inject(
		method = "resolve",
		locals = LocalCapture.CAPTURE_FAILHARD,
		cancellable = true,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;isPushable(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;ZLnet/minecraft/core/Direction;)Z"
		)
	)
	private void rtPreventSelfPush(CallbackInfoReturnable<Boolean> cir, BlockState state) {
		// make sure a piston does not push its own extending head
		if (extending && state.is(Blocks.MOVING_PISTON)) {
			if (PistonOverrides.isExtendingHead(level, startPos, pushDirection, source.isSticky())) {
				cir.setReturnValue(false);
			}
		}
	}

	@ModifyConstant(
		method = "addBlockLine",
		constant = @Constant(
			intValue = 12
		)
	)
	private int rtTweakMoveLimit(int limit) {
		return Tweaks.Piston.moveLimit(extending, source.isSticky());
	}

	@Override
	public void init(PistonOverrides source) {
		this.source = source;
	}
}
