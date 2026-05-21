package ihm.sokoban;

import ihm.sokoban.model.Direction;
import ihm.sokoban.util.SokobanController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Application JavaFX principale pour le jeu Sokoban.
 */
public class SokobanApp extends Application {

    @Override
public void start(Stage primaryStage) throws Exception {
    
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ihm/sokoban/sokoban.fxml"));
    Parent root = loader.load();
    SokobanController ctrl = loader.getController();

    Scene scene = new Scene(root);

    scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
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
    });

    primaryStage.setTitle("Sokoban");
    primaryStage.setScene(scene);
    primaryStage.show();
}

    public static void main2(String[] args) {
        launch(args);
    }
}
