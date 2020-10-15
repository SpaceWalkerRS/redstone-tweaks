package redstonetweaks.hotkeys;

import java.util.function.BiFunction;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

public class RTKeyBinding {
	
	private final String name;
	private final Key defaultKey;
	private final BiFunction<RTKeyBinding, Integer, Boolean> keyListener;
	
	private Key key;
	private boolean pressed;
	private int timesPressed;
	private boolean allowUnbinding = true;
	
	public RTKeyBinding(String name, InputUtil.Type type, int defaultKeyCode, BiFunction<RTKeyBinding, Integer, Boolean> keyListener) {
		this.name = name;
		this.defaultKey = type.createFromCode(defaultKeyCode);
		this.key = defaultKey;
		this.keyListener = keyListener;
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
	
	public int getTimesPressed() {
		return timesPressed;
	}
	
	private void reset() {
		pressed = false;
		timesPressed = 0;
	}
	
	public boolean onKey(int event) {
		switch(event) {
		case GLFW.GLFW_RELEASE:
			onKeyRelease();
			break;
		case GLFW.GLFW_PRESS:
			onKeyPress();
			break;
		case GLFW.GLFW_REPEAT:
			onKeyRepeat();
			break;
		default:
			break;
		}
		
		return keyListener.apply(this, event);
	}
	
	private void onKeyPress() {
		if (!pressed) {
			pressed = true;
			timesPressed = 1;
		}
	}
	
	private void onKeyRelease() {
		reset();
	}
	
	private void onKeyRepeat() {
		timesPressed++;
	}
}
