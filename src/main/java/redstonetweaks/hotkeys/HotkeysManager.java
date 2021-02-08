package redstonetweaks.hotkeys;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

import redstonetweaks.client.PermissionManager;
import redstonetweaks.gui.RTMenuScreen;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.packet.types.TickPausePacket;

public class HotkeysManager {
	
	private static final String CACHE_DIRECTORY = "redstonetweaks";
	private static final String HOTKEYS_PATH = "hotkeys.txt";
	
	private final MinecraftClient client;
	private final Hotkeys hotkeys;
	
	public HotkeysManager(MinecraftClient client) {
		this.client = client;
		this.hotkeys = new Hotkeys(this);
		
		this.loadHotkeys();
	}
	
	public Hotkeys getHotkeys() {
		return hotkeys;
	}
	
	public boolean onKey(int keyCode, int scanCode, int event) {
		RTKeyBinding keyBinding = hotkeys.getKeyBinding(InputUtil.fromKeyCode(keyCode, scanCode));
		
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
	
	private boolean keyPress(RTKeyBinding keyBinding) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (keyBinding == hotkeys.toggleMenu) {
			if (client.currentScreen == null) {
				client.openScreen(new RTMenuScreen(client));
				
				return true;
			}
		} else
		if (keyBinding == hotkeys.pauseWorldTicking) {
			if (client.currentScreen == null && PermissionManager.canUseTickCommand()) {
				((RTIMinecraftClient)client).getPacketHandler().sendPacket(new TickPausePacket(true));
				
				return true;
			}
		} else
		if (keyBinding == hotkeys.advanceWorldTicking) {
			if (client.currentScreen == null && PermissionManager.canUseTickCommand()) {
				((RTIMinecraftClient)client).getPacketHandler().sendPacket(new TickPausePacket(false));
				
				return true;
			}
		}
		
		return false;
	}
	
	private boolean keyRepeat(RTKeyBinding keyBinding) {
		return false;
	}
	
	private boolean keyRelease(RTKeyBinding keyBinding) {
		return false;
	}
	
	public void onKeyBindingChanged(RTKeyBinding keyBinding) {
		Screen screen = client.currentScreen;
		
		if (screen instanceof RTMenuScreen) {
			((RTMenuScreen)screen).onHotkeyChanged(keyBinding);
		}
	}
	
	public void loadHotkeys() {
		File hotkeysFile = getHotkeysFile();
		
		if (hotkeysFile.isFile()) {
			try (BufferedReader br = new BufferedReader(new FileReader(hotkeysFile))) {
				String line;
				
				while ((line = br.readLine()) != null) {
					try {
						String[] args = line.split(": ", 2);
						
						RTKeyBinding keyBinding = hotkeys.getKeyBinding(args[0]);
						Key key = InputUtil.fromTranslationKey(args[1]);
						
						if (keyBinding != null && key != null) {
							hotkeys.setKeyBinding(keyBinding, key);
						}
					} catch (Exception e) {
						
					}
				}
			} catch (IOException e) {
				
			}
		}
	}
	
	public void saveHotkeys() {
		File hotkeysFile = getHotkeysFile();
		
		try {
			if (!hotkeysFile.isFile()) {
				hotkeysFile.createNewFile();
			}
		} catch (IOException e) {
			
		}
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(hotkeysFile))) {
			for (RTKeyBinding keyBinding : hotkeys.getKeyBindings()) {
				bw.write(String.format("%s: %s", keyBinding.getName(), keyBinding.getKey().getTranslationKey()));
				bw.newLine();
			}
		} catch (IOException e) {
			
		}
	}
	
	private File getCacheDir() {
		MinecraftClient client = MinecraftClient.getInstance();
		File directory = new File(client.runDirectory, CACHE_DIRECTORY);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		return directory;
	}
	
	private File getHotkeysFile() {
		return new File(getCacheDir(), HOTKEYS_PATH);
	}
}
