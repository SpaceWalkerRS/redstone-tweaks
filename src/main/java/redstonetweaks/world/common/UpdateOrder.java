package redstonetweaks.world.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import redstonetweaks.util.Directionality;
import redstonetweaks.util.PacketUtils;
import redstonetweaks.util.RelativePos;

public class UpdateOrder {
	
	private final Directionality directionality;
	private final AbstractNeighborUpdate.Mode defaultMode;
	private final boolean forceDefaultMode;
	
	private int offsetX;
	private int offsetY;
	private int offsetZ;
	private NotifierOrder notifierOrder;
	private List<AbstractNeighborUpdate> neighborUpdates;
	
	public UpdateOrder(Directionality directionality, NotifierOrder notifierOrder) {
		this(directionality, notifierOrder, AbstractNeighborUpdate.Mode.SINGLE_UPDATE, false);
	}
	
	public UpdateOrder(Directionality directionality, NotifierOrder notifierOrder, AbstractNeighborUpdate.Mode defaultMode, boolean forceDefaultMode) {
		this.directionality = directionality;
		this.notifierOrder = notifierOrder;
		this.defaultMode = defaultMode;
		this.forceDefaultMode = forceDefaultMode;
		this.neighborUpdates = new ArrayList<>();
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
				|| order.forceDefaultMode != forceDefaultMode
				|| order.notifierOrder != notifierOrder
				|| order.offsetX != offsetX
				|| order.offsetY != offsetY
				|| order.offsetZ != offsetZ
				|| order.neighborUpdates.size() != neighborUpdates.size())
			{
				return false;
			}
			for (int i = 0; i < neighborUpdates.size(); i++) {
				AbstractNeighborUpdate update = neighborUpdates.get(i);
				AbstractNeighborUpdate otherUpdate = order.neighborUpdates.get(i);
				
				if (!update.equals(otherUpdate)) {
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	public void encode(PacketByteBuf buffer) {
		buffer.writeByte(directionality.getIndex());
		buffer.writeByte(defaultMode.getIndex());
		buffer.writeBoolean(forceDefaultMode);
		
		buffer.writeInt(offsetX);
		buffer.writeInt(offsetY);
		buffer.writeInt(offsetZ);
		buffer.writeByte(notifierOrder.getIndex());
		
		buffer.writeInt(neighborUpdates.size());
		for (AbstractNeighborUpdate update : neighborUpdates) {
			PacketUtils.writeAbstractNeighborUpdate(buffer, update);
		}
	}
	
	public void decode(PacketByteBuf buffer) {
		offsetX = buffer.readInt();
		offsetY = buffer.readInt();
		offsetZ = buffer.readInt();
		notifierOrder = NotifierOrder.fromIndex(buffer.readByte());
		
		int size = buffer.readInt();
		for (int i = 0; i < size; i++) {
			add(PacketUtils.readAbstractNeighborUpdate(buffer));
		}
	}
	
	public UpdateOrder copy() {
		UpdateOrder copy = new UpdateOrder(directionality, notifierOrder, defaultMode, forceDefaultMode);
		
		for (AbstractNeighborUpdate update : neighborUpdates) {
			copy.add(update.copy());
		}
		copy.setOffset(offsetX, offsetY, offsetZ);
		
		return copy;
	}
	
	public Directionality getDirectionality() {
		return directionality;
	}
	
	public boolean forceDefaultMode() {
		return forceDefaultMode;
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
	
	public NotifierOrder getNotifierOrder() {
		return notifierOrder;
	}
	
	public void setNotifierOrder(NotifierOrder notifierOrder) {
		this.notifierOrder = notifierOrder;
	}
	
	public void cycleNotifierOrder(boolean next) {
		setNotifierOrder(next ? notifierOrder.next() : notifierOrder.previous());
	}
	
	public List<AbstractNeighborUpdate> getNeighborUpdates() {
		return neighborUpdates;
	}
	
	public void insert(int index, RelativePos notifier, RelativePos update) {
		insert(index, defaultMode, notifier, update);
	}
	
	public void insert(int index, AbstractNeighborUpdate.Mode mode, RelativePos notifier, RelativePos update) {
		insert(index, new AbstractNeighborUpdate(mode, notifier, update));
	}
	
	public void insert(int index, AbstractNeighborUpdate update) {
		try {
			getNeighborUpdates().add(index, update);
		} catch (Exception e) {
			
		}
	}
	
	public UpdateOrder add(RelativePos notifier, RelativePos update) {
		insert(getNeighborUpdates().size(), notifier, update);
		return this;
	}
	
	public UpdateOrder add(AbstractNeighborUpdate.Mode mode, RelativePos notifier, RelativePos update) {
		insert(getNeighborUpdates().size(), mode, notifier, update);
		return this;
	}
	
	public UpdateOrder add(AbstractNeighborUpdate update) {
		insert(getNeighborUpdates().size(), update);
		return this;
	}
	
	public void remove(int index) {
		try {
			getNeighborUpdates().remove(index);
		} catch (Exception e) {
			
		}
	}
	
	public void moveUp(int index) {
		swap(index, index - 1);
	}
	
	public void moveDown(int index) {
		swap(index, index + 1);
	}
	
	public void swap(int index1, int index2) {
		if (index1 != index2 && inRange(index1) && inRange(index2)) {
			if (index1 < index2) {
				swapElements(index1, index2);
			} else {
				swapElements(index2, index1);
			}
		}
	}
	
	private void swapElements(int index1, int index2) {
		AbstractNeighborUpdate update1 = getNeighborUpdates().set(index1, null);
		AbstractNeighborUpdate update2 = getNeighborUpdates().set(index2, null);
		
		getNeighborUpdates().set(index1, update2);
		getNeighborUpdates().set(index2, update1);
	}
	
	private boolean inRange(int index) {
		return index >= 0 && index < getNeighborUpdates().size();
	}
	
	public Collection<AbstractNeighborUpdate> getUpdates(BlockPos pos, Direction sourceFacing) {
		Collection<AbstractNeighborUpdate> updates = (notifierOrder == NotifierOrder.LOCATIONAL) ? new HashSet<>() : new ArrayList<>();
		
		for (AbstractNeighborUpdate update : getNeighborUpdates()) {
			AbstractNeighborUpdate copy = update.copy();
			
			if (notifierOrder == NotifierOrder.LOCATIONAL) {
				copy.setHashPos(pos, sourceFacing, offsetX, offsetY, offsetZ);
			}
			
			updates.add(copy);
		}
		
		if (notifierOrder == NotifierOrder.RANDOM) {
			Collections.shuffle((List<AbstractNeighborUpdate>)updates);
		}
		
		return updates;
	}
	
	public enum NotifierOrder {
		
		SEQUENTIAL(0, "Sequential"),
		LOCATIONAL(1, "Locational"),
		RANDOM(2, "Random");
		
		public static final NotifierOrder[] ORDERS;
		
		static {
			ORDERS = new NotifierOrder[values().length];
			
			for (NotifierOrder order : NotifierOrder.values()) {
				ORDERS[order.index] = order;
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
			if (index < 0) {
				return ORDERS[ORDERS.length - 1];
			}
			if (index >= ORDERS.length) {
				return ORDERS[0];
			}
			
			return ORDERS[index];
		}
		
		public String getName() {
			return name;
		}
		
		public NotifierOrder next() {
			return fromIndex(index + 1);
		}
		
		public NotifierOrder previous() {
			return fromIndex(index - 1);
		}
	}
}
