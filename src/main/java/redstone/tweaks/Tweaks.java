package redstone.tweaks;

import com.g4mesoft.core.GSController;

import net.minecraft.world.ticks.TickPriority;
import redstone.tweaks.g4mespeed.RedstoneTweaksModule;
import redstone.tweaks.world.level.block.CapacitorBehavior;
import redstone.tweaks.world.level.block.QuasiConnectivity;

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

		public static QuasiConnectivity quasiConnectivity() {
			return module().activatorRailQuasiConnectivity.getValue();
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

	public static class BigDripleaf {

		public static int delay() {
			return module().bigDripleafDelay.getValue();
		}

		public static int delayUnstable() {
			return module().bigDripleafDelayUnstable.getValue();
		}

		public static int delayPartial() {
			return module().bigDripleafDelayPartial.getValue();
		}

		public static int delayFull() {
			return module().bigDripleafDelayFull.getValue();
		}

		public static QuasiConnectivity quasiConnectivity() {
			return module().bigDripleafQuasiConnectivity.getValue();
		}

		public static boolean randomizeQuasiConnectivity() {
			return module().bigDripleafRandomizeQuasiConnectivity.getValue();
		}

		public static TickPriority tickPriority() {
			return module().bigDripleafTickPriority.getValue();
		}

		public static TickPriority tickPriorityUnstable() {
			return module().bigDripleafTickPriorityUnstable.getValue();
		}

		public static TickPriority tickPriorityPartial() {
			return module().bigDripleafTickPriorityPartial.getValue();
		}

		public static TickPriority tickPriorityFull() {
			return module().bigDripleafTickPriorityFull.getValue();
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

	public static class Button {

		public static int delayRisingEdge(boolean wooden) {
			return wooden ? WoodenButton.delayRisingEdge() : StoneButton.delayRisingEdge();
		}

		public static int delayFallingEdge(boolean wooden) {
			return wooden ? WoodenButton.delayFallingEdge() : StoneButton.delayFallingEdge();
		}
	
		public static int signal(boolean wooden) {
			return wooden ? WoodenButton.signal() : StoneButton.signal();
		}

		public static int signalDirect(boolean wooden) {
			return wooden ? WoodenButton.signalDirect() : StoneButton.signalDirect();
		}

		public static TickPriority tickPriorityRisingEdge(boolean wooden) {
			return wooden ? WoodenButton.tickPriorityRisingEdge() : StoneButton.tickPriorityRisingEdge();
		}

		public static TickPriority tickPriorityFallingEdge(boolean wooden) {
			return wooden ? WoodenButton.tickPriorityFallingEdge() : StoneButton.tickPriorityFallingEdge();
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

	public static class Cauldron {

		public static int delay() {
			return module().cauldronDelay.getValue();
		}

		public static TickPriority tickPriority() {
			return module().cauldronTickPriority.getValue();
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

		public static QuasiConnectivity quasiConnectivity() {
			return module().commandBlockQuasiConnectivity.getValue();
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
			return module().comparatorMicrotickMode.getValue();
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

		public static QuasiConnectivity quasiConnectivity() {
			return module().dispenserQuasiConnectivity.getValue();
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

		public static QuasiConnectivity quasiConnectivity() {
			return module().dropperQuasiConnectivity.getValue();
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

	public static class Frogspawn {

		public static int delayHatchMin() {
			return module().frogspawnDelayHatchMin.getValue();
		}

		public static int delayHatchMax() {
			return module().frogspawnDelayHatchMax.getValue();
		}

		public static TickPriority tickPriority() {
			return module().frogspawnTickPriority.getValue();
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
	
		public static QuasiConnectivity quasiConnectivity() {
			return module().hopperQuasiConnectivity.getValue();
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

	public static class LightningRod {

		public static int delayRisingEdge() {
			return module().lightningRodDelayRisingEdge.getValue();
		}

		public static int delayFallingEdge() {
			return module().lightningRodDelayFallingEdge.getValue();
		}

		public static int signal() {
			return module().lightningRodSignal.getValue();
		}

		public static int signalDirect() {
			return module().lightningRodSignalDirect.getValue();
		}

		public static TickPriority tickPriorityRisingEdge() {
			return module().lightningRodTickPriorityRisingEdge.getValue();
		}

		public static TickPriority tickPriorityFallingEdge() {
			return module().lightningRodTickPriorityFallingEdge.getValue();
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

		public static boolean canMoveSelf() {
			return module().normalPistonCanMoveSelf.getValue();
		}

		public static boolean connectToWire() {
			return module().normalPistonConnectToWire.getValue();
		}

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

		public static boolean ignoreUpdatesWhileExtending() {
			return module().normalPistonIgnoreUpdatesWhileExtending.getValue();
		}

		public static boolean ignoreUpdatesWhileRetracting() {
			return module().normalPistonIgnoreUpdatesWhileRetracting.getValue();
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

		public static QuasiConnectivity quasiConnectivity() {
			return module().normalPistonQuasiConnectivity.getValue();
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

		public static boolean updateSelf() {
			return module().normalPistonUpdateSelf.getValue();
		}
	}

	public static class NoteBlock {

		public static int delay() {
			return module().noteBlockDelay.getValue();
		}

		public static boolean lazy() {
			return module().noteBlockLazy.getValue();
		}

		public static QuasiConnectivity quasiConnectivity() {
			return module().noteBlockQuasiConnectivity.getValue();
		}

		public static boolean randomizeQuasiConnectivity() {
			return module().noteBlockRandomizeQuasiConnectivity.getValue();
		}

		public static TickPriority tickPriority() {
			return module().noteBlockTickPriority.getValue();
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

		public static boolean microtickMode() {
			return module().observerMicrotickMode.getValue();
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

		public static boolean canMoveSelf(boolean sticky) {
			return sticky ? StickyPiston.canMoveSelf() : NormalPiston.canMoveSelf();
		}

		public static boolean connectToWire(boolean sticky) {
			return sticky ? StickyPiston.connectToWire() : NormalPiston.connectToWire();
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

		public static boolean ignoreUpdatesWhileExtending(boolean sticky) {
			return sticky ? StickyPiston.ignoreUpdatesWhileExtending() : NormalPiston.ignoreUpdatesWhileExtending();
		}

		public static boolean ignoreUpdatesWhileRetracting(boolean sticky) {
			return sticky ? StickyPiston.ignoreUpdatesWhileRetracting() : NormalPiston.ignoreUpdatesWhileRetracting();
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

		public static QuasiConnectivity quasiConnectivity(boolean sticky) {
			return sticky ? StickyPiston.quasiConnectivity() : NormalPiston.quasiConnectivity();
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

		public static boolean updateSelf(boolean sticky) {
			return sticky ? StickyPiston.updateSelf() : NormalPiston.updateSelf();
		}
	}

	public static class PointedDripstone {

		public static int delay() {
			return module().pointedDripstoneDelay.getValue();
		}

		public static int delayBelow() {
			return module().pointedDripstoneDelayBelow.getValue();
		}

		public static TickPriority tickPriority() {
			return module().pointedDripstoneTickPriority.getValue();
		}

		public static TickPriority tickPriorityBelow() {
			return module().pointedDripstoneTickPriorityBelow.getValue();
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

		public static QuasiConnectivity quasiConnectivity() {
			return module().poweredRailQuasiConnectivity.getValue();
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

	public static class Rail {

		public static int delay() {
			return module().railDelay.getValue();
		}

		public static QuasiConnectivity quasiConnectivity() {
			return module().railQuasiConnectivity.getValue();
		}

		public static boolean randomizeQuasiConnectivity() {
			return module().railRandomizeQuasiConnectivity.getValue();
		}

		public static TickPriority tickPriority() {
			return module().railTickPriority.getValue();
		}
	}

	public static class RedSand {

		public static boolean connectToWire() {
			return module().redSandConnectToWire.getValue();
		}

		public static int signal() {
			return module().redSandSignal.getValue();
		}

		public static int signalDirect() {
			return module().redSandSignalDirect.getValue();
		}
	}

	public static class RedstoneBlock {

		public static int signal() {
			return module().redstoneBlockSignal.getValue();
		}

		public static int signalDirect() {
			return module().redstoneBlockSignalDirect.getValue();
		}
	}

	public static class RedstoneLamp {

		public static int delayRisingEdge() {
			return module().redstoneLampDelayRisingEdge.getValue();
		}

		public static int delayFallingEdge() {
			return module().redstoneLampDelayFallingEdge.getValue();
		}

		public static int delay(boolean lit) {
			return lit ? delayFallingEdge() : delayRisingEdge();
		}

		public static boolean lazyRisingEdge() {
			return module().redstoneLampLazyRisingEdge.getValue();
		}

		public static boolean lazyFallingEdge() {
			return module().redstoneLampLazyFallingEdge.getValue();
		}

		public static QuasiConnectivity quasiConnectivity() {
			return module().redstoneLampQuasiConnectivity.getValue();
		}

		public static boolean randomizeQuasiConnectivity() {
			return module().redstoneLampRandomizeQuasiConnectivity.getValue();
		}

		public static TickPriority tickPriorityRisingEdge() {
			return module().redstoneLampTickPriorityRisingEdge.getValue();
		}

		public static TickPriority tickPriorityFallingEdge() {
			return module().redstoneLampTickPriorityFallingEdge.getValue();
		}

		public static TickPriority tickPriority(boolean lit) {
			return lit ? tickPriorityFallingEdge() : tickPriorityRisingEdge();
		}
	}

	public static class RedstoneOre {

		public static CapacitorBehavior capacitorBehavior() {
			return module().redstoneOreCapacitorBehavior.getValue();
		}

		public static boolean connectToWire() {
			return module().redstoneOreConnectToWire.getValue();
		}

		public static int delay() {
			return module().redstoneOreDelay.getValue();
		}

		public static int signal() {
			return module().redstoneOreSignal.getValue();
		}

		public static int signalDirect() {
			return module().redstoneOreSignalDirect.getValue();
		}

		public static TickPriority tickPriority() {
			return module().redstoneOreTickPriority.getValue();
		}
	}

	public static class RedstoneTorch {

		public static int burnoutCount() {
			return module().redstoneTorchBurnoutCount.getValue();
		}

		public static int burnoutTimer() {
			return module().redstoneTorchBurnoutTimer.getValue();
		}

		public static int delayBurnout() {
			return module().redstoneTorchDelayBurnout.getValue();
		}

		public static int delayRisingEdge() {
			return module().redstoneTorchDelayRisingEdge.getValue();
		}

		public static int delayFallingEdge() {
			return module().redstoneTorchDelayFallingEdge.getValue();
		}

		public static int delay(boolean lit) {
			return lit ? delayFallingEdge() : delayRisingEdge();
		}

		public static boolean lazyRisingEdge() {
			return module().redstoneTorchLazyRisingEdge.getValue();
		}

		public static boolean lazyFallingEdge() {
			return module().redstoneTorchLazyFallingEdge.getValue();
		}

		public static boolean lazy(boolean lit) {
			return lit ? lazyFallingEdge() : lazyRisingEdge();
		}

		public static boolean microtickMode() {
			return module().redstoneTorchMicrotickMode.getValue();
		}

		public static int signal() {
			return module().redstoneTorchSignal.getValue();
		}

		public static int signalDirect() {
			return module().redstoneTorchSignalDirect.getValue();
		}

		public static boolean softInversion() {
			return module().redstoneTorchSoftInversion.getValue();
		}

		public static TickPriority tickPriorityBurnout() {
			return module().redstoneTorchTickPriorityBurnout.getValue();
		}

		public static TickPriority tickPriorityRisingEdge() {
			return module().redstoneTorchTickPriorityRisingEdge.getValue();
		}

		public static TickPriority tickPriorityFallingEdge() {
			return module().redstoneTorchTickPriorityFallingEdge.getValue();
		}

		public static TickPriority tickPriority(boolean lit) {
			return lit ? tickPriorityFallingEdge() : tickPriorityRisingEdge();
		}
	}

	public static class RedstoneWire {

		public static int delay() {
			return module().redstoneWireDelay.getValue();
		}

		public static boolean invertFlowOnGlass() {
			return module().redstoneWireInvertFlowOnGlass.getValue();
		}

		public static boolean microtickMode() {
			return module().redstoneWireMicrotickMode.getValue();
		}

		public static boolean slabsAllowUpConnection() {
			return module().redstoneWireSlabsAllowUpConnection.getValue();
		}

		public static TickPriority tickPriority() {
			return module().redstoneWireTickPriority.getValue();
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
			return module().repeaterMicrotickMode.getValue();
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

	public static class Scaffolding {

		public static int delay() {
			return module().scaffoldingDelay.getValue();
		}

		public static TickPriority tickPriority() {
			return module().scaffoldingTickPriority.getValue();
		}
	}

	public static class SculkCatalyst {

		public static int delay() {
			return module().sculkCatalystDelay.getValue();
		}

		public static TickPriority tickPriority() {
			return module().sculkCatalystTickPriority.getValue();
		}
	}

	public static class SculkSensor {

		public static int delay() {
			return module().sculkSensorDelay.getValue();
		}

		public static TickPriority tickPriority() {
			return module().sculkSensorTickPriority.getValue();
		}
	}

	public static class Shulker {

		public static boolean conductRedstone() {
			return module().shulkerConductRedstone.getValue();
		}

		public static boolean updateNeighborsWhenPeeking() {
			return module().shulkerUpdateNeighborsWhenPeeking.getValue();
		}
	}

	public static class ShulkerBox {

		public static boolean updateNeighborsWhenPeeking() {
			return module().shulkerBoxUpdateNeighborsWhenPeeking.getValue();
		}
	}

	public static class SoulSand {

		public static int delay() {
			return module().soulSandDelay.getValue();
		}

		public static TickPriority tickPriority() {
			return module().soulSandTickPriority.getValue();
		}
	}

	public static class Stairs {

		public static boolean conductRedstone() {
			return module().stairsConductRedstone.getValue();
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

		public static boolean canMoveSelf() {
			return module().stickyPistonCanMoveSelf.getValue();
		}

		public static boolean connectToWire() {
			return module().stickyPistonConnectToWire.getValue();
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

		public static boolean ignoreUpdatesWhileExtending() {
			return module().stickyPistonIgnoreUpdatesWhileExtending.getValue();
		}

		public static boolean ignoreUpdatesWhileRetracting() {
			return module().stickyPistonIgnoreUpdatesWhileRetracting.getValue();
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

		public static QuasiConnectivity quasiConnectivity() {
			return module().stickyPistonQuasiConnectivity.getValue();
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

		public static boolean updateSelf() {
			return module().stickyPistonUpdateSelf.getValue();
		}
	}

	public static class StoneButton {

		public static int delayRisingEdge() {
			return module().stoneButtonDelayRisingEdge.getValue();
		}

		public static int delayFallingEdge() {
			return module().stoneButtonDelayFallingEdge.getValue();
		}
	
		public static int signal() {
			return module().stoneButtonSignal.getValue();
		}

		public static int signalDirect() {
			return module().stoneButtonSignalDirect.getValue();
		}

		public static TickPriority tickPriorityRisingEdge() {
			return module().stoneButtonTickPriorityRisingEdge.getValue();
		}

		public static TickPriority tickPriorityFallingEdge() {
			return module().stoneButtonTickPriorityFallingEdge.getValue();
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

	public static class SugarCane {

		public static int delay() {
			return module().sugarCaneDelay.getValue();
		}

		public static TickPriority tickPriority() {
			return module().sugarCaneTickPriority.getValue();
		}
	}

	public static class Target {

		public static int delay() {
			return module().targetDelay.getValue();
		}

		public static int delayArrow() {
			return module().targetDelayArrow.getValue();
		}

		public static boolean emitDirectSignal() {
			return module().targetEmitDirectSignal.getValue();
		}

		public static TickPriority tickPriority() {
			return module().targetTickPriority.getValue();
		}
	}

	public static class TNT {

		public static int delay() {
			return module().tntDelay.getValue();
		}

		public static int fuseTime() {
			return module().tntFuseTime.getValue();
		}

		public static boolean lazy() {
			return module().tntLazy.getValue();
		}

		public static QuasiConnectivity quasiConnectivity() {
			return module().tntQuasiConnectivity.getValue();
		}

		public static boolean randomizeQuasiConnectivity() {
			return module().tntRandomizeQuasiConnectivity.getValue();
		}

		public static TickPriority tickPriority() {
			return module().targetTickPriority.getValue();
		}
	}

	public static class TripWire {

		public static int delay() {
			return module().tripWireDelay.getValue();
		}

		public static TickPriority tickPriority() {
			return module().tripWireTickPriority.getValue();
		}
	}

	public static class TripWireHook {

		public static int delay() {
			return module().tripWireHookDelay.getValue();
		}

		public static int signal() {
			return module().tripWireHookSignal.getValue();
		}

		public static int signalDirect() {
			return module().tripWireHookSignalDirect.getValue();
		}

		public static TickPriority tickPriority() {
			return module().tripWireHookTickPriority.getValue();
		}
	}

	public static class Vines {

		public static int delay() {
			return module().vinesDelay.getValue();
		}

		public static TickPriority tickPriority() {
			return module().vinesTickPriority.getValue();
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

	public static class WhiteConcretePowder {

		public static boolean conductRedstone() {
			return module().whiteConcretePowderConductRedstone.getValue();
		}
	}

	public static class WoodenButton {

		public static int delayRisingEdge() {
			return module().woodenButtonDelayRisingEdge.getValue();
		}

		public static int delayFallingEdge() {
			return module().woodenButtonDelayFallingEdge.getValue();
		}
	
		public static int signal() {
			return module().woodenButtonSignal.getValue();
		}

		public static int signalDirect() {
			return module().woodenButtonSignalDirect.getValue();
		}

		public static TickPriority tickPriorityRisingEdge() {
			return module().woodenButtonTickPriorityRisingEdge.getValue();
		}

		public static TickPriority tickPriorityFallingEdge() {
			return module().woodenButtonTickPriorityFallingEdge.getValue();
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

	private static RedstoneTweaksModule module() {
		GSController controller = GSController.getInstanceOnThread();

		if (controller == null) {
			throw new IllegalStateException("no g4mespeed controller on this thread!");
		}

		return controller.getModule(RedstoneTweaksModule.class);
	}
}
