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

public class QuasiConnectivityCommand {
	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal("quasiconnectivity").requires(context -> {
			return context.hasPermissionLevel(2);
		});
		
		ArrayList<Setting<?>> settings = Settings.getSettings("quasiconnectivity");
		
		for (Setting<?> setting : settings) {
			builder.then(CommandManager.literal(setting.getCommandArgumentIdentifier()).executes(context -> {
				return queryValue(context.getSource(), setting);
			}).then(setting.argument("value").suggests((context, suggestionsBuilder) -> CommandSource.suggestMatching(setting.getCommandSuggestions(), suggestionsBuilder)).executes(context -> {
				return setValue(context, setting);
			})));
		}
		
		dispatcher.register(builder);
	}
	
	private static int queryValue(ServerCommandSource source, Setting<?> setting) {
		source.sendFeedback(new TranslatableText("Quasi-Connectivity is currently %s for the %s direction", (boolean)setting.get() ? "enabled" : "disabled", setting.getCommandArgumentIdentifier()), false);
		return 1;
	}
	
	private static int setValue(CommandContext<ServerCommandSource> context, Setting<?> setting) throws CommandSyntaxException {
		setting.setFromArgument(context, "value");
		context.getSource().sendFeedback(new TranslatableText("Quasi-Connectivity has been %s for the %s direction", (boolean)setting.getFromArgument(context, "value") ? "enabled" : "disabled", setting.getCommandArgumentIdentifier()), false);
		return 1;
	}
}
