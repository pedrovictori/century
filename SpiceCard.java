package core;

public class SpiceCard extends AbstractMerchantCard {
	private InventoryChange change;

	public SpiceCard(InventoryChange change) {
		this.change = change;
	}

	/**
	 * Returns just a number of spices to be added.
	 * @param inventory Included to comply with the interface, but it is not used, can be null.
	 * @return an core.InventoryChange with the spices to be added
	 */
	@Override
	public InventoryChange getChange(Inventory inventory) {
		return change;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SpiceCard spiceCard = (SpiceCard) o;
		return change.equals(spiceCard.change);
	}
}
