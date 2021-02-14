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
import redstonetweaks.setting.settings.ServerConfig;
import redstonetweaks.world.server.ServerWorldTickHandler;

public class TickCommand {
	
	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.
			literal("tick").
			requires(source -> canUseTickCommand(source)).
			then(CommandManager.
				literal("pause").
				executes(context -> pause(context.getSource()))).
			then(CommandManager.
				literal("resume").
				executes(context -> resume(context.getSource()))).
			then(CommandManager.
				literal("advance").
				executes(context -> advance(context.getSource(), 1)).
				then(CommandManager.
					argument("count", IntegerArgumentType.integer(1, 1023)).
					executes(context -> advance(context.getSource(), IntegerArgumentType.getInteger(context, "count")))));
		
		dispatcher.register(builder);
	}
	
	private static int pause(ServerCommandSource source) {
		ServerWorldTickHandler worldTickHandler = ((RTIMinecraftServer)source.getMinecraftServer()).getWorldTickHandler();
		
		if (worldTickHandler.doWorldTicks()) {
			worldTickHandler.pause();
			source.sendFeedback(new TranslatableText("World ticking has been paused"), false);
		} else {
			source.sendFeedback(new TranslatableText("World ticking is already paused"), false);
		}
		
		return 1;
	}
	
	private static int resume(ServerCommandSource source) {
		ServerWorldTickHandler worldTickHandler = ((RTIMinecraftServer)source.getMinecraftServer()).getWorldTickHandler();
		
		if (worldTickHandler.doWorldTicks()) {
			source.sendFeedback(new TranslatableText("World ticking is not paused"), false);
		} else {
			worldTickHandler.resume();
			source.sendFeedback(new TranslatableText("World ticking has been resumed"), false);
		}
		
		return 1;
	}
	
	private static int advance(ServerCommandSource source, int count) {
		ServerWorldTickHandler worldTickHandler = ((RTIMinecraftServer)source.getMinecraftServer()).getWorldTickHandler();
		
		if (worldTickHandler.doWorldTicks()) {
			source.sendFeedback(new TranslatableText("Cannot advance as world ticking is not paused"), false);
		} else {
			worldTickHandler.advance(count);
			source.sendFeedback(new TranslatableText("Worlds will tick %s %s", count, count == 1 ? "time" : "times"), false);
		}
		
		return 1;
	}
	
	private static boolean canUseTickCommand(ServerCommandSource source) {
		try {
			ServerPlayerEntity player = source.getPlayer();
			
			return player.hasPermissionLevel(2) || ServerConfig.Permissions.TICK_COMMAND.get();
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
