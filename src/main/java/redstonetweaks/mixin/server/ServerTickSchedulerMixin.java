package redstonetweaks.mixin.server;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ScheduledTick;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;

import redstonetweaks.helper.ServerTickSchedulerHelper;
import redstonetweaks.settings.Settings;

@Mixin(ServerTickScheduler.class)
public abstract class ServerTickSchedulerMixin<T> implements ServerTickSchedulerHelper, TickScheduler<T> {
	
	@Shadow ServerWorld world;
	@Shadow Predicate<T> invalidObjPredicate;
	@Shadow @Final Set<ScheduledTick<T>> scheduledTickActions;
	@Shadow @Final private TreeSet<ScheduledTick<T>> scheduledTickActionsInOrder;
	@Shadow @Final private List<ScheduledTick<T>> consumedTickActions;
	@Shadow @Final private Queue<ScheduledTick<T>> currentTickActions;
	@Shadow @Final private Consumer<ScheduledTick<T>> tickConsumer;
	
	private boolean isTicking;
	
	@Shadow abstract void addScheduledTick(ScheduledTick<T> scheduledTick);
	
	@Inject(method = "schedule", cancellable = true, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/server/world/ServerTickScheduler;addScheduledTick(Lnet/minecraft/world/ScheduledTick;)V"))
	private void onScheduledInjectBeforeAddScheduledTick(BlockPos pos, T object, int delay, TickPriority priority, CallbackInfo ci) {
		if (Settings.Global.DELAY_MULTIPLIER.get() == 0) {
			tickConsumer.accept(new ScheduledTick<>(pos, object, 0, priority));
		} else {
			if (Settings.Global.RANDOMIZE_TICK_PRIORITIES.get()) {
				int index = world.getRandom().nextInt(TickPriority.values().length) + TickPriority.values()[0].getIndex();
				priority = TickPriority.byIndex(index);
			}
			
			addScheduledTick(new ScheduledTick<>(pos, object, world.getTime() + getDelay(delay), priority));
		}
		
		ci.cancel();
	}
	
	@Override
	public boolean hasScheduledTickAtTime(BlockPos pos, Object object, int delay) {
		long time = world.getTime() + delay;
		for (ScheduledTick<T> tick : scheduledTickActions) {
			if (tick.pos.equals(pos) && tick.getObject() == object && tick.time == time) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void startTicking() {
		int counter = scheduledTickActionsInOrder.size();
		if (counter != scheduledTickActions.size()) {
			throw new IllegalStateException("TickNextTick list out of sync");
		}

		if (counter > 65536) {
			counter = 65536;
		}

		ServerChunkManager serverChunkManager = world.getChunkManager();
		Iterator<ScheduledTick<T>> iterator = scheduledTickActionsInOrder.iterator();
		world.getProfiler().push("cleaning");

		ScheduledTick<T> scheduledTick;
		while (counter > 0 && iterator.hasNext()) {
			scheduledTick = iterator.next();
			if (scheduledTick.time > world.getTime()) {
				break;
			}

			if (serverChunkManager.shouldTickBlock(scheduledTick.pos)) {
				iterator.remove();
				scheduledTickActions.remove(scheduledTick);
				currentTickActions.add(scheduledTick);
				--counter;
			}
		}

		world.getProfiler().pop();
		isTicking = true;
	}
	
	@Override
	public boolean tryContinueTicking() {
		if (isTicking) {
			ScheduledTick<T> scheduledTick = currentTickActions.poll();
					
			if (scheduledTick != null) {
				if (world.getChunkManager().shouldTickBlock(scheduledTick.pos)) {
					try {
						consumedTickActions.add(scheduledTick);
						tickConsumer.accept(scheduledTick);
					} catch (Throwable var8) {
						CrashReport crashReport = CrashReport.create(var8, "Exception while ticking");
						CrashReportSection crashReportSection = crashReport.addElement("Block being ticked");
						CrashReportSection.addBlockInfo(crashReportSection, scheduledTick.pos, (BlockState) null);
						throw new CrashException(crashReport);
					}
				} else {
					schedule(scheduledTick.pos, scheduledTick.getObject(), 0);
				}
				
				return true;
			} else {
				world.getProfiler().pop();
				consumedTickActions.clear();
				currentTickActions.clear();
				
				isTicking = false;
				
				return false;
			}
		} else {
			return false;
		}
	}
	
	private int getDelay(int delay) {
		int min = 1;
		int max = 127;
		
		if (Settings.Global.RANDOMIZE_DELAYS.get()) {
			delay = min + world.getRandom().nextInt(max);
		}
		return Settings.Global.DELAY_MULTIPLIER.get() * delay;
	}
}
