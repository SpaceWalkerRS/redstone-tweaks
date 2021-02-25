package redstonetweaks.hotkeys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

public class Hotkeys {
	
	private final HotkeysManager manager;
	
	private final List<RTKeyBinding> keyBindings;
	private final Map<Key, RTKeyBinding> keyToBinding;
	private final Map<String, RTKeyBinding> nameToBinding;
	
	public final RTKeyBinding toggleMenu;
	public final RTKeyBinding pauseWorldTicking;
	public final RTKeyBinding advanceWorldTicking;
	
	public Hotkeys(HotkeysManager manager) {
		this.manager = manager;
		
		this.keyBindings = new ArrayList<>();
		this.keyToBinding = new HashMap<>();
		this.nameToBinding = new HashMap<>();
		
		this.toggleMenu = register(new RTKeyBinding("Open Menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R));
		this.pauseWorldTicking = register(new RTKeyBinding("Pause World Ticking", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_P));
		this.advanceWorldTicking = register(new RTKeyBinding("Advance World Ticking", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O));
	}
	
	private RTKeyBinding register(RTKeyBinding keyBinding) {
		keyBindings.add(keyBinding);
		keyToBinding.put(keyBinding.getKey(), keyBinding);
		nameToBinding.put(keyBinding.getName(), keyBinding);
		
		return keyBinding;
	}
	
	public List<RTKeyBinding> getKeyBindings() {
		return keyBindings;
	}
	
	public RTKeyBinding getKeyBinding(Key key) {
		return keyToBinding.get(key);
	}
	
	public RTKeyBinding getKeyBinding(String name) {
		return nameToBinding.get(name);
	}
	
	public void updateKeyBinding(RTKeyBinding keyBinding, Key newKey) {
		setKeyBinding(keyBinding, newKey);
		
		manager.onKeyBindingChanged();
	}
	
	public void setKeyBinding(RTKeyBinding keyBinding, Key newKey) {
		keyBinding.setKey(newKey);
		
		validate();
	}
	
	public void resetKeyBindings() {
		keyToBinding.clear();
		
		for (RTKeyBinding keyBinding : keyBindings) {
			setKeyBinding(keyBinding, keyBinding.getDefaultKey());
		}
		
		manager.onKeyBindingChanged();
	}
	
	public void validate() {
		keyToBinding.clear();
		
		Set<Key> duplicates = new HashSet<>();
		
		for (RTKeyBinding keyBinding : keyBindings) {
			Key key = keyBinding.getKey();
			
			if (duplicates.contains(key)) {
				continue;
			}
			if (keyToBinding.containsKey(key)) {
				keyToBinding.remove(key);
				duplicates.add(key);
				
				continue;
			}
			
			keyToBinding.put(key, keyBinding);
		}
	}
	
	public boolean isValid(RTKeyBinding keyBinding) {
		return keyToBinding.get(keyBinding.getKey()) == keyBinding;
	}
}
