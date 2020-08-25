package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin extends AbstractBlock {

	public NoteBlockMixin(Settings settings) {
		super(settings);
	}

	@Shadow protected abstract void playNote(World world, BlockPos pos);

	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/NoteBlock;playNote(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
	private void onNeighborUpdateRedirectPlayNote(NoteBlock noteBlock, World world, BlockPos pos) {
		if (!world.getBlockTickScheduler().isTicking(pos, noteBlock)) {
			int delay = NOTE_BLOCK.get(DELAY);
			if (delay > 0) {
				world.getBlockTickScheduler().schedule(pos, noteBlock, delay, NOTE_BLOCK.get(TICK_PRIORITY));
			} else {
				playNote(world, pos);
			}
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (NOTE_BLOCK.get(LAZY) || world.isReceivingRedstonePower(pos)) {
			playNote(world, pos);
		}
	}
}
