package redstone.tweaks;

import com.g4mesoft.core.GSController;

import redstone.tweaks.g4mespeed.RedstoneTweaksModule;

public class Tweaks {

	private static RedstoneTweaksModule module() {
		GSController controller = GSController.getInstanceOnThread();

		if (controller == null) {
			throw new IllegalStateException("no g4mespeed controller on this thread!");
		}

		return controller.getModule(RedstoneTweaksModule.class);
	}

	public static class Observer {

		public static boolean disable() {
			return module().observerDisable.getValue();
		}
	}
}
