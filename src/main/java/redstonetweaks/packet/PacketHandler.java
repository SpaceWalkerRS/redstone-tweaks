package redstonetweaks.packet;

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
		
		PacketType packetType = getTypeFromPacket(packet);
		if (packetType == PacketType.INVALID) {
			throw new IllegalStateException("Unable to encode packet: " + packet.getClass());
		}
		buffer.writeByte(packetType.getIndex());
		
		packet.encode(buffer);
		return toCustomPayloadPacket(buffer);
	}
	
	public abstract Packet<?> toCustomPayloadPacket(PacketByteBuf buffer);
	
	public RedstoneTweaksPacket decodePacket(PacketByteBuf buffer) {
		PacketType packetType = PacketType.fromIndex(buffer.readByte());
		RedstoneTweaksPacket packet = getPacketFromType(packetType);
		
		packet.decode(buffer);
		return packet;
	}
	
	public PacketType getTypeFromPacket(RedstoneTweaksPacket packet) {
		if (packet instanceof SettingPacket) {
			return PacketType.SETTING;
		}
		if (packet instanceof NeighborUpdateVisualizerPacket) {
			return PacketType.NEIGHBOR_UPDATE_VISUALIZER;
		}
		if (packet instanceof NeighborUpdateSchedulerPacket) {
			return PacketType.NEIGHBOR_UPDATE_SCHEDULER;
		}
		if (packet instanceof WorldSyncPacket) {
			return PacketType.WORLD_SYNC;
		}
		if (packet instanceof TaskSyncPacket) {
			return PacketType.TASK_SYNC;
		}
		if (packet instanceof UnfinishedEventPacket) {
			return PacketType.UNFINISHED_EVENT;
		}
		if (packet instanceof WorldTimeSyncPacket) {
			return PacketType.WORLD_TIME_SYNC;
		}
		if (packet instanceof TickStatusPacket) {
			return PacketType.TICK_STATUS;
		}
		if (packet instanceof TickBlockEntityPacket) {
			return PacketType.TICK_BLOCK_ENTITY;
		}
		if (packet instanceof PlayerJoinedServerPacket) {
			return PacketType.PLAYER_JOINED_SERVER;
		}
		if (packet instanceof DoWorldTicksPacket) {
			return PacketType.DO_WORLD_TICKS;
		}
		return PacketType.INVALID;
	}
	
	public RedstoneTweaksPacket getPacketFromType(PacketType type) {
		switch (type) {
		case SETTING:
			return new SettingPacket<>();
		case NEIGHBOR_UPDATE_VISUALIZER:
			return new NeighborUpdateVisualizerPacket();
		case NEIGHBOR_UPDATE_SCHEDULER:
			return new NeighborUpdateSchedulerPacket();
		case WORLD_SYNC:
			return new WorldSyncPacket();
		case TASK_SYNC:
			return new TaskSyncPacket();
		case UNFINISHED_EVENT:
			return new UnfinishedEventPacket();
		case WORLD_TIME_SYNC:
			return new WorldTimeSyncPacket();
		case TICK_STATUS:
			return new TickStatusPacket();
		case TICK_BLOCK_ENTITY:
			return new TickBlockEntityPacket();
		case PLAYER_JOINED_SERVER:
			return new PlayerJoinedServerPacket();
		case DO_WORLD_TICKS:
			return new DoWorldTicksPacket();
		default:
			return null;
		}
	}
	
	public abstract void sendPacket(RedstoneTweaksPacket redstoneTweaksPacket);
	
	public abstract void onPacketReceived(PacketByteBuf buffer);
	
	public enum PacketType {
		INVALID(0),
		SETTING(1),
		NEIGHBOR_UPDATE_VISUALIZER(2),
		NEIGHBOR_UPDATE_SCHEDULER(3),
		WORLD_SYNC(4),
		TASK_SYNC(5),
		UNFINISHED_EVENT(6),
		WORLD_TIME_SYNC(7),
		TICK_STATUS(8),
		TICK_BLOCK_ENTITY(9),
		PLAYER_JOINED_SERVER(10),
		DO_WORLD_TICKS(11);
		
		public static final PacketType[] PACKET_TYPES;
		
		static {
			PACKET_TYPES = new PacketType[values().length];
			
			for (PacketType packetType : values()) {
				PACKET_TYPES[packetType.index] = packetType;
			}
		}
		
		private final int index;
		
		PacketType(int index) {
			this.index = index;
		}
		
		public static PacketType fromIndex(int index) {
			if (index > 0 && index < PACKET_TYPES.length) {
				return PACKET_TYPES[index];
			} else {
				return INVALID;
			}
		}
		
		public int getIndex() {
			return index;
		}
	}
}
