package sample;

import com.sun.source.tree.Tree;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class Controller implements Initializable {

    @FXML
    private VBox radioPanel;

    private TreeMap<Integer,String> radioStations;
    private String currentRadioStation;

    //zapisane stacje
    private String savedRadioStation1=null;
    private String savedRadioStation2=null;
    private String savedRadioStation3=null;
    private String savedRadioStation4=null;
    private String savedRadioStation5=null;
    private String savedRadioStation6=null;

    private MediaPlayer mediaPlayer;
    private String musicSource;
    private double musicVolume; //zakres [0.0 , 1.0]

    @FXML
    public void saveCurrentRadioStation(ActionEvent event){

    }

    @FXML
    public void changeRadioStation(ActionEvent event){

    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //inicjalizacja danych stacji
        radioStations=new TreeMap<>();
        radioStations.put(99,"RMF FM");
        radioStations.put(102,"Radio Łódź");
        radioStations.put(105,"Radio Eska");
        radioStations.put(108,"Radio ZET");
        currentRadioStation="Radio Łódź";

        //wybór źródła muzyki (radio/płyta CD)
        musicSource="radio";

        Media sound = new Media(new File("src\\sample\\RubicTurnSound.mp3").toURI().toString());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();

    }
}
