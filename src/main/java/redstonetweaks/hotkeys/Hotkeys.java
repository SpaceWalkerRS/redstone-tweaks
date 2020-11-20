package redstonetweaks.hotkeys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

public class Hotkeys {
	
	private final HotkeysManager manager;
	
	private final List<RTKeyBinding> keys;
	private final Map<Key, RTKeyBinding> keyToBinding;
	private final Map<String, RTKeyBinding> nameToBinding;
	
	public final RTKeyBinding toggleMenu;
	public final RTKeyBinding pauseWorldTicking;
	public final RTKeyBinding advanceWorldTicking;
	
	public Hotkeys(HotkeysManager manager) {
		this.manager = manager;
		
		this.keys = new ArrayList<>();
		this.keyToBinding = new HashMap<>();
		this.nameToBinding = new HashMap<>();
		
		this.toggleMenu = register(new RTKeyBinding("Open Menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R).alwaysBound());
		this.pauseWorldTicking = register(new RTKeyBinding("Pause World Ticking", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_P));
		this.advanceWorldTicking = register(new RTKeyBinding("Advance World Ticking", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O));
	}
	
	private RTKeyBinding register(RTKeyBinding keyBinding) {
		keys.add(keyBinding);
		keyToBinding.put(keyBinding.getKey(), keyBinding);
		nameToBinding.put(keyBinding.getName(), keyBinding);
		
		return keyBinding;
	}
	
	public List<RTKeyBinding> getKeyBindings() {
		return keys;
	}
	
	public RTKeyBinding getKeyBinding(Key key) {
		return keyToBinding.get(key);
	}
	
	public RTKeyBinding getKeyBinding(String name) {
		return nameToBinding.get(name);
	}
	
	public void updateKeyBinding(RTKeyBinding keyBinding, Key newKey) {
		setKeyBinding(keyBinding, newKey);
		manager.onKeyBindingChanged(keyBinding);
	}
	
	public void setKeyBinding(RTKeyBinding keyBinding, Key newKey) {
		keyToBinding.remove(keyBinding.getKey());
		
		keyBinding.setKey(newKey);
		
		keyToBinding.put(newKey, keyBinding);
	}
	
	public void resetKeyBindings() {
		keyToBinding.clear();
		
		for (RTKeyBinding keyBinding : keys) {
			keyBinding.setKey(keyBinding.getDefaultKey());
			
			keyToBinding.put(keyBinding.getKey(), keyBinding);
		}
		
		manager.onKeyBindingChanged(null);
	}
}
