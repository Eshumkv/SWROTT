package be.thomasmore.swrott;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import be.thomasmore.swrott.data.Stats;

/**
 * Created by koenv on 8-12-2016.
 */
public class Helper {

    public static final int MAXTEAMS = 7;
    public static final int MAXMEMBERS = 5;

    public final static String TEAMID_MESSAGE = "be.thomasmore.swrott.TEAMID_MESSAGE";
    public final static String MEMBERID_MESSAGE = "be.thomasmore.swrott.MEMBERID_MESSAGE";
    public final static String OUTCOME_MESSAGE = "be.thomasmore.swrott.OUTCOME_MESSAGE";

    public final static Random _rand = new Random();

    public final static List<String> PICTURES = Arrays.asList(new String[]{
            "profile_default.jpg",
            "profile_1.jpg",
            "profile_2.jpg",
            "profile_3.jpg",
            "profile_4.jpg",
            "profile_5.jpg",
            "profile_6.jpg",
            "profile_7.jpg",
            "profile_8.jpg",
            "profile_9.jpg",
            "profile_10.jpg",
            "profile_11.jpg",
            "profile_12.jpg",
            "profile_13.jpg",
            "profile_14.jpg",
            "profile_15.jpg"
    });

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

    public static int randomBetween(int min, int max) {
        return _rand.nextInt((max - min) + 1) + min;
    }

    public static <T> void showErrorDialog(final Context c, final Class<T> cls) {
        new AlertDialog.Builder(c)
            .setTitle(R.string.dialog_error_title)
            .setMessage(R.string.dialog_error_should_not_happen)
            .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(c, cls);
                    c.startActivity(intent);
                    ((Activity)c).finish();
                }
            })
            .create()
            .show();
    }

    public static <T> void showErrorDialog(final Context c, @android.support.annotation.StringRes final int id, final Class<T> cls) {
        new AlertDialog.Builder(c)
                .setTitle(R.string.dialog_error_title)
                .setMessage(id)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(c, cls);
                        c.startActivity(intent);
                        ((Activity)c).finish();
                    }
                })
                .create()
                .show();
    }

    public static <T> long getLongExtra(final Context c, String name, final Class<T> clsOnError) {
        long result = ((Activity)c).getIntent().getLongExtra(name, -1);
        if (result == -1) {
            Log.e("HELPER ERROR", "Seriously don't know what to do");
            if (clsOnError == null) {
                Helper.showErrorDialog(c, MainActivity.class);
            } else {
                Helper.showErrorDialog(c, clsOnError);
            }
            return -1;
        }
        return result;
    }

    public static Bitmap getPicture(Context c, String path, int w, int h) {
        FileInputStream fis = null;

        try {
            File file = new File(
                    c.getFilesDir(),
                    path
            );
            fis = new FileInputStream(file);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(fis, null, options);
            options.inSampleSize = calculateInSampleSize(options, w, h);
            options.inJustDecodeBounds = false;

            fis = new FileInputStream(file);
            return BitmapFactory.decodeStream(fis, null, options);

        } catch (Exception e) {
            Log.e("EDIT", "Couldn't load image!", e);
        } finally {
            if (fis != null) {
                try { fis.close(); } catch (Exception e) {}
            }
        }

        return null;
    }

    public static void updateStatsPart(Stats stats, AppCompatActivity app) {
        TextView txtAttack = (TextView) app.findViewById(R.id.attack);
        TextView txtDefense = (TextView) app.findViewById(R.id.defense);
        TextView txtSpeed = (TextView) app.findViewById(R.id.speed);
        TextView txtLevel = (TextView) app.findViewById(R.id.level);
        TextView txtExpToLevel = (TextView) app.findViewById(R.id.exp_to_level);
        TextView txtHp = (TextView) app.findViewById(R.id.hp);

        txtAttack.setText(String.valueOf(stats.getAttack()));
        txtDefense.setText(String.valueOf(stats.getDefense()));
        txtSpeed.setText(String.valueOf(stats.getSpeed()));
        txtLevel.setText(String.valueOf(stats.getLevel()));
        txtExpToLevel.setText(String.valueOf(stats.getExpToLevel()));
        txtHp.setText(String.valueOf(stats.getHealthPoints()));
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
            Log.e("HELPER deserializeObj", "Could not deserialize object", e);
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

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
