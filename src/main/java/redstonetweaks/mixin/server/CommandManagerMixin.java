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

import redstonetweaks.command.BugFixCommand;
import redstonetweaks.command.DelayCommand;
import redstonetweaks.command.DelayMultiplierCommand;
import redstonetweaks.command.QuasiConnectivityCommand;
import redstonetweaks.command.TweakCommand;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
	
	@Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;
	
	@Inject(method="<init>", at = @At("RETURN"))
	private void registerCommands(boolean isServer, CallbackInfo ci) {
		BugFixCommand.registerCommand(dispatcher);
		DelayCommand.registerCommand(dispatcher);
		DelayMultiplierCommand.registerCommand(dispatcher);
		QuasiConnectivityCommand.registerCommand(dispatcher);
		TweakCommand.registerCommand(dispatcher);
	}
}
