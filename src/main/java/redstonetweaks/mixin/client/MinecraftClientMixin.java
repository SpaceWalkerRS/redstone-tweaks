package redstonetweaks.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;

import redstonetweaks.client.PermissionManager;
import redstonetweaks.gui.RTListWidget;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.gui.setting.EditSettingsListWidget;
import redstonetweaks.hotkeys.HotkeysManager;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.listeners.Listeners;
import redstonetweaks.packet.ClientPacketHandler;
import redstonetweaks.server.ServerInfo;
import redstonetweaks.setting.ClientSettingsManager;
import redstonetweaks.setting.preset.ClientPresetsManager;
import redstonetweaks.world.client.ClientWorldTickHandler;
import redstonetweaks.world.client.NeighborUpdateVisualizer;
import redstonetweaks.world.client.TickInfoLabelRenderer;
import redstonetweaks.world.server.ServerWorldTickHandler;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements RTIMinecraftClient {
	
	@Shadow public ClientWorld world;
	@Shadow private static int currentFps;
	
	private ClientPacketHandler packetHandler;
	private ClientSettingsManager settingsManager;
	private ClientPresetsManager presetsManager;
	private HotkeysManager hotkeysManager;
	private ClientWorldTickHandler worldTickHandler;
	private NeighborUpdateVisualizer neighborUpdateVisualizer;
	private TickInfoLabelRenderer tickInfoLabelRenderer;
	
	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void onInitInjectAtReturn(RunArgs args, CallbackInfo ci) {
		PermissionManager.init((MinecraftClient)(Object)this);
		packetHandler = new ClientPacketHandler((MinecraftClient)(Object)this);
		settingsManager = new ClientSettingsManager((MinecraftClient)(Object)this);
		presetsManager = new ClientPresetsManager((MinecraftClient)(Object)this);
		hotkeysManager = new HotkeysManager((MinecraftClient)(Object)this);
		worldTickHandler = new ClientWorldTickHandler((MinecraftClient)(Object)this);
		neighborUpdateVisualizer = new NeighborUpdateVisualizer((MinecraftClient)(Object)this);
		tickInfoLabelRenderer = new TickInfoLabelRenderer((MinecraftClient)(Object)this);
	}
	
	@Inject(method = "joinWorld", at = @At(value = "RETURN"))
	private void onJoinWorldInjectAtReturn(CallbackInfo ci) {
		worldTickHandler.setCurrentWorld(world);
	}
	
	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At(value = "RETURN"))
	private void onDisconnect(Screen screen, CallbackInfo ci) {
		Listeners.clear();
		ServerInfo.clear();
		worldTickHandler.onDisconnect();
		presetsManager.onDisconnect();
		settingsManager.onDisconnect();
		neighborUpdateVisualizer.onDisconnect();
		tickInfoLabelRenderer.onDisconnect();
		RTMenuScreen.clearLastSearchQueries();
		RTMenuScreen.resetLastOpenedTabIndex();
		RTListWidget.clearSavedScrollAmounts();
		EditSettingsListWidget.resetLastModes();
	}
	
	@Inject(method = "stop", at = @At(value = "HEAD"))
	private void onStopInjectAtHead(CallbackInfo ci) {
		hotkeysManager.saveHotkeys();
	}
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isIntegratedServerRunning()Z"))
	private boolean onRenderRedirectIsIntegratedServerRunning(MinecraftClient client) {
		if (client.isIntegratedServerRunning()) {
			ServerWorldTickHandler worldTickHandler = ((RTIMinecraftServer)client.getServer()).getWorldTickHandler();
			
			if (worldTickHandler != null) {
				return !worldTickHandler.tickInProgress();
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public ClientPacketHandler getPacketHandler() {
		return packetHandler;
	}
	
	@Override
	public ClientSettingsManager getSettingsManager() {
		return settingsManager;
	}
	
	@Override
	public ClientPresetsManager getPresetsManager() {
		return presetsManager;
	}
	
	@Override
	public HotkeysManager getHotkeysManager() {
		return hotkeysManager;
	}
	
	@Override
	public ClientWorldTickHandler getWorldTickHandler() {
		return worldTickHandler;
	}
	
	@Override
	public NeighborUpdateVisualizer getNeighborUpdateVisualizer() {
		return neighborUpdateVisualizer;
	}
	
	@Override
	public TickInfoLabelRenderer getTickInfoLabelRenderer() {
		return tickInfoLabelRenderer;
	}
	
	@Override
	public int getCurrentFps() {
		return currentFps;
	}
}
