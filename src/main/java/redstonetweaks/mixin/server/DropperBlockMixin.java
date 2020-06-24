package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.dropperDelay;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.DropperBlock;
import net.minecraft.world.WorldView;

@Mixin(DropperBlock.class)
public class DropperBlockMixin {
	
	public int getTickRate(WorldView world) {
		return dropperDelay.get();
	}
}
