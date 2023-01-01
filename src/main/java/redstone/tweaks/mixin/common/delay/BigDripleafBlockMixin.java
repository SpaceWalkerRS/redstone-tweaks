package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BigDripleafBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Tilt;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.FluidOverrides;

@Mixin(BigDripleafBlock.class)
public class BigDripleafBlockMixin {

	@Shadow private static Object2IntMap<Tilt> DELAY_UNTIL_NEXT_TILT_STATE;

	@Redirect(
		method = "updateShape",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/LevelAccessor;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;I)V"
		)
	)
	private void rtTweakWaterTickPriority(LevelAccessor _level, BlockPos _pos, Fluid fluid, int delay, BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		FluidOverrides.scheduleOrDoTick(level, pos, state.getFluidState(), delay, Tweaks.Water.tickPriority());
	}

	@Inject(
		method = "setTiltAndScheduleTick",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/level/block/BigDripleafBlock;DELAY_UNTIL_NEXT_TILT_STATE:Lit/unimi/dsi/fastutil/objects/Object2IntMap;"
		)
	)
	private void rtTweakDelay(BlockState state, Level level, BlockPos pos, Tilt tilt, SoundEvent sound, CallbackInfo ci) {
		if (tilt != Tilt.NONE) {
			DELAY_UNTIL_NEXT_TILT_STATE.put(tilt, switch (tilt) {
				case UNSTABLE -> Tweaks.BigDripleaf.delayUnstable();
				case PARTIAL  -> Tweaks.BigDripleaf.delayPartial();
				case FULL     -> Tweaks.BigDripleaf.delayFull();
				default       -> -1;
			});
		}
	}

	@Redirect(
		method = "setTiltAndScheduleTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakDelayAndTickPriority(Level _level, BlockPos _pos, Block block, int delay, BlockState state, Level level, BlockPos pos, Tilt tilt, SoundEvent sound) {
		TickPriority priority = switch (tilt) {
			case UNSTABLE -> Tweaks.BigDripleaf.tickPriorityUnstable();
			case PARTIAL  -> Tweaks.BigDripleaf.tickPriorityPartial();
			case FULL     -> Tweaks.BigDripleaf.tickPriorityFull();
			default       -> TickPriority.NORMAL;
		};

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
	}
}
