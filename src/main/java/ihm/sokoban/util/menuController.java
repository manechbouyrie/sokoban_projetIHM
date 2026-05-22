package ihm.sokoban.util;

import java.net.URL;
import java.util.ResourceBundle;

import ihm.sokoban.SokobanApp;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class menuController implements Initializable{

    private SokobanApp parent;

    @FXML
    private Button buttonTuto;
    @FXML
    private Button buttonSokoban;
    @FXML
    private Button buttonCharger;

    @FXML
    private void lauchSokoban(){
        parent.showSokoban("sokoban");
    }

    @FXML
    private void lauchTutoriel(){
        parent.showSokoban("tutoriel");
    }

    @FXML
    private void lauchCharger(){
        parent.showSokoban("charger");
    }

    public void setParent(SokobanApp parent){
        this.parent = parent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void onCredit(){
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Crédits");
    alert.setHeaderText("Sokoban - IHM 2026");
    alert.setContentText("Développé par :\n- MOI \nMerci d'avoir joué !");

    var resource = getClass().getResourceAsStream("/ihm/sokoban/image/credit.jpg");
    if (resource != null)  {
        ImageView imageView = new ImageView(new Image(resource));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        alert.setGraphic(imageView);
    }

    alert.showAndWait();
}

    @FXML
    private void onRegle(){
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Règles du jeu");
    alert.setHeaderText("Sokoban - IHM 2026");
    alert.setContentText("Chai pas ... j'm'appelle tom l'hamster...............");

    var resource = getClass().getResourceAsStream("/ihm/sokoban/image/regle.jpg");

    ImageView imageView = new ImageView(new Image(resource));
    imageView.setFitWidth(150);
    imageView.setFitHeight(150);
    alert.setGraphic(imageView);

    alert.showAndWait();
    }

    @FXML
    private void onQuitter(){
        parent.Quitter(null);
    }

}
