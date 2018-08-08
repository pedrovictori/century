package core;

public interface MerchantCard {
	InventoryChange getChange(Inventory inventory);

	/**
	 * Merchant cards can have cubes on them when they are on the row
	 * @return the cubes that the card has on it at the moment
	 */
	Inventory getInventory();

	void setInventory(Inventory inventory);

}
