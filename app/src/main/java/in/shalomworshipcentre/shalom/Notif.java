package in.shalomworshipcentre.shalom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class Notif extends AppCompatActivity {
    static TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);
        // Set up the views
        textView = (TextView) findViewById(R.id.txt);
        // get the latest notification
        String newMessage = getSharedPreferences("settings", MODE_PRIVATE).getString("knock", "Nothing new for now :-)");
        textView.setText(newMessage);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}