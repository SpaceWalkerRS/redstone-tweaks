package redstonetweaks.hotkeys;

import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

public class RTKeyBinding {
	
	private final String name;
	private final Key defaultKey;
	
	private Key key;
	private boolean pressed;
	private int timesPressed;
	private boolean allowUnbinding = true;
	
	public RTKeyBinding(String name, InputUtil.Type type, int defaultKeyCode) {
		this.name = name;
		this.defaultKey = type.createFromCode(defaultKeyCode);
		this.key = defaultKey;
	}
	
	public RTKeyBinding alwaysBound() {
		allowUnbinding = false;
		return this;
	}
	
	public boolean allowUnbinding() {
		return allowUnbinding;
	}
	
	public String getName() {
		return name;
	}
	
	public Key getDefaultKey() {
		return defaultKey;
	}
	
	public boolean isDefault() {
		return key.equals(defaultKey);
	}
	
	public Key getKey() {
		return key;
	}
	
	public void setKey(Key newKey) {
		key = newKey;
	}
	
	public boolean isUnbound() {
		return key.equals(InputUtil.UNKNOWN_KEY);
	}
	
	public boolean isPressed() {
		return pressed;
	}
	
	public boolean wasPressed() {
		return getTimesPressed() > 0;
	}
	
	public int getTimesPressed() {
		return timesPressed;
	}
	
	private void reset() {
		pressed = false;
		timesPressed = 0;
	}
	
	public boolean matchesKey(int keyCode, int scanCode) {
		if (keyCode == InputUtil.UNKNOWN_KEY.getCode()) {
			return key.getCategory() == InputUtil.Type.SCANCODE && key.getCode() == scanCode;
		} else {
			return key.getCategory() == InputUtil.Type.KEYSYM && key.getCode() == keyCode;
		}
	}
	
	public void onKeyPress() {
		if (!pressed) {
			pressed = true;
			timesPressed = 1;
		}
	}
	
	public void onKeyRelease() {
		reset();
	}
	
	public void onKeyRepeat() {
		timesPressed++;
	}
}
