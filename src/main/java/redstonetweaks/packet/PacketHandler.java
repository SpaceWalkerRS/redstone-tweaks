package redstonetweaks.packet;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.Unpooled;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import redstonetweaks.RedstoneTweaks;

public abstract class PacketHandler {
	
	public static final Identifier PACKET_IDENTIFIER = new Identifier("redstone_tweaks");
	
	public Packet<?> encodePacket(RedstoneTweaksPacket packet) {
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		
		buffer.writeByte(RedstoneTweaks.MOD_VERSION.major);
		buffer.writeByte(RedstoneTweaks.MOD_VERSION.minor);
		buffer.writeByte(RedstoneTweaks.MOD_VERSION.patch);
		
		PacketType packetType = PacketType.fromPacket(packet);
		if (packetType == PacketType.INVALID) {
			throw new IllegalStateException("Unable to encode packet: " + packet.getClass());
		}
		buffer.writeByte(packetType.getIndex());
		
		packet.encode(buffer);
		return toCustomPayloadPacket(buffer);
	}
	
	public abstract Packet<?> toCustomPayloadPacket(PacketByteBuf buffer);
	
	public RedstoneTweaksPacket decodePacket(PacketByteBuf buffer) throws InstantiationException, IllegalAccessException {
		PacketType type = PacketType.fromIndex(buffer.readByte());
		RedstoneTweaksPacket packet = type.getClazz().newInstance();
		
		packet.decode(buffer);
		return packet;
	}
	
	public abstract void sendPacket(RedstoneTweaksPacket redstoneTweaksPacket);
	
	public abstract void onPacketReceived(PacketByteBuf buffer);
	
	public enum PacketType {
		INVALID(0, null),
		SETTING(1, SettingPacket.class),
		NEIGHBOR_UPDATE_VISUALIZER(2, NeighborUpdateVisualizerPacket.class),
		NEIGHBOR_UPDATE_SCHEDULER(3, NeighborUpdateSchedulerPacket.class),
		WORLD_SYNC(4, WorldSyncPacket.class),
		TASK_SYNC(5, TaskSyncPacket.class),
		UNFINISHED_EVENT(6, UnfinishedEventPacket.class),
		WORLD_TIME_SYNC(7, WorldTimeSyncPacket.class),
		TICK_STATUS(8, TickStatusPacket.class),
		TICK_BLOCK_ENTITY(9, TickBlockEntityPacket.class),
		PLAYER_JOINED_SERVER(10, PlayerJoinedServerPacket.class),
		DO_WORLD_TICKS(11, DoWorldTicksPacket.class);
		
		private static final PacketType[] PACKET_TYPES;
		private static final Map<Class<? extends RedstoneTweaksPacket>, PacketType> PACKET_TO_TYPE;
		
		static {
			PACKET_TYPES = new PacketType[values().length];
			PACKET_TO_TYPE = new HashMap<>();
			
			for (PacketType packetType : values()) {
				PACKET_TYPES[packetType.index] = packetType;
				PACKET_TO_TYPE.put(packetType.clazz, packetType);
			}
		}
		
		private final int index;
		private final Class<? extends RedstoneTweaksPacket> clazz;
		
		PacketType(int index, Class<? extends RedstoneTweaksPacket> clazz) {
			this.index = index;
			this.clazz = clazz;
		}
		
		public static PacketType fromIndex(int index) {
			if (index > 0 && index < PACKET_TYPES.length) {
				return PACKET_TYPES[index];
			} else {
				return INVALID;
			}
		}
		
		public static PacketType fromPacket(RedstoneTweaksPacket packet) {
			PacketType type = PACKET_TO_TYPE.get(packet.getClass());
			return type == null ? INVALID : type;
		}
		
		public int getIndex() {
			return index;
		}
		
		public Class<? extends RedstoneTweaksPacket> getClazz() {
			return clazz;
		}
	}
}
