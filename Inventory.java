package core;

public class Inventory implements Inventoriable {
    private int b;
    private int g;
    private int r;
    private int y;

    public Inventory() {
        b = 0;
        g = 0;
        r = 0;
        y = 0;
    }

    public Inventory(int b, int g, int r, int y) {
        this.b = b;
        this.g = g;
        this.r = r;
        this.y = y;
    }

    public Inventory(Inventory inventory) {
        this(inventory.getB(), inventory.getG(), inventory.getR(), inventory.getY());
    }

    public Inventory(int[] array) {
        this(array[0], array[1], array[2], array[3]);
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int[] getArray() {
        int[] array = {b, g, r, y};
        return array;
    }

    public int getAt(int pos) {
        return getArray()[pos];
    }

    public void setAt(int pos, int s) {
        if (pos == 0) setB(s);
        else if (pos == 1) setG(s);
        else if (pos == 2) setR(s);
        else if (pos == 3) setY(s);
    }


    public Inventory multiplyInventory(int times) {
        b *= times;
        g *= times;
        r *= times;
        y *= times;

        return this;
    }

    public void addInventory(Inventory inventory) {
        b += inventory.getB();
        g += inventory.getG();
        r += inventory.getR();
        y += inventory.getY();
    }

    public int getTotal() {
        return getB() + getG() + getR() + getY();
    }

    public void changeY(int change) {
        y += change;
    }

    public void changeR(int change) {
        r += change;
    }

    public void changeG(int change) {
       g += change;
    }

    public void changeB(int change) {
        b += change;
    }

    /**
     *
     * @param inventory
     * @return true if all elements of inventory are present in this inventory, false otherwise
     */
    public boolean contains(Inventory inventory) {
        int[] container = getArray();
        int[] contained = inventory.getArray();

        for (int i = 0; i < 4; i++) {
            if(container[i] < contained [i]) return false;
        }

        return true;
    }

    @Override
    public String toNotation() {
        return "b" + Integer.toString(getB()) +
                "g" + Integer.toString(getG()) +
                "r" + Integer.toString(getR()) +
                "y" + Integer.toString(getY());
    }

    public String toTerseNotation() {
        return Integer.toString(getB()) +
                Integer.toString(getG()) +
                Integer.toString(getR()) +
                Integer.toString(getY());
    }

    /**
     * Interprets notation in the form b1g3r0y-3
     *
     * @param notation
     * @return
     */
    public static Inventory verbose(String notation) {

        int bPos = 0;
        int gPos = notation.indexOf('g', bPos + 2);
        int rPos = notation.indexOf('r', gPos + 2);
        int yPos = notation.indexOf('y', rPos + 2);

        if (bPos < 0 || gPos < 0 || rPos < 0 || yPos < 0) {
            throw new IllegalArgumentException("Bad notation");
        }

        int bn = Integer.parseInt(notation.substring(bPos + 1, gPos));
        int gn = Integer.parseInt(notation.substring(gPos + 1, gPos));
        int rn = Integer.parseInt(notation.substring(rPos + 1, gPos));
        int yn = Integer.parseInt(notation.substring(yPos + 1, gPos));

        return new Inventory(bn, gn, rn, yn);
    }

    /**
     * Interprets notation in the form 130-3
     *
     * @param notation
     * @return
     */
    public static Inventory terse(String notation) {
        int[] inv = new int[4];
        for (int i = 0, j = 0; i < notation.length(); i++) {
            char c = notation.charAt(i);
            String num = "";

            if (c == '-') {
                num += c;
            } else {
                num += c;
                inv[j] = Integer.valueOf(String.valueOf(num));
                j++;
                if (j > 4) break;
            }
        }
        return new Inventory(inv[0], inv[1], inv[2], inv[3]);
    }

    /**
     * Values: b4, g3, r2, y1
     *
     * @return the sum of the values of each cube
     */
    public int getValue() {
        int value = 0;
        value += getB() * 4;
        value += getG() * 3;
        value += getR() * 2;
        value += getY() * 1;
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Inventory inventory = (Inventory) o;
        return getB() == inventory.getB() &&
                getG() == inventory.getG() &&
                getR() == inventory.getR() &&
                getY() == inventory.getY();
    }
}
