package in.shalomworshipcentre.shalom;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class MainActivity extends AppCompatActivity {
    private ImageView one, a, b, c, d, e, f;
    private FrameLayout mContainer;
    private WebView myWebView, popupView;
    private ProgressBar progress;
    String homeUrl;
    static boolean smart, isFirstRun, download, notif;
    WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //show help screen on first run
        isFirstRun = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
            Intent first = new Intent(MainActivity.this, New.class);
            startActivity(first);
            getSharedPreferences("settings", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).apply();
        }

        // layout initialising
        setContentView(R.layout.activity_main);
        mContainer = (FrameLayout) findViewById(R.id.webview_frame);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);

        one = (ImageView) findViewById(R.id.show);
        one.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showbtn();
            }
        });
        a = (ImageView) findViewById(R.id.a);
        a.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                hidebtn();
                Intent about = new Intent(MainActivity.this, About.class);
                startActivity(about);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        b = (ImageView) findViewById(R.id.b);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                hidebtn();
                Intent notif = new Intent(MainActivity.this, Notif.class);
                startActivity(notif);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });
        c = (ImageView) findViewById(R.id.c);
        c.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                hidebtn();
                if (!Helper.checkInternetConnection(MainActivity.this)) {
                    restart();
                } else {
                    Toast.makeText(MainActivity.this, "Refreshing..", Toast.LENGTH_LONG).show();
                    myWebView.reload();
                }
            }
        });
        d = (ImageView) findViewById(R.id.d);
        d.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                hidebtn();
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                String Url = myWebView.getUrl();
                share.putExtra(Intent.EXTRA_TEXT, "Hey! check out " + Url);
                share.setType("text/plain");
                startActivity(share);
                overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top);
            }
        });
        e = (ImageView) findViewById(R.id.e);
        e.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        f = (ImageView) findViewById(R.id.f);
        f.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                hidebtn();
                Intent browse = new Intent(MainActivity.this, FileBrowser.class);
                startActivity(browse);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        //intent passed from 'about' activity
        if (getIntent().getBooleanExtra("restart", false)) {
            restart();
            return;
        }

        // layout params applied to the webviews in order to fit 100% the parent container
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        myWebView = new WebView(this);
        myWebView.setLayoutParams(layoutParams);
        mContainer.addView(myWebView);
        //Enabling JavaScript
        webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        CookieManager.getInstance().setAcceptCookie(true);
        //chrome client
        myWebView.setWebChromeClient(new MyChromeClient(MainActivity.this, myWebView, mContainer));
        // Function to load URLs in same webview
        myWebView.setWebViewClient(new UriWebViewClient());
        //allow file access
        webSettings.setAllowFileAccess(true);
        //cache enabled
        webSettings.setAppCacheEnabled(true);
        // load online by default
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);


        if (!Helper.checkInternetConnection(this)) {
            // loading offline
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            Toast.makeText(MainActivity.this, "Offline mode", Toast.LENGTH_SHORT).show();
        }
        //HTML5 local storage feature
        webSettings.setDomStorageEnabled(true);
        //zoom
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        setDownloader(myWebView);
        // mobile (slow network) url or not
        smart = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("smart", false);
        if (smart) {
            homeUrl = "http://shalomworshipcentre.in/androidApp/mobile.html";
        } else {
            homeUrl = "http://shalomworshipcentre.in";
        }
        myWebView.loadUrl(homeUrl);
        // opted for notifications?
        notif = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("notif", true);
        if (notif && Helper.checkInternetConnection(this)) checkForNotifications();
    }

    public class MyChromeClient extends WebChromeClient {

        protected Activity activity;
        protected WebView parentWebView;
        protected FrameLayout container;

        MyChromeClient(
                Activity activity,
                WebView parentWebView,
                FrameLayout container
        ) {
            super();
            this.activity = activity;
            this.parentWebView = parentWebView;
            this.container = container;
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
            popupView = new WebView(this.activity);

            // setup popuview and add
            webSettings = popupView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setSupportMultipleWindows(true);
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
            popupView.setWebChromeClient(this);
            popupView.setWebViewClient(new UriWebViewClient());
            popupView.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            ));
            this.container.addView(popupView);
            setDownloader(popupView);
            // send popup window infos back to main (cross-document messaging)
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(popupView);
            resultMsg.sendToTarget();
            return true;
        }

        // remove new added webview on close
        @Override
        public void onCloseWindow(WebView window) {
            popupView.setVisibility(WebView.GONE);
        }
    }

    private class UriWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String host = Uri.parse(url).getHost();
            if (host == null) {
                if (url.startsWith("tel:")) {
                    Intent tel = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(tel);
                    return true;
                } else if (url.startsWith("mailto:")) {
                    String body = "Enter your question, prayer request or feedback below:\n\n";
                    Intent mail = new Intent(Intent.ACTION_SEND);
                    mail.setType("application/octet-stream");
                    mail.putExtra(Intent.EXTRA_EMAIL, new String[]{url.substring(url.indexOf(":") + 1, url.length())});
                    mail.putExtra(Intent.EXTRA_SUBJECT, " ");
                    mail.putExtra(Intent.EXTRA_TEXT, body);
                    startActivity(mail);
                    return true;
                }
            } else if (host.contains("shalomworshipcentre.in") || host.contains("facebook") || host.contains("messages")) {
                return false;
            }
            // Otherwise, the link is not for a page on my site
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        // progress bar
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favcon) {
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // hide certain parts of web page
            view.loadUrl("javascript:(function(){" +
                    "var x= document.getElementsByClassName('app');var i;for (i = 0; i < x.length; i++) {x[i].style.display= 'none';}" +
                    "})()");
            progress.setVisibility(View.GONE);
        }

        //  error page
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.e(String.valueOf(errorCode), description);
            if (errorCode == -2) {
                view.loadUrl("file:///android_asset/error.html");
                return;
            }
            // Default behaviour
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }


    // functions of back & menu hard keys
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (one.getVisibility() == View.INVISIBLE) {
                hidebtn();
                return true;
            }
            if (myWebView.canGoBack() && one.getVisibility() == View.VISIBLE) {
                myWebView.goBack();
                return true;
            }
            if (popupView != null && popupView.canGoBack()) {
                popupView.goBack();
                return true;
            }
            if (popupView != null && !popupView.canGoBack()) {
                popupView.setVisibility(View.GONE);
                mContainer.removeView(popupView);
                popupView = null;
                return true;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (one.getVisibility() == View.VISIBLE) {
                showbtn();
            } else {
                hidebtn();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showbtn() {
        one.setVisibility(View.INVISIBLE);
        a.setVisibility(View.VISIBLE);
        b.setVisibility(View.VISIBLE);
        c.setVisibility(View.VISIBLE);
        d.setVisibility(View.VISIBLE);
        e.setVisibility(View.VISIBLE);
        f.setVisibility(View.VISIBLE);
    }

    private void hidebtn() {
        a.setVisibility(View.INVISIBLE);
        b.setVisibility(View.INVISIBLE);
        c.setVisibility(View.INVISIBLE);
        d.setVisibility(View.INVISIBLE);
        e.setVisibility(View.INVISIBLE);
        f.setVisibility(View.INVISIBLE);
        one.setVisibility(View.VISIBLE);
    }

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            return;
        } else {
            Toast.makeText(this, "Tap once more to exit", Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }

    public void restart() {
        Toast.makeText(MainActivity.this, "Reloading..", Toast.LENGTH_SHORT).show();
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void setDownloader(WebView wv) {
        //downloading files using external browser or internal download listner
        download = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("download", false);
        if (download) {
            wv.setDownloadListener(new DownloadListener() {
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
        } else {
            wv.setDownloadListener(new MyDownloadListener(wv.getContext()));
        }
    }

    public void checkForNotifications() {
        checkingForNotif task = new checkingForNotif();
        task.execute(new String[]{"http://shalomworshipcentre.in/androidApp/appNotifications.txt"});
    }

    public class checkingForNotif extends AsyncTask<String, Void, String> {
        private String v;

        protected String doInBackground(String... urls) {
            String responseStr = null;
            try {
                for (String url : urls) {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpGet get = new HttpGet(url);
                    HttpResponse httpResponse = httpClient.execute(get);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    responseStr = EntityUtils.toString(httpEntity);
                }
            } catch (UnsupportedEncodingException e) {

            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }
            return responseStr;
        }

        protected void onPostExecute(String responseStr) {
            v = responseStr;
            // retrieve the notification available locally-
            String newMessage = getSharedPreferences("settings", MODE_PRIVATE).getString("knock", "Nothing new for now :-)");
            // check if anything new & if so--
            if (!newMessage.equals(v)) {
                getSharedPreferences("settings", MODE_PRIVATE).edit().putString("knock", v).commit();
                b.setVisibility(View.VISIBLE);
            }
        }
    }
    /*
      Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //do delay stuff here
                        }
                }, 5000);
     */
}