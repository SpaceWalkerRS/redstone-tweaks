package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.DiodeOverrides;

@Mixin(ComparatorBlock.class)
public abstract class ComparatorBlockMixin extends DiodeBlock implements DiodeOverrides {

	private ComparatorBlockMixin(Properties properties) {
		super(properties);
	}

	@Inject(
		method = "getDelay",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakDelay(BlockState state, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(Tweaks.Comparator.delay());
	}

	@ModifyConstant(
		method = "calculateOutputSignal",
		constant = @Constant(
			intValue = 0,
			ordinal = 0
		)
	)
	private int rtTweakOutputSignal(int zero, Level level, BlockPos pos, BlockState state) {
		return invertAlternateSignal(state) ? -1 : zero;
	}

	@ModifyConstant(
		method = "shouldTurnOn",
		constant = @Constant(
			intValue = 0,
			ordinal = 0
		)
	)
	private int rtTweakShouldTurnOn(int zero, Level level, BlockPos pos, BlockState state) {
		return invertAlternateSignal(state) ? -1 : zero;
	}

	@ModifyConstant(
		method = "getInputSignal",
		constant = @Constant(
			intValue = 15
		)
	)
	private int rtTweakInputSignal(int fifteen, Level level, BlockPos pos, BlockState state) {
		return Tweaks.Global.signalMax();
	}

	@Redirect(
		method = "checkTickOnNeighbor",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/ticks/TickPriority;HIGH:Lnet/minecraft/world/ticks/TickPriority;"
		)
	)
	private TickPriority rtTweakTickPriorityPrioritized() {
		return Tweaks.Comparator.tickPriorityPrioritized();
	}

	@Redirect(
		method = "checkTickOnNeighbor",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/ticks/TickPriority;NORMAL:Lnet/minecraft/world/ticks/TickPriority;"
		)
	)
	private TickPriority rtTweakTickPriority() {
		return Tweaks.Comparator.tickPriority();
	}

	@Override
	public boolean invertAlternateSignal(BlockState state) {
		return state.getValue(ComparatorBlock.MODE) == ComparatorMode.SUBTRACT && Tweaks.Comparator.additionMode();
	}
}
