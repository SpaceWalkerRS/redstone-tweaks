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
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import redstonetweaks.block.PowerBlockEntity;
import redstonetweaks.helper.PistonHelper;
import redstonetweaks.helper.RedstoneWireHelper;
import redstonetweaks.interfaces.RTIWorld;
import redstonetweaks.setting.Tweaks;
import redstonetweaks.world.common.ShapeUpdate;

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
		if (Tweaks.Stairs.FULL_FACES_ARE_SOLID.get()) {
			if (aboveState.getBlock() instanceof StairsBlock) {
				return aboveState.isSideSolidFullSquare(world, up, Direction.DOWN);
			}
		}
		return aboveState.isSolidBlock(world, pos);
	}
	
	@Redirect(method = "method_27843", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;method_27841(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Lnet/minecraft/block/enums/WireConnection;"))
	private WireConnection onMethod_27843RedirectMethod_27841(RedstoneWireBlock wire, BlockView world, BlockPos pos, Direction direction, boolean canConnectUp) {
		if (canConnectUp && Tweaks.Stairs.FULL_FACES_ARE_SOLID.get()) {
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
		return state;
	}
	
	@Redirect(method = "prepare", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;replace(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;II)V"))
	private void onPrepareRedirectReplace(BlockState oldState, BlockState newState, WorldAccess world, BlockPos pos, int flags, int depth) {
		
	}
	
	@Inject(method = "prepare", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/BlockState;getStateForNeighborUpdate(Lnet/minecraft/util/math/Direction;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private void onPrepareInjectAfterGetStateForNeighborUpdate(BlockState state, WorldAccess world, BlockPos sourcePos, int flags, int depth, CallbackInfo ci, BlockPos.Mutable mutable, Iterator<Direction> horizontalDirections, Direction direction) {
		Direction dir = direction.getOpposite();
		BlockPos pos = mutable.toImmutable();
		BlockPos notifierPos = pos.offset(dir);
		
		((RTIWorld)world).dispatchShapeUpdate(false, new ShapeUpdate(pos, notifierPos, sourcePos, world.getBlockState(notifierPos), dir, flags, depth));
	}
	
	@Redirect(method = "getRenderConnectionType", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isSolidBlock(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onGetRenderConnectionTypeRedirectIsSolidBlock(BlockState state, BlockView world1, BlockPos up, BlockView world, BlockPos pos, Direction direction) {
		if (Tweaks.Stairs.FULL_FACES_ARE_SOLID.get()) {
			if (state.getBlock() instanceof StairsBlock) {
				return state.isSideSolidFullSquare(world, up, Direction.DOWN) || state.isSideSolidFullSquare(world, up, direction);
			}
		}
		return state.isSolidBlock(world, pos);
	}
	
	@Redirect(method = "method_27841", at = @At(value = "FIELD", ordinal = 0, target = "Lnet/minecraft/block/enums/WireConnection;SIDE:Lnet/minecraft/block/enums/WireConnection;"))
	private WireConnection onMethod_27841RedirectWireConnectionSide() {
		return Tweaks.RedstoneWire.SLABS_ALLOW_UP_CONNECTION.get() ? WireConnection.SIDE : WireConnection.NONE;
	}
	
	@Redirect(method = "method_27841", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z"))
	private boolean onMethod_27841RedirectConnectsTo(BlockState state, Direction dir, BlockView world, BlockPos pos, Direction direction, boolean bl) {
		return connectsTo(world, pos.offset(direction), state, direction);
	}
	
	@Redirect(method = "method_27841", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isSolidBlock(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onMethod_27841RedirectIsSolidBlock(BlockState state, BlockView world1, BlockPos side, BlockView world, BlockPos pos, Direction direction, boolean blockedAbove) {
		if (Tweaks.Stairs.FULL_FACES_ARE_SOLID.get()) {
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
		int maxPower = Tweaks.Global.POWER_MAX.get();
		
		int power = getExternalPower(world, pos);
		int wirePower = 0;
		
		for (Direction dir : Direction.Type.HORIZONTAL) {
			if (power >= maxPower) {
				break;
			}
			
			BlockPos sidePos = pos.offset(dir);
			BlockPos abovePos = pos.up();
			BlockPos belowPos = pos.down();
			
			BlockState sideState = world.getBlockState(sidePos);
			BlockState aboveState = world.getBlockState(abovePos);
			BlockState belowState = world.getBlockState(belowPos);
			
			boolean isSideSolid = isSideSolid(world, sidePos, sideState, dir.getOpposite());
			
			wirePower = Math.max(wirePower, getWirePower(world, sidePos, sideState, dir));
			if ((isSideSolid || isSolidGlass(sideState)) && !hasSolidBottom(world, abovePos, aboveState, dir)) {
				wirePower = Math.max(wirePower, getWirePower(world, sidePos.up(), dir));
			}
			if (!(isSideSolid || isSolidGlass(belowState) || hasSolidBottom(world, sidePos, sideState, dir.getOpposite()))) {
				wirePower = Math.max(wirePower, getWirePower(world, sidePos.down(), dir));
			}
			
			power = Math.max(power, wirePower - 1);
		}
		
		cir.setReturnValue(Math.min(power, maxPower));
		cir.cancel();
	}
	
	@Inject(method = "onBlockAdded", at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/RedstoneWireBlock;update(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void onOnBlockAddedInjectBeforeUpdate(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
		if (world.getBlockEntity(pos) == null) {
			world.setBlockEntity(pos, createBlockEntity(world));
		}
	}
	
	@SuppressWarnings("unchecked")
	@Redirect(method = "getWeakRedstonePower", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/block/BlockState;get(Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;"))
	private <T extends Comparable<T>> T onGetWeakRedstonePowerRedirectGetProperty(BlockState blockState, Property<T> property, BlockState state, BlockView world, BlockPos pos, Direction dir) {
		return (T)(Integer)getWirePower(world, pos, state, dir);
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
		return new PowerBlockEntity();
	}
	
	private int getExternalPower(World world, BlockPos pos) {
		wiresGivePower = false;
		int power = world.getReceivedRedstonePower(pos);
		wiresGivePower = true;
		
		return power;
	}
	
	private void updatePowered(World world, BlockPos pos, BlockState state, boolean onScheduledTick) {
		int power = getWirePower(world, pos, state, Direction.DOWN);
		int powerReceived = getReceivedRedstonePower(world, pos);
		
		if (power != powerReceived && world.getBlockState(pos) == state) {
			int delay = Tweaks.RedstoneWire.DELAY.get();
			
			if (onScheduledTick || delay == 0) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				
				if (blockEntity instanceof PowerBlockEntity) {
					((PowerBlockEntity)blockEntity).setPower(powerReceived);
				}
				world.setBlockState(pos, state.with(Properties.POWER, Math.min(powerReceived, 15)), 2);
				
				updateNeighborsOnStateChange(world, pos, state);
			} else {
				world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, Tweaks.RedstoneWire.TICK_PRIORITY.get());
			}
		}
	}
	
	private void updateNeighborsOnStateChange(World world, BlockPos pos, BlockState state) {
		((RTIWorld)world).dispatchBlockUpdates(pos, null, state.getBlock(), Tweaks.RedstoneWire.BLOCK_UPDATE_ORDER.get());
	}
	
	private int getWirePower(World world, BlockPos pos, Direction dir) {
		return getWirePower(world, pos, world.getBlockState(pos), dir);
	}
	
	private int getWirePower(BlockView world, BlockPos pos, BlockState state, Direction dir) {
		if (RedstoneWireHelper.emitsPowerTo(world, pos, state, dir)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity instanceof PowerBlockEntity) {
				PowerBlockEntity powerBlockEntity = ((PowerBlockEntity)blockEntity);
				
				powerBlockEntity.ensureCorrectPower(state);
				return powerBlockEntity.getPower();
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
					
					return PistonHelper.connectsToWire(pushedBlock.isOf(Blocks.STICKY_PISTON));
				}
			}
			
			return false;
		}
		
		return connectsTo(state, direction);
	}
}
