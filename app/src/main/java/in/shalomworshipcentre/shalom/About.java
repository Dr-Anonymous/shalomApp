package in.shalomworshipcentre.shalom;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class About extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);


        //url loading
        WebView myWebView = (WebView) findViewById(R.id.about);
        myWebView.loadUrl("http://www.nrigh.esy.es");


        //chrome client
        myWebView.setWebChromeClient(new WebChromeClient());
        //cache enabled
        myWebView.getSettings().setAppCacheEnabled(true);
        //Enabling JavaScript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //the way the cache is used
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        //Handling Page Navigation in same view
        myWebView.setWebViewClient(new WebViewClient());


        //zoom feature
        myWebView.getSettings().setBuiltInZoomControls(true);
        // dont show zoom controls
        myWebView.getSettings().setDisplayZoomControls(false);

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
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Shalom_App");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);

            }
        });
    }

    // Navigating web page history by clicking back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        WebView myWebView = (WebView) findViewById(R.id.about);
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK) {
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
        getMenuInflater().inflate(R.menu.menu_about, menu);

        //hide action bar
        actionBar.hide();
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.downloads:
                Toast.makeText(this, "Opening..", Toast.LENGTH_SHORT)
                        .show();
                return true;
            case R.id.refresh:
                Toast.makeText(this, "Refreshing..", Toast.LENGTH_SHORT)
                        .show();
                WebView myWebView = (WebView) findViewById(R.id.about);
                myWebView.reload();
                return true;
            case R.id.back:
                Toast.makeText(this, "Back..", Toast.LENGTH_SHORT)
                        .show();
                Intent i = new Intent(About.this, MainActivity.class);
                startActivity(i);
                finish();
                return true;
            //only goes to previous activity-- System.exit(0);



            default:
                return super.onOptionsItemSelected(item);
        }
    }


}