package redstonetweaks.mixin.server;

import java.util.Optional;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.chunk.WorldChunk;

import redstonetweaks.helper.ServerChunkManagerHelper;
import redstonetweaks.helper.ThreadedAnvilChunkStorageHelper;

@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin implements ServerChunkManagerHelper {
	
	@Shadow @Final public ServerWorld world;
	@Shadow @Final public ThreadedAnvilChunkStorage threadedAnvilChunkStorage;

	
	@Override
	public void broadcastChunkData() {
		world.getProfiler().push("chunks");
		
		if (!world.isDebugWorld()) {
			world.getProfiler().push("pollingChunks");
			
			((ThreadedAnvilChunkStorageHelper)threadedAnvilChunkStorage).getEntryIterator().forEach((chunkHolder) -> {
				Optional<WorldChunk> optional = (chunkHolder.getTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK)).left();
				if (optional.isPresent()) {
					world.getProfiler().push("broadcast");
					chunkHolder.flushUpdates(optional.get());
					world.getProfiler().pop();
				}
			});
			
			world.getProfiler().pop();
		}
		
		world.getProfiler().pop();
	}
}
