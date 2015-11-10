package in.shalomworshipcentre.shalom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.parse.ConfigCallback;
import com.parse.GetDataCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseFile;

class Helper {
    static ParseFile file;
    static String text;
    private static final long configRefreshInterval = 12 * 60 * 60 * 1000;
    private static long lastFetchedTime;

    // Fetches the config at most once every 12 hours per app runtime
    public static void refreshConfig() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFetchedTime > configRefreshInterval) {
            lastFetchedTime = currentTime;
            ParseConfig.getInBackground(new ConfigCallback() {
                @Override
                public void done(ParseConfig config, ParseException e) {
                    if (e == null) {
                        text = config.getString("text", null);
                        file = config.getParseFile("pic", null);
                        img();
                    } else {
                        text = (ParseConfig.getCurrentConfig()).getString("text", null);
                        file = (ParseConfig.getCurrentConfig()).getParseFile("pic", null);
                        img();
                    }
                    Notif.progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            text = (ParseConfig.getCurrentConfig()).getString("text", null);
            file = (ParseConfig.getCurrentConfig()).getParseFile("pic", null);
            img();
            Notif.progressBar.setVisibility(View.GONE);
        }
    }

    static private void img() {
        if (file != null) {
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, com.parse.ParseException e) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Notif.pic.setImageBitmap(bmp);
                }
            });
        }
        Notif.server.setText(text);
    }
}