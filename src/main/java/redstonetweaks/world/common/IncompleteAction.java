package redstonetweaks.world.common;

import net.minecraft.util.math.BlockPos;

public abstract class IncompleteAction<T> implements IIncompleteAction {
	
	protected final BlockPos pos;
	protected final int type;
	protected final double viewDistance;
	
	protected final T object;
	
	public IncompleteAction(BlockPos pos, int type, T object) {
		this(pos, type, -1, object);
	}
	
	public IncompleteAction(BlockPos pos, int type,  double viewDistance, T object) {
		this.pos = pos;
		this.type = type;
		this.viewDistance = viewDistance;
		
		this.object = object;
	}
	
	@Override
	public BlockPos getPos() {
		return pos;
	}
	
	@Override
	public int getType() {
		return type;
	}
	
	@Override
	public double getViewDistance() {
		return viewDistance;
	}
	
	public T getObject() {
		return object;
	}
}
