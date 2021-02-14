package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(LecternBlockEntity.class)
public class LecternBlockEntityMixin {
	
	@Redirect(method = "setCurrentPage", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/LecternBlock;setPowered(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private  void onSetCurrentPageRedirectSetPowered(World world, BlockPos pos, BlockState state) {
		if (!world.getBlockTickScheduler().isTicking(pos, state.getBlock())) {
			TickSchedulerHelper.scheduleBlockTick(world, pos, state, Tweaks.Lectern.DELAY_RISING_EDGE.get(), Tweaks.Lectern.TICK_PRIORITY_RISING_EDGE.get());
		}
	}
	
	@ModifyConstant(method = "getComparatorOutput", constant = @Constant(floatValue = 14.0F))
	private float onGetComparatorOutputModify14(float oldValue) {
		return Math.max(0, Tweaks.Global.POWER_MAX.get() - 1);
	}
}
