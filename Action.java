package century_core;

import java.util.ArrayList;
import java.util.List;

public enum Action {
	REST {
		@Override
		public boolean doAction(GameState gameState, String[] info) {
			gameState.getCurrentPlayer().rest();
			gameState.nextTurn();
			return true;
		}
	},
	ACQUIRE {
		@Override
		public boolean doAction(GameState gameState, String[] info) {
			//the array can be {acquire, 5, yyyr, u2} or {acquire, 1, u2}
			int index;
			Player player = gameState.getCurrentPlayer();
			try {
				index = Integer.valueOf(info[1]);
				index--; //from human index to programming index

			} catch (NumberFormatException nfe) { //bad notation
				System.out.println(nfe.getMessage());
				setErrorMessage(badNotation);
				return false;
			}

			//determine new card and place cubes on row if necessary
			MerchantCard newCard;
			if(info.length == 4 && index != 0){ //if cubes were placed, it shouldn't be the first card in the row
				//check cubes placed are equal to number of cards ahead of card acquired
				String cubesPlaced = info[2];
				if(index != cubesPlaced.length()){
					setErrorMessage(badNotation);
					return false;
				}

				//place cubes on merchant cards row
				//determine how many cubes are needed
				InventoryChange cubesToBePlaced = new InventoryChange();
				List<Inventory> cubesOnRow = new ArrayList<>();
				for (int i = 0; i < index; i++) {
					char c = cubesPlaced.charAt(i);
					Inventory cube;
					if(c == 'y')cube = Inventory.terse("0001");
					else if(c == 'r')cube = Inventory.terse("0010");
					else if(c == 'g')cube = Inventory.terse("0100");
					else if(c == 'b')cube = Inventory.terse("1000");
					else{
						setErrorMessage(badNotation);
						return false; //bad notation
					}

					cubesToBePlaced.addInventory(cube);
					cubesOnRow.add(i, cube);
				}

				if (player.getInventory().contains(cubesToBePlaced)) { //if player can afford it, remove them from their inventory
					player.getInventory().change(cubesToBePlaced.multiplyInventory(-1));

					for (int i = 0; i < cubesOnRow.size(); i++) { //add cubes to cards on row
						gameState.getMerchantRow().get(i).getInventory().addInventory(cubesOnRow.get(i));
					}
				}

				else{
					setErrorMessage(cantAfford);
					return false;
				}

				newCard = MerchantCardFactory.importCard(info[3]);
			}

			else if(info.length == 3 && index == 0){ //no cubes placed, should be the first card in the row
				newCard = MerchantCardFactory.importCard(info[2]);
			}

			else{
				setErrorMessage(badNotation);
				return false; //bad notation
			}

			MerchantCard cardAcquired = gameState.getMerchantRow().get(index);
			gameState.getCurrentPlayer().getHand().add(cardAcquired); //acquire card
			gameState.getMerchantRow().remove(cardAcquired); //remove from row
			gameState.getMerchantRow().add(newCard); //add new card to row
			gameState.nextTurn();
			return true;
		}
	},
	PLAY {
		@Override
		public boolean doAction(GameState gameState, String[] info) { //the array will be something like {play,s002}, {play,u2,0002}, {play,t0011=0200, 2}
			if (info.length < 2 || info.length > 3) {
				setErrorMessage(badNotation);
				return false;
			}

			MerchantCard played = MerchantCardFactory.importCard(info[1]);
			Player player = gameState.getCurrentPlayer();
			int index = player.getHand().indexOf(played); //find the card in the player's hand
			if (index == -1) {
				setErrorMessage(cardNotFound);
				return false;
			}

			InventoryChange change;
			if (played instanceof SpiceCard) {
				 change = played.getChange(null);
			} else if (played instanceof TradeCard) {
				if (info.length == 2) {
					change = played.getChange(player.getInventory());
				} else {
					try {
						change = ((TradeCard)played).getChange(player.getInventory(), Integer.valueOf(info[2]));
					} catch (NumberFormatException e) {
						setErrorMessage(badNotation);
						e.printStackTrace();
						return false;
					}
				}
			} else if (played instanceof UpgradeCard) {
				if (info.length == 2) {
					setErrorMessage(badNotation);
					return false;
				} else {
					try {
						change = played.getChange(Inventory.terse(info[2]));
					} catch (Exception e) {
						setErrorMessage(badNotation);
						e.printStackTrace();
						return false;
					}
				}
			} else {
				throw new IllegalStateException("core.MerchantCard subclass not recognised");
			}

			player.getInventory().change(change); //play the card
			InventoryChange report = player.getInventory().checkTotal(false); //check for maximum size
			if (change != null) {
				setErrorMessage("core.Inventory exceeds maximum size. The following was removed to fix this: " + report.toNotation());
			}

			//todo move to core.Player
			player.getPlayedCards().add(player.getHand().get(index)); //add card to played cards
			player.getHand().remove(index); //remove card from hand
			gameState.nextTurn();
			return true;
		}
	},
	CLAIM {
		@Override
		public boolean doAction(GameState gameState, String[] info) {//array should be something like {claim,3,p21p4000}
			if (info.length != 3) {
				setErrorMessage(badNotation);
				return false;
			}

			int index;
			Player player = gameState.getCurrentPlayer();
			try {
				index = Integer.valueOf(info[1]);
				index--; //from human index to programming index

			} catch (NumberFormatException nfe) { //bad notation
				System.out.println(nfe.getMessage());
				setErrorMessage(badNotation);
				return false;
			}
			PointCard cardClaimed = gameState.getPointRow().get(index);

			if (player.getInventory().contains(cardClaimed.getGoal())) {
				gameState.givePointCardToPlayer(player, cardClaimed);
				gameState.getPointRow().remove(cardClaimed); //remove card from row
				gameState.getPointRow().add(PointCard.factory(info[2])); //add new card to row
				gameState.nextTurn();
				return true;
			}

			else{
				setErrorMessage(cantClaim);
				return false;
			}
		}
	};

	private String errorMessage = "";
	private static final String badNotation = "Bad notation";
	private static final String cantAfford = "Can't afford to place cubes";
	private static final String cardNotFound = "Card not found";
	private static final String cantClaim = "Card can't be claimed yet";

	/**
	 * Executes the corresponding action
	 * @param gameState
	 * @param info
	 * @return true if action execution was successful
	 */
	public abstract boolean doAction(GameState gameState, String[] info);

	public String getErrorMessage() {
		return errorMessage;
	}

	protected void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
