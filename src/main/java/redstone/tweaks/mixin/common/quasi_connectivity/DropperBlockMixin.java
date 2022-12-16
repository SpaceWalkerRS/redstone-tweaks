package redstone.tweaks.mixin.common.quasi_connectivity;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DropperBlock;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.DispenserOverrides;

@Mixin(DropperBlock.class)
public abstract class DropperBlockMixin implements DispenserOverrides {

	@Override
	public boolean lazy() {
		return Tweaks.Dropper.lazy();
	}

	@Override
	public Map<Direction, Boolean> quasiConnectivity() {
		return Tweaks.Dropper.quasiConnectivity();
	}

	@Override
	public boolean quasiConnectivity(Direction dir) {
		return Tweaks.Dropper.quasiConnectivity(dir);
	}

	@Override
	public boolean randomizeQuasiConnectivity() {
		return Tweaks.Dropper.randomizeQuasiConnectivity();
	}
}
