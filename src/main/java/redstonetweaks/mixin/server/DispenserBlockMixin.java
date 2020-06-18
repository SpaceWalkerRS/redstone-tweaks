package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstonetweaks.setting.Settings;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 4))
	private int getDelay(int oldDelay) {
		if ((Block)(Object)this == Blocks.DISPENSER) {
			return (int)Settings.dispenserDelay.get();
		}
		return (int)Settings.dropperDelay.get();
	}
	
	// Both the dispenser and the dropper are affected by Quasi-Connectivity
	// so the Quasi-Connectivity settings and the randomizeQuasiConnectivity setting
	// apply. 
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean neighborUpdateRedirectIsReceivingRedstonePower(World world, BlockPos posUp) {
		BlockPos pos = posUp.down();
		if ((boolean)Settings.randomizeQuasiConnectivity.get() ? (new Random()).nextBoolean() : (boolean)Settings.quasiConnectivityDown.get()) {
			return world.isReceivingRedstonePower(pos.down());
		}
		if ((boolean)Settings.randomizeQuasiConnectivity.get() ? (new Random()).nextBoolean() : (boolean)Settings.quasiConnectivityEast.get()) {
			return world.isReceivingRedstonePower(pos.east());
		}
		if ((boolean)Settings.randomizeQuasiConnectivity.get() ? (new Random()).nextBoolean() : (boolean)Settings.quasiConnectivityNorth.get()) {
			return world.isReceivingRedstonePower(pos.north());
		}
		if ((boolean)Settings.randomizeQuasiConnectivity.get() ? (new Random()).nextBoolean() : (boolean)Settings.quasiConnectivitySouth.get()) {
			return world.isReceivingRedstonePower(pos.south());
		}
		if ((boolean)Settings.randomizeQuasiConnectivity.get() ? (new Random()).nextBoolean() : (boolean)Settings.quasiConnectivityUp.get()) {
			return world.isReceivingRedstonePower(pos.up());
		}
		if ((boolean)Settings.randomizeQuasiConnectivity.get() ? (new Random()).nextBoolean() : (boolean)Settings.quasiConnectivityWest.get()) {
			return world.isReceivingRedstonePower(pos.west());
		}
		return false;
	}
}
