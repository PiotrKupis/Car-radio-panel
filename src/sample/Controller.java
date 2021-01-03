package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private ArrayList<RadioStation> radioStations;
    private int currentFrequency;
    private String currentRadioStation=null;
    private MediaPlayer noiseMediaPlayer;

    private ArrayList<String> cdSongTitles;
    private MediaPlayer cdMediaPlayer;
    private int cdSongDurationInSeconds;
    private Integer cdSongNumber=0;
    private Thread threadShowingSongTime;
    private static boolean applicationClosed=false;
    private boolean isMuted=false;
    private double savedVolume;

    private String musicSource;
    private double musicVolume; //zakres [0.0 , 1.0]
    private long pressTime;

    //zapisane stacje
    private String savedRadioStation1=null;
    private String savedRadioStation2=null;
    private String savedRadioStation3=null;
    private String savedRadioStation4=null;
    private String savedRadioStation5=null;
    private String savedRadioStation6=null;

    private int savedRadioStationFrequency1=109;
    private int savedRadioStationFrequency2=109;
    private int savedRadioStationFrequency3=109;
    private int savedRadioStationFrequency4=109;
    private int savedRadioStationFrequency5=109;
    private int savedRadioStationFrequency6=109;

    /**
     * Metoda odtwarzająca utwór z płyty CD
     */
    private void playCdSong(){

        Media sound = new Media(new File("src\\sample\\CD\\"+cdSongTitles.get(cdSongNumber)+".mp3").toURI().toString());
        cdMediaPlayer.pause();
        cdMediaPlayer = new MediaPlayer(sound);
        cdMediaPlayer.play();

        cdMediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                cdSongDurationInSeconds= (int) sound.getDuration().toSeconds();
            }
        });

        //wyświetlanie czasu odtwarzania
        threadShowingSongTime=new Thread()
        {
            public void run() {

                int seconds=1,minutes=0,localCdSongNumber=cdSongNumber,totalSeconds=1;

                try {
                    Thread.sleep(1000);
                    while(localCdSongNumber==cdSongNumber && !applicationClosed && musicSource.equals("CD")){

                        int finalSeconds = seconds;
                        int finalMinutes = minutes;
                        Platform.runLater(new Runnable() {
                            @Override public void run() {
                                setText(String.format("    %02d     %d:%02d" , cdSongNumber+1, finalMinutes, finalSeconds));
                            }
                        });

                        Thread.sleep(1000);
                        seconds++;
                        totalSeconds++;

                        if(seconds==60){
                            seconds=0;
                            minutes++;
                        }

                        //odtwarzanie nastepnego utworu
                        if(totalSeconds==cdSongDurationInSeconds){
                            synchronized (cdSongNumber){
                                cdSongNumber++;
                                if(cdSongNumber==cdSongTitles.size())
                                    cdSongNumber=0;
                                playCdSong();
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        threadShowingSongTime.start();
    }

    /**
     * Metoda zmieniająca informacje o tym skąd radio ma odtwarzać utwory
     * @param event
     */
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

            displayAfterDelay("   READING","   READING",1000);
            playCdSong();
        }
        else{
            musicSource="radio";
            cdMediaPlayer.pause();

            displayAfterDelay("    "+currentFrequency+"    FM",currentRadioStation,1000);
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

    /**
     * Metoda zmieniająca głośność radia
     * @param event
     */
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

        if(musicSource.equals("radio")){
            displayAfterDelay(String.format("    VOL     %02d" , (int)(Math.ceil(musicVolume*10))),currentRadioStation,1000);
            if(currentRadioStation==null)
                displayAfterDelay(String.format("    VOL     %02d" , (int)(Math.ceil(musicVolume*10))),"    "+currentFrequency+"    FM",1000);
        }
        else{
            for(int i=1;i<20;++i){
                displayAfterDelay(String.format("    VOL     %02d" , (int)(Math.ceil(musicVolume*10))),String.format("    VOL     %02d" , (int)(Math.ceil(musicVolume*10))),i*10);
            }
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
        cdMediaPlayer.setVolume(musicVolume);
    }

    /**
     * Metoda wyciszajaca oraz odciszajaca radio
     * @param event
     */
    @FXML
    public void muteRadio(MouseEvent event)
    {
        if(isMuted) {
            isMuted=false;
            musicVolume = savedVolume;

            if(musicSource.equals("radio")){
                displayAfterDelay(String.format("    VOL     %02d" , (int)(Math.ceil(musicVolume*10))),currentRadioStation,1000);

                if(currentRadioStation==null)
                    displayAfterDelay(String.format("    VOL     %02d" , (int)(Math.ceil(musicVolume*10))),"    "+currentFrequency+"    FM",1000);
            }
            else{
                for(int i=1;i<20;++i){
                    displayAfterDelay(String.format("    VOL     %02d" , (int)(Math.ceil(musicVolume*10))),String.format("    VOL     %02d" , (int)(Math.ceil(musicVolume*10))),i*10);
                }
            }
        }
        else{
            savedVolume=musicVolume;
            musicVolume=0;
            isMuted=true;

            for(int i=1;i<20;++i){
                displayAfterDelay("     MUTE ON","     MUTE ON",i*10);
            }
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
        cdMediaPlayer.setVolume(musicVolume);
    }

    /**
     * Metoda zmieniająca obecnie odtwarzaną stacje radiową
     * @param buttonId
     */
    private void radioChange(String buttonId){

        if(noiseMediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING))
            noiseMediaPlayer.pause();

        if(System.currentTimeMillis() - pressTime > 1000){

            //zapisanie stacji do przytrzymanego przycisku lub zmiana stacji automat
            switch(buttonId){
                case "station1":
                    savedRadioStation1=currentRadioStation;
                    savedRadioStationFrequency1=currentFrequency;
                    break;
                case "station2":
                    savedRadioStation2=currentRadioStation;
                    savedRadioStationFrequency2=currentFrequency;
                    break;
                case "station3":
                    savedRadioStation3=currentRadioStation;
                    savedRadioStationFrequency3=currentFrequency;
                    break;
                case "station4":
                    savedRadioStation4=currentRadioStation;
                    savedRadioStationFrequency4=currentFrequency;
                    break;
                case "station5":
                    savedRadioStation5=currentRadioStation;
                    savedRadioStationFrequency5=currentFrequency;
                    break;
                case "station6":
                    savedRadioStation6=currentRadioStation;
                    savedRadioStationFrequency6=currentFrequency;
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

                displayAfterDelay("    "+currentFrequency+"    FM",currentRadioStation,1000);
            }
            else{
                //właczenie zapisanej wcześniej stacji
                String oldRadioStation=currentRadioStation;
                switch(buttonId){
                    case "station1":
                        currentRadioStation=savedRadioStation1;
                        currentFrequency=savedRadioStationFrequency1;
                        break;
                    case "station2":
                        currentRadioStation=savedRadioStation2;
                        currentFrequency=savedRadioStationFrequency2;
                        break;
                    case "station3":
                        currentRadioStation=savedRadioStation3;
                        currentFrequency=savedRadioStationFrequency3;
                        break;
                    case "station4":
                        currentRadioStation=savedRadioStation4;
                        currentFrequency=savedRadioStationFrequency4;
                        break;
                    case "station5":
                        currentRadioStation=savedRadioStation5;
                        currentFrequency=savedRadioStationFrequency5;
                        break;
                    case "station6":
                        currentRadioStation=savedRadioStation6;
                        currentFrequency=savedRadioStationFrequency6;
                        break;
                }

                if(currentRadioStation!=null)
                    displayAfterDelay(buttonId,currentRadioStation,1000);
                else
                    displayAfterDelay(buttonId,"    "+currentFrequency+"    FM",1000);


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

            //nie znaleziono stacji
            if(currentRadioStation==null){
                noiseMediaPlayer.play();
                noiseMediaPlayer.setVolume(musicVolume);
                setText("    "+currentFrequency+"    FM");
            }
        }
    }

    /**
     * Metoda obsługująca przyciski zmiany stacji oraz utworu odtwarzanego z płyty CD
     * @param event
     */
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
        else if(event.getEventType().equals(MouseEvent.MOUSE_PRESSED)){

            if(buttonId.equals("next") || buttonId.equals("pref")) {
                synchronized (cdSongNumber){
                    if(buttonId.equals("next")){
                        cdSongNumber++;
                        if(cdSongNumber==cdSongTitles.size())
                            cdSongNumber=0;
                    }
                    else{
                        cdSongNumber--;
                        if(cdSongNumber==-1)
                            cdSongNumber=cdSongTitles.size()-1;
                    }
                }
                playCdSong();
            }
        }
    }

    /**
     * Metoda inicjalizujaca dane potrzebne do działania aplikacji
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Media sound;

        //inicjalizacja radia
        radioStations=new ArrayList<>();
        radioStations.add(new RadioStation("RMF FM",99));
        radioStations.add(new RadioStation("Radio Lodz",102));
        radioStations.add(new RadioStation("Radio Eska",105));
        radioStations.add(new RadioStation("Radio ZET",108));
        radioStations.add(new RadioStation("Los Santos",110));
        radioStations.add(new RadioStation("Radio x",120));
        radioStations.add(new RadioStation("Radio z",130));

        //inicjalizacja płyty CD
        cdSongTitles=new ArrayList<>();
        File file = new File("src\\sample\\CD");
        for (File fileEntry : file.listFiles()) {
            String fileName=fileEntry.getName();
            cdSongTitles.add(fileName.substring(0,fileName.length()-4));
        }

        sound = new Media(new File("src\\sample\\CD\\"+cdSongTitles.get(cdSongNumber)+".mp3").toURI().toString());
        cdMediaPlayer=new MediaPlayer(sound);

        //wybór źródła muzyki (radio/CD)
        musicSource="radio";

        currentFrequency=109;
        displayAfterDelay("    "+currentFrequency+"    FM",currentRadioStation,1000);

        sound = new Media(new File("src\\sample\\songs\\noise.mp3").toURI().toString());
        noiseMediaPlayer=new MediaPlayer(sound);
        noiseMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        noiseMediaPlayer.play();
        noiseMediaPlayer.setVolume(0.5);
        musicVolume=0.5;

        // dodanie gifa
        //TODO zmiana scieżki
        Image image = new javafx.scene.image.Image(getClass().getResource("signal.gif").toExternalForm());
        gif.setImage(image);
    }

    public static void shutdown() {
        applicationClosed=true;
    }

    /**
     * Metoda do ustawienie tekstu
     */
    private void setText(String text)
    {
        display.setText(text);
    }

    /**
     * Metoda wyświetlajaca drugi tekst po upłynięciu podanego czasu
     */
    private void displayAfterDelay(String firstText, String secondText, int delay){

        new Thread()
        {
            public void run() {
                try {
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            setText(firstText);
                        }
                    });

                    if(secondText!=null){
                        Thread.sleep(delay);
                        Platform.runLater(new Runnable() {
                            @Override public void run() {
                                setText(secondText);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * Metoda do znajdywania najblizej stacji
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
        displayAfterDelay("    "+currentFrequency+"    FM",currentRadioStation,1000);
    }
}
