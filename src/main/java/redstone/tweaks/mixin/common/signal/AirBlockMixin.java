package redstone.tweaks.mixin.common.signal;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.AABB;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.IShulker;

@Mixin(AirBlock.class)
public class AirBlockMixin {
	
	@ModifyVariable(
		method = "<init>",
		argsOnly = true,
		at = @At(
			value = "HEAD"
		)
	)
	private static Properties onInitModifySettings(Properties properties) {
		return properties.isRedstoneConductor((state, blockGetter, pos) -> {
			if (Tweaks.Shulker.conductRedstone() && blockGetter instanceof Level) {
				Level level = (Level)blockGetter;
				Collection<Shulker> closedShulkers = level.getEntities(EntityType.SHULKER, new AABB(pos), shulker -> {
					return ((IShulker)shulker).rt_isClosed();
				});

				return !closedShulkers.isEmpty();
			}

			return false;
		});
	}
}
