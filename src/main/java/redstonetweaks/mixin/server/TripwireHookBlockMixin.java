package redstonetweaks.mixin.server;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.TripwireHookBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(TripwireHookBlock.class)
public class TripwireHookBlockMixin {
	
	@Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int oldDelay, World world, BlockPos pos, BlockState state, boolean beingRemoved, boolean bl, int i, @Nullable BlockState blockState) {
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, Tweaks.TripwireHook.DELAY.get(), Tweaks.TripwireHook.TICK_PRIORITY.get());
	}
	
	@ModifyConstant(method = "getWeakRedstonePower", constant = @Constant(intValue = 15))
	private int onGetWeakRedstonePower(int oldValue) {
		return Tweaks.TripwireHook.POWER_WEAK.get();
	}
	
	@ModifyConstant(method = "getStrongRedstonePower", constant = @Constant(intValue = 15))
	private int onGetStrongRedstonePower(int oldValue) {
		return Tweaks.TripwireHook.POWER_STRONG.get();
	}
	
	@Inject(method = "updateNeighborsOnAxis", cancellable = true, at = @At(value = "HEAD"))
	private void onUpdateNeighborsOnAxisInjectAtHead(World world, BlockPos pos, Direction dir, CallbackInfo ci) {
		((RTIWorld)world).dispatchBlockUpdates(pos, dir.getOpposite(), world.getBlockState(pos).getBlock(), Tweaks.TripwireHook.BLOCK_UPDATE_ORDER.get());
		
		ci.cancel();
	}
}
