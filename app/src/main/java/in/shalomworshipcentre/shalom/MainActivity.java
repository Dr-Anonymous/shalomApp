package in.shalomworshipcentre.shalom;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //exit pressed from next activity
        if (getIntent().getBooleanExtra("Exit", false)) {
            finish();
            return; // add this to prevent from doing unnecessary stuffs
        }


        //url loading
        WebView myWebView = (WebView) findViewById(R.id.main);
        myWebView.loadUrl("http://www.shalomworshipcentre.in");

        // actionbar overlay
        //getWindow().requestFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        //Enabling JavaScript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //chrome client
        myWebView.setWebChromeClient(new WebChromeClient());
        //cache enabled
        webSettings.setAppCacheEnabled(true);
        //the way the cache is used
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        // Function to load all URLs in same webview
        myWebView.setWebViewClient(new WebViewClient());
        //zoom feature
        webSettings.setBuiltInZoomControls(true);
        // dont show zoom controls
        webSettings.setDisplayZoomControls(false);
        //allow file access-- not sure what it is...
        webSettings.setAllowFileAccess(true);
        //disabling debugging in webview
        WebView.setWebContentsDebuggingEnabled(false);

        /** //downloading files using external brweser
         myWebView.setDownloadListener(new DownloadListener() {
         public void onDownloadStart(String url, String userAgent,
         String contentDisposition, String mimetype,
         long contentLength) {
         Intent i = new Intent(Intent.ACTION_VIEW);
         i.setData(Uri.parse(url));
         startActivity(i);
         }
         }); */

        //download using download manager
        myWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Shalom_Messages");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);

            }
        });

    }


    // Navigating web page history by clicking back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        WebView myWebView = (WebView) findViewById(R.id.main);
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), "Tap back button once more to exit", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            case R.id.refresh:
                Toast.makeText(this, "Refreshing..", Toast.LENGTH_SHORT)
                        .show();
                WebView myWebView = (WebView) findViewById(R.id.main);
                myWebView.reload();
                return true;
            case R.id.today:
                Toast.makeText(this, "Opening..", Toast.LENGTH_SHORT)
                        .show();
                Intent today = new Intent(MainActivity.this, Today.class);
                startActivity(today);
                return true;
            case R.id.downloads:
                Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                        + "");
                Intent open = new Intent(Intent.ACTION_VIEW);
                open.setDataAndType(uri, "*/*");
                startActivity(Intent.createChooser(open, "Open downloaded files"));
            return true;
            case R.id.about:
                Toast.makeText(this, "Opening..", Toast.LENGTH_SHORT)
                        .show();
                Intent about = new Intent(MainActivity.this, About.class);
                startActivity(about);
                return true;
            case R.id.share:
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out this app at: http://shalomworshipcentre.in/app.html");
                share.setType("text/plain");
                startActivity(share);
                return true;
            /*case R.id.home:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;*/
            case R.id.exit:
                finish();

        }


        return super.onOptionsItemSelected(item);
    }

}
