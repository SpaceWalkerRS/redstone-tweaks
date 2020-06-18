package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstonetweaks.helper.PistonHelper;
import redstonetweaks.setting.Settings;

@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin {
	
	@Shadow @Final public static BooleanProperty EXTENDED;
	
	@Redirect(method = "onPlaced", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlock;tryMove(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void onPlacedRedirectTryMove(PistonBlock piston, World world, BlockPos pos, BlockState state) {
		this.newTryMove(world, pos, state, false);
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlock;tryMove(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void neighborUpdateRedirectTryMove(PistonBlock piston, World world, BlockPos pos, BlockState state) {
		this.newTryMove(world, pos, state, false);
	}
	
	@Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlock;tryMove(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void onBlockAddedRedirectTryMove(PistonBlock piston, World world, BlockPos pos, BlockState state) {
		this.newTryMove(world, pos, state, false);
	}
	
	// The shouldExtend method is declared in the PistonHelper class
	// because it needs to be accessible from other classes as well.
	@Overwrite
	private boolean shouldExtend(World world, BlockPos pos, Direction facing) {
		return PistonHelper.shouldExtend(world, pos, facing);
	}
	
	// If the pistonsCheckPoweredOnce setting is enabled,
	// the value of bl is is inferred from the current value
	// of the EXTENDED property.
	@Redirect(method = "onBlockAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlock;shouldExtend(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z"))
	private boolean onOnBlockActionRedirectShouldExtend(PistonBlock piston, World world, BlockPos pos, Direction direction) {
		return (boolean)Settings.pistonsCheckPoweredOnce.get() ? !world.getBlockState(pos).get(EXTENDED) : this.shouldExtend(world, pos, direction);
	}
	
	// The block entity of the moving block in front of
	// the piston head should only be finished if the
	// fastBlockDropping setting is enabled.
	@Redirect(method = "onBlockAction", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/block/entity/PistonBlockEntity;finish()V"))
	private void onOnBlockActionRedirectFinishBlockEntity(PistonBlockEntity pistonBlockEntity) {
		if ((boolean)Settings.fastBlockDropping.get()) {
			pistonBlockEntity.finish();
		}
	}
	
	// If the piston is powered but unable to extend and
	// the forceUpdatePoweredPistons setting is enabled,
	// a block tick should be scheduled in the next tick.
	@Inject(method = "onBlockAction", at = @At(value = "RETURN", ordinal = 1))
	private void onBlockActionCannotExtend(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Float> cir) {
		if ((boolean)Settings.forceUpdatePoweredPistons.get()) {
			world.getBlockTickScheduler().schedule(pos, (PistonBlock)(Object)this, 1);
		}
	}
	
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!world.isClient) {
	       this.newTryMove(world, pos, state, true);
	    }
	}
	
	// The onScheduledTick argument tells us if this method is called
	// from inside the scheduledTick method.
	private void newTryMove(World world, BlockPos pos, BlockState state, boolean onScheduledTick) {
		Direction facing = state.get(PistonBlock.FACING);
		int activationDelay = (int)Settings.pistonActivationDelay.get();
		
		// Usually the tryMove method is only called from inside the
		// onPlaced, neighborUpdate and onBlockAdded methods.
		// However, RedstoneTweaks allows players to add activation delay to pistons.
		// This is done using scheduled ticks.
		// Redstone Tweaks also adds the pistonsCheckPoweredOnce setting
		// If this setting is enabled, pistons should not check for power
		// if the newTryMove method is called from scheduledTick method.
		// Instead, the value of shouldExtend is inferred from the current
		// value of the EXTENDED property.
		boolean shouldExtend = (onScheduledTick && (boolean)Settings.pistonsCheckPoweredOnce.get()) ? !state.get(EXTENDED) : this.shouldExtend(world, pos, facing);
		
		if (shouldExtend && !state.get(EXTENDED)) {
			if ((new PistonHandler(world, pos, facing, true)).calculatePush()) {
				if (activationDelay == 0 || onScheduledTick) {
					world.addBlockAction(pos, (PistonBlock)(Object)this, 0, facing.getId());
				} else {
					world.getBlockTickScheduler().schedule(pos, (PistonBlock)(Object)this, activationDelay);
				}
			// We must check if the piston is currently not extending.
			// Otherwise the piston will continually pulse if the
			// forceUpdatePoweredPistons setting and the
			// pistonsCheckPoweredOnce settings are both enabled.
			} else if ((boolean)Settings.forceUpdatePoweredPistons.get() && !this.isExtending(world, pos, state) && this.shouldExtend(world, pos, facing)) {
				world.getBlockTickScheduler().schedule(pos, (PistonBlock)(Object)this, 1);
			}
		} else if (!shouldExtend && state.get(EXTENDED)) {
			if (!(boolean)Settings.extendingPistonsIgnoreUpdates.get() || !this.isExtending(world, pos, state)) {
				if (activationDelay == 0 || onScheduledTick) {
					world.addBlockAction(pos, (PistonBlock)(Object)this, 1, facing.getId());
				} else {
					world.getBlockTickScheduler().schedule(pos, (PistonBlock)(Object)this, activationDelay);
				}
			}
		}
	}
	
	// The base of an extending piston is a piston block with the
	// EXTENDED property set to true. So to determine whether
	// the piston is extending, we need to look at the block
	// in front of the piston. If that block is a moving block
	// that is extending and facing the same direction as the piston, 
	// then we can conclude that the piston is extending.
	private boolean isExtending(World world, BlockPos pos, BlockState state) {
		Direction facing = state.get(PistonBlock.FACING);
		BlockPos pos2 = pos.offset(facing);
		BlockState state2 = world.getBlockState(pos2);
		if (state2.getBlock() == Blocks.MOVING_PISTON && state2.get(PistonBlock.FACING) == facing) {
			BlockEntity blockEntity = world.getBlockEntity(pos2);
			if (blockEntity instanceof PistonBlockEntity) {
				PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
				if (pistonBlockEntity.getFacing() == facing && pistonBlockEntity.isExtending()) {
					return true;
				}
			}
		}
		return false;
	}
}
