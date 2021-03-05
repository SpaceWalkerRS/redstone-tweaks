package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.At.Shift;

import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;

import redstonetweaks.interfaces.mixin.RTISettings;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(Blocks.class)
public class BlocksMixin {
	
	@Redirect(
			method = "<clinit>",
			slice = @Slice(
					from = @At(
							value = "CONSTANT",
							args = "stringValue=terracotta"
					)
			),
			at = @At(
					value = "INVOKE",
					ordinal = 0,
					target = "Lnet/minecraft/block/AbstractBlock$Settings;of(Lnet/minecraft/block/Material;Lnet/minecraft/block/MaterialColor;)Lnet/minecraft/block/AbstractBlock$Settings;"
			)
	)
	private static Settings terracotta(Material material, MaterialColor color) {
		return ((RTISettings)Settings.of(material, color)).forceMicroTickMode(() -> Tweaks.Global.TERRACOTTA_FORCES_MICRO_TICK_MODE.get());
	}
	
	@Redirect(
			method = "<clinit>",
			slice = @Slice(
					from = @At(
							value = "CONSTANT",
							args = "stringValue=white_terracotta"
					),
					to = @At(
							value = "FIELD",
							shift = Shift.AFTER,
							target = "Lnet/minecraft/block/MaterialColor;BLACK_TERRACOTTA:Lnet/minecraft/block/MaterialColor;"
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/AbstractBlock$Settings;of(Lnet/minecraft/block/Material;Lnet/minecraft/block/MaterialColor;)Lnet/minecraft/block/AbstractBlock$Settings;"
			)
	)
	private static Settings coloredTerracotta(Material material, MaterialColor color) {
		int newDelay = color.id - MaterialColor.WHITE_TERRACOTTA.id;
		
		return ((RTISettings)Settings.of(material, color)).delayOverride((oldDelay) -> {
			return Tweaks.Global.TERRACOTTA_OVERRIDES_DELAY.get() ? newDelay : oldDelay;
		});
	}
}
