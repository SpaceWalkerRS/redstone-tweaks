package redstonetweaks.mixin.server;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AirBlock;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import redstonetweaks.setting.settings.Tweaks;

@Mixin(AirBlock.class)
public class AirBlockMixin {
	
	@ModifyVariable(
			method = "<init>",
			argsOnly = true,
			at = @At(
					value = "HEAD"
			)
	)
	private static Settings onInitModifySettings(Settings settings) {
		AbstractBlock.ContextPredicate solidPredicate = (state, world, pos) -> {
			if (Tweaks.Shulker.IS_SOLID.get() && world instanceof World) {
				List<ShulkerEntity> closedShulkers = ((World)world).getEntitiesByType(EntityType.SHULKER, new Box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D).offset(pos), (shulker) -> {
					return shulker.getPeekAmount() == 0;
				});
				
				return !closedShulkers.isEmpty();
			}
			
			return false;
		};
		
		return settings.solidBlock(solidPredicate);
	}
}
