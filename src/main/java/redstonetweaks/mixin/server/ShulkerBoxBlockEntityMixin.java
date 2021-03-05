package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;

import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityMixin extends BlockEntity {
	
	public ShulkerBoxBlockEntityMixin(BlockEntityType<?> type) {
		super(type);
	}
	
	@Inject(method = "updateNeighborStates", at = @At(value = "HEAD"))
	private void onUpdateNeighborStatesInjectAtHead(CallbackInfo ci) {
		if (Tweaks.ShulkerBox.UPDATE_NEIGHBORS_WHEN_PEEKING.get() && !world.isClient()) {
			((RTIWorld)world).dispatchBlockUpdatesAround(pos, pos, null, getCachedState().getBlock());
		}
	}
}
