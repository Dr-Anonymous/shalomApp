package in.shalomworshipcentre.shalom;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class KnockKnock extends AsyncTask<String, Void, String> {
    private String v = "nil2";

    public KnockKnock(String tv) {
        v = tv;
    }

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

    protected void onPostExecute(String result) {
        if (v != null) {
            v = result;

        }
    }
}