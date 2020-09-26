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
import redstonetweaks.command.TickCommand;
import redstonetweaks.command.TweakCommand;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
	
	@Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;
	
	@Inject(method="<init>", at = @At("RETURN"))
	private void registerCommands(CommandManager.RegistrationEnvironment environment, CallbackInfo ci) {
		//TweakCommand.registerCommand(dispatcher);
		TickCommand.registerCommand(dispatcher);
	}
}
