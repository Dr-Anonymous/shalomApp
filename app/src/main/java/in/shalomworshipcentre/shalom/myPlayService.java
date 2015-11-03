package in.shalomworshipcentre.shalom;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.io.IOException;

public class myPlayService extends Service implements OnCompletionListener,
        OnPreparedListener, OnErrorListener, OnSeekCompleteListener,
        OnInfoListener {

    private static final String TAG = "TELSERVICE", URL_STRING = "";
    private MediaPlayer mediaPlayer = new MediaPlayer();
    NotificationCompat.Builder mBuilder;
    public NotificationManager mNotifyMgr;

    // Set up the notification ID
    private static final int NOTIFICATION_ID = 1;
    private boolean isPausedInCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    // ---Variables for seekbar processing---
    String sntSeekPos;
    int intSeekPos;
    int mediaPosition, mediaMax;
    private final Handler handler = new Handler();
    private static int songEnded;
    public static final String BROADCAST_ACTION = "seekprogress";

    // Set up broadcast identifier and intent
    public static final String BROADCAST_BUFFER = "broadcastbuffer";
    Intent seekIntent;
    // Declare headsetSwitch variable
    private int headsetSwitch = 1;

    // OnCreate
    @Override
    public void onCreate() {
        // ---Set up intent for seekbar broadcast ---
        seekIntent = new Intent(BROADCAST_ACTION);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.reset();
        // Register headset receiver
        registerReceiver(headsetReceiver, new IntentFilter(
                Intent.ACTION_HEADSET_PLUG));


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // ---Set up receiver for seekbar change ---
        registerReceiver(broadcastReceiver, new IntentFilter(MyMediaPlayer.BROADCAST_SEEKBAR));
        registerReceiver(playpause, new IntentFilter(MyMediaPlayer.BROADCAST_playpause));

        // Manage incoming phone calls during playback. Pause mp on incoming,
        // resume on hangup.
        // -----------------------------------------------------------------------------------
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                // String stateString = "N/A";
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            isPausedInCall = true;
                        }

                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (isPausedInCall) {
                                isPausedInCall = false;
                                playMedia();
                            }

                        }
                        break;
                }

            }
        };

        // Register the listener with the telephony manager
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        // Insert notification start
        initNotification();
        // Set up the MediaPlayer data source using the strAudioLink value
        if (!mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.setDataSource(FileBrowser.filename);
                mediaPlayer.prepareAsync();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
            } catch (IOException e) {
            }
        }
        // --- Set up seekbar handler ---
        setupHandler();
        return START_STICKY;

    }

    // ---Send seekbar info to activity----
    private void setupHandler() {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1/2 second
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            LogMediaPosition();
            handler.postDelayed(this, 1000); // 1/2 seconds

        }
    };

    private void LogMediaPosition() {
        if (mediaPlayer.isPlaying()) {
            mediaPosition = mediaPlayer.getCurrentPosition();
            mediaMax = mediaPlayer.getDuration();
            seekIntent.putExtra("counter", Double.valueOf(mediaPosition));
            seekIntent.putExtra("mediamax", Double.valueOf(mediaMax));
            seekIntent.putExtra("song_ended", String.valueOf(songEnded));
            sendBroadcast(seekIntent);
        }
    }

    // --Receive seekbar position if it has been changed by the user
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSeekPos(intent);
        }
    };
    private BroadcastReceiver playpause = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int pp = intent.getIntExtra("pauseplay", 0);
            if (pp == 1) {
                pauseMedia();
            }
            if (pp == 2) {
                playMedia();
            }
        }
    };

    // Update seek position from Activity
    public void updateSeekPos(Intent intent) {

        int seekPos = intent.getIntExtra("seekpos", 0);
        if (mediaPlayer.isPlaying()) {
            handler.removeCallbacks(sendUpdatesToUI);
            mediaPlayer.seekTo(seekPos);
            setupHandler();
        }
    }   // ---End of seekbar code


    // If headset gets unplugged, stop music and service.
    private BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
        private boolean headsetConnected = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("state")) {
                if (headsetConnected && intent.getIntExtra("state", 0) == 0) {
                    headsetConnected = false;
                    headsetSwitch = 0;
                } else if (!headsetConnected
                        && intent.getIntExtra("state", 0) == 1) {
                    headsetConnected = true;
                    headsetSwitch = 1;
                }

            }

            switch (headsetSwitch) {
                case (0):
                    headsetDisconnected();
                    break;
                case (1):
                    break;
            }
        }

    };

    private void headsetDisconnected() {
        stopMedia();
        stopSelf();

    }

    // --- onDestroy, stop media player and release.  Also stop phoneStateListener, notification, receivers...---
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }

        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_NONE);
        }

        // Cancel the notification
        cancelNotification();
        // Unregister headsetReceiver
        unregisterReceiver(headsetReceiver);
        // Unregister seekbar receiver
        unregisterReceiver(broadcastReceiver);
        // Stop the seekbar handler from sending updates to UI
        handler.removeCallbacks(sendUpdatesToUI);

    }


    @Override
    public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

        if (!mediaPlayer.isPlaying()) {
            playMedia();
            Toast.makeText(this,
                    "End of audio", Toast.LENGTH_SHORT).show();
        }

    }


    //---Error processing ---
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(this,
                        "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra,
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(this, "MEDIA ERROR SERVER DIED " + extra,
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(this, "MEDIA ERROR UNKNOWN " + extra,
                        Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer arg0) {
        playMedia();

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // When song ends, need to tell activity to display "Play" button
        stopMedia();
        stopSelf();

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    // Add for Telephony Manager
    public void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void stopMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }


    @Override
    public void onLowMemory() {
        stopMedia();
        Toast.makeText(this,
                "Low Memory - Playback stopped", Toast.LENGTH_LONG).show();
        onDestroy();
    }

    // Create Notification
    private void initNotification() {
        Intent resultIntent = new Intent(this, MyMediaPlayer.class);
        Intent r = new Intent(this, MyMediaPlayer.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.appicon)
                .setContentTitle(FileBrowser.name)
                .setContentText("Tap here for Player")
                .setOngoing(true)
                .setTicker("Here !")
                .setContentIntent(resultPendingIntent);
        // Gets an instance of the NotificationManager service
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());

    }

    // Cancel Notification
    private void cancelNotification() {
        mNotifyMgr.cancel(NOTIFICATION_ID);
    }
}