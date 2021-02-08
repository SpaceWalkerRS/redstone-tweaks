package redstonetweaks.world.client;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.packet.types.DoWorldTicksPacket;
import redstonetweaks.world.common.WorldTickHandler;

public class ClientWorldTickHandler extends WorldTickHandler {
	
	private final MinecraftClient client;
	
	public ClientWorldTickHandler(MinecraftClient client) {
		super();
		
		this.client = client;
		
		this.currentTask = Task.NONE;
	}
	
	public void onDisconnect() {
		setStatus(Status.IDLE);
		setCurrentTask(Task.NONE);
		setCurrentWorld(null);
	}
	
	public void onDoWorldTicksPacketReceived(DoWorldTicksPacket packet) {
		doWorldTicks = packet.doWorldTicks;
	}
	
	public void syncStatus(Status status) {
		setStatus(status);
	}
	
	public void syncWorld(String worldName) {
		if (worldName.equals(client.world.getRegistryKey().getValue().toString())) {
			setCurrentWorld(client.world);
		} else {
			setCurrentWorld(null);
		}
		
		setCurrentTask(Task.NONE);
	}
	
	public void syncTask(Task task) {
		if (currentWorld != null) {
			
			// Ending the previous task
			switch (currentTask) {
			case TICK_WORLD_BORDER:
				break;
			case PROCESS_WEATHER:
				break;
			case TICK_TIME:
				break;
			case TICK_CHUNK_SOURCE:
				break;
			case TICK_BLOCKS:
				break;
			case TICK_FLUIDS:
				break;
			case TICK_RAIDS:
				break;
			case PROCESS_BLOCK_EVENTS:
				break;
			case TICK_ENTITIES:
				break;
			case TICK_BLOCK_ENTITIES:
				((RTIWorld)currentWorld).finishTickingBlockEntities(profiler);
				break;
			case SWITCH_WORLD:
				break;
			default:
				break;
			}
			
			setCurrentTask(task);
			
			// Starting the new task
			switch (currentTask) {
			case TICK_WORLD_BORDER:
				break;
			case PROCESS_WEATHER:
				break;
			case TICK_TIME:
				break;
			case TICK_CHUNK_SOURCE:
				break;
			case TICK_BLOCKS:
				break;
			case TICK_FLUIDS:
				break;
			case TICK_RAIDS:
				break;
			case PROCESS_BLOCK_EVENTS:
				break;
			case TICK_ENTITIES:
				break;
			case TICK_BLOCK_ENTITIES:
				((RTIWorld)currentWorld).startTickingBlockEntities(false);
				break;
			case SWITCH_WORLD:
				break;
			default:
				break;
			}
		}
	}
	
	public void tickBlockEntity(BlockPos pos) {
		if (currentWorld != null) {
			BlockEntity blockEntity = currentWorld.getBlockEntity(pos);
			
			if (blockEntity != null) {
				((RTIWorld)currentWorld).tickBlockEntity(blockEntity, profiler);
			}
		}
	}
}
