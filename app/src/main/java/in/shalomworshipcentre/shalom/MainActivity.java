package in.shalomworshipcentre.shalom;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private ImageView one = null;
    private ImageView two = null;
    private ImageView a = null;
    private ImageView b = null;
    private ImageView c = null;
    private ImageView d = null;
    private ImageView e = null;
    private ImageView f = null;
    private FrameLayout mContainer;
    private WebView myWebView, mWebviewPop;
    static String homeUrl, pushStore, activity;
    private ProgressBar progress;
    boolean smart, isFirstRun;
    WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //show help screen on first run
        isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
            Intent first = new Intent(MainActivity.this, New.class);
            startActivity(first);
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
        }

        // layout initialising
        setContentView(R.layout.activity_main);
        mContainer = (FrameLayout) findViewById(R.id.webview_frame);
        myWebView = (WebView) findViewById(R.id.main);
        mWebviewPop = (WebView) findViewById(R.id.webviewPop);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);

        one = (ImageView) findViewById(R.id.show);
        one.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showbtn();

            }
        });
        two = (ImageView) findViewById(R.id.hide);
        two.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                hidebtn();
            }
        });
        a = (ImageView) findViewById(R.id.a);
        a.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent about = new Intent(MainActivity.this, About.class);
                startActivity(about);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        b = (ImageView) findViewById(R.id.b);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent notif = new Intent(MainActivity.this, Notif.class);
                startActivity(notif);
                overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);

            }
        });
        c = (ImageView) findViewById(R.id.c);
        c.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!DetectConnection.checkInternetConnection(MainActivity.this)) {
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
                Intent browse = new Intent(MainActivity.this, FileBrowser.class);
                startActivity(browse);
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
            }
        });

        //intent passed from 'about' activity
        if (getIntent().getBooleanExtra("restart", false)) {
            restart();
            return;
        }

        //Enabling JavaScript
        webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        CookieManager.getInstance().setAcceptCookie(true);
        //chrome client
        myWebView.setWebChromeClient(new UriChromeClient());
        // Function to load URLs in same webview
        myWebView.setWebViewClient(new UriWebViewClient());
        //allow file access
        webSettings.setAllowFileAccess(true);
        //cache enabled
        webSettings.setAppCacheEnabled(true);
        // load online by default
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        if (!DetectConnection.checkInternetConnection(this)) {
            // loading offline
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            Toast.makeText(MainActivity.this, "Offline mode", Toast.LENGTH_SHORT).show();
        }
        //HTML5 localstorage feature
        webSettings.setDomStorageEnabled(true);
        //zoom
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        // smartHome url or not
        smart = getSharedPreferences(About.settingsTAG, MODE_PRIVATE).getBoolean("smart", false);
        if (smart) {
            homeUrl = "http://shalomworshipcentre.in/mobile.html";
        } else {
            homeUrl = "http://shalomworshipcentre.in/";
        }
        myWebView.loadUrl(homeUrl);
        myWebView.setDownloadListener(new MyDownloadListener(myWebView.getContext()));
        // push notifications-
        parse();
    }

    private class UriWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, final String url) {
            String host = Uri.parse(url).getHost();
            if (host.contains("shalomworshipcentre.in") || host.contains("facebook")) {
                // This is my web site, so do not override; let my WebView load
               /* if (mWebviewPop != null) {
                    mWebviewPop.setVisibility(View.GONE);
                    mContainer.removeView(mWebviewPop);
                    mWebviewPop = null;
                }*/
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

    class UriChromeClient extends WebChromeClient {
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            mWebviewPop = new WebView(MainActivity.this);
            WebSettings webSettings = mWebviewPop.getSettings();
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);
            mWebviewPop.setWebViewClient(new UriWebViewClient());
            webSettings.setJavaScriptEnabled(true);
            mContainer.addView(mWebviewPop);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebviewPop);
            resultMsg.sendToTarget();
            return true;
        }
    }

    // functions of back & menu hard keys
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && one.getVisibility() == View.INVISIBLE) {
            hidebtn();
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack() && one.getVisibility() == View.VISIBLE) {
            myWebView.goBack();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebviewPop != null && mWebviewPop.canGoBack()) {
            mWebviewPop.goBack();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebviewPop != null && !mWebviewPop.canGoBack()) {
            mWebviewPop.setVisibility(View.GONE);
            mContainer.removeView(mWebviewPop);
            mWebviewPop = null;
            return true;
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
        two.setVisibility(View.VISIBLE);
        a.setVisibility(View.VISIBLE);
        b.setVisibility(View.VISIBLE);
        c.setVisibility(View.VISIBLE);
        d.setVisibility(View.VISIBLE);
        e.setVisibility(View.VISIBLE);
        f.setVisibility(View.VISIBLE);
    }

    private void hidebtn() {
        two.setVisibility(View.INVISIBLE);
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
            Toast.makeText(getBaseContext(), "Tap once more to exit", Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }

    public void restart() {
        Toast.makeText(MainActivity.this, "Reloading..", Toast.LENGTH_SHORT).show();
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    // parse code
    public void parse() {
        try {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String jsonData = extras.getString("com.parse.Data");
                JSONObject json = new JSONObject(jsonData);
                pushStore = json.getString("alert");
                activity = json.getString("activity");
                Intent a = new Intent(MainActivity.this, Notif.class);
                startActivity(a);
                if (activity.equals("check")) {
                    Intent c = new Intent(MainActivity.this, Check.class);
                    startActivity(c);
                }
            }
        } catch (JSONException e) {
        }
    }
    //downloading files using external browser
      /*  myWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });*/
        /*  Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // DO DELAYED STUFF
                    }
                }, 3000);*/
}