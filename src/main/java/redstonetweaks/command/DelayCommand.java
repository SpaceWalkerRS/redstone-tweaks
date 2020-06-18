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

public class DelayCommand {
	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal("delay").requires(context -> {
			return context.hasPermissionLevel(2);
		});
		
		Collection<Setting<?>> blockDelays = Settings.getSettings("block_delay");
		
		builder.then(CommandManager.literal("RESET").executes(context -> {
			return resetDelays(context.getSource(), blockDelays);
		}));
		
		for (Setting<?> blockDelay : blockDelays) {
			builder.then(CommandManager.literal(blockDelay.getCommandIdentifier()).executes(context -> {
				return queryDelay(context.getSource(), blockDelay);
			}).then(blockDelay.argument("value").executes(context -> {
				return setDelay(context, blockDelay);
			})));
		}
		
		dispatcher.register(builder);
	}
	
	private static int resetDelays(ServerCommandSource source, Collection<Setting<?>> blockDelays) {
		for (Setting<?> blockDelay : blockDelays) {
			blockDelay.reset();
		}
		source.sendFeedback(new TranslatableText("All delays have been reset to their default values"), false);
		return 1;
	}
	
	private static int queryDelay(ServerCommandSource source, Setting<?> blockDelay) {
		source.sendFeedback(new TranslatableText("The %s delay is currently set to %s", blockDelay.getCommandIdentifier(), blockDelay.get()), false);
		return 1;
	}
	
	private static int setDelay(CommandContext<ServerCommandSource> context, Setting<?> blockDelay) throws CommandSyntaxException {
		blockDelay.setFromArgument(context, "value");
		context.getSource().sendFeedback(new TranslatableText("The %s delay has been set to %s", blockDelay.getCommandIdentifier(), blockDelay.getFromArgument(context, "value")), false);
		return 1;
	}
}
