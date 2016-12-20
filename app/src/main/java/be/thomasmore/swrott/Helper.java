package be.thomasmore.swrott;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by koenv on 8-12-2016.
 */
public class Helper {

    public final static String TEAMID_MESSAGE = "be.thomasmore.swrott.TEAMID_MESSAGE";

    public static String getXmlString(Context c, int id) {
        return c.getResources().getString(id);
    }

    public static void AddHeader(Context context, ListView list, int stringId) {
        TextView textview = new TextView(context);
        textview.setText(context.getResources().getString(stringId));
        list.addHeaderView(textview);
    }

    public static String ReadFromAssets(Context context, String filename) {
        AssetManager assetManager = context.getAssets();
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();

        try {
            reader = new BufferedReader(
                    new InputStreamReader(assetManager.open(filename)));

            String mLine;
            while ((mLine = reader.readLine()) != null) {
                sb.append(mLine);
            }
        } catch (IOException e) {
            Log.e("HELPER ReadFromAssets", "Couldn't read: " + e.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

        return sb.toString();
    }

    public static String getRandomTeamName() {
        final Random random = new Random();
        final String[] part1 = {
                "Midnight",
                "Awesome",
                "Bespin",
                "Raging",
                "Delta",
                "Lightning",
                "Cloud",
                "Sky",
                "Dancing"
        };
        final String[] part2 = {
                "Commandos",
                "Wasps",
                "Predators",
                "Jedi",
                "Sith",
                "Wasps",
                "Spiders",
                "Sabers",
                "Speeders"
        };

        StringBuilder sb = new StringBuilder();

        if (random.nextBoolean())
            sb.append("The ");

        sb.append(part1[random.nextInt(part1.length)]);
        sb.append(" ");
        sb.append(part2[random.nextInt(part2.length)]);

        return sb.toString();
    }

    public static boolean writeObject(Context c, String filename, Object o) {
        if (Helper.serializeObj(c, filename, o)) {
            if (Helper.copy(c, filename, filename)) {
                return true;
            }
        }

        return false;
    }

    public static boolean serializeObj(Context c, String filename, Object o) {
        FileOutputStream fos = null;
        ObjectOutputStream os = null;
        boolean result = false;

        try {
            fos = c.openFileOutput(filename, Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(o);
            os.flush();
            result = true;
        } catch (Exception e) {
            Log.e("HELPER serializeObj", "Could not serialize object: " + e.toString());
        } finally {
            if (os != null)
                try { os.close(); } catch (Exception e) {}
            if (fos != null)
                try { fos.close(); } catch (Exception e) {}
        }

        return result;
    }

    public static <T> T deserializeObj(Context c, String filename, boolean fromAssets) {
        FileInputStream fis = null;
        ObjectInputStream is = null;
        T result = null;

        try {
            if (fromAssets) {
                AssetManager assetManager = c.getAssets();
                //AssetFileDescriptor afd = assetManager.openFd(filename);
                is = new ObjectInputStream(assetManager.open(filename));
            } else {
                fis = c.openFileInput(filename);
                is = new ObjectInputStream(fis);
            }

            result = (T) is.readObject();
        } catch (Exception e) {
            Log.e("HELPER deserializeObj", "Could not deserialize object: " + e.getStackTrace());
        } finally {
            if (is != null)
                try { is.close(); } catch (Exception e) {}
            if (fis != null)
                try { fis.close(); } catch (Exception e) {}
        }

        return result;
    }

    public static boolean copy(Context c, String filename, String sdfilename) {
        InputStream in = null;
        OutputStream out = null;

        try {
            File root = android.os.Environment.getExternalStorageDirectory();
            File file = new File(new File(root.getAbsolutePath() + "/Download"), sdfilename);

            in = c.openFileInput(filename);
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (in != null)
                try { in.close(); } catch (Exception e) {}
            if (out != null)
                try { out.close(); } catch (Exception e) {}
        }
    }

    public static String getJson(String urlToResource) {
        HttpURLConnection conn = null;
        String result = null;

        try {
            conn = (HttpURLConnection) new URL(urlToResource).openConnection();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"), 8
            );
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            result = sb.toString();
        } catch (Exception e) {
            try{if(conn != null)conn.disconnect();}catch(Exception squish){}
        }

        return result;
    }

    public static boolean isInternetAvailable() {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<Boolean> future = es.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return isInternetAvailableHelper();
            }
        });

        boolean result = false;
        try {
            result = future.get();
        } catch (Exception e) {
        } finally {
            es.shutdown();
        }
        return result;
    }

    private static boolean isInternetAvailableHelper() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }

    public static String atos(String[] s, String between) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s.length; i++) {
            if (i != 0)
                sb.append(between);

            sb.append(i).append(": ").append(s[i]);
        }

        return sb.toString();
    }
}
