package redstone.tweaks.util;

public class MotionType {

	// Piston block event types. 0, 1 and 2 are used by vanilla.
	public static final int NONE               = -1;
	public static final int EXTEND             = 0;
	public static final int RETRACT            = 1;
	public static final int RETRACT_DROP_BLOCK = 2;
	public static final int EXTEND_BACKWARDS   = 3;
	public static final int RETRACT_FORWARDS   = 4;

	public static boolean isExtend(int type) {
		return type == EXTEND || type == EXTEND_BACKWARDS;
	}

	public static boolean isRetract(int type) {
		return type == RETRACT || type == RETRACT_DROP_BLOCK || type == RETRACT_FORWARDS;
	}
}
