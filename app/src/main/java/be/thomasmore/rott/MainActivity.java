package be.thomasmore.rott;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import be.thomasmore.rott.data.DatabaseHelper;
import be.thomasmore.rott.data.HttpReader;
import be.thomasmore.rott.data.JSONHelper;
import be.thomasmore.rott.data.People;
import be.thomasmore.rott.data.Planet;
import be.thomasmore.rott.data.RootsReader;
import be.thomasmore.rott.data.Species;
import be.thomasmore.rott.data.Team;

public class MainActivity extends AppCompatActivity {

    List<Team> teams = new ArrayList<Team>();
    TeamAdapter teamAdapter;
    List<Planet> planets = new ArrayList<>();
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DatabaseHelper(this);

        // Delete any system entities that are left
        db.cleanUpTeamAndMembers();

        teams = db.getAllTeams();
        planets = db.getAllPlanets();

        teamAdapter = new TeamAdapter(getApplicationContext(), teams);
        final ListView listviewTeams = (ListView) findViewById(R.id.listViewTeams);
        listviewTeams.setAdapter(teamAdapter);
        listviewTeams.setEmptyView(findViewById(R.id.empty));

        listviewTeams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parentView, View childView, int position, long id) {
                Team team = teams.get(position);
                Intent intent = new Intent(MainActivity.this, EditTeamActivity.class);
                intent.putExtra(Helper.TEAMID_MESSAGE, team.getId());
                startActivity(intent);
                finish();
            }
        });

        final Button button = (Button) findViewById(R.id.make_team);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        checkButton();
    }

    /**
     * Check to see if we need to disable the button to add a team.
     */
    private void checkButton() {
        final Button button = (Button) findViewById(R.id.make_team);
        if (teams.size() >= Helper.MAXTEAMS) {
            button.setVisibility(View.GONE);
        }
    }

    /**
     * Show the dialog where you make a team.
     */
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final Random random = new Random();

        View dialogView = inflater.inflate(R.layout.dialog_make_team, null);

        // Load the spinner
        final Spinner homeworldSpinner = (Spinner) dialogView.findViewById(R.id.homeplanet);
        homeworldSpinner.setAdapter(new ArrayAdapter<Planet>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                planets));

        final EditText teamnameText = (EditText) dialogView.findViewById(R.id.teamname);
        ImageButton randomName = (ImageButton) dialogView.findViewById(R.id.btnRandom);
        randomName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String teamName = Helper.getRandomTeamName();
                int planetIndex = random.nextInt(planets.size());

                teamnameText.setText(teamName);
                homeworldSpinner.setSelection(planetIndex);
            }
        });

        builder
            .setView(dialogView)
            .setTitle(R.string.dialog_maketeam_title)
            .setPositiveButton(R.string.main_make_team, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Keep this empty, because we override it later
                    // I forgot why this is needed, but we need to add the clicklistener
                    // after the dialog is shown
                }
            })
            .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

        final AlertDialog dialog = builder.create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String teamName = teamnameText.getText().toString();

                if (teamName.equals("")) {
                    return;
                }

                Team newTeam = new Team();
                Planet planet = (Planet) homeworldSpinner.getSelectedItem();

                newTeam.setName(teamName);
                newTeam.setPlanet(planet);

                db.insertTeam(newTeam);

                teamAdapter.clear();
                teamAdapter.addAll(db.getAllTeams());
                teamAdapter.notifyDataSetChanged();
                checkButton();
                invalidateOptionsMenu();
                dialog.dismiss();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_fight).setVisible(teams.size() != 0);

        // Can't refresh for now
        //menu.findItem(R.id.action_refresh).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_fight:
                startActivity(new Intent(this, StartFight.class));
                return true;
            case R.id.action_about:
                startActivity(new Intent(this, About.class));
                return true;
            case R.id.action_refresh:
                refresh();
                return true;
            case R.id.action_wiki:
                startActivity(new Intent(this, Wiki.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refresh() {
        // Create a loading spinner
        FragmentManager fm = getSupportFragmentManager();
        final MySpinnerDialog spinner = new MySpinnerDialog();
        spinner.show(fm, "some_tag");

        // Create a list to hold results
        // Hacky way of getting data from a thread
        final int listSize = 3;     // 3 threads (which might call another thread)
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

        final String planetUrl = "http://swapi.co/api/planets/";
        new HttpReader(new HttpReader.OnResultReadyListener() {
            @Override
            public void resultReady(String result) {
                new RootsReader<>(JSONHelper.JSONTypes.Planet, new RootsReader.OnResultReadyListener() {
                    @Override
                    public void resultReady(List result) {
                        List<Planet> planets = (List<Planet>)result;
                        db.deleteAllPlanets();
                        db.insertPlanets(planets);
                        results.add(true);
                    }
                }).execute(planetUrl);
            }
        }).execute(planetUrl);

        final String peopleUrl = "http://swapi.co/api/people/";
        new HttpReader(new HttpReader.OnResultReadyListener() {
            @Override
            public void resultReady(String result) {
                new RootsReader<>(JSONHelper.JSONTypes.People, new RootsReader.OnResultReadyListener() {
                    @Override
                    public void resultReady(List result) {
                        List<People> people = (List<People>)result;
                        db.deleteAllPeople();
                        db.insertPeoples(people);
                        results.add(true);
                    }
                }).execute(peopleUrl);
            }
        }).execute(peopleUrl);

        final String speciesUrl = "http://swapi.co/api/species/";
        new HttpReader(new HttpReader.OnResultReadyListener() {
            @Override
            public void resultReady(String result) {
                new RootsReader<>(JSONHelper.JSONTypes.Species, new RootsReader.OnResultReadyListener() {
                    @Override
                    public void resultReady(List result) {
                        List<Species> species = (List<Species>)result;
                        db.deleteAllSpecies();
                        db.insertSpecies(species);
                        results.add(true);
                    }
                }).execute(speciesUrl);
            }
        }).execute(speciesUrl);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
