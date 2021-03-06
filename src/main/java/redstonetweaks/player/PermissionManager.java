package redstonetweaks.player;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.PlayerEntity;

import redstonetweaks.listeners.IPermissionListener;
import redstonetweaks.server.ServerInfo;
import redstonetweaks.setting.SettingsCategory;
import redstonetweaks.setting.settings.ServerConfig;

public class PermissionManager {
	
	private static final Set<IPermissionListener> LISTENERS = new HashSet<>();
	
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
	
	public static boolean isOp(PlayerEntity player) {
		return player.hasPermissionLevel(2);
	}
	
	public static boolean canChangeSettings(PlayerEntity player, SettingsCategory category) {
		return category.opOnly() ? canManageSettings(player) : canChangeSettings(player);
	}
	
	public static boolean canChangeSettings(PlayerEntity player) {
		return ServerInfo.getModVersion().isValid() && (isOp(player) || ServerConfig.Permissions.EDIT_SETTINGS.get());
	}
	
	public static boolean canManageSettings(PlayerEntity player) {
		return ServerInfo.getModVersion().isValid() && isOp(player);
	}
	
	public static boolean canEditPresets(PlayerEntity player) {
		return ServerInfo.getModVersion().isValid() && (isOp(player) || ServerConfig.Permissions.EDIT_PRESETS.get());
	}
	
	public static boolean canManagePresets(PlayerEntity player) {
		return ServerInfo.getModVersion().isValid() && isOp(player);
	}
	
	public static boolean canUseRandomOffsetCommand(PlayerEntity player) {
		return ServerInfo.getModVersion().isValid() && (isOp(player) || ServerConfig.Permissions.RANDOM_OFFSET_COMMAND.get());
	}
	
	public static boolean canUseTickCommand(PlayerEntity player) {
		return ServerInfo.getModVersion().isValid() && (isOp(player) || ServerConfig.Permissions.TICK_COMMAND.get());
	}
}
