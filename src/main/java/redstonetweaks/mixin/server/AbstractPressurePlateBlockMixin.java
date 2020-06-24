package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.heavyWeightedPressurePlateOnDelay;
import static redstonetweaks.setting.Settings.lightWeightedPressurePlateOnDelay;
import static redstonetweaks.setting.Settings.stonePressurePlateDelay;
import static redstonetweaks.setting.Settings.stonePressurePlateOnDelay;
import static redstonetweaks.setting.Settings.woodenPressurePlateDelay;
import static redstonetweaks.setting.Settings.woodenPressurePlateOnDelay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;

@Mixin(AbstractPressurePlateBlock.class)
public abstract class AbstractPressurePlateBlockMixin {
	
	@Shadow protected abstract void updatePlateState(World world, BlockPos pos, BlockState blockState, int rsOut);
	@Shadow protected abstract int getRedstoneOutput(BlockState state);
	
	// When the pressure plate is ticked, it should call updatePlateState
	// regardless of its current redstone output, in case the plate
	// has activation delay and the plate is ticked to power on. 
	@Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractPressurePlateBlock;getRedstoneOutput(Lnet/minecraft/block/BlockState;)I"))
	private int onScheduledTickRedirectGetRedstoneOutput(AbstractPressurePlateBlock pressurePlate, BlockState state) {
		return 1;
	}
	
	@Redirect(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractPressurePlateBlock;updatePlateState(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)V"))
	public void onEntityCollisionRedirectUpdatePlateState(AbstractPressurePlateBlock pressurePlate, World world, BlockPos pos, BlockState state, int i) {
		int delay;
		if (state.getBlock() instanceof PressurePlateBlock) {
			delay = pressurePlate.getMaterial(state) == Material.WOOD ? woodenPressurePlateOnDelay.get() : stonePressurePlateOnDelay.get();
		} else {
			delay = state.getBlock() == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE ? heavyWeightedPressurePlateOnDelay.get() : lightWeightedPressurePlateOnDelay.get();
		}
		if (delay > 0) {
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, TickPriority.EXTREMELY_HIGH);
		} else {
			this.updatePlateState(world, pos, state, i);
		}
	}
	
	// We cannot just redirect the getTickRate(WorldView) call
	// because the delay depends on what type of pressure plate it is.
	@Redirect(method = "updatePlateState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void updatePlateStateRedirectScheduleTick(TickScheduler<?> tickScheduler, BlockPos pos, T Object, int oldDelay, World world, BlockPos blockPos, BlockState state) {
		int delay;
		if (state.getBlock() instanceof PressurePlateBlock) {
			delay = state.getBlock().getMaterial(state) == Material.WOOD ? woodenPressurePlateDelay.get() : stonePressurePlateDelay.get();
		} else {
			delay = state.getBlock().getTickRate(world);
		}
		world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay);
	}
}
