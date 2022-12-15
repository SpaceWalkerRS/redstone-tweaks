package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.DiodeOverrides;

@Mixin(RepeaterBlock.class)
public class RepeaterBlockMixin implements DiodeOverrides {

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
	public boolean invertAlternateSignal(BlockState state) {
		return false;
	}

	@Override
	public boolean microTickMode() {
		return Tweaks.Repeater.microTickMode();
	}

	@Override
	public int signal(BlockGetter level, BlockPos pos, BlockState state) {
		return Tweaks.Repeater.signal();
	}

	@Override
	public int signalDirect(BlockGetter level, BlockPos pos, BlockState state) {
		return Tweaks.Repeater.signalDirect();
	}
}
