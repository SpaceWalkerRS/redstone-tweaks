package redstonetweaks.setting.types;

import java.util.Arrays;

public abstract class ArraySetting<T> extends Setting<T[]> {
	
	public ArraySetting(String prefix, String name, String description, T[] defaultValue) {
		super(prefix, name, description, defaultValue);
	}
	
	@Override
	public boolean isDefault() {
		return Arrays.equals(get(), getDefault());
	}
	
	@Override
	public void setFromText(String text) {
		String[] args = text.split(", ");
		
		for (int i = 0; i < args.length; i++) {
			try {
				set(i, textToValue(args[i]));
			} catch (Exception e) {
				
			}
		}
	}
	
	@Override
	public String getAsText() {
		String asText = "";
		
		for (T value : get()) {
			asText += valueToText(value) + ", ";
		}
		
		return asText.substring(0, asText.length() - 2);
	}
	
	@Override
	public void set(T[] newValue) {
		super.set(newValue.clone());
	}
	
	public abstract T textToValue(String text);
	
	public abstract String valueToText(T element);
	
	public T get(int index) {
		if (inRange(index)) {
			return get()[index];
		}
		return null;
	}
	
	public void set(int index, T value) {
		if (inRange(index)) {
			get()[index] = value;
		}
	}
	
	public void reset(int index) {
		set(index, getDefault(index));
	}
	
	public boolean isDefault(int index) {
		if (inRange(index)) {
			return get()[index].equals(getDefault()[index]);
		}
		return false;
	}
	
	public T getDefault(int index) {
		if (inRange(index)) {
			return getDefault()[index];
		}
		return null;
	}
	
	private boolean inRange(int index) {
		return index >= 0 && index < get().length;
	}
}
