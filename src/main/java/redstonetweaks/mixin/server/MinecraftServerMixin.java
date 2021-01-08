package redstonetweaks.mixin.server;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import redstonetweaks.mixinterfaces.RTIMinecraftServer;
import redstonetweaks.packet.ServerPacketHandler;
import redstonetweaks.server.ServerInfo;
import redstonetweaks.setting.ServerSettingsManager;
import redstonetweaks.setting.preset.ServerPresetsManager;
import redstonetweaks.world.server.ServerWorldTickHandler;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements RTIMinecraftServer {
	
	@Shadow private int ticks;
	
	private ServerPacketHandler packetHandler;
	private ServerSettingsManager settingsManager;
	private ServerPresetsManager presetsManager;
	private ServerWorldTickHandler worldTickHandler;
	
	@Shadow public abstract Iterable<ServerWorld> getWorlds();
	
	@Inject(method = "startServer", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "RETURN"))
	private static <S extends MinecraftServer> void onStartServerInjectAtReturn(Function<Thread, S> serverFactory, CallbackInfoReturnable<MinecraftServer> cir,
			AtomicReference<S> atomicReference, Thread thread, S server) 
	{
		((RTIMinecraftServer)server).getSettingsManager().onStartUp();
		((RTIMinecraftServer)server).getPresetsManager().onStartUp();
	}
	
	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void onInitInjectAtReturn(CallbackInfo ci) {
		ServerInfo.init();
		
		packetHandler = new ServerPacketHandler((MinecraftServer)(Object)this);
		settingsManager = new ServerSettingsManager((MinecraftServer)(Object)this);
		presetsManager = new ServerPresetsManager((MinecraftServer)(Object)this);
	}
	
	@Inject(method = "loadWorld", at = @At(value = "RETURN"))
	private void onLoadWorld(CallbackInfo ci) {
		worldTickHandler = new ServerWorldTickHandler((MinecraftServer)(Object)this);
	}
	
	@Inject(method = "shutdown", at = @At(value = "RETURN"))
	private void onShutdown(CallbackInfo ci) {
		settingsManager.onShutdown();
		presetsManager.onShutdown();
	}
	
	@Redirect(method = "tickWorlds", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z"))
	private boolean onTickWorldsRedirectHasNext(Iterator<ServerWorld> iterator, BooleanSupplier shouldKeepTicking) {
		worldTickHandler.tick(shouldKeepTicking);
		
		return false;
	}
	
	@Override
	public ServerPacketHandler getPacketHandler() {
		return packetHandler;
	}
	
	@Override
	public ServerSettingsManager getSettingsManager() {
		return settingsManager;
	}
	
	@Override
	public ServerPresetsManager getPresetsManager() {
		return presetsManager;
	}
	
	@Override
	public ServerWorldTickHandler getWorldTickHandler() {
		return worldTickHandler;
	}
}
