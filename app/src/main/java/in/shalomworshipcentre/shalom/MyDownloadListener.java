package in.shalomworshipcentre.shalom;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.widget.Toast;

import java.io.File;

public class MyDownloadListener implements DownloadListener {
    private Context mContext;
    private DownloadManager mDownloadManager;
    private long mDownloadedFileID;
    private DownloadManager.Request mRequest;
    private static String filename;
    File direct;

    public MyDownloadListener(Context context) {
        mContext = context;
        mDownloadManager = (DownloadManager) mContext
                .getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String
            contentDisposition, final String mimetype, long contentLength) {

        // Function is called once download completes.
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(mContext, "Download complete!", Toast.LENGTH_SHORT).show();

                // Prevents the occasional unintentional call. I needed this.
                if (mDownloadedFileID == -1)
                    return;
                Intent fileIntent = new Intent(Intent.ACTION_VIEW);

                // Grabs the Uri for the file that was downloaded.
                Uri mostRecentDownload = mDownloadManager.getUriForDownloadedFile(mDownloadedFileID);
                // DownloadManager stores the Mime Type. Makes it really easy for us.
                String mimeType = mDownloadManager.getMimeTypeForDownloadedFile(mDownloadedFileID);
                fileIntent.setDataAndType(mostRecentDownload, mimeType);
                fileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    mContext.startActivity(fileIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, "No handler for this type of file.", Toast.LENGTH_LONG).show();
                }
                // Sets up the prevention of an unintentional call. I found it necessary. Maybe not for others.
                mDownloadedFileID = -1;
            }
        };
        // Registers function to listen to the completion of the download.
        mContext.registerReceiver(onComplete, new
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        mRequest = new DownloadManager.Request(Uri.parse(url));
        // create the directory if not there
        direct = new File(Environment.getExternalStorageDirectory()
                + "/Shalom");
        if (!direct.exists()) {
            direct.mkdirs();
        }
        // Limits the download to only over WiFi. Optional.
        // mRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // Makes download visible in notifications while downloading, but disappears after download completes. Optional.
        mRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        mRequest.setMimeType(mimetype);

        // If necessary for a security check. I needed it, but I don't think it's mandatory.
        // String cookie = CookieManager.getInstance().getCookie(url);
        //mRequest.addRequestHeader("Cookie", cookie);

        // Grabs the file name from the Content-Disposition ---- this not working
       /* android.util.Log.d("Applog","fileName:"+fileName);;
        Pattern regex = Pattern.compile("(?<=filename=\").*?(?=\")");
        Matcher regexMatcher = regex.matcher(contentDisposition);
        if (regexMatcher.find()) {
            filename = regexMatcher.group();
        }*/
        // so ---
        filename = URLUtil.guessFileName(url, contentDisposition, mimetype);
        // Sets the file path to save to, including the file name.
        mRequest.setDestinationInExternalPublicDir("/Shalom", filename);
        // Sets the title of the notification and how it appears to the user in the saved directory.
        mRequest.setTitle(filename);

        // resume function--


        // Adds the request to the DownloadManager queue to be executed at the next available opportunity.
        mDownloadedFileID = mDownloadManager.enqueue(mRequest);
        Toast.makeText(mContext, "Downloading . . .", Toast.LENGTH_LONG).show();

    }
}