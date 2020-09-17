package main.java.controllers;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.java.Main;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AudioController implements Initializable {

    private final static double TIME_BAR_ONE_PERCENT_PIXELS = 4.0;
    private final static double VOLUME_BAR_ONE_PERCENT_PIXELS = 1.15;

    private final Image removeImage = new Image(new File("./src/main/resources/images/Remove.png").toURI().toURL().toString());
    private final Image defaultAlbumCover = new Image(new File("./src/main/resources/images/Album.png").toURI().toURL().toString());

    private final ImageView playImage = new ImageView(new Image(new File("./src/main/resources/images/Play.png").toURI().toURL().toString()));
    private final ImageView pauseImage = new ImageView(new Image(new File("./src/main/resources/images/Pause.png").toURI().toURL().toString()));

    private Stage stage;
    private double xOffset;
    private double yOffset;

    @FXML private ListView<AnchorPane> musicList;
    @FXML private ImageView albumCover;
    @FXML private Canvas timeBar;
    @FXML private Canvas volumeBar;
    @FXML private Button playButton;

    private GraphicsContext timeContext;
    private GraphicsContext volumeContext;

    private final FileChooser fileChooser = new FileChooser();
    private final List<File> fileList = new ArrayList<>();

    private MediaPlayer mediaPlayer;
    private int currentTrack = 0;
    private double trackOnePercentSeconds;
    private double volume = 0.5;

    private boolean timeWasDragged = false;
    private boolean isTimeChanging = false;

    private final InvalidationListener timeListener = listener -> {
        if (!isTimeChanging) {
            timeContext.setFill(Color.web("#4c4c4c"));
            timeContext.fillRect(0, 0, timeBar.getWidth(), timeBar.getHeight());

            timeContext.setFill(Color.YELLOW);
            timeContext.fillRect(0, 0, mediaPlayer.getCurrentTime().toSeconds() / trackOnePercentSeconds * TIME_BAR_ONE_PERCENT_PIXELS, timeBar.getHeight());
        }
    };

    private final Runnable nextTrackListener = () -> {
        if (!isTimeChanging) {
            timeContext.setFill(Color.YELLOW);
            timeContext.fillRect(0, 0, timeBar.getWidth(), timeBar.getHeight());
        }

        currentTrack++;
        playTrack(currentTrack);
    };

    private final Runnable stopPlayingListener = () -> {
        if (!isTimeChanging) {
            timeContext.setFill(Color.YELLOW);
            timeContext.fillRect(0, 0, timeBar.getWidth(), timeBar.getHeight());
        }

        playButton.setGraphic(pauseImage);
    };

    public AudioController() throws MalformedURLException {
        fileChooser.setTitle("Choose tracks");
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
    }

    public final void setStage(Stage stage) {
        this.stage = stage;
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
        addTracks();
    }

    @FXML
    private void playClicked() {
        if (mediaPlayer != null) {
            if ((currentTrack == fileList.size() - 1) && (mediaPlayer.getTotalDuration().toMillis() == mediaPlayer.getCurrentTime().toMillis())) {
                currentTrack = 0;
                playTrack(currentTrack);
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
            if (fileList.isEmpty())
                addTracks();
            else
                playTrack(currentTrack);
        }
    }

    @FXML
    private void nextClicked() {
        if (!fileList.isEmpty()) {
            currentTrack++;
            playTrack(currentTrack);
        }
    }

    @FXML
    private void prevClicked() {
        if (!fileList.isEmpty()) {
            currentTrack--;
            playTrack(currentTrack);
        }
    }

    /**
     *  Add new node to musicList(ListView)
     */
    private void addNodeToMusicList(Media track) {
        ObservableMap<String, Object> data = track.getMetadata();

        AnchorPane outer = new AnchorPane();
        AnchorPane inner = new AnchorPane();

        Label artist = new Label();
        Label title = new Label();

        inner.getStyleClass().add("inner");

        Button remove = new Button("", new ImageView(removeImage));

        inner.getChildren().addAll(artist, title, remove);
        outer.getChildren().add(inner);
        musicList.getItems().add(outer);

        AnchorPane.setTopAnchor(inner, 15.0);
        AnchorPane.setLeftAnchor(inner, 15.0);

        AnchorPane.setRightAnchor(remove, 0.0);
        AnchorPane.setTopAnchor(remove, 0.0);

        AnchorPane.setTopAnchor(artist, 0.0);
        AnchorPane.setLeftAnchor(artist, 0.0);

        AnchorPane.setTopAnchor(title, 21.0);
        AnchorPane.setLeftAnchor(title, 0.0);

        outer.setOnMouseMoved(value -> outer.setCursor(Cursor.HAND));

        outer.setOnMouseClicked(value -> {
            currentTrack = musicList.getItems().indexOf(outer);
            playTrack(currentTrack);
        });

        data.addListener((MapChangeListener<String, Object>) change -> {
            if (data.get("artist") != null)
                if (artist.getText().isEmpty())
                    artist.setText(data.get("artist").toString().toUpperCase());

            if (data.get("title") != null)
                if (title.getText().isEmpty())
                    title.setText(data.get("title").toString().toUpperCase());
        });

        remove.setOnMouseClicked(value -> {
            int nodeIndex = musicList.getItems().indexOf(outer);

            if (currentTrack == nodeIndex) {
                mediaPlayer.currentTimeProperty().removeListener(timeListener);

                if (fileList.size() > 1) {
                    currentTrack++;
                    playTrack(currentTrack);
                } else {
                    mediaPlayer.stop();
                    mediaPlayer = null;

                    timeContext.setFill(Color.web("#4c4c4c"));
                    timeContext.fillRect(0, 0, timeBar.getWidth(), timeBar.getHeight());

                    albumCover.setImage(defaultAlbumCover);
                }
            }

            if ((musicList.getItems().size() != 1) && (nodeIndex == musicList.getItems().size() - 1)) {
                musicList.getItems()
                        .get(nodeIndex - 1)
                        .getChildren()
                        .get(0)
                        .setStyle("-fx-border-width: 0");
            }

            if (nodeIndex < currentTrack)
                currentTrack--;

            fileList.remove(nodeIndex);
            musicList.getItems().remove(nodeIndex);

            value.consume();
        });
    }

    /**
     *  1) Stop mediaPlayer(MediaPlayer) if it's playing
     *  2) Marked track in musicList(ListView) as selected (if track isn't marked)
     *  3) Set playImage(ImageView) to playButton if was pauseImage(ImageView)
     *  4) Set new track(Media) to mediaPlayer(MediaPlayer)
     *  5) Set album cover of file to albumCover(ImageView) or defaultAlbumCover(Image)(if file hasn't image)
     *  6) Fully fill timeBar(Canvas) if user hasn't hovered cursor over it
     *  7) Estimate trackOnePercentSeconds
     *  8) Set volume
     *  9) Starts playing track
     *  10) Stops playing in the end
     */
    private void playTrack(int positionInFileList) {
        if (positionInFileList >= fileList.size())
            currentTrack = 0;
        else if (positionInFileList < 0)
            currentTrack = fileList.size() - 1;

        positionInFileList = currentTrack;

        if (mediaPlayer != null)
            mediaPlayer.stop();

        if (musicList.getSelectionModel().getSelectedIndex() != positionInFileList)
            musicList.getSelectionModel().select(positionInFileList);

        if (playButton.getGraphic() == pauseImage)
            playButton.setGraphic(playImage);

        Media track = new Media(fileList.get(positionInFileList).toURI().toString());
        mediaPlayer = new MediaPlayer(track);
        ObservableMap<String, Object> data = track.getMetadata();
        albumCover.setImage(defaultAlbumCover);

        data.addListener((MapChangeListener<String, Object>) change -> {
            if (data.get("image") != null)
                if (albumCover.getImage() != data.get("image"))
                    albumCover.setImage((Image)data.get("image"));
        });

        if (positionInFileList != fileList.size() - 1)
            mediaPlayer.onEndOfMediaProperty().set(nextTrackListener);
        else
            mediaPlayer.onEndOfMediaProperty().set(stopPlayingListener);

        mediaPlayer.setOnReady(() -> trackOnePercentSeconds = mediaPlayer.getTotalDuration().toSeconds() / 100.0);
        mediaPlayer.currentTimeProperty().addListener(timeListener);
        mediaPlayer.setVolume(volume);
        mediaPlayer.play();
    }

    /**
     *  Clip value in range [min; max]
     */
    private double clip(double value, double min, double max) {
        if (value > max)
            value = max;

        if (value < min)
            value = min;

        return value;
    }

    /**
     *  Set volume instantly to mediaPlayer(MediaPlayer) and draw volume progress to volumeBar(Canvas)
     */
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

    /**
     *  Set time instantly to mediaPlayer(MediaPlayer) and draw time progress to timeBar(Canvas)
     */
    private void setTime(double timeBarXPosition) {
        timeBarXPosition = clip(timeBarXPosition, 0.0, 400.0);

        mediaPlayer.seek(new Duration(timeBarXPosition / TIME_BAR_ONE_PERCENT_PIXELS * trackOnePercentSeconds * 1000));

        timeContext.setFill(Color.web("#4c4c4c"));
        timeContext.fillRect(0, 0, timeBar.getWidth(), timeBar.getHeight());

        timeContext.setFill(Color.YELLOW);
        timeContext.fillRect(0, 0, timeBarXPosition, timeBar.getHeight());
    }

    /**
     * Adding tracks to musicList(ListView) and fileList(ArrayList)
     */
    private void addTracks() {
        List<File> list = fileChooser.showOpenMultipleDialog(stage);

        if (list != null) {
            fileList.addAll(list);

            int musicListSize = musicList.getItems().size();

            if (musicListSize != 0) {
                musicList.getItems()
                        .get(musicListSize - 1)
                        .getChildren()
                        .get(0)
                        .setStyle("-fx-border-width: 0 0 3px 0");
            }

            for (File file : list) {
                Media track = new Media(file.toURI().toString());
                addNodeToMusicList(track);
            }

            musicListSize = musicList.getItems().size();

            musicList.getItems()
                    .get(musicListSize - 1)
                    .getChildren()
                    .get(0)
                    .setStyle("-fx-border-width: 0");
        }
    }
}
