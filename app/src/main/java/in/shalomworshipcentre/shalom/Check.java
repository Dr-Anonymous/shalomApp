package in.shalomworshipcentre.shalom;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class Check extends AppCompatActivity {
    TextView tvVersionName;
    WebView myWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check);
        myWebView = (WebView) findViewById(R.id.check);
        // display current version
        Context context = getApplicationContext(); // or activity.getApplicationContext()
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        String myVersionName = "Version not available."; // initialize String
        try {
            myVersionName = packageManager.getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        // set version name to a TextView
        tvVersionName = (TextView) findViewById(R.id.versionName);
        tvVersionName.setText("Version installed : " + myVersionName);
        myWebView.setWebChromeClient(new WebChromeClient());
        myWebView.setWebViewClient(new WebViewClient());
        //downloading files using external browser or internal download listner
        if (MainActivity.download) {
            myWebView.setDownloadListener(new DownloadListener() {
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
        } else {
            myWebView.setDownloadListener(new MyDownloadListener(myWebView.getContext()));
        }
        myWebView.loadUrl("http://shalomworshipcentre.in/appupdate.html");

        // readWebpage(myWebView);

    }

    public void readWebpage(View v) {
        DownloadPage task = new DownloadPage(myWebView);
        task.execute(new String[]{"http://shalomworshipcentre.in/"});
    }

    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack())
            myWebView.goBack();
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
}