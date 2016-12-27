package be.thomasmore.swrott;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import be.thomasmore.swrott.data.DatabaseHelper;
import be.thomasmore.swrott.data.HttpReader;
import be.thomasmore.swrott.data.JSONHelper;
import be.thomasmore.swrott.data.People;
import be.thomasmore.swrott.data.Planet;
import be.thomasmore.swrott.data.RootsReader;
import be.thomasmore.swrott.data.Species;

public class Wiki extends AppCompatActivity {

    private DatabaseHelper _db;
    private WikiType _type;
    private long _id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiki);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _db = new DatabaseHelper(this);

        _type = getType();
        _id = getIntent().getLongExtra(Helper.WIKI_MESSAGE, -1);

        if (_type == WikiType.NONE) {
            // Show main page
            showMainPage();
        } else {
            if (_type != WikiType.ListPeople &&
                    _type != WikiType.ListPlanets &&
                    _type != WikiType.ListSpecies &&
                    _id == -1) {
                // Show main page
                showMainPage();
                return;
            }

            switch (_type) {
                case People:
                    showPeoplePage(_id);
                    break;
                case Planet:
                    showPlanetPage(_id);
                    break;
                case Species:
                    showSpeciesPage(_id);
                    break;
                case ListSpecies:
                    showSpeciesList(R.string.wiki_list_species, _db.getAllSpecies());
                    break;
                case ListPeople:
                    showPeopleList(R.string.wiki_list_people, _db.getAllPeople());
                    break;
                case ListPlanets:
                    showPlanetList(R.string.wiki_list_planets, _db.getAllPlanets());
                    break;
                default:
                    // Shouldn't get here
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wiki, menu);

        boolean homeVisible = false;
        boolean refreshVisible = true;

        if (_type == WikiType.NONE) {
            refreshVisible = false;
            homeVisible = false;
        } else if (_type == WikiType.ListPlanets || _type == WikiType.ListSpecies || _type == WikiType.ListPeople) {
            refreshVisible = true;
            homeVisible = true;
        } else {
            refreshVisible = true;
            homeVisible = true;
        }

        menu.findItem(R.id.action_refresh_wiki).setVisible(refreshVisible);
        menu.findItem(R.id.action_wiki_home).setVisible(homeVisible);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_wiki_home:
                gotoWiki(WikiType.NONE, -1);
                return true;
            case R.id.action_refresh_wiki:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private WikiType getType() {
        Object temp = getIntent().getSerializableExtra(Helper.WIKI_TYPE_MESSAGE);

        if (temp == null)
            return WikiType.NONE;

        return (WikiType) temp;
    }

    private void gotoWiki(WikiType type, long id) {
        Intent intent = new Intent(this, Wiki.class);
        intent.putExtra(Helper.WIKI_TYPE_MESSAGE, type);

        if (id != -1) {
            intent.putExtra(Helper.WIKI_MESSAGE, id);
        }

        startActivity(intent);
        finish();
    }

    private void showMainPage() {
        ScrollView parent = (ScrollView) findViewById(R.id.main_view);
        LayoutInflater inflater = getLayoutInflater();
        View main = inflater.inflate(R.layout.wiki_main, null);

        final Button peopleBtn = (Button) main.findViewById(R.id.people);
        final Button planetsBtn = (Button) main.findViewById(R.id.planets);
        final Button speciesBtn = (Button) main.findViewById(R.id.species);

        peopleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWiki(WikiType.ListPeople, -1);
            }
        });

        planetsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWiki(WikiType.ListPlanets, -1);
            }
        });

        speciesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWiki(WikiType.ListSpecies, -1);
            }
        });

        parent.addView(main);
    }

    private void showPeoplePage(long id) {
        ScrollView parent = (ScrollView) findViewById(R.id.main_view);
        LayoutInflater inflater = getLayoutInflater();
        View main = inflater.inflate(R.layout.wiki_people, null);
        final People person = _db.getPeople(id);

        if (person == null) {
            showMainPage();
            return;
        }

        // Fill the view
        final TextView name = (TextView) main.findViewById(R.id.name);
        final TextView height = (TextView) main.findViewById(R.id.height);
        final TextView mass = (TextView) main.findViewById(R.id.mass);
        final TextView hair = (TextView) main.findViewById(R.id.hair);
        final TextView skin = (TextView) main.findViewById(R.id.skin);
        final TextView eye = (TextView) main.findViewById(R.id.eye);
        final TextView year = (TextView) main.findViewById(R.id.year);
        final TextView gender = (TextView) main.findViewById(R.id.gender);
        final TextView homeworld = (TextView) main.findViewById(R.id.homeworld);
        final LinearLayout species = (LinearLayout) main.findViewById(R.id.speciesList);
        final LinearLayout speciesHeader = (LinearLayout) main.findViewById(R.id.speciesHeader);

        name.setText(person.getName());
        height.setText(person.getHeight());
        mass.setText(person.getMass());
        hair.setText(person.getHairColor());
        skin.setText(person.getSkinColor());
        eye.setText(person.getEyeColor());
        year.setText(person.getBirthYear());
        gender.setText(person.getGender());

        // Set the planet
        final Planet p = _db.getPlanet(person.getHomeworldId());
        homeworld.setText(p.getName());
        homeworld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWiki(WikiType.Planet, p.getId());
            }
        });

        // Set the species
        List<Long> si = person.getSpeciesIds();

        if (si.size() != 0) {
            final List<Species> ss = new ArrayList<>();

            for (long speciesId : si) {
                Species s = _db.getSpecies(speciesId);
                ss.add(s);

                View listitem = inflater.inflate(R.layout.wiki_resident_listitem, null);

                TextView t = (TextView) listitem.findViewById(R.id.text);
                t.setText(s.getName());

                listitem.setTag(s.getId());
                listitem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long t_id = (long) v.getTag();
                        gotoWiki(WikiType.Species, t_id);
                    }
                });
                species.addView(listitem);
            }
        } else {
            speciesHeader.setVisibility(View.GONE);
        }

        parent.addView(main);
    }

    private void showPlanetPage(long id) {
        ScrollView parent = (ScrollView) findViewById(R.id.main_view);
        LayoutInflater inflater = getLayoutInflater();
        View main = inflater.inflate(R.layout.wiki_planet, null);
        final Planet planet = _db.getPlanet(id);

        if (planet == null) {
            showMainPage();
            return;
        }

        // Fill the view
        final TextView name = (TextView) main.findViewById(R.id.name);
        final TextView rotation = (TextView) main.findViewById(R.id.rotation);
        final TextView orbital = (TextView) main.findViewById(R.id.orbital);
        final TextView diameter = (TextView) main.findViewById(R.id.diameter);
        final TextView climate = (TextView) main.findViewById(R.id.climate);
        final TextView gravity = (TextView) main.findViewById(R.id.gravity);
        final TextView terrain = (TextView) main.findViewById(R.id.terrain);
        final TextView water = (TextView) main.findViewById(R.id.water);
        final TextView population = (TextView) main.findViewById(R.id.population);
        final LinearLayout residents = (LinearLayout) main.findViewById(R.id.residentsList);
        final LinearLayout residentsHeader = (LinearLayout) main.findViewById(R.id.residentsHeader);

        name.setText(planet.getName());
        rotation.setText(planet.getRotationPeriod());
        orbital.setText(planet.getOrbitalPeriod());
        diameter.setText(planet.getDiameter());
        climate.setText(planet.getClimate());
        gravity.setText(planet.getGravity());
        terrain.setText(planet.getTerrain());
        water.setText(planet.getSurfaceWater());
        population.setText(planet.getPopulation());

        List<Long> rs = planet.getResidentIds();

        if (rs.size() != 0) {
            final List<People> people = new ArrayList<>();

            for (long residentId : rs) {
                People p = _db.getPeople(residentId);
                people.add(p);

                View listitem = inflater.inflate(R.layout.wiki_resident_listitem, null);

                TextView t = (TextView) listitem.findViewById(R.id.text);
                t.setText(p.getName());

                listitem.setTag(p.getId());
                listitem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long t_id = (long) v.getTag();
                        gotoWiki(WikiType.People, t_id);
                    }
                });
                residents.addView(listitem);
            }
        } else {
            residentsHeader.setVisibility(View.GONE);
        }

        parent.addView(main);
    }

    private void showSpeciesPage(long id) {
        ScrollView parent = (ScrollView) findViewById(R.id.main_view);
        LayoutInflater inflater = getLayoutInflater();
        View main = inflater.inflate(R.layout.wiki_species, null);
        final Species species = _db.getSpecies(id);

        if (species == null) {
            showMainPage();
            return;
        }

        // Fill the view
        final TextView name = (TextView) main.findViewById(R.id.name);
        final TextView classification = (TextView) main.findViewById(R.id.classification);
        final TextView designation = (TextView) main.findViewById(R.id.designation);
        final TextView height = (TextView) main.findViewById(R.id.height);
        final TextView lifespan = (TextView) main.findViewById(R.id.lifespan);
        final TextView homeworld = (TextView) main.findViewById(R.id.homeworld);
        final TextView language = (TextView) main.findViewById(R.id.language);

        final LinearLayout skin = (LinearLayout) main.findViewById(R.id.skinList);
        final LinearLayout hair = (LinearLayout) main.findViewById(R.id.hairList);
        final LinearLayout eye = (LinearLayout) main.findViewById(R.id.eyeList);

        final LinearLayout people = (LinearLayout) main.findViewById(R.id.peopleList);
        final LinearLayout peopleHeader = (LinearLayout) main.findViewById(R.id.peopleHeader);

        name.setText(species.getName());
        classification.setText(species.getClassification());
        designation.setText(species.getDesignation());
        height.setText(species.getAverageHeight());
        lifespan.setText(species.getAverageLifespan());
        language.setText(species.getLanguage());

        final Planet p = _db.getPlanet(species.getHomeworldId());
        homeworld.setText(p.getName());
        homeworld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWiki(WikiType.Planet, p.getId());
            }
        });

        // People
        List<Long> peopleIds = species.getPeopleIds();

        if (peopleIds.size() != 0) {
            final List<People> peopleList = new ArrayList<>();

            for (long peopleId : peopleIds) {
                People pp = _db.getPeople(peopleId);
                peopleList.add(pp);

                View listitem = inflater.inflate(R.layout.wiki_resident_listitem, null);

                TextView t = (TextView) listitem.findViewById(R.id.text);
                t.setText(pp.getName());

                listitem.setTag(pp.getId());
                listitem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long t_id = (long) v.getTag();
                        gotoWiki(WikiType.People, t_id);
                    }
                });
                people.addView(listitem);
            }
        } else {
            peopleHeader.setVisibility(View.GONE);
        }

        fillColorItems(species.getSkinColors(), skin);
        fillColorItems(species.getHairColors(), hair);
        fillColorItems(species.getEyeColors(), eye);

        parent.addView(main);
    }

    /**
     * Fill a LinearLayout with some values, taken from a string that we split on ', '
     * @param toSplit The string to split
     * @param ll The LinearLayout
     */
    private void fillColorItems(String toSplit, LinearLayout ll) {
        LayoutInflater inflater = getLayoutInflater();
        String[] skinColors = toSplit.split(", ");

        for (String skinColor : skinColors) {
            View listitem = inflater.inflate(R.layout.wiki_resident_listitem, null);
            TextView t = (TextView) listitem.findViewById(R.id.text);
            t.setText(skinColor);
            ll.addView(listitem);
        }
    }

    private void showPeopleList(int headerTextResId, List<People> objects) {
        ScrollView parent = (ScrollView) findViewById(R.id.main_view);
        LayoutInflater inflater = getLayoutInflater();
        View main = inflater.inflate(R.layout.wiki_list, null);
        final TextView headerText = (TextView) main.findViewById(R.id.name);
        final LinearLayout list = (LinearLayout) main.findViewById(R.id.list);

        headerText.setText(Helper.getXmlString(this, headerTextResId));

        for (People obj : objects) {
            View listitem = inflater.inflate(R.layout.wiki_resident_listitem, null);

            TextView t = (TextView) listitem.findViewById(R.id.text);
            t.setText(obj.getName());

            listitem.setTag(obj.getId());
            listitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long t_id = (long) v.getTag();
                    gotoWiki(WikiType.People, t_id);
                }
            });

            list.addView(listitem);
        }

        parent.addView(main);
    }

    private void showPlanetList(int headerTextResId, List<Planet> objects) {
        ScrollView parent = (ScrollView) findViewById(R.id.main_view);
        LayoutInflater inflater = getLayoutInflater();
        View main = inflater.inflate(R.layout.wiki_list, null);
        final TextView headerText = (TextView) main.findViewById(R.id.name);
        final LinearLayout list = (LinearLayout) main.findViewById(R.id.list);

        headerText.setText(Helper.getXmlString(this, headerTextResId));

        for (Planet obj : objects) {
            View listitem = inflater.inflate(R.layout.wiki_resident_listitem, null);

            TextView t = (TextView) listitem.findViewById(R.id.text);
            t.setText(obj.getName());

            listitem.setTag(obj.getId());
            listitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long t_id = (long) v.getTag();
                    gotoWiki(WikiType.Planet, t_id);
                }
            });

            list.addView(listitem);
        }

        parent.addView(main);
    }
    private void showSpeciesList(int headerTextResId, List<Species> objects) {
        ScrollView parent = (ScrollView) findViewById(R.id.main_view);
        LayoutInflater inflater = getLayoutInflater();
        View main = inflater.inflate(R.layout.wiki_list, null);
        final TextView headerText = (TextView) main.findViewById(R.id.name);
        final LinearLayout list = (LinearLayout) main.findViewById(R.id.list);

        headerText.setText(Helper.getXmlString(this, headerTextResId));

        for (Species obj : objects) {
            View listitem = inflater.inflate(R.layout.wiki_resident_listitem, null);

            TextView t = (TextView) listitem.findViewById(R.id.text);
            t.setText(obj.getName());

            listitem.setTag(obj.getId());
            listitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long t_id = (long) v.getTag();
                    gotoWiki(WikiType.Species, t_id);
                }
            });

            list.addView(listitem);
        }

        parent.addView(main);
    }
    /**
     * Refresh the data, i.e. get the correct data from the internet
     */
    private void refresh() {
        if (_type == WikiType.NONE) return;

        // Is there internet?
        if (!Helper.isInternetAvailable()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_error_title)
                    .setMessage(R.string.dialog_error_no_internet)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create()
                    .show();
            return;
        }

        switch (_type) {
            case People:
                final People person = _db.getPeople(_id);
                new HttpReader(new HttpReader.OnResultReadyListener() {
                    @Override
                    public void resultReady(String result) {
                        try {
                            People newP = JSONHelper.getJSONPeople(new JSONObject(result));
                            newP.setId(person.getId());
                            _db.updatePeople(newP);
                            gotoWiki(WikiType.People, newP.getId());
                        } catch (Exception e) {
                            Log.e("WIKI", "Couldn't get person");
                        }
                    }
                }).execute(person.getUrl());
                break;
            case Planet:
                final Planet planet = _db.getPlanet(_id);
                new HttpReader(new HttpReader.OnResultReadyListener() {
                    @Override
                    public void resultReady(String result) {
                        try {
                            Planet newP = JSONHelper.getJSONPlanet(new JSONObject(result));
                            newP.setId(planet.getId());
                            _db.updatePlanet(newP);
                            gotoWiki(WikiType.Planet, newP.getId());
                        } catch (Exception e) {
                            Log.e("WIKI", "Couldn't get planet");
                        }
                    }
                }).execute(planet.getUrl());
                break;
            case Species:
                final Species species = _db.getSpecies(_id);
                new HttpReader(new HttpReader.OnResultReadyListener() {
                    @Override
                    public void resultReady(String result) {
                        try {
                            Species newS = JSONHelper.getJSONSpecies(new JSONObject(result));
                            newS.setId(species.getId());
                            _db.updateSpecies(newS);
                            gotoWiki(WikiType.Species, newS.getId());
                        } catch (Exception e) {
                            Log.e("WIKI", "Couldn't get species");
                        }
                    }
                }).execute(species.getUrl());
                break;
            case ListSpecies:
            case ListPeople:
            case ListPlanets:
                // Create a loading spinner
                FragmentManager fm = getSupportFragmentManager();
                final MySpinnerDialog spinner = new MySpinnerDialog();
                spinner.show(fm, "some_tag");

                // Create a list to hold results
                // Hacky way of getting data from a thread
                final int listSize = 1;
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
                    }
                }).start();

                if (_type == WikiType.ListPlanets) {
                    final String planetUrl = "http://swapi.co/api/planets/";
                    new HttpReader(new HttpReader.OnResultReadyListener() {
                        @Override
                        public void resultReady(String result) {
                            new RootsReader<>(JSONHelper.JSONTypes.Planet, new RootsReader.OnResultReadyListener() {
                                @Override
                                public void resultReady(List result) {
                                    List<Planet> planets = (List<Planet>) result;
                                    _db.deleteAllPlanets();
                                    _db.insertPlanets(planets);
                                    results.add(true);
                                }
                            }).execute(planetUrl);
                        }
                    }).execute(planetUrl);
                }

                if (_type == WikiType.ListPeople) {
                    final String peopleUrl = "http://swapi.co/api/people/";
                    new HttpReader(new HttpReader.OnResultReadyListener() {
                        @Override
                        public void resultReady(String result) {
                            new RootsReader<>(JSONHelper.JSONTypes.People, new RootsReader.OnResultReadyListener() {
                                @Override
                                public void resultReady(List result) {
                                    List<People> people = (List<People>) result;
                                    _db.deleteAllPeople();
                                    _db.insertPeoples(people);
                                    results.add(true);
                                }
                            }).execute(peopleUrl);
                        }
                    }).execute(peopleUrl);
                }

                if (_type == WikiType.ListPeople) {
                    final String speciesUrl = "http://swapi.co/api/species/";
                    new HttpReader(new HttpReader.OnResultReadyListener() {
                        @Override
                        public void resultReady(String result) {
                            new RootsReader<>(JSONHelper.JSONTypes.Species, new RootsReader.OnResultReadyListener() {
                                @Override
                                public void resultReady(List result) {
                                    List<Species> species = (List<Species>) result;
                                    _db.deleteAllSpecies();
                                    _db.insertSpecies(species);
                                    results.add(true);
                                }
                            }).execute(speciesUrl);
                        }
                    }).execute(speciesUrl);
                }
                break;
            default:
                // Shouldn't get here
                break;
        }
    }
}
