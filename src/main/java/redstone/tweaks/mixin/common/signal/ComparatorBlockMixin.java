package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.redstone.Redstone;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.DiodeOverrides;

@Mixin(ComparatorBlock.class)
public abstract class ComparatorBlockMixin extends DiodeBlock implements DiodeOverrides {

	private ComparatorBlockMixin(Properties properties) {
		super(properties);
	}

	@ModifyConstant(
		method = "shouldTurnOn",
		constant = @Constant(
			intValue = Redstone.SIGNAL_MIN,
			ordinal = 0
		)
	)
	private int rtTweakShouldTurnOn(int zero, Level level, BlockPos pos, BlockState state) {
		return invertAlternateSignal(state) ? -1 : zero;
	}

	@ModifyConstant(
		method = "getInputSignal",
		constant = @Constant(
			intValue = Redstone.SIGNAL_MAX
		)
	)
	private int rtTweakInputSignal(int fifteen, Level level, BlockPos pos, BlockState state) {
		return Tweaks.Global.signalMax();
	}

	@Override
	public boolean invertAlternateSignal(BlockState state) {
		return state.getValue(ComparatorBlock.MODE) == ComparatorMode.SUBTRACT && Tweaks.Comparator.additionMode();
	}

	@Override
	public int signal(BlockGetter level, BlockPos pos, BlockState state) {
		return getOutputSignal(level, pos, state);
	}

	@Override
	public int signalDirect(BlockGetter level, BlockPos pos, BlockState state) {
		return getOutputSignal(level, pos, state);
	}
}
