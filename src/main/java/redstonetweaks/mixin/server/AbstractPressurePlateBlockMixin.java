package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

import redstonetweaks.setting.Settings;

@Mixin(AbstractPressurePlateBlock.class)
public abstract class AbstractPressurePlateBlockMixin {
	
	@Shadow protected abstract void updatePlateState(World world, BlockPos pos, BlockState blockState, int rsOut);
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 20))
	private int getPressurePlateDelay(int oldDelay) {
		return (int)Settings.pressurePlateDelay.get();
	}
	
	// When the pressure plate is ticked, it should call updatePlateState
	// regardless of its current redstone output, in case the plate
	// has activation delay and the plate is ticked to power on. 
	// To do that the call to getRedstoneOutput is redirected and
	// a value of 1 is returned.
	@Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractPressurePlateBlock;getRedstoneOutput(Lnet/minecraft/block/BlockState;)I"))
	public int scheduledTickRedirectGetRedstoneOutput(AbstractPressurePlateBlock pressurePlate, BlockState state) {
		return 1;
	}
	
	@Redirect(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractPressurePlateBlock;updatePlateState(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)V"))
	public void onEntityCollisionRedirectUpdatePlateState(AbstractPressurePlateBlock pressurePlate, World world, BlockPos pos, BlockState state, int i) {
		int delay = (Block)(Object) this instanceof PressurePlateBlock ? (int)Settings.pressurePlateOnDelay.get() : (int)Settings.weightedPressurePlateOnDelay.get();
		if (delay > 0) {
			world.getBlockTickScheduler().schedule(pos, (Block)(Object) this, delay, TickPriority.EXTREMELY_HIGH);
		} else {
			this.updatePlateState(world, pos, state, i);
		}
	}
}
