package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import redstonetweaks.mixinterfaces.RTIMinecraftClient;
import redstonetweaks.world.common.WorldTickHandler.Task;

public class TaskSyncPacket extends RedstoneTweaksPacket {
	
	public Task currentTask;
	
	public TaskSyncPacket() {
		
	}
	
	public TaskSyncPacket(Task currentTask) {
		this.currentTask = currentTask;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeByte(currentTask.getIndex());
	}

	@Override
	public void decode(PacketByteBuf buffer) {
		currentTask = Task.fromIndex(buffer.readByte());
	}

	@Override
	public void execute(MinecraftServer server) {
		
	}

	@Override
	public void execute(MinecraftClient client) {
		((RTIMinecraftClient)client).getWorldTickHandler().onTaskSyncPacketReceived(this);
		((RTIMinecraftClient)client).getTickInfoLabelRenderer().onTaskSyncPacketReceived(this);
	}

}
