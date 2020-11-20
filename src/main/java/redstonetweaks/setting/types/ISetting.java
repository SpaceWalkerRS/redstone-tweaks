package redstonetweaks.setting.types;

public interface ISetting {
	
	public String getId();
	
	public void setId(String id);
	
	public String getName();
	
	public String getDescription();
	
	public boolean isEnabled();
	
	public void setEnabled(boolean enabled);
	
	public boolean isLocked();
	
	public void setLocked(boolean locked);
	
	public boolean isDefault();
	
	public void reset();
	
	public String getAsString();
	
	public void setFromString(String string);
	
	public String getValueAsString();
	
	public void setValueFromString(String string);
	
}
