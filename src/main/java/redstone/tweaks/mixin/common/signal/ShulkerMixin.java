package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.IShulker;

@Mixin(Shulker.class)
public abstract class ShulkerMixin extends Entity implements IShulker {

	private ShulkerMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@Shadow private boolean isClosed() { return false; }

	@Inject(
		method = "setRawPeekAmount",
		at = @At(
			value = "TAIL"
		)
	)
	private void rtTweakUpdateNeighborsWhenPeeking(int rawPeekAmount, CallbackInfo ci) {
		if (!level.isClientSide() && Tweaks.Shulker.conductRedstone() && Tweaks.Shulker.updateNeighborsWhenPeeking()) {
			BlockPos pos = this.blockPosition();
			BlockState state = level.getBlockState(pos);

			state.updateNeighbourShapes(level, pos, Block.UPDATE_ALL);
			level.updateNeighborsAt(pos, state.getBlock());
		}
	}

	@Override
	public boolean rt_isClosed() {
		return isClosed();
	}
}
