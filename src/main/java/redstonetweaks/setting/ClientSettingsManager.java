package redstonetweaks.setting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;

import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.listeners.ISettingListener;
import redstonetweaks.packet.types.LockCategoryPacket;
import redstonetweaks.packet.types.LockPackPacket;
import redstonetweaks.packet.types.LockSettingPacket;
import redstonetweaks.packet.types.ResetSettingPacket;
import redstonetweaks.packet.types.ResetCategoryPacket;
import redstonetweaks.packet.types.ResetPackPacket;
import redstonetweaks.packet.types.SettingPacket;
import redstonetweaks.setting.settings.Settings;
import redstonetweaks.setting.types.ISetting;

public class ClientSettingsManager implements ISettingListener {
	
	private final MinecraftClient client;
	
	private boolean deaf;
	
	public ClientSettingsManager(MinecraftClient client) {
		this.client = client;
	}
	
	@Override
	public void categoryLockedChanged(SettingsCategory category) {
		if (!deaf && !client.isInSingleplayer()) {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new LockCategoryPacket(category));
		}
	}
	
	@Override
	public void packLockedChanged(SettingsPack pack) {
		if (!deaf && !client.isInSingleplayer()) {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new LockPackPacket(pack));
		}
	}
	
	@Override
	public void settingLockedChanged(ISetting setting) {
		if (!deaf && !client.isInSingleplayer()) {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new LockSettingPacket(setting));
		}
	}
	
	@Override
	public void settingValueChanged(ISetting setting) {
		if (!deaf && !client.isInSingleplayer()) {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new SettingPacket(setting));
		}
	}
	
	public void resetCategory(SettingsCategory category, boolean fromPacket) {
		if (fromPacket) {
			deaf = true;
			
			if (fromPacket) {
				category.resetAll();
			} else {
				((RTIMinecraftServer)client.getServer()).getSettingsManager().resetCategory(category);
			}
			
			deaf = false;
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new ResetCategoryPacket(category));
		}
	}
	
	public void resetPack(SettingsPack pack, boolean fromPacket) {
		if (fromPacket) {
			deaf = true;
			
			if (fromPacket) {
				pack.resetAll();
			} else {
				((RTIMinecraftServer)client.getServer()).getSettingsManager().resetPack(pack);
			}
			
			deaf = false;
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new ResetPackPacket(pack));
		}
	}
	
	public void resetSetting(ISetting setting, boolean fromPacket) {
		if (client.isInSingleplayer() || fromPacket) {
			deaf = true;
			
			if (fromPacket) {
				setting.reset();
			} else {
				((RTIMinecraftServer)client.getServer()).getSettingsManager().resetSetting(setting);
			}
			
			deaf = false;
		} else {
			((RTIMinecraftClient)client).getPacketHandler().sendPacket(new ResetSettingPacket(setting));
		}
	}
	
	public void setCategoryLocked(SettingsCategory category, boolean locked) {
		deaf = true;
		
		category.setLocked(locked);
		
		deaf = false;
	}
	
	public void setPackLocked(SettingsPack pack, boolean locked) {
		deaf = true;
		
		pack.setLocked(locked);
		
		deaf = false;
	}
	
	public void setSettingLocked(ISetting setting, boolean locked) {
		deaf = true;
		
		setting.setLocked(locked);
		
		deaf = false;
	}
	
	public void decodeSetting(ISetting setting, PacketByteBuf buffer) {
		deaf = true;
		
		setting.decode(buffer);
		
		deaf = false;
	}
	
	public void decodeSettings(ISetting[] settings, PacketByteBuf buffer) {
		deaf = true;
		
		for (ISetting setting : settings) {
			if (setting != null) {
				setting.setEnabled(true);
				setting.decode(buffer);
			}
		}
		
		deaf = false;
	}
	
	public void onConnect() {
		Settings.addListener(this);
		
		if (!client.isInSingleplayer()) {
			Settings.toDefault();
		}
	}
	
	public void onDisconnect() {
		Settings.removeListener(this);
		
		if (!client.isInSingleplayer()) {
			Settings.toDefault();
		}
	}
}
