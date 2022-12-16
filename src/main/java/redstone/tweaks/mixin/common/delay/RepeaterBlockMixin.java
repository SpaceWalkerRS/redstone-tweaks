package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.DiodeOverrides;

@Mixin(RepeaterBlock.class)
public abstract class RepeaterBlockMixin implements DiodeOverrides {

	@ModifyConstant(
		method = "getDelay",
		constant = @Constant(
			intValue = 2
		)
	)
	private int rtTweakDelay(int two, BlockState state) {
		return state.getValue(DiodeBlock.POWERED) ? Tweaks.Repeater.delayFallingEdge() : Tweaks.Repeater.delayRisingEdge();
	}

	@Override
	public boolean microtickMode() {
		return Tweaks.Repeater.microtickMode();
	}
}
