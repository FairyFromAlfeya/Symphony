package main.java.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.java.Main;

public class HomeController {

    private Stage stage;

    private double xOffset;
    private double yOffset;

    public final void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void headPressed(MouseEvent e) {
        xOffset = e.getSceneX();
        yOffset = e.getSceneY();
    }

    @FXML
    private void headDragged(MouseEvent e) {
        stage.setX(e.getScreenX() - xOffset);
        stage.setY(e.getScreenY() - yOffset);
    }

    @FXML
    private void exitClicked() {
        Platform.exit();
    }

    @FXML
    private void iconifyClicked() {
        stage.setIconified(true);
    }

    @FXML
    private void audioClicked() {
        Main.setAudioScene();
    }

    @FXML
    private void videoClicked() {
        Main.setVideoScene();
    }
}
