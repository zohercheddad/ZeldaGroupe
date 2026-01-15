import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.Random;

public class Monster extends Character {

    // =========================
    // Attributs (PRIVATE)
    // =========================
    private int frameCount = 0;      // compteur de frames
    private Random rnd = new Random(); // pour direction aléatoire

    // =========================
    // Constructeur
    // =========================
    /**
     * @param x position X initiale
     * @param y position Y initiale
     */
    public Monster(double x, double y) {
        super(x, y, 30, 30, 3); // width, height, vitesse
        changeDirection();     // direction initiale
    }

    // =========================
    // Création graphique
    // =========================
    @Override
    public void createToken(double x, double y) {

        // Corps du monstre
        Rectangle corps = new Rectangle(-getWidth() / 2, -getHeight() / 2, getWidth(), getHeight());
        corps.setFill(Color.DARKGREEN);
        corps.setOpacity(0.5);

        // Yeux
        Circle oeil1 = new Circle(8 - getWidth()/2, 1 - getHeight()/2, 3);
        Circle oeil2 = new Circle(22 - getWidth()/2, 1 - getHeight()/2, 3);

        // Groupe principal
        Group g = new Group(corps, oeil1, oeil2);

        // Queue (3 segments)
        for (int k = 0; k < 3; k++) {
            Ellipse e = new Ellipse(15 - getWidth()/2, k * 10 + 5 - getHeight()/2, 3, 5);
            e.setFill(Color.GREEN);
            g.getChildren().add(e);
        }

        // Position dans la scène
        g.setLayoutX(x);
        g.setLayoutY(y);

        setToken(g);
    }


    // =========================
    // Accesseurs / Mutateurs
    // =========================
    public double getRotation() {
        return getToken().getRotate();
    }
    public void setRotation(double r) {
        getToken().setRotate(r);
    }

    // =========================
    // Déplacement automatique
    // =========================
    public void resolveMove(List<GameObject> gos) {

        double dx = 0;
        double dy = 0;

        // Déplacement selon la rotation
        switch ((int) getRotation()) {
            case 0:   dy = -getVitesse(); break; // nord
            case 90:  dx =  getVitesse(); break; // est
            case 180: dy =  getVitesse(); break; // sud
            case 270: dx = -getVitesse(); break; // ouest
        }

        move(dx, dy, getRotation(), gos);

        // Changement de direction toutes les 150 frames
        frameCount++;
        if (frameCount % 150 == 0) {//remettrea 0
            changeDirection();
        }
    }

    // =========================
    // Direction aléatoire
    // =========================
    public void changeDirection() {
        int[] dirs = {0, 90, 180, 270};
        setRotation(dirs[rnd.nextInt(4)]);
    }

    // =========================
    // Collision
    // =========================
    @Override
    public void onCollideWith(GameObject go) {
        changeDirection(); // rebond

        if (go instanceof Joueur) {
            ((Joueur) go).reciveDamages(2);
        }
    }
}




