package redstonetweaks.setting;

import java.util.ArrayList;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;

public abstract class Setting<T> {
	private final String settingTypeID;
	private final String name;
	private final String commandArgumentIdentifier;
	private final T[] commandSuggestions;
	private final T defaultValue;
	
	protected ArrayList<Setting<?>> subSettings;
	
	public Setting(String settingTypeID, String name, String commandArgumentIdentifier, T[] commandSuggestions, T defaultValue) {
		this.settingTypeID = settingTypeID;
		this.name = name;
		this.commandArgumentIdentifier = commandArgumentIdentifier;
		this.commandSuggestions = commandSuggestions;
		this.defaultValue = defaultValue;
	}
	
	public String getSettingTypeID() {
		return settingTypeID;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCommandArgumentIdentifier() {
		return commandArgumentIdentifier;
	}
	
	public String[] getCommandSuggestions() {
		int l = this.commandSuggestions.length;
		String[] commandSuggestions = new String[l];
		for (int i = 0; i < l; i++) {
			commandSuggestions[i] = String.valueOf(this.commandSuggestions[i]);
		}
		return commandSuggestions;
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
