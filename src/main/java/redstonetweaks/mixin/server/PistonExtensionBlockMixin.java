package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import redstonetweaks.helper.BlockHelper;

@Mixin(PistonExtensionBlock.class)
public class PistonExtensionBlockMixin implements BlockHelper {
	
	@Override
	public boolean continueEvent(World world, BlockState state, BlockPos pos, int type) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof PistonBlockEntity) {
			PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
			
			if (pistonBlockEntity.isSource()) {
				BlockState piston = pistonBlockEntity.getPushedBlock();
				((BlockHelper)piston.getBlock()).continueEvent(world, piston, pos, type);
				
				return true;
			}
		}
		
		return false;
	}

}
