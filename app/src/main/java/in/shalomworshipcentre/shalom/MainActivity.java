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
        if( getIntent().getBooleanExtra("Exit", false)){
            finish();
            return; // add this to prevent from doing unnecessary stuffs
        }


        //url loading
        WebView myWebView = (WebView) findViewById(R.id.main);
        myWebView.loadUrl("http://www.shalomworshipcentre.in");

        // actionbar overlay
        //getWindow().requestFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);

        //chrome client
        myWebView.setWebChromeClient(new WebChromeClient());
        //cache enabled
        myWebView.getSettings().setAppCacheEnabled(true);
        //the way the cache is used
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //Enabling JavaScript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Function to load all URLs in same webview
        myWebView.setWebViewClient(new WebViewClient());
        //zoom feature
        myWebView.getSettings().setBuiltInZoomControls(true);
        // dont show zoom controls
        myWebView.getSettings().setDisplayZoomControls(false);
        //allow file access-- not sure what it is...
        myWebView.getSettings().setAllowFileAccess(true);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //hide action bar
        actionBar.hide();
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
                Intent today = new Intent(MainActivity.this, Today.class);
                startActivity(today);
                return true;

            case R.id.refresh:
                Toast.makeText(this, "Refreshing..", Toast.LENGTH_SHORT)
                        .show();
                WebView myWebView = (WebView) findViewById(R.id.main);
                myWebView.reload();
                return true;

            case R.id.about:
                Toast.makeText(this, "Opening..", Toast.LENGTH_SHORT)
                        .show();
                Intent about = new Intent(MainActivity.this, About.class);
                startActivity(about);
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
