package redstonetweaks.world.common;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import redstonetweaks.packet.types.RedstoneTweaksPacket;

public interface IIncompleteAction {
	
	public BlockPos getPos();
	
	public int getType();
	
	public double getViewDistance();
		
	public boolean tryContinue(World world);
	
	public RedstoneTweaksPacket toPacket();
	
}
