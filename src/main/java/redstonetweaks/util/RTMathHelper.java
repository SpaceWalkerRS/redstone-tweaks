package redstonetweaks.util;

public class RTMathHelper {
	
	public static int roundToMultiple(double i, int multiple) {
		return (int)Math.round(i / multiple) * multiple;
	}
}
