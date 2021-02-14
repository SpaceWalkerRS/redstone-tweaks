package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import redstonetweaks.interfaces.mixin.RTIDaylightDetectorBlockEntity;
import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(DaylightDetectorBlock.class)
public abstract class DaylightDetectorBlockMixin extends AbstractBlock {
	
	public DaylightDetectorBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Inject(method = "getWeakRedstonePower", cancellable = true, at = @At(value = "HEAD"))
	private void onGetWeakRedstonePowerInjectAtHead(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (blockEntity instanceof DaylightDetectorBlockEntity) {
			((RTIDaylightDetectorBlockEntity)blockEntity).ensureCorrectPower(state);
			
			cir.setReturnValue(((RTIDaylightDetectorBlockEntity)blockEntity).getPower());
			cir.cancel();
		}
	}
	
	@Redirect(method = "updateState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getLightLevel(Lnet/minecraft/world/LightType;Lnet/minecraft/util/math/BlockPos;)I"))
	private static int onUpdateStateRedirectGetLightLevel(World world, LightType lightType, BlockPos pos) {
		int lightLevel = world.getLightLevel(lightType, pos);
		int ambientDarkness = world.getAmbientDarkness();
		
		int maxPower = Tweaks.Global.POWER_MAX.get();
		
		return (int)((maxPower / 15.0F) * (lightLevel - ambientDarkness));
	}
	
	@Redirect(method = "updateState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getAmbientDarkness()I"))
	private static int onUpdateStateRedirectGetAmbientDarkness(World world) {
		// replaced by the redirect above
		return 0;
	}
	
	@ModifyConstant(method = "updateState", constant = @Constant(intValue = 15, ordinal = 0))
	private static int onUpdateStateModifyInvertPower(int oldValue) {
		return Tweaks.Global.POWER_MAX.get();
	}
	
	@Inject(method = "updateState", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I"))
	private static void onUpdateStateInjectBeforeClamp(BlockState state, World world, BlockPos pos, CallbackInfo ci, int newPower) {
		boolean powerChanged = false;
		
		newPower = MathHelper.clamp(newPower, 0, Tweaks.Global.POWER_MAX.get());
		
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (blockEntity instanceof DaylightDetectorBlockEntity) {
			if (((RTIDaylightDetectorBlockEntity)blockEntity).getPower() != newPower) {
				((RTIDaylightDetectorBlockEntity)blockEntity).setPower(newPower);
				
				powerChanged = true;
			}
		}
		
		newPower = MathHelper.clamp(newPower, 0, 15);
		
		if (state.get(Properties.POWER) != newPower) {
			world.setBlockState(pos, state.with(Properties.POWER, newPower), 3);
			
			powerChanged = true;
		} else if (!world.isClient()) {
			if (blockEntity instanceof DaylightDetectorBlockEntity) {
				world.getServer().getPlayerManager().sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), 64.0D, world.getRegistryKey(), blockEntity.toUpdatePacket());
			}
		}
		
		if (powerChanged) {
			if (Tweaks.DaylightDetector.EMITS_STRONG_POWER.get()) {
				((RTIWorld)world).dispatchBlockUpdates(pos, null, state.getBlock(), Tweaks.DaylightDetector.BLOCK_UPDATE_ORDER.get());
			} else {
				world.updateNeighborsAlways(pos, state.getBlock());
			}
		}
	}
	
	@Redirect(method = "updateState", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I"))
	private static int onUpdateStateRedirectClamp(int value, int min, int max, BlockState state, World world, BlockPos pos) {
		// replaced by the inject above
		return state.get(Properties.POWER);
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return (Tweaks.DaylightDetector.EMITS_STRONG_POWER.get() && direction == Direction.UP) ? getWeakRedstonePower(state, world, pos, direction) : 0;
	}
}
