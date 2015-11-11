package in.shalomworshipcentre.shalom;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private ImageView one = null;
    private ImageView a = null;
    private ImageView b = null;
    private ImageView c = null;
    private ImageView d = null;
    private ImageView e = null;
    private ImageView f = null;
    private FrameLayout mContainer;
    private WebView myWebView, popupView;
    String homeUrl, pushStore;
    private ProgressBar progress;
    static boolean smart, isFirstRun, download;
    WebSettings webSettings;
    /*LoginButton loginButton;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;*/

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
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);
        //loginButton = (LoginButton) findViewById(R.id.login_button);

        one = (ImageView) findViewById(R.id.show);
        one.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showbtn();

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
        smart = getSharedPreferences(About.settings, MODE_PRIVATE).getBoolean("smart", false);
        if (smart) {
            homeUrl = "http://shalomworshipcentre.in/mobile.html";
        } else {
            homeUrl = "http://shalomworshipcentre.in/";
        }
        myWebView.loadUrl(homeUrl);

        //downloading files using external browser or internal download listner
        download = getSharedPreferences(About.settings, MODE_PRIVATE).getBoolean("download", false);
        if (download) {
            getFile(myWebView);
        } else {
            myWebView.setDownloadListener(new MyDownloadListener(myWebView.getContext()));
        }
        //    fb();
        // push notifications-
        parse();
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
            popupView.setWebChromeClient(this);
            popupView.setWebViewClient(new UriWebViewClient());
            popupView.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            ));
            this.container.addView(popupView);
            if (download) {
                getFile(popupView);
            } else {
                popupView.setDownloadListener(new MyDownloadListener(popupView.getContext()));
            }
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
            if (host.contains("shalomworshipcentre.in") || host.contains("facebook")) {
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

   /* public void fb() {
        loginButton.setReadPermissions("user_friends");
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }*/


    // parse code
    public void parse() {
        try {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String jsonData = extras.getString("com.parse.Data");
                JSONObject json = new JSONObject(jsonData);
                pushStore = json.getString("alert");
                if (json.getString("alert").equals("update available")) {
                    if (DetectConnection.checkInternetConnection(MainActivity.this)) {
                        Intent a = new Intent(MainActivity.this, Check.class);
                        startActivity(a);
                    } else
                        Toast.makeText(MainActivity.this, "Enable internet to view updates", Toast.LENGTH_LONG).show();
                } else {
                    Intent a = new Intent(MainActivity.this, Notif.class);
                    a.putExtra("txt", pushStore);
                    startActivity(a);
                }
            }
        } catch (JSONException e) {
        } catch (NullPointerException n) {
        }
    }

    public void getFile(WebView view) {
        view.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }
        /*  Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // DO DELAYED STUFF
                    }
                }, 3000);*/
}