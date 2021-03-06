package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import redstonetweaks.command.RandomOffsetCommand;
import redstonetweaks.command.SafeStopCommand;
import redstonetweaks.command.TickCommand;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
	
	@Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;
	
	@Inject(method="<init>", at = @At("RETURN"))
	private void registerCommands(CommandManager.RegistrationEnvironment environment, CallbackInfo ci) {
		RandomOffsetCommand.registerCommand(dispatcher);
		TickCommand.registerCommand(dispatcher);
		
		if (environment == CommandManager.RegistrationEnvironment.DEDICATED) {
			SafeStopCommand.registerCommand(dispatcher);
		}
	}
}
