package redstonetweaks.helper;

import net.minecraft.block.AbstractBlock;
import net.minecraft.util.math.Direction;

public abstract class AbstractBlockHelper extends AbstractBlock {
	
	public AbstractBlockHelper(Settings settings) {
		super(settings);
	}
	
	public static final Direction[] FACINGS = AbstractBlock.FACINGS;
	
}
