package core;

import java.util.ArrayList;
import java.util.List;

public class Player {
	private List<MerchantCard> hand = new ArrayList<>();
	private List<MerchantCard> playedCards = new ArrayList<>();
	private List<PointCard> pointCardsClaimed = new ArrayList<>();
	private int silverCoins = 0;
	private int goldCoins = 0;
	private PlayerInventory inventory;
	private int turnOrder;
	private int points = 0;

	public Player(PlayerInventory inventory, int turnOrder) {
		this.inventory = inventory;
		this.turnOrder = turnOrder;

		//all players start with the same hand
		hand.add(new SpiceCard(new InventoryChange(0, 0, 0, 2))); //adding create 2y card
		hand.add(new UpgradeCard(2)); //adding upgrade 2 card
	}

	public List<MerchantCard> getHand() {
		return hand;
	}

	public List<MerchantCard> getPlayedCards() {
		return playedCards;
	}

	public List<PointCard> getPointCardsClaimed() {
		return pointCardsClaimed;
	}

	public PlayerInventory getInventory() {
		return inventory;
	}

	public int getSilverCoins() {
		return silverCoins;
	}

	public void addSilverCoin() {
		silverCoins ++;
	}

	public int getGoldCoins() {
		return goldCoins;
	}

	public void addGoldCoin() {
		goldCoins++;
	}

	public int getTurnOrder() {
		return turnOrder;
	}

	public void rest(){
		hand.addAll(playedCards);
		playedCards.clear();
	}
}
