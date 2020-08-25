package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@Mixin(RedstoneBlock.class)
public abstract class RedstoneBlockMixin extends AbstractBlock {
	
	public RedstoneBlockMixin(Settings settings) {
		super(settings);
	}

	@ModifyConstant(method = "getWeakRedstonePower", constant = @Constant(intValue = 15))
	private int onGetWeakRedstonePower(int oldValue) {
		return REDSTONE_BLOCK.get(WEAK_POWER);
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
		return REDSTONE_BLOCK.get(STRONG_POWER);
	}
	
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (REDSTONE_BLOCK.get(STRONG_POWER) > 0) {
			for (Direction direction : Direction.values()) {
				BlockPos neighborPos = pos.offset(direction);
				world.updateNeighborsExcept(neighborPos, (RedstoneBlock)(Object)this, direction.getOpposite());
			}
		}
	}
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			if (REDSTONE_BLOCK.get(STRONG_POWER) > 0) {
				for (Direction direction : Direction.values()) {
					BlockPos neighborPos = pos.offset(direction);
					world.updateNeighborsExcept(neighborPos, (RedstoneBlock)(Object)this, direction.getOpposite());
				}
			}
		}
	}
}
