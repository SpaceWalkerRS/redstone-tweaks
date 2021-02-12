package redstonetweaks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

import redstonetweaks.interfaces.mixin.RTIMinecraftServer;

public class SafeStopCommand {
	
	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.
			literal("safestop").
			requires(source -> source.hasPermissionLevel(4)).
			executes(context -> scheduleServerStop(context.getSource())).
			then(CommandManager.
				literal("cancel").
				executes(context -> cancelServerStop(context.getSource())));
		
		dispatcher.register(builder);
	}
	
	private static int scheduleServerStop(ServerCommandSource source) {
		((RTIMinecraftServer)source.getMinecraftServer()).getWorldTickHandler().scheduleStop();
		
		source.sendFeedback(new TranslatableText("The server will stop once the current tick has been completed"), true);
		
		return 1;
	}
	
	private static int cancelServerStop(ServerCommandSource source) {
		((RTIMinecraftServer)source.getMinecraftServer()).getWorldTickHandler().cancelStop();
		
		source.sendFeedback(new TranslatableText("The scheduled server stop has been cancelled"), true);
		
		return 1;
	}
}
