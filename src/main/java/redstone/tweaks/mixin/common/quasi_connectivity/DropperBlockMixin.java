package redstone.tweaks.mixin.common.quasi_connectivity;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.block.DropperBlock;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.DispenserOverrides;
import redstone.tweaks.world.level.block.QuasiConnectivity;

@Mixin(DropperBlock.class)
public abstract class DropperBlockMixin implements DispenserOverrides {

	@Override
	public boolean lazy() {
		return Tweaks.Dropper.lazy();
	}

	@Override
	public QuasiConnectivity quasiConnectivity() {
		return Tweaks.Dropper.quasiConnectivity();
	}

	@Override
	public boolean randomizeQuasiConnectivity() {
		return Tweaks.Dropper.randomizeQuasiConnectivity();
	}
}
