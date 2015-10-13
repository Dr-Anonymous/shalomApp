package in.shalomworshipcentre.shalom;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;


public class PlayerService extends Service {
    private static final String TAG = null;
    MediaPlayer player;
    private String path;

    public IBinder onBind(Intent arg0) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        path = Environment.getExternalStorageDirectory()
                + "/Shalom/1.mp3";
        try {
            player.setDataSource(path);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Tap once more t", Toast.LENGTH_SHORT).show();

        }
        player.setLooping(true); // Set looping

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            player.prepare();
            player.start();
        } catch (IOException e) {
        }
        return 1;
    }

    public void onStart(Intent intent, int startId) {
        // TO DO
    }

    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() {
        Toast.makeText(getBaseContext(), "stopped", Toast.LENGTH_SHORT).show();

    }

    public void onPause() {

    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {

    }
}