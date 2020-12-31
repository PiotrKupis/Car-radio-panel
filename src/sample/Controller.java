package sample;

import javafx.event.ActionEvent;
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
    void changeMusicSource(ActionEvent event) {

        if(musicSource.equals("radio")){
            musicSource="CD";

            if(noiseMediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING))
                noiseMediaPlayer.pause();
            else{
                for(RadioStation rs:radioStations){
                    if(rs.getRadioStationName().equals(currentRadioStation)){
                        rs.getMediaPlayer().pause();
                    }
                }
            }

            File file = new File("src\\sample\\CD");
            for (File fileEntry : file.listFiles()) {
                System.out.println(fileEntry.getName());
            }
            //TODO dodanie MediaPlayerów do listy i ich odtwarzanie
        }
        else{
            musicSource="radio";

            if(currentRadioStation==null)
                noiseMediaPlayer.play();
            else{
                for(RadioStation rs:radioStations){
                    if(rs.getRadioStationName().equals(currentRadioStation)){
                        rs.getMediaPlayer().play();
                    }
                }
            }
        }
    }

    @FXML
    public void changeVolume(ActionEvent event) {

        String buttonId=((Button)event.getSource()).getId();

        if(buttonId.equals("volup")){
            if(musicVolume<0.9)
                musicVolume+=0.1;
        }
        else{
            if(musicVolume>0.05)
                musicVolume-=0.1;
        }

        if(noiseMediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING))
            noiseMediaPlayer.setVolume(musicVolume);
        else{
            for(RadioStation rs:radioStations){
                if(rs.getRadioStationName().equals(currentRadioStation)){
                    rs.getMediaPlayer().setVolume(musicVolume);
                }
            }
        }
    }
    @FXML
    public void muteRadio(MouseEvent event)
    {
        String buttonId=((Button)event.getSource()).getId();

        if(buttonId.equals("pwr"))
            musicVolume = 0;

        if(noiseMediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING))
            noiseMediaPlayer.setVolume(musicVolume);
        else{
            for(RadioStation rs:radioStations){
                if(rs.getRadioStationName().equals(currentRadioStation)){
                    rs.getMediaPlayer().setVolume(musicVolume);
                }
            }
        }
    }

    private void radioChange(String buttonId){

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
                case "next":
                    findnearbystation();
                    break;
            }

            Media sound = new Media(new File("src\\sample\\songs\\notification.mp3").toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setVolume(musicVolume);
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
                        rs.getMediaPlayer().setVolume(musicVolume);
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
                        rs.getMediaPlayer().setVolume(musicVolume);
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
                noiseMediaPlayer.setVolume(musicVolume);
            }
        }
    }

    @FXML
    void changeRadioStation(MouseEvent event) {

        String buttonId=((Button)event.getSource()).getId();

        //sprawdzenie czy muzyka jest odtwarzana z radia czy z płyty
        if(musicSource.equals("radio")){
            //mierzenie czasu naciśniecia
            if(event.getEventType().equals(MouseEvent.MOUSE_PRESSED)){
                pressTime = System.currentTimeMillis();
            }
            else{
                radioChange(buttonId);
            }
        }
        else{
            //TODO przełacza się na kolejny utwór z listy utworów na płycie
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        radioStations=new ArrayList<>();
        radioStations.add(new RadioStation("RMF FM",99));
        radioStations.add(new RadioStation("Radio Lodz",102));
        radioStations.add(new RadioStation("Radio Eska",105));
        radioStations.add(new RadioStation("Radio ZET",108));
        radioStations.add(new RadioStation("Radio Los Sanots",110));
        radioStations.add(new RadioStation("Radio x",120));
        radioStations.add(new RadioStation("Radio z",130));
        currentFrequency=104;
        //wybór źródła muzyki (radio/płyta CD)
        musicSource="radio";

        //zmienic dzwięć na szum radia
        Media sound = new Media(new File("src\\sample\\songs\\noise.mp3").toURI().toString());
        noiseMediaPlayer=new MediaPlayer(sound);
        noiseMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        noiseMediaPlayer.play();
        noiseMediaPlayer.setVolume(0.5);
        musicVolume=0.5;
    }
    /**
    Metoda do znajdywania najblizej stacji
     */
    private void findnearbystation()
    {
        boolean endwhile = true;
        int oldFrequency=currentFrequency;
        currentRadioStation=null;
        while (endwhile)
        {
            // sprawdzanie zakresu czestotliwosci
            currentFrequency++;
            if(currentFrequency>130)
                currentFrequency = 80;

            for(RadioStation rs:radioStations)
            {
                //wyciszenie starej stacji
                if(rs.getFrequency()==oldFrequency){
                    rs.getMediaPlayer().setMute(true);
                }

                //włączenie nowej stacji
                if(rs.getFrequency()==currentFrequency){
                    currentRadioStation=rs.getRadioStationName();
                    rs.getMediaPlayer().setVolume(musicVolume);
                    rs.getMediaPlayer().setMute(false);
                    endwhile = false;
                    break;

                }
            }
        }

    }
}
