package redstonetweaks.mixin.server;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstonetweaks.setting.settings.Tweaks;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {
	
	private static final Map<Block, Block> WOOL_TO_CARPET = new HashMap<>();
	private static final Map<Block, Block> CONCRETE_TO_POWDER = new HashMap<>();
	
	public FallingBlockEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}
	
	@Inject(
			method = "tick",
			cancellable = true,
			locals = LocalCapture.CAPTURE_FAILHARD,
			slice = @Slice(
					from = @At(
							value = "FIELD",
							target = "Lnet/minecraft/block/Blocks;MOVING_PISTON:Lnet/minecraft/block/Block;"
					)
			),
			at = @At(
					value = "INVOKE",
					ordinal = 0,
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/entity/FallingBlockEntity;remove()V"
			)
	)
	private void onTickInjectBeforeRemove(CallbackInfo ci, Block fallingBlock, BlockPos pos, boolean isConcretePowder, boolean isConcretePowderInWater, BlockState state) {
		if (fallingBlock instanceof AnvilBlock) {
			boolean crushConcrete = Tweaks.Anvil.CRUSH_CONCRETE.get();
			boolean crushWool = Tweaks.Anvil.CRUSH_WOOL.get();
			
			if (!crushConcrete && !crushWool) {
				return;
			}
			
			BlockPos belowPos = pos.down();
			Block block = world.getBlockState(belowPos).getBlock();
			
			Block newBlock = null;
			
			if (crushConcrete) {
				newBlock = CONCRETE_TO_POWDER.get(block);
			} else if (crushWool) {
				newBlock = WOOL_TO_CARPET.get(block);
			}
			
			if (newBlock != null) {
				world.setBlockState(belowPos, newBlock.getDefaultState(), 3);
				
				ci.cancel();
			}
		}
	}
	
	static {
		
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
