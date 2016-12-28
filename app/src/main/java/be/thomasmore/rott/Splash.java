package be.thomasmore.rott;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import be.thomasmore.rott.data.DatabaseHelper;
import be.thomasmore.rott.data.HttpReader;
import be.thomasmore.rott.data.JSONHelper;
import be.thomasmore.rott.data.People;
import be.thomasmore.rott.data.Planet;
import be.thomasmore.rott.data.RootsReader;
import be.thomasmore.rott.data.Species;

public class Splash extends AppCompatActivity {

    private final static int SPLASH_WAIT_TIME = 1500;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Get a reference to the database
        db = new DatabaseHelper(this);

        // See if we have a few items in the db
        // If we have, we probably updated
        if (db.getPlanetsCount() > 20) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    done();
                    goToMain();
                }
            }, SPLASH_WAIT_TIME);
            return;
        }

        // If we don't have internet, what then??!
        if (!Helper.isInternetAvailable()) {
            // No internet :(

            // There is not enough data, maybe load the old data?
            // If not, there's nothing we can do.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                .setMessage(R.string.dialog_no_internet)
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Quit
                        Splash.this.finish();
                        System.exit(0);
                    }
                })
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Load the old data (bundled with apk)
                        List<Planet> planets = Helper.deserializeObj(getApplicationContext(), "planets.ser", true);
                        List<Species> species = Helper.deserializeObj(getApplicationContext(), "species.ser", true);
                        List<People> people = Helper.deserializeObj(getApplicationContext(), "people.ser", true);

                        // This is pretty inefficient, but seems like the best way
                        db.deleteAllPlanets();
                        db.deleteAllSpecies();
                        db.deleteAllPeople();

                        db.insertPlanets(planets);
                        db.insertSpecies(species);
                        db.insertPeoples(people);

                        // Loaded the data, so now just wait a bit
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                done();
                                goToMain();
                            }
                        }, SPLASH_WAIT_TIME);
                    }
                });
            builder.create().show();

            return;
        }

        // Create a loading spinner
        FragmentManager fm = getSupportFragmentManager();
        final MySpinnerDialog spinner = new MySpinnerDialog();
        spinner.show(fm, "some_tag");

        // Create a list to hold results
        // Hacky way of getting data from a thread
        final int listSize = 3 * 2 + 1;     // 3 threads (which might call another thread)
        final List<Boolean> results = new ArrayList<>(listSize);

        // Make the thread to check whether we've loaded
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (results.size() < listSize) {
                    SystemClock.sleep(500); // NEVER on UI-THREAD!!!!
                }

                // We're done loading
                spinner.dismiss();
                goToMain();
            }
        }).start();

        // Execute all the HTTPReaders to download Planets, Species and People
        // This tends to take a while.
        // When we need to update, we use a RootsReader. This is used because SWAPI
        // gives you back 10 results only and a link to the next 10 results.
        // We need to parse them all, so there's a specific class to do that.
        final String planetUrl = "http://swapi.co/api/planets/";
        new HttpReader(new HttpReader.OnResultReadyListener() {
            @Override
            public void resultReady(String result) {
                int apiCount = JSONHelper.getCount(result);
                int dbPlanetCount = db.getPlanetsCount();

                if (apiCount != dbPlanetCount) {
                    // Database is not synced
                    new RootsReader<>(JSONHelper.JSONTypes.Planet, new RootsReader.OnResultReadyListener() {
                        @Override
                        public void resultReady(List result) {
                            List<Planet> planets = (List<Planet>)result;
                            db.deleteAllPlanets();
                            db.insertPlanets(planets);
                            results.add(true);
                        }
                    }).execute(planetUrl);
                } else {
                    // DB is *probably* synced
                    results.add(true);
                }

                // Done
                results.add(true);
            }
        }).execute(planetUrl);

        final String peopleUrl = "http://swapi.co/api/people/";
        new HttpReader(new HttpReader.OnResultReadyListener() {
            @Override
            public void resultReady(String result) {
                int apiCount = JSONHelper.getCount(result);
                int dbCount = db.getPeopleCount();

                if (apiCount != dbCount) {
                    // Database is not synced
                    new RootsReader<>(JSONHelper.JSONTypes.People, new RootsReader.OnResultReadyListener() {
                        @Override
                        public void resultReady(List result) {
                            List<People> people = (List<People>)result;
                            db.deleteAllPeople();
                            db.insertPeoples(people);
                            results.add(true);
                        }
                    }).execute(peopleUrl);
                } else {
                    // DB is *probably* synced
                    results.add(true);
                }

                // Done
                results.add(true);
            }
        }).execute(peopleUrl);

        final String speciesUrl = "http://swapi.co/api/species/";
        new HttpReader(new HttpReader.OnResultReadyListener() {
            @Override
            public void resultReady(String result) {
                int apiCount = JSONHelper.getCount(result);
                int dbCount = db.getSpeciesCount();

                if (apiCount != dbCount) {
                    // Database is not synced
                    new RootsReader<>(JSONHelper.JSONTypes.Species, new RootsReader.OnResultReadyListener() {
                        @Override
                        public void resultReady(List result) {
                            List<Species> species = (List<Species>)result;
                            db.deleteAllSpecies();
                            db.insertSpecies(species);
                            results.add(true);
                        }
                    }).execute(speciesUrl);
                } else {
                    // DB is *probably* synced
                    results.add(true);
                }

                // Done
                results.add(true);
            }
        }).execute(speciesUrl);

        // Handler to start the MainActivity
        // Close this splash after specified time
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                done();
                results.add(true);
            }
        }, SPLASH_WAIT_TIME);
    }

    private void done() {
        // Make sure the pictures are where they need to be
        handlePictures();
        // Delete any remaining SYSTEM teams and members
        db.cleanUpTeamAndMembers();
    }

    /**
     * This makes sure the pictures that are bundled with the app are on the
     * device. Otherwise, we can't load it well.
     */
    private void handlePictures() {
        // Handle pictures
        // If the standard picture does not exist, just add it
        for (String path: Helper.PICTURES) {
            String[] parts = path.split("\\.");
            int resourceId = getResources().getIdentifier(
                    parts[0],
                    "drawable",
                    getPackageName()
            );
            //Drawable drawable = getResources().getDrawable(R.drawable.profile_default);
            Drawable drawable = getResources().getDrawable(resourceId);
            File file = new File(
                    getApplicationContext().getFilesDir(),
                    path
            );

            if (file.exists() || drawable == null) {
                continue;
            }

            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            final byte[] bitmapdata = stream.toByteArray();

            OutputStream out = null;
            try {
                out = new FileOutputStream(file);
                out.write(bitmapdata);
                // Needed to free some memory
                bitmap.recycle();
            } catch (Exception e) {
                Log.e("Splash", "Could not copy default profile image: " + e);
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
