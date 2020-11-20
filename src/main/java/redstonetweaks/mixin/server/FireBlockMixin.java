package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import redstonetweaks.setting.Tweaks;

@Mixin(FireBlock.class)
public class FireBlockMixin {
	
	@Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerTickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onScheduledTickRedirectSchedule(ServerTickScheduler<T> tickScheduler, BlockPos pos1, T block, int delay, BlockState state, ServerWorld world, BlockPos pos, Random random) {
		tickScheduler.schedule(pos, block, delay, Tweaks.Fire.TICK_PRIORITY.get());
	}
	
	@ModifyConstant(method = "method_26155", constant = @Constant(intValue = 30))
	private static int onMethod_26155Modify30(int oldValue) {
		return Tweaks.Fire.DELAY_MIN.get();
	}
	
	@ModifyConstant(method = "method_26155", constant = @Constant(intValue = 10))
	private static int onMethod_26155Modify10(int oldValue) {
		int min = Tweaks.Fire.DELAY_MIN.get();
		int max = Tweaks.Fire.DELAY_MAX.get();
		
		return min > max ? min : max - min;
	}
}
