package redstonetweaks.setting.types;

public interface ISetting {
	
	public String getId();
	
	public String getName();
	
	public String getDescription();
	
	public boolean isDefault();
	
	public void reset();
	
	public void setFromText(String text);
	
	public String getAsText();
	
}
