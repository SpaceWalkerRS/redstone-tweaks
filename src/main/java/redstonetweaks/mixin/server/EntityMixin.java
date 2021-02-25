package redstonetweaks.mixin.server;

import java.util.Arrays;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import redstonetweaks.interfaces.mixin.RTIEntity;

@Mixin(Entity.class)
public abstract class EntityMixin implements RTIEntity {
	
	@Shadow public World world;
	@Shadow private long pistonMovementTick;
	@Shadow @Final private double[] pistonMovementDelta;
	@Shadow public boolean noClip;
	
	private double[] maxMovementByPiston;
	
	@Shadow public abstract void move(MovementType type, Vec3d movement);
	
	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void onInitInjectAtReturn(EntityType<?> type, World world, CallbackInfo ci) {
		maxMovementByPiston = new double[] {0.0D, 0.0D, 0.0D};
	}
	
	@Redirect(method = "move", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/MovementType;PISTON:Lnet/minecraft/entity/MovementType;"))
	private MovementType onMoveRedirectMovementTypePiston(MovementType type, Vec3d movement) {
		// The movement is adjusted for pistons before the call to move
		return (type == MovementType.PISTON) ? null : MovementType.PISTON;
	}
	
	@Override
	public void moveByPiston(Vec3d movement, Vec3d pistonMovement) {
		if (noClip) {
			move(MovementType.PISTON, movement);
		} else {
			move(MovementType.PISTON, adjustMovementByPiston(movement, pistonMovement));
		}
	}
	
	private Vec3d adjustMovementByPiston(Vec3d movement, Vec3d blockMovement) {
		long time = world.getTime();
		
		if (pistonMovementTick != time) {
			pistonMovementTick = time;
			
			Arrays.fill(pistonMovementDelta, 0.0D);
			Arrays.fill(maxMovementByPiston, 0.0D);
		}
		
		updateMaxMovementByPiston(blockMovement);
		
		double movementX = adjustMovementByPiston(movement.x, 0);
		double movementY = adjustMovementByPiston(movement.y, 1);
		double movementZ = adjustMovementByPiston(movement.z, 2);
		
		return new Vec3d(movementX, movementY, movementZ);
	}
	
	private void updateMaxMovementByPiston(Vec3d blockMovement) {
		maxMovementByPiston[0] = Math.max(maxMovementByPiston[0], 1.02D * Math.abs(blockMovement.x));
		maxMovementByPiston[1] = Math.max(maxMovementByPiston[1], 1.02D * Math.abs(blockMovement.y));
		maxMovementByPiston[2] = Math.max(maxMovementByPiston[2], 1.02D * Math.abs(blockMovement.z));
	}
	
	private double adjustMovementByPiston(double movement, int axis) {
		if (movement == 0.0D) {
			return movement;
		}
		
		double currentMovement = pistonMovementDelta[axis];
		double maxMovement = maxMovementByPiston[axis];
		
		double combinedMovement = MathHelper.clamp(movement + currentMovement, -maxMovement, maxMovement);
		double newMovement = combinedMovement - currentMovement;
		
		pistonMovementDelta[axis] = combinedMovement;
		
		return (Math.abs(newMovement) <= 9.999999747378752E-6D) ? 0.0D : newMovement;
	}
}
