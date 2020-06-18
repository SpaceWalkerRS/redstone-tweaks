package redstonetweaks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

import redstonetweaks.setting.Settings;

public class DelayMultiplierCommand {
	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal("delaymultiplier").requires(context -> {
			return context.hasPermissionLevel(2);
		});
		
		builder.executes(context -> queryValue(context.getSource()));
		
		builder.then(Settings.delayMultiplier.argument("value").executes(context -> {
			return setValue(context);
		}));
		
		dispatcher.register(builder);
	}
	
	private static int queryValue(ServerCommandSource source) {
		source.sendFeedback(new TranslatableText("The delay multiplier is currently set to %s", (int)Settings.delayMultiplier.get()), false);
		return 1;
	}
	
	private static int setValue(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Settings.delayMultiplier.setFromArgument(context, "value");
		context.getSource().sendFeedback(new TranslatableText("The delay multiplier has been set to %s", Settings.delayMultiplier.getFromArgument(context, "value")), false);
		return 1;
	}
}
