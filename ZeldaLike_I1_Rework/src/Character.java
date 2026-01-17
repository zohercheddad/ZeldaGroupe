import java.util.List;

/**
 * Character = tout personnage mobile (Joueur ou Monster).
 * Hérite de GameObject : a donc un token, des dimensions, et des bornes AABB.
 */
public abstract class Character extends GameObject {

    // =========================
    // Attribut
    // =========================
    // Vitesse "de base" utilisée par le clavier / IA pour générer dx/dy.
    private int vitesse;

    // =========================
    // Constructeur
    // =========================
    public Character(double x, double y, double width, double height, int vitesse) {
        super(x, y, width, height); // initialise la partie GameObject (dimensions + createToken)
        setVitesse(vitesse);        // initialise la vitesse (avec validation)
    }

    // =========================
    // Accesseurs / Mutateurs
    // =========================
    public int getVitesse() { return vitesse; }

    public void setVitesse(int vitesse) {
        if (vitesse < 0) throw new IllegalArgumentException("vitesse doit être >= 0");
        this.vitesse = vitesse;
    }

    // =========================
    // Méthodes "collision" : rotation-aware
    // =========================
    // Quand on tourne à 90°/270°, largeur et hauteur "logiques" s'inversent.
    private double demiLargeurCollision() {
        double r = getToken().getRotate();
        return (r == 0 || r == 180) ? getWidth() / 2.0 : getHeight() / 2.0;
    }

    private double demiHauteurCollision() {
        double r = getToken().getRotate();
        return (r == 0 || r == 180) ? getHeight() / 2.0 : getWidth() / 2.0;
    }

    // On redéfinit les bornes AABB du personnage en tenant compte de la rotation.
    @Override public double getXLeft()   { return getX() - demiLargeurCollision(); }
    @Override public double getXRight()  { return getX() + demiLargeurCollision(); }
    @Override public double getYTop()    { return getY() - demiHauteurCollision(); }
    @Override public double getYBottom() { return getY() + demiHauteurCollision(); }

    // =========================
    // Déplacement + résolution des collisions (logique "prof")
    // =========================
    /**
     * dx, dy : translation demandée (ex: +3 droite, -3 gauche, etc.)
     * rotation : 0, 90, 180, 270 (orientation du sprite)
     * gos : liste de tous les GameObject du monde (murs, items, monstres...)
     *
     * Stratégie :
     * 1) On applique le mouvement.
     * 2) On teste les collisions.
     * 3) En cas de collision : on "clamp" le personnage au bord de l'objet (sans revenir à l'ancienne position).
     * 4) On appelle onCollideWith(go) pour le comportement spécifique (joueur vs item/monster, monster vs mur, etc.).
     */
    public void move(double dx, double dy, double rotation, List<GameObject> gos) {


        // 1) Appliquer la translation + rotation
        setX(getX() + dx);
        setY(getY() + dy);
        getToken().setRotate(rotation);

        double demiW = demiLargeurCollision();
        double demiH = demiHauteurCollision();

        // 2) Vérifier collisions + 3) Repositionner au bord + 4) Appeler le comportement spécifique
        for (GameObject go : gos) {
            if (go == this) continue;        // on ne se collisionne pas avec soi-même
            if (!go.isEnable()) continue;    // un objet désactivé ne bloque pas

            if (collideLeft(go)) {
                setX(go.getXRight() + demiW);   // se place à droite du bord droit de go
                onCollideWith(go);
            } else if (collideRight(go)) {
                setX(go.getXLeft() - demiW);    // se place à gauche du bord gauche de go
                onCollideWith(go);
            } else if (collideTop(go)) {
                setY(go.getYBottom() + demiH);  // se place sous le bord bas de go
                onCollideWith(go);
            } else if (collideBottom(go)) {
                setY(go.getYTop() - demiH);     // se place au-dessus du bord haut de go
                onCollideWith(go);
            }
        }
    }

    // =========================
    // Tests de collision "par côté"
    // =========================
    // go = "l'autre objet" (mur, item, monstre...)
    public boolean collideWith(GameObject go) {
        return collideBottom(go) || collideLeft(go) || collideRight(go) || collideTop(go);
    }

    public boolean collideLeft(GameObject elem) {
        if (!elem.isEnable()) return false;
        double xGauchePerso = getXLeft();
        return getY() >= elem.getYTop()
                && getY() <= elem.getYBottom()
                && xGauchePerso <= elem.getXRight()
                && xGauchePerso >= elem.getXLeft();
    }

    public boolean collideRight(GameObject elem) {
        if (!elem.isEnable()) return false;
        return getY() >= elem.getYTop()
                && getY() <= elem.getYBottom()
                && getXRight() >= elem.getXLeft()
                && getXRight() <= elem.getXRight();
    }

    public boolean collideTop(GameObject elem) {
        if (!elem.isEnable()) return false;
        return getX() >= elem.getXLeft()
                && getX() <= elem.getXRight()
                && getYTop() <= elem.getYBottom()
                && getYTop() >= elem.getYTop();
    }

    public boolean collideBottom(GameObject elem) {
        if (!elem.isEnable()) return false;
        return getX() >= elem.getXLeft()
                && getX() <= elem.getXRight()
                && getYBottom() >= elem.getYTop()
                && getYBottom() <= elem.getYBottom();
    }

    // =========================
    // Contrat POO
    // =========================
    // Chaque type de personnage définit son comportement en cas de collision.
    public abstract void onCollideWith(GameObject go);
}
