package ihm.sokoban;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import ihm.sokoban.model.Direction;
import ihm.sokoban.util.SokobanController;
import ihm.sokoban.util.menuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Application JavaFX principale pour le jeu Sokoban.
 */
public class SokobanApp extends Application {

    private BorderPane rootPane;
	private Stage primaryStage;

    @Override
public void start(Stage primaryStage){
    
    this.primaryStage = primaryStage;
	this.rootPane     = new BorderPane();
		
	Scene scene = new Scene(rootPane);
	scene.getStylesheets().add(SokobanApp.class.getResource("style.css").toExternalForm());
	primaryStage.setTitle("GestClub App");
	primaryStage.setScene(scene);

	loadmenu();
	//showSaisieMembre(); // affichage temporaire pour validation

	primaryStage.show();
    
}

    public static void main2(String[] args) {
        launch(args);
    }

    public void showSokoban(String nom){
        
    try{
        FXMLLoader loader = new FXMLLoader();
		loader.setLocation( SokobanApp.class.getResource("view/sokoban.fxml"));
        BorderPane vueSokoban = loader.load();
        

        Scene scene = new Scene(vueSokoban);

        
        Stage dialogStage =new Stage();
		dialogStage.setTitle("Sokoban");
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(this.primaryStage);
		dialogStage.setScene(scene);
        scene.getStylesheets().setAll( primaryStage.getScene().getStylesheets() );
        SokobanController ctrl = loader.getController();
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {testTouche(event, ctrl);});

        if(nom.compareTo("tutoriel") == 0){
            ctrl.setJeuTutoriel();
        }
        if(nom.compareTo("sokoban") == 0){
            ctrl.setJeuSokoban();
        }if(nom.compareTo("charger") == 0){
            Optional<Path> path = fileSelection();

        if (path.isPresent()) {
            ctrl.setJeuTutoriel(path.get());
            }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de sélection");
            alert.setHeaderText(null);
            alert.setContentText("Aucun fichier sélectionné. Veuillez réessayer.");
            alert.showAndWait();
        }
        }

        dialogStage.showAndWait();

    }catch(Exception e){
        System.out.println(e);
        e.printStackTrace();
    }
    }

    public void loadmenu() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation( SokobanApp.class.getResource("view/menu.fxml"));
			
			BorderPane vueListe = loader.load();
			
			menuController ctrl = loader.getController();
            ctrl.setParent(this);
			
			this.rootPane.setCenter( vueListe );
						
		} catch (IOException e) {
			System.out.println("Ressource FXML non disponible : ListeMembres");
			System.exit(1);
		}	
	}

    private void testTouche(KeyEvent event, SokobanController ctrl){
        if (event.getCode() == KeyCode.UP) {
            ctrl.deplacer(Direction.HAUT);
            event.consume();
        } else if (event.getCode() == KeyCode.DOWN) {
            ctrl.deplacer(Direction.BAS);
            event.consume();
        } else if (event.getCode() == KeyCode.LEFT) {
            ctrl.deplacer(Direction.GAUCHE);
            event.consume();
        } else if (event.getCode() == KeyCode.RIGHT) {
            ctrl.deplacer(Direction.DROITE);
            event.consume();
        } else if (event.getCode() == KeyCode.R) {
            ctrl.recommencer();
            event.consume();
        } else if (event.getCode() == KeyCode.Z && event.isControlDown()) {
            ctrl.annuler();
            event.consume();
        }
    }

    private Optional<Path> fileSelection(){
        
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Sélectionner un dossier de niveaux");
        File selectedFile = directoryChooser.showDialog(primaryStage);
        
        return Optional.of(selectedFile.toPath());
    }
}



