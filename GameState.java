package century_core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {
	private int goldCoinsLeft;
	private int silverCoinsLeft;
	private int playerCount;
	private int aiCount;
	private int totalCount;
	private int currentTurn = 0;
	private boolean gameWillEndThisRound = false;
	private boolean gameEnded = false;
	private Map<Player, Integer> scores;
	private Player winner;
	private List<PointCard> pointRow;
	private List<MerchantCard> merchantRow;
	private List<Player> players = new ArrayList<>();
	private static List<PlayerInventory> startingInventories = new ArrayList<>();

	static {
		startingInventories.add(0, new PlayerInventory(0, 0, 0, 3));
		startingInventories.add(1, new PlayerInventory(0, 0, 0, 4));
		startingInventories.add(2, new PlayerInventory(0, 0, 0, 4));
		startingInventories.add(3, new PlayerInventory(0, 0, 1, 3));
		startingInventories.add(4, new PlayerInventory(0, 0, 1, 3));
	}

	public GameState(List<PointCard> pointRow, List<MerchantCard> merchantRow, int playerCount, int aiCount) {
		this.pointRow = pointRow;
		this.merchantRow = merchantRow;
		this.playerCount = playerCount;
		this.aiCount = aiCount;
		totalCount = playerCount + aiCount;
		if (totalCount > 5) {
			throw new IllegalArgumentException("Total count can't be larger than 5");
		}

		goldCoinsLeft = 2 * totalCount;
		silverCoinsLeft = goldCoinsLeft;

		for (int i = 1; i <= totalCount; i++) {
			Player newPlayer = new Player(startingInventories.get(i), i);
			players.add(newPlayer);
		}

	}

	/**
	 * This method gives a claimed point card to the player, removes the cubes spent and gives coins to the player as needed. This method doesn't remove the card from the point row, nor adds a new point card to the row.
	 *
	 * @param player
	 * @param pointCard
	 */
	public void givePointCardToPlayer(Player player, PointCard pointCard) {
		int index = pointRow.indexOf(pointCard);
		if (index == -1) {
			throw new IllegalArgumentException("Point card does not exist");
		}
		if (player.getInventory().contains(pointCard.getGoal())) {
			player.getPointCardsClaimed().add(pointCard); //give card to player

			//check for coins
			if (index == 0) {
				if (goldCoinsLeft > 0) {
					player.addGoldCoin();
					goldCoinsLeft--;
				} else if (silverCoinsLeft > 0) {// if there are no gold coins left, silver coins would be on the first card
					player.addSilverCoin();
					silverCoinsLeft--;
				}
			} else if (index == 1 && silverCoinsLeft > 0) {
				player.addSilverCoin();
				silverCoinsLeft--;
			}

			//remove cubes from player's inventory
			InventoryChange change = new InventoryChange(pointCard.getGoal()).multiplyInventory(-1);
			player.getInventory().change(change);

			//check for end of the game condition
			if ((totalCount <= 3 && player.getPointCardsClaimed().size() == 6) || totalCount > 3 && player.getPointCardsClaimed().size() == 5) {
				gameWillEndThisRound = true;
			}
		} else {
			throw new IllegalArgumentException("core.Player cannot claim that card");
		}
	}

	public int getGoldCoinsLeft() {
		return goldCoinsLeft;
	}

	public int getSilverCoinsLeft() {
		return silverCoinsLeft;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public int getAiCount() {
		return aiCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public int getCurrentTurn() {
		return currentTurn;
	}

	public List<PointCard> getPointRow() {
		return pointRow;
	}

	public List<MerchantCard> getMerchantRow() {
		return merchantRow;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Player getCurrentPlayer() {
		return players.get(currentTurn);
	}

	public void nextTurn() {
		currentTurn++;
		if (currentTurn == totalCount) {
			if (gameWillEndThisRound) {
				scores = score();
				gameEnded = true;
			} else {
				currentTurn = 0;
			}
		}
	}

	private Map<Player, Integer> score() {
		Map<Player, Integer> scores = new HashMap<>();
		for (Player player : players) {
			int score = 0;
			for (PointCard card : player.getPointCardsClaimed()) {
				score += card.getPoints();
			}
			score += player.getGoldCoins() * 3;
			score += player.getSilverCoins();
			score += player.getInventory().getTotal() - player.getInventory().getY();
			scores.put(player, score);

			int highestScore = scores.get(winner);

			//if highest is null, this means we are dealing with the first player, who so far is the highest scoring.
			// if the new score is higher than the previous highest, the current player becomes the current winner
			if (winner == null || score > highestScore) winner = player;
			else if (score == highestScore) { //there's a tie, determine who's the winner
				int currentPlayerTurnOrder = player.getTurnOrder();
				int currentWinnerTurnOrder = winner.getTurnOrder();
				if (currentPlayerTurnOrder > currentWinnerTurnOrder) winner = player;
			}
		}
		return scores;
	}

	public boolean hasGameEnded() {
		return gameEnded;
	}

	public Map<Player, Integer> getScores() {
		return scores;
	}

	public Player getWinner() {
		return winner;
	}
}
