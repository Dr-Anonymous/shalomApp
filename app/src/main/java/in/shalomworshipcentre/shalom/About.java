package in.shalomworshipcentre.shalom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class About extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);

        //hide action bar
        //actionBar.hide();
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.today:
                Toast.makeText(this, "Opening..", Toast.LENGTH_SHORT)
                        .show();
                Intent today = new Intent(About.this, Today.class);
                startActivity(today);
                finish();
                return true;
            case R.id.check:
                Toast.makeText(this, "Checking for Update..", Toast.LENGTH_SHORT)
                        .show();
                Intent check = new Intent(About.this, Check.class);
                startActivity(check);
                finish();
                return true;
            case R.id.share:
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out this app at: http://shalomworshipcentre.in/app.html");
                share.setType("text/plain");
                startActivity(share);
                return true;
            case R.id.exit:
                Intent exit = new Intent(this, MainActivity.class);
                exit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                exit.putExtra("Exit", true);
                startActivity(exit);
                finish();

        }
        return super.onOptionsItemSelected(item);
    }

}