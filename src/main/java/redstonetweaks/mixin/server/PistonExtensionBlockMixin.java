package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstonetweaks.helper.BlockHelper;
import redstonetweaks.helper.PistonHelper;

@Mixin(PistonExtensionBlock.class)
public class PistonExtensionBlockMixin extends Block implements BlockHelper {
	
	public PistonExtensionBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof PistonBlockEntity) {
			PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
			if (pistonBlockEntity.isSource() && !pistonBlockEntity.isExtending()) {
				boolean sticky = state.get(Properties.PISTON_TYPE) == PistonType.STICKY;
				Direction facing = state.get(Properties.FACING);
				if (!PistonHelper.ignoreUpdatesWhileRetracting(sticky) && PistonHelper.isReceivingPower(world, pos, state, facing)) {
					pistonBlockEntity.finish();
					world.setBlockState(pos, pistonBlockEntity.getPushedBlock(), 67);
					
					if (sticky) {
						BlockPos frontPos = pos.offset(facing);
						BlockState frontState = world.getBlockState(frontPos);
						if (frontState.isOf(Blocks.MOVING_PISTON) && frontState.get(Properties.FACING) == facing) {
							blockEntity = world.getBlockEntity(frontPos);
							if (blockEntity instanceof PistonBlockEntity) {
								((PistonBlockEntity)blockEntity).finish();
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public boolean continueEvent(World world, BlockState state, BlockPos pos, int type) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof PistonBlockEntity) {
			PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
			
			if (pistonBlockEntity.isSource()) {
				BlockState piston = pistonBlockEntity.getPushedBlock();
				((BlockHelper)piston.getBlock()).continueEvent(world, piston, pos, type);
				
				return true;
			}
		}
		
		return false;
	}
}
