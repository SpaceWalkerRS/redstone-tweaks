package redstonetweaks.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;

import redstonetweaks.helper.MinecraftClientHelper;
import redstonetweaks.packet.ClientPacketHandler;
import redstonetweaks.setting.ClientSettingsManager;
import redstonetweaks.world.client.ClientWorldHandler;
import redstonetweaks.world.client.NeighborUpdateVisualizer;
import redstonetweaks.world.client.TickInfoLabelRenderer;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements MinecraftClientHelper {
	
	@Shadow public ClientWorld world;
	
	private ClientSettingsManager settingsManager;
	private NeighborUpdateVisualizer neighborUpdateVisualizer;
	private ClientPacketHandler packetHandler;
	private ClientWorldHandler worldHandler;
	private TickInfoLabelRenderer tickInfoLabelRenderer;
	
	@Shadow public abstract boolean isIntegratedServerRunning();
	
	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void onInitInjectAtReturn(RunArgs args, CallbackInfo ci) {
		settingsManager = new ClientSettingsManager();
		neighborUpdateVisualizer = new NeighborUpdateVisualizer((MinecraftClient)(Object)this);
		packetHandler = new ClientPacketHandler((MinecraftClient)(Object)this);
		worldHandler = new ClientWorldHandler((MinecraftClient)(Object)this);
		tickInfoLabelRenderer = new TickInfoLabelRenderer((MinecraftClient)(Object)this);
	}
	
	@Inject(method = "joinWorld", at = @At(value = "RETURN"))
	private void onJoinWorldInjectAtReturn(CallbackInfo ci) {
		worldHandler.setCurrentWorld(world);
	}
	
	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At(value = "RETURN"))
	private void onDisconnect(Screen screen, CallbackInfo ci) {
		worldHandler.setCurrentWorld(null);
		settingsManager.resetSettings();
	}
	
	@Override
	public ClientSettingsManager getSettingsManager() {
		return settingsManager;
	}
	
	@Override
	public NeighborUpdateVisualizer getNeighborUpdateVisualizer() {
		return neighborUpdateVisualizer;
	}

	@Override
	public ClientPacketHandler getPacketHandler() {
		return packetHandler;
	}

	@Override
	public ClientWorldHandler getWorldHandler() {
		return worldHandler;
	}
	
	@Override
	public TickInfoLabelRenderer getTickInfoLabelRenderer() {
		return tickInfoLabelRenderer;
	}
}
