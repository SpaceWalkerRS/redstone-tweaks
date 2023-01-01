package redstone.tweaks.mixin.common.crush;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;

@Mixin(AnvilBlock.class)
public class AnvilBlockMixin {

	private static final Map<Block, Block> WOOL_TO_CARPET;
	private static final Map<Block, Block> CONCRETE_TO_POWDER;

	@Inject(
		method = "onLand",
		at = @At(
			value = "HEAD"
		)
	)
	private void rtCrushBlockBelow(Level level, BlockPos pos, BlockState state, BlockState oldState, FallingBlockEntity blockEntity, CallbackInfo ci) {
		BlockPos below = pos.below();
		BlockState belowState = level.getBlockState(below);
		BlockState crushedState = crush(belowState);

		if (crushedState != null) {
			level.setBlock(pos, crushedState, Block.UPDATE_ALL);
		}
	}

	private BlockState crush(BlockState state) {
		Block block = state.getBlock();
		Block crushed = null;

		if (Tweaks.Anvil.crushConcrete()) {
			crushed = CONCRETE_TO_POWDER.get(block);
		} else if (Tweaks.Anvil.crushWool()) {
			crushed = WOOL_TO_CARPET.get(block);
		}

		return crushed == null ? null : crushed.defaultBlockState();
	}

	static {

		WOOL_TO_CARPET = new HashMap<>();
		CONCRETE_TO_POWDER = new HashMap<>();

		WOOL_TO_CARPET.put(Blocks.WHITE_WOOL, Blocks.WHITE_CARPET);
		WOOL_TO_CARPET.put(Blocks.ORANGE_WOOL, Blocks.ORANGE_CARPET);
		WOOL_TO_CARPET.put(Blocks.MAGENTA_WOOL, Blocks.MAGENTA_CARPET);
		WOOL_TO_CARPET.put(Blocks.LIGHT_BLUE_WOOL, Blocks.LIGHT_BLUE_CARPET);
		WOOL_TO_CARPET.put(Blocks.YELLOW_WOOL, Blocks.YELLOW_CARPET);
		WOOL_TO_CARPET.put(Blocks.LIME_WOOL, Blocks.LIME_CARPET);
		WOOL_TO_CARPET.put(Blocks.PINK_WOOL, Blocks.PINK_CARPET);
		WOOL_TO_CARPET.put(Blocks.GRAY_WOOL, Blocks.GRAY_CARPET);
		WOOL_TO_CARPET.put(Blocks.LIGHT_GRAY_WOOL, Blocks.LIGHT_GRAY_CARPET);
		WOOL_TO_CARPET.put(Blocks.CYAN_WOOL, Blocks.CYAN_CARPET);
		WOOL_TO_CARPET.put(Blocks.PURPLE_WOOL, Blocks.PURPLE_CARPET);
		WOOL_TO_CARPET.put(Blocks.BLUE_WOOL, Blocks.BLUE_CARPET);
		WOOL_TO_CARPET.put(Blocks.BROWN_WOOL, Blocks.BROWN_CARPET);
		WOOL_TO_CARPET.put(Blocks.GREEN_WOOL, Blocks.GREEN_CARPET);
		WOOL_TO_CARPET.put(Blocks.RED_WOOL, Blocks.RED_CARPET);
		WOOL_TO_CARPET.put(Blocks.BLACK_WOOL, Blocks.BLACK_CARPET);

		CONCRETE_TO_POWDER.put(Blocks.WHITE_CONCRETE, Blocks.WHITE_CONCRETE_POWDER);
		CONCRETE_TO_POWDER.put(Blocks.ORANGE_CONCRETE, Blocks.ORANGE_CONCRETE_POWDER);
		CONCRETE_TO_POWDER.put(Blocks.MAGENTA_CONCRETE, Blocks.MAGENTA_CONCRETE_POWDER);
		CONCRETE_TO_POWDER.put(Blocks.LIGHT_BLUE_CONCRETE, Blocks.LIGHT_BLUE_CONCRETE_POWDER);
		CONCRETE_TO_POWDER.put(Blocks.YELLOW_CONCRETE, Blocks.YELLOW_CONCRETE_POWDER);
		CONCRETE_TO_POWDER.put(Blocks.LIME_CONCRETE, Blocks.LIME_CONCRETE_POWDER);
		CONCRETE_TO_POWDER.put(Blocks.PINK_CONCRETE, Blocks.PINK_CONCRETE_POWDER);
		CONCRETE_TO_POWDER.put(Blocks.GRAY_CONCRETE, Blocks.GRAY_CONCRETE_POWDER);
		CONCRETE_TO_POWDER.put(Blocks.LIGHT_GRAY_CONCRETE, Blocks.LIGHT_GRAY_CONCRETE_POWDER);
		CONCRETE_TO_POWDER.put(Blocks.CYAN_CONCRETE, Blocks.CYAN_CONCRETE_POWDER);
		CONCRETE_TO_POWDER.put(Blocks.PURPLE_CONCRETE, Blocks.PURPLE_CONCRETE_POWDER);
		CONCRETE_TO_POWDER.put(Blocks.BLUE_CONCRETE, Blocks.BLUE_CONCRETE_POWDER);
		CONCRETE_TO_POWDER.put(Blocks.BROWN_CONCRETE, Blocks.BROWN_CONCRETE_POWDER);
		CONCRETE_TO_POWDER.put(Blocks.GREEN_CONCRETE, Blocks.GREEN_CONCRETE_POWDER);
		CONCRETE_TO_POWDER.put(Blocks.RED_CONCRETE, Blocks.RED_CONCRETE_POWDER);
		CONCRETE_TO_POWDER.put(Blocks.BLACK_CONCRETE, Blocks.BLACK_CONCRETE_POWDER);

	}
}
