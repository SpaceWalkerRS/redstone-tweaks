package redstonetweaks.setting.types;

import java.util.Arrays;

import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.Presets;

public abstract class ArraySetting<K, E> extends Setting<E[]> {
	
	public ArraySetting(SettingsPack pack, String name, String description) {
		super(pack, name, description);
	}
	
	@Override
	public String valueToString(E[] values) {
		String string = "";
		
		for (E value : get()) {
			string += elementToString(value) + ",";
		}
		
		return string.substring(0, string.length() - 1);
	}
	
	@Override
	public E[] stringToValue(String string) {
		String[] args = string.split(",");
		int size = args.length;
		
		E[] values = getEmptyArray(size);
		for (int index = 0; index < size; index++) {
			values[index] = stringToElement(args[index]);
		}
		
		return values;
	}
	
	@Override
	public void set(E[] newValue) {
		if (newValue.length == getSize()) {
			super.set(newValue.clone());
		}
	}
	
	@Override
	public void setPresetValue(Preset preset, E[] newValue) {
		if (preset == Presets.Default.DEFAULT || newValue.length == getSize()) {
			super.setPresetValue(preset, newValue.clone());
		}
	}
	
	@Override
	public boolean valueEquals(E[] value1, E[] value2) {
		return Arrays.equals(value1, value2);
	}
	
	protected abstract E[] getEmptyArray(int size);
	
	public abstract E stringToElement(String string);
	
	public String elementToString(E element) {
		return element.toString();
	}
	
	public E get(int index) {
		if (inRange(index)) {
			return get()[index];
		}
		
		return null;
	}
	
	public E get(K key) {
		return get(getIndexFromKey(key));
	}
	
	public void set(int index, E value) {
		if (inRange(index)) {
			get()[index] = value;
		}
	}
	
	public void set(K key, E value) {
		set(getIndexFromKey(key), value);
	}
	
	public void reset(int index) {
		set(index, getDefault(index));
	}
	
	public void reset(K key) {
		reset(getIndexFromKey(key));
	}
	
	public boolean isDefault(int index) {
		if (inRange(index)) {
			return get()[index].equals(getDefault()[index]);
		}
		
		return false;
	}
	
	public boolean isDefault(K key) {
		return isDefault(getIndexFromKey(key));
	}
	
	public E getDefault(int index) {
		if (inRange(index)) {
			return getDefault()[index];
		}
		
		return null;
	}
	
	public E getDefault(K key) {
		return getDefault(getIndexFromKey(key));
	}
	
	public abstract int getIndexFromKey(K key);
	
	public abstract K getKeyFromIndex(int index);
	
	public String getKeyAsString(K key) {
		return key.toString();
	}
	
	public String getKeyAsString(int index) {
		return getKeyAsString(getKeyFromIndex(index));
	}
	
	public int getSize() {
		return getDefault().length;
	}
	
	private boolean inRange(int index) {
		return index >= 0 && index < getSize();
	}
}
