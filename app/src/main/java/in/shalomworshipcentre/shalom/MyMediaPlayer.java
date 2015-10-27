package in.shalomworshipcentre.shalom;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MyMediaPlayer extends Activity implements SeekBar.OnSeekBarChangeListener {
    public TextView songName, duration, total;
    private int forwardTime = 10000, backwardTime = 10000, boolMusicPlaying = 0, seekMax, seekProgress;
    private SeekBar seekBar;
    private double counter, mediamax;
    private Button buttonPlayPause;
    Intent serviceIntent, intent, inten;
    // Set up the notification ID
    private static int songEnded = 0;
    boolean mBroadcastIsRegistered;
    // --Set up constant ID for broadcast of seekbar position--
    public static final String BROADCAST_SEEKBAR = "sendseekbar", BROADCAST_playpause = "playpause";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            serviceIntent = new Intent(this, myPlayService.class);
            // --- set up seekbar intent for broadcasting new position to service ---
            intent = new Intent(BROADCAST_SEEKBAR);
            inten = new Intent(BROADCAST_playpause);
            initViews();
            setListeners();
            buttonPlayPauseClick();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    e.getClass().getName() + " " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }


    // -- Broadcast Receiver to update position of seekbar from service --
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            updateUI(serviceIntent);
        }
    };

    private void updateUI(Intent serviceIntent) {
        counter = serviceIntent.getDoubleExtra("counter", 0.00);
        mediamax = serviceIntent.getDoubleExtra("mediamax", 0.00);
        String strSongEnded = serviceIntent.getStringExtra("song_ended");

        duration.setText(String.format("%d : %d", TimeUnit.MILLISECONDS.toMinutes((long) counter), TimeUnit.MILLISECONDS.toSeconds((long) counter) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) counter))));
        total.setText(String.format("%d : %d", TimeUnit.MILLISECONDS.toMinutes((long) mediamax), TimeUnit.MILLISECONDS.toSeconds((long) mediamax) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) mediamax))));

        seekProgress = (int) counter;
        seekMax = (int) mediamax;
        songEnded = Integer.parseInt(strSongEnded);
        seekBar.setMax(seekMax);
        seekBar.setProgress(seekProgress);
        if (songEnded == 1) {
            buttonPlayPause.setBackgroundResource(R.drawable.playbuttonsm);
        }
    }

    // --End of seekbar update code--

    private void initViews() {
        setContentView(R.layout.player);
        songName = (TextView) findViewById(R.id.songName);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        songName.setText(FileBrowser.name);
        duration = (TextView) findViewById(R.id.songDuration);
        total = (TextView) findViewById(R.id.total);

        buttonPlayPause = (Button) findViewById(R.id.media_play);
        buttonPlayPause.setBackgroundResource(R.drawable.playbuttonsm);
    }

    // --- Set up listeners ---
    private void setListeners() {
        buttonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boolMusicPlaying == 0) {
                    buttonPlayPauseClick();
                    return;
                }
                if (boolMusicPlaying == 1) {
                    pauseButton();
                    return;
                }
                if (boolMusicPlaying == 2) {
                    playAfterPause();
                    return;
                }
                if (boolMusicPlaying == 3) {
                    finish();
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(this);

    }

    // --- invoked from ButtonPlayStop listener above ----
    public void buttonPlayPauseClick() {
        buttonPlayPause.setBackgroundResource(R.drawable.pausebuttonsm);
        playAudio();
        boolMusicPlaying = 1;
    }

    public void pauseButton() {
        buttonPlayPause.setBackgroundResource(R.drawable.playbuttonsm);
        inten.putExtra("pauseplay", 1);
        sendBroadcast(inten);
        boolMusicPlaying = 2;
    }

    public void playAfterPause() {
        buttonPlayPause.setBackgroundResource(R.drawable.pausebuttonsm);
        inten.putExtra("pauseplay", 2);
        sendBroadcast(inten);
        boolMusicPlaying = 1;
    }

    public void stop(View view) {
        stop();
    }

    public void stop() {
        buttonPlayPause.setBackgroundResource(R.drawable.playbuttonsm);
        stopMyPlayService();
    }

    public void close(View view) {
        close();
    }

    public void close() {
        stopMyPlayService();
        super.onBackPressed();
        overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);
    }

    // --- Stop service (and music) ---
    public void stopMyPlayService() {
        // --Unregister broadcastReceiver for seekbar
        if (mBroadcastIsRegistered) {
            try {
                unregisterReceiver(broadcastReceiver);
                mBroadcastIsRegistered = false;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getClass().getName() + " " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        try {
            stopService(serviceIntent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getClass().getName() + " " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        boolMusicPlaying = 0;
    }

    // --- Start service and play music ---
    private void playAudio() {
        //stopMyPlayService();
        try {
            startService(serviceIntent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getClass().getName() + " " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        // -- Register receiver for seekbar--
        registerReceiver(broadcastReceiver, new IntentFilter(
                myPlayService.BROADCAST_ACTION));
        mBroadcastIsRegistered = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // --- When user manually moves seekbar, broadcast new position to service ---
    @Override
    public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
        if (fromUser) {
            int seekPos = sb.getProgress();
            intent.putExtra("seekpos", seekPos);
            sendBroadcast(intent);
        }
    }


    public void vol(View view) {
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
    }

    public void ff(View view) {
        ff();
    }

    public void ff() {
        //check if we can go forward at forwardTime seconds before song endes
        if ((seekProgress + forwardTime) <= seekMax) {
            seekProgress = seekProgress + forwardTime;

            //seek to the exact second of the track
            intent.putExtra("seekpos", seekProgress);
            sendBroadcast(intent);
        }
    }

    public void rew(View view) {
        rew();
    }

    public void rew() {
        if ((seekProgress - backwardTime) >= 0) {
            seekProgress = seekProgress - backwardTime;

            //seek to the exact second of the track
            intent.putExtra("seekpos", seekProgress);
            sendBroadcast(intent);
        }
    }


    // --- The following two methods are alternatives to track seekbar if moved.
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }


    // when pressed on blank screen
    public void finish(View view) {
        onBackPressed();
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);
    }

}