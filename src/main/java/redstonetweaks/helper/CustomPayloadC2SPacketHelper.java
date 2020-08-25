package redstonetweaks.helper;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface CustomPayloadC2SPacketHelper {
	
	public Identifier getChannel();
	
	public PacketByteBuf getData();
	
}
