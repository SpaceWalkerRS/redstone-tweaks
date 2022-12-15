package redstone.tweaks;

import java.util.Map;

import com.g4mesoft.core.GSController;

import net.minecraft.core.Direction;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.g4mespeed.RedstoneTweaksModule;

public class Tweaks {

	public static class Global {

		public static int signalMax() {
			return module().globalSignalMax.getValue();
		}
	}

	public static class ActivatorRail {

		public static int delayRisingEdge() {
			return module().activatorRailDelayRisingEdge.getValue();
		}

		public static int delayFallingEdge() {
			return module().activatorRailDelayFallingEdge.getValue();
		}

		public static boolean lazyRisingEdge() {
			return module().activatorRailLazyRisingEdge.getValue();
		}

		public static boolean lazyFallingEdge() {
			return module().activatorRailLazyFallingEdge.getValue();
		}

		public static int powerLimit() {
			return module().activatorRailPowerLimit.getValue();
		}

		public static Map<Direction, Boolean> quasiConnectivity() {
			return module().activatorRailQuasiConnectivity.getValue();
		}

		public static boolean quasiConnectivity(Direction dir) {
			return module().activatorRailQuasiConnectivity.getValue(dir);
		}

		public static boolean randomizeQuasiConnectivity() {
			return module().activatorRailRandomizeQuasiConnectivity.getValue();
		}

		public static TickPriority tickPriorityRisingEdge() {
			return module().activatorRailTickPriorityRisingEdge.getValue();
		}

		public static TickPriority tickPriorityFallingEdge() {
			return module().activatorRailTickPriorityFallingEdge.getValue();
		}
	}

	public static class Anvil {

		public static boolean crushConcrete() {
			return module().anvilCrushConcrete.getValue();
		}

		public static boolean crushWool() {
			return module().anvilCrushWool.getValue();
		}
	}

	public static class Bamboo {

		public static int delay() {
			return module().bambooDelay.getValue();
		}

		public static TickPriority tickPriority() {
			return module().bambooTickPriority.getValue();
		}
	}

	public static class Barrier {

		public static boolean movable() {
			return module().barrierMovable.getValue();
		}
	}

	public static class BubbleColumn {

		public static int delay() {
			return module().bubbleColumnDelay.getValue();
		}

		public static TickPriority tickPriority() {
			return module().bubbleColumnTickPriority.getValue();
		}
	}

	public static class Cactus {

		public static int delay() {
			return module().cactusDelay.getValue();
		}

		public static boolean nou() {
			return module().cactusNou.getValue();
		}

		public static TickPriority tickPriority() {
			return module().cactusTickPriority.getValue();
		}
	}

	public static class ChorusPlant {

		public static int delay() {
			return module().chorusPlantDelay.getValue();
		}

		public static TickPriority tickPriority() {
			return module().chorusPlantTickPriority.getValue();
		}
	}

	public static class CommandBlock {

		public static int delay() {
			return module().commandBlockDelay.getValue();
		}

		public static Map<Direction, Boolean> quasiConnectivity() {
			return module().commandBlockQuasiConnectivity.getValue();
		}

		public static boolean quasiConnectivity(Direction dir) {
			return module().commandBlockQuasiConnectivity.getValue(dir);
		}

		public static boolean randomizeQuasiConnectivity() {
			return module().commandBlockRandomizeQuasiConnectivity.getValue();
		}

		public static TickPriority tickPriority() {
			return module().commandBlockTickPriority.getValue();
		}
	}

	public static class Comparator {

		public static boolean additionMode() {
			return module().comparatorAdditionMode.getValue();
		}

		public static int delay() {
			return module().comparatorDelay.getValue();
		}

		public static boolean microTickMode() {
			return module().comparatorMicroTickMode.getValue();
		}

		public static boolean redstoneBlockAlternateInput() {
			return module().comparatorRedstoneBlockAlternateInput.getValue();
		}

		public static TickPriority tickPriority() {
			return module().comparatorTickPriority.getValue();
		}

		public static TickPriority tickPriorityPrioritized() {
			return module().comparatorTickPriorityPrioritized.getValue();
		}
	}

	public static class Composter {

		public static int delay() {
			return module().composterDelay.getValue();
		}

		public static TickPriority tickPriority() {
			return module().composterTickPriority.getValue();
		}
	}

	public static class Coral {

		public static int delayMin() {
			return module().coralDelayMin.getValue();
		}

		public static int delayMax() {
			return module().coralDelayMax.getValue();
		}

		public static TickPriority tickPriority() {
			return module().coralTickPriority.getValue();
		}
	}

	public static class DaylightDetector {

		public static boolean emitDirectSignal() {
			return module().daylightDetectorEmitDirectSignal.getValue();
		}
	}

	public static class DetectorRail {

		public static int delay() {
			return module().detectorRailDelay.getValue();
		}

		public static int signal() {
			return module().detectorRailSignal.getValue();
		}

		public static int signalDirect() {
			return module().detectorRailSignalDirect.getValue();
		}

		public static TickPriority tickPriority() {
			return module().detectorRailTickPriority.getValue();
		}
	}

	public static class DirtPath {

		public static int delay() {
			return module().dirtPathDelay.getValue();
		}

		public static TickPriority tickPriority() {
			return module().dirtPathTickPriority.getValue();
		}
	}

	public static class Dispenser {

		public static int delay() {
			return module().dispenserDelay.getValue();
		}

		public static boolean lazy() {
			return module().dispenserLazy.getValue();
		}

		public static Map<Direction, Boolean> quasiConnectivity() {
			return module().dispenserQuasiConnectivity.getValue();
		}

		public static boolean quasiConnectivity(Direction dir) {
			return module().dispenserQuasiConnectivity.getValue(dir);
		}

		public static boolean randomizeQuasiConnectivity() {
			return module().dispenserRandomizeQuasiConnectivity.getValue();
		}

		public static TickPriority tickPriority() {
			return module().dispenserTickPriority.getValue();
		}
	}

	public static class DragonEgg {

		public static int delay() {
			return module().dragonEggDelay.getValue();
		}
	}

	public static class Dropper {

		public static int delay() {
			return module().dropperDelay.getValue();
		}

		public static boolean lazy() {
			return module().dropperLazy.getValue();
		}

		public static Map<Direction, Boolean> quasiConnectivity() {
			return module().dropperQuasiConnectivity.getValue();
		}

		public static boolean quasiConnectivity(Direction dir) {
			return module().dropperQuasiConnectivity.getValue(dir);
		}

		public static boolean randomizeQuasiConnectivity() {
			return module().dropperRandomizeQuasiConnectivity.getValue();
		}

		public static TickPriority tickPriority() {
			return module().dropperTickPriority.getValue();
		}
	}

	public static class FallingBlock {

		public static int delay() {
			return module().fallingBlockDelay.getValue();
		}

		public static boolean suspendedByStickyBlocks() {
			return module().fallingBlockSuspendedByStickyBlocks.getValue();
		}

		public static TickPriority tickPriority() {
			return module().fallingBlockTickPriority.getValue();
		}
	}

	public static class Farmland {

		public static int delay() {
			return module().farmlandDelay.getValue();
		}

		public static TickPriority tickPriority() {
			return module().farmlandTickPriority.getValue();
		}
	}

	public static class Fire {

		public static int delayMin() {
			return module().fireDelayMin.getValue();
		}

		public static int delayMax() {
			return module().fireDelayMax.getValue();
		}

		public static TickPriority tickPriority() {
			return module().fireTickPriority.getValue();
		}
	}

	public static class FrostedIce {

		public static int delayMin() {
			return module().frostedIceDelayMin.getValue();
		}

		public static int delayMax() {
			return module().frostedIceDelayMax.getValue();
		}

		public static TickPriority tickPriority() {
			return module().frostedIceTickPriority.getValue();
		}
	}

	public static class Hay {

		public static boolean blockMisalignedPistonMove() {
			return module().hayBlockMisalignedPistonMove.getValue();
		}
	}

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
			return module().observerTickPriorityRisingEdge.getValue();
		}

		public static TickPriority tickPriorityFallingEdge() {
			return module().observerTickPriorityFallingEdge.getValue();
		}
	}

	public static class PoweredRail {

		public static int delayRisingEdge() {
			return module().poweredRailDelayRisingEdge.getValue();
		}

		public static int delayFallingEdge() {
			return module().poweredRailDelayFallingEdge.getValue();
		}

		public static boolean lazyRisingEdge() {
			return module().poweredRailLazyRisingEdge.getValue();
		}

		public static boolean lazyFallingEdge() {
			return module().poweredRailLazyFallingEdge.getValue();
		}

		public static int powerLimit() {
			return module().poweredRailPowerLimit.getValue();
		}

		public static Map<Direction, Boolean> quasiConnectivity() {
			return module().poweredRailQuasiConnectivity.getValue();
		}

		public static boolean quasiConnectivity(Direction dir) {
			return module().poweredRailQuasiConnectivity.getValue(dir);
		}

		public static boolean randomizeQuasiConnectivity() {
			return module().poweredRailRandomizeQuasiConnectivity.getValue();
		}

		public static TickPriority tickPriorityRisingEdge() {
			return module().poweredRailTickPriorityRisingEdge.getValue();
		}

		public static TickPriority tickPriorityFallingEdge() {
			return module().poweredRailTickPriorityFallingEdge.getValue();
		}
	}

	public static class Repeater {

		public static int delayRisingEdge() {
			return module().repeaterDelayRisingEdge.getValue();
		}

		public static int delayFallingEdge() {
			return module().repeaterDelayFallingEdge.getValue();
		}

		public static boolean lazyRisingEdge() {
			return module().repeaterLazyRisingEdge.getValue();
		}

		public static boolean lazyFallingEdge() {
			return module().repeaterLazyFallingEdge.getValue();
		}

		public static boolean microTickMode() {
			return module().repeaterMicroTickMode.getValue();
		}

		public static int signal() {
			return module().repeaterSignal.getValue();
		}

		public static int signalDirect() {
			return module().repeaterSignalDirect.getValue();
		}

		public static TickPriority tickPriorityRisingEdge() {
			return module().repeaterTickPriorityRisingEdge.getValue();
		}

		public static TickPriority tickPriorityFallingEdge() {
			return module().repeaterTickPriorityFallingEdge.getValue();
		}

		public static TickPriority tickPriorityPrioritized() {
			return module().repeaterTickPriorityPrioritized.getValue();
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
}
