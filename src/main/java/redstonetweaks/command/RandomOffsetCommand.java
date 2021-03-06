package redstonetweaks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.player.PermissionManager;
import redstonetweaks.world.server.ServerWorldTickHandler;

public class RandomOffsetCommand {
	
	private static final String INFO = "";
	
	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.
			literal("randomoffset").
			requires(source -> canUseRandomOffsetCommand(source)).
			executes(context -> info(context.getSource())).
			then(CommandManager.
				literal("start").
				then(CommandManager.
					argument("interval", IntegerArgumentType.integer(0)).
					executes(context -> start(context.getSource(), IntegerArgumentType.getInteger(context, "interval"))))).
			then(CommandManager.
				literal("stop").
				executes(context -> stop(context.getSource())));
		
		dispatcher.register(builder);
	}
	
	private static int info(ServerCommandSource source) {
		source.sendFeedback(new TranslatableText(INFO), false);
		
		return 1;
	}
	
	private static int start(ServerCommandSource source, int interval) {
		ServerWorldTickHandler worldTickHandler = ((RTIMinecraftServer)source.getMinecraftServer()).getWorldTickHandler();
		
		if (worldTickHandler.isRandomizingOffset()) {
			source.sendFeedback(new TranslatableText(String.format("Updated interval to %d", interval)), false);
		} else {
			if (interval == 0) {
				source.sendFeedback(new TranslatableText("Started randomizing position offset"), false);
			} else {
				source.sendFeedback(new TranslatableText(String.format("Started randomizing position offset every %d ticks", interval)), false);
			}
		}
		
		worldTickHandler.startRandomizingOffset(interval);
		
		return 1;
	}
	
	private static int stop(ServerCommandSource source) {
		ServerWorldTickHandler worldTickHandler = ((RTIMinecraftServer)source.getMinecraftServer()).getWorldTickHandler();
		
		if (worldTickHandler.isRandomizingOffset()) {
			source.sendFeedback(new TranslatableText("Stopped randomizing position offset"), false);
		} else {
			source.sendFeedback(new TranslatableText("The position offset wasn't being randomized!"), false);
		}
		
		worldTickHandler.stopRandomizingOffset();
		
		return 1;
	}
	
	private static boolean canUseRandomOffsetCommand(ServerCommandSource source) {
		try {
			ServerPlayerEntity player = source.getPlayer();
			
			return PermissionManager.canUseRandomOffsetCommand(player);
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
