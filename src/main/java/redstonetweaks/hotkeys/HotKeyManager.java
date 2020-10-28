package redstonetweaks.hotkeys;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.interfaces.RTIMinecraftClient;
import redstonetweaks.packet.TickPausePacket;

public class HotKeyManager {
	
	private static final String CACHE_DIRECTORY = "redstonetweaks";
	private static final String HOTKEYS_PATH = "hotkeys.txt";
	
	private static final List<RTKeyBinding> KEYS = new ArrayList<>();
	private static final Map<String, RTKeyBinding> NAME_TO_BINDING = new HashMap<>();
	private static final Map<Key, RTKeyBinding> KEY_TO_BINDING = new HashMap<>();
	
	public static final RTKeyBinding TOGGLE_MENU;
	public static final RTKeyBinding PAUSE_WORLD_TICKING;
	public static final RTKeyBinding ADVANCE_WORLD_TICKING;
	
	public static RTKeyBinding register(RTKeyBinding keyBinding) {
		KEYS.add(keyBinding);
		NAME_TO_BINDING.put(keyBinding.getName(), keyBinding);
		KEY_TO_BINDING.put(keyBinding.getKey(), keyBinding);
		
		return keyBinding;
	}
	
	public static RTKeyBinding getKeyBinding(Key key) {
		return KEY_TO_BINDING.get(key);
	}
	
	public static List<RTKeyBinding> getKeyBindings() {
		return KEYS;
	}
	
	public static void updateKeyBinding(RTKeyBinding keyBinding, Key newKey) {
		KEY_TO_BINDING.remove(keyBinding.getKey());
		
		keyBinding.setKey(newKey);
		
		KEY_TO_BINDING.put(newKey, keyBinding);
		
		onKeyBindingChanged(keyBinding);
	}
	
	public static void resetKeyBindings() {
		KEY_TO_BINDING.clear();
		
		for (RTKeyBinding keyBinding : KEYS) {
			keyBinding.setKey(keyBinding.getDefaultKey());
			
			KEY_TO_BINDING.put(keyBinding.getKey(), keyBinding);
		}
		
		onKeyBindingChanged(null);
	}
	
	public static boolean onKey(int keyCode, int scanCode, int event) {
		RTKeyBinding keyBinding = getKeyBinding(InputUtil.fromKeyCode(keyCode, scanCode));
		
		if (keyBinding == null) {
			return false;
		}
		
		switch (event) {
		case GLFW.GLFW_RELEASE:
			keyBinding.onKeyRelease();
			return keyRelease(keyBinding);
		case GLFW.GLFW_PRESS:
			keyBinding.onKeyPress();
			return keyPress(keyBinding);
		case GLFW.GLFW_REPEAT:
			keyBinding.onKeyRepeat();
			return keyRepeat(keyBinding);
		}
		
		return false;
	}
	
	private static boolean keyPress(RTKeyBinding keyBinding) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (keyBinding == TOGGLE_MENU) {
			if (client.currentScreen == null) {
				client.openScreen(new RTMenuScreen(client));
				
				return true;
			}
		} else
		if (keyBinding == PAUSE_WORLD_TICKING) {
			if (client.currentScreen == null) {
				TickPausePacket packet = new TickPausePacket(TickPausePacket.PAUSE);
				((RTIMinecraftClient)client).getPacketHandler().sendPacket(packet);
				
				return true;
			}
		} else
		if (keyBinding == ADVANCE_WORLD_TICKING) {
			if (client.currentScreen == null) {
				TickPausePacket packet = new TickPausePacket(TickPausePacket.ADVANCE);
				((RTIMinecraftClient)client).getPacketHandler().sendPacket(packet);
				
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean keyRepeat(RTKeyBinding keyBinding) {
		return false;
	}
	
	private static boolean keyRelease(RTKeyBinding keyBinding) {
		return false;
	}
	
	private static void onKeyBindingChanged(RTKeyBinding keyBinding) {
		MinecraftClient client = MinecraftClient.getInstance();
		Screen screen = client.currentScreen;
		
		if (screen instanceof RTMenuScreen) {
			((RTMenuScreen)screen).onHotkeyChanged(keyBinding);
		}
	}
	
	public static void loadHotkeys() {
		File hotkeysFile = getHotkeysFile();
		
		if (hotkeysFile.isFile()) {
			try (BufferedReader br = new BufferedReader(new FileReader(hotkeysFile))) {
				String line;
				
				while ((line = br.readLine()) != null) {
					try {
						String[] args = line.split(": ", 2);
						
						RTKeyBinding keyBinding = NAME_TO_BINDING.get(args[0]);
						Key key = InputUtil.fromTranslationKey(args[1]);
						
						if (keyBinding != null && key != null) {
							KEY_TO_BINDING.remove(keyBinding.getKey());
							keyBinding.setKey(key);
							KEY_TO_BINDING.put(key, keyBinding);
						}
					} catch (Exception e) {
						
					}
				}
			} catch (IOException e) {
				
			}
		}
	}
	
	public static void saveHotkeys() {
		File hotkeysFile = getHotkeysFile();
		
		try {
			if (!hotkeysFile.isFile()) {
				hotkeysFile.createNewFile();
			}
		} catch (IOException e) {
			
		}
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(hotkeysFile))) {
			for (RTKeyBinding keyBinding : KEYS) {
				bw.write(keyBinding.getName());
				bw.write(": ");
				bw.write(keyBinding.getKey().getTranslationKey());
				bw.newLine();
			}
		} catch (IOException e) {
			
		}
	}
	
	private static File getCacheDir() {
		MinecraftClient client = MinecraftClient.getInstance();
		File directory = new File(client.runDirectory, CACHE_DIRECTORY);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		return directory;
	}
	
	private static File getHotkeysFile() {
		return new File(getCacheDir(), HOTKEYS_PATH);
	}
	
	static {
		TOGGLE_MENU = register(new RTKeyBinding("Open Menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R).alwaysBound());
		PAUSE_WORLD_TICKING = register(new RTKeyBinding("Pause World Ticking", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_P));
		ADVANCE_WORLD_TICKING = register(new RTKeyBinding("Advance World Ticking", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O));
	}
}
