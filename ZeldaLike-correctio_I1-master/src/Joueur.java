import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

public class Joueur extends Character {

    // -------------------------
    // Etat du joueur
    // -------------------------
    private int live = 10;     // 10 par défaut
    private int points = 0;    // score

    // -------------------------
    // Constructeur
    // -------------------------
    public Joueur(double x, double y) {
        super(x, y, 80, 20, 3); // width=80, height=20, vitesse=3
    }

    // -------------------------
    // Getters pour le HUD du Main
    // -------------------------
    public int getLive()   { return live; }
    public int getPoints() { return points; }
    public double getRotation() { return getToken().getRotate(); }

    // -------------------------
    // Token Joueur (visuel)
    // -------------------------
    @Override
    public void createToken(double x, double y) {
        System.out.println("Méthode createToken du joueur appelée");

        Ellipse corp = new Ellipse(0, 0, getWidth() / 2, getHeight() / 2);
        Circle tete  = new Circle(0, 0, 15);
        Circle main  = new Circle(32, -10, 6);
        Rectangle sword = new Rectangle(25, -13, 15, 3);

        sword.setFill(Color.WHITE);
        corp.setFill(Color.DARKOLIVEGREEN);

        Group g = new Group(corp, tete, main, sword);
        g.setLayoutX(x);
        g.setLayoutY(y);

        setToken(g); // IMPORTANT : initialise token (sinon null)
    }

    // -------------------------
    // Item : ramassage + score
    // -------------------------
    public void drop(Item i) {
        i.disable();            // désactive l’item (invisible + plus de collision)
        points += i.getValue(); // ajoute la valeur de l’item au score
    }

    // -------------------------
    // Dégâts + recul (10 px)
    // -------------------------
    public void reciveDamages(int x) {
        live = Math.max(0, live - x); // ne descend jamais sous 0

        double r = getRotation();
        if (r == 0)        setY(getY() + 10); // regarde nord -> recule vers le bas
        else if (r == 180) setY(getY() - 10); // regarde sud  -> recule vers le haut
        else if (r == 90)  setX(getX() - 10); // regarde est  -> recule vers la gauche
        else if (r == 270) setX(getX() + 10); // regarde ouest-> recule vers la droite
    }

    // -------------------------
    // Collision : Item vs reste
    // -------------------------
    @Override
    public void onCollideWith(GameObject go) {
        if (go instanceof Item) {
            drop((Item) go);
        } else {
            repositionAbout(go); // recale le joueur au bord de l’objet
        }
    }

    // -----------------------------------------------------
    // Recalage au bord (logique "prof" Itération 1)
    // -----------------------------------------------------
    private void repositionAbout(GameObject go) {
        // Opérateur ternaire : condition ? valeur_si_vrai : valeur_si_faux
        // Dimensions de collision selon rotation (0/180 = normal, 90/270 = inversé)
        double actualWidth  = (getRotation() == 0 || getRotation() == 180) ? getWidth()  : getHeight();
        double actualHeight = (getRotation() == 0 || getRotation() == 180) ? getHeight() : getWidth();

        if (collideLeft(go)) {
            setX(go.getXRight() + actualWidth / 2 + 1);
        } else if (collideRight(go)) {
            setX(go.getXLeft() - actualWidth / 2 - 1);
        } else if (collideTop(go)) {
            setY(go.getYBottom() + actualHeight / 2 + 1);
        } else if (collideBottom(go)) {
            setY(go.getYTop() - actualHeight / 2 - 1);
        }
    }
}

