package redstonetweaks.world.server;

import net.minecraft.server.MinecraftServer;

public class ServerTickHandler {
	
	private final MinecraftServer server;
	
	private boolean isPaused;
	private long time;
	
	public ServerTickHandler(MinecraftServer server) {
		this.server = server;
		
		isPaused = false;
		time = 0;
	}
	
	public void pause() {
		isPaused = true;
	}
	
	public void resume() {
		isPaused = false;
	}
	
	public void advance(int count) {
		time = server.getWorlds().iterator().next().getTime() + count;
	}
	
	public boolean isPaused() {
		return isPaused;
	}
	
	public boolean shouldTick() {
		return !isPaused || server.getWorlds().iterator().next().getTime() < time;
	}
}
