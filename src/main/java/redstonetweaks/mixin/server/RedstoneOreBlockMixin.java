package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import redstonetweaks.block.capacitor.CapacitorBehavior;
import redstonetweaks.block.entity.PowerBlockEntity;
import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(RedstoneOreBlock.class)
public abstract class RedstoneOreBlockMixin extends AbstractBlock implements BlockEntityProvider {
	
	@Shadow private native static void light(BlockState state, World world, BlockPos pos);
	
	public RedstoneOreBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Redirect(method = "onBlockBreakStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneOreBlock;light(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
	private void onOnBlockBreakStartRedirectLight(BlockState state, World world, BlockPos pos) {
		if (!isCapacitor()) {
			lightUp(world, pos, state);
		}
	}
	
	@Redirect(method = "onSteppedOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneOreBlock;light(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
	private void onOnSteppedOnRedirectLight(BlockState state, World world, BlockPos pos) {
		if (!isCapacitor()) {
			lightUp(world, pos, state);
		}
	}
	
	@Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneOreBlock;light(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
	private void onOnUseRedirectLight(BlockState state, World world, BlockPos pos) {
		if (!isCapacitor()) {
			lightUp(world, pos, state);
		}
	}
	
	@Inject(method = "light", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private static void onLightInjectAfterSetBlockState(BlockState state, World world, BlockPos pos, CallbackInfo ci) {
		if (Tweaks.RedstoneOre.POWER_STRONG.get() > 0) {
			updateNeighbors(world, pos, state);
		}
	}
	
	@Inject(method = "randomTick", cancellable = true, at = @At(value = "HEAD"))
	private void onRandomTickInjectAtHead(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (isCapacitor()) {
			ci.cancel();
		}
	}
	
	@Inject(method = "randomTick", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onRandomTickInjectAfterSetBlockState(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (Tweaks.RedstoneOre.POWER_STRONG.get() > 0) {
			updateNeighbors(world, pos, state);
		}
	}
	
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (isCapacitor() && !world.isClient()) {
			update(world, pos, state, false);
		}
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (isCapacitor()) {
			update(world, pos, state, true);
		} else {
			light(state, world, pos);
		}
	}
	
	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return Tweaks.RedstoneOre.CONNECTS_TO_WIRE.get();
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction facing) {
		if (state.get(Properties.LIT)) {
			if (isCapacitor()) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				
				if (blockEntity instanceof PowerBlockEntity) {
					return ((PowerBlockEntity)blockEntity).getPower();
				}
			} else {
				return Tweaks.RedstoneOre.POWER_WEAK.get();
			}
		}
		
		return 0;
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction facing) {
		return (state.get(Properties.LIT) && !Tweaks.RedstoneOre.CAPACITOR_BEHAVIOR.get().isEnabled()) ? Tweaks.RedstoneOre.POWER_STRONG.get() : 0;
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new PowerBlockEntity();
	}
	
	private boolean isCapacitor() {
		return Tweaks.RedstoneOre.CAPACITOR_BEHAVIOR.get().isEnabled();
	}
	
	private void lightUp(World world, BlockPos pos, BlockState state) {
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, Tweaks.RedstoneOre.DELAY.get(), Tweaks.RedstoneOre.TICK_PRIORITY.get());
	}
	
	private void update(World world, BlockPos pos, BlockState state, boolean onScheduledTick) {
		CapacitorBehavior capacitor = Tweaks.RedstoneOre.CAPACITOR_BEHAVIOR.get();
		
		CapacitorBehavior.Mode mode = capacitor.getMode();
		int stepSize = capacitor.getStepSize();
		
		PowerBlockEntity powerBlockEntity = getPowerBlockEntity(world, pos);
		
		int currentPower = powerBlockEntity.getPower();
		int powerReceived = WorldHelper.getStrongPowerNoWire(world, pos);
		int maxPower = Tweaks.Global.POWER_MAX.get();
		
		int newPower = currentPower;
		int desiredPower = currentPower;
		
		if (mode == CapacitorBehavior.Mode.MATCH_INPUT) {
			desiredPower = powerReceived;
		} else if (mode == CapacitorBehavior.Mode.CHARGE_INDEFINITELY) {
			desiredPower = (powerReceived == 0) ? 0 : maxPower;
		}
		
		if (currentPower != desiredPower) {
			int difference = desiredPower - currentPower;
			
			if (Math.abs(difference) <= stepSize) {
				newPower = desiredPower;
			} else {
				newPower = currentPower + stepSize * (currentPower < desiredPower ? 1 : -1);
			}
		}
		
		newPower = MathHelper.clamp(newPower, 0, maxPower);
		
		if (currentPower != newPower) {
			int fadeInDelay = capacitor.getIncrementDelay();
			int fadeOutDelay = capacitor.getDecrementDelay();
			
			if (onScheduledTick) {
				powerBlockEntity.setPower(newPower);
				
				if (newPower != desiredPower) {
					TickSchedulerHelper.scheduleBlockTick(world, pos, state, (newPower > desiredPower) ? fadeOutDelay : fadeInDelay, Tweaks.RedstoneOre.TICK_PRIORITY.get());
				}
				
				boolean wasLit = (currentPower != 0);
				boolean isLit = (newPower != 0);
				
				if (wasLit != isLit) {
					state = state.with(Properties.LIT, isLit);
					
					world.setBlockState(pos, state, 3);
				} else {
					world.updateNeighborsAlways(pos, state.getBlock());
				}
			} else {
				TickSchedulerHelper.scheduleBlockTick(world, pos, state, (newPower > currentPower) ? fadeInDelay : fadeOutDelay, Tweaks.RedstoneOre.TICK_PRIORITY.get());
			}
		}
	}
	
	private PowerBlockEntity getPowerBlockEntity(World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (blockEntity instanceof PowerBlockEntity) {
			return (PowerBlockEntity)blockEntity;
		}
		
		PowerBlockEntity powerBlockEntity = new PowerBlockEntity();
		
		world.setBlockEntity(pos, powerBlockEntity);
		
		return powerBlockEntity;
	}
	
	private static void updateNeighbors(World world, BlockPos pos, BlockState state) {
		((RTIWorld)world).dispatchBlockUpdates(pos, null, state.getBlock(), Tweaks.RedstoneOre.BLOCK_UPDATE_ORDER.get());
	}
}
