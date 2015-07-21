package in.shalomworshipcentre.shalom;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
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
    public Todo todo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
      /*  Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);
        if (isFirstRun) {
            Toast.makeText(getApplicationContext(), "Hit 'Refresh' to fetch new data", Toast.LENGTH_LONG).show();
        }
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();*/
        setContentView(R.layout.activity_main);
        final WebView myWebView = (WebView) findViewById(R.id.main);

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
                one.setVisibility(View.VISIBLE);
            }
        });
        a = (ImageView) findViewById(R.id.a);
        a.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent about = new Intent(MainActivity.this, About.class);
                startActivity(about);
            }
        });
        b = (ImageView) findViewById(R.id.b);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent notif = new Intent(MainActivity.this, Notif.class);
                startActivity(notif);
            }
        });
        c = (ImageView) findViewById(R.id.c);
        c.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!DetectConnection.checkInternetConnection(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "Offline - Reload", Toast.LENGTH_SHORT).show();
                    Intent mStartActivity = new Intent(MainActivity.this, MainActivity.class);
                    finish();
                    startActivity(mStartActivity);

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
                share.putExtra(Intent.EXTRA_TEXT, Url);
                share.setType("text/plain");
                startActivity(share);
            }
        });
        e = (ImageView) findViewById(R.id.e);
        e.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

        //exit pressed from next activity
        if (getIntent().getBooleanExtra("Exit", false)) {
            finish();
            return; // add this to prevent from doing unnecessary stuffs
        }
        //Enabling JavaScript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        //chrome client
        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
                WebView newWebView = new WebView(MainActivity.this);
                //addView(newWebView);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
                return true;
            }
        });
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
        //zoom
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
                dm.enqueue(request);}});*/
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
                Toast.makeText(getBaseContext(), "New Message", Toast.LENGTH_SHORT).show();
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
                                    //  then start notif activity
                                } else {
                                }
                            }
                        });
            }
        } catch (JSONException e) {
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView myWebView = (WebView) findViewById(R.id.main);
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
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
            } else {
                two.setVisibility(View.INVISIBLE);
                a.setVisibility(View.INVISIBLE);
                b.setVisibility(View.INVISIBLE);
                c.setVisibility(View.INVISIBLE);
                d.setVisibility(View.INVISIBLE);
                e.setVisibility(View.INVISIBLE);
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
            return;
        } else {
            Toast.makeText(getBaseContext(), "Tap 'Back' once more to exit", Toast.LENGTH_SHORT).show();
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