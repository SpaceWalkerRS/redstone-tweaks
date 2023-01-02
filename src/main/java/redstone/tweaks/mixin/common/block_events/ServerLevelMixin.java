package redstone.tweaks.mixin.common.block_events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.g4mesoft.core.server.GSServerController;
import com.g4mesoft.module.tps.GSTpsModule;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.BlockEventData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;

import redstone.tweaks.Tweaks;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {

	@Shadow private ObjectLinkedOpenHashSet<BlockEventData> blockEvents;
	@Shadow private List<BlockEventData> blockEventsToReschedule;

	private final List<BlockEventData> blockEventList = new ArrayList<>();

	private int successfulBlockEvents;

	private ServerLevelMixin(WritableLevelData data, ResourceKey<Level> key, Holder<DimensionType> dimension, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long seed, int maxChainedNeighborUpdates) {
		super(data, key, dimension, profiler, isClientSide, isDebug, seed, maxChainedNeighborUpdates);
	}

	@Shadow private boolean doBlockEvent(BlockEventData blockEvent) { return false; }

	@Inject(
		method = "blockEvent",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakInstantBlockEvents(BlockPos pos, Block block, int type, int data, CallbackInfo ci) {
		if (Tweaks.Global.instantBlockEvents()) {
			if (doBlockEvent(new BlockEventData(pos, block, type, data))) {
				GSServerController controller = GSServerController.getInstance();
				GSTpsModule tps = controller.getTpsModule();

				PlayerList playerList = getServer().getPlayerList();

				double x = pos.getX();
				double y = pos.getY();
				double z = pos.getZ();
				double range = tps.sBlockEventDistance.getValue();
				ResourceKey<Level> key = dimension();

				Packet<?> packet = new ClientboundBlockEventPacket(pos, block, type, data);
				playerList.broadcast(null, x, y, z, range, key, packet);
			}

			ci.cancel();
		}
	}

	@Redirect(
		method = "blockEvent",
		at = @At(
			value = "INVOKE",
			target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;add(Ljava/lang/Object;)Z"
		)
	)
	private boolean rtAddBlockEvent(ObjectLinkedOpenHashSet<Object> blockEvents, Object blockEvent) {
		boolean added = blockEvents.add(blockEvent);

		if (added && Tweaks.Global.randomizeBlockEvents()) {
			blockEventList.add((BlockEventData)blockEvent);
		}

		return added;
	}

	@Inject(
		method = "runBlockEvents",
		at = @At(
			value = "HEAD"
		)
	)
	private void rtResetSuccessfulBlockEventCounter(CallbackInfo ci) {
		successfulBlockEvents = 0;
	}

	@Redirect(
		method = "runBlockEvents",
		at = @At(
			value = "INVOKE",
			target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;isEmpty()Z"
		)
	)
	private boolean rtTweakBlockEventLimit(ObjectLinkedOpenHashSet<BlockEventData> blockEvents) {
		return blockEvents.isEmpty() || successfulBlockEvents > Tweaks.Global.blockEventLimit();
	}

	@Redirect(
		method = "runBlockEvents",
		at = @At(
			value = "INVOKE",
			target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;removeFirst()Ljava/lang/Object;"
		)
	)
	private Object rtTweakRandomizeBlockEvents(ObjectLinkedOpenHashSet<BlockEventData> blockEvents) {
		if (Tweaks.Global.randomizeBlockEvents()) {
			int randomIndex = getRandom().nextInt(blockEventList.size());
			int lastIndex = blockEventList.size() - 1;

			Collections.swap(blockEventList, randomIndex, lastIndex);

			BlockEventData blockEvent = blockEventList.remove(lastIndex);
			blockEvents.remove(blockEvent);

			return blockEvent;
		} else {
			return blockEvents.removeFirst();
		}
	}

	@Inject(
		method = "runBlockEvents",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/MinecraftServer;getPlayerList()Lnet/minecraft/server/players/PlayerList;"
		)
	)
	private void rtOnSuccessfulBlockEvent(CallbackInfo ci) {
		successfulBlockEvents++;
	}
}
