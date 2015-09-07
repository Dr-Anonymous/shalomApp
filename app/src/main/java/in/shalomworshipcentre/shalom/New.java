package in.shalomworshipcentre.shalom;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

public class New extends ActionBarActivity {
    private ImageView one = null;
    private ImageView two = null;
    private ImageView a = null;
    private ImageView b = null;
    private ImageView c = null;
    private ImageView d = null;
    private ImageView e = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        final WebView myWebView = (WebView) findViewById(R.id.main);
        one = (ImageView) findViewById(R.id.show);
        two = (ImageView) findViewById(R.id.hide);
        a = (ImageView) findViewById(R.id.a);
        b = (ImageView) findViewById(R.id.b);
        c = (ImageView) findViewById(R.id.c);
        d = (ImageView) findViewById(R.id.d);
        e = (ImageView) findViewById(R.id.e);

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
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl("file:///android_asset/index.html");
    }

}
