package redstonetweaks.world.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import redstonetweaks.util.Directionality;
import redstonetweaks.util.RelativePos;

public class UpdateOrder {
	
	private final Directionality directionality;
	private final BlockUpdate.Mode defaultMode;
	private final boolean modeLocked;
	
	private NotifierOrder notifierOrder;
	private List<BlockUpdate> blockUpdates;
	private int offsetX;
	private int offsetY;
	private int offsetZ;
	
	public UpdateOrder(Directionality directionality, NotifierOrder notifierOrder) {
		this(directionality, notifierOrder, BlockUpdate.Mode.SINGLE_UPDATE, false);
	}
	
	public UpdateOrder(Directionality directionality, NotifierOrder notifierOrder, BlockUpdate.Mode defaultMode, boolean modeLocked) {
		this.directionality = directionality;
		this.notifierOrder = notifierOrder;
		this.defaultMode = defaultMode;
		this.modeLocked = modeLocked;
		this.blockUpdates = new ArrayList<>();
		this.offsetX = 0;
		this.offsetY = 0;
		this.offsetZ = 0;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof UpdateOrder) {
			UpdateOrder order = (UpdateOrder)other;
			
			if (order.directionality != directionality
				|| order.defaultMode != defaultMode
				|| order.modeLocked != modeLocked
				|| order.notifierOrder != notifierOrder
				|| order.offsetX != offsetX
				|| order.offsetY != offsetY
				|| order.offsetZ != offsetZ
				|| order.blockUpdates.size() != blockUpdates.size())
			{
				return false;
			}
			for (int i = 0; i < blockUpdates.size(); i++) {
				BlockUpdate update = blockUpdates.get(i);
				BlockUpdate otherUpdate = order.blockUpdates.get(i);
				
				if (otherUpdate.getMode() != update.getMode() 
					|| otherUpdate.getNotifierPos() != update.getNotifierPos() 
					|| otherUpdate.getUpdatePos() != update.getUpdatePos())
				{
					return false;
				}
			}
			
			return true;
		}
		return false;
	}

	public UpdateOrder copy() {
		UpdateOrder copy = new UpdateOrder(directionality, notifierOrder, defaultMode, modeLocked);
		
		for (BlockUpdate update : blockUpdates) {
			copy.add(update.getMode(), update.getNotifierPos(), update.getUpdatePos());
		}
		copy.setOffset(offsetX, offsetY, offsetZ);
		
		return copy;
	}
	
	public boolean modeLocked() {
		return modeLocked;
	}
	
	public Directionality getDirectionality() {
		return directionality;
	}
	
	public NotifierOrder getNotifierOrder() {
		return notifierOrder;
	}
	
	public void setNotifierOrder(NotifierOrder notifierOrder) {
		this.notifierOrder = notifierOrder;
	}
	
	public void cycleNotifierOrder() {
		setNotifierOrder(getNotifierOrder().next());
	}
	
	public int getOffsetX() {
		return offsetX;
	}
	
	public int getOffsetY() {
		return offsetY;
	}
	
	public int getOffsetZ() {
		return offsetZ;
	}
	
	public void setOffsetX(int x) {
		offsetX = x;
	}
	
	public void setOffsetY(int y) {
		offsetY = y;
	}
	
	public void setOffsetZ(int z) {
		offsetZ = z;
	}
	
	public void setOffset(int x, int y, int z) {
		setOffsetX(x);
		setOffsetY(y);
		setOffsetZ(z);
	}
	
	public List<BlockUpdate> getBlockUpdates() {
		return blockUpdates;
	}
	
	public void insert(int index, RelativePos notifier, RelativePos update) {
		insert(index, defaultMode, notifier, update);
	}
	
	public void insert(int index, BlockUpdate.Mode mode, RelativePos notifier, RelativePos update) {
		insert(index, new BlockUpdate(mode, notifier, update));
	}
	
	public void insert(int index, BlockUpdate update) {
		try {
			blockUpdates.add(index, update);
		} catch (Exception e) {
			
		}
	}
	
	public UpdateOrder add(RelativePos notifier, RelativePos update) {
		insert(getBlockUpdates().size(), notifier, update);
		return this;
	}
	
	public UpdateOrder add(BlockUpdate.Mode mode, RelativePos notifier, RelativePos update) {
		insert(getBlockUpdates().size(), mode, notifier, update);
		return this;
	}
	
	public void remove(int index) {
		try {
			blockUpdates.remove(index);
		} catch (Exception e) {
			
		}
	}
	
	public void dispatchBlockUpdates(World world, BlockPos sourcePos, Block sourceBlock) {
		dispatchBlockUpdates(world, sourcePos, sourceBlock, null);
	}
	
	public void dispatchBlockUpdates(World world, BlockPos sourcePos, Block sourceBlock, Direction sourceFacing) {
		getUpdates(sourcePos, sourceFacing).forEach((update) -> {
			update.removeOffset(offsetX, offsetY, offsetZ);
			update.dispatch(world, sourceBlock);
		});
	}
	
	// Only call this method from the updateNeighborsExcept method
	public void dispatchBlockUpdatesExcept(World world, BlockPos sourcePos, Block sourceBlock, RelativePos except) {
		for (BlockUpdate update : getUpdates(sourcePos, null)) {
			update.removeOffset(offsetX, offsetY, offsetZ);
			if (update.getUpdatePos() != except) {
				update.dispatch(world, sourceBlock);
			}
		}
	}
	
	protected Collection<BlockUpdate> getUpdates(BlockPos sourcePos, Direction sourceFacing) {
		Collection<BlockUpdate> updates;
		if (notifierOrder == NotifierOrder.LOCATIONAL) {
			updates = new HashSet<>();
		} else {
			updates = new ArrayList<>();
		}
		
		for (BlockUpdate update : blockUpdates) {
			BlockUpdate copy = update.copy();
			copy.initNotifier(sourcePos, sourceFacing);
			copy.applyOffset(offsetX, offsetY, offsetZ);
			updates.add(copy);
		}
		
		if (notifierOrder == NotifierOrder.RANDOM) {
			Collections.shuffle((List<BlockUpdate>)updates);
		}
		
		return updates;
	}
	
	public enum NotifierOrder {
		
		NORMAL(0, "Normal"),
		LOCATIONAL(1, "Locational"),
		RANDOM(2, "Random");
		
		public static final NotifierOrder[] ORDERS;
		
		static {
			ORDERS = new NotifierOrder[values().length];
			
			for (NotifierOrder mode : NotifierOrder.values()) {
				ORDERS[mode.index] = mode;
			}
		}
		
		private final int index;
		private final String name;
		
		private NotifierOrder(int index, String name) {
			this.index = index;
			this.name = name;
		}
		
		public int getIndex() {
			return index;
		}
		
		public static NotifierOrder fromIndex(int index) {
			if (index >= 0 && index < ORDERS.length) {
				return ORDERS[index];
			}
			return NORMAL;
		}
		
		public String getName() {
			return name;
		}
		
		public NotifierOrder next() {
			return fromIndex(index + 1);
		}
	}
}
