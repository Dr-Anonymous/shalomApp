package in.shalomworshipcentre.shalom;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MyMediaPlayer extends Activity {
    private String path;
    private MediaPlayer mediaPlayer;
    public TextView songName, duration;
    private double timeElapsed = 0, finalTime = 0;
    private int forwardTime = 5000, backwardTime = 5000;
    private Handler durationHandler = new Handler();
    private SeekBar seekbar;
    ImageButton media_pause;
    ImageButton media_play;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the layout of the Activity
        setContentView(R.layout.player);

        // Use the current directory as title
        path = Environment.getExternalStorageDirectory()
                + "/Shalom";
        if (getIntent().hasExtra("path")) {
            path = getIntent().getStringExtra("path");
            name = getIntent().getStringExtra("name");
        }
        setTitle(path);

        songName = (TextView) findViewById(R.id.songName);
        media_play = (ImageButton) findViewById(R.id.media_play);
        media_pause = (ImageButton) findViewById(R.id.media_pause);

        mediaPlayer = MediaPlayer.create(this, Uri.parse(path));
        finalTime = mediaPlayer.getDuration();
        duration = (TextView) findViewById(R.id.songDuration);
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        songName.setText(name);
        seekbar.setMax((int) finalTime);

        mediaPlayer.start();
        media_play.setVisibility(View.GONE);
        media_pause.setVisibility(View.VISIBLE);
        timeElapsed = mediaPlayer.getCurrentPosition();
        seekbar.setProgress((int) timeElapsed);
        durationHandler.postDelayed(updateSeekBarTime, 100);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                timeElapsed = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo((int) timeElapsed);
            }
        });
    }

    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            timeElapsed = mediaPlayer.getCurrentPosition();
            //set seekbar progress
            seekbar.setProgress((int) timeElapsed);
            //set time remaing
            double timeRemaining = finalTime - timeElapsed;
            duration.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));

            //repeat yourself that again in 200 miliseconds
            durationHandler.postDelayed(this, 200);
        }
    };

    // play mp3 song
    public void play(View view) {
        mediaPlayer.start();
        media_play.setVisibility(View.GONE);
        media_pause.setVisibility(View.VISIBLE);
        timeElapsed = mediaPlayer.getCurrentPosition();
        seekbar.setProgress((int) timeElapsed);
        durationHandler.postDelayed(updateSeekBarTime, 100);
    }

    // pause mp3 song
    public void pause(View view) {
        media_play.setVisibility(View.VISIBLE);
        media_pause.setVisibility(View.GONE);
        mediaPlayer.pause();

    }

    public void stop(View view) {
        //android.os.Process.killProcess(android.os.Process.myPid());
        mediaPlayer.stop();
        media_play.setVisibility(View.VISIBLE);
        media_pause.setVisibility(View.GONE);

    }


    // go forward at forwardTime seconds
    public void forward(View view) {
        //check if we can go forward at forwardTime seconds before song endes
        if ((timeElapsed + forwardTime) <= finalTime) {
            timeElapsed = timeElapsed + forwardTime;

            //seek to the exact second of the track
            mediaPlayer.seekTo((int) timeElapsed);
        }
    }

    public void rewind(View view) {
        if ((timeElapsed - backwardTime) >= 0) {
            timeElapsed = timeElapsed - backwardTime;

            //seek to the exact second of the track
            mediaPlayer.seekTo((int) timeElapsed);
        }
    }

    @Override
    public void onBackPressed() {
        mediaPlayer.stop();
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


}