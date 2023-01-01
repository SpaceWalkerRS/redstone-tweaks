package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityMixin {

	@Inject(
		method = "doNeighborUpdates",
		at = @At(
			value = "TAIL"
		)
	)
	private static void rtTweakUpdateNeighborsWhenPeeking(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
		if (Tweaks.ShulkerBox.updateNeighborsWhenPeeking()) {
			level.updateNeighborsAt(pos, state.getBlock());
		}
	}
}
