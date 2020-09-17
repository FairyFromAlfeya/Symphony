package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.controllers.AudioController;
import main.java.controllers.HomeController;
import main.java.controllers.VideoController;

import java.io.File;

public class Main extends Application {

    private static Stage stage;

    private static Scene homeScene;
    private static Scene audioScene;
    private static Scene videoScene;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;

        FXMLLoader homeLoader = new FXMLLoader(new File("./src/main/resources/view/home_scene.fxml").toURI().toURL());
        FXMLLoader audioLoader = new FXMLLoader(new File("./src/main/resources/view/audio_scene.fxml").toURI().toURL());
        FXMLLoader videoLoader = new FXMLLoader(new File("./src/main/resources/view/video_scene.fxml").toURI().toURL());

        homeScene = new Scene(homeLoader.load());
        audioScene = new Scene(audioLoader.load());
        videoScene = new Scene(videoLoader.load());

        ((HomeController)homeLoader.getController()).setStage(primaryStage);
        ((AudioController)audioLoader.getController()).setStage(primaryStage);
        ((VideoController)videoLoader.getController()).setStage(primaryStage);

        primaryStage.getIcons().add(new Image(new File("./src/main/resources/images/Symphony.png").toURI().toURL().toString()));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Symphony");
        primaryStage.setScene(homeScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void setHomeScene() {
        stage.setScene(homeScene);
        stage.setX(stage.getX() + 222.5);
        homeScene.setCursor(Cursor.DEFAULT);
    }

    public static void setAudioScene() {
        stage.setScene(audioScene);
        stage.setX(stage.getX() - 222.5);
        audioScene.setCursor(Cursor.DEFAULT);
    }

    public static void setVideoScene() {
        stage.setScene(videoScene);
        stage.setX(stage.getX() - 222.5);
        videoScene.setCursor(Cursor.DEFAULT);
    }
}
