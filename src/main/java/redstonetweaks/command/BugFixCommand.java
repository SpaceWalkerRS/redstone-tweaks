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

public class BugFixCommand {
	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal("bugfix").requires(context -> {
			return context.hasPermissionLevel(2);
		});
		
		Collection<Setting<?>> bugFixes = Settings.getSettings("bug_fix");
		
		builder.then(CommandManager.literal("RESET").executes(context -> {
			return resetBugFixes(context.getSource(), bugFixes);
		}));
		
		for (Setting<?> bugFix : bugFixes) {
			builder.then(CommandManager.literal(bugFix.getCommandIdentifier()).executes(context -> {
				return queryValue(context.getSource(), bugFix);
			}).then(bugFix.argument("value").executes(context -> {
				return setValue(context, bugFix);
			})));
		}
		
		dispatcher.register(builder);
	}
	
	private static int resetBugFixes(ServerCommandSource source, Collection<Setting<?>> bugFixes) {
		for (Setting<?> bugFix : bugFixes) {
			bugFix.reset();
		}
		source.sendFeedback(new TranslatableText("All bug fixes have been disabled"), false);
		return 1;
	}
	
	private static int queryValue(ServerCommandSource source, Setting<?> bugFix) {
		source.sendFeedback(new TranslatableText("The bug fix for %s is currently %s", bugFix.getCommandIdentifier(), (boolean)bugFix.get() ? "enabled" : "disabled"), false);
		return 1;
	}
	
	private static int setValue(CommandContext<ServerCommandSource> context, Setting<?> bugFix) throws CommandSyntaxException {
		bugFix.setFromArgument(context, "value");
		context.getSource().sendFeedback(new TranslatableText("The bug fix for %s has been %s", bugFix.getCommandIdentifier(), (boolean)bugFix.getFromArgument(context, "value") ? "enabled" : "disabled"), false);
		return 1;
	}
}
