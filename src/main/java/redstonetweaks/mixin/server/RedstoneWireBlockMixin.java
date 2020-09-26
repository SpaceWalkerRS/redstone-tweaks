package redstonetweaks.mixin.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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

import com.google.common.collect.Sets;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractGlassBlock;
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

import redstonetweaks.helper.DirectionHelper;
import redstonetweaks.helper.PistonHelper;
import redstonetweaks.helper.ServerWorldHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.world.server.ScheduledNeighborUpdate.UpdateType;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireBlockMixin extends AbstractBlock {
	
	@Shadow protected abstract WireConnection method_27841(BlockView blockView, BlockPos blockPos, Direction direction, boolean bl);
	@Shadow protected abstract void update(World world, BlockPos pos, BlockState state);
	@Shadow protected abstract int getReceivedRedstonePower(World world, BlockPos blockPos);
	@Shadow protected static native boolean connectsTo(BlockState state, Direction dir);
	
	public RedstoneWireBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Redirect(method = "method_27843", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isSolidBlock(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onMethod_27843RedirectIsSolidBlock(BlockState aboveState, BlockView world1, BlockPos up, BlockView world, BlockState state, BlockPos pos) {
		if (redstonetweaks.settings.Settings.Stairs.FULL_FACES_ARE_SOLID.get()) {
			if (aboveState.getBlock() instanceof StairsBlock) {
				return aboveState.isSideSolidFullSquare(world, up, Direction.DOWN);
			}
		}
		return aboveState.isSolidBlock(world, pos);
	}
	
	@Redirect(method = "method_27843", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;method_27841(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Lnet/minecraft/block/enums/WireConnection;"))
	private WireConnection onMethod_27843RedirectMethod_27841(RedstoneWireBlock wire, BlockView world, BlockPos pos, Direction direction, boolean canConnectUp) {
		if (canConnectUp && redstonetweaks.settings.Settings.Stairs.FULL_FACES_ARE_SOLID.get()) {
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
		return redstonetweaks.settings.Settings.Global.DO_SHAPE_UPDATES.get() && ((WorldHelper)world).updateNeighborsNormally() ? state.getStateForNeighborUpdate(direction, blockState, world, mutable, notifierPos) : state;
	}
	
	@Inject(method = "prepare", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/BlockState;getStateForNeighborUpdate(Lnet/minecraft/util/math/Direction;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private void onPrepareInjectAfterGetStateForNeighborUpdate(BlockState state, WorldAccess world, BlockPos blockPos, int flags, int depth, CallbackInfo ci, BlockPos.Mutable mutable, Iterator<Direction> horizontalDirections, Direction direction) {
		if (redstonetweaks.settings.Settings.Global.DO_SHAPE_UPDATES.get() && !((WorldHelper)world).updateNeighborsNormally()) {
			if (!world.isClient()) {
				BlockPos pos = mutable.toImmutable();
				BlockPos notifierPos = pos.offset(direction.getOpposite());
				((ServerWorldHelper)world).getNeighborUpdateScheduler().schedule(pos, notifierPos, direction.getOpposite(), flags, depth, UpdateType.STATE_UPDATE);
			}
		}
	}
	
	@Redirect(method = "getRenderConnectionType", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isSolidBlock(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onGetRenderConnectionTypeRedirectIsSolidBlock(BlockState state, BlockView world1, BlockPos up, BlockView world, BlockPos pos, Direction direction) {
		if (redstonetweaks.settings.Settings.Stairs.FULL_FACES_ARE_SOLID.get()) {
			if (state.getBlock() instanceof StairsBlock) {
				return state.isSideSolidFullSquare(world, up, Direction.DOWN) || state.isSideSolidFullSquare(world, up, direction);
			}
		}
		return state.isSolidBlock(world, pos);
	}
	
	@Redirect(method = "method_27841", at = @At(value = "FIELD", ordinal = 0, target = "Lnet/minecraft/block/enums/WireConnection;SIDE:Lnet/minecraft/block/enums/WireConnection;"))
	private WireConnection onMethod_27841RedirectWireConnectionSide() {
		return redstonetweaks.settings.Settings.RedstoneWire.SLABS_ALLOW_UP_CONNECTION.get() ? WireConnection.SIDE : WireConnection.NONE;
	}
	
	@Redirect(method = "method_27841", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z"))
	private boolean onMethod_27841RedirectConnectsTo(BlockState state, Direction dir, BlockView world, BlockPos pos, Direction direction, boolean bl) {
		return connectsTo(world, pos.offset(direction), state, direction);
	}
	
	@Redirect(method = "method_27841", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isSolidBlock(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onMethod_27841RedirectIsSolidBlock(BlockState state, BlockView world1, BlockPos side, BlockView world, BlockPos pos, Direction direction, boolean blockedAbove) {
		if (redstonetweaks.settings.Settings.Stairs.FULL_FACES_ARE_SOLID.get()) {
			if (state.getBlock() instanceof StairsBlock) {
				return state.isSideSolidFullSquare(world, side, Direction.DOWN) || state.isSideSolidFullSquare(world, side, direction.getOpposite());
			}
		}
		return state.isSolidBlock(world, side);
	}
	
	@Inject(method = "update", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Sets;newHashSet()Ljava/util/HashSet;", shift = Shift.BEFORE), cancellable = true)
	private void onUpdateInjectBeforeNewHashSet(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
		updateNeighborsOnStateChange(world, pos, state);
		ci.cancel();
	}
	
	@Redirect(method = "getReceivedRedstonePower", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/block/BlockState;isSolidBlock(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onGetReceivedRedstonePowerRedirectIsSolidBlock0(BlockState sideState, BlockView world1, BlockPos side, World world, BlockPos pos) {
		boolean sideSolid = sideState.isSolidBlock(world, side) || (redstonetweaks.settings.Settings.RedstoneWire.INVERT_FLOW_ON_GLASS.get() && sideState.getBlock() instanceof AbstractGlassBlock);
		boolean topSolid = false;
		
		BlockPos up = pos.up();
		BlockState aboveState = world.getBlockState(up);
		if (redstonetweaks.settings.Settings.Stairs.FULL_FACES_ARE_SOLID.get()) {
			Direction direction = DirectionHelper.getFromPositions(pos, side);
			
			if (!sideSolid && sideState.getBlock() instanceof StairsBlock) {
				sideSolid = sideState.isSideSolidFullSquare(world, side, direction.getOpposite());
			}
			if (aboveState.getBlock() instanceof StairsBlock) {
				topSolid = aboveState.isSideSolidFullSquare(world, up, Direction.DOWN) || aboveState.isSideSolidFullSquare(world, up, direction);
			}
		}
		topSolid |= aboveState.isSolidBlock(world, up);
		
		return sideSolid && !topSolid;
	}
	
	@Redirect(method = "getReceivedRedstonePower", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/block/BlockState;isSolidBlock(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onGetReceivedRedstonePowerRedirectIsSolidBlock1(BlockState state, BlockView world, BlockPos up) {
		return false;
	}
	
	@Redirect(method = "getReceivedRedstonePower", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/block/BlockState;isSolidBlock(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onGetReceivedRedstonePowerRedirectIsSolidBlock2(BlockState state, BlockView world1, BlockPos side, World world, BlockPos pos) {
		if (redstonetweaks.settings.Settings.Stairs.FULL_FACES_ARE_SOLID.get()) {
			if (state.getBlock() instanceof StairsBlock) {
				Direction direction = DirectionHelper.getFromPositions(pos, side);
				return state.isSideSolidFullSquare(world, side, Direction.DOWN) || state.isSideSolidFullSquare(world, side, direction.getOpposite());
			}
		}
		return state.isSolidBlock(world, side) || (redstonetweaks.settings.Settings.RedstoneWire.INVERT_FLOW_ON_GLASS.get() && world.getBlockState(pos.down()).getBlock() instanceof AbstractGlassBlock);
	}
	
	@Redirect(method = "getReceivedRedstonePower", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onGetReceivedRedstonePowerRedirectGetBlockState0(World world1, BlockPos sidePos, World world, BlockPos pos) {
		Direction direction = DirectionHelper.getFromPositions(sidePos, pos);
		return WorldHelper.getStateForPower(world, sidePos, direction);
	}
	
	@Redirect(method = "getReceivedRedstonePower", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onGetReceivedRedstonePowerRedirectGetBlockState2(World world1, BlockPos sideUpPos, World world, BlockPos pos) {
		Direction direction = DirectionHelper.getFromPositions(sideUpPos, pos.up());
		return WorldHelper.getStateForPower(world, sideUpPos, direction);
	}
	
	@Redirect(method = "getReceivedRedstonePower", at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onGetReceivedRedstonePowerRedirectGetBlockState3(World world1, BlockPos sideDownPos, World world, BlockPos pos) {
		Direction direction = DirectionHelper.getFromPositions(sideDownPos, pos.down());
		return WorldHelper.getStateForPower(world, sideDownPos, direction);
	}
	
	@Inject(method = "updateNeighbors", at = @At(value = "HEAD"))
	private void onUpdateNeighborsInjectAtHead(World world, BlockPos pos, CallbackInfo ci) {
		((ServerWorldHelper)world).getNeighborUpdateScheduler().setCurrentSourcePos(pos);
	}
	
	@Inject(method = "updateNeighbors", at = @At(value = "RETURN"))
	private void onUpdateNeighborsInjectAtReturn(World world, BlockPos pos, CallbackInfo ci) {
		((ServerWorldHelper)world).getNeighborUpdateScheduler().clearCurrentSourcePos();
	}
	
	@Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;update(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void onOnBlockAddedRedirectUpdate(RedstoneWireBlock wireBlock, World world, BlockPos pos, BlockState state) {
		onNeighborUpdate(world, pos, state);
	}
	
	@Redirect(method = "onStateReplaced", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;update(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void onOnStateReplacedRedirectUpdate(RedstoneWireBlock wireBlock, World world, BlockPos pos, BlockState state) {
		onNeighborUpdate(world, pos, state);
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;update(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void onNeighborUpdateRedirectUpdate(RedstoneWireBlock wireBlock, World world, BlockPos pos, BlockState state) {
		onNeighborUpdate(world, pos, state);
	}
	
	@Inject(method = "getWeakRedstonePower", cancellable = true, at = @At(value = "RETURN"))
	private void onGetWeakRedstonePowerInjectAtReturn(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		if (redstonetweaks.settings.Settings.MagentaGlazedTerracotta.IS_POWER_DIODE.get()) {
			int power = cir.getReturnValueI();
			if (power > 0) {
				BlockState belowState = world.getBlockState(pos.down());
				if (belowState.isOf(Blocks.MAGENTA_GLAZED_TERRACOTTA)) {
					cir.setReturnValue(belowState.get(Properties.HORIZONTAL_FACING) == direction ? power : 0);
					cir.cancel();
				}
			}
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
		update(world, pos, state);
	}
	
	private void onNeighborUpdate(World world, BlockPos pos, BlockState state) {
		int delay = redstonetweaks.settings.Settings.RedstoneWire.DELAY.get();
		if (delay == 0) {
			update(world, pos, state);
		} else if (!world.getBlockTickScheduler().isTicking(pos, state.getBlock())) {
			int powerReceived = this.getReceivedRedstonePower(world, pos);
			if (state.get(Properties.POWER) != powerReceived) {
				world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, redstonetweaks.settings.Settings.RedstoneWire.TICK_PRIORITY.get());
			}
		}
	}
	
	private void updateNeighborsOnStateChange(World world, BlockPos pos, BlockState state) {
		((ServerWorldHelper)world).getNeighborUpdateScheduler().setCurrentSourcePos(pos);
		
		Collection<BlockPos> notifiers;
		if (redstonetweaks.settings.Settings.RedstoneWire.RANDOM_UPDATE_ORDER.get()) {
			notifiers = new ArrayList<>();
		} else {
			notifiers = redstonetweaks.settings.Settings.RedstoneWire.DIRECTIONAL_UPDATE_ORDER.get() ? Sets.newLinkedHashSet() : Sets.newHashSet();
		}
		
		notifiers.add(pos);
		for (Direction direction : Direction.values()) {
			notifiers.add(pos.offset(direction));
		}
		if (redstonetweaks.settings.Settings.RedstoneWire.RANDOM_UPDATE_ORDER.get()) {
			Collections.shuffle((List<BlockPos>)notifiers);
		}
		
		for (BlockPos blockPos : notifiers) {
			world.updateNeighborsAlways(blockPos, state.getBlock());
		}
		
		((ServerWorldHelper)world).getNeighborUpdateScheduler().clearCurrentSourcePos();
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
