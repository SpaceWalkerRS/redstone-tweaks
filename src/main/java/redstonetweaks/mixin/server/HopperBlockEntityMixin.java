package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.delayMultiplier;
import static redstonetweaks.setting.Settings.hopperDelay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.HopperBlockEntity;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {
	
	// Since the hopper cooldown varies between 7 and 8 ticks in vanilla,
	// we only allow it to be changed by multiplying it by some value.
	
	@Shadow private int transferCooldown;
	
	@Inject(method = "setCooldown", at = @At(value = "HEAD"), cancellable = true)
	private void onSetCooldown(int oldCooldown, CallbackInfo ci) {
		this.transferCooldown = delayMultiplier.get() * hopperDelay.get() * oldCooldown;
		ci.cancel();
	}
	
	@ModifyConstant(method = "isDisabled", constant = @Constant(intValue = 8))
	private int getHopperDelay(int oldMaxCooldown) {
		return delayMultiplier.get() * hopperDelay.get() * oldMaxCooldown;
	}
}
