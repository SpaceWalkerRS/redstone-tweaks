package redstonetweaks.mixin.server;

import java.util.function.Function;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.WorldAccess;

import redstonetweaks.helper.BlockHelper;
import redstonetweaks.interfaces.mixin.RTIAbstractBlockState;
import redstonetweaks.interfaces.mixin.RTISettings;
import redstonetweaks.interfaces.mixin.RTIWorld;

@Mixin(AbstractBlockState.class)
public abstract class AbstractBlockStateMixin implements RTIAbstractBlockState {
	
	private Function<Integer, Integer> delayOverride;
	private Function<TickPriority, TickPriority> tickPriorityOverride;
	private Supplier<Boolean> forceMicroTickMode;
	
	@Shadow protected abstract BlockState asBlockState();
	
	@Inject(
			method = "<init>",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(
					value = "RETURN"
			)
	)
	private void onInitInjectAtReturn(Block block, ImmutableMap<Property<?>, Comparable<?>> propertyMap, MapCodec<BlockState> mapCodec, CallbackInfo ci, AbstractBlock.Settings settings) {
		delayOverride = ((RTISettings)settings).getDelayOverride();
		tickPriorityOverride = ((RTISettings)settings).getTickPriorityOverride();
		forceMicroTickMode = ((RTISettings)settings).getForceMicroTickMode();
	}
	
	@Inject(method = "isSideSolid", cancellable = true, at = @At(value = "HEAD"))
	private void onIsSideSolidInjectAtReturn(BlockView world, BlockPos pos, Direction direction, SideShapeType shapeType, CallbackInfoReturnable<Boolean> cir) {
		if (BlockHelper.isSideSolid(world, pos, direction, asBlockState(), shapeType)) {
			cir.setReturnValue(true);
			cir.cancel();
		}
	}

	@Inject(method = "updateNeighbors(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;II)V", cancellable = true, at = @At(value = "HEAD"))
	private void onUpdateNeighborsInjectAtHead(WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth, CallbackInfo ci) {
		((RTIWorld)world).dispatchShapeUpdatesAround(pos, pos, asBlockState(), flags, maxUpdateDepth);
		
		ci.cancel();
	}
	
	@Override
	public int delayOverride(int delay) {
		return delayOverride.apply(delay);
	}
	
	@Override
	public TickPriority tickPriorityOverride(TickPriority tickPriority) {
		return tickPriorityOverride.apply(tickPriority);
	}
	
	@Override
	public boolean forceMicroTickMode() {
		return forceMicroTickMode.get();
	}
}
