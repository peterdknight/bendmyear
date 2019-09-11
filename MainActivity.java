package com.example.cisentences;




import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private SoundPool soundPool;

    private AudioManager audioManager;

    // Maximumn sound stream.
    private static final int MAX_STREAMS = 2;

    // Stream type.
    private static final int streamType = AudioManager.STREAM_MUSIC;

    private boolean loaded;

    private int soundIdBabble,babbleStreamId;
    private int sentenceStreamId;
    private int soundIdSentence;
    private int stopIdBabble;
    private float volume,babbleLeftVolume,babbleRightVolume;
    private Float leftSentenceVolume,rightSentenceVolume;
    private Float speechRate,normalSpeechRate,slowSpeechRate;
    private Uri uri,noiseUri;  //uri for sentences
    private String waveFile, waveName, sentence,noiseClip,waveSentence;
    private String nameNoise,waveNoise;
    private int wave,noiseWave; //returned int value for wav file
    private Boolean babbleRunning,babblePaused;
    private int pointer;  //index to rnd sentences
    private TextView viewTheSentence;
    Button displaySentence,playBabbleButton,pauseBabbleButton;
    Button playSoundSentence,repeatSentence;
    Button helpButton,acknowledgeButton;
    RadioButton zeroVolumeButton,quarterVolumeButton,halfVolumeButton;
    RadioButton threeQuarterVolumeButton,fullVolumeButton;
    RadioButton normalSpeech,slowSpeech;
    RadioButton babbleNoise,birdNoise;
    String[] sentences;
    int counter,maxRecs;
    String line;
    InputStream fis;
    Boolean progStarted;
    SoundPool.Builder builder;
    private int soundID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playSoundSentence = findViewById(R.id.buttonPlaySentence);
        repeatSentence = findViewById(R.id.buttonRepeatSentence);
        displaySentence = findViewById(R.id.buttonDisplaySentence);
        helpButton = findViewById(R.id.buttonHelp);
        acknowledgeButton = findViewById(R.id.buttonAcknowledgement);
        viewTheSentence = findViewById(R.id.textviewTheSentence);
        zeroVolumeButton = findViewById(R.id.buttonZeroVol);
        quarterVolumeButton = findViewById(R.id.buttonQuarterVol);
        halfVolumeButton = findViewById(R.id.buttonHalfVol);
        threeQuarterVolumeButton = findViewById(R.id.button3quarterVol);
        fullVolumeButton = findViewById(R.id.buttonFullVol);
        normalSpeech = findViewById(R.id.normalSpeech);
        slowSpeech  = findViewById(R.id.slowSpeech);
        babbleNoise = findViewById(R.id.babbleNoise);
        birdNoise = findViewById(R.id.birdNoise);

        normalSpeechRate= (float) 1.0;
        slowSpeechRate = (float) 0.8;
        speechRate = normalSpeechRate;
         sentences = new String[1450];
         babbleRunning = false;
         babblePaused = false;
        this.readSentence();

        // AudioManager audio settings for adjusting the volume
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Current volumn Index of particular stream type.
        float currentVolumeIndex = (float) audioManager.getStreamVolume(streamType);

        // Get the maximum volume index for a particular stream type.
        float maxVolumeIndex = (float) audioManager.getStreamMaxVolume(streamType);

        // Volumn (0 --> 1)babblePoo
        this.volume = currentVolumeIndex / maxVolumeIndex;

        // Suggests an audio stream whose volume should be changed by
        // the hardware volume controls.
        this.setVolumeControlStream(streamType);

        // For Android SDK >= 21
        if (Build.VERSION.SDK_INT >= 21) {

            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

           builder = new SoundPool.Builder();
            builder.setAudioAttributes(audioAttrib).setMaxStreams(MAX_STREAMS);

            this.soundPool = builder.build();
        }
        // for Android SDK < 21
        else {
            // SoundPool(int maxStreams, int streamType, int srcQuality)
            this.soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }

        // When Sound Pool load complete.
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });

        // Load sound file (destroy.wav) into SoundPool.
     //   waveName="babble";
     //   uri= Uri.parse("android.resource://"+getPackageName()+"/raw/" + waveName);
     //   waveFile = uri.toString();
      //  int wave = getResources().getIdentifier(waveName, "raw", getPackageName());
      //  soundID = soundPool.load(this, wave, 1);
   //    this.soundIdBabble = this.soundPool.load(this, R.raw.babble, 1);
   // float leftVolumn = volume;
   // float rightVolumn = volume;
       babbleLeftVolume = (float) .50;
      babbleRightVolume = (float) .50;
      leftSentenceVolume = (float) 1;
      rightSentenceVolume = (float) 1;

        halfVolumeButton.setChecked(true);
        normalSpeech.setChecked(true);
        babbleNoise.setChecked(true);
        nameNoise = "babble";

    }


    // When users click on the button "Babble"
    public void playSoundBabble
    (View view) throws InterruptedException {
       // this.soundIdBabble = this.soundPool.load(this, R.raw.babble, 1);
        noiseUri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + nameNoise);

        noiseWave = getResources().getIdentifier(nameNoise, "raw", getPackageName());
        this.soundIdBabble = (int) this.soundPool.load(this, noiseWave, 1);
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        babbleStreamId = this.soundPool.play(this.soundIdBabble, babbleLeftVolume,babbleRightVolume, 1, -1, 1f);
        babbleRunning = true;
    }

    // When users click on the button "Sentence"
    public void playSoundSentence(View view) throws InterruptedException {
        Random rndIndex = new Random();
      //  showSentence.setText("               ");
        progStarted = true;
        //     System.out.println("recLen " + recLen + "  max " + max);
        pointer = rndIndex.nextInt(maxRecs)  ;
        sentence = sentences[pointer];

        waveName = DecodeSentence.getWave(sentence);
        waveSentence = DecodeSentence.getSentence(sentence);
     //   Toast.makeText(getApplicationContext(),"wave *"+waveName + "*" ,
            //    Toast.LENGTH_LONG).show();
        uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + waveName);

       wave = getResources().getIdentifier(waveName, "raw", getPackageName());
       this.soundIdSentence = (int) this.soundPool.load(this, wave, 1);
        Thread.sleep(1000);
       sentenceStreamId = this.soundPool.play(this.soundIdSentence, leftSentenceVolume, rightSentenceVolume,
               1, 0, speechRate);
            viewTheSentence.setText("");
    }
    public void repeatSentence(View view) {
               sentenceStreamId = this.soundPool.play(this.soundIdSentence, leftSentenceVolume, rightSentenceVolume,
                       1, 0, speechRate);
    }

    public void stopBabble(View view) {

        soundPool.stop(this.soundIdBabble);
       // soundPool.unload(this.soundIdBabble);
    }
    public void displaySentence(View view) {

       viewTheSentence.setText(waveSentence);
    }

public void setZeroVolumeButton (View view) {
    babbleLeftVolume = (float) 0.0;
    babbleRightVolume = (float) 0.0;
    soundPool.setVolume(babbleStreamId,babbleLeftVolume,babbleRightVolume);
}
    public void setQuarterVolumeButton (View view) {

        babbleLeftVolume = (float) 0.25;
      babbleRightVolume = (float) 0.25;
        // Play sound of gunfire. Returns the ID of the new stream.
     soundPool.setVolume(babbleStreamId,babbleLeftVolume,babbleRightVolume);
    }
    public void setHalfVolumeButton (View view) {

        babbleLeftVolume = (float) 0.5;
        babbleRightVolume = (float) 0.5;
        soundPool.setVolume(babbleStreamId,babbleLeftVolume,babbleRightVolume);
    }
    public void setThreeQuarterVolumeButton (View view) {

        babbleLeftVolume = (float) 0.75;
        babbleRightVolume = (float) 0.75;
        soundPool.setVolume(babbleStreamId,babbleLeftVolume,babbleRightVolume);
    }
    public void setFullVolumeButton (View view) {
        babbleLeftVolume = (float) 1;
        babbleRightVolume = (float)1;
        soundPool.setVolume(babbleStreamId,babbleLeftVolume,babbleRightVolume);
    }
    public void setPauseBabbleButton(View view) {
        soundPool.pause(babbleStreamId);
    }

    public void setNormalSpeech(View view) {
        speechRate = normalSpeechRate;
        soundPool.setRate(sentenceStreamId,speechRate);
    }

    public void setSlowSpeech(View view) {
        speechRate = slowSpeechRate;
        soundPool.setRate(sentenceStreamId,speechRate);
    }
    public void setBabbleNoise(View view){
        nameNoise ="babble";
        soundPool.pause(babbleStreamId);
    }

    public void setBirdNoise(View view) {
        nameNoise = "birdnoise";
        soundPool.pause(babbleStreamId);
    }

    // Button btn = (Button)findViewById(R.id.getBtn);
    //    btn.setOnClickListener(new View.OnClickListener() {
     //   @Override
        public void setHelpButton(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("HELP")
                 .setMessage("Play a New Sentence, chooses a \n" +
                         "sentence from 1440 sentences\n" +
                         "and plays it.\n" +
                         "You may repeat the recording as \n" +
                         "often as You need to be able \n" +
                         "to understand it.\n" +
                         "Play it slower." +
                         "If You need help\n" +
                         "Display the Sentence.\n" +
                         "To make it more difficult\n" +
                         "Play Background Noise.\n" +
                         "Adjust the Volume\n" +
                         "to suit you.")

                    .setCancelable(false)
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           // Toast.makeText(MainActivity.this,"Selected Option: YES",Toast.LENGTH_SHORT).show();
                        }
                    });
                  //Creating dialog box
            AlertDialog dialog  = builder.create();
            dialog.show();
        }
        public void setAcknowledgeButton (View view){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("ACKNOWLEDGEMENT")
                    .setMessage("This App would not have been\n" +
                            "possible without Tiger Speech.\n" +
                            "angelsound.tigerspeech.com\n" +
                            "They have spent many hours\n" +
                           "Creating a fantastic program.\n" +
                          "This App uses the sentences \n" +
                                    "they created."
                            )

                    .setCancelable(false)
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Toast.makeText(MainActivity.this,"Selected Option: YES",Toast.LENGTH_SHORT).show();
                        }
                    });
            //Creating dialog box
            AlertDialog dialog  = builder.create();
            dialog.show();
        }

    protected void onResume() {
        super.onResume();
        babbleLeftVolume = (float) 0.5;
        babbleRightVolume = (float) 0.5;
        soundPool.setVolume(babbleStreamId,babbleLeftVolume,babbleRightVolume);
        speechRate = normalSpeechRate;
        soundPool.setRate(sentenceStreamId,speechRate);

        halfVolumeButton.setChecked(true);
        normalSpeech.setChecked(true);
      //  Log.i(TAG, "onResume");
    }

    protected void onPause() {
        super.onPause();
        if ( babbleRunning ) {
          soundPool.pause(babbleStreamId);
          babblePaused = true;
        }
     // Log.i(TAG, "onPause");
    }

    // read the sentence/wave index file
    public void readSentence() {

        try {
            InputStream fis = getAssets().open("sentences");

            // prepare the file for reading
            InputStreamReader chapterReader = new InputStreamReader(fis);
            BufferedReader buffreader = new BufferedReader(chapterReader);

            maxRecs = 0;
            counter = 0;
            // read every line of the file into the line-variable, on line at the time
            do {
                line = buffreader.readLine();
                sentences[counter] = line;
                counter++;


            } //while (counter < 1441);
            while (line != null);
            maxRecs = counter-1;
          //  viewTheSentence.setText("recs read " + maxRecs);
         // showSentence.setText("max recs = " + maxRecs);
          //   Toast.makeText(getApplicationContext(),"The file read operation is finished successfully.",
               //  Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {
            // displaySentence.setText("error reading sentences");

        }
    }
}