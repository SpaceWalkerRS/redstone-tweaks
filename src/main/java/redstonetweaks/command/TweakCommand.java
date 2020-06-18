package redstonetweaks.command;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

import redstonetweaks.setting.Setting;
import redstonetweaks.setting.Settings;

public class TweakCommand {
	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal("tweak").requires(context -> {
			return context.hasPermissionLevel(2);
		});
		
		Collection<Setting<?>> tweaks = Settings.getSettings("tweak");
		
		builder.then(CommandManager.literal("RESET").executes(context -> {
			return resetTweaks(context.getSource(), tweaks);
		}));
		
		for (Setting<?> tweak : tweaks) {
			builder.then(CommandManager.literal(tweak.getCommandIdentifier()).executes(context -> {
				return queryValue(context.getSource(), tweak);
			}).then(tweak.argument("value").executes(context -> {
				return setValue(context, tweak);
			})));
		}
		
		dispatcher.register(builder);
	}
	
	private static int resetTweaks(ServerCommandSource source, Collection<Setting<?>> tweaks) {
		for (Setting<?> tweak : tweaks) {
			tweak.reset();
		}
		source.sendFeedback(new TranslatableText("All settings have been reset to their default values"), false);
		return 1;
	}
	
	private static int queryValue(ServerCommandSource source, Setting<?> tweak) {
		source.sendFeedback(new TranslatableText("The %s setting is currently set to %s", tweak.getCommandIdentifier(), tweak.get()), false);
		return 1;
	}
	
	private static int setValue(CommandContext<ServerCommandSource> context, Setting<?> tweak) throws CommandSyntaxException {
		tweak.setFromArgument(context, "value");
		context.getSource().sendFeedback(new TranslatableText("The %s setting has been set to %s", tweak.getCommandIdentifier(), tweak.getFromArgument(context, "value")), false);
		return 1;
	}
}
