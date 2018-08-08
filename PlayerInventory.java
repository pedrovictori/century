package century_core;

import android.util.Log;

public class PlayerInventory extends Inventory {


	public PlayerInventory(int b, int g, int r, int y) {
		super(b, g, r, y);
		if (b < 0 || g < 0 || r < 0 || y < 0) {
			throw new IllegalArgumentException("Quantities need to be positive");
		}
		checkTotal(true);
	}

	public PlayerInventory(Inventory inventory) {
		this(inventory.getB(), inventory.getG(), inventory.getR(), inventory.getY());
		checkTotal(true);
	}

	public void change(InventoryChange change) {
		setB(getB() + change.getB());
		setG(getG() + change.getG());
		setR(getR() + change.getR());
		setY(getY() + change.getY());

		checkTotal(false);
	}

	/**
	 *
	 * @param throwError
	 * @return an core.InventoryChange if inventory was changed, null otherwise
	 */
	public InventoryChange checkTotal(boolean throwError) {
		if (getTotal() > 10) {
			if (throwError) {
				throw new IllegalArgumentException("core.Inventory can't have more than ten elements");
			} else {
				Log.i("Info", "core.Inventory can't have more than ten elements");
				return fixOverflow();
			}
		} else return null;
	}

	private InventoryChange fixOverflow() {
		int exceeding = getTotal() - 10;
		InventoryChange inventoryChange = new InventoryChange();
		for (int i = 0; i < exceeding; i++) {
			if(getY() > 0) inventoryChange.changeY(-1);
			else if(getR() > 0) inventoryChange.changeR(-1);
			else if(getG() > 0) inventoryChange.changeG(-1);
			else{
				inventoryChange.changeB(-1);
			}
		}

		change(inventoryChange);
		return inventoryChange;
	}
}
