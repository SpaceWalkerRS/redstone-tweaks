package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.util.DyeColor;
import net.minecraft.world.TickPriority;
import redstonetweaks.interfaces.mixin.RTISettings;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(Blocks.class)
public class BlocksMixin {
	
	@Redirect(
			method = "<clinit>",
			slice = @Slice(
					from = @At(
							value = "CONSTANT",
							args = "stringValue=oak_wood"
					),
					to = @At(
							value = "FIELD",
							target = "Lnet/minecraft/block/Blocks;STRIPPED_OAK_WOOD:Lnet/minecraft/block/Block;"
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/AbstractBlock$Settings;of(Lnet/minecraft/block/Material;Lnet/minecraft/block/MaterialColor;)Lnet/minecraft/block/AbstractBlock$Settings;"
			)
	)
	private static Settings wood(Material material, MaterialColor color) {
		TickPriority newTickPriority;
		if (color == MaterialColor.WOOD) {
			newTickPriority = TickPriority.EXTREMELY_HIGH;
		} else if (color == MaterialColor.SPRUCE) {
			newTickPriority = TickPriority.VERY_HIGH;
		} else if (color == MaterialColor.SAND) {
			newTickPriority = TickPriority.HIGH;
		} else if (color == MaterialColor.DIRT) {
			newTickPriority = TickPriority.NORMAL;
		} else if (color == MaterialColor.GRAY) {
			newTickPriority = TickPriority.LOW;
		} else if (color == MaterialColor.BROWN) {
			newTickPriority = TickPriority.VERY_LOW;
		} else {
			return Settings.of(material, color);
		}
		
		return ((RTISettings)Settings.of(material, color)).tickPriorityOverride((tickPriority) -> {
			return Tweaks.PropertyOverrides.WOOD_TICK_PRIORITY.get() ? newTickPriority : tickPriority;
		});
	}
	
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
		return ((RTISettings)Settings.of(material, color)).forceMicroTickMode(() -> Tweaks.PropertyOverrides.TERRACOTTA_MICRO_TICK_MODE.get());
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
			return Tweaks.PropertyOverrides.TERRACOTTA_DELAY.get() ? newDelay : oldDelay;
		});
	}
	
	@Redirect(
			method = "<clinit>",
			slice = @Slice(
					from = @At(
							value = "CONSTANT",
							args = "stringValue=white_wool"
					),
					to = @At(
							value = "FIELD",
							target = "Lnet/minecraft/block/Blocks;MOVING_PISTON:Lnet/minecraft/block/Block;"
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/AbstractBlock$Settings;of(Lnet/minecraft/block/Material;Lnet/minecraft/block/MaterialColor;)Lnet/minecraft/block/AbstractBlock$Settings;"
			)
	)
	private static Settings wool(Material material, MaterialColor color) {
		int newPower = Math.max(0, color.id - MaterialColor.ORANGE.id + 1);
		
		return ((RTISettings)Settings.of(material, color)).weakPowerOverride((oldPower) -> {
			return Tweaks.PropertyOverrides.WOOL_WEAK_POWER.get() ? newPower : oldPower;
		});
	}
	
	@Redirect(
			method = "<clinit>",
			slice = @Slice(
					from = @At(
							value = "CONSTANT",
							args = "stringValue=white_concrete"
					),
					to = @At(
							value = "FIELD",
							ordinal = 1,
							shift = Shift.BEFORE,
							target = "Lnet/minecraft/block/Blocks;WHITE_CONCRETE:Lnet/minecraft/block/Block;"
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/AbstractBlock$Settings;of(Lnet/minecraft/block/Material;Lnet/minecraft/util/DyeColor;)Lnet/minecraft/block/AbstractBlock$Settings;"
			)
	)
	private static Settings concrete(Material material, DyeColor color) {
		int newPower = color.getId();
		
		return ((RTISettings)Settings.of(material, color)).hardStrongOverride((oldPower) -> {
			return Tweaks.PropertyOverrides.CONCRETE_STRONG_POWER.get() ? newPower : oldPower;
		});
	}
	
	@Redirect(
			method = "<clinit>",
			slice = @Slice(
					from = @At(
							value = "CONSTANT",
							args = "stringValue=crimson_hyphae"
					)
			),
			at = @At(
					value = "INVOKE",
					ordinal = 0,
					target = "Lnet/minecraft/block/AbstractBlock$Settings;of(Lnet/minecraft/block/Material;Lnet/minecraft/block/MaterialColor;)Lnet/minecraft/block/AbstractBlock$Settings;"
			)
	)
	private static Settings crimsonHyphae(Material material, MaterialColor color) {
		return ((RTISettings)Settings.of(material, color)).tickPriorityOverride((tickPriority) -> {
			return Tweaks.PropertyOverrides.WOOD_TICK_PRIORITY.get() ? TickPriority.EXTREMELY_LOW : tickPriority;
		});
	}

	@Redirect(
			method = "<clinit>",
			slice = @Slice(
					from = @At(
							value = "CONSTANT",
							args = "stringValue=warped_hyphae"
					)
			),
			at = @At(
					value = "INVOKE",
					ordinal = 0,
					target = "Lnet/minecraft/block/AbstractBlock$Settings;of(Lnet/minecraft/block/Material;Lnet/minecraft/block/MaterialColor;)Lnet/minecraft/block/AbstractBlock$Settings;"
			)
	)
	private static Settings warpedHyphae(Material material, MaterialColor color) {
		TickPriority[] priorities = TickPriority.values();
		
		int firstIndex = priorities[0].getIndex();
		int bound = priorities.length;
		
		return ((RTISettings)Settings.of(material, color)).tickPriorityOverride((tickPriority) -> {
			int index = firstIndex + new Random().nextInt(bound);
			TickPriority randomTickPriority = TickPriority.byIndex(index);
			
			return Tweaks.PropertyOverrides.WOOD_TICK_PRIORITY.get() ? randomTickPriority : tickPriority;
		});
	}
}
