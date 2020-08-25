package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

import redstonetweaks.helper.CustomPayloadC2SPacketHelper;

@Mixin(CustomPayloadC2SPacket.class)
public class CustomPayloadC2SPacketMixin implements CustomPayloadC2SPacketHelper {
	
	@Shadow private Identifier channel;
	@Shadow private PacketByteBuf data;
	
	@Override
	public Identifier getChannel() {
		return channel;
	}

	@Override
	public PacketByteBuf getData() {
		return new PacketByteBuf(data);
	}
}
