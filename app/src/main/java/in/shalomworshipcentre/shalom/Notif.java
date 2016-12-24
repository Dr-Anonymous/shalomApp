package in.shalomworshipcentre.shalom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

public class Notif extends AppCompatActivity {
    static TextView textView;
    // GPSTracker class
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);
        // Set up the views
        textView = (TextView) findViewById(R.id.txt);
        // get the latest notification
        String newMessage = getSharedPreferences("settings", MODE_PRIVATE).getString("knock", "Nothing new for now :-)");
        textView.setText(newMessage);


        // create gps class object
        gps = new GPSTracker(this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // create gps class object
        gps = new GPSTracker(this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        // stop using gps services
        gps.stopUsingGPS();
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}