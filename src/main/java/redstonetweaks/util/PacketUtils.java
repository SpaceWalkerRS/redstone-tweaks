package redstonetweaks.util;

import net.minecraft.network.PacketByteBuf;

import redstonetweaks.RedstoneTweaksVersion;
import redstonetweaks.block.capacitor.CapacitorBehavior;
import redstonetweaks.world.common.AbstractNeighborUpdate;
import redstonetweaks.world.common.UpdateOrder;
import redstonetweaks.world.common.WorldTickOptions;

public class PacketUtils {
	
	public static final int MAX_STRING_LENGTH = 32767;
	
	public static void writeRedstoneTweaksVersion(PacketByteBuf buffer, RedstoneTweaksVersion version) {
		buffer.writeByte(version.type.getIndex());
		
		buffer.writeInt(version.major);
		buffer.writeInt(version.minor);
		buffer.writeInt(version.patch);
		
		buffer.writeInt(version.snapshot);
	}
	
	public static RedstoneTweaksVersion readRedstoneTweaksVersion(PacketByteBuf buffer) {
		RedstoneTweaksVersion.Type type = RedstoneTweaksVersion.Type.fromIndex(buffer.readByte());
		
		int major = buffer.readInt();
		int minor = buffer.readInt();
		int patch = buffer.readInt();
		
		int snapshot = buffer.readInt();
		
		return RedstoneTweaksVersion.create(type, major, minor, patch, snapshot);
	}
	
	public static void writeUpdateOrder(PacketByteBuf buffer, UpdateOrder order) {
		order.encode(buffer);
	}
	
	public static UpdateOrder readUpdateOrder(PacketByteBuf buffer) {
		Directionality directionality = Directionality.fromIndex(buffer.readByte());
		AbstractNeighborUpdate.Mode defaultMode = AbstractNeighborUpdate.Mode.fromIndex(buffer.readByte());
		boolean forceDefaultMode = buffer.readBoolean();
		
		UpdateOrder order = new UpdateOrder(directionality, UpdateOrder.NotifierOrder.SEQUENTIAL, defaultMode, forceDefaultMode);
		
		order.decode(buffer);
		
		return order;
	}
	
	public static void writeAbstractNeighborUpdate(PacketByteBuf buffer, AbstractNeighborUpdate update) {
		update.encode(buffer);
	}
	
	public static AbstractNeighborUpdate readAbstractNeighborUpdate(PacketByteBuf buffer) {
		AbstractNeighborUpdate.Mode mode = AbstractNeighborUpdate.Mode.fromIndex(buffer.readByte());
		RelativePos notifierPos = RelativePos.fromIndex(buffer.readByte());
		RelativePos updatePos = RelativePos.fromIndex(buffer.readByte());
		
		return new AbstractNeighborUpdate(mode, notifierPos, updatePos);
	}
	
	public static void writeWorldTickOptions(PacketByteBuf buffer, WorldTickOptions options) {
		options.encode(buffer);
	}
	
	public static WorldTickOptions readWorldTickOptions(PacketByteBuf buffer) {
		WorldTickOptions options = new WorldTickOptions();
		
		options.decode(buffer);
		
		return options;
	}
	
	public static void writeCapacitorBehavior(PacketByteBuf buffer, CapacitorBehavior behavior) {
		behavior.encode(buffer);
	}
	
	public static CapacitorBehavior readCapacitorBehavior(PacketByteBuf buffer) {
		CapacitorBehavior behavior = new CapacitorBehavior();
		
		behavior.decode(buffer);
		
		return behavior;
	}
}
