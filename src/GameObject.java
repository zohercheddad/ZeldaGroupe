import javafx.scene.Group;       // Conteneur JavaFX : regroupe plusieurs formes (Rectangle, Circle, etc.)
import javafx.scene.layout.Pane; // Monde JavaFX (on y ajoute les tokens)

// Classe abstraite : base commune de tous les objets du jeu (Mur, Joueur, Monster, Item, ...)
public abstract class GameObject {

    // =========================
    // Attributs
    // =========================
    private double width;
    private double height;

    // Représentation visuelle (JavaFX). Doit être initialisée dans createToken(...)
    private Group token;

    // Actif par défaut : visible + pris en compte pour les collisions
    private boolean enable = true;

    // =========================
    // Constructeur
    // =========================
    // Reçoit position + dimensions, puis force la création du token via createToken(x,y)
    public GameObject(double x, double y, double width, double height) {
        this.width = width;
        this.height = height;
        createToken(x, y); // contrat : la classe fille doit y construire et affecter token
    }

    // =========================
    // Accesseurs / Mutateurs
    // =========================
    public double getWidth()  { return width; }
    public double getHeight() { return height; }

    // Position : pas d'attribut x/y -> on récupère la position via token.layoutX/layoutY
    // Opérateur ternaire : condition ? valeur_si_vrai : valeur_si_faux
    public double getX() { return (token == null) ? 0 : token.getLayoutX(); }
    public double getY() { return (token == null) ? 0 : token.getLayoutY(); }

    // Sécurité : évite NullPointerException si setX/setY est appelé trop tôt
    public void setX(double x) { if (token != null) token.setLayoutX(x); }
    public void setY(double y) { if (token != null) token.setLayoutY(y); }

    public boolean isEnable() { return enable; }

    // Désactive / active l'objet : on synchronise la logique et l'affichage (visible/invisible)
    public void setEnable(boolean enable) {
        this.enable = enable;
        if (token != null) token.setVisible(enable);
    }

    public Group getToken() { return token; }

    // Accessible aux classes filles uniquement (Mur/Joueur/Monster/Item) pour initialiser token
    protected void setToken(Group token) {
        this.token = token;
    }

    // =========================
    // Méthodes "moteur" : bornes de collision (AABB)
    // =========================
    // Convention : (x,y) est le centre logique -> bornes = centre +/- largeur/2, hauteur/2
    public double getXLeft()   { return getX() - width / 2.0; }
    public double getXRight()  { return getX() + width / 2.0; }
    public double getYTop()    { return getY() - height / 2.0; }
    public double getYBottom() { return getY() + height / 2.0; }

    // =========================
    // Méthode utilitaire : ajout au monde
    // =========================
    public void addToPane(Pane pane) {
        if (token == null) {
            // Fail fast : erreur claire si une classe fille oublie d'initialiser le token
            throw new IllegalStateException("token est null : createToken doit initialiser token");
        }
        pane.getChildren().add(token);
    }

    // =========================
    // Contrat POO
    // =========================
    // Oblige chaque classe fille à définir sa représentation visuelle
    public abstract void createToken(double x, double y);
}
