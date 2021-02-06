package redstonetweaks.packet;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.Unpooled;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import redstonetweaks.RedstoneTweaks;
import redstonetweaks.RedstoneTweaksVersion;
import redstonetweaks.packet.types.ApplyPresetPacket;
import redstonetweaks.packet.types.DoWorldTicksPacket;
import redstonetweaks.packet.types.IncompleteBlockActionPacket;
import redstonetweaks.packet.types.LockCategoryPacket;
import redstonetweaks.packet.types.LockPackPacket;
import redstonetweaks.packet.types.LockSettingPacket;
import redstonetweaks.packet.types.NeighborUpdateSchedulerPacket;
import redstonetweaks.packet.types.NeighborUpdateVisualizerPacket;
import redstonetweaks.packet.types.OpenMenuPacket;
import redstonetweaks.packet.types.PresetPacket;
import redstonetweaks.packet.types.PresetsPacket;
import redstonetweaks.packet.types.RedstoneTweaksPacket;
import redstonetweaks.packet.types.ReloadPresetsPacket;
import redstonetweaks.packet.types.RemovePresetPacket;
import redstonetweaks.packet.types.ResetSettingPacket;
import redstonetweaks.packet.types.ResetCategoryPacket;
import redstonetweaks.packet.types.ResetPackPacket;
import redstonetweaks.packet.types.ServerInfoPacket;
import redstonetweaks.packet.types.SettingPacket;
import redstonetweaks.packet.types.SettingsPacket;
import redstonetweaks.packet.types.TaskSyncPacket;
import redstonetweaks.packet.types.TickBlockEntityPacket;
import redstonetweaks.packet.types.TickPausePacket;
import redstonetweaks.packet.types.TickStatusPacket;
import redstonetweaks.packet.types.WorldSyncPacket;
import redstonetweaks.packet.types.WorldTimeSyncPacket;

public abstract class AbstractPacketHandler {
	
	public static final Identifier PACKET_IDENTIFIER = new Identifier("redstonetweaks");
	
	protected Packet<?> encodePacket(RedstoneTweaksPacket packet) {
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		
		buffer.writeString(RedstoneTweaks.PACKET_PROTOCOL.toString());
		
		PacketType packetType = PacketType.fromPacket(packet);
		if (packetType == PacketType.INVALID) {
			throw new IllegalStateException("Unable to encode packet: " + packet.getClass());
		}
		buffer.writeByte(packetType.getIndex());
		
		packet.encode(buffer);
		
		return toCustomPayloadPacket(buffer);
	}
	
	protected abstract Packet<?> toCustomPayloadPacket(PacketByteBuf buffer);
	
	protected RedstoneTweaksPacket decodePacket(PacketByteBuf buffer) throws InstantiationException, IllegalAccessException {
		PacketType type = PacketType.fromIndex(buffer.readByte());
		RedstoneTweaksPacket packet = type.getClazz().newInstance();
		
		packet.decode(buffer);
		
		return packet;
	}
	
	public abstract void sendPacket(RedstoneTweaksPacket redstoneTweaksPacket);
	
	public void onPacketReceived(PacketByteBuf buffer) {
		RedstoneTweaksVersion packetProtocol = RedstoneTweaksVersion.parseVersion(buffer.readString(32767));
		
		if (RedstoneTweaks.PACKET_PROTOCOL.equals(packetProtocol)) {
			try {
				execute(decodePacket(buffer));
			} catch (Exception e) {
				
			}
		}
	}
	
	protected abstract void execute(RedstoneTweaksPacket packet);
	
	private enum PacketType {
		
		INVALID(0, null),
		SETTING(1, SettingPacket.class),
		SETTINGS(2, SettingsPacket.class),
		RESET_SETTING(3, ResetSettingPacket.class),
		RESET_PACK(4, ResetPackPacket.class),
		RESET_CATEGORY(5, ResetCategoryPacket.class),
		NEIGHBOR_UPDATE_VISUALIZER(6, NeighborUpdateVisualizerPacket.class),
		NEIGHBOR_UPDATE_SCHEDULER(7, NeighborUpdateSchedulerPacket.class),
		WORLD_SYNC(8, WorldSyncPacket.class),
		TASK_SYNC(9, TaskSyncPacket.class),
		UNFINISHED_EVENT(10, IncompleteBlockActionPacket.class),
		WORLD_TIME_SYNC(11, WorldTimeSyncPacket.class),
		TICK_STATUS(12, TickStatusPacket.class),
		TICK_BLOCK_ENTITY(13, TickBlockEntityPacket.class),
		DO_WORLD_TICKS(14, DoWorldTicksPacket.class),
		TICK_PAUSE(15, TickPausePacket.class),
		SERVER_INFO(16, ServerInfoPacket.class),
		PRESET(17, PresetPacket.class),
		PRESETS(18, PresetsPacket.class),
		RELOAD_PRESETS(19, ReloadPresetsPacket.class),
		REMOVE_PRESET(20, RemovePresetPacket.class),
		APPLY_PRESET(21, ApplyPresetPacket.class),
		LOCK_SETTING(22, LockSettingPacket.class),
		LOCK_PACK(23, LockPackPacket.class),
		LOCK_CATEGORY(24, LockCategoryPacket.class),
		OPEN_MENU(25, OpenMenuPacket.class);
		
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
			}
			return INVALID;
		}
		
		public static PacketType fromPacket(RedstoneTweaksPacket packet) {
			return PACKET_TO_TYPE.getOrDefault(packet.getClass(), INVALID);
		}
		
		public int getIndex() {
			return index;
		}
		
		public Class<? extends RedstoneTweaksPacket> getClazz() {
			return clazz;
		}
	}
}
