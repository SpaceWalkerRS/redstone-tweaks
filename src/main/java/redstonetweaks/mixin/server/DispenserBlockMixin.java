package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.dispenserDelay;
import static redstonetweaks.setting.Settings.quasiConnectivityDown;
import static redstonetweaks.setting.Settings.quasiConnectivityEast;
import static redstonetweaks.setting.Settings.quasiConnectivityNorth;
import static redstonetweaks.setting.Settings.quasiConnectivitySouth;
import static redstonetweaks.setting.Settings.quasiConnectivityUp;
import static redstonetweaks.setting.Settings.quasiConnectivityWest;
import static redstonetweaks.setting.Settings.randomizeQuasiConnectivity;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.DispenserBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 4))
	private int getDispenserDelay(int oldDelay) {
		return dispenserDelay.get();
	}
	
	// Both the dispenser and the dropper are affected by Quasi-Connectivity
	// so the Quasi-Connectivity settings and the randomizeQuasiConnectivity setting
	// apply. 
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean neighborUpdateRedirectIsReceivingRedstonePower(World world, BlockPos posUp) {
		BlockPos pos = posUp.down();
		boolean randQC = randomizeQuasiConnectivity.get();
		if (randQC ? (new Random()).nextBoolean() : quasiConnectivityDown.get()) {
			return world.isReceivingRedstonePower(pos.down());
		}
		if (randQC ? (new Random()).nextBoolean() : quasiConnectivityEast.get()) {
			return world.isReceivingRedstonePower(pos.east());
		}
		if (randQC ? (new Random()).nextBoolean() : quasiConnectivityNorth.get()) {
			return world.isReceivingRedstonePower(pos.north());
		}
		if (randQC ? (new Random()).nextBoolean() : quasiConnectivitySouth.get()) {
			return world.isReceivingRedstonePower(pos.south());
		}
		if (randQC ? (new Random()).nextBoolean() : quasiConnectivityUp.get()) {
			return world.isReceivingRedstonePower(pos.up());
		}
		if (randQC ? (new Random()).nextBoolean() : quasiConnectivityWest.get()) {
			return world.isReceivingRedstonePower(pos.west());
		}
		return false;
	}
}
