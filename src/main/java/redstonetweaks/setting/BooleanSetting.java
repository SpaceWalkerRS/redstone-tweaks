package redstonetweaks.setting;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class BooleanSetting extends Setting<Boolean> {
	private boolean value;
	
	public BooleanSetting(String name, boolean defaultValue) {
		this(name, name, defaultValue);
	}
	
	public BooleanSetting(String name, String commandIdentifier, boolean defaultValue) {
		super("bool", name, commandIdentifier, new Boolean[] {false, true}, defaultValue);
		
		set(defaultValue);
	}
	
	@Override
	public Boolean get() {
		return Boolean.valueOf(value);
	}
	
	@Override
	public Boolean getFromArgument(CommandContext<ServerCommandSource> context, String name) {
		return Boolean.valueOf(BoolArgumentType.getBool(context, name));
	}
	
	@Override
	public void set(Boolean newValue) {
		value = newValue.booleanValue();
	}
	
	@Override
	public void setFromArgument(CommandContext<ServerCommandSource> context, String name) {
        set(BoolArgumentType.getBool(context, name));
     }
	
	public RequiredArgumentBuilder<ServerCommandSource, ?> argument(String name) {
        return CommandManager.argument(name, BoolArgumentType.bool());
     }
}
