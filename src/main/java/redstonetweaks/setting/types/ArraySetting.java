package redstonetweaks.setting.types;

import java.util.Arrays;

import net.minecraft.network.PacketByteBuf;

import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.preset.Preset;

public abstract class ArraySetting<K, E> extends Setting<E[]> {
	
	public ArraySetting(SettingsPack pack, String name, String description) {
		super(pack, name, description);
	}
	
	@Override
	protected void write(PacketByteBuf buffer, E[] value) {
		buffer.writeInt(value.length);
		
		for (E element : value) {
			writeElement(buffer, element);
		}
	}
	
	@Override
	protected E[] read(PacketByteBuf buffer) {
		int size = buffer.readInt();
		
		E[] array = getEmptyArray(size);
		for (int index = 0; index < size; index++) {
			array[index] = readElement(buffer);
		}
		
		return array;
	}
	
	@Override
	public void set(E[] newValue) {
		if (newValue.length == getSize()) {
			super.set(newValue);
		}
	}
	
	@Override
	public void setPresetValue(Preset preset, E[] newValue) {
		if (newValue.length == getSize()) {
			super.setPresetValue(preset, newValue);
		}
	}
	
	@Override
	protected E[] copy(E[] value) {
		return value.clone();
	}
	
	@Override
	protected boolean valueEquals(E[] value1, E[] value2) {
		return Arrays.equals(value1, value2);
	}
	
	protected abstract void writeElement(PacketByteBuf buffer, E element);
	
	protected abstract E readElement(PacketByteBuf buffer);
	
	protected abstract E[] getEmptyArray(int size);
	
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
	
	public int getSize() {
		return getDefault().length;
	}
	
	private boolean inRange(int index) {
		return index >= 0 && index < getSize();
	}
}
