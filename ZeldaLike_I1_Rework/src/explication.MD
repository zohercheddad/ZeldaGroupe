1.0) “GameObject doit demander … de définir createToken(x, y)”

GameObject est abstraite : elle sert de modèle commun.
Elle impose une méthode abstraite : public abstract void createToken(double x, double y);

toute classe qui fait extends GameObject (Mur, Joueur, Monster, Coin…) est obligée d’écrire sa propre version de createToken.

Pourquoi ?
Parce que GameObject ne sait pas à quoi ressemble un mur, un joueur ou une pièce.
Le moteur impose “tu dois fournir le visuel”, mais chaque objet le fait à sa manière.

1.1) “Le constructeur reçoit x, y, width, height … les assigner … et appeler createToken”

Quand tu construis un objet (ex : un Mur), tu passes ses paramètres :
x, y : sa position initiale dans le monde
width, height : sa taille logique.

Le constructeur de GameObject doit donc :
stocker ces valeurs dans les attributs de l’objet courant (this.x, this.y, this.width, this.height)
puis appeler createToken(x, y) pour que l’objet construise immédiatement son “sprite” (Group token) au bon endroit.
L’idée moteur derrière :
dès qu’un GameObject existe, il a ses données (position/taille) + son visuel (token)
aucune sous-classe ne peut “oublier” de créer son token, car c’est imposé par la classe mère.

GameObject : ce que demande la consigne

GameObject est une classe abstraite qui représente n’importe quel élément du jeu (mur, joueur, monstre, pièce, etc.).

1) Méthode abstraite imposée

GameObject doit obliger toutes les classes filles (extends GameObject) à définir la méthode :

createToken(double x, double y)


Cette méthode sert à créer le visuel JavaFX de l’objet (le Group token).

2) Constructeur obligatoire

Le constructeur de GameObject reçoit :x, y : position initiale
width, height : taille logique

Il doit : assigner ces paramètres aux attributs de l’objet appeler createToken(x, y) pour construire le token dès la création.

Exemple :

    public GameObject(double x, double y, double width, double height) {
        this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         createToken(x, y);
    }





















Attention : createToken doit ensuite remplir token (sinon il reste null)

onCollideWith(GameObject go) est une méthode abstraite de Character qui définit un contrat 
chaque personnage (joueur ou monstre) doit implémenter son propre comportement lors d’une collision.
Dans Character, on ajoute une méthode abstraite :

public abstract void onCollideWith(GameObject go);

Pourquoi abstract ?

Parce que Character ne sait pas quoi faire concrètement lors d’une collision.

Exemple :

un Joueur perd de la vie quand il touche un monstre

un Monster rebondit sur un mur

un Item disparaît quand le joueur le touche

Même événement (collision), comportement différent selon la classe.

Effet concret de ce contrat
À partir du moment où cette ligne existe dans Character :
public abstract void onCollideWith(GameObject go);
Alors :
toute classe qui fait extends Character
doit écrire :
    
    public class ClasseQuiHeriteCharacter extends Character {
    @Override
    public void onCollideWith(GameObject go) {
    // comportement spécifique
        }
    }
Sinon → erreur de compilation.
Exemple mental (sans tout coder)
Joueur.onCollideWith(go) → perdre des points, ramasser un item, mourir
Monster.onCollideWith(go) → changer de direction, attaquer, disparaître
Character dit seulement :“Une collision arrive → tu dois savoir quoi faire.”

### **Création de la classe abstraite Character**

On met GameObject go en argument parce qu’une collision se fait toujours entre deux objets :
this = le personnage courant (le Character qui appelle la méthode)
go = l’autre objet potentiel de collision (mur, monstre, item, etc.)

Exemple :

    if (joueur.collideWith(mur)) { ... }

 Ici :
 this = joueur
 go = mur

Sans cet argument, la méthode ne saurait pas avec quoi tester la collision,
ni quel objet passer à onCollideWith(go) pour appliquer le bon comportement.
public void move(double dx, double dy, double rotation, List<GameObject> gos)
public : accessible depuis Main (ex : joueur.move(...)).

void : la méthode ne retourne rien ; elle modifie l’état du personnage (position/rotation).

move : “déplacer le personnage”.

Paramètres
double dx : déplacement sur l’axe X (ex : +3 → droite, -3 → gauche).

double dy : déplacement sur l’axe Y (ex : +3 → bas, -3 → haut).

double rotation : orientation du personnage en degrés (ex : 0, 90, 180, 270).

List<GameObject> gos : la liste de tous les objets du monde à tester pour les collisions (murs, monstres, items…).
On en a besoin pour vérifier “est-ce que le mouvement provoque une collision avec un objet ?”.

En pratique : move applique (dx,dy), puis parcourt gos pour voir si this (le personnage) entre en collision avec un GameObject. Si oui, on annule et on appelle onCollideWith(go).

### **Classe Joueur**

Le Joueur hérite de Character. 
Il gère : les points de vie (live = 10 par défaut), le score (points),
le ramassage d’items et la réaction aux collisions.

### createToken(x, y)

Construit le visuel JavaFX du joueur (ellipse + tête + main + épée), crée un Group, puis :
positionne le token (layoutX/layoutY),
initialise l’attribut token via setToken(...).

##### drop(Item i)

Appelée quand le joueur percute un item :
désactive l’item (i.disable()),
ajoute la valeur de l’item au score (points += i.getValue()).

##### reciveDamages(int x)

Applique des dégâts :retire x points de vie sans jamais descendre sous 0,
fait reculer le joueur de 10 pixels dans la direction opposée à sa rotation.

##### onCollideWith(GameObject go)

Comportement spécifique lors d’une collision :
si go est un Item → drop((Item) go),
sinon → repositionAbout(go) (recalage au bord).

##### repositionAbout(GameObject go)

Repositionne le joueur au bord de l’objet (logique du cours) :
ajuste la largeur/hauteur de collision selon la rotation (0/180 vs 90/270),

en fonction du côté touché (left/right/top/bottom),
recale x ou y pour sortir l’objet de la collision.

### Classe `Monster`

Le monstre est un personnage mobile qui :
- avance automatiquement dans la direction de sa rotation ;
- change de direction toutes les 150 frames ;
- rebondit sur les murs/obstacles ;
- inflige 2 points de dégâts au joueur lors



