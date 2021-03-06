package redstonetweaks.mixin.server;

import java.util.function.Function;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.world.TickPriority;

import redstonetweaks.interfaces.mixin.RTISettings;

@Mixin(Settings.class)
public class SettingsMixin implements RTISettings {
	
	private Function<Integer, Integer> delayOverride;
	private Function<TickPriority, TickPriority> tickPriorityOverride;
	private Function<Integer, Integer> weakPowerOverride;
	private Function<Integer, Integer> strongPowerOverride;
	private Supplier<Boolean> forceMicroTickMode;
	
	@Inject(
			method = "<init>(Lnet/minecraft/block/Material;Ljava/util/function/Function;)V",
			at = @At(
					value = "RETURN"
			)
	)
	private void onInitInjectAtReturn(Material material, Function<BlockState, MaterialColor> materialColorFactory, CallbackInfo ci) {
		delayOverride = delay -> delay;
		tickPriorityOverride = tickPriority -> tickPriority;
		weakPowerOverride = power -> power;
		strongPowerOverride = power -> power;
		forceMicroTickMode = () -> false;
	}
	
	@Override
	public Settings delayOverride(Function<Integer, Integer> delayOverride) {
		this.delayOverride = delayOverride;
		return (Settings)(Object)this;
	}
	
	@Override
	public Function<Integer, Integer> getDelayOverride() {
		return delayOverride;
	}
	
	@Override
	public Settings tickPriorityOverride(Function<TickPriority, TickPriority> tickPriorityOverride) {
		this.tickPriorityOverride = tickPriorityOverride;
		return (Settings)(Object)this;
	}
	
	@Override
	public Function<TickPriority, TickPriority> getTickPriorityOverride() {
		return tickPriorityOverride;
	}
	
	@Override
	public Settings weakPowerOverride(Function<Integer, Integer> weakPowerOverride) {
		this.weakPowerOverride = weakPowerOverride;
		return (Settings)(Object)this;
	}
	
	@Override
	public Function<Integer, Integer> getWeakPowerOverride() {
		return weakPowerOverride;
	}
	
	@Override
	public Settings hardStrongOverride(Function<Integer, Integer> strongPowerOverride) {
		this.strongPowerOverride = strongPowerOverride;
		return (Settings)(Object)this;
	}
	
	@Override
	public Function<Integer, Integer> getStrongPowerOverride() {
		return strongPowerOverride;
	}
	
	@Override
	public Settings forceMicroTickMode(Supplier<Boolean> forceMicroTickMode) {
		this.forceMicroTickMode = forceMicroTickMode;
		return (Settings)(Object)this;
	}
	
	@Override
	public Supplier<Boolean> getForceMicroTickMode() {
		return forceMicroTickMode;
	}
}
