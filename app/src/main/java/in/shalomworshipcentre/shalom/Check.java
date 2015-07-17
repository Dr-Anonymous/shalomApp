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
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class Check extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView myWebView = (WebView) findViewById(R.id.main);
        myWebView.loadUrl("http://shalomworshipcentre.in/appupdate.html");
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebChromeClient(new WebChromeClient());
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        myWebView.setWebViewClient(new WebViewClient());
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setAllowFileAccess(true);

        myWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "shalom.apk");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);

            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_check, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                Toast.makeText(this, "Refreshing..", Toast.LENGTH_SHORT)
                        .show();
                WebView myWebView = (WebView) findViewById(R.id.main);
                myWebView.clearCache(true);
                myWebView.reload();
                return true;
            case R.id.about:
                Toast.makeText(this, "Opening..", Toast.LENGTH_SHORT)
                        .show();
                Intent about = new Intent(Check.this, About.class);
                startActivity(about);
                finish();
                return true;
            case R.id.exit:
                Intent exit = new Intent(this, MainActivity.class);
                exit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                exit.putExtra("Exit", true);
                startActivity(exit);
                finish();

        }
        return super.onOptionsItemSelected(item);

    }
}
