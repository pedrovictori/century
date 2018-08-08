package century_core;

public class TradeCard extends AbstractMerchantCard{
	private final Inventory input;
	private final Inventory output;

	public TradeCard(Inventory input, Inventory output) {
		this.input = input;
		this.output = output;
	}

	/**
	 * Computes one iteration. See getChange(core.PlayerInventory inventory, int iterations) for a more complete method.
	 * @param inventory the current player's inventory
	 * @return an core.InventoryChange with the input to be detracted and output to be added
	 */
	@Override
	public InventoryChange getChange(Inventory inventory) {
		PlayerInventory playerInventory = (PlayerInventory) inventory;
		return getChange(playerInventory, 1);
	}

	public InventoryChange getChange(PlayerInventory inventory, int iterations) {
		if (iterations > getMaxIterations(inventory)) {
			throw new IllegalArgumentException("Number of iterations exceeds possible iterations");
		} else {
			Inventory thisInput = new Inventory(input);
			thisInput.multiplyInventory(- iterations); //negative because these quantities need to be removed from player's inventory
			Inventory thisOutput = new Inventory(output);
			thisOutput.multiplyInventory(iterations);

			InventoryChange change = new InventoryChange(thisInput);
			change.addInventory(output);

			return change;
		}
	}

	private int getMaxIterations(PlayerInventory inventory) {
		int minIt = 100;
		for (int i = 0; i < 4; i++) {
			int s = input.getAt(i);

			if (s != 0) { /*divide spice numbers in inventory by the spice number needed for one iteration.
			Store the minimum across the 4 spices as that will ultimately determine how many iterations can be made in total */
				int maxIt = Math.floorDiv(inventory.getAt(i), s);
				if(maxIt<minIt) minIt = maxIt;
			}
		}

		return minIt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TradeCard tradeCard = (TradeCard) o;
		return input.equals(tradeCard.input) &&
				output.equals(tradeCard.output);
	}
}
