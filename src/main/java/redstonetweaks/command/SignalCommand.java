package redstonetweaks.command;

import java.util.Collection;

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
		
		Collection<Setting<?>> blockSignals = Settings.getSettings("block_signal");
		
		builder.then(CommandManager.literal("RESET").executes(context -> {
			return resetValues(context.getSource(), blockSignals);
		}));
		
		for (Setting<?> blockSignal : blockSignals) {
			builder.then(CommandManager.literal(blockSignal.getCommandIdentifier()).executes(context -> {
				return queryValue(context.getSource(), blockSignal);
			}).then(blockSignal.argument("value").suggests((context, suggestionsBuilder) -> CommandSource.suggestMatching(new String[]{blockSignal.getDefault().toString()}, suggestionsBuilder)).executes(context -> {
				return setValue(context, blockSignal);
			})));
		}
		
		dispatcher.register(builder);
	}
	
	private static int resetValues(ServerCommandSource source, Collection<Setting<?>> blockSignals) {
		for (Setting<?> blockSignal : blockSignals) {
			blockSignal.reset();
		}
		source.sendFeedback(new TranslatableText("All signal strengths have been reset to their default values"), false);
		return 1;
	}
	
	private static int queryValue(ServerCommandSource source, Setting<?> blockSignal) {
		source.sendFeedback(new TranslatableText("The %s signal strength is currently set to %s", blockSignal.getCommandIdentifier(), blockSignal.get()), false);
		return 1;
	}
	
	private static int setValue(CommandContext<ServerCommandSource> context, Setting<?> blockSignal) throws CommandSyntaxException {
		blockSignal.setFromArgument(context, "value");
		context.getSource().sendFeedback(new TranslatableText("The %s signal strength has been set to %s", blockSignal.getCommandIdentifier(), blockSignal.getFromArgument(context, "value")), false);
		return 1;
	}
}
