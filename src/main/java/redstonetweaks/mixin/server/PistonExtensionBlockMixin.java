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
		boolean sticky = data == 1;
		boolean extend = type == MotionType.EXTEND || type == MotionType.EXTEND_BACKWARDS;
		
		if (!world.isClient()) {
			boolean lazy = extend ? PistonSettings.lazyRisingEdge(sticky) : PistonSettings.lazyFallingEdge(sticky);
			boolean shouldExtend = lazy ? extend : PistonHelper.isReceivingPower(world, pos, state, facing, true);
			
			if (extend != shouldExtend) {
				return false;
			}
		}
		
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (blockEntity instanceof PistonBlockEntity) {
			PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
			
			if (pistonBlockEntity.isSource()) {
				((RTIPistonBlockEntity)pistonBlockEntity).finishSource();
				
				if (extend) {
					if (sticky && PistonSettings.fastBlockDropping()) {
						if (PistonSettings.superBlockDropping()) {
							PistonHandler pistonHandler = PistonHelper.createPistonHandler(world, pos, facing, false, sticky);
							
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
				}
				
				BlockState piston = world.getBlockState(pos);
				
				if (PistonHelper.isPiston(piston)) {
					data = facing.getId();
					
					if (extend) {
						piston.onSyncedBlockEvent(world, pos, type, data);
					} else {
						if (!piston.onSyncedBlockEvent(world, pos, MotionType.RETRACT_FORWARDS, data)) {
							piston.onSyncedBlockEvent(world, pos, MotionType.RETRACT_A, data);
						}
					}
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (world.isClient()) {
			return;
		}
		
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (blockEntity instanceof PistonBlockEntity) {
			PistonBlockEntity pistonBlockEntity = (PistonBlockEntity) blockEntity;
			
			if (pistonBlockEntity.isSource()) {
				boolean sticky = ((RTIPistonBlockEntity)pistonBlockEntity).isSticky();
				boolean extending = pistonBlockEntity.isExtending();
				
				if (!(extending ? PistonSettings.ignoreUpdatesWhileExtending(sticky) : PistonSettings.ignoreUpdatesWhileRetracting(sticky))) {
					PistonHelper.tryMove(world, pos, state, sticky, extending, false);
				}
			}
		}
	}
}
