package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.delayMultiplier;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.TickPriority;;

@Mixin(ServerTickScheduler.class)
public abstract class ServerTickSchedulerMixin<T> {
	
	@Shadow ServerWorld world;
	@Shadow Predicate<T> invalidObjPredicate;
	
	@Shadow abstract void addScheduledTick(ScheduledTick<T> scheduledTick);
	
	// Whenever a new tick is scheduled, we need to multiply the delay
	// by the delay multiplier, then schedule the tick with the new delay.
	@Inject(method = "schedule", at = @At(value = "HEAD"), cancellable = true)
	private void onSchedule(BlockPos pos, T object, int delay, TickPriority priority, CallbackInfo ci) {
		if (!this.invalidObjPredicate.test(object)) {
			this.addScheduledTick(new ScheduledTick<T>(pos, object, delayMultiplier.get() * delay + this.world.getTime(), priority));
	   	}
		ci.cancel();
	}
}
