package redstonetweaks.mixin.server;

import java.util.Iterator;
import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import redstonetweaks.helper.MinecraftServerHelper;
import redstonetweaks.packet.ServerPacketHandler;
import redstonetweaks.setting.ServerSettingsManager;
import redstonetweaks.world.server.ServerWorldTickHandler;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerHelper {
	
	@Shadow private int ticks;
	
	private ServerSettingsManager settingsManager;
	private ServerPacketHandler packetHandler;
	private ServerWorldTickHandler worldTickHandler;
	
	@Shadow public abstract Iterable<ServerWorld> getWorlds();
	
	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void onInitInjectAtReturn(CallbackInfo ci) {
		settingsManager = new ServerSettingsManager((MinecraftServer)(Object)this);
		packetHandler = new ServerPacketHandler((MinecraftServer)(Object)this);
	}
	
	@Inject(method = "loadWorld", at = @At(value = "RETURN"))
	private void onLoadWorld(CallbackInfo ci) {
		worldTickHandler = new ServerWorldTickHandler((MinecraftServer)(Object)this);
	}
	
	@Inject(method = "shutdown", at = @At(value = "RETURN"))
	private void onShutdown(CallbackInfo ci) {
		settingsManager.onShutdown();
	}
	
	@Redirect(method = "tickWorlds", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z"))
	private boolean onTickWorldsRedirectHasNext(Iterator<ServerWorld> iterator, BooleanSupplier shouldKeepTicking) {
		worldTickHandler.tick(shouldKeepTicking);
		
		return false;
	}
	
	public ServerSettingsManager getSettingsManager() {
		return settingsManager;
	}
	
	@Override
	public ServerPacketHandler getPacketHandler() {
		return packetHandler;
	}
	
	@Override
	public ServerWorldTickHandler getWorldTickHandler() {
		return worldTickHandler;
	}
}
