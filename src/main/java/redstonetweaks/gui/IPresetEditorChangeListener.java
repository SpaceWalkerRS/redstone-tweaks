package redstonetweaks.gui;

import redstonetweaks.interfaces.IChangeListener;

public interface IPresetEditorChangeListener extends IChangeListener {
	
	@Override
	default void addChangeListener() {
		
	}
	
	@Override
	default void removeChangeListener() {
		
	}
	
}
