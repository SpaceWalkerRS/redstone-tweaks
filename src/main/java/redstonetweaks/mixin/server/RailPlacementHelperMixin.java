package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.RailPlacementHelper;
import redstonetweaks.interfaces.mixin.RTIRailPlacementHelper;

@Mixin(RailPlacementHelper.class)
public abstract class RailPlacementHelperMixin implements RTIRailPlacementHelper {
	
	@Shadow protected abstract int getNeighborCount();
	
	@Override
	public int neighborCount() {
		return getNeighborCount();
	}
}
