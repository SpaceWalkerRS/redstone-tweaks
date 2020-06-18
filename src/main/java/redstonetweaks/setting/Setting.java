package redstonetweaks.setting;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;

public abstract class Setting<T> {
	private final String settingTypeID;
	
	private final String category;
	private final String name;
	private final String commandIdentifier;
	private final T defaultValue;
	
	public Setting(String settingTypeID, String category, String name, String commandIdentifier, T defaultValue) {
		this.settingTypeID = settingTypeID;
		
		this.category = category;
		this.name = name;
		this.commandIdentifier = commandIdentifier;
		this.defaultValue = defaultValue;
	}
	
	public String getSettingTypeID() {
		return settingTypeID;
	}
	
	public String getCategory() {
		return category;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCommandIdentifier() {
		return commandIdentifier;
	}
	
	public T getDefault() {
		return defaultValue;
	}
	
	public abstract T get();
	
	public abstract T getFromArgument(CommandContext<ServerCommandSource> context, String name);
	
	public abstract void set(T newValue);
	
	public abstract void setFromArgument(CommandContext<ServerCommandSource> context, String name);
	
	public void reset() {
		set(defaultValue);
	}
	
	public abstract RequiredArgumentBuilder<ServerCommandSource, ?> argument(String name);
}
