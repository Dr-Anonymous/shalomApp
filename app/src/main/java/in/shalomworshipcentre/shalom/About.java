package in.shalomworshipcentre.shalom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

public class About extends AppCompatActivity {
    public CheckBox mySwitch, checkBox, checkBox2, checkBox3;
    public static SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        settings();

    }


    public void settings() {
        prefs = getSharedPreferences("settings", MODE_PRIVATE);

        mySwitch = (CheckBox) findViewById(R.id.mySwitch);
        mySwitch.setChecked(prefs.getBoolean("smart", false));

        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    prefs.edit().putBoolean("smart", true).commit();
                    // then restart
                    restart();
                } else {
                    prefs.edit().putBoolean("smart", false).commit();
                    // then restart
                    restart();
                }

            }
        });


        checkBox = (CheckBox) findViewById(R.id.chk);
        checkBox.setChecked(prefs.getBoolean("download", false));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    prefs.edit().putBoolean("download", true).commit();
                    restart();
                } else {
                    prefs.edit().putBoolean("download", false).commit();
                    restart();
                }
            }
        });


        checkBox2 = (CheckBox) findViewById(R.id.chk2);
        checkBox2.setChecked(prefs.getBoolean("notif", true));
        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    prefs.edit().putBoolean("notif", true).commit();
                } else {
                    prefs.edit().putBoolean("notif", false).commit();
                }
            }
        });
        checkBox3 = (CheckBox) findViewById(R.id.chk3);
        checkBox3.setChecked(prefs.getBoolean("audio", false));
        checkBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    prefs.edit().putBoolean("audio", true).commit();
                } else {
                    prefs.edit().putBoolean("audio", false).commit();
                }
            }
        });

    }

    public void restart() {
        Intent exit = new Intent(About.this, MainActivity.class);
        exit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        exit.putExtra("restart", true);
        startActivity(exit);
        finish();
    }

    public void check(View view) {
        if (!Helper.checkInternetConnection(this)) {
            Toast.makeText(this, "Enable internet to view updates", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Checking for Update..", Toast.LENGTH_SHORT).show();
            Intent check = new Intent(About.this, Check.class);
            startActivity(check);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

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