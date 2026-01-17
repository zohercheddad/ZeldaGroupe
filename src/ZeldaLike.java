// Version modifiée avec bandeau GAME OVER en dégradé
// --- Main.Main.java ---

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

//TODO: Attention, tu as deux fois ta classe main dans le même fichier

public class ZeldaLike extends Application {

    private static final int LARGEUR = 1200;
    private static final int HAUTEUR = 900;

    private Pane monde;
    private AnimationTimer animationTimer;
    private Joueur joueur;
    private ArrayList<GameObject> gos = new ArrayList<>();

    // HUD
    private Label hudPoints;
    private Label hudVie;
    private Rectangle gameOverBanner;
    private Label gameOverText;

    @Override
    public void start(Stage primaryStage) {

        // ----- Monde -----
        monde = new Pane();
        monde.setPrefSize(LARGEUR, HAUTEUR);
        monde.setStyle("-fx-background-color: #8B7355;");

        // ----- Création du joueur -----
        joueur = new Joueur(100,100);
        gos.add(joueur);

        // ----- Création des monstres -----
        Monster[] monsters = new Monster[]{
                new Monster(200,400),
                new Monster(250,400),
                new Monster(300,400),
                new Monster(900,800),
                new Monster(100,600),//rajouter un montre

        };
        for (Monster m : monsters) gos.add(m);

       // ----- Création des items -----
        Item[] items = new Item[]{
                new Coin(200,800), new Coin(400,200), new Coin(500,500),
                new Coin(600,740), new Coin(700,500), new Coin(800,100),
                new Coin(900,100), new Coin(800,400), new Coin(800,300),
                new Ruby(350,100), new Ruby(850,800)
        };
        for (Item i : items) gos.add(i);

        // ----- Création des murs -----
        Mur[] murs = new Mur[]{
                new Mur(LARGEUR/2, 20, LARGEUR, 40),
                new Mur(LARGEUR/2, HAUTEUR - 20, LARGEUR, 40),
                new Mur(20, HAUTEUR/2, 40, HAUTEUR),
                new Mur(LARGEUR - 20, HAUTEUR/2, 40, HAUTEUR),
                new Mur(420, 580, 40, 560),
                new Mur(590, 320, 300, 40),
                new Mur(1010, 320, 300, 40),
                new Mur(860, 450, 600, 40),
                new Mur(740, 580, 600, 40),
                new Mur(860, 675, 40, 150)
        };
        for (Mur m : murs) gos.add(m);

        // ----- Ajout de tous les objets -----
        for (GameObject g : gos) g.addToPane(monde);

        // ============================================================
        //                         HUD PROPRE
        // ============================================================

        // Fond HUD (transparent)
        Rectangle hudBackground = new Rectangle(LARGEUR, 40);
        hudBackground.setFill(Color.rgb(0,0,0,0.0));

        // Points
        hudPoints = new Label("Points : 0");
        hudPoints.setFont(new Font(20));
        hudPoints.setTextFill(Color.WHITE);
        hudPoints.setLayoutX(50);
        hudPoints.setLayoutY(10);

        // Vie
        hudVie = new Label("Vie : 10");
        hudVie.setFont(new Font(20));
        hudVie.setTextFill(Color.RED);
        hudVie.setLayoutX(250);
        hudVie.setLayoutY(10);

        // ============================================================
        //                BANDEAU GAME OVER AVEC DÉGRADÉ
        // ============================================================

        gameOverBanner = new Rectangle(LARGEUR, 120);
        gameOverBanner.setLayoutY(HAUTEUR/2 - 60);
        gameOverBanner.setVisible(false);

        // Dégradé horizontal (transparent -> rouge -> transparent)
        Stop[] stops = new Stop[]{
                new Stop(0, Color.TRANSPARENT),
                new Stop(0.5, Color.rgb(150,0,0,0.85)),
                new Stop(1, Color.TRANSPARENT)
        };
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops
        );
        gameOverBanner.setFill(gradient);

        gameOverText = new Label("GAME OVER");
        gameOverText.setFont(new Font(70));
        gameOverText.setTextFill(Color.WHITE);
        gameOverText.setVisible(false);
        gameOverText.setLayoutX(LARGEUR/2 - 220);
        gameOverText.setLayoutY(HAUTEUR/2 - 40);

        monde.getChildren().addAll(hudBackground, hudPoints, hudVie, gameOverBanner, gameOverText);

        // ============================================================
        //                    GESTION DU CLAVIER
        // ============================================================
        Scene scene = new Scene(monde, LARGEUR, HAUTEUR);

        scene.setOnKeyPressed(e -> {

            // ===== REBOOT SUR ESC =====
            if (e.getCode() == KeyCode.ESCAPE) {
                resetGame();   // méthode qu’on va écrire juste après
                return;
            }

            if (joueur.getLive() <= 0) return;

            double dx = 0, dy = 0;
            double rotation = joueur.getRotation();

            // ===== DÉPLACEMENT =====
            if (e.getCode() == KeyCode.Z || e.getCode() == KeyCode.UP) { dy -= joueur.getVitesse(); rotation = 0; }
            else if (e.getCode() == KeyCode.S || e.getCode() == KeyCode.DOWN) { dy += joueur.getVitesse(); rotation = 180; }
            else if (e.getCode() == KeyCode.Q || e.getCode() == KeyCode.LEFT) { dx -= joueur.getVitesse(); rotation = 270; }
            else if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT) { dx += joueur.getVitesse(); rotation = 90; }

            joueur.move(dx, dy, rotation, gos);

            // ===== ZOOM GLOBAL =====
            if (e.getCode() == KeyCode.PLUS) {
                ScaleManager.zoomIn();
            }
            else if (e.getCode() == KeyCode.MINUS) {
                ScaleManager.zoomOut();
            }

            monde.setScaleX(ScaleManager.getScale());
            monde.setScaleY(ScaleManager.getScale());
        });

        // ============================================================
        //                     ANIMATION PRINCIPALE
        // ============================================================

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {

                for (Monster m : monsters) m.resolveMove(gos);

                hudPoints.setText("Points : " + joueur.getPoints());
                hudVie.setText("Vie : " + joueur.getLive());

                if (joueur.getLive() <= 0) {
                    gameOverBanner.setVisible(true);
                    gameOverText.setVisible(true);
                    stop();
                }
            }
        };

        animationTimer.start();

        primaryStage.setTitle("🗡️ Zelda-like - Vue du dessus");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // ============================================================
    //                   RESET LA PARTIE
    // ============================================================

    private void resetGame() {
        // STOP l’animation actuelle
        animationTimer.stop();

        // Nettoie le monde
        gos.clear();
        monde.getChildren().clear();

        // Relance le jeu
        try {
            start((Stage) monde.getScene().getWindow());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}