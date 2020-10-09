package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.TripwireHookBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;

import redstonetweaks.setting.Settings;

@Mixin(TripwireHookBlock.class)
public class TripwireHookBlockMixin {
	
	@Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos, T object, int oldDelay) {
		tickScheduler.schedule(pos, object, Settings.TripwireHook.DELAY.get(), Settings.TripwireHook.TICK_PRIORITY.get());
	}
	
	@ModifyConstant(method = "getWeakRedstonePower", constant = @Constant(intValue = 15))
	private int onGetWeakRedstonePower(int oldValue) {
		return Settings.TripwireHook.POWER_WEAK.get();
	}
	
	@ModifyConstant(method = "getStrongRedstonePower", constant = @Constant(intValue = 15))
	private int onGetStrongRedstonePower(int oldValue) {
		return Settings.TripwireHook.POWER_STRONG.get();
	}
	
	@Inject(method = "updateNeighborsOnAxis", cancellable = true, at = @At(value = "HEAD"))
	private void onUpdateNeighborsOnAxisInjectAtHead(World world, BlockPos pos, Direction dir, CallbackInfo ci) {
		Settings.TripwireHook.BLOCK_UPDATE_ORDER.get().dispatchBlockUpdates(world, pos, world.getBlockState(pos).getBlock(), dir.getOpposite());
		
		ci.cancel();
	}
}
