package redstonetweaks.packet;

import static redstonetweaks.setting.SettingsManager.SETTINGS;
import static redstonetweaks.setting.SettingsManager.SETTINGS_PACKS;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.TickPriority;

import redstonetweaks.helper.MinecraftClientHelper;
import redstonetweaks.setting.BooleanProperty;
import redstonetweaks.setting.IntegerProperty;
import redstonetweaks.setting.Property;
import redstonetweaks.setting.Setting;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.TickPriorityProperty;

public class SettingPacket<T> extends RedstoneTweaksPacket {
	
	public SettingsPack pack;
	public Setting<? extends Property<T>> setting;
	public T value;
	
	public SettingPacket() {
		
	}
	
	public SettingPacket(SettingsPack pack, Setting<? extends Property<T>> setting) {
		this.pack = pack;
		this.setting = setting;
		this.value = pack.get(setting);
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeString(pack.getName());
		buffer.writeString(setting.getName());
		
		if (value instanceof Boolean) {
			buffer.writeBoolean((boolean)(Object)value);
		}
		if (value instanceof Integer) {
			buffer.writeInt((int)(Object)value);
		}
		if (value instanceof TickPriority) {
			buffer.writeInt(((TickPriority)(Object)value).getIndex());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void decode(PacketByteBuf buffer) {
		pack = SETTINGS_PACKS.get(buffer.readString());
		setting = (Setting<? extends Property<T>>)SETTINGS.get(buffer.readString());
		
		Property<T> property = pack.getProperty(setting);
		if (property instanceof BooleanProperty) {
			value = (T)(Object)buffer.readBoolean();
		} else if (property instanceof IntegerProperty) {
			value = (T)(Object)buffer.readInt();
		} else if (property instanceof TickPriorityProperty) {
			value = (T)TickPriority.byIndex(buffer.readInt());
		}
	}

	@Override
	public void execute(MinecraftServer server) {

	}

	@Override
	public void execute(MinecraftClient client) {
		((MinecraftClientHelper)client).getSettingsManager().updateSetting(pack, setting, value);
	}
}
