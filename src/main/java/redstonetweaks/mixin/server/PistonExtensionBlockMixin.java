package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstonetweaks.helper.PistonHelper;
import redstonetweaks.interfaces.RTIBlock;
import redstonetweaks.interfaces.RTIPistonBlockEntity;

@Mixin(PistonExtensionBlock.class)
public class PistonExtensionBlockMixin extends Block implements RTIBlock {
	
	public PistonExtensionBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof PistonBlockEntity) {
			PistonBlockEntity pistonBlockEntity = (PistonBlockEntity) blockEntity;
			if (pistonBlockEntity.isSource() && !pistonBlockEntity.isExtending()) {
				boolean sticky = ((RTIPistonBlockEntity)pistonBlockEntity).isMovedByStickyPiston();
				Direction facing = state.get(Properties.FACING);
				if (!PistonHelper.ignoreUpdatesWhileRetracting(sticky) && (PistonHelper.lazyRisingEdge(sticky) || PistonHelper.isReceivingPower(world, pos, state, facing))) {
					BlockState pushedState = pistonBlockEntity.getPushedBlock();
					if (pushedState.getBlock() instanceof PistonBlock) {
						((RTIPistonBlockEntity)pistonBlockEntity).finishSource();
						if (sticky && PistonHelper.fastBlockDropping()) {
							BlockPos frontPos = pos.offset(facing);
							BlockState frontState = world.getBlockState(frontPos);
							if (frontState.isOf(Blocks.MOVING_PISTON) && frontState.get(Properties.FACING) == facing) {
								blockEntity = world.getBlockEntity(frontPos);
								if (blockEntity instanceof PistonBlockEntity) {
									((PistonBlockEntity) blockEntity).finish();
								}
							}
						}
						pushedState.onSyncedBlockEvent(world, pos, type, data);
						
						return true;
					}
				}
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
				boolean sticky = state.get(Properties.PISTON_TYPE) == PistonType.STICKY;
				Direction facing = state.get(Properties.FACING);
				if (!PistonHelper.ignoreUpdatesWhileRetracting(sticky) && PistonHelper.isReceivingPower(world, pos, state, facing)) {
					if (!world.isClient()) {
						world.addSyncedBlockEvent(pos, state.getBlock(), 0, facing.getId());
					}
				}
			}
		}
	}
	
	@Override
	public boolean continueEvent(World world, BlockState state, BlockPos pos, int type) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof PistonBlockEntity) {
			PistonBlockEntity pistonBlockEntity = (PistonBlockEntity) blockEntity;
			
			if (pistonBlockEntity.isSource()) {
				BlockState pistonState = pistonBlockEntity.getPushedBlock();
				((RTIBlock)pistonState.getBlock()).continueEvent(world, pistonState, pos, type);

				return true;
			}
		}
		
		return false;
	}
}
