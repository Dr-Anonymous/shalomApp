package in.shalomworshipcentre.shalom;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class Check extends AppCompatActivity {
    boolean download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.check);
        WebView myWebView = (WebView) findViewById(R.id.check);
        // display current version
        Context context = getApplicationContext(); // or activity.getApplicationContext()
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        String myVersionName = "Version not available."; // initialize String
        try {
            myVersionName = packageManager.getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // set version name to a TextView
        TextView tvVersionName = (TextView) findViewById(R.id.versionName);
        tvVersionName.setText("Version installed : " + myVersionName);

        myWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        myWebView.setWebChromeClient(new WebChromeClient());
        myWebView.setWebViewClient(new UriWebViewClient());
        myWebView.loadUrl("http://shalomworshipcentre.in/appupdate.html");

        //downloading files using external browser or internal download listner
        download = getSharedPreferences(About.settings, MODE_PRIVATE).getBoolean("download", false);
        if (download) {
            myWebView.setDownloadListener(new DownloadListener() {
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
        } else {
            myWebView.setDownloadListener(new MyDownloadListener(myWebView.getContext()));
        }
    }

    private class UriWebViewClient extends WebViewClient {
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.e(String.valueOf(errorCode), description);
            if (errorCode == -2) {
                view.loadUrl("file:///android_asset/error.html");
                return;
            }
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
