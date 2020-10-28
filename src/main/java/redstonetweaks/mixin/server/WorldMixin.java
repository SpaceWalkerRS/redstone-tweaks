package redstonetweaks.mixin.server;

import java.util.ArrayList;
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
import net.minecraft.block.BlockEntityProvider;
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
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockView;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;

import redstonetweaks.block.piston.BlockEventHandler;
import redstonetweaks.helper.MinecraftServerHelper;
import redstonetweaks.helper.ServerWorldHelper;
import redstonetweaks.helper.StairsHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.packet.TickBlockEntityPacket;
import redstonetweaks.setting.Settings;
import redstonetweaks.world.server.ScheduledNeighborUpdate.UpdateType;

@Mixin(World.class)
public abstract class WorldMixin implements WorldHelper, WorldAccess, WorldView {
	
	@Shadow @Final protected List<BlockEntity> unloadedBlockEntities;
	@Shadow @Final public List<BlockEntity> tickingBlockEntities;
	@Shadow @Final public List<BlockEntity> blockEntities;
	@Shadow @Final protected List<BlockEntity> pendingBlockEntities;
	@Shadow protected boolean iteratingTickingBlockEntities;
	
	private Iterator<BlockEntity> blockEntitiesIterator;
	private List<BlockEntity> pendingMovedBlockEntities;
	private Map<BlockPos, BlockEventHandler> blockEventHandlers;
	
	@Shadow public abstract Profiler getProfiler();
	@Shadow public abstract WorldChunk getWorldChunk(BlockPos pos);
	@Shadow public abstract boolean addBlockEntity(BlockEntity blockEntity);
	@Shadow public abstract void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags);
	@Shadow public abstract RegistryKey<World> getRegistryKey();
	@Shadow public abstract MinecraftServer getServer();
	@Shadow public static boolean isHeightInvalid(BlockPos pos) { return false; }
	@Shadow public abstract void setBlockEntity(BlockPos pos, BlockEntity blockEntity);
	@Shadow public abstract void removeBlockEntity(BlockPos pos);
	@Shadow public abstract void updateComparators(BlockPos pos, Block block);
	
	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void onInitInjectAtReturn(MutableWorldProperties properties, RegistryKey<World> registryKey, final DimensionType dimensionType, Supplier<Profiler> supplier, boolean bl, boolean bl2, long l, CallbackInfo ci) {
		pendingMovedBlockEntities = new ArrayList<>();
		blockEventHandlers = new HashMap<>();
	}
	
	// Don't display breaking particles if the block that is broken is a piston head
	// and it's broken during block events or the ticking of block entities
	@Redirect(method = "breakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;syncWorldEvent(ILnet/minecraft/util/math/BlockPos;I)V"))
	private void onBreakBlockRedirectSyncWorldEvent(World world, int eventId, BlockPos pos, int data) {
		if (world.isClient() || !(iteratingTickingBlockEntities || ((ServerWorldHelper)world).isProcessingBlockEvents()) || !world.getBlockState(pos).isOf(Blocks.PISTON_HEAD)) {
			world.syncWorldEvent(eventId, pos, data);
		}
	}
	
	@Inject(method = "updateNeighborsAlways", cancellable = true, at = @At(value = "HEAD"))
	private void onUpdateNeighborsAlwaysInjectAtHead(BlockPos pos, Block block, CallbackInfo ci) {
		Settings.Global.BLOCK_UPDATE_ORDER.get().dispatchBlockUpdates((World)(Object)this, pos, block);
		
		ci.cancel();
	}
	
	@Redirect(method = "updateNeighbor", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;neighborUpdate(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;Z)V"))
	private void onUpdateNeighborRedirectNeighborUpdate(BlockState blockState, World world, BlockPos pos, Block sourceBlock, BlockPos notifierPos, boolean notify) {
		if (Settings.Global.DO_BLOCK_UPDATES.get()) {
			if (updateNeighborsNormally()) {
				blockState.neighborUpdate(world, pos, sourceBlock, notifierPos, notify);
			} else {
				if (!world.isClient()) {
					((ServerWorldHelper)world).getNeighborUpdateScheduler().schedule(pos, notifierPos, null, UpdateType.BLOCK_UPDATE);
				}
			}
		}
	}
	
	@Redirect(method = "updateComparators", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;neighborUpdate(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;Z)V"))
	private void onUpdateComparatorsRedirectNeighborUpdate(BlockState blockState, World world, BlockPos pos, Block sourceBlock, BlockPos notifierPos, boolean notify) {
		if (Settings.Global.DO_COMPARATOR_UPDATES.get()) {
			if (updateNeighborsNormally()) {
				blockState.neighborUpdate(world, pos, sourceBlock, notifierPos, notify);
			} else {
				if (!world.isClient) {
					((ServerWorldHelper)world).getNeighborUpdateScheduler().schedule(pos, notifierPos, null, UpdateType.COMPARATOR_UPDATE);
				}
			}
		}
	}
	
	@Inject(method = "tickBlockEntities", at = @At(value = "RETURN"))
	private void onTickBlockEntitiesInjectAtReturn(CallbackInfo ci) {
		handlePendingMovedBlockEntities();
	}
	
	@Inject(method = "getReceivedStrongRedstonePower", cancellable = true, at = @At(value = "HEAD"))
	private void onGetReceivedStrongRedstonePowerInjectAtHead(BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		if (Settings.Stairs.FULL_FACES_ARE_SOLID.get()) {
			BlockState state = getBlockState(pos);
			if (state.getBlock() instanceof StairsBlock) {
				cir.setReturnValue(StairsHelper.getReceivedStrongRedstonePower((World)(Object)this, pos, state));
				cir.cancel();
			}
		}
	}
	
	@ModifyConstant(method = "getReceivedStrongRedstonePower", constant = @Constant(intValue = 15))
	private int onGetReceivedStrongRedstonePowerModify15(int oldValue) {
		return Settings.Global.POWER_MAX.get();
	}
	
	@Redirect(method = "getEmittedRedstonePower", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isSolidBlock(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onGetEmittedRedstonePowerRedirectIsSolidBlock(BlockState state, BlockView world, BlockPos pos1, BlockPos pos, Direction direction) {
		if (Settings.MagentaGlazedTerracotta.IS_POWER_DIODE.get()) {
			if (state.isOf(Blocks.MAGENTA_GLAZED_TERRACOTTA)) {
				return state.get(Properties.HORIZONTAL_FACING) == direction;
			}
		}
		if (Settings.Stairs.FULL_FACES_ARE_SOLID.get()) {
			if (state.getBlock() instanceof StairsBlock) {
				return state.isSideSolidFullSquare(world, pos, direction.getOpposite());
			}
		}
		return state.isSolidBlock(world, pos);
	}
	
	@ModifyConstant(method = "getReceivedRedstonePower", constant = @Constant(intValue = 15))
	private int onGetReceivedRedstonePowerModify15(int oldValue) {
		return Settings.Global.POWER_MAX.get();
	}
	
	@Override
	public void addMovedBlockEntity(BlockPos pos, BlockEntity blockEntity) {
		blockEntity.cancelRemoval();
		blockEntity.setLocation((World)(Object)this, pos);
		
		if (iteratingTickingBlockEntities) {
			for (BlockEntity pendingBlockEntity : pendingMovedBlockEntities) {
				if (pendingBlockEntity.getPos().equals(pos)) {
					return;
				}
			}
			pendingMovedBlockEntities.add(blockEntity);
		} else {
			setMovedBlockEntity(pos, blockEntity);
		}
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
	public BlockEventHandler getBlockEventHandler(BlockPos pos) {
		return blockEventHandlers.get(pos);
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
		handlePendingMovedBlockEntities();
		
		profiler.pop();
	}
	
	@Override
	public void tickBlockEntity(BlockEntity blockEntity, Profiler profiler) {
		if (!blockEntity.isRemoved() && blockEntity.hasWorld()) {
			BlockPos blockPos = blockEntity.getPos();
			if (getChunkManager().shouldTickBlock(blockPos) && getWorldBorder().contains(blockPos)) {
				try {
					profiler.push(() -> {
						return String.valueOf(BlockEntityType.getId(blockEntity.getType()));
					});
					if (blockEntity.getType().supports(getBlockState(blockPos).getBlock())) {
						((Tickable)blockEntity).tick();
					} else {
						blockEntity.markInvalid();
					}
					
					profiler.pop();
				} catch (Throwable var8) {
					CrashReport crashReport = CrashReport.create(var8, "Ticking block entity");
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
			if (isChunkLoaded(blockEntity.getPos())) {
				getWorldChunk(blockEntity.getPos()).removeBlockEntity(blockEntity.getPos());
			}
		}
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
	
	private void handlePendingMovedBlockEntities() {
		if (!pendingMovedBlockEntities.isEmpty()) {
			for (BlockEntity blockEntity : pendingMovedBlockEntities) {
				if (!blockEntity.isRemoved() && !blockEntities.contains(blockEntity)) {
					BlockPos pos = blockEntity.getPos();
					
					setMovedBlockEntity(pos, blockEntity);
				}
			}
			
			pendingMovedBlockEntities.clear();
		}
	}
	
	private  void setMovedBlockEntity(BlockPos pos, BlockEntity blockEntity) {
		Block block = getBlockState(pos).getBlock();
		
		if (block instanceof BlockEntityProvider) {
			removeBlockEntity(pos);
			setBlockEntity(pos, blockEntity);
			
			updateComparators(pos, block);
		}
	}
	
	private void syncClientBlockEntityTickingQueue(BlockEntity blockEntity) {
		TickBlockEntityPacket packet = new TickBlockEntityPacket(blockEntity.getPos());
		((MinecraftServerHelper)getServer()).getPacketHandler().sendPacketToDimension(packet, getRegistryKey());
	}
}
