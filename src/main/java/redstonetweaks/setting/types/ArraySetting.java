package redstonetweaks.setting.types;

import java.util.Arrays;

public abstract class ArraySetting<K, V> extends Setting<V[]> {
	
	private final int size;
	
	public ArraySetting(String name, String description, V[] defaultValues) {
		super(name, description, defaultValues);
		
		this.size = getDefault().length;
	}
	
	@Override
	public boolean isDefault() {
		return Arrays.equals(get(), getDefault());
	}
	
	@Override
	public void setValueFromString(String string) {
		String[] args = string.split(",");
		
		for (int i = 0; i < args.length; i++) {
			try {
				set(i, stringToElement(args[i]));
			} catch (Exception e) {
				
			}
		}
	}
	
	@Override
	public String getValueAsString() {
		String string = "";
		
		for (V value : get()) {
			string += elementToString(value) + ",";
		}
		
		return string.substring(0, string.length() - 1);
	}
	

	@Override
	public void set(V[] newValue) {
		super.set(newValue.clone());
	}
	
	public abstract V stringToElement(String string);
	
	public String elementToString(V element) {
		return element.toString();
	}
	
	public V get(int index) {
		if (inRange(index)) {
			return get()[index];
		}
		return null;
	}
	
	public V get(K key) {
		return get(getIndexFromKey(key));
	}
	
	public void set(int index, V value) {
		if (inRange(index)) {
			get()[index] = value;
		}
	}
	
	public void set(K key, V value) {
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
	
	public V getDefault(int index) {
		if (inRange(index)) {
			return getDefault()[index];
		}
		return null;
	}
	
	public V getDefault(K key) {
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
		return size;
	}
	
	private boolean inRange(int index) {
		return index >= 0 && index < getSize();
	}
}
