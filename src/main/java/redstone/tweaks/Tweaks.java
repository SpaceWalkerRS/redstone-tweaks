package redstone.tweaks;

import com.g4mesoft.core.GSController;

import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.g4mespeed.RedstoneTweaksModule;

public class Tweaks {

	public static class Observer {

		public static int delayRisingEdge() {
			return module().observerDelayRisingEdge.getValue();
		}

		public static int delayFallingEdge() {
			return module().observerDelayFallingEdge.getValue();
		}

		public static boolean disable() {
			return module().observerDisable.getValue();
		}

		public static boolean microTickMode() {
			return module().observerMicroTickMode.getValue();
		}

		public static boolean observeBlockUpdates() {
			return module().observerObserveBlockUpdates.getValue();
		}

		public static int signal() {
			return module().observerSignal.getValue();
		}

		public static int signalDirect() {
			return module().observerSignalDirect.getValue();
		}

		public static TickPriority tickPriorityRisingEdge() {
			return tickPriority(module().observerTickPriorityRisingEdge.getValue());
		}

		public static TickPriority tickPriorityFallingEdge() {
			return tickPriority(module().observerTickPriorityFallingEdge.getValue());
		}
	}


	// helper methods

	private static RedstoneTweaksModule module() {
		GSController controller = GSController.getInstanceOnThread();

		if (controller == null) {
			throw new IllegalStateException("no g4mespeed controller on this thread!");
		}

		return controller.getModule(RedstoneTweaksModule.class);
	}

	private static TickPriority tickPriority(int value) {
		return TickPriority.byValue(value);
	}
}
