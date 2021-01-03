package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.mixinterfaces.RTIRail;

@Mixin(AbstractRailBlock.class)
public class AbstractRailBlockMixin {
	
	@Redirect(method = "updateBlockState(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isReceivingRedstonePower(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean onUpdateBlockStateRedirectIsReceivingRedstonePower(World world1, BlockPos blockPos, World world, BlockPos pos, BlockState state, boolean forceUpdate) {
		if (state.isOf(Blocks.RAIL)) {
			return WorldHelper.isPowered(world, pos, state, false, ((RTIRail)this).getQC(), ((RTIRail)this).randQC());
		}
		return world.isReceivingRedstonePower(pos);
	}
}
