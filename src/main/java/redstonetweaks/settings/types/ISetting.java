package redstonetweaks.settings.types;

import redstonetweaks.gui.ButtonPanel;

public interface ISetting {
	
	public String getId();
	
	public String getName();
	
	public String getDescription();
	
	public boolean isDefault();
	
	public void reset();
	
	public boolean hasChanged();
	
	public void setFromText(String text);
	
	public String getAsText();
	
	public void populateButtonPanel(ButtonPanel panel);
	
}
