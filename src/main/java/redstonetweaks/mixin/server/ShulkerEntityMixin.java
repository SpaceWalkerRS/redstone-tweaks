package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(ShulkerEntity.class)
public abstract class ShulkerEntityMixin extends Entity {
	
	public ShulkerEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}
	
	@Inject(method = "setPeekAmount", at = @At(value = "RETURN"))
	private void onSetPeekAmountInjectAtReturn(int peekAmount, CallbackInfo ci) {
		if (Tweaks.Shulker.IS_SOLID.get() && Tweaks.Shulker.UPDATE_NEIGHBORS_WHEN_PEEKING.get() && !world.isClient()) {
			BlockPos pos = getBlockPos();
			BlockState state = world.getBlockState(pos);
			
			((RTIWorld)world).dispatchShapeUpdatesAround(pos, pos, state, 3, 512);
			((RTIWorld)world).dispatchBlockUpdatesAround(pos, pos, null, state.getBlock());
		}
	}
}
