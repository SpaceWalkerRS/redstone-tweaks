package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@Mixin(RedstoneOreBlock.class)
public abstract class RedstoneOreBlockMixin extends AbstractBlock {
	
	@Shadow private native static void light(BlockState state, World world, BlockPos pos);
	
	public RedstoneOreBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Redirect(method = "onBlockBreakStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneOreBlock;light(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
	private void onOnBlockBreakStartRedirectLight(BlockState state, World world, BlockPos pos) {
		update(world, state, pos);
	}
	
	@Redirect(method = "onSteppedOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneOreBlock;light(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
	private void onOnSteppedOnRedirectLight(BlockState state, World world, BlockPos pos) {
		update(world, state, pos);
	}
	
	@Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneOreBlock;light(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
	private void onOnUseRedirectLight(BlockState state, World world, BlockPos pos) {
		update(world, state, pos);
	}
	
	@Inject(method = "light", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private static void onLightInjectAfterSetBlockState(BlockState state, World world, BlockPos pos, CallbackInfo ci) {
		if (REDSTONE_ORE.get(STRONG_POWER) > 0) {
			for (Direction direction : Direction.values()) {
				BlockPos neighborPos = pos.offset(direction);
				world.updateNeighborsExcept(neighborPos, state.getBlock(), direction.getOpposite());
			}
		}
	}
	
	@Inject(method = "randomTick", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onRandomTickInjectAfterSetBlockState(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (REDSTONE_ORE.get(STRONG_POWER) > 0) {
			for (Direction direction : Direction.values()) {
				BlockPos neighborPos = pos.offset(direction);
				world.updateNeighborsExcept(neighborPos, state.getBlock(), direction.getOpposite());
			}
		}
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		light(state, world, pos);
	}
	
	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return REDSTONE_ORE.get(CONNECTS_TO_WIRE);
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
		return state.get(Properties.LIT) ? REDSTONE_ORE.get(WEAK_POWER) : 0;
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
		return state.get(Properties.LIT) ? REDSTONE_ORE.get(STRONG_POWER) : 0;
	}
	
	private void update(World world, BlockState state, BlockPos pos) {
		int delay = REDSTONE_ORE.get(DELAY);
		if (delay == 0) {
			light(state, world, pos);
		} else {
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, REDSTONE_ORE.get(TICK_PRIORITY));
		}
	}
}
