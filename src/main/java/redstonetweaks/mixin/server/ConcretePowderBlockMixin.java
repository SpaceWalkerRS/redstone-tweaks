package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConcretePowderBlock;

import redstonetweaks.setting.settings.Tweaks;

@Mixin(ConcretePowderBlock.class)
public class ConcretePowderBlockMixin {
	
	@ModifyVariable(
			method = "<init>",
			argsOnly = true,
			at = @At(
					value = "HEAD"
			)
	)
	private static Settings onInitModifySettings(Settings oldSettings, Block hardened, Settings settings) {
		if (hardened == Blocks.WHITE_CONCRETE) {
			AbstractBlock.ContextPredicate solidPredicate = (state, world, pos) -> {
				return Tweaks.WhiteConcretePowder.IS_SOLID.get();
			};
			
			return settings.solidBlock(solidPredicate);
		}
		
		return settings;
	}
}
