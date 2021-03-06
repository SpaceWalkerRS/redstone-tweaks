package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstonetweaks.block.piston.MotionType;
import redstonetweaks.block.piston.PistonSettings;
import redstonetweaks.helper.PistonHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.interfaces.mixin.RTIPistonBlockEntity;
import redstonetweaks.interfaces.mixin.RTIPistonHandler;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(PistonExtensionBlock.class)
public abstract class PistonExtensionBlockMixin extends AbstractBlock {
	
	public PistonExtensionBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Redirect(method = "onStateReplaced", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/PistonBlockEntity;finish()V"))
	private void onOnStateReplacedRedirectFinish(PistonBlockEntity pistonBlockEntity, BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		pistonBlockEntity.finish();
		
		if (newState.isAir()) {
			// This makes sure block entities with inventories drop their contents
			// when the moving block is destroyed, for example by an explosion
			
			BlockState movedState = ((RTIPistonBlockEntity)pistonBlockEntity).getMovedMovingState();
			BlockEntity movedBlockEntity = ((RTIPistonBlockEntity)pistonBlockEntity).getMovedMovingBlockEntity();
			
			if (movedBlockEntity != null && !(movedBlockEntity instanceof PistonBlockEntity) && movedBlockEntity.getType().supports(movedState.getBlock())) {
				WorldHelper.setBlockWithEntity(world, pos, movedState, movedBlockEntity, 18);
				world.breakBlock(pos, true);
			}
		}
	}
	
	@Redirect(method = "getDroppedStacks", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/PistonBlockEntity;getPushedBlock()Lnet/minecraft/block/BlockState;"))
	private BlockState onGetDroppedStacksRedirectGetPushedBlock(PistonBlockEntity pistonBlockEntity) {
		BlockState movedState = pistonBlockEntity.getPushedBlock();
		
		return movedState.isOf(Blocks.MOVING_PISTON) ? Blocks.AIR.getDefaultState() : movedState;
	}
	
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		tryMove(world, pos, state, false);
	}
	
	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (blockEntity instanceof PistonBlockEntity) {
			PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
			
			if (pistonBlockEntity.isSource()) {
				Direction facing = state.get(Properties.FACING);
				boolean sticky = ((RTIPistonBlockEntity)pistonBlockEntity).isSticky();
				boolean extend = type == MotionType.EXTEND || type == MotionType.EXTEND_BACKWARDS;
				
				if (!world.isClient()) {
					boolean lazy = extend ? PistonSettings.lazyRisingEdge(sticky) : PistonSettings.lazyFallingEdge(sticky);
					boolean shouldExtend = lazy ? extend : PistonHelper.isReceivingPower(world, pos, sticky, facing, true);
					
					if (extend != shouldExtend) {
						return false;
					}
				}
				
				if (Tweaks.Global.SPONTANEOUS_EXPLOSIONS.get()) {
					WorldHelper.createSpontaneousExplosion(world, pos);
					
					return true;
				}
				
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
						if (!sticky || PistonSettings.doBlockDropping() || !piston.onSyncedBlockEvent(world, pos, MotionType.RETRACT_FORWARDS, data)) {
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
		if (!world.isClient()) {
			tryMove(world, pos, state, false);
		}
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		tryMove(world, pos, state, true);
	}
	
	private void tryMove(World world, BlockPos pos, BlockState state, boolean onScheduledTick) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (blockEntity instanceof PistonBlockEntity) {
			PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
			
			if (pistonBlockEntity.isSource() && PistonHelper.isPiston(pistonBlockEntity.getPushedBlock())) {
				boolean sticky = ((RTIPistonBlockEntity)pistonBlockEntity).isSticky();
				boolean extending = pistonBlockEntity.isExtending();
				
				if (!(extending ? PistonSettings.ignoreUpdatesWhileExtending(sticky) : PistonSettings.ignoreUpdatesWhileRetracting(sticky))) {
					PistonHelper.tryMove(world, pos, state, sticky, extending, onScheduledTick);
				}
			}
		}
	}
}
