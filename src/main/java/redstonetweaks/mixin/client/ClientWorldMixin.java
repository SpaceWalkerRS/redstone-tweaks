package redstonetweaks.mixin.client;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.interfaces.RTIClientWorld;
import redstonetweaks.interfaces.RTIWorld;
import redstonetweaks.setting.Tweaks;
import redstonetweaks.world.client.ClientNeighborUpdateScheduler;
import redstonetweaks.world.client.ClientUnfinishedEventScheduler;
import redstonetweaks.world.client.ClientWorldTickHandler;
import redstonetweaks.world.common.WorldTickHandler;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin implements RTIWorld, RTIClientWorld {
	
	@Shadow @Final private MinecraftClient client;
	
	private ClientNeighborUpdateScheduler neighborUpdateScheduler;
	private ClientUnfinishedEventScheduler unfinishedEventScheduler;
	
	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void onInitInjectAtReturn(ClientPlayNetworkHandler clientPlayNetworkHandler, ClientWorld.Properties properties, RegistryKey<World> registryKey, DimensionType dimensionType, int i, Supplier<Profiler> supplier, WorldRenderer worldRenderer, boolean bl, long l, CallbackInfo ci) {
		neighborUpdateScheduler = new ClientNeighborUpdateScheduler();
		unfinishedEventScheduler = new ClientUnfinishedEventScheduler((ClientWorld)(Object)this);
	}
	
	@Redirect(method = "tickEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;tickBlockEntities()V"))
	private void onTickEntitiesRedirectTickBlockEntities(ClientWorld world) {
		if (tickWorldsNormally()) {
			world.tickBlockEntities();
		}
	}
	
	@Override
	public WorldTickHandler getWorldTickHandler() {
		return ((RTIMinecraftClient)client).getWorldTickHandler();
	}
	
	@Override
	public ClientNeighborUpdateScheduler getNeighborUpdateScheduler() {
		return neighborUpdateScheduler;
	}

	@Override
	public ClientUnfinishedEventScheduler getUnfinishedEventScheduler() {
		return unfinishedEventScheduler;
	}
	
	@Override
	public boolean tickWorldsNormally() {
		ClientWorldTickHandler worldTickHandler = ((RTIMinecraftClient)client).getWorldTickHandler();
		return worldTickHandler.doWorldTicks() && !(worldTickHandler.tickInProgress() || Tweaks.Global.SHOW_PROCESSING_ORDER.get() > 0);
	}
	
	@Override
	public boolean updateNeighborsNormally() {
		boolean hasScheduledNeighborUpdates = getNeighborUpdateScheduler().hasScheduledNeighborUpdates();
		return tickWorldsNormally() || !(hasScheduledNeighborUpdates || Tweaks.Global.SHOW_NEIGHBOR_UPDATES.get());
	}
}
