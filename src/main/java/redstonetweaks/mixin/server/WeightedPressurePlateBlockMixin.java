package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

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
import net.minecraft.block.ShapeContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import redstonetweaks.helper.PressurePlateHelper;

@Mixin(WeightedPressurePlateBlock.class)
public abstract class WeightedPressurePlateBlockMixin extends AbstractPressurePlateBlock implements PressurePlateHelper {
	
	@Shadow @Final private int weight;
	
	protected WeightedPressurePlateBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Redirect(method = "getRedstoneOutput", at = @At(value = "FIELD", target = "Lnet/minecraft/block/WeightedPressurePlateBlock;weight:I"))
	private int getPlateWeight(WeightedPressurePlateBlock pressurePlate) {
		return (Block)(Object)this == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE ? HEAVY_WEIGHTED_PRESSURE_PLATE.get(WEIGHT) : LIGHT_WEIGHTED_PRESSURE_PLATE.get(WEIGHT);
	}
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 10))
	private int getWeightedPressurePlateDelay(int oldDelay) {
		return (Block)(Object)this == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE ? HEAVY_WEIGHTED_PRESSURE_PLATE.get(FALLING_DELAY) : LIGHT_WEIGHTED_PRESSURE_PLATE.get(FALLING_DELAY);
	}
	
	// We need to override this function because when the blocks
	// are initialized, and this function is called, 
	// the Redstone Tweaks settings have not yet been initialized.
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
		return state.get(Properties.POWER) > 0 ? PRESSED_SHAPE : DEFAULT_SHAPE;
	}
	
	@Override
	public int getWeakPower(BlockState state) {
		return getRedstoneOutput(state);
	}
	
	@Override
	public int getStrongPower(BlockState state) {
		return getRedstoneOutput(state);
	}
}
