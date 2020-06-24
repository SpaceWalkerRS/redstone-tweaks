package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.redstoneBlockSignal;
import static redstonetweaks.setting.Settings.redstoneBlocksEmitDirectSignal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

@Mixin(RedstoneBlock.class)
public class RedstoneBlockMixin {
	
	@ModifyConstant(method = "getWeakRedstonePower", constant = @Constant(intValue = 15))
	private int onGetWeakRedstonePower(int oldValue) {
		return redstoneBlockSignal.get();
	}
	
	public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
		return redstoneBlocksEmitDirectSignal.get() ? redstoneBlockSignal.get() : 0;
	}
}
