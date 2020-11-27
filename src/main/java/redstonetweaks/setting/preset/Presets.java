package redstonetweaks.setting.preset;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.TickPriority;

import redstonetweaks.setting.ServerConfig;
import redstonetweaks.setting.Tweaks;
import redstonetweaks.util.Directionality;
import redstonetweaks.util.RelativePos;
import redstonetweaks.world.common.AbstractNeighborUpdate;
import redstonetweaks.world.common.UpdateOrder;

public class Presets {
	
	public static final List<Preset> ALL = new ArrayList<>();
	
	public static void register(Preset preset) {
		ALL.add(preset);
	}
	
	public static boolean isRegistered(Preset preset) {
		return ALL.contains(preset);
	}
	
	public static void init() {
		Default.init();
		Bedrock.init();
	}
	
	public static class Default {
		
		public static final Preset DEFAULT = new Preset("Default", Tweaks.TWEAKS, Preset.Mode.SET, false);
		
		public static void init() {
			Presets.register(DEFAULT);
			
			Tweaks.Global.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL, AbstractNeighborUpdate.Mode.SINGLE_UPDATE, true).
					add(RelativePos.SELF, RelativePos.WEST).
					add(RelativePos.SELF, RelativePos.EAST).
					add(RelativePos.SELF, RelativePos.DOWN).
					add(RelativePos.SELF, RelativePos.UP).
					add(RelativePos.SELF, RelativePos.NORTH).
					add(RelativePos.SELF, RelativePos.SOUTH));
			Tweaks.Global.COMPARATOR_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL, AbstractNeighborUpdate.Mode.SINGLE_UPDATE, true).
					add(RelativePos.SELF, RelativePos.WEST).
					add(RelativePos.SELF, RelativePos.EAST).
					add(RelativePos.SELF, RelativePos.NORTH).
					add(RelativePos.SELF, RelativePos.SOUTH));
			Tweaks.Global.SHAPE_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL, AbstractNeighborUpdate.Mode.SINGLE_UPDATE, true).
					add(RelativePos.SELF, RelativePos.WEST).
					add(RelativePos.SELF, RelativePos.EAST).
					add(RelativePos.SELF, RelativePos.NORTH).
					add(RelativePos.SELF, RelativePos.SOUTH).
					add(RelativePos.SELF, RelativePos.DOWN).
					add(RelativePos.SELF, RelativePos.UP));
			Tweaks.Global.CHAINSTONE.setPresetValue(DEFAULT, false);
			Tweaks.Global.DELAY_MULTIPLIER.setPresetValue(DEFAULT, 1);
			Tweaks.Global.DO_BLOCK_UPDATES.setPresetValue(DEFAULT, true);
			Tweaks.Global.DO_SHAPE_UPDATES.setPresetValue(DEFAULT, true);
			Tweaks.Global.DO_COMPARATOR_UPDATES.setPresetValue(DEFAULT, true);
			Tweaks.Global.DOUBLE_RETRACTION.setPresetValue(DEFAULT, false);
			Tweaks.Global.INSTANT_BLOCK_EVENTS.setPresetValue(DEFAULT, false);
			Tweaks.Global.MERGE_SLABS.setPresetValue(DEFAULT, false);
			Tweaks.Global.MOVABLE_BLOCK_ENTITIES.setPresetValue(DEFAULT, false);
			Tweaks.Global.MOVABLE_MOVING_BLOCKS.setPresetValue(DEFAULT, false);
			Tweaks.Global.POWER_MAX.setPresetValue(DEFAULT, 15);
			Tweaks.Global.RANDOMIZE_BLOCK_EVENTS.setPresetValue(DEFAULT, false);
			Tweaks.Global.RANDOMIZE_DELAYS.setPresetValue(DEFAULT, false);
			Tweaks.Global.RANDOMIZE_TICK_PRIORITIES.setPresetValue(DEFAULT, false);
			Tweaks.Global.SHOW_NEIGHBOR_UPDATES.setPresetValue(DEFAULT, false);
			Tweaks.Global.SHOW_PROCESSING_ORDER.setPresetValue(DEFAULT, 0);
			
			Tweaks.BugFixes.MC54711.setPresetValue(DEFAULT, false);
			Tweaks.BugFixes.MC120986.setPresetValue(DEFAULT, false);
			Tweaks.BugFixes.MC136566.setPresetValue(DEFAULT, false);
			Tweaks.BugFixes.MC137127.setPresetValue(DEFAULT, false);
			Tweaks.BugFixes.MC172213.setPresetValue(DEFAULT, false);
			
			Tweaks.ActivatorRail.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.ActivatorRail.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.ActivatorRail.LAZY_RISING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.ActivatorRail.LAZY_FALLING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.ActivatorRail.POWER_LIMIT.setPresetValue(DEFAULT, 9);
			Tweaks.ActivatorRail.QC.setPresetValue(DEFAULT, new Boolean[] {false, false, false, false, false, false});
			Tweaks.ActivatorRail.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.ActivatorRail.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.ActivatorRail.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Bamboo.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.Bamboo.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Barrier.IS_MOVABLE.setPresetValue(DEFAULT, false);
			
			Tweaks.BubbleColumn.DELAY.setPresetValue(DEFAULT, 5);
			Tweaks.BubbleColumn.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Cactus.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.Cactus.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.ChorusPlant.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.ChorusPlant.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.CommandBlock.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.CommandBlock.QC.setPresetValue(DEFAULT, new Boolean[] {false, false, false, false, false, false});
			Tweaks.CommandBlock.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.CommandBlock.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Comparator.ADDITION_MODE.setPresetValue(DEFAULT, false);
			Tweaks.Comparator.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.HORIZONTAL, UpdateOrder.NotifierOrder.NORMAL).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.SELF, RelativePos.FRONT).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.FRONT, RelativePos.BACK));
			Tweaks.Comparator.DELAY.setPresetValue(DEFAULT, 2);
			Tweaks.Comparator.REDSTONE_BLOCKS_VALID_SIDE_INPUT.setPresetValue(DEFAULT, true);
			Tweaks.Comparator.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.Comparator.TICK_PRIORITY_FACING_DIODE.setPresetValue(DEFAULT, TickPriority.HIGH);
			
			Tweaks.Composter.DELAY.setPresetValue(DEFAULT, 20);
			Tweaks.Composter.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Coral.DELAY_MIN.setPresetValue(DEFAULT, 60);
			Tweaks.Coral.DELAY_MAX.setPresetValue(DEFAULT, 100);
			Tweaks.Coral.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.CoralBlock.DELAY_MIN.setPresetValue(DEFAULT, 60);
			Tweaks.CoralBlock.DELAY_MAX.setPresetValue(DEFAULT, 100);
			Tweaks.CoralBlock.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.DetectorRail.DELAY.setPresetValue(DEFAULT, 20);
			Tweaks.DetectorRail.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.DetectorRail.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.DetectorRail.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Dispenser.DELAY.setPresetValue(DEFAULT, 4);
			Tweaks.Dispenser.LAZY.setPresetValue(DEFAULT, true);
			Tweaks.Dispenser.QC.setPresetValue(DEFAULT, new Boolean[] {false, true, false, false, false, false});
			Tweaks.Dispenser.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.Dispenser.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.DragonEgg.DELAY.setPresetValue(DEFAULT, 5);
			
			Tweaks.Dropper.DELAY.setPresetValue(DEFAULT, 4);
			Tweaks.Dropper.LAZY.setPresetValue(DEFAULT, true);
			Tweaks.Dropper.QC.setPresetValue(DEFAULT, new Boolean[] {false, true, false, false, false, false});
			Tweaks.Dropper.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.Dropper.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Farmland.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.Farmland.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Fire.DELAY_MIN.setPresetValue(DEFAULT, 10);
			Tweaks.Fire.DELAY_MAX.setPresetValue(DEFAULT, 30);
			Tweaks.Fire.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.FrostedIce.DELAY_MIN.setPresetValue(DEFAULT, 20);
			Tweaks.FrostedIce.DELAY_MAX.setPresetValue(DEFAULT, 40);
			Tweaks.FrostedIce.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.GrassPath.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.GrassPath.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.GravityBlock.DELAY.setPresetValue(DEFAULT, 2);
			Tweaks.GravityBlock.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.HeavyWeightedPressurePlate.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.DOWN, RelativePos.WEST));
			Tweaks.HeavyWeightedPressurePlate.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.HeavyWeightedPressurePlate.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 10);
			Tweaks.HeavyWeightedPressurePlate.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.HeavyWeightedPressurePlate.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.HeavyWeightedPressurePlate.WEIGHT.setPresetValue(DEFAULT, 150);
			
			Tweaks.Hopper.COOLDOWN_DEFAULT.setPresetValue(DEFAULT, 8);
			Tweaks.Hopper.COOLDOWN_PRIORITY.setPresetValue(DEFAULT, 7);
			Tweaks.Hopper.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.Hopper.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.Hopper.LAZY_RISING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.Hopper.LAZY_FALLING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.Hopper.QC.setPresetValue(DEFAULT, new Boolean[] {false, false, false, false, false, false});
			Tweaks.Hopper.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.Hopper.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.Hopper.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Lava.DELAY_DEFAULT.setPresetValue(DEFAULT, 30);
			Tweaks.Lava.DELAY_NETHER.setPresetValue(DEFAULT, 10);
			Tweaks.Lava.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Leaves.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.Leaves.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Lectern.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.Lectern.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.Lectern.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.Lectern.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.Lectern.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.Lectern.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Lever.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.BOTH, UpdateOrder.NotifierOrder.NORMAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.FRONT, RelativePos.WEST));
			Tweaks.Lever.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.Lever.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.Lever.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.Lever.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.Lever.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.Lever.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.LightWeightedPressurePlate.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.DOWN, RelativePos.WEST));
			Tweaks.LightWeightedPressurePlate.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.LightWeightedPressurePlate.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 10);
			Tweaks.LightWeightedPressurePlate.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.LightWeightedPressurePlate.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.LightWeightedPressurePlate.WEIGHT.setPresetValue(DEFAULT, 15);
			
			Tweaks.MagentaGlazedTerracotta.IS_POWER_DIODE.setPresetValue(DEFAULT, false);
			
			Tweaks.MagmaBlock.DELAY.setPresetValue(DEFAULT, 20);
			Tweaks.MagmaBlock.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.NormalPiston.CONNECTS_TO_WIRE.setPresetValue(DEFAULT, false);
			Tweaks.NormalPiston.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.NormalPiston.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.NormalPiston.IGNORE_UPDATES_WHILE_EXTENDING.setPresetValue(DEFAULT, false);
			Tweaks.NormalPiston.IGNORE_UPDATES_WHILE_RETRACTING.setPresetValue(DEFAULT, true);
			Tweaks.NormalPiston.LAZY_RISING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.NormalPiston.LAZY_FALLING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.NormalPiston.MOVABLE_WHEN_EXTENDED.setPresetValue(DEFAULT, false);
			Tweaks.NormalPiston.PUSH_LIMIT.setPresetValue(DEFAULT, 12);
			Tweaks.NormalPiston.QC.setPresetValue(DEFAULT, new Boolean[] {false, true, false, false, false, false});
			Tweaks.NormalPiston.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.NormalPiston.SPEED_RISING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.NormalPiston.SPEED_FALLING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.NormalPiston.SUPPORTS_BRITTLE_BLOCKS.setPresetValue(DEFAULT, false);
			Tweaks.NormalPiston.HEAD_UPDATES_ON_EXTENSION.setPresetValue(DEFAULT, true);
			Tweaks.NormalPiston.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.NormalPiston.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.NormalPiston.UPDATE_SELF_WHILE_POWERED.setPresetValue(DEFAULT, false);
			
			Tweaks.NoteBlock.DELAY.setPresetValue(DEFAULT, 0);
			Tweaks.NoteBlock.LAZY.setPresetValue(DEFAULT, false);
			Tweaks.NoteBlock.QC.setPresetValue(DEFAULT, new Boolean[] {false, false, false, false, false, false});
			Tweaks.NoteBlock.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.NoteBlock.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Observer.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.BOTH, UpdateOrder.NotifierOrder.NORMAL).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.SELF, RelativePos.FRONT).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.FRONT, RelativePos.BACK));
			Tweaks.Observer.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.Observer.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.Observer.DISABLE.setPresetValue(DEFAULT, false);
			Tweaks.Observer.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.Observer.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.Observer.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.Observer.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.PoweredRail.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.PoweredRail.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.PoweredRail.LAZY_RISING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.PoweredRail.LAZY_FALLING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.PoweredRail.POWER_LIMIT.setPresetValue(DEFAULT, 9);
			Tweaks.PoweredRail.QC.setPresetValue(DEFAULT, new Boolean[] {false, false, false, false, false, false});
			Tweaks.PoweredRail.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.PoweredRail.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.PoweredRail.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.RedSand.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.DOWN, RelativePos.UP).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.UP, RelativePos.DOWN).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.NORTH, RelativePos.SOUTH).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.SOUTH, RelativePos.NORTH).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.WEST, RelativePos.EAST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.EAST, RelativePos.WEST));
			Tweaks.RedSand.CONNECTS_TO_WIRE.setPresetValue(DEFAULT, false);
			Tweaks.RedSand.POWER_WEAK.setPresetValue(DEFAULT, 0);
			Tweaks.RedSand.POWER_STRONG.setPresetValue(DEFAULT, 0);
			
			Tweaks.RedstoneBlock.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.DOWN, RelativePos.UP).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.UP, RelativePos.DOWN).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.NORTH, RelativePos.SOUTH).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.SOUTH, RelativePos.NORTH).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.WEST, RelativePos.EAST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.EAST, RelativePos.WEST));
			Tweaks.RedstoneBlock.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.RedstoneBlock.POWER_STRONG.setPresetValue(DEFAULT, 0);
			
			Tweaks.RedstoneLamp.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.RedstoneLamp.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 4);
			Tweaks.RedstoneLamp.LAZY_RISING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.RedstoneLamp.LAZY_FALLING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.RedstoneLamp.QC.setPresetValue(DEFAULT, new Boolean[] {false, false, false, false, false, false});
			Tweaks.RedstoneLamp.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.RedstoneLamp.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.RedstoneLamp.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.RedstoneOre.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.DOWN, RelativePos.UP).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.UP, RelativePos.DOWN).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.NORTH, RelativePos.SOUTH).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.SOUTH, RelativePos.NORTH).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.WEST, RelativePos.EAST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.EAST, RelativePos.WEST));
			Tweaks.RedstoneOre.CONNECTS_TO_WIRE.setPresetValue(DEFAULT, false);
			Tweaks.RedstoneOre.DELAY.setPresetValue(DEFAULT, 0);
			Tweaks.RedstoneOre.POWER_WEAK.setPresetValue(DEFAULT, 0);
			Tweaks.RedstoneOre.POWER_STRONG.setPresetValue(DEFAULT, 0);
			Tweaks.RedstoneOre.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.RedstoneTorch.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.DOWN, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.UP, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.NORTH, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SOUTH, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.WEST, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.EAST, RelativePos.WEST));
			Tweaks.RedstoneTorch.BURNOUT_COUNT.setPresetValue(DEFAULT, 8);
			Tweaks.RedstoneTorch.BURNOUT_TIMER.setPresetValue(DEFAULT, 60);
			Tweaks.RedstoneTorch.DELAY_BURNOUT.setPresetValue(DEFAULT, 160);
			Tweaks.RedstoneTorch.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.RedstoneTorch.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.RedstoneTorch.LAZY_RISING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.RedstoneTorch.LAZY_FALLING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.RedstoneTorch.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.RedstoneTorch.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.RedstoneTorch.SOFT_INVERSION.setPresetValue(DEFAULT, false);
			Tweaks.RedstoneTorch.TICK_PRIORITY_BURNOUT.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.RedstoneTorch.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.RedstoneTorch.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.RedstoneWire.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.LOCATIONAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.DOWN, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.UP, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.NORTH, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SOUTH, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.WEST, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.EAST, RelativePos.WEST));
			Tweaks.RedstoneWire.DELAY.setPresetValue(DEFAULT, 0);
			Tweaks.RedstoneWire.INVERT_FLOW_ON_GLASS.setPresetValue(DEFAULT, false);
			Tweaks.RedstoneWire.SLABS_ALLOW_UP_CONNECTION.setPresetValue(DEFAULT, true);
			Tweaks.RedstoneWire.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Repeater.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.HORIZONTAL, UpdateOrder.NotifierOrder.NORMAL).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.SELF, RelativePos.FRONT).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.FRONT, RelativePos.BACK));
			Tweaks.Repeater.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.Repeater.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.Repeater.LAZY_RISING_EDGE.setPresetValue(DEFAULT, true);
			Tweaks.Repeater.LAZY_FALLING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.Repeater.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.Repeater.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.Repeater.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.HIGH);
			Tweaks.Repeater.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.VERY_HIGH);
			Tweaks.Repeater.TICK_PRIORITY_FACING_DIODE.setPresetValue(DEFAULT, TickPriority.EXTREMELY_HIGH);
			
			Tweaks.Scaffolding.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.Scaffolding.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.SoulSand.DELAY.setPresetValue(DEFAULT, 20);
			Tweaks.SoulSand.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Stairs.FULL_FACES_ARE_SOLID.setPresetValue(DEFAULT, false);
			
			Tweaks.StickyPiston.CONNECTS_TO_WIRE.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.DO_BLOCK_DROPPING.setPresetValue(DEFAULT, true);
			Tweaks.StickyPiston.FAST_BLOCK_DROPPING.setPresetValue(DEFAULT, true);
			Tweaks.StickyPiston.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.StickyPiston.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.StickyPiston.IGNORE_UPDATES_WHILE_EXTENDING.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.IGNORE_UPDATES_WHILE_RETRACTING.setPresetValue(DEFAULT, true);
			Tweaks.StickyPiston.LAZY_RISING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.LAZY_FALLING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.MOVABLE_WHEN_EXTENDED.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.PUSH_LIMIT.setPresetValue(DEFAULT, 12);
			Tweaks.StickyPiston.QC.setPresetValue(DEFAULT, new Boolean[] {false, true, false, false, false, false});
			Tweaks.StickyPiston.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.SPEED_RISING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.StickyPiston.SPEED_FALLING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.StickyPiston.SUPER_STICKY.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.SUPPORTS_BRITTLE_BLOCKS.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.HEAD_UPDATES_ON_EXTENSION.setPresetValue(DEFAULT, true);
			Tweaks.StickyPiston.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.StickyPiston.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.StickyPiston.UPDATE_SELF_WHILE_POWERED.setPresetValue(DEFAULT, false);
			
			Tweaks.StoneButton.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.BOTH, UpdateOrder.NotifierOrder.NORMAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.FRONT, RelativePos.WEST));
			Tweaks.StoneButton.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.StoneButton.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 20);
			Tweaks.StoneButton.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.StoneButton.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.StoneButton.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.StoneButton.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.StonePressurePlate.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.DOWN, RelativePos.WEST));
			Tweaks.StonePressurePlate.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.StonePressurePlate.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 20);
			Tweaks.StonePressurePlate.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.StonePressurePlate.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.StonePressurePlate.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.StonePressurePlate.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.SugarCane.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.SugarCane.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.TargetBlock.DELAY_DEFAULT.setPresetValue(DEFAULT, 8);
			Tweaks.TargetBlock.DELAY_PERSISTENT_PROJECTILE.setPresetValue(DEFAULT, 20);
			Tweaks.TargetBlock.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.TNT.DELAY.setPresetValue(DEFAULT, 0);
			Tweaks.TNT.FUSE_TIME.setPresetValue(DEFAULT, 80);
			Tweaks.TNT.LAZY.setPresetValue(DEFAULT, false);
			Tweaks.TNT.QC.setPresetValue(DEFAULT, new Boolean[] {false, false, false, false, false, false});
			Tweaks.TNT.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.TNT.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Tripwire.DELAY.setPresetValue(DEFAULT, 10);
			Tweaks.Tripwire.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.TripwireHook.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.HORIZONTAL, UpdateOrder.NotifierOrder.NORMAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.FRONT, RelativePos.WEST));
			Tweaks.TripwireHook.DELAY.setPresetValue(DEFAULT, 10);
			Tweaks.TripwireHook.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.TripwireHook.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.TripwireHook.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Vines.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.Vines.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Water.DELAY.setPresetValue(DEFAULT, 5);
			Tweaks.Water.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.WoodenButton.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.BOTH, UpdateOrder.NotifierOrder.NORMAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.FRONT, RelativePos.WEST));
			Tweaks.WoodenButton.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.WoodenButton.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 30);
			Tweaks.WoodenButton.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.WoodenButton.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.WoodenButton.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.WoodenButton.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.WoodenPressurePlate.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.DOWN, RelativePos.WEST));
			Tweaks.WoodenPressurePlate.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.WoodenPressurePlate.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 20);
			Tweaks.WoodenPressurePlate.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.WoodenPressurePlate.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.WoodenPressurePlate.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.WoodenPressurePlate.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			
			ServerConfig.Tweaks.EDIT_PERMISSION_LEVEL.setPresetValue(DEFAULT, 2);
			ServerConfig.Tweaks.LOCK_PERMISSION_LEVEL.setPresetValue(DEFAULT, 2);
		}
	}
	
	public static class Bedrock {
		
		public static final Preset BEDROCK = new Preset("Bedrock", Tweaks.TWEAKS, Preset.Mode.SET);
		
		public static void init() {
			Presets.register(BEDROCK);
			
			Tweaks.Global.MOVABLE_BLOCK_ENTITIES.setPresetValue(BEDROCK, true);
			
			Tweaks.RedstoneTorch.SOFT_INVERSION.setPresetValue(BEDROCK, true);
		}
	}
}
