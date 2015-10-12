package in.shalomworshipcentre.shalom;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileBrowser extends ListActivity {
    private String path;
    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser);

        // Use the current directory as title
        path = Environment.getExternalStorageDirectory()
                + "/Shalom";
        if (getIntent().hasExtra("path")) {
            path = getIntent().getStringExtra("path");
        }
        setTitle(path);

        // Read all files sorted into the values-array
        List values = new ArrayList();
        File dir = new File(path);
        if (!dir.canRead()) {
            setTitle(getTitle() + " (inaccessible)");
        }
        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                if (!file.startsWith(".")) {
                    values.add(file);
                }
            }
        }
        Collections.sort(values);

        // Put the data into the list
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, values);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        filename = (String) getListAdapter().getItem(position);
        String name = filename;
        if (path.endsWith(File.separator)) {
            filename = path + filename;
        } else {
            filename = path + File.separator + filename;
        }
        if (new File(filename).isDirectory()) {
            Intent intent = new Intent(this, FileBrowser.class);
            intent.putExtra("path", filename);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        if ((filename).endsWith(".mp3") || filename.endsWith(".wav")) {
            //Intent newIntent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
            Intent newIntent = new Intent(this, MyMediaPlayer.class);
            newIntent.putExtra("path", filename);
            newIntent.putExtra("name", name);
            this.startActivity(newIntent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            //startService();

        } else {
            //stopService();
            Toast.makeText(this, "Use native file browser.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to start the service
    public void startService() {
        startService(new Intent(getBaseContext(), PlayerService.class));
    }

    // Method to stop the service
    public void stopService() {
        stopService(new Intent(getBaseContext(), PlayerService.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}