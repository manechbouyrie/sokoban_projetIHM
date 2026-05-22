package ihm.sokoban;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import ihm.sokoban.model.Direction;
import ihm.sokoban.util.SokobanController;
import ihm.sokoban.util.menuController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
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
    primaryStage.setOnCloseRequest(event -> Quitter(event)); // Appelle la méthode de confirmation
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
        ctrl.setSokobanApp(this);
        dialogStage.showAndWait();

    }catch(Exception e){
        marchePas();
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
            marchePas();
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

    public Dialog<ButtonType> getPrimaryStage() {
        throw new UnsupportedOperationException("Unimplemented method 'getPrimaryStage'");
    }

    public void Quitter(Event event) {
    
    var resource = getClass().getResourceAsStream("/ihm/sokoban/image/quiter.jpg");

    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
    confirm.setTitle("Quitter");
    confirm.setHeaderText("On part déja ? (mental de défaitiste)");
    ImageView imageView = new ImageView(new Image(resource));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        confirm.setGraphic(imageView);

    ButtonType btnOui = new ButtonType("Oui");
    ButtonType btnNon = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
    confirm.getButtonTypes().setAll(btnOui, btnNon);

    Optional<ButtonType> result = confirm.showAndWait();
    if (result.isPresent() && result.get() == btnOui) {
        Platform.exit();
        System.exit(0);
    } else {
        event.consume();
    }
}

    private void marchePas(){

        var resource = getClass().getResourceAsStream("/ihm/sokoban/image/marchepas.jpg");
        Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sa marche pas !");
            alert.setHeaderText(null);
            ImageView imageView = new ImageView(new Image(resource));
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            alert.setGraphic(imageView);
            alert.showAndWait();
    }


}



