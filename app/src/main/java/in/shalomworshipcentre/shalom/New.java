package in.shalomworshipcentre.shalom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class New extends AppCompatActivity {
    private ImageView one = null;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // layout params applied to the webviews in order to fit 100% the parent container
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        WebView myWebView = new WebView(this);
        myWebView.setLayoutParams(layoutParams);
        FrameLayout mContainer = (FrameLayout) findViewById(R.id.webview_frame);
        mContainer.addView(myWebView);
        one = (ImageView) findViewById(R.id.show);
        one.setVisibility(View.GONE);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl("file:///android_asset/index.html");
        // progress bar
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
