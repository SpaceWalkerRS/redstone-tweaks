package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.util.Rnd;

@Mixin(FireBlock.class)
public class FireBlockMixin {

	@Redirect(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerLevel;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakTickPriority(ServerLevel _level, BlockPos _pos, Block block, int delay, BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, Tweaks.Fire.tickPriority());
	}

	@Redirect(
		method = "onPlace",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakTickPriority(Level _level, BlockPos _pos, Block block, int delay, BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, Tweaks.Fire.tickPriority());
	}

	@Inject(
		method = "getFireTickDelay",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private static void rtTweakDelay(RandomSource rand, CallbackInfoReturnable<Integer> cir) {
		int min = Tweaks.Fire.delayMin();
		int max = Tweaks.Fire.delayMax();

		cir.setReturnValue(Rnd.nextInt(rand, min, max));
	}
}
