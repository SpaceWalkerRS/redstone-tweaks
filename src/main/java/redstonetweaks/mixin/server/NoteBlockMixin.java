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

import redstonetweaks.helper.WorldHelper;
import redstonetweaks.setting.types.DirectionalBooleanSetting;

@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin extends AbstractBlock {
	
	public NoteBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Shadow protected abstract void playNote(World world, BlockPos pos);
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onNeighborUpdateRedirectGetReceivedPower(World world1, BlockPos blockPos, BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		return world.isReceivingRedstonePower(pos) || WorldHelper.isQCPowered(world, pos, state, false, getQC(), randQC());
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/NoteBlock;playNote(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
	private void onNeighborUpdateRedirectPlayNote(NoteBlock noteBlock, World world, BlockPos pos) {
		if (!world.getBlockTickScheduler().isTicking(pos, noteBlock)) {
			int delay = getDelay();
			if (delay == 0) {
				playNote(world, pos);
			} else {
				world.getBlockTickScheduler().schedule(pos, noteBlock, delay, getTickPriority());
			}
		}
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (isLazy() || world.isReceivingRedstonePower(pos) || WorldHelper.isQCPowered(world, pos, state, true, getQC(), randQC())) {
			playNote(world, pos);
		}
	}
	
	private DirectionalBooleanSetting getQC() {
		return redstonetweaks.setting.Settings.NoteBlock.QC;
	}
	
	private boolean randQC() {
		return redstonetweaks.setting.Settings.NoteBlock.RANDOMIZE_QC.get();
	}
	
	private int getDelay() {
		return redstonetweaks.setting.Settings.NoteBlock.DELAY.get();
	}
	
	private boolean isLazy() {
		return redstonetweaks.setting.Settings.NoteBlock.LAZY.get();
	}
	
	private TickPriority getTickPriority() {
		return redstonetweaks.setting.Settings.NoteBlock.TICK_PRIORITY.get();
	}
}
