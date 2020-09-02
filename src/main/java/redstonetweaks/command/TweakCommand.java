package redstonetweaks.command;

import static redstonetweaks.setting.SettingsManager.*;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.TickPriority;

import redstonetweaks.helper.MinecraftServerHelper;
import redstonetweaks.setting.BooleanProperty;
import redstonetweaks.setting.BugReports;
import redstonetweaks.setting.IntegerProperty;
import redstonetweaks.setting.Property;
import redstonetweaks.setting.Setting;
import redstonetweaks.setting.SettingsManager;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.TickPriorityProperty;

public class TweakCommand {
	
	@SuppressWarnings("unchecked")
	public static <T> void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.
			literal("tweak").
			requires(context -> {return context.hasPermissionLevel(2);}).
			then(CommandManager.
				literal("RESET").
				executes(context -> {return reset(context.getSource());}));
		
		for (SettingsPack pack : SETTINGS_PACKS.values()) {
			if (pack == BUG_FIXES) {
				buildBugFixSubCommand(builder);
			} else {
				for (Setting<?> setting : pack.getSettings()) {
					if (setting == RISING_DELAY) {
						buildSubCommandTwoArgs(builder, pack, DELAY, RISING_DELAY, FALLING_DELAY, "risingEdge", "fallingEdge");
					} else if (setting == FALLING_DELAY) {
						
					} else if (setting == DELAY_MIN) {
						buildSubCommandTwoArgs(builder, pack, DELAY_RANGE, DELAY_MIN, DELAY_MAX, "minimum", "maximum");
					} else if (setting == DELAY_MAX) {
					
					} else if (setting == RANDOMIZE_DELAYS_MIN) {
						buildSubCommandTwoArgs(builder, pack, RANDOMIZE_DELAYS, RANDOMIZE_DELAYS_MIN, RANDOMIZE_DELAYS_MAX, "minimum", "maximum");
					} else if (setting == RANDOMIZE_DELAYS_MAX) {
						
					} else if (setting == RISING_LAZY) { 
						buildSubCommandTwoArgs(builder, pack, LAZY, RISING_LAZY, FALLING_LAZY, "risingEdge", "fallingEdge");
					} else if (setting == FALLING_LAZY) { 
						
					} else if (setting == RISING_SPEED) {
						buildSubCommandTwoArgs(builder, pack, SPEED, RISING_SPEED, FALLING_SPEED, "risingEdge", "fallingEdge");
					} else if (setting == FALLING_SPEED) { 
						
					} else if (setting == RISING_TICK_PRIORITY) {
						buildSubCommandTwoArgs(builder, pack, TICK_PRIORITY, RISING_TICK_PRIORITY, FALLING_TICK_PRIORITY, "risingEdge", "fallingEdge");
					} else if (setting == FALLING_TICK_PRIORITY) { 
						
					} else if (setting == WEAK_POWER) {
						buildSubCommandTwoArgs(builder, pack, POWER, WEAK_POWER, STRONG_POWER, "weak", "strong");
					} else if (setting == STRONG_POWER) { 
						
					} else {
						buildSubCommandOneArg(builder, pack, (Setting<? extends Property<T>>)setting, "value");
					}
				}
			}
		}
		
		dispatcher.register(builder);
	}
	
	private static <T> ArgumentType<?> getArgumentType(SettingsPack pack, Setting<? extends Property<T>> setting) {
		Property<T> property = pack.getProperty(setting);
		if (property instanceof BooleanProperty) {
			return BoolArgumentType.bool();
		}
		if (property instanceof IntegerProperty) {
			IntegerProperty integerProperty = (IntegerProperty)property;
			return IntegerArgumentType.integer(integerProperty.getMin(), integerProperty.getMax());
		}
		if (property instanceof TickPriorityProperty) {
			int min = TickPriority.values()[0].getIndex();
			return IntegerArgumentType.integer(min, min + TickPriority.values().length);
		}
		
		throw new IllegalStateException("Unknown setting");
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T getArgument(CommandContext<ServerCommandSource> context, SettingsPack pack, Setting<? extends Property<T>> setting, String arg) {
		Property<T> property = pack.getProperty(setting);
		if (property instanceof BooleanProperty) {
			return (T)(Object)BoolArgumentType.getBool(context, arg);
		}
		if (property instanceof IntegerProperty) {
			return (T)(Object)IntegerArgumentType.getInteger(context, arg);
		}
		if (property instanceof TickPriorityProperty) {
			return (T)TickPriority.byIndex(IntegerArgumentType.getInteger(context, arg));
		}
		
		throw new IllegalStateException("Unknown setting");
	}
	
	@SuppressWarnings("unchecked")
	private static void buildBugFixSubCommand(LiteralArgumentBuilder<ServerCommandSource> builder) {
		for (Setting<?> setting : BUG_FIXES.getSettings()) {
			Setting<BooleanProperty> booleanSetting = (Setting<BooleanProperty>)setting;
			builder.then(CommandManager.
				literal(BUG_FIXES.getName()).
				then(CommandManager.
					literal(setting.getName()).
					executes(context -> {return queryBugFix(context.getSource(), booleanSetting);}).
					then(CommandManager.
						literal("disable").
						executes(context -> {return setBugFix(context.getSource(), booleanSetting, false);})).
					then(CommandManager.
						literal("enable").
						executes(context -> {return setBugFix(context.getSource(), booleanSetting, true);})).
					then(CommandManager.
						literal("report").
						executes(context -> {return queryBugReport(context.getSource(), booleanSetting);}))));
		}
	}
	
	private static <T> void buildSubCommandOneArg(LiteralArgumentBuilder<ServerCommandSource> builder, SettingsPack pack, Setting<? extends Property<T>> setting, String arg) {
		builder.then(CommandManager.
			literal(pack.getName()).
			then(CommandManager.
				literal(setting.getName()).
				executes(context -> {return query(context.getSource(), pack, setting);}).
				then(CommandManager.
					argument(arg, getArgumentType(pack, setting)).
					suggests((context, suggestionsBuilder) -> CommandSource.suggestMatching(pack.getCommandSuggestions(setting), suggestionsBuilder)).
					executes(context -> {return set(context, pack, setting, arg);}))));
	}
	
	private static <T> void buildSubCommandTwoArgs(LiteralArgumentBuilder<ServerCommandSource> builder, SettingsPack pack, Setting<? extends Property<T>> parentSetting, Setting<? extends Property<T>> setting1, Setting<? extends Property<T>> setting2, String arg1, String arg2) {
		builder.then(CommandManager.
			literal(pack.getName()).
			then(CommandManager.
				literal(parentSetting.getName()).
				executes(context -> {return query(context.getSource(), pack, parentSetting, setting1, setting2, arg1, arg2);}).
				then(CommandManager.
					argument(arg1, getArgumentType(pack, setting1)).
					suggests((context, suggestionsBuilder) -> CommandSource.suggestMatching(pack.getCommandSuggestions(setting1), suggestionsBuilder)).
					then(CommandManager.
						argument(arg2, getArgumentType(pack, setting2)).
						suggests((context, suggestionsBuilder) -> CommandSource.suggestMatching(pack.getCommandSuggestions(setting2), suggestionsBuilder)).
						executes(context -> {return set(context, pack, parentSetting, setting1, setting2, arg1, arg2);})))));
	}
	
	@SuppressWarnings("unchecked")
	private static <T> int reset(ServerCommandSource source) {
		SettingsManager settingsManager = ((MinecraftServerHelper)source.getMinecraftServer()).getSettingsManager();
		
		for (SettingsPack pack : SETTINGS_PACKS.values()) {
			for (Setting<?> setting : pack.getSettings()) {
				settingsManager.resetSetting(pack, (Setting<? extends Property<T>>)setting);
			}
		}
		source.sendFeedback(new TranslatableText("All settings have been reset to their default values"), false);
		return 1;
	}
	
	private static <T> void updateSetting(ServerCommandSource source, SettingsPack pack, Setting<? extends Property<T>> setting, T value) {
		((MinecraftServerHelper)source.getMinecraftServer()).getSettingsManager().updateSetting(pack, setting, value);
	}
	
	private static int queryBugFix(ServerCommandSource source, Setting<BooleanProperty> setting) {
		source.sendFeedback(new TranslatableText("The fix for %s is currently %s", setting.getName(), BUG_FIXES.get(setting) ? "enabled" : "disabled"), false);
		return 1;
	}
	
	private static int queryBugReport(ServerCommandSource source, Setting<BooleanProperty> setting) {
		source.sendFeedback(new TranslatableText("%s", BugReports.BUG_REPORTS.get(setting)).
				styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, BugReports.BUG_REPORTS.get(setting)))), false);
		return 1;
	}
	
	private static int setBugFix(ServerCommandSource source, Setting<BooleanProperty> setting, boolean value) {
		updateSetting(source, BUG_FIXES, setting, value);
		source.sendFeedback(new TranslatableText("The fix for %s has been %s", setting.getName(), value ? "enabled" : "disabled"), false);
		return 1;
	}
	
	private static <T> int query(ServerCommandSource source, SettingsPack pack, Setting<? extends Property<T>> setting) {
		source.sendFeedback(new TranslatableText("The %s %s setting is currently set to %s", pack.getName(), setting.getName(), pack.get(setting)), false);
		return 1;
	}
	
	private static <T> int query(ServerCommandSource source, SettingsPack pack, Setting<? extends Property<T>> parentSetting, Setting<? extends Property<T>> setting1, Setting<? extends Property<T>> setting2, String arg1, String arg2) {
		source.sendFeedback(new TranslatableText("The %s %s %s is currently set to %s and the %s %s is set to %s", pack.getName(), arg1, parentSetting.getName(), pack.get(setting1), arg2, parentSetting.getName(), pack.get(setting2)), false);
		return 1;
	}
	
	private static <T> int set(CommandContext<ServerCommandSource> context, SettingsPack pack, Setting<? extends Property<T>> setting, String arg) {
		ServerCommandSource source = context.getSource();
		
		T value = getArgument(context, pack, setting, arg);
		updateSetting(source, pack, setting, value);
		
		source.sendFeedback(new TranslatableText("The %s %s setting has been set to %s", pack.getName(), setting.getName(), value), false);
		return 1;
	}
	
	private static <T> int set(CommandContext<ServerCommandSource> context, SettingsPack pack, Setting<? extends Property<T>> parentSetting, Setting<? extends Property<T>> setting1, Setting<? extends Property<T>> setting2, String arg1, String arg2) {
		ServerCommandSource source = context.getSource();
		
		T value1 = getArgument(context, pack, setting1, arg1);
		T value2 = getArgument(context, pack, setting2, arg2);
		updateSetting(source, pack, setting1, value1);
		updateSetting(source, pack, setting2, value2);
		
		source.sendFeedback(new TranslatableText("The %s %s %s has been set to %s and the %s %s has been set to %s", pack.getName(), arg1, parentSetting.getName(), value1, arg2, parentSetting.getName(), value2), false);
		return 1;
	}
}