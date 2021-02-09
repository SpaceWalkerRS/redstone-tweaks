package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.setting.Tweaks;
import redstonetweaks.setting.types.DirectionToBooleanSetting;

@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin extends AbstractBlock {
	
	public NoteBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Shadow protected abstract void playNote(World world, BlockPos pos);
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onNeighborUpdateRedirectGetReceivedPower(World world1, BlockPos blockPos, BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		return WorldHelper.isPowered(world, pos, state, false, getQC(), randQC());
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/NoteBlock;playNote(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
	private void onNeighborUpdateRedirectPlayNote(NoteBlock noteBlock, World world1, BlockPos pos1, BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!world.getBlockTickScheduler().isTicking(pos, noteBlock)) {
			TickSchedulerHelper.scheduleBlockTick(world, pos, state, getDelay(), getTickPriority());
		}
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (isLazy() || WorldHelper.isPowered(world, pos, state, true, getQC(), randQC())) {
			playNote(world, pos);
		}
	}
	
	private DirectionToBooleanSetting getQC() {
		return Tweaks.NoteBlock.QC;
	}
	
	private boolean randQC() {
		return Tweaks.NoteBlock.RANDOMIZE_QC.get();
	}
	
	private int getDelay() {
		return Tweaks.NoteBlock.DELAY.get();
	}
	
	private boolean isLazy() {
		return Tweaks.NoteBlock.LAZY.get();
	}
	
	private TickPriority getTickPriority() {
		return Tweaks.NoteBlock.TICK_PRIORITY.get();
	}
}
