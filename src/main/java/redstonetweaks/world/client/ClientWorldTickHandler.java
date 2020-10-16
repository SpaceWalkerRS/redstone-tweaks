package redstonetweaks.world.client;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;

import redstonetweaks.helper.WorldHelper;
import redstonetweaks.packet.TaskSyncPacket;
import redstonetweaks.packet.TickBlockEntityPacket;
import redstonetweaks.packet.TickStatusPacket;
import redstonetweaks.packet.WorldSyncPacket;
import redstonetweaks.packet.DoWorldTicksPacket;
import redstonetweaks.world.common.WorldTickHandler;

public class ClientWorldTickHandler extends WorldTickHandler {
	
	private final MinecraftClient client;
	
	public ClientWorldTickHandler(MinecraftClient client) {
		super();
		this.client = client;
		
		this.currentTask = Task.NONE;
	}
	
	public void onDoWorldTicksPacketReceived(DoWorldTicksPacket packet) {
		doWorldTicks = packet.doWorldTicks;
	}
	
	public void onTickStatusPacketReceived(TickStatusPacket packet) {
		setStatus(packet.status);
	}
	
	public void onWorldSyncPacketReceived(WorldSyncPacket packet) {
		if (packet.worldName.equals(client.world.getRegistryKey().getValue().toString())) {
			setCurrentWorld(client.world);
		} else {
			setCurrentWorld(null);
		}
		
		setCurrentTask(Task.NONE);
	}
	
	public void onTaskSyncPacketReceived(TaskSyncPacket packet) {
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
				((WorldHelper)currentWorld).finishTickingBlockEntities(profiler);
				break;
			case SWITCH_WORLD:
				break;
			default:
				break;
			}
			
			setCurrentTask(packet.currentTask);
			
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
				((WorldHelper)currentWorld).startTickingBlockEntities(false);
				break;
			case SWITCH_WORLD:
				break;
			default:
				break;
			}
		}
	}
	
	public void onTickBlockEntityPacketReveiced(TickBlockEntityPacket packet) {
		if (currentWorld != null) {
			BlockEntity blockEntity = currentWorld.getBlockEntity(packet.pos);
			if (blockEntity != null) {
				((WorldHelper)currentWorld).tickBlockEntity(blockEntity, profiler);
			}
		}
	}
}
