package redstonetweaks.packet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import redstonetweaks.helper.MinecraftClientHelper;
import redstonetweaks.world.common.WorldHandler.Task;

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
		((MinecraftClientHelper)client).getWorldHandler().onTaskSyncPacketReceived(this);
		((MinecraftClientHelper)client).getTickInfoLabelRenderer().onTaskSyncPacketReceived(this);
	}

}
