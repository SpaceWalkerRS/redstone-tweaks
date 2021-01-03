package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.google.common.collect.Iterables;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import redstonetweaks.mixinterfaces.RTIThreadedAnvilChunkStorage;

@Mixin(ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorageMixin implements RTIThreadedAnvilChunkStorage {
	
	@Shadow private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> chunkHolders;
	
	@Override
	public Iterable<ChunkHolder> getEntryIterator() {
		return Iterables.unmodifiableIterable(chunkHolders.values());
	}
}
