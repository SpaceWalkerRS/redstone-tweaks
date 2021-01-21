package redstonetweaks.mixin.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockView;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;

import redstonetweaks.block.piston.BlockEventHandler;
import redstonetweaks.helper.PistonHelper;
import redstonetweaks.helper.StairsHelper;
import redstonetweaks.mixinterfaces.RTIMinecraftServer;
import redstonetweaks.mixinterfaces.RTIServerWorld;
import redstonetweaks.mixinterfaces.RTIWorld;
import redstonetweaks.packet.types.TickBlockEntityPacket;
import redstonetweaks.setting.Tweaks;
import redstonetweaks.util.RelativePos;
import redstonetweaks.world.common.AbstractNeighborUpdate;
import redstonetweaks.world.common.BlockUpdate;
import redstonetweaks.world.common.ComparatorUpdate;
import redstonetweaks.world.common.NeighborUpdate;
import redstonetweaks.world.common.ShapeUpdate;
import redstonetweaks.world.common.UpdateOrder;

@Mixin(World.class)
public abstract class WorldMixin implements RTIWorld, WorldAccess, WorldView {
	
	@Shadow @Final protected List<BlockEntity> unloadedBlockEntities;
	@Shadow @Final public List<BlockEntity> tickingBlockEntities;
	@Shadow @Final public List<BlockEntity> blockEntities;
	@Shadow @Final protected List<BlockEntity> pendingBlockEntities;
	@Shadow protected boolean iteratingTickingBlockEntities;
	
	private Map<BlockPos, BlockEntity> queuedBlockEntities;
	
	private Iterator<BlockEntity> blockEntitiesIterator;
	private Map<BlockPos, BlockEventHandler> blockEventHandlers;
	
	@Shadow public abstract Profiler getProfiler();
	@Shadow public abstract WorldChunk getWorldChunk(BlockPos pos);
	@Shadow public abstract boolean addBlockEntity(BlockEntity blockEntity);
	@Shadow public abstract void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags);
	@Shadow public abstract RegistryKey<World> getRegistryKey();
	@Shadow public abstract MinecraftServer getServer();
	@Shadow public static boolean isOutOfBuildLimitVertically(BlockPos pos) { return false; }
	@Shadow public abstract void setBlockEntity(BlockPos pos, BlockEntity blockEntity);
	@Shadow public abstract void removeBlockEntity(BlockPos pos);
	@Shadow public abstract void updateComparators(BlockPos pos, Block block);
	
	private World world() {
		return (World)(Object)this;
	}
	
	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void onInitInjectAtReturn(MutableWorldProperties properties, RegistryKey<World> registryKey, final DimensionType dimensionType, Supplier<Profiler> supplier, boolean bl, boolean bl2, long l, CallbackInfo ci) {
		blockEventHandlers = new HashMap<>();
		queuedBlockEntities = new HashMap<>();
	}
	
	// Don't display breaking particles if the block that is broken is a piston (head)
	// and it's broken during block events or the ticking of block entities
	@Redirect(method = "breakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;syncWorldEvent(ILnet/minecraft/util/math/BlockPos;I)V"))
	private void onBreakBlockRedirectSyncWorldEvent(World world, int eventId, BlockPos pos, int data) {
		if (!world.isClient() && ((RTIWorld)world).getWorldTickHandler().inWorldTick()) {
			BlockState state = world.getBlockState(pos);
			
			if (PistonHelper.isPiston(state) || state.isOf(Blocks.PISTON_HEAD)) {
				return;
			}
		}
		
		world.syncWorldEvent(eventId, pos, data);
	}
	
	@Inject(method = "updateNeighborsAlways", cancellable = true, at = @At(value = "HEAD"))
	private void onUpdateNeighborsAlwaysInjectAtHead(BlockPos pos, Block block, CallbackInfo ci) {
		if (!isClient()) {
			dispatchBlockUpdatesAround(pos, pos, null, block);
		}
		ci.cancel();
	}
	
	@Inject(method = "updateNeighbor", cancellable = true, at = @At(value = "HEAD"))
	private void onUpdateNeighborInjectAtHead(BlockPos pos, Block sourceBlock, BlockPos notifierPos, CallbackInfo ci) {
		if (!isClient()) {
			dispatchBlockUpdate(false, new BlockUpdate(pos, notifierPos, notifierPos, sourceBlock));
		}
		ci.cancel();
	}
	
	@Inject(method = "updateComparators", cancellable = true, at = @At(value = "HEAD"))
	private void onUpdateComparatorsInjectAtHead(BlockPos pos, Block block, CallbackInfo ci) {
		if (!isClient()) {
			dispatchComparatorUpdatesAround(pos, pos, null, block);
		}
		ci.cancel();
	}
	
	@Inject(method = "addSyncedBlockEvent", at = @At(value = "RETURN"))
	private void onAddSyncedBlockEventInjectAtReturn(BlockPos pos, Block block, int type, int data, CallbackInfo ci) {
		// When the world is ticking step by step the block event handler is removed
		// by the unfinished event
		if (immediateNeighborUpdates()) {
			removeBlockEventHandler(pos);
		}
	}
	
	@Inject(method = "getReceivedStrongRedstonePower", cancellable = true, at = @At(value = "HEAD"))
	private void onGetReceivedStrongRedstonePowerInjectAtHead(BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		if (Tweaks.Stairs.FULL_FACES_ARE_SOLID.get()) {
			BlockState state = getBlockState(pos);
			
			if (state.getBlock() instanceof StairsBlock) {
				cir.setReturnValue(StairsHelper.getReceivedStrongRedstonePower(world(), pos, state));
				cir.cancel();
			}
		}
	}
	
	@ModifyConstant(method = "getReceivedStrongRedstonePower", constant = @Constant(intValue = 15))
	private int onGetReceivedStrongRedstonePowerModify15(int oldValue) {
		return Tweaks.Global.POWER_MAX.get();
	}
	
	@Redirect(method = "getEmittedRedstonePower", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isSolidBlock(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onGetEmittedRedstonePowerRedirectIsSolidBlock(BlockState state, BlockView world, BlockPos pos1, BlockPos pos, Direction direction) {
		if (Tweaks.MagentaGlazedTerracotta.IS_POWER_DIODE.get() && state.isOf(Blocks.MAGENTA_GLAZED_TERRACOTTA)) {
			return state.get(Properties.HORIZONTAL_FACING) == direction;
		}
		if (Tweaks.Stairs.FULL_FACES_ARE_SOLID.get() && StairsHelper.isStairs(state)) {
			return state.isSideSolidFullSquare(world, pos, direction.getOpposite());
		}
		
		return state.isSolidBlock(world, pos);
	}
	
	@ModifyConstant(method = "getReceivedRedstonePower", constant = @Constant(intValue = 15))
	private int onGetReceivedRedstonePowerModify15(int oldValue) {
		return Tweaks.Global.POWER_MAX.get();
	}
	
	@Override
	public BlockEventHandler getBlockEventHandler(BlockPos pos) {
		return blockEventHandlers.get(pos);
	}
	
	@Override
	public boolean hasBlockEventHandler(BlockPos pos) {
		return blockEventHandlers.containsKey(pos);
	}
	
	@Override
	public boolean addBlockEventHandler(BlockEventHandler blockEventHandler) {
		return blockEventHandlers.putIfAbsent(blockEventHandler.getPos(), blockEventHandler) == null;
	}
	
	@Override
	public void removeBlockEventHandler(BlockPos pos) {
		blockEventHandlers.remove(pos);
	}
	
	@Override
	public boolean isTickingBlockEntities() {
		return iteratingTickingBlockEntities;
	}
	
	@Override
	public BlockEntity fetchQueuedBlockEntity(BlockPos pos) {
		return queuedBlockEntities.remove(pos);
	}
	
	@Override
	public void queueBlockEntityPlacement(BlockPos pos, BlockEntity blockEntity) {
		queuedBlockEntities.put(pos, blockEntity);
	}
	
	@Override
	public void startTickingBlockEntities(boolean startIterating) {
		if (!unloadedBlockEntities.isEmpty()) {
			tickingBlockEntities.removeAll(unloadedBlockEntities);
			blockEntities.removeAll(unloadedBlockEntities);
			unloadedBlockEntities.clear();
		}
		
		iteratingTickingBlockEntities = startIterating;
		blockEntitiesIterator = iteratingTickingBlockEntities ? tickingBlockEntities.iterator() : null;
	}

	@Override
	public boolean tryContinueTickingBlockEntities() {
		if (iteratingTickingBlockEntities) {
			Profiler profiler = getProfiler();
			
			if (blockEntitiesIterator.hasNext()) {
				BlockEntity blockEntity = blockEntitiesIterator.next();
				
				tickBlockEntity(blockEntity, profiler);
				
				if (!isClient()) {
					syncClientBlockEntityTickingQueue(blockEntity);
				}
				
				return true;
			} else {
				iteratingTickingBlockEntities = false;
				
				finishTickingBlockEntities(profiler);
				
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public void finishTickingBlockEntities(Profiler profiler) {
		profiler.push("pendingBlockEntities");
		
		handlePendingBlockEntities();
		
		profiler.pop();
	}
	
	@Override
	public void tickBlockEntity(BlockEntity blockEntity, Profiler profiler) {
		if (!blockEntity.isRemoved() && blockEntity.hasWorld()) {
			BlockPos pos = blockEntity.getPos();
			if (getChunkManager().shouldTickBlock(pos) && getWorldBorder().contains(pos)) {
				try {
					profiler.push(() -> {
						return String.valueOf(BlockEntityType.getId(blockEntity.getType()));
					});
					if (blockEntity.getType().supports(getBlockState(pos).getBlock())) {
						((Tickable)blockEntity).tick();
					} else {
						blockEntity.markInvalid();
					}
					
					profiler.pop();
				} catch (Throwable t) {
					CrashReport crashReport = CrashReport.create(t, "Ticking block entity");
					CrashReportSection crashReportSection = crashReport.addElement("Block entity being ticked");
					blockEntity.populateCrashReport(crashReportSection);
					throw new CrashException(crashReport);
				}
			}
		}
		
		if (blockEntity.isRemoved()) {
			if (iteratingTickingBlockEntities) {
				blockEntitiesIterator.remove();
			}
			blockEntities.remove(blockEntity);
			
			BlockPos pos = blockEntity.getPos();
			if (isChunkLoaded(pos)) {
				getWorldChunk(pos).removeBlockEntity(pos);
			}
		}
	}
	
	@Override
	public void dispatchBlockUpdates(BlockPos sourcePos, Direction sourceFacing, Block sourceBlock, UpdateOrder updateOrder) {
		for (AbstractNeighborUpdate update : updateOrder.getUpdates(sourcePos, sourceFacing)) {
			BlockPos notifierPos = update.getNotifierPos().toBlockPos(sourcePos, sourceFacing);
			
			switch (update.getMode()) {
			case NEIGHBORS:
				dispatchBlockUpdatesAround(notifierPos, sourcePos, sourceFacing, sourceBlock);
				break;
			case NEIGHBORS_EXCEPT:
				dispatchBlockUpdatesAroundExcept(notifierPos, sourcePos, sourceFacing, sourceBlock, update.getUpdatePos());
				break;
			case SINGLE_UPDATE:
				dispatchBlockUpdate(false, update.toBlockUpdate(world(), sourcePos, sourcePos, sourceFacing, sourceBlock));
				break;
			default:
				break;
			}
		}
	}
	
	@Override
	public void dispatchBlockUpdatesAround(BlockPos notifierPos, BlockPos sourcePos, Direction sourceFacing, Block sourceBlock) {
		for (AbstractNeighborUpdate update : Tweaks.Global.BLOCK_UPDATE_ORDER.get().getUpdates(notifierPos, sourceFacing)) {
			dispatchBlockUpdate(false, update.toBlockUpdate(world(), notifierPos, sourcePos, sourceFacing, sourceBlock));
		}
	}
	
	@Override
	public void dispatchBlockUpdatesAroundExcept(BlockPos notifierPos, BlockPos sourcePos, Direction sourceFacing, Block sourceBlock, RelativePos except) {
		for (AbstractNeighborUpdate update : Tweaks.Global.BLOCK_UPDATE_ORDER.get().getUpdates(notifierPos, sourceFacing)) {
			if (!update.getUpdatePos().equals(except)) {
				dispatchBlockUpdate(false, update.toBlockUpdate(world(), notifierPos, sourcePos, sourceFacing, sourceBlock));
			}
		}
	}
	
	@Override
	public void dispatchBlockUpdate(boolean scheduled, BlockUpdate blockUpdate) {
		if (Tweaks.Global.DO_BLOCK_UPDATES.get()) {
			if (scheduled || immediateNeighborUpdates()) {
				BlockPos pos = blockUpdate.getUpdatePos();
				BlockPos notifierPos = blockUpdate.getNotifierPos();
				Block sourceBlock = blockUpdate.getSourceBlock();
				
				BlockState state = getBlockState(pos);
				
				stateNeighborUpdate(state, pos, notifierPos, sourceBlock);
			} else if (!isClient()) {
				scheduleNeighborUpdate(blockUpdate);
			}
		}
	}
	
	@Override
	public void dispatchComparatorUpdatesAround(BlockPos notifierPos, BlockPos sourcePos, Direction sourceFacing, Block sourceBlock) {
		for (AbstractNeighborUpdate update : Tweaks.Global.COMPARATOR_UPDATE_ORDER.get().getUpdates(notifierPos, sourceFacing)) {
			ComparatorUpdate comparatorUpdate = update.toComparatorUpdate(world(), notifierPos, sourcePos, sourceFacing, sourceBlock);
			
			if (comparatorUpdate != null) {
				dispatchComparatorUpdate(false, comparatorUpdate);
			}
		}
	}
	
	@Override
	public void dispatchComparatorUpdate(boolean scheduled, ComparatorUpdate comparatorUpdate) {
		if (Tweaks.Global.DO_COMPARATOR_UPDATES.get()) {
			if (scheduled || immediateNeighborUpdates()) {
				BlockPos pos = comparatorUpdate.getUpdatePos();
				BlockPos notifierPos = comparatorUpdate.getNotifierPos();
				Block sourceBlock = comparatorUpdate.getSourceBlock();
				
				BlockState state = getBlockState(pos);
				
				if (state.isOf(Blocks.COMPARATOR)) {
					stateNeighborUpdate(state, pos, notifierPos, sourceBlock);
				}
			} else if (!isClient()) {
				scheduleNeighborUpdate(comparatorUpdate);
			}
		}
	}
	
	private void stateNeighborUpdate(BlockState state, BlockPos pos, BlockPos notifierPos, Block sourceBlock) {
		try {
			state.neighborUpdate(world(), pos, sourceBlock, notifierPos, false);
		} catch (Throwable t) {
			CrashReport crashReport = CrashReport.create(t, "Exception while updating neighbours");
			CrashReportSection crashReportSection = crashReport.addElement("Block being updated");
			crashReportSection.add("Source block type", () -> {
				try {
					return String.format("ID #%s (%s // %s)", Registry.BLOCK.getId(sourceBlock), sourceBlock.getTranslationKey(), sourceBlock.getClass().getCanonicalName());
				} catch (Throwable t2) {
					return "ID #" + Registry.BLOCK.getId(sourceBlock);
				}
			});
			CrashReportSection.addBlockInfo(crashReportSection, pos, state);
			
			throw new CrashException(crashReport);
		}
	}
	
	@Override
	public void dispatchShapeUpdatesAround(BlockPos notifierPos, BlockPos sourcePos, BlockState notifierState, int flags, int depth) {
		for (AbstractNeighborUpdate update : Tweaks.Global.SHAPE_UPDATE_ORDER.get().getUpdates(notifierPos, null)) {
			dispatchShapeUpdate(false, update.toShapeUpdate(world(), notifierPos, notifierPos, notifierState, flags, depth));
		}
	}
	
	@Override
	public void dispatchShapeUpdate(boolean scheduled, ShapeUpdate shapeUpdate) {
		if (Tweaks.Global.DO_SHAPE_UPDATES.get()) {
			if (scheduled || immediateNeighborUpdates()) {
				BlockPos pos = shapeUpdate.getUpdatePos();
				BlockPos notifierPos = shapeUpdate.getNotifierPos();
				BlockState notifierState = shapeUpdate.getNotifierState();
				Direction dir = shapeUpdate.getDirection();
				int flags  = shapeUpdate.getFlags();
				int depth = shapeUpdate.getDepth();
				
				BlockState state = getBlockState(pos);
				
				try {
					BlockState newState = state.getStateForNeighborUpdate(dir, notifierState, world(), pos, notifierPos);
					Block.replace(state, newState, world(), pos, flags, depth);
				} catch (Throwable t) {
					Block sourceBlock = notifierState.getBlock();
					
					CrashReport crashReport = CrashReport.create(t, "Exception while updating neighbours");
					CrashReportSection crashReportSection = crashReport.addElement("Block being updated");
					crashReportSection.add("Source block type", () -> {
						try {
							return String.format("ID #%s (%s // %s)", Registry.BLOCK.getId(sourceBlock), sourceBlock.getTranslationKey(), sourceBlock.getClass().getCanonicalName());
						} catch (Throwable var2) {
							return "ID #" + Registry.BLOCK.getId(sourceBlock);
						}
					});
					CrashReportSection.addBlockInfo(crashReportSection, pos, state);
					
					throw new CrashException(crashReport);
				}
			} else if (!isClient()) {
				scheduleNeighborUpdate(shapeUpdate);
			}
		}
		
	}
	
	private void scheduleNeighborUpdate(NeighborUpdate neighborUpdate) {
		((RTIServerWorld)this).getNeighborUpdateScheduler().schedule(neighborUpdate);
	}
	
	private void handlePendingBlockEntities() {
		if (!pendingBlockEntities.isEmpty()) {
			for (int i = 0; i < pendingBlockEntities.size(); ++i) {
				BlockEntity blockEntity = pendingBlockEntities.get(i);
				if (!blockEntity.isRemoved()) {
					if (!blockEntities.contains(blockEntity)) {
						addBlockEntity(blockEntity);
					}

					if (isChunkLoaded(blockEntity.getPos())) {
						WorldChunk worldChunk = getWorldChunk(blockEntity.getPos());
						BlockState blockState = worldChunk.getBlockState(blockEntity.getPos());
						worldChunk.setBlockEntity(blockEntity.getPos(), blockEntity);
						updateListeners(blockEntity.getPos(), blockState, blockState, 3);
					}
				}
			}

			pendingBlockEntities.clear();
		}
	}
	
	private void syncClientBlockEntityTickingQueue(BlockEntity blockEntity) {
		TickBlockEntityPacket packet = new TickBlockEntityPacket(blockEntity.getPos());
		((RTIMinecraftServer)getServer()).getPacketHandler().sendPacketToDimension(packet, getRegistryKey());
	}
}
