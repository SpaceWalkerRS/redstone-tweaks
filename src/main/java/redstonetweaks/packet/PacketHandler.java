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
		
		buffer.writeByte(RedstoneTweaks.PACKET_PROTOCOL.major);
		buffer.writeByte(RedstoneTweaks.PACKET_PROTOCOL.minor);
		buffer.writeByte(RedstoneTweaks.PACKET_PROTOCOL.patch);
		
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
		SETTINGS(2, SettingsPacket.class),
		RESET_SETTING(3, ResetSettingPacket.class),
		RESET_SETTINGS(4, ResetSettingsPacket.class),
		NEIGHBOR_UPDATE_VISUALIZER(5, NeighborUpdateVisualizerPacket.class),
		NEIGHBOR_UPDATE_SCHEDULER(6, NeighborUpdateSchedulerPacket.class),
		WORLD_SYNC(7, WorldSyncPacket.class),
		TASK_SYNC(8, TaskSyncPacket.class),
		UNFINISHED_EVENT(9, UnfinishedEventPacket.class),
		WORLD_TIME_SYNC(10, WorldTimeSyncPacket.class),
		TICK_STATUS(11, TickStatusPacket.class),
		TICK_BLOCK_ENTITY(12, TickBlockEntityPacket.class),
		PLAYER_JOINED_SERVER(13, PlayerJoinedServerPacket.class),
		DO_WORLD_TICKS(14, DoWorldTicksPacket.class),
		TICK_PAUSE(15, TickPausePacket.class),
		SERVER_INFO(16, ServerInfoPacket.class),
		PRESET_PACKET(17, PresetPacket.class);
		
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
