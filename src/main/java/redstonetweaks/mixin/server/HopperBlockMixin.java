package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

import redstonetweaks.helper.WorldHelper;
import redstonetweaks.setting.types.DirectionalBooleanSetting;

@Mixin(HopperBlock.class)
public abstract class HopperBlockMixin extends Block {
	
	public HopperBlockMixin(Settings settings) {
		super(settings);
	}

	@Shadow protected abstract void updateEnabled(World world, BlockPos pos, BlockState state);
	
	@Redirect(method = "updateEnabled", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onUpdateEnabledRedirectGetReceivedPower(World world1, BlockPos blockPos, World world, BlockPos pos, BlockState state) {
		return isReceivingPower(world, pos, state, false);
	}
	
	@Redirect(method = "updateEnabled", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private boolean onUpdateEnabledRedirectSetBlockState(World world, BlockPos pos, BlockState state, int flags) {
		// We invert the powered property because this state is the new state
		boolean enabled = !state.get(Properties.ENABLED);
		int delay = getDelay(enabled);
		if (delay == 0) {
			world.setBlockState(pos, state, flags);
		} else {
			TickPriority priority = getTickPriority(enabled);
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, priority);
		}
		return true;
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		boolean enabled = state.get(Properties.ENABLED);
		boolean lazy = isLazy(enabled);
		boolean isReceivingPower = isReceivingPower(world, pos, state, true);
		boolean shouldBeEnabled = lazy ? !enabled : !isReceivingPower;
		
		if (enabled != shouldBeEnabled) {
			BlockState newState = state.with(Properties.ENABLED, shouldBeEnabled);
			world.setBlockState(pos, newState, 6);
			if (shouldBeEnabled == isReceivingPower) {
				updateEnabled(world, pos, newState);
			}
		}
	}
	
	private boolean isReceivingPower(World world, BlockPos pos, BlockState state, boolean onScheduledTick) {
		return world.isReceivingRedstonePower(pos) || WorldHelper.isQCPowered(world, pos, state, onScheduledTick, getQC(), randQC());
	}
	
	private DirectionalBooleanSetting getQC() {
		return redstonetweaks.setting.Settings.Hopper.QC;
	}
	
	private boolean randQC() {
		return redstonetweaks.setting.Settings.Hopper.RANDOMIZE_QC.get();
	}
	
	private int getDelay(boolean enabled) {
		return enabled ? redstonetweaks.setting.Settings.Hopper.DELAY_RISING_EDGE.get() : redstonetweaks.setting.Settings.Hopper.DELAY_FALLING_EDGE.get();
	}
	
	private boolean isLazy(boolean enabled) {
		return enabled ? redstonetweaks.setting.Settings.Hopper.LAZY_RISING_EDGE.get() : redstonetweaks.setting.Settings.Hopper.LAZY_FALLING_EDGE.get();
	}
	
	private TickPriority getTickPriority(boolean enabled) {
		return enabled ? redstonetweaks.setting.Settings.Hopper.TICK_PRIORITY_RISING_EDGE.get() : redstonetweaks.setting.Settings.Hopper.TICK_PRIORITY_FALLING_EDGE.get();
	}
}
