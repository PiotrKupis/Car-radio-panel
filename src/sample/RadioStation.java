package sample;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

public class RadioStation {

    private String radioStationName;
    private int frequency;
    private MediaPlayer mediaPlayer;

    public RadioStation(String radioStationName, int frequency) {
        this.radioStationName = radioStationName;
        this.frequency = frequency;

        Media sound = new Media(new File("src\\sample\\songs\\" + radioStationName + ".mp3").toURI().toString());
        this.mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
        mediaPlayer.setMute(true);
    }

    public String getRadioStationName() {
        return radioStationName;
    }

    public int getFrequency() {
        return frequency;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
