package ihm.sokoban.util;

import ihm.sokoban.model.*;
import ihm.sokoban.util.NiveauxTutoriel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SokobanController {

    // --- Éléments FXML ---
    @FXML private GridPane  grillePlateau;
    @FXML private Label     labelNiveau, labelMouvements, labelPoussees, labelCaisses;
    @FXML private VBox      overlayMessage;
    @FXML private Label     labelMessage;
    @FXML private Button    btnAction;

    // --- Modèle ---
    private JeuSokoban jeu;

    private static final int TAILLE_CASE = 64; // pixels

    @FXML
    public void initialize() {
        // Créer le jeu avec les niveaux tutoriel
        jeu = new JeuSokoban(
            NiveauxSokoban.getNiveaux(),
            NiveauxSokoban.getNoms(),
            0
        );
        afficherGrille();
        mettreAJourInfos();
    }

    // ========== Affichage de la grille ==========

    private void afficherGrille() {
        grillePlateau.getChildren().clear();
        grillePlateau.getColumnConstraints().clear();
        grillePlateau.getRowConstraints().clear();

        int lignes = jeu.getNbLignes();
        int cols   = jeu.getNbColonnes();

        // Contraintes de taille fixe par case
        for (int c = 0; c < cols; c++) {
            ColumnConstraints cc = new ColumnConstraints(TAILLE_CASE);
            grillePlateau.getColumnConstraints().add(cc);
        }
        for (int l = 0; l < lignes; l++) {
            RowConstraints rc = new RowConstraints(TAILLE_CASE);
            grillePlateau.getRowConstraints().add(rc);
        }

        // Remplir les cases
        for (int l = 0; l < lignes; l++) {
            for (int c = 0; c < cols; c++) {
                TypeCase tc = jeu.getCase(l, c);
                StackPane cellule = creerCellule(tc);
                grillePlateau.add(cellule, c, l); // attention : add(node, col, row)
            }
        }

        // Masquer l'overlay quand on recharge
        overlayMessage.setVisible(false);
    }

    private StackPane creerCellule(TypeCase tc) {
        StackPane cell = new StackPane();
        cell.setPrefSize(TAILLE_CASE, TAILLE_CASE);

        // Choisir la classe CSS selon le type de case
        switch (tc) {
            case MUR:             cell.getStyleClass().add("case-mur");    break;
            case SOL:             cell.getStyleClass().add("case-sol");    break;
            case VIDE:            cell.getStyleClass().add("case-vide");   break;
            case CIBLE:           cell.getStyleClass().add("case-cible");  break;
            case CAISSE:          cell.getStyleClass().add("case-caisse"); break;
            case CAISSE_SUR_CIBLE:cell.getStyleClass().add("case-caisse-ok"); break;
            case JOUEUR:
            case JOUEUR_SUR_CIBLE:
                cell.getStyleClass().add(
                    tc == TypeCase.JOUEUR_SUR_CIBLE ? "case-cible" : "case-sol"
                );
                // Ajouter un emoji ou label joueur par-dessus
                Label joueur = new Label("🧑");
                joueur.setStyle("-fx-font-size: 36;");
                cell.getChildren().add(joueur);
                break;
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
                labelMessage.setText("🎉 Bravo, dernier niveau réussi !");
                btnAction.setText("Retour au début");
            } else {
                labelMessage.setText("✅ Niveau réussi !");
                btnAction.setText("Niveau suivant ❯");
            }
            overlayMessage.setVisible(true);
        } else if (jeu.isPerdu()) {
            labelMessage.setText("❌ Caisse coincée ! Annulez ou recommencez.");
            btnAction.setText("🔄 Recommencer");
            overlayMessage.setVisible(true);
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
            case RIGHT: case D: dir = Direction.DROITE; break;
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
}