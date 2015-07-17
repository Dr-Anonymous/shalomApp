package in.shalomworshipcentre.shalom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    private ImageView one = null;
    private ImageView two = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("");
       /* Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);
        if (isFirstRun) {
            Toast.makeText(getApplicationContext(), "Hit 'Refresh' to fetch new data", Toast.LENGTH_LONG).show();
        }
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();*/

        setContentView(R.layout.activity_main);
        //hide actionbar after delay
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                // DO DELAYED STUFF
                getSupportActionBar().hide();
            }
        }, 3000);
        one = (ImageView) findViewById(R.id.show);
        one.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                getSupportActionBar().show();
                one.setVisibility(View.GONE);
                two.setVisibility(View.VISIBLE);
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getSupportActionBar().hide();
                        one.setVisibility(View.VISIBLE);
                        two.setVisibility(View.GONE);
                    }
                }, 7000);
            }
        });
        two = (ImageView) findViewById(R.id.hide);
        two.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                getSupportActionBar().hide();
                one.setVisibility(View.VISIBLE);
                two.setVisibility(View.GONE);
            }
        });
        //exit pressed from next activity
        if (getIntent().getBooleanExtra("Exit", false)) {
            finish();
            return; // add this to prevent from doing unnecessary stuffs
        }
        WebView myWebView = (WebView) findViewById(R.id.main);
        //Enabling JavaScript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //chrome client
        myWebView.setWebChromeClient(new WebChromeClient());
        //cache path
        webSettings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        //allow file access
        webSettings.setAllowFileAccess(true);
        //cache enabled
        webSettings.setAppCacheEnabled(true);
        // load online by default
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        if (!DetectConnection.checkInternetConnection(this)) {// loading offline
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        myWebView.loadUrl("http://www.shalomworshipcentre.in");
        //HTML5 localstorage feature
        webSettings.setDomStorageEnabled(true);
        // Function to load all URLs in same webview
        myWebView.setWebViewClient(new WebViewClient());

       /* File dir = this.getCacheDir();
        if (dir == null && DetectConnection.checkInternetConnection(this)) {
            myWebView.loadUrl("http://www.shalomworshipcentre.in");
        } else {
            Toast.makeText(getApplicationContext(), "Offline version", Toast.LENGTH_SHORT).show();
            myWebView.loadUrl("file:///android_asset/index.html");

        }*/
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        //downloading files using external browser
         myWebView.setDownloadListener(new DownloadListener() {
             public void onDownloadStart(String url, String userAgent,
                                         String contentDisposition, String mimetype,
                                         long contentLength) {
                 Intent i = new Intent(Intent.ACTION_VIEW);
                 i.setData(Uri.parse(url));
                 startActivity(i);
             }
         });
        //download using download manager
       /* myWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Shalom.mp3");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);

            }
        });*/

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView myWebView = (WebView) findViewById(R.id.main);
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
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
            Toast.makeText(getBaseContext(), "Tap 'Back' once more to exit", Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        WebView myWebView = (WebView) findViewById(R.id.main);
        switch (item.getItemId()) {
            case R.id.refresh:
                if (!DetectConnection.checkInternetConnection(this)) {
                    Toast.makeText(getApplicationContext(), "No Internet! Please enable net and then hit 'Refresh'", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Refreshing..", Toast.LENGTH_LONG).show();
                    myWebView.reload();
                }
                return true;
           /* case R.id.downloads:
             Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                    + "");
             Intent open = new Intent(Intent.ACTION_VIEW);
              open.setDataAndType(uri, "audio/mpeg");
              startActivity(Intent.createChooser(open, "Open downloaded files"));
             return true;*/
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
            case R.id.screenshot:
                ShareScreenshot ss = new ShareScreenshot(MainActivity.this);
                ss.shareImage();
                return true;
            case R.id.restart:
                Toast.makeText(this, "Clean slate..", Toast.LENGTH_SHORT)
                        .show();
                Intent restart = new Intent(MainActivity.this, MainActivity.class);
                startActivity(restart);
                finish();
                return true;
            case R.id.exit:
                finish();

        }
        return super.onOptionsItemSelected(item);
    }
}
