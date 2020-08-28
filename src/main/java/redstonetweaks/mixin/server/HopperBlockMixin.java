package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

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

@Mixin(HopperBlock.class)
public abstract class HopperBlockMixin extends Block {
	
	public HopperBlockMixin(Settings settings) {
		super(settings);
	}

	@Shadow protected abstract void updateEnabled(World world, BlockPos pos, BlockState state);
	
	@Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/HopperBlock;updateEnabled(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void onOnBlockAddedRedirectUpdateEnabled(HopperBlock hopper, World world, BlockPos pos, BlockState state) {
		update(world, pos, state);
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/HopperBlock;updateEnabled(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void onNeighborUpdateRedirectUpdateEnabled(HopperBlock hopper, World world, BlockPos pos, BlockState state) {
		update(world, pos, state);
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		boolean enabled = state.get(Properties.ENABLED);
		boolean lazy = enabled ? HOPPER.get(RISING_LAZY) : HOPPER.get(FALLING_LAZY);
		boolean isReceivingPower = world.isReceivingRedstonePower(pos);
		boolean shouldBeEnabled = lazy ? !enabled : !isReceivingPower;
		
		if (enabled != shouldBeEnabled) {
			world.setBlockState(pos, state.with(Properties.ENABLED, shouldBeEnabled), 6);
			if (shouldBeEnabled == isReceivingPower) {
				world.updateNeighbor(pos, state.getBlock(), pos);
			}
		}
	}
	
	private void update(World world, BlockPos pos, BlockState state) {
		boolean enabled = state.get(Properties.ENABLED);
		int delay = enabled ? HOPPER.get(RISING_DELAY) : HOPPER.get(FALLING_DELAY);
		if (delay == 0) {
			updateEnabled(world, pos, state);
		} else if (!world.isClient()) {
			TickPriority priority = enabled ? HOPPER.get(RISING_TICK_PRIORITY) : HOPPER.get(FALLING_TICK_PRIORITY);
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, priority);
		}
	}
}
