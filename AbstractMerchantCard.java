package core;

public abstract class AbstractMerchantCard implements MerchantCard {
	private Inventory inventory = new Inventory();

	public abstract InventoryChange getChange(Inventory inventory);

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
}
