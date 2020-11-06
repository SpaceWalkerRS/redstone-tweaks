package redstonetweaks.world.common;

import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;

public abstract class WorldTickHandler {
	
	protected boolean doWorldTicks;
	protected Status status;
	protected World currentWorld;
	protected Profiler profiler;
	protected Task currentTask;
	protected boolean shouldSwitchTask;
	protected boolean doTasks;
	
	protected boolean inWorldTick;
	
	public WorldTickHandler() {
		this.doWorldTicks = true;
		this.status = Status.IDLE;
		this.shouldSwitchTask = true;
		this.doTasks = true;
	}

	public boolean inWorldTick() {
		return inWorldTick;
	}
	
	public boolean doWorldTicks() {
		return doWorldTicks;
	}
	
	protected void setStatus(Status newStatus) {
		status = newStatus;
	}
	
	public void setCurrentWorld(World world) {
		currentWorld = world;
		if (currentWorld != null) {
			profiler = currentWorld.getProfiler();
		}
	}
	
	protected void setCurrentTask(Task task) {
		currentTask = task;
	}
	
	public boolean tickInProgress() {
		return status != Status.IDLE;
	}
	
	public enum Status {
		IDLE(0),
		START_TICK(1),
		TICKING_WORLDS(2),
		END_TICK(3);
		
		public static final Status[] STATUSES;
		
		static {
			STATUSES = new Status[values().length];
			
			for (Status status : values()) {
				STATUSES[status.index] = status;
			}
		}
		
		private final int index;
		
		Status(int index) {
			this.index = index;
		}
		
		public static Status fromIndex(int index) {
			if (index > 0 && index < STATUSES.length) {
				return STATUSES[index];
			} else {
				return IDLE;
			}
		}
		
		public int getIndex() {
			return index;
		}
		
		public Status next() {
			int nextIndex = index + 1;
			if (nextIndex >= STATUSES.length) {
				nextIndex = 0;
			}
			return fromIndex(nextIndex);
		}
	}
	
	public enum Task {
		NONE(0, "", false),
		TICK_WORLD_BORDER(1, "tick world border", true),
		PROCESS_WEATHER(2, "process weather", true),
		TICK_TIME(3, "tick time", true),
		TICK_CHUNK_SOURCE(4, "tick chunk source", true),
		TICK_BLOCKS(5, "tick blocks", true),
		TICK_FLUIDS(6, "tick fluids", true),
		TICK_RAIDS(7, "tick raids", true),
		PROCESS_BLOCK_EVENTS(8, "process block events", true),
		TICK_ENTITIES(9, "tick entities", true),
		TICK_BLOCK_ENTITIES(10, "tick block entities", true),
		SWITCH_WORLD(11, "switch world", false);
		
		public static final Task[] TASKS;
		
		static {
			TASKS = new Task[values().length];
			
			for (Task task : values()) {
				TASKS[task.index] = task;
			}
		}
		
		private final int index;
		private final String name;
		private final boolean display;
		
		Task(int index, String name, boolean display) {
			this.index = index;
			this.name = name;
			this.display = display;
		}
		
		public static Task fromIndex(int index) {
			if (index > 0 && index < TASKS.length) {
				return TASKS[index];
			} else {
				return NONE;
			}
		}
		public int getIndex() {
			return index;
		}
		
		public String getName() {
			return name;
		}
		
		public boolean display() {
			return display;
		}
		
		public Task next() {
			int nextIndex = index + 1;
			if (nextIndex >= TASKS.length) {
				nextIndex = 1;
			}
			return fromIndex(nextIndex);
		}
	}
}
