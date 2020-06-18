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

public class QuasiConnectivityCommand {
	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal("quasiconnectivity").requires(context -> {
			return context.hasPermissionLevel(2);
		});
		
		Collection<Setting<?>> qcSettings = Settings.getSettings("qc");
		
		for (Setting<?> qcSetting : qcSettings) {
			builder.then(CommandManager.literal(qcSetting.getCommandIdentifier()).executes(context -> {
				return queryValue(context.getSource(), qcSetting);
			}).then(qcSetting.argument("value").executes(context -> {
				return setValue(context, qcSetting);
			})));
		}
		
		dispatcher.register(builder);
	}
	
	private static int queryValue(ServerCommandSource source, Setting<?> qcSetting) {
		source.sendFeedback(new TranslatableText("Quasi-Connectivity is currently %s for the %s direction", (boolean)qcSetting.get() ? "enabled" : "disabled", qcSetting.getCommandIdentifier()), false);
		return 1;
	}
	
	private static int setValue(CommandContext<ServerCommandSource> context, Setting<?> qcSetting) throws CommandSyntaxException {
		qcSetting.setFromArgument(context, "value");
		context.getSource().sendFeedback(new TranslatableText("Quasi-Connectivity has been %s for the %s direction", (boolean)qcSetting.getFromArgument(context, "value") ? "enabled" : "disabled", qcSetting.getCommandIdentifier()), false);
		return 1;
	}
}
