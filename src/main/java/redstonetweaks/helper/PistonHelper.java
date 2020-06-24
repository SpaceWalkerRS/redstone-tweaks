package redstonetweaks.helper;

import static redstonetweaks.setting.Settings.quasiConnectivityDown;
import static redstonetweaks.setting.Settings.quasiConnectivityEast;
import static redstonetweaks.setting.Settings.quasiConnectivityNorth;
import static redstonetweaks.setting.Settings.quasiConnectivitySouth;
import static redstonetweaks.setting.Settings.quasiConnectivityUp;
import static redstonetweaks.setting.Settings.quasiConnectivityWest;
import static redstonetweaks.setting.Settings.randomizeQuasiConnectivity;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

// This class declares methods for the piston mixin classes
// that also need to be accessible for other classes.
public class PistonHelper {
	
	public static boolean shouldExtend(World world, BlockPos pos, Direction facing) {
		Direction[] directions = Direction.values();
		for (Direction direction : directions) {
			if (direction != facing && world.isEmittingRedstonePower(pos.offset(direction), direction)) {
				return true;
			}
		}
		
		if (world.isEmittingRedstonePower(pos, Direction.DOWN)) {
			return true;
		} else {
			// For all positions adjacent to the piston,
			// we check if Quasi-Connectivity is enabled in that direction
			// or if the randomizeQuasiConnectivity setting is enabled,
			// and if so, check if that position is receiving redstone power.
			boolean randQC = randomizeQuasiConnectivity.get();
			if (randQC ? (new Random()).nextBoolean() : quasiConnectivityDown.get()) {
				BlockPos blockPos1 = pos.down();
				for (Direction direction : directions) {
					if (direction != Direction.UP && world.isEmittingRedstonePower(blockPos1.offset(direction), direction)) {
						return true;
					}
				}
			}
			if (randQC ? (new Random()).nextBoolean() : quasiConnectivityEast.get()) {
				BlockPos blockPos2 = pos.east();
				for (Direction direction : directions) {
					if (direction != Direction.WEST && world.isEmittingRedstonePower(blockPos2.offset(direction), direction)) {
						return true;
					}
				}
			}
			if (randQC ? (new Random()).nextBoolean() : quasiConnectivityNorth.get()) {
				BlockPos blockPos3 = pos.north();
				for (Direction direction : directions) {
					if (direction != Direction.SOUTH && world.isEmittingRedstonePower(blockPos3.offset(direction), direction)) {
						return true;
					}
				}
			}
			if (randQC ? (new Random()).nextBoolean() : quasiConnectivitySouth.get()) {
				BlockPos blockPos4 = pos.south();
				for (Direction direction : directions) {
					if (direction != Direction.NORTH && world.isEmittingRedstonePower(blockPos4.offset(direction), direction)) {
						return true;
					}
				}
			}
			if (randQC ? (new Random()).nextBoolean() : quasiConnectivityUp.get()) {
				BlockPos blockPos5 = pos.up();
				for (Direction direction : directions) {
					if (direction != Direction.DOWN && world.isEmittingRedstonePower(blockPos5.offset(direction), direction)) {
						return true;
					}
				}
			}
			if (randQC ? (new Random()).nextBoolean() : quasiConnectivityWest.get()) {
				BlockPos blockPos6 = pos.west();
				for (Direction direction : directions) {
					if (direction != Direction.EAST && world.isEmittingRedstonePower(blockPos6.offset(direction), direction)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
