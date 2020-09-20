package redstonetweaks.settings;

import java.util.ArrayList;
import java.util.List;

public abstract class ListSetting<T> extends Setting<List<T>> {

	public ListSetting(String name, String description, List<T> defaultValue) {
		super(name, description, defaultValue);
	}

	@Override
	public void setFromText(String text) {
		String[] args = text.split(", ", 0);
		
		List<T> objs = new ArrayList<>();
		for (String arg : args) {
			T obj = getElementFromText(arg);
			if (obj != null) {
				objs.add(obj);
			}
		}
		
		set(objs);
	}
	
	@Override
	public String getAsText() {
		String asText = "";
		
		List<T> objs = get();
		int i;
		for (i = 0; i < objs.size(); i++) {
			asText += objs.get(i) + ", ";
		}
		asText += objs.get(i);
		
		return asText;
	}
	
	protected abstract T getElementFromText(String text);
	
	public void addElement(T element) {
		get().add(element);
	}
	
	public void removeElement(T element) {
		get().remove(element);
	}
}
