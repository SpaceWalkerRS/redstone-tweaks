package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ComparatorBlockEntity;

import redstonetweaks.helper.BlockEntityHelper;
import redstonetweaks.helper.ServerWorldHelper;

@Mixin(ComparatorBlockEntity.class)
public abstract class ComparatorBlockEntityMixin extends BlockEntity implements BlockEntityHelper {
	
	// 1-14 are used by vanilla block entities
	// In case they add more, just make the number sufficiently large
	private static final int ID = 1002;
	
	public ComparatorBlockEntityMixin(BlockEntityType<?> type) {
		super(type);
	}
	
	@Inject(method = "setOutputSignal", at = @At(value = "RETURN"))
	private void onSetOutputSignalInjectAtReturn(int newPower, CallbackInfo ci) {
		if (!world.isClient()) {
			((ServerWorldHelper)world).markForBlockEntityUpdate(pos);
		}
	}
	
	@Override
	public int getId() {
		return ID;
	}
}
