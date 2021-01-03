package redstonetweaks.mixinterfaces;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface RTICustomPayloadC2SPacket {
	
	public Identifier getChannel();
	
	public PacketByteBuf getData();
	
}
