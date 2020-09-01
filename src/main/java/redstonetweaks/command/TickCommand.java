package redstonetweaks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

import redstonetweaks.helper.MinecraftServerHelper;
import redstonetweaks.world.server.ServerTickHandler;

public class TickCommand {
	
	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.
			literal("tick").
			requires(context -> {return context.hasPermissionLevel(2);}).
			then(CommandManager.
				literal("pause").
				executes(context -> {return pause(context.getSource());})).
			then(CommandManager.
				literal("resume").
				executes(context -> {return resume(context.getSource());})).
			then(CommandManager.
				literal("advance").
				executes(context -> {return advance(context.getSource(), 1);}).
				then(CommandManager.
					argument("count", IntegerArgumentType.integer(1, 1023)).
					executes(context -> {return advance(context.getSource(), IntegerArgumentType.getInteger(context, "count"));})));
		
		dispatcher.register(builder);
	}
	
	private static int pause(ServerCommandSource source) {
		ServerTickHandler tickHandler = ((MinecraftServerHelper)source.getMinecraftServer()).getTickHandler();
		
		if (tickHandler.isPaused()) {
			source.sendFeedback(new TranslatableText("World ticking is already paused"), false);
		} else {
			tickHandler.pause();
			source.sendFeedback(new TranslatableText("World ticking has been paused"), false);
		}
		
		return 1;
	}
	
	private static int resume(ServerCommandSource source) {
		ServerTickHandler tickHandler = ((MinecraftServerHelper)source.getMinecraftServer()).getTickHandler();
		
		if (tickHandler.isPaused()) {
			tickHandler.resume();
			source.sendFeedback(new TranslatableText("World ticking has been resumed"), false);
		} else {
			source.sendFeedback(new TranslatableText("World ticking is not paused"), false);
		}
		return 1;
	}
	
	private static int advance(ServerCommandSource source, int count) {
		ServerTickHandler tickHandler = ((MinecraftServerHelper)source.getMinecraftServer()).getTickHandler();
		
		if (tickHandler.isPaused()) {
			tickHandler.advance(count);
			source.sendFeedback(new TranslatableText("Worlds will tick %s time%s", count, count == 1 ? "" : "s"), false);
		} else {
			source.sendFeedback(new TranslatableText("Cannot advance as world ticking is not paused"), false);
		}
		return 1;
	}
}
