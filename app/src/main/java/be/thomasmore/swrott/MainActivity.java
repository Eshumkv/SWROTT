package be.thomasmore.swrott;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import be.thomasmore.swrott.data.DatabaseHelper;
import be.thomasmore.swrott.data.HttpReader;
import be.thomasmore.swrott.data.JSONHelper;
import be.thomasmore.swrott.data.Planet;
import be.thomasmore.swrott.data.RootsReader;
import be.thomasmore.swrott.data.Team;

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

    private void checkButton() {
        final Button button = (Button) findViewById(R.id.make_team);
        if (teams.size() >= Helper.MAXTEAMS) {
            button.setVisibility(View.GONE);
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final Random random = new Random();

        View dialogView = inflater.inflate(R.layout.dialog_make_team, null);

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
                boolean wantToClose = false;
                String teamName = teamnameText.getText().toString();

                if (!teamName.equals("")) {
                    Team newTeam = new Team();

                    Planet planet = (Planet) homeworldSpinner.getSelectedItem();

                    newTeam.setName(teamName);
                    newTeam.setPlanet(planet);

                    db.insertTeam(newTeam);
                    wantToClose = true;
                }

                if (wantToClose) {
                    teamAdapter.clear();
                    teamAdapter.addAll(db.getAllTeams());
                    teamAdapter.notifyDataSetChanged();
                    checkButton();
                    invalidateOptionsMenu();
                    dialog.dismiss();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_fight).setVisible(teams.size() != 0);

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
