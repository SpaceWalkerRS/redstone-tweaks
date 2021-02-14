package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.settings.Settings;
import redstonetweaks.util.PacketUtils;

public class LockPackPacket extends RedstoneTweaksPacket {
	
	public SettingsPack pack;
	public boolean locked;
	
	public LockPackPacket() {
		
	}
	
	public LockPackPacket(SettingsPack pack) {
		this(pack, pack.isLocked());
	}
	
	public LockPackPacket(SettingsPack pack, boolean locked) {
		this.pack = pack;
		this.locked = locked;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(pack.getId());
		buffer.writeBoolean(locked);
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		pack = Settings.getPackFromId(buffer.readString(PacketUtils.MAX_STRING_LENGTH));
		locked = buffer.readBoolean();
	}
	
	@Override
	public void execute(MinecraftServer server) {
		if (pack != null) {
			pack.setLocked(locked);
		}
	}
	
	@Override
	public void execute(MinecraftClient client) {
		if (!client.isInSingleplayer() && pack != null) {
			pack.setLocked(locked);
		}
	}
}