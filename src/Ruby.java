import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.Group;

public class Ruby extends Item {

    public Ruby(double x, double y) {
        super(x, y, 30, 30, 3); // OBLIGATOIRE
    }


    @Override
    public void createToken(double x, double y) {

        // On récupère les dimensions via les accesseurs
        // car width et height sont PRIVATE dans GameObject
        double w = getWidth();
        double h = getHeight();

        // Polygone principal : diamant
        Polygon diamant = new Polygon(
                0, -h / 2,   // haut
                w / 2, 0,    // droite
                0, h / 2,    // bas
                -w / 2, 0    // gauche
        );
        diamant.setFill(Color.CYAN);
        diamant.setStroke(Color.DARKBLUE);
        diamant.setStrokeWidth(3);

        // Facette supérieure droite
        Polygon facet1 = new Polygon(
                0, -h / 2,
                w / 2, 0,
                0, -h / 4
        );
        facet1.setFill(Color.LIGHTBLUE);

        // Facette supérieure gauche
        Polygon facet2 = new Polygon(
                0, -h / 2,
                0, -h / 4,
                -w / 2, 0
        );
        facet2.setFill(Color.AZURE);

        // Facette inférieure droite
        Polygon facet3 = new Polygon(
                0, h / 2,
                w / 2, 0,
                0, h / 4
        );
        facet3.setFill(Color.DEEPSKYBLUE);

        // Facette inférieure gauche
        Polygon facet4 = new Polygon(
                0, h / 2,
                0, h / 4,
                -w / 2, 0
        );
        facet4.setFill(Color.SKYBLUE);

        // Assemblage de toutes les formes
        Group g = new Group(
                diamant,
                facet1,
                facet2,
                facet3,
                facet4
        );

        // Positionnement dans la scène
        g.setLayoutX(x);
        g.setLayoutY(y);

        // Enregistrement du token via le setter protégé
        // (token est PRIVATE dans GameObject)
        setToken(g);
    }
}

