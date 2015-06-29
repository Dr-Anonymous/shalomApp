package in.shalomworshipcentre.shalom;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class Today extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView myWebView = (WebView) findViewById(R.id.main);

        //url loading
        myWebView.loadUrl("https://www.facebook.com/pages/Shalom-Worship-Centre/696870770385720");
        //chrome client
        myWebView.setWebChromeClient(new WebChromeClient());
        //cache enabled
        myWebView.getSettings().setAppCacheEnabled(true);
        //Enabling JavaScript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //form data and password
        webSettings.setSaveFormData(true);
        //cookie accepted
        CookieManager.getInstance().setAcceptCookie(true);
        //the way the cache is used
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //Handling Page Navigation in same view
        myWebView.setWebViewClient(new WebViewClient());
        //zoom feature
        webSettings.setBuiltInZoomControls(true);
        // dont show zoom controls
        webSettings.setDisplayZoomControls(false);
        //disabling debugging in webview
        WebView.setWebContentsDebuggingEnabled(false);

        //download using download manager
        myWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "SHALOM");
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
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_today, menu);

        //hide action bar
        //actionBar.hide();
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Toast.makeText(this, "Opening..", Toast.LENGTH_SHORT)
                        .show();
                Intent about = new Intent(Today.this, About.class);
                startActivity(about);
                finish();
                return true;
            case R.id.refresh:
                Toast.makeText(this, "Refreshing..", Toast.LENGTH_SHORT)
                        .show();
                WebView myWebView = (WebView) findViewById(R.id.main);
                myWebView.reload();
                return true;
            case R.id.exit:
                Intent exit = new Intent(this, MainActivity.class);
                exit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                exit.putExtra("Exit", true);
                startActivity(exit);
                finish();


            default:
                return super.onOptionsItemSelected(item);
        }
    }


}