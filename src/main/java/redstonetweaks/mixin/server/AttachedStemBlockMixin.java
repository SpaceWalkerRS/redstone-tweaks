package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.AttachedStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GourdBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

import redstonetweaks.interfaces.mixin.RTIPlant;

@Mixin(AttachedStemBlock.class)
public class AttachedStemBlockMixin implements RTIPlant {
	
	@Shadow @Final private GourdBlock gourdBlock;
	
	@Override
	public boolean hasAttachmentTo(BlockState state, Direction dir, Block neighborBlock) {
		return dir == state.get(Properties.HORIZONTAL_FACING) && neighborBlock == gourdBlock;
	}
}
