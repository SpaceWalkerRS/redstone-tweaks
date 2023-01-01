package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import redstone.tweaks.Tweaks;

@Mixin(ConcretePowderBlock.class)
public class ConcretePowderBlockMixin {

	@ModifyVariable(
		method = "<init>",
		argsOnly = true,
		at = @At(
			value = "HEAD"
		)
	)
	private static Properties rtTweakConductRedstone(Properties _properties, Block concrete, Properties properties) {
		if (concrete == Blocks.WHITE_CONCRETE) {
			properties.isRedstoneConductor((state, level, pos) -> {
				return Tweaks.WhiteConcretePowder.conductRedstone();
			});
		}

		return properties;
	}
}
