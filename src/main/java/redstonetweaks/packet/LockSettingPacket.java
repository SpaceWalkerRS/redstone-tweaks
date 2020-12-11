package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.interfaces.RTIMinecraftServer;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.types.ISetting;

public class LockSettingPacket extends RedstoneTweaksPacket {
	
	public ISetting setting;
	public boolean locked;
	
	public LockSettingPacket() {
		
	}
	
	public LockSettingPacket(ISetting setting) {
		this(setting, setting.isLocked());
	}
	
	public LockSettingPacket(ISetting setting, boolean locked) {
		this.setting = setting;
		this.locked = locked;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(setting.getId());
		buffer.writeBoolean(locked);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		setting = Settings.getSettingFromId(buffer.readString(MAX_STRING_LENGTH));
		locked = buffer.readBoolean();
	}
	
	@Override
	public void execute(MinecraftServer server) {
		if (setting != null) {
			setting.setLocked(locked);
			
			((RTIMinecraftServer)server).getSettingsManager().onLockSettingPacketReceived(setting);
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (setting != null) {
			setting.setLocked(locked);
			
			((RTIMinecraftClient)client).getSettingsManager().onLockSettingPacketReceived(setting);
		}
	}
}