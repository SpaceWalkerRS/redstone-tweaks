package redstonetweaks.util;

import net.minecraft.util.math.Box;

public class BoxUtils {
	
	public static Box[] getExpansionBoxes(Box box, double expandX, double expandY, double expandZ) {
		Box[] boxes = new Box[3];
		
		boolean positiveX = (expandX >= 0.0D);
		boolean positiveY = (expandY >= 0.0D);
		boolean positiveZ = (expandZ >= 0.0D);
		
		double x = positiveX ? box.maxX + expandX : box.minX + expandX;
		double y = positiveY ? box.maxY + expandY : box.minY + expandY;
		double z = positiveZ ? box.maxZ + expandZ : box.minZ + expandZ;
		
		boxes[0] = new Box(positiveX ? box.maxX : x, positiveY ? box.minY : y, positiveZ ? box.minZ : z, positiveX ? x : box.minX, positiveY ? y : box.maxY, positiveZ ? z : box.maxZ);
		boxes[1] = new Box(positiveX ? box.minX : x, positiveY ? box.maxY : y, positiveZ ? box.minZ : z, positiveX ? x : box.maxX, positiveY ? y : box.minY, positiveZ ? z : box.maxZ);
		boxes[2] = new Box(positiveX ? box.minX : x, positiveY ? box.minY : y, positiveZ ? box.maxZ : z, positiveX ? x : box.maxX, positiveY ? y : box.maxY, positiveZ ? z : box.minZ);
		
		return boxes;
	}
}
