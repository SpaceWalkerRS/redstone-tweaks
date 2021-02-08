package redstonetweaks.packet.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import redstonetweaks.interfaces.mixin.RTIMinecraftClient;
import redstonetweaks.world.common.WorldTickHandler.Task;

public class TaskSyncPacket extends RedstoneTweaksPacket {
	
	public Task task;
	
	public TaskSyncPacket() {
		
	}
	
	public TaskSyncPacket(Task currentTask) {
		this.task = currentTask;
	}
	
	@Override
	public void encode(PacketByteBuf buffer) {
		buffer.writeByte(task.getIndex());
	}

	@Override
	public void decode(PacketByteBuf buffer) {
		task = Task.fromIndex(buffer.readByte());
	}

	@Override
	public void execute(MinecraftServer server) {
		
	}

	@Override
	public void execute(MinecraftClient client) {
		((RTIMinecraftClient)client).getWorldTickHandler().syncTask(task);
		((RTIMinecraftClient)client).getTickInfoLabelRenderer().syncTask(task);
	}

}
