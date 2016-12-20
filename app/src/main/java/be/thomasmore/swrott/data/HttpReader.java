package be.thomasmore.swrott.data;

/**
 * Created by koenv on 13-12-2016.
 */
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpReader extends AsyncTask<String, Void, String> {

    public interface OnResultReadyListener {
        public void resultReady(String result);
    }

    private OnResultReadyListener onResultReadyListener;

    public void setOnResultReadyListener(OnResultReadyListener listener) {
        onResultReadyListener = listener;
    }

    private Exception exception;

    public HttpReader(OnResultReadyListener listener) {
        super();
        setOnResultReadyListener(listener);
    }

    protected String doInBackground(String... urls) {
        String text = null;

        try {
            HttpURLConnection urlConnection =
                    (HttpURLConnection) new URL(urls[0]).openConnection();

            try {
                InputStream in = new BufferedInputStream(
                        urlConnection.getInputStream());
                text = convertStreamToString(in);
            }
            finally {
                urlConnection.disconnect();
            }
        } catch (Exception ex) {
            Log.e("Error: ", ex.getMessage());

        }
        return text;
    }

    private String convertStreamToString(InputStream is) {
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    protected void onPostExecute(String result) {
        onResultReadyListener.resultReady(result);
    }

}
