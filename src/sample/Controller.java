package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private VBox radioPanel;
    @FXML
    private Button efectbutton;
    @FXML
    private Button turnoff;
    @FXML
    private Button volup;
    @FXML
    private Button voldown;
    @FXML
    private Button next;
    @FXML
    private Button pref;
    @FXML
    private ImageView gif;
    @FXML
    private Label display;
    @FXML
    private Button avreg;
    @FXML
    private Button disp;
    @FXML
    private Button func;
    @FXML
    private Button eqxdss;
    @FXML
    private Button pwr;
    @FXML
    private Button band;
    @FXML
    private Button station1;
    @FXML
    private Button station2;
    @FXML
    private Button station3;
    @FXML
    private Button station4;

    private ArrayList<RadioStation> radioStations;
    private int currentFrequency;
    private String currentRadioStation=null;

    //zapisane stacje
    private String savedRadioStation1=null;
    private String savedRadioStation2=null;
    private String savedRadioStation3=null;
    private String savedRadioStation4=null;
    private String savedRadioStation5=null;
    private String savedRadioStation6=null;

    private MediaPlayer noiseMediaPlayer;
    private String musicSource;
    private double musicVolume; //zakres [0.0 , 1.0]
    private long pressTime;

    @FXML
    void changeRadioStation(MouseEvent event) {

        String buttonId=((Button)event.getSource()).getId();

        //tutaj bedzie sprawdzenie czy muzyka ma być odtwarzana z radia czy z płyty

        //mierzenie czasu naciśniecia
        if(event.getEventType().equals(MouseEvent.MOUSE_PRESSED)){
            pressTime = System.currentTimeMillis();
        }
        else{
            if(noiseMediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING))
                noiseMediaPlayer.pause();

            if(System.currentTimeMillis() - pressTime > 1000){

                //zapisanie stacji do przytrzymanego przycisku
                switch(buttonId){
                    case "station1":
                        savedRadioStation1=currentRadioStation;
                        break;
                    case "station2":
                        savedRadioStation2=currentRadioStation;
                        break;
                    case "station3":
                        savedRadioStation3=currentRadioStation;
                        break;
                    case "station4":
                        savedRadioStation4=currentRadioStation;
                        break;
                    case "station5":
                        savedRadioStation5=currentRadioStation;
                        break;
                    case "station6":
                        savedRadioStation6=currentRadioStation;
                        break;
                }

                Media sound = new Media(new File("src\\sample\\songs\\notification.mp3").toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.play();
            }
            else{
                //zmiana częstotliwości radia
                if(buttonId.equals("next") || buttonId.equals("pref")) {

                    int oldFrequency=currentFrequency;
                    if (buttonId.equals("next"))
                        currentFrequency += 1;
                    else
                        currentFrequency -= 1;

                    System.out.println("nowa czestotliwosc: "+currentFrequency);

                    currentRadioStation=null;
                    for(RadioStation rs:radioStations){

                        //włączenie nowej stacji
                        if(rs.getFrequency()==currentFrequency){
                            currentRadioStation=rs.getRadioStationName();
                            rs.getMediaPlayer().setMute(false);
                        }

                        //wyciszenie starej stacji
                        if(rs.getFrequency()==oldFrequency){
                            rs.getMediaPlayer().setMute(true);
                        }
                    }
                }
                else{
                    //właczenie zapisanej wcześniej stacji
                    String oldRadioStation=currentRadioStation;
                    switch(buttonId){
                        case "station1":
                            currentRadioStation=savedRadioStation1;
                            break;
                        case "station2":
                            currentRadioStation=savedRadioStation2;
                            break;
                        case "station3":
                            currentRadioStation=savedRadioStation3;
                            break;
                        case "station4":
                            currentRadioStation=savedRadioStation4;
                            break;
                        case "station5":
                            currentRadioStation=savedRadioStation5;
                            break;
                        case "station6":
                            currentRadioStation=savedRadioStation6;
                            break;
                    }

                    for(RadioStation rs:radioStations){

                        //włączenie nowej stacji
                        if(rs.getRadioStationName().equals(currentRadioStation)){
                            currentFrequency=rs.getFrequency();
                            rs.getMediaPlayer().setMute(false);
                        }

                        //wyciszenie starej stacji
                        if(rs.getRadioStationName().equals(oldRadioStation)){
                            rs.getMediaPlayer().setMute(true);
                        }
                    }
                }

                //nie znaleziono stacji - szum
                if(currentRadioStation==null){
                    noiseMediaPlayer.play();
                }

                //dodać ustawienie globalnej głośności nowej stacji
            }
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        radioStations=new ArrayList<>();
        radioStations.add(new RadioStation("RMF FM",99));
        radioStations.add(new RadioStation("Radio Lodz",102));
        radioStations.add(new RadioStation("Radio Eska",105));
        radioStations.add(new RadioStation("Radio ZET",108));

        currentFrequency=104;
        //wybór źródła muzyki (radio/płyta CD)
        musicSource="radio";

        //zmienic dzwięć na szum radia
        Media sound = new Media(new File("src\\sample\\songs\\noise.mp3").toURI().toString());
        noiseMediaPlayer=new MediaPlayer(sound);
        noiseMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        noiseMediaPlayer.play();
        musicVolume=noiseMediaPlayer.getVolume();
    }
}
