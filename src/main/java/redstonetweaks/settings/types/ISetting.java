package redstonetweaks.settings.types;

import net.minecraft.client.MinecraftClient;

import redstonetweaks.gui.SettingsListWidget;

public interface ISetting {
	
	public String getId();
	
	public String getName();
	
	public String getDescription();
	
	public boolean isDefault();
	
	public void reset();
	
	public boolean hasChanged();
	
	public void setFromText(String text);
	
	public String getAsText();
	
	public SettingsListWidget.Entry createGUIEntry(MinecraftClient client);
	
}
