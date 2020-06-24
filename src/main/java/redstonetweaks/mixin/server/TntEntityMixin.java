package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.tntDelay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.entity.TntEntity;;

@Mixin(TntEntity.class)
public class TntEntityMixin {
	
	@ModifyConstant(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", constant = @Constant(intValue = 80))
	private int init2FuseTime(int oldFuseTime) {
		return tntDelay.get();
	}
	
	@ModifyConstant(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/entity/LivingEntity;)V", constant = @Constant(intValue = 80))
	private int init5FuseTime(int oldFuseTime) {
		return tntDelay.get();
	}
	
	@ModifyConstant(method = "initDataTracker", constant = @Constant(intValue = 80))
	private int initDataTrackerFuseTime(int oldFuseTime) {
		return tntDelay.get();
	}
}
