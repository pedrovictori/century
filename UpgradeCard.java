package century_core;

public class UpgradeCard extends AbstractMerchantCard {
	private int upgrades;

	public UpgradeCard(int upgrades) {
		this.upgrades = upgrades;
	}

	public int getUpgrades() {
		return upgrades;
	}

	/**
	 * For upgrade 2 all options are univocal.
	 * For upgrade 3, most of the options are also univocal, ej with 3 0003 = 0030, 0021 = 0210, 0111 = 1110
	 * ej with 2: 0110 = 2000, 0002 = 0110, 0020 = 1100
	 * ej with 1: 0001 = 1000
	 * But there is a case that can go two ways:
	 * 0011 = 0200 or 1010
	 * But is assumed that 0011 = 0200, since 1010 is actually the same than (u3 0001) + 0010 = u3 0011
	 *
	 * @param inventory the spices to be upgrades, not a whole core.PlayerInventory
	 * @return
	 */
	@Override
	public InventoryChange getChange(Inventory inventory) {
		InventoryChange change;
		int nSpices = inventory.getTotal();
		if (nSpices > upgrades) throw new IllegalArgumentException("Too many cubes to be upgraded");

		else if (nSpices == upgrades) {
			if (inventory.getB() != 0) throw new IllegalArgumentException("Brown cube can't be upgraded");
			change = new InventoryChange();
			for (int i = 0; i < 4; i++) {
				int s = inventory.getAt(i);
				if (s != 0) {
					change.setAt(i, -s); //delete spices at old level
					change.setAt(i + 1, s); //create spices at new level
				}
			}
		} else { // if (nSpices < upgrades)
			String c = inventory.toTerseNotation();
			change = new InventoryChange();
			if (upgrades == 2) {
				if (inventory.getB() != 0 || inventory.getG() != 0)
					throw new IllegalArgumentException("Trying to upgrade above brown cube");
				for (int i = 0; i < 4; i++) {
					int s = inventory.getAt(i);
					if (s != 0) {
						change.setAt(i, -s); //delete spices at old level
						change.setAt(i + 2, s); //create spices at new level
					}
				}

			} else {//(upgrades == 3). The easiest solution here is to just plan for each specific case, since they are very few
				if (c.equals("0110")) change = new InventoryChange(2, 0, 0, 0);
				else if (c.equals("0002")) change = new InventoryChange(0, 1, 1, 0);
				else if (c.equals("0001")) change = new InventoryChange(1, 0, 0, 0);
				else if (c.equals("0020")) change = new InventoryChange(1, 1, 0, 0);

					//special case
				else if (c.equals("0011")) change = new InventoryChange(0, 2, 0, 0);
			}
		}

		return change;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UpgradeCard upgradeCard = (UpgradeCard) o;
		return upgrades == upgradeCard.upgrades;
	}
}
