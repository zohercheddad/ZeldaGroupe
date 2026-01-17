public abstract class Item extends GameObject {

    private int value;

    public Item(double x, double y, double width, double height, int value) {
        super(x, y, width, height);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    // désactive l’item (il disparaît + plus de collision)
    public void disable() {
        setEnable(false);
    }



}
