package redstonetweaks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import redstonetweaks.mixinterfaces.RTIMinecraftServer;
import redstonetweaks.packet.types.OpenMenuPacket;

public class RedstoneTweaksMenuCommand {
	
	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.
			literal("redstonetweaksmenu").
			executes(context -> openMenu(context.getSource()));
		
		dispatcher.register(builder);
	}
	
	private static int openMenu(ServerCommandSource source) {
		try {
			ServerPlayerEntity player = source.getPlayer();
			
			if (player != null) {
				((RTIMinecraftServer)source.getMinecraftServer()).getPacketHandler().sendPacketToPlayer(new OpenMenuPacket(), player);
			}
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		
		return 1;
	}
}
