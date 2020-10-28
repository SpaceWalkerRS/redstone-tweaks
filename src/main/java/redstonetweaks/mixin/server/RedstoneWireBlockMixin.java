package redstonetweaks.mixin.server;

import java.util.Iterator;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import redstonetweaks.block.AnaloguePowerComponentBlockEntity;
import redstonetweaks.helper.PistonHelper;
import redstonetweaks.helper.RedstoneWireHelper;
import redstonetweaks.interfaces.RTIWorld;
import redstonetweaks.interfaces.RTIServerWorld;
import redstonetweaks.world.server.ScheduledNeighborUpdate.UpdateType;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireBlockMixin extends AbstractBlock implements BlockEntityProvider {
	
	@Shadow protected boolean wiresGivePower;
	
	@Shadow protected abstract WireConnection method_27841(BlockView blockView, BlockPos blockPos, Direction direction, boolean bl);
	@Shadow protected abstract void update(World world, BlockPos pos, BlockState state);
	@Shadow protected abstract int getReceivedRedstonePower(World world, BlockPos blockPos);
	@Shadow protected static native boolean connectsTo(BlockState state, Direction dir);
	
	public RedstoneWireBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Redirect(method = "method_27843", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isSolidBlock(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onMethod_27843RedirectIsSolidBlock(BlockState aboveState, BlockView world1, BlockPos up, BlockView world, BlockState state, BlockPos pos) {
		if (redstonetweaks.setting.Settings.Stairs.FULL_FACES_ARE_SOLID.get()) {
			if (aboveState.getBlock() instanceof StairsBlock) {
				return aboveState.isSideSolidFullSquare(world, up, Direction.DOWN);
			}
		}
		return aboveState.isSolidBlock(world, pos);
	}
	
	@Redirect(method = "method_27843", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;method_27841(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Lnet/minecraft/block/enums/WireConnection;"))
	private WireConnection onMethod_27843RedirectMethod_27841(RedstoneWireBlock wire, BlockView world, BlockPos pos, Direction direction, boolean canConnectUp) {
		if (canConnectUp && redstonetweaks.setting.Settings.Stairs.FULL_FACES_ARE_SOLID.get()) {
			BlockPos up = pos.up();
			BlockState aboveState = world.getBlockState(up);
			
			if (aboveState.getBlock() instanceof StairsBlock) {
				canConnectUp = !aboveState.isSideSolidFullSquare(world, up, direction);
			}
		}
		return method_27841(world, pos, direction, canConnectUp);
	}
	
	@Redirect(method = "prepare", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getStateForNeighborUpdate(Lnet/minecraft/util/math/Direction;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onPrepareRedirectGetStateForNeighborUpdate(BlockState state, Direction direction, BlockState blockState, WorldAccess world, BlockPos mutable, BlockPos notifierPos) {
		return redstonetweaks.setting.Settings.Global.DO_SHAPE_UPDATES.get() && ((RTIWorld)world).updateNeighborsNormally() ? state.getStateForNeighborUpdate(direction, blockState, world, mutable, notifierPos) : state;
	}
	
	@Inject(method = "prepare", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/BlockState;getStateForNeighborUpdate(Lnet/minecraft/util/math/Direction;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private void onPrepareInjectAfterGetStateForNeighborUpdate(BlockState state, WorldAccess world, BlockPos blockPos, int flags, int depth, CallbackInfo ci, BlockPos.Mutable mutable, Iterator<Direction> horizontalDirections, Direction direction) {
		if (redstonetweaks.setting.Settings.Global.DO_SHAPE_UPDATES.get() && !((RTIWorld)world).updateNeighborsNormally()) {
			if (!world.isClient()) {
				BlockPos pos = mutable.toImmutable();
				BlockPos notifierPos = pos.offset(direction.getOpposite());
				((RTIServerWorld)world).getNeighborUpdateScheduler().schedule(pos, notifierPos, direction.getOpposite(), flags, depth, UpdateType.STATE_UPDATE);
			}
		}
	}
	
	@Redirect(method = "getRenderConnectionType", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isSolidBlock(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onGetRenderConnectionTypeRedirectIsSolidBlock(BlockState state, BlockView world1, BlockPos up, BlockView world, BlockPos pos, Direction direction) {
		if (redstonetweaks.setting.Settings.Stairs.FULL_FACES_ARE_SOLID.get()) {
			if (state.getBlock() instanceof StairsBlock) {
				return state.isSideSolidFullSquare(world, up, Direction.DOWN) || state.isSideSolidFullSquare(world, up, direction);
			}
		}
		return state.isSolidBlock(world, pos);
	}
	
	@Redirect(method = "method_27841", at = @At(value = "FIELD", ordinal = 0, target = "Lnet/minecraft/block/enums/WireConnection;SIDE:Lnet/minecraft/block/enums/WireConnection;"))
	private WireConnection onMethod_27841RedirectWireConnectionSide() {
		return redstonetweaks.setting.Settings.RedstoneWire.SLABS_ALLOW_UP_CONNECTION.get() ? WireConnection.SIDE : WireConnection.NONE;
	}
	
	@Redirect(method = "method_27841", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z"))
	private boolean onMethod_27841RedirectConnectsTo(BlockState state, Direction dir, BlockView world, BlockPos pos, Direction direction, boolean bl) {
		return connectsTo(world, pos.offset(direction), state, direction);
	}
	
	@Redirect(method = "method_27841", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isSolidBlock(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onMethod_27841RedirectIsSolidBlock(BlockState state, BlockView world1, BlockPos side, BlockView world, BlockPos pos, Direction direction, boolean blockedAbove) {
		if (redstonetweaks.setting.Settings.Stairs.FULL_FACES_ARE_SOLID.get()) {
			if (state.getBlock() instanceof StairsBlock) {
				return state.isSideSolidFullSquare(world, side, Direction.DOWN) || state.isSideSolidFullSquare(world, side, direction.getOpposite());
			}
		}
		return state.isSolidBlock(world, side);
	}
	
	@Inject(method = "update", cancellable = true, at = @At(value = "HEAD"))
	private void onUpdateInjectAtHead(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
		updatePowered(world, pos, state, false);
		ci.cancel();
	}
	
	@Inject(method = "getReceivedRedstonePower", cancellable = true, at = @At(value = "HEAD"))
	private void onGetReceivedPowerInjectAtHead(World world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		wiresGivePower = false;
		int power = world.getReceivedRedstonePower(pos);
		wiresGivePower = true;
		
		int maxPower = redstonetweaks.setting.Settings.Global.POWER_MAX.get();
		int wirePower = 0;
		if (power < maxPower) {
			for (Direction dir : Direction.Type.HORIZONTAL) {
				BlockPos sidePos = pos.offset(dir);
				BlockState sideState = world.getBlockState(sidePos);
				
				wirePower = Math.max(wirePower, getWirePower(world, sidePos, sideState, dir.getOpposite()));
				
				BlockPos abovePos = pos.up();
				BlockState aboveState = world.getBlockState(abovePos);
				
				boolean isSideSolid = isSideSolid(world, sidePos, sideState, dir.getOpposite());
				
				if ((isSideSolid || isSolidGlass(sideState)) && !hasSolidBottom(world, abovePos, aboveState, dir)) {
					wirePower = Math.max(wirePower, getWirePower(world, sidePos.up(), dir.getOpposite()));
				}
				if (!(isSideSolid || isSolidGlass(world.getBlockState(pos.down())) || hasSolidBottom(world, sidePos, sideState, dir.getOpposite()))) {
					wirePower = Math.max(wirePower, getWirePower(world, sidePos.down(), dir.getOpposite()));
				}
			}
		}
		power = Math.max(power, wirePower - 1);
		
		cir.setReturnValue(Math.min(power, maxPower));
		cir.cancel();
	}
	
	@Inject(method = "updateNeighbors", at = @At(value = "HEAD"))
	private void onUpdateNeighborsInjectAtHead(World world, BlockPos pos, CallbackInfo ci) {
		((RTIServerWorld)world).getNeighborUpdateScheduler().setCurrentSourcePos(pos);
	}
	
	@Inject(method = "updateNeighbors", at = @At(value = "RETURN"))
	private void onUpdateNeighborsInjectAtReturn(World world, BlockPos pos, CallbackInfo ci) {
		((RTIServerWorld)world).getNeighborUpdateScheduler().clearCurrentSourcePos();
	}
	
	@Inject(method = "onBlockAdded", at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/RedstoneWireBlock;update(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void onOnBlockAddedInjectBeforeUpdate(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
		if (world.getBlockEntity(pos) == null) {
			world.setBlockEntity(pos, createBlockEntity(world));
		}
	}
	
	@Inject(method = "getWeakRedstonePower", cancellable = true, at = @At(value = "RETURN"))
	private void onGetWeakRedstonePowerInjectAtReturn(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		int power = cir.getReturnValueI();
		
		if (power > 0) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof AnaloguePowerComponentBlockEntity) {
				power = ((AnaloguePowerComponentBlockEntity)blockEntity).getPower();
			}
			
			if (redstonetweaks.setting.Settings.MagentaGlazedTerracotta.IS_POWER_DIODE.get()) {
				BlockState belowState = world.getBlockState(pos.down());
				if (belowState.isOf(Blocks.MAGENTA_GLAZED_TERRACOTTA) && belowState.get(Properties.HORIZONTAL_FACING) == direction) {
					power = 0;
				}
			}
			
			cir.setReturnValue(power);
			cir.cancel();
		}
	}
	
	@Inject(method = "connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z", cancellable = true, at = @At(value = "HEAD"))
	private static void onConnectsToInjectAtHead(BlockState state, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		if (state.getBlock() instanceof PistonBlock) {
			cir.setReturnValue(PistonHelper.connectsToWire(state.isOf(Blocks.STICKY_PISTON)) && direction != null && state.get(Properties.FACING) != direction.getOpposite());
			cir.cancel();
		}
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		updatePowered(world, pos, state, true);
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new AnaloguePowerComponentBlockEntity();
	}
	
	private void updatePowered(World world, BlockPos pos, BlockState state, boolean onScheduledTick) {
		int blockStatePower = state.get(Properties.POWER);
		int power;
		
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof AnaloguePowerComponentBlockEntity) {
			power = ((AnaloguePowerComponentBlockEntity)blockEntity).getPower();
			
			// If the world was loaded in vanilla there will not be
			// any block entity data, but there might still be
			// powered redstone wires. In that case a new block entity
			// is created and given a default power value of 0.
			// In the case where the block entity power is 0 but
			// the power level in the block state is not, we set
			// the block entity power level to the block state power level
			if (power == 0 && blockStatePower > 0) {
				power = blockStatePower;
			}
		} else {
			power = blockStatePower;
		}
		
		int powerReceived = getReceivedRedstonePower(world, pos);
		
		if (power != powerReceived && world.getBlockState(pos) == state) {
			int delay = redstonetweaks.setting.Settings.RedstoneWire.DELAY.get();
			if (onScheduledTick || delay == 0) {
				if (blockEntity instanceof AnaloguePowerComponentBlockEntity) {
					((AnaloguePowerComponentBlockEntity)blockEntity).setPower(powerReceived);
				}
				world.setBlockState(pos, state.with(Properties.POWER, Math.min(15, powerReceived)), 2);
				
				updateNeighborsOnStateChange(world, pos, state);
			} else {
				world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, redstonetweaks.setting.Settings.RedstoneWire.TICK_PRIORITY.get());
			}
		}
	}
	
	private void updateNeighborsOnStateChange(World world, BlockPos pos, BlockState state) {
		((RTIServerWorld)world).getNeighborUpdateScheduler().setCurrentSourcePos(pos);
		
		redstonetweaks.setting.Settings.RedstoneWire.BLOCK_UPDATE_ORDER.get().dispatchBlockUpdates(world, pos, state.getBlock());
		
		((RTIServerWorld)world).getNeighborUpdateScheduler().clearCurrentSourcePos();
	}
	
	private int getWirePower(World world, BlockPos pos, Direction dir) {
		return getWirePower(world, pos, world.getBlockState(pos), dir);
	}
	
	private int getWirePower(World world, BlockPos pos, BlockState state, Direction dir) {
		if (state.isOf(Blocks.REDSTONE_WIRE) && RedstoneWireHelper.emitsPowerTo(world, pos, dir)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof AnaloguePowerComponentBlockEntity) {
				return ((AnaloguePowerComponentBlockEntity)blockEntity).getPower();
			}
			return state.get(Properties.POWER);
		}
		return 0;
	}
	
	private boolean isSideSolid(World world, BlockPos pos, BlockState state, Direction face) {
		if (state.isSolidBlock(world, pos)) {
			return true;
		}
		if (state.getBlock() instanceof StairsBlock) {
			return redstonetweaks.setting.Settings.Stairs.FULL_FACES_ARE_SOLID.get() && state.isSideSolidFullSquare(world, pos, face);
		}
		return false;
	}
	
	private boolean isSolidGlass(BlockState state) {
		return redstonetweaks.setting.Settings.RedstoneWire.INVERT_FLOW_ON_GLASS.get() && state.getBlock() instanceof AbstractGlassBlock;
	}
	
	private boolean hasSolidBottom(World world, BlockPos pos, BlockState state, Direction dir) {
		if (isSideSolid(world, pos, state, Direction.DOWN)) {
			return true;
		}
		if (state.getBlock() instanceof StairsBlock) {
			return redstonetweaks.setting.Settings.Stairs.FULL_FACES_ARE_SOLID.get() && state.isSideSolidFullSquare(world, pos, dir);
		}
		return false;
	}
	
	private boolean connectsTo(BlockView world, BlockPos pos, BlockState state, Direction direction) {
		if (state.isOf(Blocks.MOVING_PISTON)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof PistonBlockEntity) {
				PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
				
				if (pistonBlockEntity.isSource() && !pistonBlockEntity.isExtending()) {
					BlockState pushedBlock = pistonBlockEntity.getPushedBlock();
					
					return PistonHelper.connectsToWire(pushedBlock.isOf(Blocks.STICKY_PISTON));
				}
			}
			
			return false;
		}
		
		return connectsTo(state, direction);
	}
}
