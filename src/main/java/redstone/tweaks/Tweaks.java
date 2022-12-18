package redstone.tweaks;

import java.util.Map;

import com.g4mesoft.core.GSController;

import net.minecraft.core.Direction;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.g4mespeed.RedstoneTweaksModule;

public class Tweaks {

	public static class Global {

		public static boolean movableBlockEntities() {
			return module().globalMovableBlockEntities.getValue();
		}

		public static boolean movableMovingBlocks() {
			return module().globalMovableMovingBlocks.getValue();
		}

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

		public static boolean microtickMode() {
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

	public static class HeavyWeightedPressurePlate {

		public static int delayRisingEdge() {
			return module().heavyWeightedPressurePlateDelayRisingEdge.getValue();
		}

		public static int delayFallingEdge() {
			return module().heavyWeightedPressurePlateDelayFallingEdge.getValue();
		}

		public static TickPriority tickPriorityRisingEdge() {
			return module().heavyWeightedPressurePlateTickPriorityRisingEdge.getValue();
		}

		public static TickPriority tickPriorityFallingEdge() {
			return module().heavyWeightedPressurePlateTickPriorityFallingEdge.getValue();
		}

		public static int weight() {
			return module().heavyWeightedPressurePlateWeight.getValue();
		}
	}

	public static class Hopper {

		public static int cooldown() {
			return module().hopperCooldown.getValue();
		}

		public static int cooldownPrioritized() {
			return module().hopperCooldownPrioritized.getValue();
		}

		public static int delayRisingEdge() {
			return module().hopperDelayRisingEdge.getValue();
		}

		public static int delayFallingEdge() {
			return module().hopperDelayFallingEdge.getValue();
		}

		public static boolean lazyRisingEdge() {
			return module().hopperLazyRisingEdge.getValue();
		}

		public static boolean lazyFallingEdge() {
			return module().hopperLazyFallingEdge.getValue();
		}
	
		public static Map<Direction, Boolean> quasiConnectivity() {
			return module().hopperQuasiConnectivity.getValue();
		}

		public static boolean quasiConnectivity(Direction dir) {
			return module().hopperQuasiConnectivity.getValue(dir);
		}

		public static boolean randomizeQuasiConnectivity() {
			return module().hopperRandomizeQuasiConnectivity.getValue();
		}

		public static TickPriority tickPriorityRisingEdge() {
			return module().hopperTickPriorityRisingEdge.getValue();
		}

		public static TickPriority tickPriorityFallingEdge() {
			return module().hopperTickPriorityFallingEdge.getValue();
		}
	}

	public static class Lava {

		public static int delay() {
			return module().lavaDelay.getValue();
		}

		public static int delayNether() {
			return module().lavaDelayNether.getValue();
		}

		public static TickPriority tickPriority() {
			return module().lavaTickPriority.getValue();
		}
	}

	public static class Leaves {

		public static int delay() {
			return module().leavesDelay.getValue();
		}

		public static TickPriority tickPriority() {
			return module().leavesTickPriority.getValue();
		}
	}

	public static class Lectern {

		public static int delayRisingEdge() {
			return module().lecternDelayRisingEdge.getValue();
		}

		public static int delayFallingEdge() {
			return module().lecternDelayFallingEdge.getValue();
		}

		public static int signal() {
			return module().lecternSignal.getValue();
		}

		public static int signalDirect() {
			return module().lecternSignalDirect.getValue();
		}

		public static TickPriority tickPriorityRisingEdge() {
			return module().lecternTickPriorityRisingEdge.getValue();
		}

		public static TickPriority tickPriorityFallingEdge() {
			return module().lecternTickPriorityFallingEdge.getValue();
		}
	}

	public static class Lever {

		public static int delayRisingEdge() {
			return module().leverDelayRisingEdge.getValue();
		}

		public static int delayFallingEdge() {
			return module().leverDelayFallingEdge.getValue();
		}

		public static int signal() {
			return module().leverSignal.getValue();
		}

		public static int signalDirect() {
			return module().leverSignalDirect.getValue();
		}

		public static TickPriority tickPriorityRisingEdge() {
			return module().leverTickPriorityRisingEdge.getValue();
		}

		public static TickPriority tickPriorityFallingEdge() {
			return module().leverTickPriorityFallingEdge.getValue();
		}
	}

	public static class LightWeightedPressurePlate {

		public static int delayRisingEdge() {
			return module().lightWeightedPressurePlateDelayRisingEdge.getValue();
		}

		public static int delayFallingEdge() {
			return module().lightWeightedPressurePlateDelayFallingEdge.getValue();
		}

		public static TickPriority tickPriorityRisingEdge() {
			return module().lightWeightedPressurePlateTickPriorityRisingEdge.getValue();
		}

		public static TickPriority tickPriorityFallingEdge() {
			return module().lightWeightedPressurePlateTickPriorityFallingEdge.getValue();
		}

		public static int weight() {
			return module().lightWeightedPressurePlateWeight.getValue();
		}
	}

	public static class MagentaGlazedTerracotta {

		public static boolean signalDiode() {
			return module().magentaGlazedTerracottaSignalDiode.getValue();
		}
	}

	public static class Magma {

		public static int delay() {
			return module().magmaDelay.getValue();
		}

		public static TickPriority tickPriority() {
			return module().magmaTickPriority.getValue();
		}
	}

	public static class NormalPiston {

		public static int delayRisingEdge() {
			return module().normalPistonDelayRisingEdge.getValue();
		}

		public static int delayFallingEdge() {
			return module().normalPistonDelayFallingEdge.getValue();
		}

		public static boolean headUpdatesNeighborsOnExtension() {
			return module().normalPistonHeadUpdatesNeighborsOnExtension.getValue();
		}

		public static boolean headUpdatesNeighborsOnRetraction() {
			return true;
		}

		public static boolean ignorePowerFromFront() {
			return module().normalPistonIgnorePowerFromFront.getValue();
		}

		public static boolean lazyRisingEdge() {
			return module().normalPistonLazyRisingEdge.getValue();
		}

		public static boolean lazyFallingEdge() {
			return module().normalPistonLazyFallingEdge.getValue();
		}

		public static boolean looseHead() {
			return module().normalPistonLooseHead.getValue();
		}

		public static boolean movableWhenExtended() {
			return module().normalPistonMovableWhenExtended.getValue();
		}

		public static int pushLimit() {
			return module().normalPistonPushLimit.getValue();
		}

		public static int pullLimit() {
			return -1;
		}

		public static Map<Direction, Boolean> quasiConnectivity() {
			return module().normalPistonQuasiConnectivity.getValue();
		}

		public static boolean quasiConnectivity(Direction dir) {
			return module().normalPistonQuasiConnectivity.getValue(dir);
		}

		public static boolean randomizeQuasiConnectivity() {
			return module().normalPistonRandomizeQuasiConnectivity.getValue();
		}

		public static int speedRisingEdge() {
			return module().normalPistonSpeedRisingEdge.getValue();
		}

		public static int speedFallingEdge() {
			return module().normalPistonSpeedFallingEdge.getValue();
		}

		public static TickPriority tickPriorityRisingEdge() {
			return module().normalPistonTickPriorityRisingEdge.getValue();
		}

		public static TickPriority tickPriorityFallingEdge() {
			return module().normalPistonTickPriorityFallingEdge.getValue();
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

	public static class Piston {

		public static boolean doBlockDropping() {
			return StickyPiston.blockDropping();
		}

		public static boolean doFastBlockDropping() {
			return doBlockDropping() && StickyPiston.fastBlockDropping();
		}

		public static boolean doSuperBlockDropping() {
			return doBlockDropping() && StickyPiston.superBlockDropping();
		}

		public static int delayRisingEdge(boolean sticky) {
			return sticky ? StickyPiston.delayRisingEdge() : NormalPiston.delayRisingEdge();
		}

		public static int delayFallingEdge(boolean sticky) {
			return sticky ? StickyPiston.delayFallingEdge() : NormalPiston.delayFallingEdge();
		}

		public static int delay(boolean extend, boolean sticky) {
			return extend ? delayRisingEdge(sticky) : delayFallingEdge(sticky);
		}

		public static boolean doDoubleRetraction() {
			return StickyPiston.doubleRetraction() && !StickyPiston.movableWhenExtended() && !StickyPiston.looseHead();
		}

		public static boolean headUpdatesNeighborsOnExtension(boolean sticky) {
			return sticky ? StickyPiston.headUpdatesNeighborsOnExtension() : NormalPiston.headUpdatesNeighborsOnExtension();
		}

		public static boolean headUpdatesNeighborsOnRetraction(boolean sticky) {
			return sticky ? StickyPiston.headUpdatesNeighborsOnRetraction() : NormalPiston.headUpdatesNeighborsOnRetraction();
		}

		public static boolean ignorePowerFromFront(boolean sticky) {
			return sticky ? StickyPiston.ignorePowerFromFront() : NormalPiston.ignorePowerFromFront();
		}

		public static boolean lazyRisingEdge(boolean sticky) {
			return sticky ? StickyPiston.lazyRisingEdge() : NormalPiston.lazyRisingEdge();
		}

		public static boolean lazyFallingEdge(boolean sticky) {
			return sticky ? StickyPiston.lazyFallingEdge() : NormalPiston.lazyFallingEdge();
		}

		public static boolean lazy(boolean extend, boolean sticky) {
			return extend ? lazyRisingEdge(sticky) : lazyFallingEdge(sticky);
		}

		public static boolean looseHead(boolean sticky) {
			return sticky ? StickyPiston.looseHead() : NormalPiston.looseHead();
		}

		public static boolean movableWhenExtended(boolean sticky) {
			return sticky ? StickyPiston.movableWhenExtended() : NormalPiston.movableWhenExtended();
		}

		public static int pushLimit(boolean sticky) {
			return sticky ? StickyPiston.pushLimit() : NormalPiston.pushLimit();
		}

		public static int pullLimit(boolean sticky) {
			return sticky ? StickyPiston.pullLimit() : NormalPiston.pullLimit();
		}

		public static int moveLimit(boolean extend, boolean sticky) {
			return extend ? pushLimit(sticky) : pullLimit(sticky);
		}

		public static Map<Direction, Boolean> quasiConnectivity(boolean sticky) {
			return sticky ? StickyPiston.quasiConnectivity() : NormalPiston.quasiConnectivity();
		}

		public static boolean quasiConnectivity(boolean sticky, Direction dir) {
			return sticky ? StickyPiston.quasiConnectivity(dir) : NormalPiston.quasiConnectivity(dir);
		}

		public static boolean randomizeQuasiConnectivity(boolean sticky) {
			return sticky ? StickyPiston.randomizeQuasiConnectivity() : NormalPiston.randomizeQuasiConnectivity();
		}

		public static int speedRisingEdge(boolean sticky) {
			return sticky ? StickyPiston.speedRisingEdge() : NormalPiston.speedRisingEdge();
		}

		public static int speedFallingEdge(boolean sticky) {
			return sticky ? StickyPiston.speedFallingEdge() : NormalPiston.speedFallingEdge();
		}

		public static int speed(boolean extend, boolean sticky) {
			return extend ? speedRisingEdge(sticky) : speedFallingEdge(sticky);
		}

		public static TickPriority tickPriorityRisingEdge(boolean sticky) {
			return sticky ? StickyPiston.tickPriorityRisingEdge() : NormalPiston.tickPriorityRisingEdge();
		}

		public static TickPriority tickPriorityFallingEdge(boolean sticky) {
			return sticky ? StickyPiston.tickPriorityFallingEdge() : NormalPiston.tickPriorityFallingEdge();
		}

		public static TickPriority tickPriority(boolean extend, boolean sticky) {
			return extend ? tickPriorityRisingEdge(sticky) : tickPriorityFallingEdge(sticky);
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

		public static boolean microtickMode() {
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

	public static class StickyPiston {

		/**
		 * use {@link redstone.tweaks.Tweaks.Piston#doBlockDropping Piston.doBlockDropping}!
		 */
		public static boolean blockDropping() {
			return module().stickyPistonBlockDropping.getValue();
		}

		/**
		 * use {@link redstone.tweaks.Tweaks.Piston#doFastBlockDropping Piston.doFastBlockDropping}!
		 */
		public static boolean fastBlockDropping() {
			return module().stickyPistonFastBlockDropping.getValue();
		}

		/**
		 * use {@link redstone.tweaks.Tweaks.Piston#doSuperBlockDropping Piston.doSuperBlockDropping}!
		 */
		public static boolean superBlockDropping() {
			return module().stickyPistonSuperBlockDropping.getValue();
		}

		public static int delayRisingEdge() {
			return module().stickyPistonDelayRisingEdge.getValue();
		}

		public static int delayFallingEdge() {
			return module().stickyPistonDelayFallingEdge.getValue();
		}

		/**
		 * use {@link redstone.tweaks.Tweaks.Piston#doDoubleRetraction Piston.doDoubleRetraction}!
		 */
		public static boolean doubleRetraction() {
			return module().stickyPistonDoubleRetraction.getValue();
		}

		public static boolean headUpdatesNeighborsOnExtension() {
			return module().stickyPistonHeadUpdatesNeighborsOnExtension.getValue();
		}

		public static boolean headUpdatesNeighborsOnRetraction() {
			return module().stickyPistonHeadUpdatesNeighborsOnRetraction.getValue();
		}

		public static boolean ignorePowerFromFront() {
			return module().stickyPistonIgnorePowerFromFront.getValue();
		}

		public static boolean lazyRisingEdge() {
			return module().stickyPistonLazyRisingEdge.getValue();
		}

		public static boolean lazyFallingEdge() {
			return module().stickyPistonLazyFallingEdge.getValue();
		}

		public static boolean looseHead() {
			return module().stickyPistonLooseHead.getValue();
		}

		public static boolean movableWhenExtended() {
			return module().stickyPistonMovableWhenExtended.getValue();
		}

		public static int pushLimit() {
			return module().stickyPistonPushLimit.getValue();
		}

		public static int pullLimit() {
			return module().stickyPistonPullLimit.getValue();
		}

		public static Map<Direction, Boolean> quasiConnectivity() {
			return module().stickyPistonQuasiConnectivity.getValue();
		}

		public static boolean quasiConnectivity(Direction dir) {
			return module().stickyPistonQuasiConnectivity.getValue(dir);
		}

		public static boolean randomizeQuasiConnectivity() {
			return module().stickyPistonRandomizeQuasiConnectivity.getValue();
		}

		public static int speedRisingEdge() {
			return module().stickyPistonSpeedRisingEdge.getValue();
		}

		public static int speedFallingEdge() {
			return module().stickyPistonSpeedFallingEdge.getValue();
		}

		public static boolean superSticky() {
			return module().stickyPistonSuperSticky.getValue();
		}

		public static TickPriority tickPriorityRisingEdge() {
			return module().stickyPistonTickPriorityRisingEdge.getValue();
		}

		public static TickPriority tickPriorityFallingEdge() {
			return module().stickyPistonTickPriorityFallingEdge.getValue();
		}
	}

	public static class StonePressurePlate {

		public static int delayRisingEdge() {
			return module().stonePressurePlateDelayRisingEdge.getValue();
		}

		public static int delayFallingEdge() {
			return module().stonePressurePlateDelayFallingEdge.getValue();
		}
	
		public static int signal() {
			return module().stonePressurePlateSignal.getValue();
		}

		public static int signalDirect() {
			return module().stonePressurePlateSignalDirect.getValue();
		}

		public static TickPriority tickPriorityRisingEdge() {
			return module().stonePressurePlateTickPriorityRisingEdge.getValue();
		}

		public static TickPriority tickPriorityFallingEdge() {
			return module().stonePressurePlateTickPriorityFallingEdge.getValue();
		}
	}

	public static class Water {

		public static int delay() {
			return module().waterDelay.getValue();
		}

		public static TickPriority tickPriority() {
			return module().waterTickPriority.getValue();
		}
	}

	public static class WoodenPressurePlate {

		public static int delayRisingEdge() {
			return module().woodenPressurePlateDelayRisingEdge.getValue();
		}

		public static int delayFallingEdge() {
			return module().woodenPressurePlateDelayFallingEdge.getValue();
		}
	
		public static int signal() {
			return module().woodenPressurePlateSignal.getValue();
		}

		public static int signalDirect() {
			return module().woodenPressurePlateSignalDirect.getValue();
		}

		public static TickPriority tickPriorityRisingEdge() {
			return module().woodenPressurePlateTickPriorityRisingEdge.getValue();
		}

		public static TickPriority tickPriorityFallingEdge() {
			return module().woodenPressurePlateTickPriorityFallingEdge.getValue();
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
