package redstonetweaks.world.common;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import redstonetweaks.interfaces.mixin.RTIBlock;
import redstonetweaks.packet.types.IncompleteBlockActionPacket;
import redstonetweaks.packet.types.RedstoneTweaksPacket;

public class IncompleteBlockAction extends IncompleteAction<Block> {
	
	public IncompleteBlockAction(BlockPos pos, int type, Block block) {
		super(pos, type, block);
	}
	
	public IncompleteBlockAction(BlockPos pos, int type, double viewDistance, Block block) {
		super(pos, type, viewDistance, block);
	}
	
	@Override
	public boolean tryContinue(World world) {
		return ((RTIBlock)object).continueAction(world, pos, type);
	}
	
	@Override
	public RedstoneTweaksPacket toPacket() {
		return new IncompleteBlockActionPacket(this);
	}
}
