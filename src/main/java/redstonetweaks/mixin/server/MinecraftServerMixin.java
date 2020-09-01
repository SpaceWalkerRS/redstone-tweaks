package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import java.util.Iterator;
import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;

import redstonetweaks.helper.MinecraftServerHelper;
import redstonetweaks.helper.ServerChunkManagerHelper;
import redstonetweaks.helper.ServerWorldHelper;
import redstonetweaks.packet.ServerPacketHandler;
import redstonetweaks.setting.ServerSettingsManager;
import redstonetweaks.world.server.ServerTickHandler;
import redstonetweaks.world.server.ServerWorldHandler;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerHelper {
	
	@Shadow private int ticks;
	
	private ServerSettingsManager settingsManager;
	private ServerPacketHandler packetHandler;
	private ServerTickHandler tickHandler;
	private ServerWorldHandler worldHandler;
	private boolean tickedWorldHandler = false;
	
	@Shadow public abstract Iterable<ServerWorld> getWorlds();
	
	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void onInitInjectAtReturn(CallbackInfo ci) {
		settingsManager = new ServerSettingsManager((MinecraftServer)(Object)this);
		packetHandler = new ServerPacketHandler((MinecraftServer)(Object)this);
		tickHandler = new ServerTickHandler((MinecraftServer)(Object)this);
	}
	
	@Inject(method = "loadWorld", at = @At(value = "RETURN"))
	private void onLoadWorld(CallbackInfo ci) {
		worldHandler = new ServerWorldHandler((MinecraftServer)(Object)this);
	}
	
	@Inject(method = "shutdown", at = @At(value = "RETURN"))
	private void onShutdown(CallbackInfo ci) {
		settingsManager.onShutdown();
	}
	
	@Inject(method = "tickWorlds", at = @At(value = "HEAD"))
	private void onTickWorldsInjectAtHead(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		tickedWorldHandler = false;
	}
	
	@Redirect(method = "tickWorlds", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z"))
	private boolean onTickWorldsRedirectHasNext(Iterator<ServerWorld> iterator) {
		if (tickHandler.shouldTick()) {
			if (GLOBAL.get(SHOW_PROCESSING_ORDER) > 0 || worldHandler.isTicking()) {
				return !tickedWorldHandler;
			}
			return iterator.hasNext();
		}
		return false;
	}
	
	@Redirect(method = "tickWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;tick(Ljava/util/function/BooleanSupplier;)V"))
	private void onTickWorldsRedirectServerWorldTick(ServerWorld world, BooleanSupplier shouldKeepTicking) {
		int interval = GLOBAL.get(SHOW_PROCESSING_ORDER);
		
		if (interval > 0 || worldHandler.isTicking()) {
			if (interval == 0 || ticks % interval == 0) {
				worldHandler.tick(shouldKeepTicking);
				broadcastChunkData();
			}
			tickedWorldHandler = true;
		} else {
			world.tick(shouldKeepTicking);
		}
	}
	
	@Inject(method = "tickWorlds", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", shift = Shift.BEFORE))
	private void onTickWorldsInjectBeforeSwapConnection(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		if (BUG_FIXES.get(MC172213)) {
			int interval = GLOBAL.get(SHOW_PROCESSING_ORDER);
			
			if ((interval == 0 && !worldHandler.isTicking())) {
				for (ServerWorld world : getWorlds()) {
					((ServerWorldHelper)world).tickTimeAccess();
				}
			}
		}
	}
	
	public ServerSettingsManager getSettingsManager() {
		return settingsManager;
	}
	
	@Override
	public ServerPacketHandler getPacketHandler() {
		return packetHandler;
	}
	
	@Override
	public ServerTickHandler getTickHandler() {
		return tickHandler;
	}
	
	@Override
	public ServerWorldHandler getWorldHandler() {
		return worldHandler;
	}
	
	private void broadcastChunkData() {
		for (ServerWorld world : getWorlds()) {
			ServerChunkManager chunkManager = world.getChunkManager();
			((ServerChunkManagerHelper)chunkManager).broadcastChunkData();
		}
	}
}
