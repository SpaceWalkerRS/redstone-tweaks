package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.heavyWeightedPressurePlateDelay;
import static redstonetweaks.setting.Settings.heavyWeightedPressurePlateWeight;
import static redstonetweaks.setting.Settings.lightWeightedPressurePlateDelay;
import static redstonetweaks.setting.Settings.lightWeightedPressurePlateWeight;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WeightedPressurePlateBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

@Mixin(WeightedPressurePlateBlock.class)
public abstract class WeightedPressurePlateBlockMixin extends AbstractPressurePlateBlock {

	@Shadow @Final public static IntProperty POWER;
	@Shadow @Final private int weight;
	
	protected WeightedPressurePlateBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Redirect(method = "getRedstoneOutput", at = @At(value = "FIELD", target = "Lnet/minecraft/block/WeightedPressurePlateBlock;weight:I"))
	private int getPlateWeight(WeightedPressurePlateBlock pressurePlate) {
		return (Block)(Object)this == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE ? heavyWeightedPressurePlateWeight.get() : lightWeightedPressurePlateWeight.get();
	}
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 10))
	private int getWeightedPressurePlateDelay(int oldDelay) {
		return (Block)(Object)this == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE ? heavyWeightedPressurePlateDelay.get() : lightWeightedPressurePlateDelay.get();
	}
	
	// We need to override this function because when the blocks
	// are initialized, and this function is called, 
	// the Redstone Tweaks settings have not yet been initialized.
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return state.get(POWER) > 0 ? PRESSED_SHAPE : DEFAULT_SHAPE;
	}
}
