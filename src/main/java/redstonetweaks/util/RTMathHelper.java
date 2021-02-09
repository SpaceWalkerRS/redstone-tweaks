package redstonetweaks.util;

import java.util.Random;

public class RTMathHelper {
	
	public static int roundToMultiple(double i, int multiple) {
		return (int)Math.round(i / multiple) * multiple;
	}
	
	public static int randomInt(Random random, int min, int max) {
		return min >= max ? min : min + random.nextInt(max - min);
	}
}
