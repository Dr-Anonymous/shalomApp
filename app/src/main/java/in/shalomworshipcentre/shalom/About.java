package in.shalomworshipcentre.shalom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class About extends ActionBarActivity {
    public Switch mySwitch;
    public SharedPreferences prefs;
    String settingsTAG = "AppSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.about);
        mySwitch = (Switch) findViewById(R.id.mySwitch);
        //SharedPreferences sharedPrefs = getSharedPreferences("SwitchButton", MODE_PRIVATE);
        //mySwitch.setChecked(sharedPrefs.getBoolean("smart", false));
        prefs = getSharedPreferences(settingsTAG, 0);
        mySwitch.setChecked(prefs.getBoolean("rb0", false));


        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    prefs = getSharedPreferences(settingsTAG, 0);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("rb0", true);
                    editor.commit();

                    //restart
                    Intent exit = new Intent(About.this, MainActivity.class);
                    exit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    exit.putExtra("on", true);
                    startActivity(exit);
                    finish();
                } else {
                    prefs = getSharedPreferences(settingsTAG, 0);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("rb0", false);
                    editor.commit();

                    // apply change
                    Intent exit = new Intent(About.this, MainActivity.class);
                    exit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    exit.putExtra("off", true);
                    startActivity(exit);
                    finish();
                }

            }
        });
        /* check the current state of switch

           if (mySwitch.isChecked()) {
            } else {
            }*/

    }

    public void check(View view) {
        if (!DetectConnection.checkInternetConnection(this)) {
            Toast.makeText(getApplicationContext(), "No Internet! Please enable net and retry", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Checking for Update..", Toast.LENGTH_SHORT)
                    .show();
            Intent check = new Intent(About.this, Check.class);
            startActivity(check);
            overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);

        }
    }

    public void share(View view) {
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT,
                "Hey check out this app at: http://shalomworshipcentre.in/app.html");
        share.setType("text/plain");
        startActivity(share);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void help(View view) {
        Intent help = new Intent(this, New.class);
        startActivity(help);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}