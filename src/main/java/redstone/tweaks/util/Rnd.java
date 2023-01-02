package redstone.tweaks.util;

import net.minecraft.util.RandomSource;

public class Rnd {

	public static int nextInt(RandomSource rand, int origin, int bound) {
		return origin < bound ? rand.nextInt(origin, bound) : origin;
	}
}
