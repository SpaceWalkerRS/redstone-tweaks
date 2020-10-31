package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WeightedPressurePlateBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;

import redstonetweaks.block.AnaloguePowerComponentBlockEntity;
import redstonetweaks.interfaces.RTIPressurePlate;
import redstonetweaks.world.common.UpdateOrder;

@Mixin(WeightedPressurePlateBlock.class)
public abstract class WeightedPressurePlateBlockMixin extends AbstractPressurePlateBlock implements RTIPressurePlate, BlockEntityProvider {
	
	@Shadow @Final private int weight;
	
	protected WeightedPressurePlateBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Redirect(method = "getRedstoneOutput(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)I", at = @At(value = "FIELD", target = "Lnet/minecraft/block/WeightedPressurePlateBlock;weight:I"))
	private int getPlateWeight(WeightedPressurePlateBlock pressurePlate) {
		return pressurePlate == Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE ? redstonetweaks.setting.Settings.LightWeightedPressurePlate.WEIGHT.get() : redstonetweaks.setting.Settings.HeavyWeightedPressurePlate.WEIGHT.get();
	}
	
	@ModifyConstant(method = "getRedstoneOutput(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)I", constant = @Constant(floatValue = 15.0F))
	private float onGetRedstoneOutputModify15(float oldDelay) {
		return redstonetweaks.setting.Settings.Global.POWER_MAX.get();
	}
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 10))
	private int getWeightedPressurePlateDelay(int oldDelay) {
		return (Block)(Object)this == Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE ? redstonetweaks.setting.Settings.LightWeightedPressurePlate.DELAY_FALLING_EDGE.get() : redstonetweaks.setting.Settings.HeavyWeightedPressurePlate.DELAY_FALLING_EDGE.get();
	}
	
	@ModifyVariable(method = "setRedstoneOutput", argsOnly = true, ordinal = 0, at = @At(value = "HEAD"))
	private int onSetRedstoneOutputModifyPower(int oldValue) {
		return Math.max(oldValue, 15);
	}
	
	// We need to override this function because when the blocks
	// are initialized, and this function is called, 
	// the Redstone Tweaks settings have not yet been initialized.
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
		return state.get(Properties.POWER) > 0 ? PRESSED_SHAPE : DEFAULT_SHAPE;
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new AnaloguePowerComponentBlockEntity();
	}
	
	@Override
	public UpdateOrder updateOrder(BlockState state) {
		return isLight(state) ? redstonetweaks.setting.Settings.LightWeightedPressurePlate.BLOCK_UPDATE_ORDER.get() : redstonetweaks.setting.Settings.HeavyWeightedPressurePlate.BLOCK_UPDATE_ORDER.get();
	}
	
	@Override
	public int powerWeak(BlockView world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof AnaloguePowerComponentBlockEntity) {
			AnaloguePowerComponentBlockEntity powerBlockEntity = ((AnaloguePowerComponentBlockEntity)blockEntity);
			
			powerBlockEntity.ensureCorrectPower(state);
			return powerBlockEntity.getPower();
		}
		return state.get(Properties.POWER);
	}
	
	@Override
	public int powerStrong(BlockView world, BlockPos pos, BlockState state) {
		return powerWeak(world, pos, state);
	}
	
	@Override
	public int delayRisingEdge(BlockState state) {
		return isLight(state) ? redstonetweaks.setting.Settings.LightWeightedPressurePlate.DELAY_RISING_EDGE.get() : redstonetweaks.setting.Settings.HeavyWeightedPressurePlate.DELAY_RISING_EDGE.get();
	}
	
	@Override
	public int delayFallingEdge(BlockState state) {
		return isLight(state) ? redstonetweaks.setting.Settings.LightWeightedPressurePlate.DELAY_FALLING_EDGE.get() : redstonetweaks.setting.Settings.HeavyWeightedPressurePlate.DELAY_FALLING_EDGE.get();
	}
	
	@Override
	public TickPriority tickPriorityRisingEdge(BlockState state) {
		return isLight(state) ? redstonetweaks.setting.Settings.LightWeightedPressurePlate.TICK_PRIORITY_RISING_EDGE.get() : redstonetweaks.setting.Settings.HeavyWeightedPressurePlate.TICK_PRIORITY_RISING_EDGE.get();
	}
	
	@Override
	public TickPriority tickPriorityFallingEdge(BlockState state) {
		return isLight(state) ? redstonetweaks.setting.Settings.LightWeightedPressurePlate.TICK_PRIORITY_FALLING_EDGE.get() : redstonetweaks.setting.Settings.HeavyWeightedPressurePlate.TICK_PRIORITY_FALLING_EDGE.get();
	}
	
	private boolean isLight(BlockState state) {
		return state.isOf(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
	}
}
