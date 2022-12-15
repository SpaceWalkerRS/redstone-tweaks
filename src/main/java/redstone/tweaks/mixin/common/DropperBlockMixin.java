package redstone.tweaks.mixin.common;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.DispenserOverrides;

@Mixin(DropperBlock.class)
public class DropperBlockMixin implements DispenserOverrides {

	@Override
	public int delay() {
		return Tweaks.Dropper.delay();
	}

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

	@Override
	public TickPriority tickPriority() {
		return Tweaks.Dropper.tickPriority();
	}
}
