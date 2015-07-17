package in.shalomworshipcentre.shalom;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Dr on 09/07/2015.
 */
public class First extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("");
        getSupportActionBar().hide();


        setContentView(R.layout.first);
    }
}