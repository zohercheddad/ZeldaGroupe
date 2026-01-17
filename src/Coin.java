import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Coin extends Item {
    private static final double COIN_SIZE = 22; //

    public Coin(double x, double y) {
        super(x, y, COIN_SIZE, COIN_SIZE, 1);
    }

    @Override
    public void createToken(double x, double y) {
        //new Circle(centerX, centerY, radius);
        double rayon = getWidth() / 2.0;
        double centerX = rayon;
        double centerY = rayon;

        System.out.println("diametre=" + getWidth() + " rayon=" + rayon);
        Circle corps = new Circle(centerX, centerY , rayon );
        corps.setFill(Color.GOLD);
        corps.setStroke(Color.DARKGOLDENROD);
        corps.setStrokeWidth(3);

        Circle relief = new Circle(centerX, centerY , rayon - 5);
        relief.setFill(Color.GOLDENROD);
        relief.setOpacity(0.6);

        Group g = new Group(corps, relief);
        g.setLayoutX(x);
        g.setLayoutY(y);

        setToken(g);
    }
}

