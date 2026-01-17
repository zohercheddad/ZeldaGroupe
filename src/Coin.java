import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Coin extends Item {

    public Coin(double x, double y) {
        super(x, y, 30, 30, 1);
    }

    @Override
    public void createToken(double x, double y) {

        double r = getWidth() / 2;

        Circle corps = new Circle(r, r, r);
        corps.setFill(Color.GOLD);
        corps.setStroke(Color.DARKGOLDENROD);
        corps.setStrokeWidth(3);

        Circle relief = new Circle(r, r, r - 5);
        relief.setFill(Color.GOLDENROD);
        relief.setOpacity(0.6);

        Group g = new Group(corps, relief);
        g.setLayoutX(x);
        g.setLayoutY(y);

        setToken(g);
    }
}

