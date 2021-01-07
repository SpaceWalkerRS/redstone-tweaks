package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstonetweaks.block.piston.MotionType;
import redstonetweaks.block.piston.PistonSettings;
import redstonetweaks.helper.PistonHelper;
import redstonetweaks.mixinterfaces.RTIPistonBlockEntity;
import redstonetweaks.mixinterfaces.RTIPistonHandler;

@Mixin(PistonExtensionBlock.class)
public class PistonExtensionBlockMixin extends Block {
	
	public PistonExtensionBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		Direction facing = state.get(Properties.FACING);
		
		if (!world.isClient()) {
			if (type == MotionType.EXTEND && !PistonHelper.isReceivingPower(world, pos, state, facing)) {
				return false;
			}
		}
		
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (blockEntity instanceof PistonBlockEntity) {
			PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
			
			if (pistonBlockEntity.isSource() && !pistonBlockEntity.isExtending()) {
				((RTIPistonBlockEntity)pistonBlockEntity).finishSource();
				
				if (((RTIPistonBlockEntity)pistonBlockEntity).isMovedByStickyPiston() && PistonSettings.fastBlockDropping()) {
					if (PistonSettings.superBlockDropping()) {
						PistonHandler pistonHandler = new PistonHandler(world, pos, facing, false);
						
						for (BlockPos blockPos : ((RTIPistonHandler)pistonHandler).getMovingStructure()) {
							blockEntity = world.getBlockEntity(blockPos);
							
							if (blockEntity instanceof PistonBlockEntity) {
								((RTIPistonBlockEntity)blockEntity).finishSource();
							}
						}
					} else {
						BlockPos frontPos = pos.offset(facing);
						BlockState frontState = world.getBlockState(frontPos);
						
						if (frontState.isOf(Blocks.MOVING_PISTON) && frontState.get(Properties.FACING) == facing) {
							blockEntity = world.getBlockEntity(frontPos);
							
							if (blockEntity instanceof PistonBlockEntity) {
								((RTIPistonBlockEntity)blockEntity).finishSource();
							}
						}
					}
				}
				
				BlockState piston = world.getBlockState(pos);
				
				if (PistonHelper.isPiston(piston)) {
					if (!piston.onSyncedBlockEvent(world, pos, MotionType.EXTEND, data)) {
						piston.onSyncedBlockEvent(world, pos, MotionType.EXTEND_BACKWARDS, data);
					}
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (blockEntity instanceof PistonBlockEntity) {
			PistonBlockEntity pistonBlockEntity = (PistonBlockEntity) blockEntity;
			
			if (pistonBlockEntity.isSource() && !pistonBlockEntity.isExtending()) {
				boolean sticky = ((RTIPistonBlockEntity)pistonBlockEntity).isMovedByStickyPiston();
				Direction facing = pistonBlockEntity.getFacing();
				
				if (!PistonSettings.ignoreUpdatesWhileRetracting(sticky) && PistonHelper.isReceivingPower(world, pos, state, facing)) {
					if (!world.isClient()) {
						world.addSyncedBlockEvent(pos, state.getBlock(), MotionType.EXTEND, facing.getId());
					}
				}
			}
		}
	}
}
