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
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import redstonetweaks.block.entity.PowerBlockEntity;
import redstonetweaks.block.piston.PistonSettings;
import redstonetweaks.helper.BlockHelper;
import redstonetweaks.helper.PistonHelper;
import redstonetweaks.helper.RedstoneWireHelper;
import redstonetweaks.helper.StairsHelper;
import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.interfaces.mixin.RTIAbstractBlockState;
import redstonetweaks.interfaces.mixin.RTIRedstoneWireBlock;
import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.setting.settings.Tweaks;
import redstonetweaks.world.common.ShapeUpdate;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireBlockMixin extends AbstractBlock implements BlockEntityProvider, RTIRedstoneWireBlock {
	
	@Shadow protected boolean wiresGivePower;
	
	@Shadow protected abstract BlockState method_27840(BlockView world, BlockState state, BlockPos pos);
	@Shadow protected abstract WireConnection method_27841(BlockView blockView, BlockPos blockPos, Direction direction, boolean bl);
	@Shadow protected abstract void update(World world, BlockPos pos, BlockState state);
	@Shadow protected abstract int getReceivedRedstonePower(World world, BlockPos blockPos);
	@Shadow protected static native boolean connectsTo(BlockState state, Direction dir);
	
	public RedstoneWireBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Redirect(
			method = "method_27843",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/BlockState;isSolidBlock(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"
			)
	)
	private boolean onMethod_27843RedirectIsSolidBlock(BlockState aboveState, BlockView world1, BlockPos blockPos, BlockView world, BlockState state, BlockPos pos) {
		BlockPos abovePos = pos.up();
		
		if (Tweaks.Stairs.FULL_FACES_ARE_SOLID.get()) {
			if (StairsHelper.isStairs(aboveState)) {
				return aboveState.isSideSolidFullSquare(world, abovePos, Direction.DOWN);
			}
		}
		
		return aboveState.isSolidBlock(world, abovePos);
	}
	
	@Redirect(
			method = "method_27843",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/RedstoneWireBlock;method_27841(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Lnet/minecraft/block/enums/WireConnection;"
			)
	)
	private WireConnection onMethod_27843RedirectMethod_27841(RedstoneWireBlock wire, BlockView world, BlockPos pos, Direction direction, boolean canConnectUp) {
		if (canConnectUp && Tweaks.Stairs.FULL_FACES_ARE_SOLID.get()) {
			BlockPos abovePos = pos.up();
			BlockState aboveState = world.getBlockState(abovePos);
			
			if (StairsHelper.isStairs(aboveState)) {
				canConnectUp = !aboveState.isSideSolidFullSquare(world, abovePos, direction);
			}
		}
		
		return method_27841(world, pos, direction, canConnectUp);
	}
	
	@Redirect(
			method = "prepare",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/BlockState;getStateForNeighborUpdate(Lnet/minecraft/util/math/Direction;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"
			)
	)
	private BlockState onPrepareRedirectGetStateForNeighborUpdate(BlockState state, Direction direction, BlockState blockState, WorldAccess world, BlockPos mutable, BlockPos notifierPos) {
		return state;
	}
	
	@Redirect(
			method = "prepare",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/RedstoneWireBlock;replace(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;II)V"
			)
	)
	private void onPrepareRedirectReplace(BlockState oldState, BlockState newState, WorldAccess world, BlockPos pos, int flags, int depth) {
		// replaced by the inject below
	}
	
	@Inject(
			method = "prepare",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/BlockState;getStateForNeighborUpdate(Lnet/minecraft/util/math/Direction;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"
			)
	)
	private void onPrepareInjectAfterGetStateForNeighborUpdate(BlockState state, WorldAccess world, BlockPos sourcePos, int flags, int depth, CallbackInfo ci, BlockPos.Mutable mutable, Iterator<Direction> horizontalDirections, Direction direction) {
		Direction dir = direction.getOpposite();
		BlockPos pos = mutable.toImmutable();
		BlockPos notifierPos = pos.offset(dir);
		
		((RTIWorld)world).dispatchShapeUpdate(false, new ShapeUpdate(pos, notifierPos, sourcePos, world.getBlockState(notifierPos), dir, flags, depth));
	}
	
	@Redirect(
			method = "getRenderConnectionType",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/BlockState;isSolidBlock(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"
			)
	)
	private boolean onGetRenderConnectionTypeRedirectIsSolidBlock(BlockState aboveState, BlockView world1, BlockPos blockPos, BlockView world, BlockPos pos, Direction direction) {
		BlockPos abovePos = pos.up();
		
		if (Tweaks.Stairs.FULL_FACES_ARE_SOLID.get()) {
			if (aboveState.getBlock() instanceof StairsBlock) {
				return aboveState.isSideSolidFullSquare(world, abovePos, Direction.DOWN) || aboveState.isSideSolidFullSquare(world, abovePos, direction);
			}
		}
		
		return aboveState.isSolidBlock(world, abovePos);
	}
	
	@Redirect(
			method = "method_27841",
			at = @At(
					value = "FIELD",
					ordinal = 0,
					target = "Lnet/minecraft/block/enums/WireConnection;SIDE:Lnet/minecraft/block/enums/WireConnection;"
			)
	)
	private WireConnection onMethod_27841RedirectWireConnectionSide() {
		return Tweaks.RedstoneWire.SLABS_ALLOW_UP_CONNECTION.get() ? WireConnection.SIDE : WireConnection.NONE;
	}
	
	@Redirect(
			method = "method_27841",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/RedstoneWireBlock;connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z"
			)
	)
	private boolean onMethod_27841RedirectConnectsTo(BlockState state, Direction dir, BlockView world, BlockPos pos, Direction direction, boolean bl) {
		return connectsTo(world, pos.offset(direction), state, direction);
	}
	
	@Redirect(
			method = "method_27841",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/BlockState;isSolidBlock(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"
			)
	)
	private boolean onMethod_27841RedirectIsSolidBlock(BlockState state, BlockView world1, BlockPos side, BlockView world, BlockPos pos, Direction direction, boolean blockedAbove) {
		if (Tweaks.Stairs.FULL_FACES_ARE_SOLID.get()) {
			if (StairsHelper.isStairs(state)) {
				return state.isSideSolidFullSquare(world, side, Direction.DOWN) || state.isSideSolidFullSquare(world, side, direction.getOpposite());
			}
		}
		
		return state.isSolidBlock(world, side);
	}
	
	@Inject(
			method = "update",
			cancellable = true,
			at = @At(
					value = "HEAD"
			)
	)
	private void onUpdateInjectAtHead(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
		updatePowered(world, pos, state, false);
		
		ci.cancel();
	}
	
	@Inject(
			method = "getReceivedRedstonePower",
			cancellable = true,
			at = @At(
					value = "HEAD"
			)
	)
	private void onGetReceivedPowerInjectAtHead(World world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		int maxPower = Tweaks.Global.POWER_MAX.get();
		
		int powerReceived = getExternalPower(world, pos);
		int wirePower = 0;
		
		for (Direction dir : Direction.Type.HORIZONTAL) {
			if (powerReceived >= maxPower) {
				break;
			}
			
			BlockPos sidePos = pos.offset(dir);
			BlockPos abovePos = pos.up();
			BlockPos belowPos = pos.down();
			
			BlockState sideState = world.getBlockState(sidePos);
			BlockState aboveState = world.getBlockState(abovePos);
			BlockState belowState = world.getBlockState(belowPos);
			
			boolean isSideSolid = isSideSolid(world, sidePos, sideState, dir.getOpposite());
			
			wirePower = Math.max(wirePower, getNeighboringWirePower(world, sidePos, sideState, dir));
			if ((isSideSolid || isSolidGlass(sideState)) && !hasSolidBottom(world, abovePos, aboveState, dir)) {
				wirePower = Math.max(wirePower, getNeighboringWirePower(world, sidePos.up(), dir));
			}
			if (!(isSideSolid || isSolidGlass(belowState) || hasSolidBottom(world, sidePos, sideState, dir.getOpposite()))) {
				wirePower = Math.max(wirePower, getNeighboringWirePower(world, sidePos.down(), dir));
			}
			
			powerReceived = Math.max(powerReceived, wirePower - 1);
		}
		
		cir.setReturnValue(Math.min(powerReceived, maxPower));
		cir.cancel();
	}
	
	@Inject(
			method = "onBlockAdded",
			at = @At(
					value = "INVOKE",
					shift = Shift.BEFORE,
					target = "Lnet/minecraft/block/RedstoneWireBlock;update(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"
			)
	)
	private void onOnBlockAddedInjectBeforeUpdate(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
		if (world.getBlockEntity(pos) == null) {
			world.setBlockEntity(pos, createBlockEntity(world));
		}
	}
	
	@Inject(
			method = "getStrongRedstonePower",
			cancellable = true,
			at = @At(
					value = "HEAD"
			)
	)
	private void onGetStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(getPowerOutput(world, pos, state, dir, true));
		cir.cancel();
	}
	
	@Inject(
			method = "getWeakRedstonePower",
			cancellable = true,
			at = @At(
					value = "HEAD"
			)
	)
	private void onGetWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(getPowerOutput(world, pos, state, dir, false));
		cir.cancel();
	}
	
	@Inject(
			method = "connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z",
			cancellable = true, 
			at = @At(
					value = "HEAD"
			)
	)
	private static void onConnectsToInjectAtHead(BlockState state, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		if (state.getBlock() instanceof PistonBlock) {
			cir.setReturnValue(PistonSettings.connectsToWire(PistonHelper.isSticky(state)) && direction != null && state.get(Properties.FACING) != direction.getOpposite());
			cir.cancel();
		}
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		updatePowered(world, pos, state, true);
	}
	
	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		return BlockHelper.microTickModeBlockEvent(state, world, pos, type, data);
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new PowerBlockEntity();
	}
	
	@Override
	public void setWiresGivePower(boolean wiresGivePower) {
		this.wiresGivePower = wiresGivePower;
	}
	
	private int getExternalPower(World world, BlockPos pos) {
		wiresGivePower = false;
		int power = world.getReceivedRedstonePower(pos);
		wiresGivePower = true;
		
		return power;
	}
	
	private void updatePowered(World world, BlockPos pos, BlockState state, boolean onScheduledTick) {
		int power = state.get(Properties.POWER);
		
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (blockEntity instanceof PowerBlockEntity) {
			power = ((PowerBlockEntity)blockEntity).getPower();
		}
		
		int powerReceived = getReceivedRedstonePower(world, pos);
		
		if (power != powerReceived && world.getBlockState(pos) == state) {
			if (onScheduledTick) {
				if (blockEntity instanceof PowerBlockEntity) {
					((PowerBlockEntity)blockEntity).setPower(powerReceived);
				}
				
				// This ensures neighboring blocks are updated when the new state is placed
				BlockState tempState = state.with(Properties.POWER, (powerReceived == 0) ? 1 : 0);
				world.setBlockState(pos, tempState, 16);
				
				BlockState newState = state.with(Properties.POWER, Math.min(powerReceived, 15));
				world.setBlockState(pos, newState, 2);
				
				updateNeighborsOnPowerChange(world, pos, newState);
			} else {
				BlockPos belowPos = pos.down();
				RTIAbstractBlockState belowState = (RTIAbstractBlockState)world.getBlockState(belowPos);
				
				int delay = belowState.delayOverride(Tweaks.RedstoneWire.DELAY.get());
				
				if (Tweaks.RedstoneWire.MICRO_TICK_MODE.get() || belowState.forceMicroTickMode()) {
					if (!world.isClient()) {
						world.addSyncedBlockEvent(pos, state.getBlock(), delay, 0);
					}
				} else {
					TickPriority tickPriority = belowState.tickPriorityOverride(Tweaks.RedstoneWire.TICK_PRIORITY.get());
					
					TickSchedulerHelper.scheduleBlockTick(world, pos, state, delay, tickPriority);
				}
			}
		}
	}
	
	private void updateNeighborsOnPowerChange(World world, BlockPos pos, BlockState state) {
		((RTIWorld)world).dispatchBlockUpdates(pos, null, state.getBlock(), Tweaks.RedstoneWire.BLOCK_UPDATE_ORDER.get());
	}
	
	private int getNeighboringWirePower(World world, BlockPos pos, Direction dir) {
		return getNeighboringWirePower(world, pos, world.getBlockState(pos), dir);
	}
	
	private int getNeighboringWirePower(World world, BlockPos pos, BlockState state, Direction dir) {
		return state.isOf(Blocks.REDSTONE_WIRE) ? getWirePower(world, pos, dir, false) : 0;
	}
	
	private int getWirePower(World world, BlockPos pos, Direction dir, boolean checkConnections) {
		return getWirePower(world, pos, world.getBlockState(pos), dir, checkConnections);
	}
	
	private int getWirePower(BlockView world, BlockPos pos, BlockState state, Direction dir, boolean checkConnections) {
		if (!RedstoneWireHelper.emitsPowerTo(world, pos, dir)) {
			return 0;
		}
		if (checkConnections && dir.getAxis().isHorizontal()) {
			BlockState connectedState = method_27840(world, state, pos);
			EnumProperty<WireConnection> connectionProperty = RedstoneWireBlock.DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(dir.getOpposite());
			
			if (!connectedState.get(connectionProperty).isConnected()) {
				return 0;
			}
		}
		
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (blockEntity instanceof PowerBlockEntity) {
			PowerBlockEntity powerBlockEntity = ((PowerBlockEntity)blockEntity);
			
			powerBlockEntity.ensureCorrectPower(state);
			
			return powerBlockEntity.getPower();
		}
		
		return state.get(Properties.POWER);
	}
	
	private int getPowerOutput(BlockView world, BlockPos pos, BlockState state, Direction dir, boolean strong) {
		if (!wiresGivePower || dir == Direction.DOWN) {
			return 0;
		}
		
		int power = getWirePower(world, pos, state, dir, true);
		
		if (power > 0) {
			BlockPos belowPos = pos.down();
			RTIAbstractBlockState belowState = (RTIAbstractBlockState)world.getBlockState(belowPos);
			
			return strong ? belowState.strongPowerOverride(power) : belowState.weakPowerOverride(power);
		}
		
		return power;
	}
	
	private boolean isSideSolid(World world, BlockPos pos, BlockState state, Direction face) {
		if (state.isSolidBlock(world, pos)) {
			return true;
		}
		if (state.getBlock() instanceof StairsBlock) {
			return Tweaks.Stairs.FULL_FACES_ARE_SOLID.get() && state.isSideSolidFullSquare(world, pos, face);
		}
		
		return false;
	}
	
	private boolean isSolidGlass(BlockState state) {
		return Tweaks.RedstoneWire.INVERT_FLOW_ON_GLASS.get() && state.getBlock() instanceof AbstractGlassBlock;
	}
	
	private boolean hasSolidBottom(World world, BlockPos pos, BlockState state, Direction dir) {
		if (isSideSolid(world, pos, state, Direction.DOWN)) {
			return true;
		}
		if (state.getBlock() instanceof StairsBlock) {
			return Tweaks.Stairs.FULL_FACES_ARE_SOLID.get() && state.isSideSolidFullSquare(world, pos, dir);
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
					
					return PistonSettings.connectsToWire(pushedBlock.isOf(Blocks.STICKY_PISTON));
				}
			}
			
			return false;
		}
		
		return connectsTo(state, direction);
	}
}
