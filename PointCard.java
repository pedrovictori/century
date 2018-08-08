package core;

import android.util.Log;

public class PointCard {
    private boolean hasGoldCoin = false;
    private boolean hasSilverCoin = false;
    private final int points;
    private final Inventory goal;

    public PointCard(int points, Inventory goal) {
        this.points = points;
        this.goal = goal;
    }

    public boolean hasGoldCoin() {
        return hasGoldCoin;
    }

    public void setHasGoldCoin(boolean hasGoldCoin) {
        this.hasGoldCoin = hasGoldCoin;
    }

    public boolean hasSilverCoin() {
        return hasSilverCoin;
    }

    public void setHasSilverCoin(boolean hasSilverCoin) {
        this.hasSilverCoin = hasSilverCoin;
    }

    public int getPoints() {
        return points;
    }

    public Inventory getGoal() {
        return goal;
    }

    /**
     * Most cards give points according to a simple rule:
     * brown cubes are worth 4p, green 3p, red 2p, yellow 1p.
     * But there are exceptions where the points amount is bigger than the cubes' value.
     *
     * @return true if points and value are the same, false if not.
     */
    public boolean pointsAgreeWithValue() {
        return points == goal.getValue();
    }

    /**
     * Imports cards in the following notation:
     * p21p4110
     *
     * @param notation
     * @return
     */
    public static PointCard factory(String notation) {
        char cardType = notation.charAt(0);
        String[] split = notation.split("p");
        split = notation.substring(1).split("p");
        Log.d("import of point card", "points: " + split[0] + ", goal: " + split[1]);
        return new PointCard(Integer.valueOf(split[0]), Inventory.terse(split[1]));
    }
}
