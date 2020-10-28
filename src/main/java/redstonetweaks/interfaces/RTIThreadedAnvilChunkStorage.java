package redstonetweaks.interfaces;

import net.minecraft.server.world.ChunkHolder;

public interface RTIThreadedAnvilChunkStorage {
	
	public Iterable<ChunkHolder> getEntryIterator();
	
}
