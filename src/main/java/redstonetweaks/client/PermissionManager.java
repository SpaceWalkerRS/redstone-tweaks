package redstonetweaks.client;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.MinecraftClient;
import redstonetweaks.changelisteners.IPermissionChangeListener;
import redstonetweaks.setting.ServerConfig;

public class PermissionManager {
	
	private static final Set<IPermissionChangeListener> CHANGE_LISTENERS = new HashSet<>();
	
	private static MinecraftClient client;
	
	
	public static void init(MinecraftClient client) {
		PermissionManager.client = client;
	}
	
	public static void addChangeListener(IPermissionChangeListener listener) {
		CHANGE_LISTENERS.add(listener);
	}
	
	public static void removeChangeListener(IPermissionChangeListener listener) {
		CHANGE_LISTENERS.remove(listener);
	}
	
	public static void permissionLevelChanged() {
		CHANGE_LISTENERS.forEach((listener) -> listener.permissionLevelChanged());
	}
	
	public static boolean isOp() {
		return client.player.hasPermissionLevel(2);
	}
	
	public static boolean canChangeSettings() {
		return isOp() || ServerConfig.Settings.EDIT_GAME_MODES.get(client.interactionManager.getCurrentGameMode());
	}
	
	public static boolean canManageSettings() {
		return isOp();
	}
}
