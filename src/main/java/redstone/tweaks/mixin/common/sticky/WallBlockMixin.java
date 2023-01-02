package redstone.tweaks.mixin.common.sticky;

import java.util.EnumMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WallSide;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(WallBlock.class)
public class WallBlockMixin implements BlockOverrides {

	private static final Map<Direction, EnumProperty<WallSide>> WALL_SIDE_PROPERTIES = Util.make(new EnumMap<>(Direction.class), map -> {
		map.put(Direction.NORTH, WallBlock.NORTH_WALL);
		map.put(Direction.SOUTH, WallBlock.SOUTH_WALL);
		map.put(Direction.WEST, WallBlock.WEST_WALL);
		map.put(Direction.EAST, WallBlock.EAST_WALL);
	});

	@Override
	public boolean isSticky(BlockState state) {
		return Tweaks.Global.stickyConnections();
	}

	@Override
	public boolean isStickyToNeighbor(Level level, BlockPos pos, BlockState state, BlockPos neighborPos, BlockState neighborState, Direction dir, Direction moveDir) {
		if (!neighborState.is(block())) {
			return false;
		}

		EnumProperty<WallSide> connection = WALL_SIDE_PROPERTIES.get(dir);

		if (connection == null) {
			return dir == Direction.DOWN || state.getValue(WallBlock.UP); // vertical connection
		}

		EnumProperty<WallSide> neighborConnection = WALL_SIDE_PROPERTIES.get(dir.getOpposite());

		boolean connected = state.getValue(connection) != WallSide.NONE;
		boolean neighborConnected = neighborState.getValue(neighborConnection) != WallSide.NONE;

		return connected && neighborConnected;
	}
}
