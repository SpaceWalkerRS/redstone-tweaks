package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.poweredRailLimit;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.PoweredRailBlock;

@Mixin(PoweredRailBlock.class)
public class PoweredRailBlockMixin {
	
	@ModifyConstant(method = "isPoweredByOtherRails(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;ZI)Z", constant = @Constant(intValue = 8))
	private int getPoweredRailLimit(int oldValue) {
		return poweredRailLimit.get() - 1;
	}
}
