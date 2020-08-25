package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.TargetBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;

@Mixin(TargetBlock.class)
public class TargetBlockMixin {
	
	private static boolean persistentProjectile;
	
	@ModifyConstant(method = "trigger", constant = @Constant(intValue = 20))
	private static int onTriggerPersistentProjectileDelay(int oldValue) {
		persistentProjectile = true;
		return TARGET_BLOCK.get(PERSISTENT_PROJECTILE_DELAY);
	}
	
	@ModifyConstant(method = "trigger", constant = @Constant(intValue = 8))
	private static int onTriggerDefaultDelay(int oldValue) {
		persistentProjectile = false;
		return TARGET_BLOCK.get(DELAY);
	}
	
	@Redirect(method = "setPower", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private static <T> void onSetPowerRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos, T object, int delay) {
		TickPriority priority = persistentProjectile ? TARGET_BLOCK.get(PERSISTENT_PROJECTILE_TICK_PRIORITY) : TARGET_BLOCK.get(TICK_PRIORITY);
		tickScheduler.schedule(pos, object, delay, priority);
	}
}
