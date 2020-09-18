package redstonetweaks.settings;

public interface ISetting {
	
	public String getName();
	
	public String getDescription();
	
	public boolean isDefault();
	
	public void reset();
	
	public void setFromText(String text);
	
	public String getAsText();
	
}
