package core;

public class InventoryChange extends Inventory {
	public InventoryChange(int b, int g, int r, int y) {
		super(b, g, r, y);
	}

	public InventoryChange(Inventory inventory) {
		super(inventory);
	}

	public InventoryChange() {
		super();
	}

	@Override
	public int getTotal() {
		throw new UnsupportedOperationException();
	}

	@Override
	public InventoryChange multiplyInventory(int times) {
		super.multiplyInventory(times);
		return this;
	}
}
