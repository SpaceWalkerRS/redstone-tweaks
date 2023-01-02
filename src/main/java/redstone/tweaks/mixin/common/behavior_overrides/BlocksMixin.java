package redstone.tweaks.mixin.common.behavior_overrides;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.interfaces.mixin.BehaviorOverrides;

@Mixin(Blocks.class)
public class BlocksMixin {

	@Inject(
		method = "<clinit>",
		at = @At(
			value = "TAIL"
		)
	)
	private static void rtRegisterPropertyOverrides(CallbackInfo ci) {
		overrideDelay(Blocks.WHITE_TERRACOTTA, 0);
		overrideDelay(Blocks.ORANGE_TERRACOTTA, 1);
		overrideDelay(Blocks.MAGENTA_TERRACOTTA, 2);
		overrideDelay(Blocks.LIGHT_BLUE_TERRACOTTA, 3);
		overrideDelay(Blocks.YELLOW_TERRACOTTA, 4);
		overrideDelay(Blocks.LIME_TERRACOTTA, 5);
		overrideDelay(Blocks.PINK_TERRACOTTA, 6);
		overrideDelay(Blocks.GRAY_TERRACOTTA, 7);
		overrideDelay(Blocks.LIGHT_GRAY_TERRACOTTA, 8);
		overrideDelay(Blocks.CYAN_TERRACOTTA, 9);
		overrideDelay(Blocks.PURPLE_TERRACOTTA, 10);
		overrideDelay(Blocks.BLUE_TERRACOTTA, 11);
		overrideDelay(Blocks.BROWN_TERRACOTTA, 12);
		overrideDelay(Blocks.GREEN_TERRACOTTA, 13);
		overrideDelay(Blocks.RED_TERRACOTTA, 14);
		overrideDelay(Blocks.BLACK_TERRACOTTA, 15);

		overrideMicrotickMode(Blocks.TERRACOTTA, true);

		overrideSignal(Blocks.WHITE_WOOL, 0);
		overrideSignal(Blocks.ORANGE_WOOL, 1);
		overrideSignal(Blocks.MAGENTA_WOOL, 2);
		overrideSignal(Blocks.LIGHT_BLUE_WOOL, 3);
		overrideSignal(Blocks.YELLOW_WOOL, 4);
		overrideSignal(Blocks.LIME_WOOL, 5);
		overrideSignal(Blocks.PINK_WOOL, 6);
		overrideSignal(Blocks.GRAY_WOOL, 7);
		overrideSignal(Blocks.LIGHT_GRAY_WOOL, 8);
		overrideSignal(Blocks.CYAN_WOOL, 9);
		overrideSignal(Blocks.PURPLE_WOOL, 10);
		overrideSignal(Blocks.BLUE_WOOL, 11);
		overrideSignal(Blocks.BROWN_WOOL, 12);
		overrideSignal(Blocks.GREEN_WOOL, 13);
		overrideSignal(Blocks.RED_WOOL, 14);
		overrideSignal(Blocks.BLACK_WOOL, 15);

		overrideDirectSignal(Blocks.WHITE_CONCRETE, 0);
		overrideDirectSignal(Blocks.ORANGE_CONCRETE, 1);
		overrideDirectSignal(Blocks.MAGENTA_CONCRETE, 2);
		overrideDirectSignal(Blocks.LIGHT_BLUE_CONCRETE, 3);
		overrideDirectSignal(Blocks.YELLOW_CONCRETE, 4);
		overrideDirectSignal(Blocks.LIME_CONCRETE, 5);
		overrideDirectSignal(Blocks.PINK_CONCRETE, 6);
		overrideDirectSignal(Blocks.GRAY_CONCRETE, 7);
		overrideDirectSignal(Blocks.LIGHT_GRAY_CONCRETE, 8);
		overrideDirectSignal(Blocks.CYAN_CONCRETE, 9);
		overrideDirectSignal(Blocks.PURPLE_CONCRETE, 10);
		overrideDirectSignal(Blocks.BLUE_CONCRETE, 11);
		overrideDirectSignal(Blocks.BROWN_CONCRETE, 12);
		overrideDirectSignal(Blocks.GREEN_CONCRETE, 13);
		overrideDirectSignal(Blocks.RED_CONCRETE, 14);
		overrideDirectSignal(Blocks.BLACK_CONCRETE, 15);

		overrideTickPriority(Blocks.OAK_WOOD, TickPriority.EXTREMELY_HIGH);
		overrideTickPriority(Blocks.SPRUCE_WOOD, TickPriority.VERY_HIGH);
		overrideTickPriority(Blocks.BIRCH_WOOD, TickPriority.HIGH);
		overrideTickPriority(Blocks.JUNGLE_WOOD, TickPriority.NORMAL);
		overrideTickPriority(Blocks.ACACIA_WOOD, TickPriority.LOW);
		overrideTickPriority(Blocks.DARK_OAK_WOOD, TickPriority.VERY_LOW);
		overrideTickPriority(Blocks.MANGROVE_WOOD, TickPriority.EXTREMELY_LOW);
	}

	private static void overrideDelay(Block block, int delay) {
		((BehaviorOverrides)block).setDelayOverride(delay);
	}

	private static void overrideMicrotickMode(Block block, boolean microtickMode) {
		((BehaviorOverrides)block).setMicrotickModeOverride(microtickMode);
	}

	private static void overrideSignal(Block block, int signal) {
		((BehaviorOverrides)block).setSignalOverride(signal);
	}

	private static void overrideDirectSignal(Block block, int signal) {
		((BehaviorOverrides)block).setDirectSignalOverride(signal);
	}

	private static void overrideTickPriority(Block block, TickPriority priority) {
		((BehaviorOverrides)block).setTickPriorityOverride(priority);
	}
}
