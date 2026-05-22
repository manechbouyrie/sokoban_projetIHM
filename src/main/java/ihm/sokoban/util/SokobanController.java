package ihm.sokoban.util;

import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ResourceBundle;

import ihm.sokoban.SokobanApp;
import ihm.sokoban.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SokobanController implements Initializable{

    // --- Éléments FXML ---
    @FXML 
    private GridPane  grillePlateau;
    @FXML 
    private Label     labelNiveau, labelMouvements, labelPoussees, labelCaisses;
    @FXML
    private VBox      overlayMessage;
    @FXML 
    private Label     labelMessage;
    @FXML 
    private Button    btnAction;

    private SokobanApp app;

    // --- Modèle ---
    private JeuSokoban jeu;

    /** Empêche sizeToScene() d'être rappelé à chaque rafraîchissement de grille */
    private boolean premiereAffichage = true;

    private static final int TAILLE_CASE = 64; // pixels

    // ========== Affichage de la grille ==========

    private void afficherGrille() {
    if (jeu == null || grillePlateau.getScene() == null) return;

    grillePlateau.getChildren().clear();
    grillePlateau.getColumnConstraints().clear();
    grillePlateau.getRowConstraints().clear();

    int lignes = jeu.getNbLignes();
    int cols   = jeu.getNbColonnes();

    if ((lignes == 0 || cols == 0) || (lignes > 100 || cols > 100)) {
        var resource = getClass().getResourceAsStream("/ihm/sokoban/image/marchepas.jpg");
        Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Le niveau est trop grand ou vide !");
            alert.setHeaderText(null);
            ImageView imageView = new ImageView(new Image(resource));
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            alert.setGraphic(imageView);
            alert.showAndWait();
        onNiveauSuivant();
    }

    // Hauteur dispo = scène - barre haut (~60) - barre bas (~50)
    double largeur    = grillePlateau.getScene().getWidth();
    double hauteur    = grillePlateau.getScene().getHeight() - 110;
    double tailleCase = Math.min(largeur / cols, hauteur / lignes);

    // Taille minimale pour éviter des cases invisibles
    tailleCase = Math.max(tailleCase, 32);

    for (int c = 0; c < cols; c++) {
        ColumnConstraints cc = new ColumnConstraints(tailleCase);
        grillePlateau.getColumnConstraints().add(cc);
    }
    for (int l = 0; l < lignes; l++) {
        RowConstraints rc = new RowConstraints(tailleCase);
        grillePlateau.getRowConstraints().add(rc);
    }

    for (int l = 0; l < lignes; l++) {
        for (int c = 0; c < cols; c++) {
            TypeCase tc = jeu.getCase(l, c);
            grillePlateau.add(creerCellule(tc, tailleCase), c, l);
        }
    }

    overlayMessage.setVisible(false);
}

private StackPane creerCellule(TypeCase tc, double tailleCase) {
    StackPane cell = new StackPane();
    cell.setPrefSize(tailleCase, tailleCase);
    cell.setMinSize(tailleCase, tailleCase);
    cell.setMaxSize(tailleCase, tailleCase);

    switch (tc) {
        case MUR:              cell.getStyleClass().add("case-mur");       break;
        case SOL:              cell.getStyleClass().add("case-sol");       break;
        case VIDE:             cell.getStyleClass().add("case-vide");      break;
        case CIBLE:            cell.getStyleClass().add("case-cible");     break;
        case CAISSE:           cell.getStyleClass().add("case-caisse");    break;
        case CAISSE_SUR_CIBLE: cell.getStyleClass().add("case-caisse-ok"); break;
        case JOUEUR:
        case JOUEUR_SUR_CIBLE: cell.getStyleClass().add("case-joueur");    break;
    }
    return cell;
}


    // ========== Mise à jour des infos ==========

    private void mettreAJourInfos() {
        labelNiveau.setText(jeu.getNomNiveauCourant()
            + "  (" + (jeu.getNiveauCourant()+1) + "/" + jeu.getNbNiveaux() + ")");
        labelMouvements.setText("Mouvements : " + jeu.getNbMouvements());
        labelPoussees.setText("Poussées : " + jeu.getNbPoussees());
        labelCaisses.setText("Caisses : "
            + jeu.getNbCaissesSurCible() + "/" + jeu.getNbCaisses());

        // Gérer la fin de partie
        if (jeu.isNiveauTermine()) {
            if (jeu.estDernierNiveau()) {
                labelMessage.setText("");
                btnAction.setText("Retour au début");
            } else {
                labelMessage.setText("");
                btnAction.setText("Niveau suivant ❯");
            }
            overlayMessage.setVisible(true);
            btnAction.requestFocus();
        } else if (jeu.isPerdu()) {
        var resource = getClass().getResourceAsStream("/ihm/sokoban/image/perdu.jpg");

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Perdu");
        confirm.setHeaderText("NOOOOOOOOOOOOOOOON !");
        ImageView imageView = new ImageView(new Image(resource));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        confirm.setGraphic(imageView);

        ButtonType btnOui = new ButtonType("Recommencer");
        ButtonType btnNon = new ButtonType("Quiter", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(btnOui, btnNon);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == btnOui) {
        this.onRecommencer();
        
    } else {
        int i = app.Quitter(null);
        if (i == 1) {
            this.onRecommencer();
        }
    }
        }
    }

    // ========== Clavier ==========

    // À appeler depuis SokobanApp après chargement du FXML :
    //   scene.addEventFilter(KeyEvent.KEY_PRESSED, controller::onKeyPressed);
    public void onKeyPressed(KeyEvent e) {
        Direction dir = null;
        switch (e.getCode()) {
            case UP:    case Z: dir = Direction.HAUT;   break;
            case DOWN:  case S: dir = Direction.BAS;    break;
            case LEFT:  case Q: dir = Direction.GAUCHE; break;
            case RIGHT: case D: dir = Direction.DROITE;  break;
            case R:  onRecommencer(); return;
            default: return;
        }
        // Ctrl+Z = undo
        if (e.getCode() == KeyCode.Z && e.isControlDown()) {
            onAnnuler(); return;
        }

        if (jeu.peutJouer()) {
            jeu.deplacer(dir);
            afficherGrille();
            mettreAJourInfos();
        }
        e.consume();
    }

    public void recommencer(){
        this.onRecommencer();
    }

    public void annuler(){
        this.onAnnuler();
    }

    // ========== Boutons ==========

    @FXML 
    private void onAnnuler() {
        if (jeu.peutAnnuler()) {
            jeu.annuler();
            afficherGrille();
            mettreAJourInfos();
        }
    }

    @FXML 
    private void onRecommencer() {
        jeu.reset();
        afficherGrille();
        mettreAJourInfos();
    }

    @FXML 
    private void onNiveauSuivant() {
        if (jeu.estDernierNiveau()) {
            jeu.chargerNiveauParIndex(0); // retour au début
        } else {
            jeu.niveauSuivant();
        }
        afficherGrille();
        mettreAJourInfos();
    }

    @FXML 
    private void onPrecedent() {
        jeu.niveauPrecedent();
        afficherGrille();
        mettreAJourInfos();
    }

    @FXML 
    private void onSuivant() {
        jeu.niveauSuivant();
        afficherGrille();
        mettreAJourInfos();
    }

    @FXML 
    private void onQuitter() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Voulez-vous vraiment quitter ?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Quitter");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) {
                Stage stage = (Stage) grillePlateau.getScene().getWindow();
                stage.close();
            }
        });
    }

    // Appelée depuis SokobanApp via le eventFilter
    public void deplacer(Direction dir) {
    if (jeu.peutJouer()) {
            jeu.deplacer(dir);
            afficherGrille();
            mettreAJourInfos();
        }
    }

    @Override
public void initialize(URL location, ResourceBundle resources) {
    grillePlateau.sceneProperty().addListener((obs, oldScene, newScene) -> {
        if (newScene != null) {
            newScene.widthProperty().addListener((o, ov, nv) -> Platform.runLater(() -> afficherGrille()));
            newScene.heightProperty().addListener((o, ov, nv) -> Platform.runLater(() -> afficherGrille()));
            Platform.runLater(() -> {
                afficherGrille();
                mettreAJourInfos();
                if (premiereAffichage) {
                    Stage stage = (Stage) grillePlateau.getScene().getWindow();
                    stage.sizeToScene();
                    premiereAffichage = false;
                }
            });
        }
    });
}

    public void setJeuSokoban() {
    jeu = new JeuSokoban(NiveauxSokoban.getNiveaux(), NiveauxSokoban.getNoms(), 0);
    rafraichir();
}

public void setJeuTutoriel() {
    jeu = new JeuSokoban(NiveauxTutoriel.getNiveaux(), NiveauxTutoriel.getNoms(), 0);
    rafraichir();
}

public void setJeuTutoriel(Path nomDossier) {
    LoaderNiveauxXSB.Banque banque = LoaderNiveauxXSB.chargerDepuisDossier(nomDossier);
    jeu = new JeuSokoban(banque.niveaux, banque.noms, 0);
    rafraichir();
}



    public void setSokobanApp(SokobanApp app){
        this.app = app;
    }

    private void rafraichir() {
    if (jeu == null) return;
    afficherGrille();
    mettreAJourInfos();
}
}