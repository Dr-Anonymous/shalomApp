package in.shalomworshipcentre.shalom;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
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

import com.parse.ParseException;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {
    private ImageView one = null;
    private ImageView two = null;
    private ImageView a = null;
    private ImageView b = null;
    private ImageView c = null;
    private ImageView d = null;
    private ImageView e = null;
    private ImageView f = null;
    public Todo todo;
    private FrameLayout mContainer;
    private WebView myWebView;
    private WebView mWebviewPop;
    public static String homeUrl;
    public ProgressBar progress;
    String settingsTAG;
    SharedPreferences prefs;
    boolean rb0;
    WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //show help screen on first run
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);
        if (isFirstRun) {
            Intent first = new Intent(MainActivity.this, New.class);
            startActivity(first);
        }
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();


        setContentView(R.layout.activity_main);//layout
        // layout initialising
        myWebView = (WebView) findViewById(R.id.main);
        mContainer = (FrameLayout) findViewById(R.id.webview_frame);
        mWebviewPop = (WebView) findViewById(R.id.webviewPop);
      /*  Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                // DO DELAYED STUFF
            }
        }, 3000);*/
        one = (ImageView) findViewById(R.id.show);
        one.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                one.setVisibility(View.INVISIBLE);
                two.setVisibility(View.VISIBLE);
                a.setVisibility(View.VISIBLE);
                b.setVisibility(View.VISIBLE);
                c.setVisibility(View.VISIBLE);
                d.setVisibility(View.VISIBLE);
                e.setVisibility(View.VISIBLE);
                f.setVisibility(View.VISIBLE);

            }
        });
        two = (ImageView) findViewById(R.id.hide);
        two.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                two.setVisibility(View.INVISIBLE);
                a.setVisibility(View.INVISIBLE);
                b.setVisibility(View.INVISIBLE);
                c.setVisibility(View.INVISIBLE);
                d.setVisibility(View.INVISIBLE);
                e.setVisibility(View.INVISIBLE);
                f.setVisibility(View.INVISIBLE);
                one.setVisibility(View.VISIBLE);
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
                    Toast.makeText(MainActivity.this, "Offline - Reload", Toast.LENGTH_SHORT).show();
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
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
                overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top);
            }
        });

        settingsTAG = "AppSettings";
        prefs = getSharedPreferences(settingsTAG, 0);
        rb0 = prefs.getBoolean("rb0", false);
        if (rb0) {
            homeUrl = "http://shalomworshipcentre.in/mobile.html";
        } else {
            homeUrl = "http://shalomworshipcentre.in/";

        }
        //intent passed from about activity
        if (getIntent().getBooleanExtra("off", false)) {
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            return;
        }
        if (getIntent().getBooleanExtra("on", false)) {
            Toast.makeText(MainActivity.this, "smartHome on", Toast.LENGTH_SHORT).show();
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            return; // add this to prevent from doing unnecessary stuffs
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
        //cache path ---
        //webSettings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        //allow file access
        webSettings.setAllowFileAccess(true);
        //cache enabled
        webSettings.setAppCacheEnabled(true);
        // load online by default
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        if (!DetectConnection.checkInternetConnection(this)) {// loading offline
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        //HTML5 localstorage feature
        webSettings.setDomStorageEnabled(true);
        //zoom
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        // progress bar
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);

        // load the url
        myWebView.loadUrl(homeUrl);

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
        myWebView.setDownloadListener(new MyDownloadListener(myWebView.getContext()));
        // parse code
        try {
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String jsonData = extras.getString("com.parse.Data");
                JSONObject json;
                json = new JSONObject(jsonData);
                String pushStore = json.getString("alert");
                //data.setText(pushStore);
                final Intent a = new Intent(MainActivity.this, Notif.class);
                //a.putExtra(EXTRA_MESSAGE, pushStore);
                startActivity(a);
                todo = new Todo();
                todo.setUuidString();
                todo.setTitle(pushStore);
                todo.setDraft(true);
                todo.pinInBackground(Application.TODO_GROUP_NAME,
                        new SaveCallback() {

                            @Override
                            public void done(ParseException e) {
                                if (isFinishing()) {
                                    return;
                                }
                                if (e == null) {
                                    setResult(Activity.RESULT_OK);
                                }
                            }
                        });
            }
        } catch (JSONException e) {
        }
    }

    private class UriWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, final String url) {
            String host = Uri.parse(url).getHost();
            if (host.contains("shalomworshipcentre.in")) {
                // This is my web site, so do not override; let my WebView load
                if (mWebviewPop != null) {
                    mWebviewPop.setVisibility(View.GONE);
                    mContainer.removeView(mWebviewPop);
                    mWebviewPop = null;
                }
                return false;
            }
            if ((host.contains("facebook")) && rb0) {
                view.loadUrl("https://m.facebook.com/shalomworshipcentre.kkd");
                return false;
            }
            if (host.contains("facebook")) {
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
            // API level 5: WebViewClient.ERROR_HOST_LOOKUP
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
            mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
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
            two.setVisibility(View.INVISIBLE);
            a.setVisibility(View.INVISIBLE);
            b.setVisibility(View.INVISIBLE);
            c.setVisibility(View.INVISIBLE);
            d.setVisibility(View.INVISIBLE);
            e.setVisibility(View.INVISIBLE);
            f.setVisibility(View.INVISIBLE);
            one.setVisibility(View.VISIBLE);
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
                one.setVisibility(View.INVISIBLE);
                two.setVisibility(View.VISIBLE);
                a.setVisibility(View.VISIBLE);
                b.setVisibility(View.VISIBLE);
                c.setVisibility(View.VISIBLE);
                d.setVisibility(View.VISIBLE);
                e.setVisibility(View.VISIBLE);
                f.setVisibility(View.VISIBLE);
            } else {
                two.setVisibility(View.INVISIBLE);
                a.setVisibility(View.INVISIBLE);
                b.setVisibility(View.INVISIBLE);
                c.setVisibility(View.INVISIBLE);
                d.setVisibility(View.INVISIBLE);
                e.setVisibility(View.INVISIBLE);
                f.setVisibility(View.INVISIBLE);
                one.setVisibility(View.VISIBLE);
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
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

    /* case R.id.downloads:
         Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                + "");
         Intent open = new Intent(Intent.ACTION_VIEW);
          open.setDataAndType(uri, "audio/mpeg");
          startActivity(Intent.createChooser(open, "Open downloaded files"));
         return true;
        case R.id.exit:
            finish();}return super.onOptionsItemSelected(item);}*/

}