package be.thomasmore.swrott.data;

/**
 * Created by koenv on 13-12-2016.
 */
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import be.thomasmore.swrott.Helper;

public class RootsReader<T> extends AsyncTask<String, Void, List<T>> {

    public interface OnResultReadyListener<T> {
        public void resultReady(List<T> result);
    }

    private Exception exception;
    private OnResultReadyListener onResultReadyListener;
    private JSONHelper.JSONTypes type;

    public RootsReader(JSONHelper.JSONTypes type, OnResultReadyListener listener) {
        super();
        setOnResultReadyListener(listener);
        this.type = type;
    }

    public void setOnResultReadyListener(OnResultReadyListener listener) {
        onResultReadyListener = listener;
    }

    protected List<T> doInBackground(String... urls) {
        List<T> result = new ArrayList<>();
        String next = urls[0];

        do {
            try {
                String json = Helper.getJson(next);
                JSONArray results = new JSONObject(json).getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject obj = results.getJSONObject(i);
                    result.add((T)JSONHelper.getObject(type, obj));
                }

                next = JSONHelper.getNextPage(json);

            } catch (Exception e) {
                Log.e("ROOTSREADER", "Error: " + e.toString());
            }
        } while (!next.equals("null"));

        return result;
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

    protected void onPostExecute(List<T> result) {
        onResultReadyListener.resultReady(result);
    }

}
