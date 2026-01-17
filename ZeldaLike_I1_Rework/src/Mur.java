import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Mur extends GameObject {

    public Mur(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    @Override
    public void createToken(double x, double y) {
        Rectangle m = new Rectangle(-getWidth() / 2.0, -getHeight() / 2.0, getWidth(), getHeight());
        m.setFill(Color.DARKOLIVEGREEN);
        m.setStroke(Color.BLACK);

        setToken(new Group(m)); // équivalent de "this.token = new Group(m)" -->token private
        setX(x);
        setY(y);
    }
}


