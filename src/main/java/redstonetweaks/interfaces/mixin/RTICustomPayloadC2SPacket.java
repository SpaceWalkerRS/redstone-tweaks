package redstonetweaks.interfaces.mixin;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface RTICustomPayloadC2SPacket {
	
	public Identifier getPacketChannel();
	
	public PacketByteBuf getPacketData();
	
}
