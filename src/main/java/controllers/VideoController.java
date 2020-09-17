package main.java.controllers;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.java.Main;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class VideoController implements Initializable {

    private static double TIME_BAR_ONE_PERCENT_PIXELS = 8.15;
    private final static double VOLUME_BAR_ONE_PERCENT_PIXELS = 1.15;

    private final ImageView playImage = new ImageView(new Image(new File("./src/main/resources/images/Play.png").toURI().toURL().toString()));
    private final ImageView pauseImage = new ImageView(new Image(new File("./src/main/resources/images/Pause.png").toURI().toURL().toString()));

    private Stage stage;
    private double xOffset;
    private double yOffset;

    @FXML private Canvas timeBar;
    @FXML private Canvas volumeBar;
    @FXML private Button playButton;
    @FXML private Button nextButton;
    @FXML private Button prevButton;
    @FXML private MediaView videoPlayer;
    @FXML private AnchorPane menu;
    @FXML private AnchorPane controlButtons;
    @FXML private AnchorPane headBar;
    @FXML private AnchorPane homeButtonContainer;

    private GraphicsContext timeContext;
    private GraphicsContext volumeContext;

    private final FileChooser fileChooser = new FileChooser();
    private File film;

    private MediaPlayer mediaPlayer;
    private double trackOnePercentSeconds;
    private double volume = 0.5;

    private boolean timeWasDragged = false;
    private boolean isTimeChanging = false;

    PauseTransition hideMenu = new PauseTransition(Duration.seconds(5));

    private final InvalidationListener timeListener = listener -> {
        if (!isTimeChanging) {
            timeContext.setFill(Color.web("#4c4c4c"));
            timeContext.fillRect(0, 0, timeBar.getWidth(), timeBar.getHeight());

            timeContext.setFill(Color.YELLOW);
            timeContext.fillRect(0, 0, mediaPlayer.getCurrentTime().toSeconds() / trackOnePercentSeconds * TIME_BAR_ONE_PERCENT_PIXELS, timeBar.getHeight());
        }
    };

    private final Runnable stopPlayingListener = () -> {
        if (!isTimeChanging) {
            timeContext.setFill(Color.YELLOW);
            timeContext.fillRect(0, 0, timeBar.getWidth(), timeBar.getHeight());
        }

        playButton.setGraphic(pauseImage);
    };

    public VideoController() throws MalformedURLException {
        fileChooser.setTitle("Choose film");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timeContext = timeBar.getGraphicsContext2D();
        volumeContext = volumeBar.getGraphicsContext2D();

        timeContext.setFill(Color.web("#4c4c4c"));
        timeContext.fillRect(0, 0, timeBar.getWidth(), timeBar.getHeight());

        volumeContext.setFill(Color.web("#4c4c4c"));
        volumeContext.fillRect(0, 0, volumeBar.getWidth(), volumeBar.getHeight());

        volumeContext.setFill(Color.YELLOW);
        volumeContext.fillRect(0, 0, volume * 100 * VOLUME_BAR_ONE_PERCENT_PIXELS, volumeBar.getHeight());

        hideMenu.setOnFinished(value -> {
            menu.setVisible(false);
            videoPlayer.setCursor(Cursor.NONE);
        });
    }

    public final void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void homeKeyTyped(KeyEvent e) {
        if (e.getCharacter().equals("f")) {
            if (stage.isFullScreen()) {
                stage.setFullScreen(false);

                videoPlayer.setFitWidth(845.0);
                videoPlayer.setFitHeight(475.0);

                AnchorPane.setRightAnchor(nextButton, 373.0);
                AnchorPane.setRightAnchor(playButton, 413.0);
                AnchorPane.setRightAnchor(prevButton, 448.0);

                menu.setStyle("-fx-pref-width: 845px");

                timeBar.setWidth(815.0);
                TIME_BAR_ONE_PERCENT_PIXELS = 8.15;

                timeContext.setFill(Color.web("#4c4c4c"));
                timeContext.fillRect(0, 0, timeBar.getWidth(), timeBar.getHeight());

                controlButtons.setVisible(true);
                headBar.setVisible(true);
                homeButtonContainer.setVisible(true);
            } else {
                stage.setFullScreen(true);

                videoPlayer.setFitWidth(1920.0);
                videoPlayer.setFitHeight(1080.0);

                AnchorPane.setRightAnchor(nextButton, 910.0);
                AnchorPane.setRightAnchor(playButton, 950.0);
                AnchorPane.setRightAnchor(prevButton, 985.0);

                menu.setStyle("-fx-pref-width: 1920px");

                timeBar.setWidth(1890.0);
                TIME_BAR_ONE_PERCENT_PIXELS = 18.9;

                timeContext.setFill(Color.web("#4c4c4c"));
                timeContext.fillRect(0, 0, timeBar.getWidth(), timeBar.getHeight());

                controlButtons.setVisible(false);
                headBar.setVisible(false);
                homeButtonContainer.setVisible(false);
            }
        }
    }

    @FXML
    private void mediaMoved() {
        if (film != null) {
            menu.setVisible(true);
            videoPlayer.setCursor(Cursor.DEFAULT);
            hideMenu.playFromStart();
        }
    }

    @FXML
    private void menuEntered() {
        if (film != null)
            hideMenu.stop();
    }

    @FXML
    private void menuExited() {
        if (film != null)
            hideMenu.play();
    }

    @FXML
    private void volumeMoved(MouseEvent e) {
        volumeBar.setCursor(Cursor.HAND);

        volumeContext.setFill(Color.web("#4c4c4c"));
        volumeContext.fillRect(0, 0, volumeBar.getWidth(), volumeBar.getHeight());

        volumeContext.setFill(Color.YELLOW);
        volumeContext.fillRect(0, 0, e.getX(), volumeBar.getHeight());
    }

    @FXML
    private void volumeExited() {
        volumeBar.setCursor(Cursor.DEFAULT);

        volumeContext.setFill(Color.web("#4c4c4c"));
        volumeContext.fillRect(0, 0, volumeBar.getWidth(), volumeBar.getHeight());

        volumeContext.setFill(Color.YELLOW);
        volumeContext.fillRect(0, 0, volume * 100 * VOLUME_BAR_ONE_PERCENT_PIXELS, volumeBar.getHeight());
    }

    @FXML
    private void volumeDragged(MouseEvent e) {
        setVolume(e.getX());
    }

    @FXML
    private void volumeClicked(MouseEvent e) {
        setVolume(e.getX());
    }

    @FXML
    private void timeMoved(MouseEvent e) {
        if (mediaPlayer != null) {
            timeBar.setCursor(Cursor.HAND);

            timeContext.setFill(Color.web("#4c4c4c"));
            timeContext.fillRect(0, 0, timeBar.getWidth(), timeBar.getHeight());

            timeContext.setFill(Color.YELLOW);
            timeContext.fillRect(0, 0, e.getX(), timeBar.getHeight());
        }
    }

    @FXML
    private void timeEntered() {
        isTimeChanging = true;
    }

    @FXML
    private void timeExited() {
        timeBar.setCursor(Cursor.DEFAULT);
        isTimeChanging = false;

        if (mediaPlayer != null) {
            if (mediaPlayer.getCurrentTime().toMillis() == mediaPlayer.getTotalDuration().toMillis()) {
                timeContext.setFill(Color.YELLOW);
                timeContext.fillRect(0, 0, timeBar.getWidth(), timeBar.getHeight());
            }

            if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                timeContext.setFill(Color.web("#4c4c4c"));
                timeContext.fillRect(0, 0, timeBar.getWidth(), timeBar.getHeight());

                timeContext.setFill(Color.YELLOW);
                timeContext.fillRect(0, 0, mediaPlayer.getCurrentTime().toSeconds() / trackOnePercentSeconds * TIME_BAR_ONE_PERCENT_PIXELS, timeBar.getHeight());
            }

            mediaPlayer.currentTimeProperty().addListener(timeListener);
        }
    }

    @FXML
    private void timeDragged(MouseEvent e) {
        if (mediaPlayer != null) {
            timeWasDragged = true;
            setTime(e.getX());
        }
    }

    @FXML
    private void timeReleased(MouseEvent e) {
        if (mediaPlayer != null)
            if (!timeWasDragged)
                setTime(e.getX());
            else
                timeWasDragged = false;
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
    private void homeClicked() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            playButton.setGraphic(pauseImage);
        }

        Main.setHomeScene();
    }

    @FXML
    private void addClicked() {
        addFilm();
    }

    @FXML
    private void playClicked() {
        if (mediaPlayer != null) {
            if (mediaPlayer.getTotalDuration().toMillis() == mediaPlayer.getCurrentTime().toMillis()) {
                setTime(0.0);
                playButton.setGraphic(playImage);
            } else {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.pause();
                    playButton.setGraphic(pauseImage);
                } else {
                    mediaPlayer.play();
                    playButton.setGraphic(playImage);
                }
            }
        } else {
            if (film == null)
                addFilm();
            else
                playFilm();
        }
    }

    @FXML
    private void nextClicked() {
        if (mediaPlayer != null)
            setTime((mediaPlayer.getCurrentTime().toSeconds() + 10.0) / trackOnePercentSeconds * TIME_BAR_ONE_PERCENT_PIXELS);
    }

    @FXML
    private void prevClicked() {
        if (mediaPlayer != null)
            setTime((mediaPlayer.getCurrentTime().toSeconds() - 10.0) / trackOnePercentSeconds * TIME_BAR_ONE_PERCENT_PIXELS);
    }

    private void playFilm() {
        if (playButton.getGraphic() == pauseImage)
            playButton.setGraphic(playImage);

        Media filmMedia = new Media(film.toURI().toString());
        mediaPlayer = new MediaPlayer(filmMedia);
        videoPlayer.setMediaPlayer(mediaPlayer);

        mediaPlayer.onEndOfMediaProperty().set(stopPlayingListener);
        mediaPlayer.setOnReady(() -> trackOnePercentSeconds = mediaPlayer.getTotalDuration().toSeconds() / 100.0);
        mediaPlayer.currentTimeProperty().addListener(timeListener);
        mediaPlayer.setVolume(volume);
        mediaPlayer.play();
    }

    private double clip(double value, double min, double max) {
        if (value > max)
            value = max;

        if (value < min)
            value = min;

        return value;
    }

    private void setVolume(double volumeBarXPosition) {
        volumeBarXPosition = clip(volumeBarXPosition, 0.0, 115.0);
        volume = volumeBarXPosition / VOLUME_BAR_ONE_PERCENT_PIXELS / 100.0;

        if (mediaPlayer != null)
            mediaPlayer.setVolume(volume);

        volumeContext.setFill(Color.web("#4c4c4c"));
        volumeContext.fillRect(0, 0, volumeBar.getWidth(), volumeBar.getHeight());

        volumeContext.setFill(Color.YELLOW);
        volumeContext.fillRect(0, 0, volumeBarXPosition, volumeBar.getHeight());
    }

    private void setTime(double timeBarXPosition) {
        if (stage.isFullScreen())
            timeBarXPosition = clip(timeBarXPosition, 0.0, 1890.0);
        else
            timeBarXPosition = clip(timeBarXPosition, 0.0, 815.0);

        mediaPlayer.seek(new Duration(timeBarXPosition / TIME_BAR_ONE_PERCENT_PIXELS * trackOnePercentSeconds * 1000));

        timeContext.setFill(Color.web("#4c4c4c"));
        timeContext.fillRect(0, 0, timeBar.getWidth(), timeBar.getHeight());

        timeContext.setFill(Color.YELLOW);
        timeContext.fillRect(0, 0, timeBarXPosition, timeBar.getHeight());
    }

    private void addFilm() {
        File filmFile = fileChooser.showOpenDialog(stage);

        if (filmFile != null) {
            if (mediaPlayer != null) {
                mediaPlayer.currentTimeProperty().removeListener(timeListener);
                mediaPlayer.stop();

                timeContext.setFill(Color.web("#4c4c4c"));
                timeContext.fillRect(0, 0, timeBar.getWidth(), timeBar.getHeight());

                mediaPlayer = null;
                videoPlayer.setMediaPlayer(null);
            }
            film = filmFile;
            hideMenu.play();
        }
    }
}
