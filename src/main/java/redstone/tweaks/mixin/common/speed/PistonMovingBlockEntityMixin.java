package redstone.tweaks.mixin.common.speed;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.IPistonMovingBlockEntity;
import redstone.tweaks.interfaces.mixin.PistonOverrides;

@Mixin(PistonMovingBlockEntity.class)
public abstract class PistonMovingBlockEntityMixin implements IPistonMovingBlockEntity {

	private static final String NBT_KEY_SPEED = "rt_speed";

	@Shadow private boolean extending;
	@Shadow private float progress;

	private int rt_speed = 2;
	private float rt_amountPerStep = 0.5F;
	private float gs_numberOfSteps = 2.0F;

	@Inject(
		method = "load",
		at = @At(
			value = "TAIL"
		)
	)
	private void rtLoadNbt(CompoundTag nbt, CallbackInfo ci) {
		if (nbt.contains(NBT_KEY_SPEED)) {
			initSpeed(nbt.getInt(NBT_KEY_SPEED));
		}
	}

	@Inject(
		method = "saveAdditional",
		at = @At(
			value = "TAIL"
		)
	)
	private void rtSaveNbt(CompoundTag nbt, CallbackInfo ci) {
		nbt.putInt(NBT_KEY_SPEED, rt_speed);
	}

	@ModifyConstant(
		method = "tick",
		constant = @Constant(
			floatValue = 0.5F
		)
	)
	private static float rtTweakSpeed(float amount, Level level, BlockPos pos, BlockState state, PistonMovingBlockEntity movingBlockEntity) {
		return ((IPistonMovingBlockEntity)movingBlockEntity).getAmountPerStep();
	}

	@Override
	public void init(PistonOverrides source) {
		initSpeed(Tweaks.Piston.speed(extending, source.isSticky()));
	}

	@Override
	public float getAmountPerStep() {
		return rt_amountPerStep;
	}

	private void initSpeed(int speed) {
		rt_speed = speed;

		if (rt_speed == 0) {
			rt_amountPerStep = 1.0F;
			gs_numberOfSteps = 1.0F;

			// This ensures the block entity finishes the first time it is ticked
			// Otherwise the behavior would be the same as if the speed was set to 1
			progress = 1.0F;
		} else {
			rt_amountPerStep = 1.0F / rt_speed;
			gs_numberOfSteps = rt_speed;
		}
	}
}
