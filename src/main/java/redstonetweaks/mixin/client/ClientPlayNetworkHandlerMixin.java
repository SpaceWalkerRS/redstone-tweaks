package redstonetweaks.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.math.BlockPos;

import redstonetweaks.helper.BlockEntityHelper;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.packet.AbstractPacketHandler;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	
	@Shadow private MinecraftClient client;
	
	@Inject(method = "onCustomPayload", cancellable = true, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;getChannel()Lnet/minecraft/util/Identifier;"))
	private void onOnCustomPayloadInjectAfterForceMainThread(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		if (AbstractPacketHandler.PACKET_IDENTIFIER.equals(packet.getChannel())) {
			((RTIMinecraftClient)client).getPacketHandler().onPacketReceived(packet.getData());
			
			ci.cancel();
		}
	}
	
	@Inject(method = "onBlockEntityUpdate", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/network/packet/s2c/play/BlockEntityUpdateS2CPacket;getBlockEntityType()I"))
	private void onOnBlockEntityUpdateInjectAfterGetType(BlockEntityUpdateS2CPacket packet, CallbackInfo ci, BlockPos pos, BlockEntity blockEntity) {
		if (BlockEntityHelper.getId(blockEntity.getType()) == packet.getBlockEntityType()) {
			blockEntity.fromTag(client.world.getBlockState(pos), packet.getCompoundTag());
			
			ci.cancel();
		}
	}
}
