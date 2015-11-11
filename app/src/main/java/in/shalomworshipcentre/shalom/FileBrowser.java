package in.shalomworshipcentre.shalom;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileBrowser extends ListActivity {
    private String path;
    public static String filename, name, part2;
    TextView textView;
    boolean audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser);
        textView = (TextView) findViewById(R.id.title);

        // Use the current directory as title
        path = Environment.getExternalStorageDirectory()
                + "/Shalom";
        if (getIntent().hasExtra("path")) {
            path = getIntent().getStringExtra("path");
        }
        // setTitle(path);
        textView.setText(path);

        // Read all files sorted into the values-array
        List values = new ArrayList();
        File dir = new File(path);
        if (!dir.canRead()) {
            Toast.makeText(this, "inaccessible", Toast.LENGTH_SHORT).show();
            // setTitle(getTitle() + " inaccessible");
        }
        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                if (!file.startsWith(".")) {
                    values.add(file);
                }
            }
        } else {
            path = Environment.getExternalStorageDirectory()
                    + "/download";
            Intent intent = new Intent(this, FileBrowser.class);
            intent.putExtra("path", path);
            startActivity(intent);
            finish();
        }
        Collections.sort(values);
        // Put the data into the list
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, values);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        filename = (String) getListAdapter().getItem(position);
        name = filename;
        if (path.endsWith(File.separator)) {
            filename = path + filename;
        } else {
            filename = path + File.separator + filename;
        }
        if (new File(filename).isDirectory()) {
            Intent intent = new Intent(this, FileBrowser.class);
            intent.putExtra("path", filename);
            try {
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No handler for this type of file.", Toast.LENGTH_SHORT).show();
            }
        } else if (isSong()) {
            audio = getSharedPreferences(About.settings, MODE_PRIVATE).getBoolean("audio", true);
            if (audio) {
                Intent newIntent = new Intent(this, MyMediaPlayer.class);
                startActivity(newIntent);
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
            } else {
                openFile();
            }
        } else {
            openFile();
        }
    }

    void openFile() {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        try {
            String mimeType = myMime.getMimeTypeFromExtension(fileExt(filename).substring(1));
            newIntent.setDataAndType(Uri.fromFile(new File(filename)), mimeType);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No handler for this type of file.", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException n) {
            Toast.makeText(this, "Can't open this file.", Toast.LENGTH_SHORT).show();
        }
    }

    private String fileExt(String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf("."));
            if (ext.contains("%")) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.contains("/")) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    public void back() {
        // invert filename
        path = new StringBuffer(path).reverse().toString();
        // take off first part at /
        String[] parts = path.split("/", 2);
        part2 = parts[1];
        // re invert part 2
        path = new StringBuffer(part2).reverse().toString();
        Intent intent = new Intent(this, FileBrowser.class);
        intent.putExtra("path", path);
        startActivity(intent);
        finish();
    }

    public void goBack(View view) {
        if (path.equals("/storage")) {
            super.onBackPressed();
        } else {
            back();
        }
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static boolean isSong() {
        if (filename.endsWith("mp3") || filename.endsWith("m4a") || filename.endsWith("flac") ||
                filename.endsWith("wav") || filename.endsWith("wma") || filename.endsWith("ogg"))
            return true;
        else
            return false;
    }
}