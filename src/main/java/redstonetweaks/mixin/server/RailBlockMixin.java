package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailBlock;
import net.minecraft.block.RailPlacementHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import redstonetweaks.interfaces.mixin.RTIRail;
import redstonetweaks.interfaces.mixin.RTIRailPlacementHelper;
import redstonetweaks.setting.Tweaks;
import redstonetweaks.setting.types.DirectionToBooleanSetting;

@Mixin(RailBlock.class)
public abstract class RailBlockMixin extends AbstractRailBlock implements RTIRail {
	
	protected RailBlockMixin(boolean allowCurves, Settings settings) {
		super(allowCurves, settings);
	}
	
	@Redirect(method = "updateBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RailBlock;updateBlockState(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;"))
	private BlockState onUpdateBlockStateRedirectUpdateBlockState(RailBlock rail, World world, BlockPos pos, BlockState state, boolean forceUpdate) {
		int delay = getDelay();
		
		if (delay == 0) {
			return updateBlockState(world, pos, state, false);
		} else if (!world.isClient() && !world.getBlockTickScheduler().isTicking(pos, rail)) {
			world.getBlockTickScheduler().schedule(pos, rail, delay, getTickPriority());
		}
		
		return state;
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (((RTIRailPlacementHelper)new RailPlacementHelper(world, pos, state)).neighborCount() == 3) {
			updateBlockState(world, pos, state, false);
		}
	}
	
	public DirectionToBooleanSetting getQC() {
		return Tweaks.Rail.QC;
	}
	
	public boolean randQC() {
		return Tweaks.Rail.RANDOMIZE_QC.get();
	}
	
	public int getDelay() {
		return Tweaks.Rail.DELAY.get();
	}
	
	public TickPriority getTickPriority() {
		return Tweaks.Rail.TICK_PRIORITY.get();
	}
}
