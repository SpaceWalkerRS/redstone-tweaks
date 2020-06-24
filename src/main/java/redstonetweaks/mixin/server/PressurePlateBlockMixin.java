package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.stonePressurePlateSignal;
import static redstonetweaks.setting.Settings.woodenPressurePlateSignal;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

@Mixin(PressurePlateBlock.class)
public abstract class PressurePlateBlockMixin extends AbstractPressurePlateBlock {
	
	@Shadow @Final public static BooleanProperty POWERED;
	
	protected PressurePlateBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Inject(method = "getRedstoneOutput", at = @At(value = "HEAD"), cancellable = true)
	private void getRedstoneOutput(BlockState state, CallbackInfoReturnable<Integer> cir) {
		int power = state.getBlock().getMaterial(state) == Material.WOOD ? woodenPressurePlateSignal.get() : stonePressurePlateSignal.get();
		cir.setReturnValue(state.get(POWERED) ? power : 0);
		cir.cancel();
	}
	
	// We need to override this function because when the blocks
	// are initialized, this method is called, and in vanilla it
	// calls the getRedstoneOutput(BlockState), but the Redstone
	// Tweaks settings have not yet been initialized, which would
	// cause a crash.
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return state.get(POWERED) ? PRESSED_SHAPE : DEFAULT_SHAPE;
	}
}
