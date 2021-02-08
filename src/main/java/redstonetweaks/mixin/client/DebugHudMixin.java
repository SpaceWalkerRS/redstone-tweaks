package redstonetweaks.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.math.BlockPos;

import redstonetweaks.block.entity.BlockEntityTypes;
import redstonetweaks.block.entity.PowerBlockEntity;

@Mixin(DebugHud.class)
public class DebugHudMixin {
	
	@Shadow @Final private MinecraftClient client;
	
	@Inject(method = "getRightText", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 1, shift = Shift.BEFORE, target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"))
	private void onGetRightTextInjectBeforeGetBlock1(CallbackInfoReturnable<List<String>> cir, long l, long m, long n, long o, List<String> text, BlockPos pos) {
		BlockState state = client.world.getBlockState(pos);
		
		if (BlockEntityTypes.POWER_BLOCK.supports(state.getBlock())) {
			BlockEntity blockEntity = client.world.getBlockEntity(pos);
			
			if (blockEntity instanceof PowerBlockEntity) {
				text.add(String.format("real power: ", ((PowerBlockEntity)blockEntity).getPower()));
			}
		} else if (BlockEntityType.COMPARATOR.supports(state.getBlock())) {
			BlockEntity blockEntity = client.world.getBlockEntity(pos);
			
			if (blockEntity instanceof ComparatorBlockEntity) {
				text.add(String.format("power: ", ((ComparatorBlockEntity)blockEntity).getOutputSignal()));
			}
		}
	}
}
