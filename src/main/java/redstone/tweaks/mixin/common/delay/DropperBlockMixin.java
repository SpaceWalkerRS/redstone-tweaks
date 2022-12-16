package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.DispenserOverrides;

@Mixin(DropperBlock.class)
public abstract class DropperBlockMixin implements DispenserOverrides {

	@Override
	public int delay() {
		return Tweaks.Dropper.delay();
	}

	@Override
	public TickPriority tickPriority() {
		return Tweaks.Dropper.tickPriority();
	}
}
