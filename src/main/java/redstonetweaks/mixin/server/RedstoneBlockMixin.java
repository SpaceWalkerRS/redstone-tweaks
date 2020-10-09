package redstonetweaks.mixin.server;

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
		return redstonetweaks.setting.Settings.RedstoneBlock.POWER_WEAK.get();
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
		return redstonetweaks.setting.Settings.RedstoneBlock.POWER_STRONG.get();
	}
	
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (redstonetweaks.setting.Settings.RedstoneBlock.POWER_STRONG.get() > 0) {
			redstonetweaks.setting.Settings.RedstoneBlock.BLOCK_UPDATE_ORDER.get().dispatchBlockUpdates(world, pos, state.getBlock());
		}
	}
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			if (redstonetweaks.setting.Settings.RedstoneBlock.POWER_STRONG.get() > 0) {
				redstonetweaks.setting.Settings.RedstoneBlock.BLOCK_UPDATE_ORDER.get().dispatchBlockUpdates(world, pos, state.getBlock());
			}
		}
	}
}
