package core;

public class MerchantCardFactory {

	private MerchantCardFactory() { }

	/**
	 * Imports cards in the following notation:
	 * - Spice Cards: ej. for y2 and r1: s0012
	 * - Upgrade Cards: u2 or u3
	 * - Trade Cards: ej. for b1 to y2r2: t1000=0022
	 * @param notation
	 * @return
	 */
	public static MerchantCard importCard(String notation) {
		MerchantCard card;
		char cardType = notation.charAt(0);
		if(cardType == 's'){
			card = new SpiceCard(new InventoryChange(notation.charAt(1),
					notation.charAt(2),
					notation.charAt(3),
					notation.charAt(4)));
		}

		else if (cardType == 'u') {
			card = new UpgradeCard(notation.charAt(1));
		}

		else if (cardType == 't') {
			card = new TradeCard(
					new Inventory(notation.charAt(1),
							notation.charAt(2),
							notation.charAt(3),
							notation.charAt(4)),
					new Inventory(notation.charAt(6),
							notation.charAt(7),
							notation.charAt(8),
							notation.charAt(9)));
		}
		else throw new IllegalArgumentException("Bad notation");

		return card;
	}
}
