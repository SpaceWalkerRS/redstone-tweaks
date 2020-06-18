package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;

import redstonetweaks.setting.Settings;

@Mixin(ServerTickScheduler.class)
public class ServerTickSchedulerMixin<T> {
	
	@Shadow ServerWorld world;
	
	// When creating a new scheduled tick, we modify the time argument.
	// By subtracting the current world time from the scheduled tick time
	// we can infer the delay. We then multiply this by the delayMultiplier
	// and add the current world time again.
	@ModifyArg(method = "schedule", index = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/ScheduledTick;<init>"))
	public long scheduledTickTimeArg(long oldScheduledTime) {
		long worldTime = this.world.getTime();
		return (long)(int)Settings.delayMultiplier.get() * (oldScheduledTime - worldTime) + worldTime;
	}
}
