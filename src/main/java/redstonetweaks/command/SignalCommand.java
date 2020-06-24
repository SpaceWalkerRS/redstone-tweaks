package redstonetweaks.command;

import java.util.ArrayList;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

import redstonetweaks.setting.Setting;
import redstonetweaks.setting.Settings;

public class SignalCommand {
	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal("signal").requires(context -> {
			return context.hasPermissionLevel(2);
		});
		
		ArrayList<Setting<?>> settings = Settings.getSettings("signal");
		
		builder.then(CommandManager.literal("RESET").executes(context -> {
			return resetValues(context.getSource(), settings);
		}));
		
		for (Setting<?> setting : settings) {
			builder.then(CommandManager.literal(setting.getCommandArgumentIdentifier()).executes(context -> {
				return queryValue(context.getSource(), setting);
			}).then(setting.argument("value").suggests((context, suggestionsBuilder) -> CommandSource.suggestMatching(setting.getCommandSuggestions(), suggestionsBuilder)).executes(context -> {
				return setValue(context, setting);
			})));
		}
		
		dispatcher.register(builder);
	}
	
	private static int resetValues(ServerCommandSource source, ArrayList<Setting<?>> settings) {
		for (Setting<?> setting : settings) {
			setting.reset();
		}
		source.sendFeedback(new TranslatableText("All signal strengths have been reset to their default values"), false);
		return 1;
	}
	
	private static int queryValue(ServerCommandSource source, Setting<?> setting) {
		source.sendFeedback(new TranslatableText("The %s signal strength is currently set to %s", setting.getCommandArgumentIdentifier(), setting.get()), false);
		return 1;
	}
	
	private static int setValue(CommandContext<ServerCommandSource> context, Setting<?> setting) throws CommandSyntaxException {
		setting.setFromArgument(context, "value");
		context.getSource().sendFeedback(new TranslatableText("The %s signal strength has been set to %s", setting.getCommandArgumentIdentifier(), setting.getFromArgument(context, "value")), false);
		return 1;
	}
}
