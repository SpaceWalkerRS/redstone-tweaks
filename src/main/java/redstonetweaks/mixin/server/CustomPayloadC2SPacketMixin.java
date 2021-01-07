package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import redstonetweaks.mixinterfaces.RTICustomPayloadC2SPacket;

@Mixin(CustomPayloadC2SPacket.class)
public class CustomPayloadC2SPacketMixin implements RTICustomPayloadC2SPacket {
	
	@Shadow private Identifier channel;
	@Shadow private PacketByteBuf data;
	
	@Override
	public Identifier getPacketChannel() {
		return channel;
	}

	@Override
	public PacketByteBuf getPacketData() {
		return new PacketByteBuf(data);
	}
}
