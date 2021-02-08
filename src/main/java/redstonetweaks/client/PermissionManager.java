package redstonetweaks.client;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.MinecraftClient;
import redstonetweaks.listeners.IPermissionListener;
import redstonetweaks.server.ServerInfo;
import redstonetweaks.setting.ServerConfig;
import redstonetweaks.setting.SettingsCategory;

public class PermissionManager {
	
	private static final Set<IPermissionListener> LISTENERS = new HashSet<>();
	
	private static MinecraftClient client;
	
	public static void init(MinecraftClient client) {
		PermissionManager.client = client;
	}
	
	public static void addListener(IPermissionListener listener) {
		LISTENERS.add(listener);
	}
	
	public static void removeListener(IPermissionListener listener) {
		LISTENERS.remove(listener);
	}
	
	public static void clearListeners() {
		LISTENERS.clear();
	}
	
	public static void permissionsChanged() {
		LISTENERS.forEach((listener) -> listener.permissionsChanged());
	}
	
	public static boolean isOp() {
		return client.player.hasPermissionLevel(2);
	}
	
	public static boolean canChangeSettings(SettingsCategory category) {
		return category.opOnly() ? canManageSettings() : canChangeSettings();
	}
	
	public static boolean canChangeSettings() {
		return ServerInfo.getModVersion().isValid() && (isOp() || ServerConfig.Permissions.EDIT_SETTINGS.get());
	}
	
	public static boolean canManageSettings() {
		return ServerInfo.getModVersion().isValid() && isOp();
	}
	
	public static boolean canEditPresets() {
		return ServerInfo.getModVersion().isValid() && (isOp() || ServerConfig.Permissions.EDIT_PRESETS.get());
	}
	
	public static boolean canManagePresets() {
		return ServerInfo.getModVersion().isValid() && isOp();
	}
	
	public static boolean canUseTickCommand() {
		return ServerInfo.getModVersion().isValid() && (isOp() || ServerConfig.Permissions.TICK_COMMAND.get());
	}
}
