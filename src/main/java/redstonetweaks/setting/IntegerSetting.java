package redstonetweaks.setting;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class IntegerSetting extends Setting<Integer> {
	private final int minValue;
	private final int maxValue;
	
	private int value;
	
	public IntegerSetting(String category, String name, int defaultValue, int minValue, int maxValue) {
		super("str", category, name, defaultValue);
		this.minValue = minValue;
		this.maxValue = maxValue;
		
		value = defaultValue;
	}
	
	public int getMin() {
		return minValue;
	}
	
	public int getMax() {
		return maxValue;
	}
	
	@Override
	public Integer get() {
		return Integer.valueOf(value);
	}
	
	@Override
	public Integer getFromArgument(CommandContext<ServerCommandSource> context, String name) {
		return Integer.valueOf(IntegerArgumentType.getInteger(context, name));
	}
	
	@Override
	public void set(Integer newValue) {
		value = newValue.intValue();
	}
	
	@Override
	public void setFromArgument(CommandContext<ServerCommandSource> context, String name) {
        value = IntegerArgumentType.getInteger(context, name);
     }
	
	public RequiredArgumentBuilder<ServerCommandSource, ?> argument(String name) {
        return CommandManager.argument(name, IntegerArgumentType.integer(this.getMin(), this.getMax()));
     }
}
