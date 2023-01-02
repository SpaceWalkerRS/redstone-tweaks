package redstone.tweaks.mixin.common.sticky;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(CrossCollisionBlock.class)
public class CrossCollisionBlockMixin implements BlockOverrides {

	@Shadow private static Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;

	@Override
	public boolean isSticky(BlockState state) {
		return Tweaks.Global.stickyConnections();
	}

	@Override
	public boolean isStickyToNeighbor(Level level, BlockPos pos, BlockState state, BlockPos neighborPos, BlockState neighborState, Direction dir, Direction moveDir) {
		if (!neighborState.is(block())) {
			return false;
		}

		BooleanProperty connection = PROPERTY_BY_DIRECTION.get(dir);

		if (connection == null) {
			return true; // vertical connection, always sticky
		}

		BooleanProperty neighborConnection = PROPERTY_BY_DIRECTION.get(dir.getOpposite());

		boolean connected = state.getValue(connection);
		boolean neighborConnected = neighborState.getValue(neighborConnection);

		return connected && neighborConnected;
	}
}
