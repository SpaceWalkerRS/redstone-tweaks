package redstonetweaks.helper;

import net.minecraft.server.world.ChunkHolder;

public interface ThreadedAnvilChunkStorageHelper {
	
	public Iterable<ChunkHolder> getEntryIterator();
}
