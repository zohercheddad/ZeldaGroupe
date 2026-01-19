# 📘 Explication détaillée de la classe `GameObject`

## 🎯 Vue d'ensemble

La classe **`GameObject`** est la **pierre angulaire** de notre architecture orientée objet. C'est une **classe abstraite** qui représente **tout élément présent sur la carte** du jeu : murs, joueur, monstres, pièces, rubis, etc.

### Pourquoi une classe abstraite ?

Une classe abstraite est comme un **contrat** ou un **modèle** que toutes les classes enfants doivent respecter. Elle :
- ✅ **Mutualise** le code commun (position, dimensions, collision)
- ✅ **Impose** des règles aux classes filles (obligation d'implémenter `createToken`)
- ✅ **Structure** le projet de manière professionnelle

> 💡 **Analogie** : GameObject est comme le "plan architectural" d'une maison. Chaque maison (Joueur, Monstre, Mur) suivra ce plan de base, mais personnalisera sa décoration (son apparence visuelle).

---

## 🏗️ Architecture de la classe

```
GameObject (abstraite)
    ├── Attributs communs (position, dimensions, visibilité)
    ├── Méthodes concrètes (getters, setters, collisions)
    └── Méthode abstraite (createToken) → à implémenter par les enfants
```

---

## 📦 Les Attributs

### 1️⃣ `private double width` et `private double height`

**Rôle** : Stockent les **dimensions logiques** de l'objet.

```java
private double width;   // Largeur de l'objet
private double height;  // Hauteur de l'objet
```

**Pourquoi "logiques" ?**
- Ces dimensions servent aux **calculs de collision**
- Elles peuvent différer de la taille visuelle réelle du sprite
- Exemple : Un joueur peut avoir un sprite de 80×20 px, mais une boîte de collision légèrement plus petite

**Encapsulation** :
- `private` = invisible de l'extérieur
- Accessibles uniquement via `getWidth()` et `getHeight()`
- Protection contre les modifications accidentelles

---

### 2️⃣ `private Group token`

**Rôle** : C'est la **représentation visuelle JavaFX** de l'objet.

```java
private Group token;
```

#### Qu'est-ce qu'un `Group` ?

Un `Group` est un **conteneur JavaFX** qui peut regrouper plusieurs formes géométriques :
- Rectangles (`Rectangle`)
- Cercles (`Circle`)
- Ellipses (`Ellipse`)
- Polygones (`Polygon`)
- etc.

#### Pourquoi un `Group` et pas une forme simple ?

Certains objets sont **composés de plusieurs formes** :

**Exemple : Le Joueur**
```java
Ellipse corp  = new Ellipse(...);  // Corps
Circle tete   = new Circle(...);   // Tête
Circle main   = new Circle(...);   // Main
Rectangle sword = new Rectangle(...); // Épée

Group g = new Group(corp, tete, main, sword); // Assemblage
```

Le `Group` permet de **manipuler toutes ces formes comme une seule entité** :
- Déplacer le joueur = déplacer le groupe entier
- Tourner le joueur = tourner le groupe entier
- Rendre invisible = cacher toutes les formes d'un coup

#### Position du `token`

La position du `Group` est stockée dans :
- `token.getLayoutX()` → position X
- `token.getLayoutY()` → position Y

**C'est pour ça qu'on n'a PAS d'attributs `x` et `y` dans la classe !**

---

### 3️⃣ `private boolean enable`

**Rôle** : Indique si l'objet est **actif** ou **désactivé**.

```java
private boolean enable = true; // Actif par défaut
```

#### Qu'est-ce que "actif" signifie ?

| État | Visible à l'écran | Peut entrer en collision |
|------|-------------------|-------------------------|
| **Actif** (`true`) | ✅ Oui | ✅ Oui |
| **Désactivé** (`false`) | ❌ Non | ❌ Non |

#### Cas d'usage pratique

Quand le joueur ramasse une pièce :
1. La pièce est **désactivée** (`setEnable(false)`)
2. Elle devient **invisible** à l'écran
3. Le joueur ne peut **plus la ramasser** (pas de collision)

**Synchronisation visuelle** :
```java
public void setEnable(boolean enable) {
    this.enable = enable;              // État logique
    if (token != null) 
        token.setVisible(enable);      // État visuel (JavaFX)
}
```

---

## 🏗️ Le Constructeur

```java
public GameObject(double x, double y, double width, double height) {
    this.width = width;
    this.height = height;
    createToken(x, y); // ⚠️ Appel de la méthode abstraite
}
```

### Étapes d'initialisation

1. **Stockage des dimensions** : `width` et `height` sont sauvegardés
2. **Création du visuel** : Appel de `createToken(x, y)`
    - ⚠️ C'est une méthode **abstraite** → implémentée dans les classes filles
    - Chaque type d'objet définit son propre visuel

### Pourquoi appeler `createToken()` dans le constructeur ?

**Principe de conception : "Fail Fast"**
- Si une classe fille oublie d'implémenter `createToken()`, le compilateur **refuse de compiler**
- Garantit que **tous** les objets ont un visuel dès leur création
- Évite les `NullPointerException` plus tard

---

## 🔧 Accesseurs et Mutateurs

### Position X et Y

#### Pourquoi pas d'attributs `x` et `y` ?

**Réponse** : La position est **déjà stockée dans le `token` JavaFX** !

```java
public double getX() { 
    return (token == null) ? 0 : token.getLayoutX(); 
}

public double getY() { 
    return (token == null) ? 0 : token.getLayoutY(); 
}
```

#### Décryptage de l'opérateur ternaire

**Format** : `condition ? valeur_si_vrai : valeur_si_faux`

**Traduction** :
```java
// Version compacte
return (token == null) ? 0 : token.getLayoutX();

// Équivalent if-else
if (token == null) {
    return 0;
} else {
    return token.getLayoutX();
}
```

**Pourquoi ce test ?**

Protection contre les appels prématurés :
1. Le constructeur appelle `createToken(x, y)`
2. Si une classe fille appelle `getX()` **avant** de faire `setToken()` → `token` est encore `null`
3. Sans le test, on aurait une **`NullPointerException`** 💥
4. Avec le test, on retourne simplement `0` (valeur par défaut sécurisée)

---

### Setters de position

```java
public void setX(double x) { 
    if (token != null) token.setLayoutX(x); 
}

public void setY(double y) { 
    if (token != null) token.setLayoutY(y); 
}
```

**Protection similaire** :
- Si `token` n'existe pas encore, on ne fait rien (pas de crash)
- Sinon, on met à jour la position dans JavaFX

---

### Getter/Setter du token

```java
public Group getToken() { 
    return token; 
}

protected void setToken(Group token) {
    this.token = token;
}
```

**⚠️ Notez le `protected` sur `setToken` !**

| Modificateur | Qui peut appeler ? |
|--------------|-------------------|
| `public` | Tout le monde |
| `protected` | Seulement les classes filles (Joueur, Monster, etc.) |
| `private` | Seulement la classe elle-même |

**Pourquoi `protected` ?**
- Seules les classes filles doivent initialiser leur propre `token`
- Le reste du programme n'a **pas besoin** de modifier le token
- **Principe d'encapsulation** : on limite l'accès au strict nécessaire

---

## 🎯 Méthodes de collision (AABB)

### Qu'est-ce qu'une AABB ?

**AABB** = **Axis-Aligned Bounding Box** (Boîte de collision alignée sur les axes)

C'est un **rectangle invisible** qui entoure l'objet et sert à détecter les collisions.

```
        getYTop()
            ↓
    ┌───────────────┐
    │               │
    │   (x, y)      │ ← Centre de l'objet
    │      •        │
    │               │
    └───────────────┘
            ↑
      getYBottom()

getXLeft()        getXRight()
```

### Les 4 bornes

```java
public double getXLeft()   { return getX() - width / 2.0; }
public double getXRight()  { return getX() + width / 2.0; }
public double getYTop()    { return getY() - height / 2.0; }
public double getYBottom() { return getY() + height / 2.0; }
```

#### Convention importante : **Le centre comme référence**

Dans notre jeu, `(x, y)` représente le **centre** de l'objet, pas le coin supérieur gauche !

**Exemple concret** :

```java
Joueur joueur = new Joueur(100, 100); // Centre en (100, 100)
// width = 80, height = 20

joueur.getX();        // → 100 (centre X)
joueur.getY();        // → 100 (centre Y)

joueur.getXLeft();    // → 100 - 80/2 = 60
joueur.getXRight();   // → 100 + 80/2 = 140
joueur.getYTop();     // → 100 - 20/2 = 90
joueur.getYBottom();  // → 100 + 20/2 = 110
```

**Pourquoi diviser par 2.0 et pas 2 ?**
- `2.0` est un **double** (nombre à virgule)
- Force Java à faire une **division décimale** au lieu d'une division entière
- Évite les erreurs d'arrondi

---

## 🌍 Ajout au monde JavaFX

```java
public void addToPane(Pane pane) {
    if (token == null) {
        throw new IllegalStateException(
            "token est null : createToken doit initialiser token"
        );
    }
    pane.getChildren().add(token);
}
```

### Décryptage

1. **Vérification de sécurité** :
    - Si `token` est `null` → **erreur immédiate** avec message clair
    - Principe **"Fail Fast"** : mieux vaut un crash explicite qu'un bug silencieux

2. **Ajout au `Pane`** :
    - `pane.getChildren()` = liste des éléments visuels du monde
    - `.add(token)` = ajoute notre objet à cette liste
    - L'objet devient **visible** à l'écran

### Utilisation dans `Main.java`

```java
for (GameObject g : gos) {
    g.addToPane(monde); // Ajoute chaque objet au monde
}
```

---

## 🎨 La méthode abstraite `createToken()`

```java
public abstract void createToken(double x, double y);
```

### Qu'est-ce qu'une méthode abstraite ?

**Définition** : Une méthode **sans corps** (sans implémentation) qui **oblige** les classes filles à la définir.

**Analogie** : C'est comme un formulaire vierge que chaque enfant doit remplir à sa manière.

### Pourquoi abstraite ?

Chaque type d'objet a une **apparence différente** :

- Un **Mur** = simple rectangle marron
- Un **Joueur** = corps + tête + épée
- Un **Monstre** = corps + yeux + queue
- Une **Pièce** = cercle doré

**GameObject ne peut pas savoir** comment dessiner ces objets → il délègue cette responsabilité aux classes filles.

### Contrat POO

En déclarant `createToken()` comme abstraite, GameObject dit :

> "Je ne sais pas comment tu vas te dessiner, mais **tu DOIS te dessiner** ! Débrouille-toi, mais respecte la signature : `createToken(double x, double y)`"

---

## 🧩 Exemple d'utilisation complète

### 1️⃣ Création d'un objet (dans `Main.java`)

```java
Joueur joueur = new Joueur(100, 100);
```

**Ce qui se passe en coulisses** :

```
1. Constructeur de Joueur appelé
   ↓
2. Appel du constructeur de Character (parent)
   ↓
3. Appel du constructeur de GameObject (grand-parent)
   ↓
4. GameObject stocke width=80, height=20
   ↓
5. GameObject appelle createToken(100, 100)
   ↓
6. createToken du JOUEUR s'exécute (polymorphisme!)
   ↓
7. Le joueur construit son Group (corps, tête, épée)
   ↓
8. Le joueur fait setToken(group)
   ↓
9. token est maintenant initialisé ✅
```

### 2️⃣ Ajout au monde

```java
joueur.addToPane(monde);
```

**Résultat** : Le joueur apparaît à l'écran en position (100, 100)

### 3️⃣ Déplacement

```java
joueur.setX(150);
joueur.setY(200);
```

**Résultat** : Le `token` JavaFX est déplacé → le joueur se déplace visuellement

### 4️⃣ Détection de collision

```java
if (joueur.getXRight() >= mur.getXLeft()) {
    // Le côté droit du joueur touche le côté gauche du mur !
}
```

---

## 🎓 Concepts POO illustrés

### 1. **Encapsulation**

Tous les attributs sont `private` → on contrôle l'accès via des méthodes

**Avantages** :
- ✅ Impossible de mettre `width = -50` (on peut valider dans le setter)
- ✅ Synchronisation automatique entre logique et affichage
- ✅ Modification du fonctionnement interne sans casser le code externe

### 2. **Abstraction**

GameObject cache la complexité JavaFX aux classes utilisatrices.

**L'utilisateur manipule** :
```java
joueur.setX(100); // Simple et clair
```

**Ce qui se passe vraiment** :
```java
token.setLayoutX(100); // Détail d'implémentation caché
```

### 3. **Polymorphisme**

Grâce à `createToken()` abstraite, chaque classe dessine son propre visuel :

```java
GameObject obj1 = new Joueur(100, 100);
GameObject obj2 = new Monster(200, 200);
GameObject obj3 = new Mur(300, 300, 40, 100);

// Même appel, comportements différents !
obj1.createToken(x, y); // Dessine un joueur
obj2.createToken(x, y); // Dessine un monstre
obj3.createToken(x, y); // Dessine un mur
```

### 4. **Principe DRY** (Don't Repeat Yourself)

Au lieu de dupliquer le code de collision dans Joueur, Monster, Item, etc., on le centralise dans GameObject.

**1 seule implémentation → 1 seul endroit à corriger en cas de bug !**

---

## 🚀 Points clés à retenir

| Concept | Explication |
|---------|-------------|
| **Classe abstraite** | Modèle de base pour tous les objets du jeu |
| **token (Group)** | Représentation visuelle JavaFX, peut contenir plusieurs formes |
| **enable** | Active/désactive l'objet (visuel + collision) |
| **AABB** | Boîte de collision rectangulaire alignée sur les axes |
| **createToken()** | Méthode abstraite = obligation pour les enfants de définir leur visuel |
| **Pas d'attributs x/y** | Position stockée directement dans le token JavaFX |
| **protected setToken()** | Accessible seulement aux classes filles |
| **Fail Fast** | Erreurs immédiates et claires plutôt que bugs silencieux |

---

## 💬 Questions fréquentes

### ❓ Pourquoi pas de méthode `update()` ?

GameObject est purement **statique** (position, taille, visuel). Le comportement dynamique (déplacement, IA) est géré dans les classes **Character** et **Monster**.

### ❓ Pourquoi `token` n'est pas `final` ?

Il est initialisé dans `createToken()`, appelée depuis le constructeur, mais **après** `super()`. Java n'autorise pas `final` dans ce cas.

### ❓ Peut-on changer `width` et `height` après création ?

Non, il n'y a **pas de setters** pour ces attributs. Les dimensions sont définies à la création et ne changent jamais. C'est un **choix de conception** : la taille d'un objet est considérée comme immuable.

### ❓ Pourquoi `getXLeft()` n'est pas `private` ?

Elle doit être **accessible de l'extérieur** pour les calculs de collision entre différents objets. Par exemple, dans `Character`, on appelle `go.getXLeft()` pour tester si on touche l'autre objet.

---

# 📘 Explication détaillée de la classe `Character`

## 🎯 Vue d'ensemble

La classe **`Character`** représente **tout personnage mobile** du jeu : le joueur et les monstres. C'est une **classe abstraite** qui hérite de `GameObject` et ajoute les fonctionnalités de **déplacement** et de **gestion des collisions intelligente**.

### Hiérarchie d'héritage

```
GameObject (abstraite)
    ↓
Character (abstraite)
    ↓
    ├── Joueur (concrète)
    └── Monster (concrète)
```

### Pourquoi une classe Character ?

Au lieu de dupliquer le code de déplacement dans `Joueur` ET `Monster`, on le **mutualise** dans `Character`. Principe DRY : **Don't Repeat Yourself** !

> 💡 **Analogie** : GameObject est le "plan architectural", Character ajoute les "roues et le moteur", et Joueur/Monster personnalisent le "pilotage".

---

## 📦 L'attribut vitesse

```java
private int vitesse;
```

### Rôle

C'est la **vitesse de déplacement** du personnage, exprimée en **pixels par frame**.

**Exemples concrets** :
- `vitesse = 3` → Le personnage se déplace de 3 pixels à chaque touche appuyée
- `vitesse = 5` → Déplacement plus rapide (5 pixels/frame)
- `vitesse = 1` → Déplacement lent (1 pixel/frame)

### Pourquoi `int` et pas `double` ?

Les déplacements en pixels sont généralement **entiers** pour éviter :
- ❌ Des positions flottantes type `x = 100.73849` (antialiasing bizarre)
- ❌ Des arrondis qui s'accumulent au fil du temps

### Validation dans le setter

```java
public void setVitesse(int vitesse) {
    if (vitesse < 0) {
        throw new IllegalArgumentException("vitesse doit être >= 0");
    }
    this.vitesse = vitesse;
}
```

**Protection** : Impossible de mettre une vitesse négative (qui ferait reculer le personnage de manière imprévisible).

> ⚠️ **Note** : Une vitesse de 0 est autorisée (personnage immobile/paralysé).

---

## 🔄 Le système rotation-aware

### Le problème

Imaginez un joueur de dimensions **80×20** (largeur × hauteur) :

```
Rotation 0° (Nord)     Rotation 90° (Est)
┌────────────┐         ┌──┐
│            │         │  │
│     👤     │         │👤│
│            │         │  │
└────────────┘         │  │
  80px × 20px          │  │
                       └──┘
                      20px × 80px
```

**Quand le personnage tourne à 90° ou 270°, ses dimensions visuelles s'inversent !**

### La solution : demi-largeur et demi-hauteur dynamiques

```java
private double demiLargeurCollision() {
    double r = getToken().getRotate();
    return (r == 0 || r == 180) ? getWidth() / 2.0 : getHeight() / 2.0;
}

private double demiHauteurCollision() {
    double r = getToken().getRotate();
    return (r == 0 || r == 180) ? getHeight() / 2.0 : getWidth() / 2.0;
}
```

#### Décryptage avec un exemple

**Joueur** : `width = 80`, `height = 20`

| Rotation | Orientation | `demiLargeurCollision()` | `demiHauteurCollision()` |
|----------|-------------|-------------------------|-------------------------|
| **0°**   | Nord ↑      | `80 / 2 = 40`           | `20 / 2 = 10`           |
| **90°**  | Est →       | `20 / 2 = 10` ⚠️        | `80 / 2 = 40` ⚠️        |
| **180°** | Sud ↓       | `80 / 2 = 40`           | `20 / 2 = 10`           |
| **270°** | Ouest ←     | `20 / 2 = 10` ⚠️        | `80 / 2 = 40` ⚠️        |

**Remarquez l'inversion** aux rotations 90° et 270° !

### Pourquoi `private` ?

Ces méthodes sont des **détails d'implémentation**. Seule la classe `Character` en a besoin pour calculer ses bornes. Personne d'autre ne doit les appeler → `private`.

---

## 🎯 Surcharge des bornes AABB

```java
@Override 
public double getXLeft()   { return getX() - demiLargeurCollision(); }

@Override 
public double getXRight()  { return getX() + demiLargeurCollision(); }

@Override 
public double getYTop()    { return getY() - demiHauteurCollision(); }

@Override 
public double getYBottom() { return getY() + demiHauteurCollision(); }
```

### Qu'est-ce que `@Override` ?

C'est une **annotation** qui indique qu'on **redéfinit** (surcharge) une méthode de la classe parente (`GameObject`).

**Avantages** :
- ✅ Le compilateur vérifie qu'on surcharge bien une méthode existante
- ✅ Protection contre les fautes de frappe (`getXLefT()` → erreur de compilation)
- ✅ Clarté du code : on voit immédiatement qu'il y a héritage

### Pourquoi redéfinir ces méthodes ?

**GameObject** calcule les bornes avec `width` et `height` **fixes** :
```java
// Dans GameObject
public double getXLeft() { return getX() - width / 2.0; }
```

**Character** doit tenir compte de la **rotation** :
```java
// Dans Character
@Override
public double getXLeft() { return getX() - demiLargeurCollision(); }
//                                          ^^^^^^^^^^^^^^^^^^^^^^
//                                          Ajusté selon rotation !
```

### Exemple visuel

**Joueur à (100, 100), width=80, height=20**

#### Rotation 0° (Nord)
```
getXLeft()  = 100 - 40 = 60
getXRight() = 100 + 40 = 140
getYTop()   = 100 - 10 = 90
getYBottom()= 100 + 10 = 110

        90
        ↓
   ┌─────────┐
60 │    •    │ 140
   └─────────┘
        ↑
       110
```

#### Rotation 90° (Est)
```
getXLeft()  = 100 - 10 = 90  ⚠️ Inversé !
getXRight() = 100 + 10 = 110
getYTop()   = 100 - 40 = 60  ⚠️ Inversé !
getYBottom()= 100 + 40 = 140

      60
      ↓
    ┌───┐
90  │ • │ 110
    │   │
    └───┘
      ↑
     140
```

**Sans cette surcharge, les collisions seraient fausses quand le personnage tourne !**

---

## 🚀 La méthode centrale : `move()`

### Signature

```java
public void move(double dx, double dy, double rotation, List<GameObject> gos)
```

#### Paramètres

| Paramètre | Type | Signification | Exemples |
|-----------|------|---------------|----------|
| `dx` | `double` | Déplacement horizontal | `+3` (droite), `-3` (gauche), `0` (immobile) |
| `dy` | `double` | Déplacement vertical | `+3` (bas), `-3` (haut), `0` (immobile) |
| `rotation` | `double` | Orientation en degrés | `0` (Nord), `90` (Est), `180` (Sud), `270` (Ouest) |
| `gos` | `List<GameObject>` | Tous les objets du monde | Murs, items, autres personnages |

### Algorithme en 4 étapes

```java
public void move(double dx, double dy, double rotation, List<GameObject> gos) {
    // ═══════════════════════════════════════════════════════════
    // ÉTAPE 1 : APPLIQUER LE MOUVEMENT
    // ═══════════════════════════════════════════════════════════
    setX(getX() + dx);              // Nouvelle position X
    setY(getY() + dy);              // Nouvelle position Y
    getToken().setRotate(rotation); // Nouvelle orientation

    // Calcul des demi-dimensions (ajustées selon rotation)
    double demiW = demiLargeurCollision();
    double demiH = demiHauteurCollision();

    // ═══════════════════════════════════════════════════════════
    // ÉTAPES 2-3-4 : COLLISION + REPOSITIONNEMENT + COMPORTEMENT
    // ═══════════════════════════════════════════════════════════
    for (GameObject go : gos) {
        if (go == this) continue;        // Pas de collision avec soi-même
        if (!go.isEnable()) continue;    // Objets désactivés = pas de collision

        if (collideLeft(go)) {
            setX(go.getXRight() + demiW);  // Repositionner
            onCollideWith(go);              // Comportement spécifique
        } 
        else if (collideRight(go)) {
            setX(go.getXLeft() - demiW);
            onCollideWith(go);
        } 
        else if (collideTop(go)) {
            setY(go.getYBottom() + demiH);
            onCollideWith(go);
        } 
        else if (collideBottom(go)) {
            setY(go.getYTop() - demiH);
            onCollideWith(go);
        }
    }
}
```

---

## 🎬 Exemple d'exécution étape par étape

### Situation initiale

```
Joueur en (100, 100), regarde Nord (rotation=0)
Vitesse = 3
Mur vertical en X=140

   60        140        
    ↓         ↓
┌────────┐ ┏━━━┓
│ Joueur │ ┃Mur┃
│   👤   │ ┃   ┃
└────────┘ ┗━━━┛
    ↑
   100
```

### L'utilisateur appuie sur **→ (Droite)**

```java
joueur.move(3, 0, 90, gos);
//          ↑  ↑  ↑
//          dx dy rotation
```

#### Étape 1 : Application du mouvement

```java
setX(100 + 3);  // X devient 103
setY(100 + 0);  // Y reste 100
getToken().setRotate(90);  // Tourne vers l'Est
```

**Position après mouvement** :
```
   63        140
    ↓         ↓
   ┌────────┐┏━━━┓
   │ Joueur ││Mur┃  ⚠️ COLLISION !
   │   👤→  ││   ┃
   └────────┘┗━━━┛
       ↑
      103
```

#### Étape 2 : Détection de collision

```java
demiW = demiLargeurCollision();  // rotation=90 → height/2 = 20/2 = 10
demiH = demiHauteurCollision();  // rotation=90 → width/2 = 80/2 = 40

// Bornes du joueur (rotation 90°) :
getXLeft()  = 103 - 10 = 93
getXRight() = 103 + 10 = 113  ⚠️ Dépasse le mur !
getYTop()   = 100 - 40 = 60
getYBottom()= 100 + 40 = 140

// Bornes du mur :
mur.getXLeft()  = 140
mur.getXRight() = 160
```

**Test `collideRight(mur)` :**
```java
getY() >= mur.getYTop()      → 100 >= ... → true ✅
getY() <= mur.getYBottom()   → 100 <= ... → true ✅
getXRight() >= mur.getXLeft()→ 113 >= 140 → false ❌
```

Hmm, pas de collision détectée ? 🤔

**Attendez !** En réalité, le joueur **entre dans le mur** progressivement. Au prochain frame, il sera plus à droite et **alors** la collision sera détectée !

#### Étape 3 : Repositionnement (au prochain frame où collision = true)

```java
if (collideRight(mur)) {
    setX(mur.getXLeft() - demiW);  // X = 140 - 10 = 130
    onCollideWith(mur);
}
```

**Position finale** :
```
   120       140
    ↓         ↓
   ┌────────┐┏━━━┓
   │ Joueur │┃Mur┃  ✅ Recalé au bord !
   │   👤→  │┃   ┃
   └────────┘┗━━━┛
       ↑
      130
```

**C'est ce `130` que vous voyez dans la console !** 🎯

Ce n'est **pas une erreur**, c'est juste la **position X finale** du joueur après repositionnement au bord du mur.

#### Étape 4 : Comportement spécifique

```java
// Dans Joueur.onCollideWith()
if (go instanceof Item) {
    drop((Item) go);  // Pas un Item ici
} 
// Rien d'autre à faire, le repositionnement est déjà fait !
```

---

## 🎯 Les méthodes de collision par côté

### Structure commune

Toutes les 4 méthodes ont la **même structure** :

```java
public boolean collideXXX(GameObject elem) {
    if (!elem.isEnable()) return false;  // Sécurité
    
    // Conditions de collision spécifiques au côté
    return condition1
        && condition2
        && condition3
        && condition4;
}
```

### Détail : `collideLeft()`

```java
public boolean collideLeft(GameObject elem) {
    if (!elem.isEnable()) return false;
    
    double xGauchePerso = getXLeft();
    return getY() >= elem.getYTop()           // (1)
        && getY() <= elem.getYBottom()        // (2)
        && xGauchePerso <= elem.getXRight()   // (3)
        && xGauchePerso >= elem.getXLeft();   // (4)
}
```

#### Explication visuelle

**Collision par la GAUCHE** = Le côté gauche du personnage touche l'objet

```
Personnage            Objet
    •──────┐         ┏━━━━┓
    │      │    ⚠️   ┃    ┃
    └──────┘         ┗━━━━┛
    ↑                ↑    ↑
  xGauchePerso   getXLeft getXRight
```

**Conditions** :

1. `getY() >= elem.getYTop()` : Le centre du perso est **au-dessus ou au niveau** du haut de l'objet
2. `getY() <= elem.getYBottom()` : Le centre du perso est **en-dessous ou au niveau** du bas de l'objet
3. `xGauchePerso <= elem.getXRight()` : Le bord gauche du perso est **à gauche ou au niveau** du bord droit de l'objet
4. `xGauchePerso >= elem.getXLeft()` : Le bord gauche du perso est **à droite ou au niveau** du bord gauche de l'objet

**Les 4 conditions doivent être vraies simultanément pour qu'il y ait collision !**

### Schéma récapitulatif des 4 côtés

```
        collideTop()
             ↓
      ╔═════════════╗
      ║             ║
  ←   ║      •      ║   → collideRight()
collideLeft()       ║
      ║             ║
      ╚═════════════╝
             ↑
      collideBottom()
```

### Pourquoi tester `!elem.isEnable()` ?

Un objet **désactivé** (comme une pièce déjà ramassée) ne doit **plus** causer de collision.

**Exemple** :
```java
Coin coin = new Coin(200, 300);
joueur.drop(coin);  // Désactive la pièce

// Maintenant, même si le joueur traverse la position (200, 300),
// collideLeft(coin) retournera FALSE car coin.isEnable() = false
```

---

## 🎨 La méthode abstraite `onCollideWith()`

```java
public abstract void onCollideWith(GameObject go);
```

### Pourquoi abstraite ?

Chaque type de personnage réagit **différemment** aux collisions :

| Personnage | Collision avec Mur | Collision avec Item | Collision avec autre personnage |
|------------|-------------------|---------------------|--------------------------------|
| **Joueur** | Déjà repositionné par `move()` | Ramasse l'item (`drop`) | Subit des dégâts |
| **Monster** | Change de direction | Ignore | Inflige des dégâts si c'est le joueur |

**Character ne peut pas deviner** ces comportements → délégation aux classes filles.

### Implémentations concrètes

#### Dans `Joueur`

```java
@Override
public void onCollideWith(GameObject go) {
    if (go instanceof Item) {
        drop((Item) go);  // Ramasser l'item
    }
    // Pas besoin de repositionner : déjà fait par Character.move() !
}
```

#### Dans `Monster`

```java
@Override
public void onCollideWith(GameObject go) {
    changeDirection();  // Rebondir
    
    if (go instanceof Joueur) {
        ((Joueur) go).reciveDamages(2);  // Attaquer le joueur
    }
}
```

---

## 🧩 Principes POO illustrés

### 1. **Héritage**

Character **hérite** de GameObject et **ajoute** des fonctionnalités :

```
GameObject
├── position (x, y)
├── dimensions (width, height)
├── token (visuel)
└── enable (actif/désactivé)

Character (AJOUTE)
├── vitesse
├── move()
├── collisions rotation-aware
└── onCollideWith() [abstrait]
```

### 2. **Abstraction**

Character est abstraite car :
- ❌ On ne crée **jamais** de `new Character()` (pas de sens)
- ✅ On crée des `new Joueur()` ou `new Monster()`

**Un Character est un concept abstrait, pas un objet concret.**

### 3. **Polymorphisme**

```java
List<Character> personnages = new ArrayList<>();
personnages.add(new Joueur(100, 100));
personnages.add(new Monster(200, 200));

for (Character c : personnages) {
    c.move(3, 0, 90, gos);  // Même appel...
    // Mais onCollideWith() aura un comportement différent !
}
```

### 4. **Encapsulation**

Les méthodes `demiLargeurCollision()` et `demiHauteurCollision()` sont **private** :
- ✅ Détail d'implémentation caché
- ✅ Peut être modifié sans casser le code externe
- ✅ Simplifie l'interface publique de la classe

### 5. **Template Method Pattern**

`move()` est un **modèle** (template) qui définit le **squelette** de l'algorithme :
1. Appliquer le mouvement
2. Détecter les collisions
3. Repositionner
4. Appeler `onCollideWith()` ← **point d'extension** pour les classes filles

---

## 🚨 Pièges courants et solutions

### Piège 1 : Oublier la rotation dans les dimensions

```java
// ❌ FAUX - Dimensions fixes
@Override
public double getXLeft() { return getX() - getWidth() / 2.0; }

// ✅ CORRECT - Dimensions ajustées selon rotation
@Override
public double getXLeft() { return getX() - demiLargeurCollision(); }
```

**Conséquence** : Collisions fausses quand le personnage tourne à 90°/270°.

### Piège 2 : Tester `go == this` après les collisions

```java
// ❌ FAUX - Test trop tard
for (GameObject go : gos) {
    if (collideLeft(go)) {
        if (go == this) continue;  // Trop tard, déjà calculé !
        // ...
    }
}

// ✅ CORRECT - Test au début
for (GameObject go : gos) {
    if (go == this) continue;  // Évite les calculs inutiles
    if (collideLeft(go)) {
        // ...
    }
}
```

### Piège 3 : Oublier de tester `isEnable()`

```java
// ❌ FAUX - Collision avec objets désactivés
if (collideLeft(elem)) {
    // Une pièce ramassée peut encore bloquer !
}

// ✅ CORRECT - Tester isEnable() dans les méthodes de collision
public boolean collideLeft(GameObject elem) {
    if (!elem.isEnable()) return false;
    // ...
}
```

### Piège 4 : Double repositionnement

```java
// ❌ FAUX - Repositionner dans move() ET dans onCollideWith()
// Character.move()
if (collideLeft(go)) {
    setX(go.getXRight() + demiW);  // Repositionnement 1
    onCollideWith(go);
}

// Joueur.onCollideWith()
public void onCollideWith(GameObject go) {
    repositionAbout(go);  // Repositionnement 2 (doublon !)
}

// ✅ CORRECT - Repositionner une seule fois dans move()
// Joueur.onCollideWith()
public void onCollideWith(GameObject go) {
    if (go instanceof Item) {
        drop((Item) go);
    }
    // Pas de repositionnement ici !
}
```

---

## 🎓 Points clés à retenir

| Concept | Explication |
|---------|-------------|
| **Vitesse** | Pixels de déplacement par frame (généralement 1-5) |
| **Rotation-aware** | Les dimensions de collision s'inversent à 90°/270° |
| **demiLargeurCollision()** | Largeur ajustée : width/2 (0°/180°) ou height/2 (90°/270°) |
| **move()** | Applique mouvement → détecte collisions → repositionne → appelle onCollideWith |
| **collideXXX()** | Teste la collision d'un côté spécifique avec 4 conditions |
| **onCollideWith()** | Méthode abstraite pour les comportements spécifiques (ramasser, rebondir, etc.) |
| **Repositionnement** | Fait dans `move()`, pas dans `onCollideWith()` (éviter doublon) |
| **@Override** | Indique qu'on redéfinit une méthode de GameObject |

---

## 💬 Questions fréquentes

### ❓ Pourquoi ne pas annuler le mouvement au lieu de repositionner ?

**Annuler** = remettre à l'ancienne position → effet "téléportation" brutal
**Repositionner** = placer au bord → mouvement fluide, sensation naturelle

### ❓ Pourquoi tester 4 côtés au lieu d'une collision globale ?

Pour **repositionner correctement** ! Si on sait que la collision est par la GAUCHE, on recale à DROITE de l'objet.

### ❓ Que se passe-t-il si le personnage est coincé entre 2 murs ?

La boucle traite les collisions **séquentiellement** :
1. Collision avec mur1 → repositionnement
2. Collision avec mur2 → repositionnement
3. Le personnage finit "coincé" entre les deux (comportement attendu)

### ❓ Pourquoi `double` pour dx/dy mais `int` pour vitesse ?

- `vitesse` (int) = valeur de base stable
- `dx/dy` (double) = permet des calculs plus complexes si besoin (ex: vitesse × 0.5 pour ralentissement)

### ❓ C'est quoi le "130" dans la console ?

C'est la **position X finale** du joueur après repositionnement au bord d'un mur. Ce n'est **pas une erreur**, juste une trace de debug quelque part dans votre code (probablement un `System.out.println()`).

---

## 📚 Liens avec les autres classes

### Character utilise :
- **GameObject** : hérite de position, dimensions, token, bornes AABB
- **List\<GameObject\>** : pour tester les collisions avec tous les objets

### Character est utilisé par :
- **Joueur** : implémente `onCollideWith()` pour ramasser items
- **Monster** : implémente `onCollideWith()` pour rebondir et attaquer

### Prochaines classes à étudier :
1. **Joueur** : Gestion des vies, score, items, dégâts
2. **Monster** : IA simple (déplacement automatique, changement de direction)

---

## 🎉 Conclusion

**Character** est le **cœur du système de déplacement** de votre jeu. En mutualisant la logique de collision et de repositionnement, vous :

✅ **Évitez** la duplication de code
✅ **Facilitez** la maintenance (1 bug = 1 seul endroit à corriger)
✅ **Uniformisez** le comportement de tous les personnages
✅ **Respectez** les principes POO (héritage, abstraction, polymorphisme)

Cette architecture solide vous permet d'ajouter facilement de nouveaux types de personnages (Boss, PNJ, animaux...) sans réécrire la logique de collision !

# 📘 Explication détaillée de la classe `Character`

## 🎯 Vue d'ensemble

La classe **`Character`** représente **tout personnage mobile** du jeu : le joueur et les monstres. C'est une **classe abstraite** qui hérite de `GameObject` et ajoute les fonctionnalités de **déplacement** et de **gestion des collisions intelligente**.

### Hiérarchie d'héritage

```
GameObject (abstraite)
    ↓
Character (abstraite)
    ↓
    ├── Joueur (concrète)
    └── Monster (concrète)
```

### Pourquoi une classe Character ?

Au lieu de dupliquer le code de déplacement dans `Joueur` ET `Monster`, on le **mutualise** dans `Character`. Principe DRY : **Don't Repeat Yourself** !

> 💡 **Analogie** : GameObject est le "plan architectural", Character ajoute les "roues et le moteur", et Joueur/Monster personnalisent le "pilotage".

---

## 📦 L'attribut vitesse

```java
private int vitesse;
```

### Rôle

C'est la **vitesse de déplacement** du personnage, exprimée en **pixels par frame**.

**Exemples concrets** :
- `vitesse = 3` → Le personnage se déplace de 3 pixels à chaque touche appuyée
- `vitesse = 5` → Déplacement plus rapide (5 pixels/frame)
- `vitesse = 1` → Déplacement lent (1 pixel/frame)

### Pourquoi `int` et pas `double` ?

Les déplacements en pixels sont généralement **entiers** pour éviter :
- ❌ Des positions flottantes type `x = 100.73849` (antialiasing bizarre)
- ❌ Des arrondis qui s'accumulent au fil du temps

### Validation dans le setter

```java
public void setVitesse(int vitesse) {
    if (vitesse < 0) {
        throw new IllegalArgumentException("vitesse doit être >= 0");
    }
    this.vitesse = vitesse;
}
```

**Protection** : Impossible de mettre une vitesse négative (qui ferait reculer le personnage de manière imprévisible).

> ⚠️ **Note** : Une vitesse de 0 est autorisée (personnage immobile/paralysé).

---

## 🔄 Le système rotation-aware

### Le problème

Imaginez un joueur de dimensions **80×20** (largeur × hauteur) :

```
Rotation 0° (Nord)     Rotation 90° (Est)
┌────────────┐         ┌──┐
│            │         │  │
│     👤     │         │👤│
│            │         │  │
└────────────┘         │  │
  80px × 20px          │  │
                       └──┘
                      20px × 80px
```

**Quand le personnage tourne à 90° ou 270°, ses dimensions visuelles s'inversent !**

### La solution : demi-largeur et demi-hauteur dynamiques

```java
private double demiLargeurCollision() {
    double r = getToken().getRotate();
    return (r == 0 || r == 180) ? getWidth() / 2.0 : getHeight() / 2.0;
}

private double demiHauteurCollision() {
    double r = getToken().getRotate();
    return (r == 0 || r == 180) ? getHeight() / 2.0 : getWidth() / 2.0;
}
```

#### Décryptage avec un exemple

**Joueur** : `width = 80`, `height = 20`

| Rotation | Orientation | `demiLargeurCollision()` | `demiHauteurCollision()` |
|----------|-------------|-------------------------|-------------------------|
| **0°**   | Nord (haut) | `80 / 2 = 40`           | `20 / 2 = 10`           |
| **90°**  | Est (droite)| `20 / 2 = 10` (inverse) | `80 / 2 = 40` (inverse) |
| **180°** | Sud (bas)   | `80 / 2 = 40`           | `20 / 2 = 10`           |
| **270°** | Ouest (gauche)| `20 / 2 = 10` (inverse)| `80 / 2 = 40` (inverse) |

**Remarquez l'inversion** aux rotations 90° et 270°.

### Pourquoi `private` ?

Ces méthodes sont des **détails d'implémentation**. Seule la classe `Character` en a besoin pour calculer ses bornes. Personne d'autre ne doit les appeler, d'où le modificateur `private`.

---

## 🎯 Surcharge des bornes AABB

```java
@Override 
public double getXLeft()   { return getX() - demiLargeurCollision(); }

@Override 
public double getXRight()  { return getX() + demiLargeurCollision(); }

@Override 
public double getYTop()    { return getY() - demiHauteurCollision(); }

@Override 
public double getYBottom() { return getY() + demiHauteurCollision(); }
```

### Qu'est-ce que `@Override` ?

C'est une **annotation** qui indique qu'on **redéfinit** (surcharge) une méthode de la classe parente (`GameObject`).

### Avantages de @Override

- Le compilateur vérifie qu'on surcharge bien une méthode existante
- Protection contre les fautes de frappe (`getXLefT()` provoquera une erreur de compilation)
- Clarté du code : on voit immédiatement qu'il y a héritage

### Pourquoi redéfinir ces méthodes ?

**GameObject** calcule les bornes avec `width` et `height` **fixes** :
```java
// Dans GameObject
public double getXLeft() { return getX() - width / 2.0; }
```

**Character** doit tenir compte de la **rotation** :
```java
// Dans Character
@Override
public double getXLeft() { return getX() - demiLargeurCollision(); }
//                                          ^^^^^^^^^^^^^^^^^^^^^^
//                                          Ajusté selon rotation !
```

### Exemple visuel

**Joueur à (100, 100), width=80, height=20**

#### Rotation 0° (Nord)
```
getXLeft()  = 100 - 40 = 60
getXRight() = 100 + 40 = 140
getYTop()   = 100 - 10 = 90
getYBottom()= 100 + 10 = 110

        90
        ↓
   ┌─────────┐
60 │    •    │ 140
   └─────────┘
        ↑
       110
```

#### Rotation 90° (Est)
```
getXLeft()  = 100 - 10 = 90  ⚠️ Inversé !
getXRight() = 100 + 10 = 110
getYTop()   = 100 - 40 = 60  ⚠️ Inversé !
getYBottom()= 100 + 40 = 140

      60
      ↓
    ┌───┐
90  │ • │ 110
    │   │
    └───┘
      ↑
     140
```

**Sans cette surcharge, les collisions seraient fausses quand le personnage tourne !**

---

## 🚀 La méthode centrale : `move()`

### Signature

```java
public void move(double dx, double dy, double rotation, List<GameObject> gos)
```

#### Paramètres

| Paramètre | Type | Signification | Exemples |
|-----------|------|---------------|----------|
| `dx` | `double` | Déplacement horizontal | `+3` (droite), `-3` (gauche), `0` (immobile) |
| `dy` | `double` | Déplacement vertical | `+3` (bas), `-3` (haut), `0` (immobile) |
| `rotation` | `double` | Orientation en degrés | `0` (Nord), `90` (Est), `180` (Sud), `270` (Ouest) |
| `gos` | `List<GameObject>` | Tous les objets du monde | Murs, items, autres personnages |

### Algorithme en 4 étapes

```java
public void move(double dx, double dy, double rotation, List<GameObject> gos) {
    // ETAPE 1 : APPLIQUER LE MOUVEMENT
    setX(getX() + dx);              // Nouvelle position X
    setY(getY() + dy);              // Nouvelle position Y
    getToken().setRotate(rotation); // Nouvelle orientation

    // Calcul des demi-dimensions (ajustées selon rotation)
    double demiW = demiLargeurCollision();
    double demiH = demiHauteurCollision();

    // ETAPES 2-3-4 : COLLISION + REPOSITIONNEMENT + COMPORTEMENT
    for (GameObject go : gos) {
        // Traiter uniquement les objets valides (pas soi-même et objets actifs)
        if (go != this && go.isEnable()) {
            
            if (collideLeft(go)) {
                setX(go.getXRight() + demiW);  // Repositionner
                onCollideWith(go);              // Comportement spécifique
            } 
            else if (collideRight(go)) {
                setX(go.getXLeft() - demiW);
                onCollideWith(go);
            } 
            else if (collideTop(go)) {
                setY(go.getYBottom() + demiH);
                onCollideWith(go);
            } 
            else if (collideBottom(go)) {
                setY(go.getYTop() - demiH);
                onCollideWith(go);
            }
        }
    }
}
```

### Note importante sur les conditions

Dans la boucle `for`, nous utilisons une condition positive pour traiter les objets :

```java
if (go != this && go.isEnable()) {
    // Traiter les collisions
}
```

**Ceci est strictement équivalent à :**

```java
if (go == this) continue;
if (!go.isEnable()) continue;
// Traiter les collisions
```

**Les deux versions sont correctes et produisent exactement le même résultat :**

| Version | Description | Avantage |
|---------|-------------|----------|
| **Avec `continue`** | "Si invalide, passe au suivant" | Plus compact, utilisé en industrie |
| **Avec condition positive** | "Si valide, traite-le" | Plus intuitif pour les débutants |

Le mot-clé `continue` signifie "arrête l'itération actuelle et passe à l'élément suivant de la boucle". En utilisant `continue`, on évite d'imbriquer le code dans un `if`, mais le résultat est identique.

---

## 🎬 Exemple d'exécution étape par étape

### Situation initiale

```
Joueur en (100, 100), regarde Nord (rotation=0)
Vitesse = 3
Mur vertical en X=140

   60        140        
    ↓         ↓
┌────────┐ ┏━━━┓
│ Joueur │ ┃Mur┃
│   👤   │ ┃   ┃
└────────┘ ┗━━━┛
    ↑
   100
```

### L'utilisateur appuye sur (Droite)

```java
joueur.move(3, 0, 90, gos);
//          ^  ^  ^
//          dx dy rotation
```

#### Étape 1 : Application du mouvement

```java
setX(100 + 3);  // X devient 103
setY(100 + 0);  // Y reste 100
getToken().setRotate(90);  // Tourne vers l'Est
```

**Position apres mouvement** :
```
   63        140
    ↓         ↓
   ┌────────┐┏━━━┓
   │ Joueur ││Mur┃  COLLISION DETECTEE
   │   👤→  ││   ┃
   └────────┘┗━━━┛
       ↑
      103
```

#### Étape 2 : Détection de collision

```java
demiW = demiLargeurCollision();  // rotation=90 → height/2 = 20/2 = 10
demiH = demiHauteurCollision();  // rotation=90 → width/2 = 80/2 = 40

// Bornes du joueur (rotation 90°) :
getXLeft()  = 103 - 10 = 93
getXRight() = 103 + 10 = 113  (Depasse le mur)
getYTop()   = 100 - 40 = 60
getYBottom()= 100 + 40 = 140

// Bornes du mur :
mur.getXLeft()  = 140
mur.getXRight() = 160
```

**Test `collideRight(mur)` :**
```java
getY() >= mur.getYTop()      (100 >= ...) true
getY() <= mur.getYBottom()   (100 <= ...) true
getXRight() >= mur.getXLeft()(113 >= 140) false
```

Hmm, pas de collision detectee ? Attendez !

En realite, le joueur **entre dans le mur** progressivement. Au prochain frame, il sera plus a droite et **alors** la collision sera detectee.

#### Etape 3 : Repositionnement (au prochain frame ou collision = true)

```java
if (collideRight(mur)) {
    setX(mur.getXLeft() - demiW);  // X = 140 - 10 = 130
    onCollideWith(mur);
}
```

**Position finale** :
```
   120       140
    ↓         ↓
   ┌────────┐┏━━━┓
   │ Joueur │┃Mur┃  Recale au bord
   │   👤→  │┃   ┃
   └────────┘┗━━━┛
       ↑
      130
```

**C'est ce `130` que vous voyez dans la console.**

Ce n'est **pas une erreur**, c'est juste la **position X finale** du joueur apres repositionnement au bord du mur.

#### Etape 4 : Comportement specifique

```java
// Dans Joueur.onCollideWith()
if (go instanceof Item) {
    drop((Item) go);  // Pas un Item ici
} 
// Rien d'autre à faire, le repositionnement est déjà fait !
```

---

## Les methodes de collision par cote

### Structure commune

Toutes les 4 méthodes ont la **même structure** :

```java
public boolean collideXXX(GameObject elem) {
    if (!elem.isEnable()) return false;  // Sécurité
    
    // Conditions de collision spécifiques au côté
    return condition1
        && condition2
        && condition3
        && condition4;
}
```

### Détail : `collideLeft()`

```java
public boolean collideLeft(GameObject elem) {
    if (!elem.isEnable()) return false;
    
    double xGauchePerso = getXLeft();
    return getY() >= elem.getYTop()           // (1)
        && getY() <= elem.getYBottom()        // (2)
        && xGauchePerso <= elem.getXRight()   // (3)
        && xGauchePerso >= elem.getXLeft();   // (4)
}
```

#### Explication visuelle

**Collision par la GAUCHE** = Le côté gauche du personnage touche l'objet

```
Personnage            Objet
    •──────┐         ┏━━━━┓
    │      │    ⚠️   ┃    ┃
    └──────┘         ┗━━━━┛
    ↑                ↑    ↑
  xGauchePerso   getXLeft getXRight
```

**Conditions** :

1. `getY() >= elem.getYTop()` : Le centre du perso est **au-dessus ou au niveau** du haut de l'objet
2. `getY() <= elem.getYBottom()` : Le centre du perso est **en-dessous ou au niveau** du bas de l'objet
3. `xGauchePerso <= elem.getXRight()` : Le bord gauche du perso est **à gauche ou au niveau** du bord droit de l'objet
4. `xGauchePerso >= elem.getXLeft()` : Le bord gauche du perso est **à droite ou au niveau** du bord gauche de l'objet

**Les 4 conditions doivent être vraies simultanément pour qu'il y ait collision !**

### Schéma récapitulatif des 4 côtés

```
        collideTop()
             ↓
      ╔═════════════╗
      ║             ║
  ←   ║      •      ║   → collideRight()
collideLeft()       ║
      ║             ║
      ╚═════════════╝
             ↑
      collideBottom()
```

### Pourquoi tester `!elem.isEnable()` ?

Un objet **desactive** (comme une piece deja ramassee) ne doit **plus** causer de collision.

**Exemple** :
```java
Coin coin = new Coin(200, 300);
joueur.drop(coin);  // Desactive la piece

// Maintenant, meme si le joueur traverse la position (200, 300),
// collideLeft(coin) retournera FALSE car coin.isEnable() = false
```

---

## La methode abstraite `onCollideWith()`

```java
public abstract void onCollideWith(GameObject go);
```

### Pourquoi abstraite ?

Chaque type de personnage réagit **différemment** aux collisions :

| Personnage | Collision avec Mur | Collision avec Item | Collision avec autre personnage |
|------------|-------------------|---------------------|--------------------------------|
| **Joueur** | Deja repositionne par `move()` | Ramasse l'item (`drop`) | Subit des degats |
| **Monster** | Change de direction | Ignore | Inflige des degats si c'est le joueur |

**Character ne peut pas deviner** ces comportements, il delegue donc aux classes filles.

### Implementations concretes

#### Dans `Joueur`

```java
@Override
public void onCollideWith(GameObject go) {
    if (go instanceof Item) {
        drop((Item) go);  // Ramasser l'item
    }
    // Pas besoin de repositionner : deja fait par Character.move()
}
```

#### Dans `Monster`

```java
@Override
public void onCollideWith(GameObject go) {
    changeDirection();  // Rebondir
    
    if (go instanceof Joueur) {
        ((Joueur) go).reciveDamages(2);  // Attaquer le joueur
    }
}
```

---

## Principes POO illustres

### 1. **Heritage**

Character **herite** de GameObject et **ajoute** des fonctionnalites :

```
GameObject
├── position (x, y)
├── dimensions (width, height)
├── token (visuel)
└── enable (actif/désactivé)

Character (AJOUTE)
├── vitesse
├── move()
├── collisions rotation-aware
└── onCollideWith() [abstrait]
```

### 2. **Abstraction**

Character est abstraite car :
- On ne cree **jamais** de `new Character()` (pas de sens)
- On cree des `new Joueur()` ou `new Monster()`

**Un Character est un concept abstrait, pas un objet concret.**

### 3. **Polymorphisme**

```java
List<Character> personnages = new ArrayList<>();
personnages.add(new Joueur(100, 100));
personnages.add(new Monster(200, 200));

for (Character c : personnages) {
    c.move(3, 0, 90, gos);  // Même appel...
    // Mais onCollideWith() aura un comportement différent !
}
```

### 4. **Encapsulation**

Les methodes `demiLargeurCollision()` et `demiHauteurCollision()` sont **private** :
- Detail d'implementation cache
- Peut etre modifie sans casser le code externe
- Simplifie l'interface publique de la classe

### 5. **Template Method Pattern**

`move()` est un **modele** (template) qui definit le **squelette** de l'algorithme :
1. Appliquer le mouvement
2. Detecter les collisions
3. Repositionner
4. Appeler `onCollideWith()` (point d'extension pour les classes filles)

---

## Pieges courants et solutions

### Piege 1 : Oublier la rotation dans les dimensions

```java
// FAUX - Dimensions fixes
@Override
public double getXLeft() { return getX() - getWidth() / 2.0; }

// CORRECT - Dimensions ajustees selon rotation
@Override
public double getXLeft() { return getX() - demiLargeurCollision(); }
```

**Consequence** : Collisions fausses quand le personnage tourne a 90 ou 270 degres.

### Piege 2 : Tester `go == this` apres les collisions

```java
// FAUX - Test trop tard
for (GameObject go : gos) {
    if (collideLeft(go)) {
        if (go == this) continue;  // Trop tard, deja calcule
        // ...
    }
}

// CORRECT - Test au debut
for (GameObject go : gos) {
    if (go != this && go.isEnable()) {  // Evite les calculs inutiles
        if (collideLeft(go)) {
            // ...
        }
    }
}
```

### Piege 3 : Oublier de tester `isEnable()`

```java
// FAUX - Collision avec objets desactives
if (collideLeft(elem)) {
    // Une piece ramassee peut encore bloquer
}

// CORRECT - Tester isEnable() dans les methodes de collision
public boolean collideLeft(GameObject elem) {
    if (!elem.isEnable()) return false;
    // ...
}
```

### Piege 4 : Double repositionnement

```java
// FAUX - Repositionner dans move() ET dans onCollideWith()
// Character.move()
if (collideLeft(go)) {
    setX(go.getXRight() + demiW);  // Repositionnement 1
    onCollideWith(go);
}

// Joueur.onCollideWith()
public void onCollideWith(GameObject go) {
    repositionAbout(go);  // Repositionnement 2 (doublon)
}

// CORRECT - Repositionner une seule fois dans move()
// Joueur.onCollideWith()
public void onCollideWith(GameObject go) {
    if (go instanceof Item) {
        drop((Item) go);
    }
    // Pas de repositionnement ici
}
```

---

## Points cles a retenir

| Concept | Explication |
|---------|-------------|
| **Vitesse** | Pixels de deplacement par frame (generalement 1-5) |
| **Rotation-aware** | Les dimensions de collision s'inversent a 90 et 270 degres |
| **demiLargeurCollision()** | Largeur ajustee : width/2 (0/180°) ou height/2 (90/270°) |
| **move()** | Applique mouvement puis detecte collisions puis repositionne puis appelle onCollideWith |
| **collideXXX()** | Teste la collision d'un cote specifique avec 4 conditions |
| **onCollideWith()** | Methode abstraite pour les comportements specifiques (ramasser, rebondir, etc.) |
| **Repositionnement** | Fait dans `move()`, pas dans `onCollideWith()` (eviter doublon) |
| **@Override** | Indique qu'on redefinit une methode de GameObject |

---

## Questions frequentes

### Pourquoi ne pas annuler le mouvement au lieu de repositionner ?

**Annuler** = remettre a l'ancienne position donne un effet "teleportation" brutal
**Repositionner** = placer au bord donne un mouvement fluide, sensation naturelle

### Pourquoi tester 4 cotes au lieu d'une collision globale ?

Pour **repositionner correctement**. Si on sait que la collision est par la GAUCHE, on recale a DROITE de l'objet.

### Que se passe-t-il si le personnage est coince entre 2 murs ?

La boucle traite les collisions **sequentiellement** :
1. Collision avec mur1 puis repositionnement
2. Collision avec mur2 puis repositionnement
3. Le personnage finit "coince" entre les deux (comportement attendu)

### Pourquoi `double` pour dx/dy mais `int` pour vitesse ?

- `vitesse` (int) = valeur de base stable
- `dx/dy` (double) = permet des calculs plus complexes si besoin (ex: vitesse fois 0.5 pour ralentissement)

### C'est quoi le "130" dans la console ?

C'est la **position X finale** du joueur apres repositionnement au bord d'un mur. Ce n'est **pas une erreur**, juste une trace de debug quelque part dans votre code (probablement un `System.out.println()`).

---

## Liens avec les autres classes

### Character utilise :
- **GameObject** : herite de position, dimensions, token, bornes AABB
- **List de GameObject** : pour tester les collisions avec tous les objets

### Character est utilise par :
- **Joueur** : implemente `onCollideWith()` pour ramasser items
- **Monster** : implemente `onCollideWith()` pour rebondir et attaquer

### Prochaines classes a etudier :
1. **Joueur** : Gestion des vies, score, items, degats
2. **Monster** : IA simple (deplacement automatique, changement de direction)

---

## Conclusion

**Character** est le **coeur du systeme de deplacement** de votre jeu. En mutualisant la logique de collision et de repositionnement, vous :

- Evitez la duplication de code
- Facilitez la maintenance (1 bug = 1 seul endroit a corriger)
- Uniformisez le comportement de tous les personnages
- Respectez les principes POO (heritage, abstraction, polymorphisme)

Cette architecture solide vous permet d'ajouter facilement de nouveaux types de personnages (Boss, PNJ, animaux...) sans reecrire la logique de collision.

# Explication détaillée de la classe `Joueur`

## Vue d'ensemble

La classe **`Joueur`** représente le **personnage contrôlé par le joueur** via le clavier. C'est une classe **concrète** qui hérite de `Character` et implémente tous les comportements spécifiques au joueur : gestion de la vie, du score, ramassage d'items, et réception de dégâts.

### Hiérarchie d'héritage

```
GameObject (abstraite)
    |
    v
Character (abstraite)
    |
    v
Joueur (concrète) <-- Vous êtes ici
```

### Pourquoi une classe Joueur ?

Le joueur a des **comportements uniques** que les monstres n'ont pas :
- Accumulation de points (score)
- Gestion de points de vie affichés dans le HUD
- Ramassage d'items
- Réception de dégâts avec effet visuel de recul

> Analogie : GameObject est la "fondation", Character ajoute les "roues", et Joueur ajoute le "tableau de bord et les contrôles".

---

## Les attributs

### Attribut `live` (points de vie)

```java
private int live = 10;
```

#### Rôle

C'est le nombre de **points de vie** du joueur. Quand il tombe à 0, c'est le **Game Over**.

**Caractéristiques** :
- Valeur initiale : 10
- Valeur minimum : 0 (ne peut jamais être négatif)
- Valeur maximum : Pas de limite dans le code actuel (peut être étendu)

#### Modification

La vie ne peut diminuer qu'avec la méthode `reciveDamages(int x)` qui garantit que `live >= 0`.

**Exemple d'utilisation** :
```java
Joueur joueur = new Joueur(100, 100);
System.out.println(joueur.getLive());  // Affiche : 10

// Le joueur se fait attaquer par un monstre
joueur.reciveDamages(2);
System.out.println(joueur.getLive());  // Affiche : 8

// Attaque massive
joueur.reciveDamages(100);
System.out.println(joueur.getLive());  // Affiche : 0 (pas -92 !)
```

---

### Attribut `points` (score)

```java
private int points = 0;
```

#### Rôle

C'est le **score** du joueur, qui augmente quand il ramasse des items.

**Valeurs des items** :
- Pièce (`Coin`) : +1 point
- Rubis (`Ruby`) : +3 points

#### Modification

Le score augmente via la méthode `drop(Item i)` qui ajoute `i.getValue()` au total.

**Exemple d'utilisation** :
```java
Joueur joueur = new Joueur(100, 100);
System.out.println(joueur.getPoints());  // Affiche : 0

Coin piece = new Coin(200, 200);
joueur.drop(piece);
System.out.println(joueur.getPoints());  // Affiche : 1

Ruby rubis = new Ruby(300, 300);
joueur.drop(rubis);
System.out.println(joueur.getPoints());  // Affiche : 4 (1 + 3)
```

---

## Le constructeur

```java
public Joueur(double x, double y) {
    super(x, y, 80, 20, 3);
}
```

### Décryptage des paramètres

| Paramètre | Valeur | Signification |
|-----------|--------|---------------|
| `x` | Variable | Position X initiale (centre du joueur) |
| `y` | Variable | Position Y initiale (centre du joueur) |
| `80` | Fixe | Largeur du joueur (sprite horizontal) |
| `20` | Fixe | Hauteur du joueur (sprite horizontal) |
| `3` | Fixe | Vitesse de déplacement (3 pixels/frame) |

### Appel de `super()`

Le mot-clé `super()` appelle le **constructeur de la classe parente** (`Character`), qui lui-même appelle le constructeur de `GameObject`.

**Chaîne d'appels** :
```
Joueur(100, 100)
    |
    v
Character(100, 100, 80, 20, 3)
    |
    v
GameObject(100, 100, 80, 20)
    |
    v
createToken(100, 100)  // Exécute la version dans Joueur !
```

### Pourquoi ces dimensions ?

Le joueur a un sprite **horizontal** (80×20) :
- Largeur 80 : Le joueur est "long" horizontalement
- Hauteur 20 : Le joueur est "fin" verticalement

**Visualisation** :
```
Rotation 0° (Nord)          Rotation 90° (Est)
┌──────────────────┐        ┌────┐
│      Joueur      │        │    │
└──────────────────┘        │Joue│
    80px × 20px             │ur  │
                            │    │
                            └────┘
                           20px × 80px
```

Quand le joueur tourne à 90° ou 270°, les dimensions de collision **s'inversent** automatiquement grâce à `Character`.

---

## Les getters pour le HUD

### `getLive()`

```java
public int getLive() { 
    return live; 
}
```

**Utilisation dans Main.java** :
```java
hudVie.setText("Vie : " + joueur.getLive());
```

Permet d'afficher en temps réel les points de vie du joueur à l'écran.

---

### `getPoints()`

```java
public int getPoints() { 
    return points; 
}
```

**Utilisation dans Main.java** :
```java
hudPoints.setText("Points : " + joueur.getPoints());
```

Permet d'afficher en temps réel le score du joueur à l'écran.

---

### `getRotation()`

```java
public double getRotation() { 
    return getToken().getRotate(); 
}
```

**Rôle** : Récupère l'orientation actuelle du joueur (0, 90, 180, 270).

**Utilisation** :
- Dans le clavier (`Main.java`) pour conserver la rotation actuelle
- Dans `reciveDamages()` pour calculer le recul dans la bonne direction

**Exemple** :
```java
double r = joueur.getRotation();

if (r == 0)   { /* Regarde vers le nord */ }
if (r == 90)  { /* Regarde vers l'est */ }
if (r == 180) { /* Regarde vers le sud */ }
if (r == 270) { /* Regarde vers l'ouest */ }
```

---

## La méthode `createToken()`

```java
@Override
public void createToken(double x, double y) {
    System.out.println("Méthode createToken du joueur appelée");

    // Corps du joueur (ellipse)
    Ellipse corp = new Ellipse(0, 0, getWidth() / 2, getHeight() / 2);
    corp.setFill(Color.DARKOLIVEGREEN);
    
    // Tête
    Circle tete = new Circle(0, 0, 15);
    
    // Main
    Circle main = new Circle(32, -10, 6);
    
    // Épée
    Rectangle sword = new Rectangle(25, -13, 15, 3);
    sword.setFill(Color.WHITE);

    // Assemblage de toutes les formes
    Group g = new Group(corp, tete, main, sword);
    g.setLayoutX(x);
    g.setLayoutY(y);

    setToken(g);
}
```

### Composition du joueur

Le joueur est composé de **4 formes géométriques** :

#### 1. Le corps (Ellipse)

```java
Ellipse corp = new Ellipse(0, 0, getWidth() / 2, getHeight() / 2);
```

**Paramètres** :
- `0, 0` : Centre au point (0, 0) **relatif au Group**
- `getWidth() / 2` : Rayon horizontal = 80/2 = 40
- `getHeight() / 2` : Rayon vertical = 20/2 = 10

**Résultat** : Une ellipse verte olive de 80×20 pixels.

#### 2. La tête (Circle)

```java
Circle tete = new Circle(0, 0, 15);
```

**Paramètres** :
- `0, 0` : Centre au milieu du corps
- `15` : Rayon de 15 pixels

**Résultat** : Un cercle noir de 30 pixels de diamètre.

#### 3. La main (Circle)

```java
Circle main = new Circle(32, -10, 6);
```

**Paramètres** :
- `32` : Décalé de 32 pixels à droite (vers l'avant du joueur)
- `-10` : Décalé de 10 pixels vers le haut
- `6` : Rayon de 6 pixels

**Résultat** : Un petit cercle noir représentant la main qui tient l'épée.

#### 4. L'épée (Rectangle)

```java
Rectangle sword = new Rectangle(25, -13, 15, 3);
sword.setFill(Color.WHITE);
```

**Paramètres** :
- `25, -13` : Position (coin supérieur gauche du rectangle)
- `15, 3` : Dimensions (15 pixels de long, 3 pixels de large)
- Couleur : Blanc

**Résultat** : Une fine barre blanche représentant l'épée.

### Assemblage dans un Group

```java
Group g = new Group(corp, tete, main, sword);
```

Toutes les formes sont regroupées dans un **seul conteneur** `Group`. Cela permet de :
- Déplacer tout le joueur d'un coup (`g.setLayoutX()`)
- Tourner tout le joueur d'un coup (`g.setRotate()`)
- Masquer tout le joueur d'un coup (`g.setVisible()`)

### Positionnement dans la scène

```java
g.setLayoutX(x);
g.setLayoutY(y);
```

Le Group est positionné à la position `(x, y)` demandée dans le monde JavaFX.

### Enregistrement du token

```java
setToken(g);
```

**CRITIQUE** : Cette ligne est **obligatoire** ! Elle stocke le Group dans l'attribut `token` hérité de `GameObject`.

Sans cette ligne, `token` reste `null` et le jeu crash avec une `NullPointerException`.

---

## La méthode `drop(Item i)`

```java
public void drop(Item i) {
    i.disable();            // Désactive l'item (invisible + plus de collision)
    points += i.getValue(); // Ajoute la valeur de l'item au score
}
```

### Étape par étape

#### Étape 1 : Désactivation de l'item

```java
i.disable();
```

Appelle la méthode `disable()` de la classe `Item`, qui fait :
```java
public void disable() {
    setEnable(false);  // Désactive l'objet
}
```

**Conséquences** :
- `token.setVisible(false)` : L'item disparaît de l'écran
- `isEnable()` retourne `false` : L'item ne cause plus de collision

#### Étape 2 : Ajout des points

```java
points += i.getValue();
```

Ajoute la valeur de l'item au score du joueur.

**Rappel des valeurs** :
- `Coin` : `getValue()` retourne 1
- `Ruby` : `getValue()` retourne 3

### Exemple complet

```java
Joueur joueur = new Joueur(100, 100);
Coin piece = new Coin(200, 200);

System.out.println(joueur.getPoints());  // 0
System.out.println(piece.isEnable());    // true

// Le joueur ramasse la pièce
joueur.drop(piece);

System.out.println(joueur.getPoints());  // 1
System.out.println(piece.isEnable());    // false
```

---

## La méthode `reciveDamages(int x)`

```java
public void reciveDamages(int x) {
    // Retrait des points de vie (minimum 0)
    live = Math.max(0, live - x);

    // Effet de recul : 10 pixels dans la direction opposée
    double r = getRotation();
    
    if (r == 0) {
        // Regarde Nord (haut) -> recule vers le bas
        setY(getY() + 10);
    } 
    else if (r == 180) {
        // Regarde Sud (bas) -> recule vers le haut
        setY(getY() - 10);
    } 
    else if (r == 90) {
        // Regarde Est (droite) -> recule vers la gauche
        setX(getX() - 10);
    } 
    else if (r == 270) {
        // Regarde Ouest (gauche) -> recule vers la droite
        setX(getX() + 10);
    }
}
```

### Étape 1 : Retrait des points de vie

```java
live = Math.max(0, live - x);
```

**Décryptage de `Math.max()`** :

`Math.max(a, b)` retourne le **plus grand** des deux nombres.

**Exemples** :
```java
// Cas normal
live = 10;
live = Math.max(0, 10 - 2);  // Math.max(0, 8) = 8

// Cas limite
live = 3;
live = Math.max(0, 3 - 5);   // Math.max(0, -2) = 0 (pas de vie négative !)
```

**Pourquoi cette protection ?**

Sans `Math.max()`, on pourrait avoir `live = -2`, ce qui n'a pas de sens dans un jeu. Le joueur est soit vivant (live > 0), soit mort (live = 0).

### Étape 2 : Effet de recul

Le joueur **recule de 10 pixels** dans la **direction opposée** à celle qu'il regarde.

#### Logique du recul

| Rotation | Direction du regard | Direction du recul | Code |
|----------|--------------------|--------------------|------|
| **0°**   | Nord (haut)        | Sud (bas)          | `setY(getY() + 10)` |
| **180°** | Sud (bas)          | Nord (haut)        | `setY(getY() - 10)` |
| **90°**  | Est (droite)       | Ouest (gauche)     | `setX(getX() - 10)` |
| **270°** | Ouest (gauche)     | Est (droite)       | `setX(getX() + 10)` |

#### Visualisation

**Joueur regarde vers le Nord (rotation = 0°)** :
```
Avant le dégât         Après le dégât
     ^                      ^
     |                      |
  ┌─────┐              ┌─────┐
  │  J  │              │     │
  └─────┘              │  J  │ <- Recule vers le bas
  Position Y           └─────┘
                       Position Y + 10
```

**Joueur regarde vers l'Est (rotation = 90°)** :
```
Avant le dégât         Après le dégât

  ┌─────┐ ->        ┌─────┐ ->
  │  J  │           │  J  │
  └─────┘           └─────┘
  Position X     Position X - 10 (recule vers la gauche)
```

### Pourquoi cet effet de recul ?

C'est un **feedback visuel** important pour le joueur :
1. Indique clairement qu'il a subi des dégâts
2. Crée une sensation d'impact
3. Éloigne temporairement le joueur de la source de danger

---

## La méthode `onCollideWith(GameObject go)`

```java
@Override
public void onCollideWith(GameObject go) {
    if (go instanceof Item) {
        drop((Item) go);
    }
    // Pas besoin de repositionner : Character.move() s'en est déjà occupé
}
```

### Rôle de cette méthode

C'est l'**implémentation concrète** de la méthode abstraite `onCollideWith()` définie dans `Character`.

Elle définit ce que fait **spécifiquement le joueur** quand il entre en collision avec un objet.

### Logique de collision

#### Cas 1 : Collision avec un Item

```java
if (go instanceof Item) {
    drop((Item) go);
}
```

**Opérateur `instanceof`** : Teste si un objet est d'un type donné.

**Exemples** :
```java
Coin piece = new Coin(100, 100);
Ruby rubis = new Ruby(200, 200);
Mur mur = new Mur(300, 300, 40, 100);

piece instanceof Item     // true
rubis instanceof Item     // true
mur instanceof Item       // false

piece instanceof Coin     // true
piece instanceof Ruby     // false
```

**Comportement** : Si l'objet touché est un Item (Coin ou Ruby), le joueur le ramasse via `drop()`.

#### Cas 2 : Collision avec autre chose (mur, monstre)

```java
// Pas besoin de repositionner : Character.move() s'en est déjà occupé
```

**Important** : Le joueur ne fait rien de spécial car :
1. `Character.move()` a déjà repositionné le joueur au bord de l'objet
2. Les dégâts des monstres sont gérés dans `Monster.onCollideWith()`

### Exemple de flux complet

**Situation** : Le joueur se déplace vers la droite et touche une pièce.

```java
// Dans Main.java (gestion du clavier)
joueur.move(3, 0, 90, gos);  // dx=3, dy=0, rotation=90°

// Dans Character.move()
setX(getX() + 3);              // Déplacement
// ... collision détectée avec la pièce ...
onCollideWith(coin);           // Appelle Joueur.onCollideWith()

// Dans Joueur.onCollideWith()
if (coin instanceof Item) {    // true
    drop(coin);                // Ramasse la pièce
}
```

**Résultat** :
- La pièce disparaît
- Le score augmente de 1
- Le joueur continue sa route

---

## La méthode `repositionAbout(GameObject go)`

```java
private void repositionAbout(GameObject go) {
    // Dimensions de collision selon rotation (0/180 = normal, 90/270 = inversé)
    double actualWidth;
    if (getRotation() == 0 || getRotation() == 180) {
        actualWidth = getWidth();
    } else {
        actualWidth = getHeight();
    }
    
    double actualHeight;
    if (getRotation() == 0 || getRotation() == 180) {
        actualHeight = getHeight();
    } else {
        actualHeight = getWidth();
    }

    // Repositionnement selon le côté de collision
    if (collideLeft(go)) {
        double newX = go.getXRight() + actualWidth / 2 + 1;
        setX(newX);
    } 
    else if (collideRight(go)) {
        double newX = go.getXLeft() - actualWidth / 2 - 1;
        setX(newX);
    } 
    else if (collideTop(go)) {
        double newY = go.getYBottom() + actualHeight / 2 + 1;
        setY(newY);
    } 
    else if (collideBottom(go)) {
        double newY = go.getYTop() - actualHeight / 2 - 1;
        setY(newY);
    }
}
```

### Rôle de cette méthode

C'est une méthode **utilitaire** qui permet de **repositionner manuellement** le joueur au bord d'un objet en cas de collision.

### Pourquoi elle existe ?

#### Contexte pédagogique

Le professeur demande cette méthode dans l'énoncé pour que vous compreniez la **logique de repositionnement**.

#### Usage dans le projet

**Dans le fonctionnement normal** : Cette méthode **n'est pas appelée** car `Character.move()` gère déjà le repositionnement automatiquement.

**Cas d'usage potentiel** : Elle peut être utilisée pour un contrôle manuel si le joueur se retrouve coincé dans une situation complexe (bug, collision multiple, etc.).

### Logique de repositionnement

#### Étape 1 : Calcul des dimensions ajustées

```java
double actualWidth;
if (getRotation() == 0 || getRotation() == 180) {
    actualWidth = getWidth();
} else {
    actualWidth = getHeight();
}
```

**Rappel** : Quand le joueur tourne à 90° ou 270°, ses dimensions s'inversent.

| Rotation | `actualWidth` | `actualHeight` |
|----------|---------------|----------------|
| 0° ou 180° | `getWidth()` (80) | `getHeight()` (20) |
| 90° ou 270° | `getHeight()` (20) | `getWidth()` (80) |

#### Étape 2 : Repositionnement selon le côté

**Collision par la GAUCHE** :
```java
if (collideLeft(go)) {
    double newX = go.getXRight() + actualWidth / 2 + 1;
    setX(newX);
}
```

**Décomposition** :
- `go.getXRight()` : Bord droit de l'objet
- `+ actualWidth / 2` : Demi-largeur du joueur
- `+ 1` : Petit décalage de sécurité (évite de rester collé)

**Résultat** : Le joueur est placé juste à droite de l'objet, sans le toucher.

**Schéma** :
```
Avant                    Après
  Joueur   Mur          Joueur  Mur
    ┌─┐   ┏━━┓          ┌─┐   ┏━━┓
    │J│   ┃  ┃   ->     │J│   ┃  ┃
    └─┘   ┗━━┛          └─┘   ┗━━┛
    ^     ^                ^   ^
    |     |                |   |
  centre  XRight       newX   XRight
```

**Calcul de `newX`** :
```
newX = go.getXRight() + actualWidth/2 + 1
newX = 140 + 40 + 1
newX = 181
```

Les autres côtés suivent la même logique (droite, haut, bas).

### Pourquoi `+ 1` ?

Le `+ 1` est un **décalage de sécurité** pour éviter que le joueur reste "collé" au mur et que la collision soit détectée à nouveau au prochain frame.

---

## Principes POO illustrés

### 1. Heritage

Joueur **hérite** de Character, qui hérite de GameObject.

**Ce que Joueur hérite** :
- De GameObject : position, dimensions, token, enable
- De Character : vitesse, move(), collisions rotation-aware

**Ce que Joueur ajoute** :
- Attributs : live, points
- Méthodes : drop(), reciveDamages()
- Implémentation : onCollideWith()

### 2. Surcharge de méthode (Override)

```java
@Override
public void onCollideWith(GameObject go) {
    // Implémentation spécifique au joueur
}
```

Joueur **redéfinit** la méthode abstraite de Character pour implémenter son propre comportement.

### 3. Polymorphisme

```java
Character perso = new Joueur(100, 100);
perso.onCollideWith(item);  // Appelle la version JOUEUR, pas Character !
```

Même si la variable est de type `Character`, c'est bien la méthode de `Joueur` qui est exécutée (résolution dynamique à l'exécution).

### 4. Encapsulation

Les attributs `live` et `points` sont **private** :
- Impossible de faire `joueur.live = -50` depuis l'extérieur
- Accès contrôlé via getters
- Modification contrôlée via `reciveDamages()` et `drop()`

### 5. Operateur instanceof

```java
if (go instanceof Item) {
    // Traitement spécifique aux items
}
```

Permet de tester le type réel d'un objet à l'exécution et d'adapter le comportement en conséquence.

---

## Pieges courants et solutions

### Piege 1 : Oublier setToken()

```java
// FAUX
@Override
public void createToken(double x, double y) {
    Group g = new Group(...);
    g.setLayoutX(x);
    g.setLayoutY(y);
    // Oubli de setToken(g) !
}
```

**Consequence** : `token` reste `null`, crash avec `NullPointerException` quand on essaie d'afficher le joueur.

**CORRECT** :
```java
@Override
public void createToken(double x, double y) {
    Group g = new Group(...);
    g.setLayoutX(x);
    g.setLayoutY(y);
    setToken(g);  // OBLIGATOIRE
}
```

### Piege 2 : Vie negative

```java
// FAUX
public void reciveDamages(int x) {
    live = live - x;  // Peut devenir négatif !
}
```

**CORRECT** :
```java
public void reciveDamages(int x) {
    live = Math.max(0, live - x);  // Minimum 0
}
```

### Piege 3 : Mauvaise direction de recul

```java
// FAUX - Le joueur recule toujours vers le bas
public void reciveDamages(int x) {
    live = Math.max(0, live - x);
    setY(getY() + 10);  // Toujours la même direction
}
```

**CORRECT** : Adapter le recul selon la rotation (code complet fourni plus haut).

### Piege 4 : Oublier le cast avec instanceof

```java
// FAUX
if (go instanceof Item) {
    drop(go);  // Erreur de compilation : go est un GameObject, pas un Item !
}

// CORRECT
if (go instanceof Item) {
    drop((Item) go);  // Cast explicite vers Item
}
```

---

## Points cles a retenir

| Concept | Explication |
|---------|-------------|
| **live** | Points de vie (10 par defaut, minimum 0) |
| **points** | Score du joueur (augmente avec les items) |
| **drop()** | Ramasse un item : le desactive et ajoute des points |
| **reciveDamages()** | Retire des points de vie et fait reculer le joueur |
| **onCollideWith()** | Ramasse les items, ignore les murs (deja repositionne) |
| **createToken()** | Cree le visuel du joueur (corps, tete, main, epee) |
| **repositionAbout()** | Methode utilitaire pour controle manuel (rarement utilisee) |
| **instanceof** | Teste le type reel d'un objet a l'execution |
| **Math.max()** | Prend le maximum de deux valeurs (protection contre negatif) |

---

## Questions frequentes

### Pourquoi `live` est un `int` et pas un `double` ?

Les points de vie sont des **valeurs discretes** (1, 2, 3...), pas des valeurs continues (1.5, 2.7...). Un `int` est plus adapte.

### Pourquoi le recul est de 10 pixels exactement ?

C'est une valeur arbitraire choisie pour l'effet visuel. Vous pouvez la modifier selon vos preferences :
- Recul plus petit (5 pixels) : effet plus subtil
- Recul plus grand (20 pixels) : effet plus dramatique

### Peut-on augmenter la vie du joueur ?

Dans le code actuel, non. Mais vous pouvez facilement ajouter une methode :
```java
public void heal(int x) {
    live = Math.min(10, live + x);  // Maximum 10
}
```

### Pourquoi `drop()` au lieu de `collect()` ou `pickup()` ?

C'est un choix de nomenclature du professeur. "Drop" signifie ici "ramasser et faire tomber de l'inventaire" (desactiver).

### Que se passe-t-il si le joueur touche un monstre ?

Le monstre appelle `joueur.reciveDamages(2)` dans sa propre methode `onCollideWith()` (voir classe Monster).

---

## Liens avec les autres classes

### Joueur utilise :
- **Character** : herite de vitesse, move(), collisions
- **GameObject** : herite de position, dimensions, token
- **Item** : pour ramasser (Coin, Ruby)

### Joueur est utilise par :
- **Main** : creation du joueur, gestion du clavier, affichage HUD
- **Monster** : pour infliger des degats via `reciveDamages()`

### Prochaines classes a etudier :
1. **Monster** : IA simple, deplacement automatique, attaque
2. **Item, Coin, Ruby** : Objets ramassables
3. **Mur** : Obstacles statiques

---

## Code complet de la classe

```java
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

/**
 * Joueur = personnage contrôlé par le clavier.
 * Hérite de Character : a donc une vitesse, un système de collision, et un déplacement.
 * 
 * Responsabilités :
 * -
