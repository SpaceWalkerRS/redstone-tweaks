package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.interfaces.mixin.RTIHopperBlockEntity;
import redstonetweaks.setting.Tweaks;
import redstonetweaks.setting.types.DirectionToBooleanSetting;

@Mixin(HopperBlock.class)
public abstract class HopperBlockMixin extends Block {
	
	public HopperBlockMixin(Settings settings) {
		super(settings);
	}

	@Shadow protected abstract void updateEnabled(World world, BlockPos pos, BlockState state);
	
	@Redirect(method = "updateEnabled", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onUpdateEnabledRedirectGetReceivedPower(World world1, BlockPos blockPos, World world, BlockPos pos, BlockState state) {
		return WorldHelper.isPowered(world, pos, state, false, getQC(), randQC());
	}
	
	@Redirect(method = "updateEnabled", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private boolean onUpdateEnabledRedirectSetBlockState(World world1, BlockPos pos1, BlockState newState, int flags, World world, BlockPos pos, BlockState state) {
		boolean enabled = state.get(Properties.ENABLED);
		
		int delay = getDelay(enabled);
		TickPriority priority = getTickPriority(enabled);
		
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, delay, priority);
		
		return true;
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		boolean enabled = state.get(Properties.ENABLED);
		boolean lazy = isLazy(enabled);
		boolean isReceivingPower = WorldHelper.isPowered(world, pos, state, true, getQC(), randQC());
		boolean shouldBeEnabled = lazy ? !enabled : !isReceivingPower;
		
		if (enabled != shouldBeEnabled) {
			if (Tweaks.Global.SPONTANEOUS_EXPLOSIONS.get() && shouldBeEnabled && isHopperOnCooldown(world, pos)) {
				WorldHelper.createSpontaneousExplosion(world, pos);
			} else {
				BlockState newState = state.with(Properties.ENABLED, shouldBeEnabled);
				
				world.setBlockState(pos, newState, 4);
				
				if (shouldBeEnabled == isReceivingPower) {
					updateEnabled(world, pos, newState);
				}
			}
		}
	}
	
	private boolean isHopperOnCooldown(World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (blockEntity instanceof HopperBlockEntity) {
			return ((RTIHopperBlockEntity)blockEntity).isHopperOnCooldown();
		}
		
		return false;
	}
	
	private DirectionToBooleanSetting getQC() {
		return Tweaks.Hopper.QC;
	}
	
	private boolean randQC() {
		return Tweaks.Hopper.RANDOMIZE_QC.get();
	}
	
	private int getDelay(boolean enabled) {
		return enabled ? Tweaks.Hopper.DELAY_RISING_EDGE.get() : Tweaks.Hopper.DELAY_FALLING_EDGE.get();
	}
	
	private boolean isLazy(boolean enabled) {
		return enabled ? Tweaks.Hopper.LAZY_RISING_EDGE.get() : Tweaks.Hopper.LAZY_FALLING_EDGE.get();
	}
	
	private TickPriority getTickPriority(boolean enabled) {
		return enabled ? Tweaks.Hopper.TICK_PRIORITY_RISING_EDGE.get() : Tweaks.Hopper.TICK_PRIORITY_FALLING_EDGE.get();
	}
}
