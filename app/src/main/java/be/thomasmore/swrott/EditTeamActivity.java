package be.thomasmore.swrott;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import be.thomasmore.swrott.data.DatabaseHelper;
import be.thomasmore.swrott.data.FightHelper;
import be.thomasmore.swrott.data.Member;
import be.thomasmore.swrott.data.Planet;
import be.thomasmore.swrott.data.Stats;
import be.thomasmore.swrott.data.Team;

public class EditTeamActivity extends AppCompatActivity {

    private Team _team;
    private DatabaseHelper _db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        long teamId = Helper.getLongExtra(this, Helper.TEAMID_MESSAGE, null);

        if (teamId == -1) return;

        _db = new DatabaseHelper(this);

        setup(teamId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // empty for now
            }

            long teamId = data.getLongExtra(Helper.TEAMID_MESSAGE, -1);

            if (teamId == -1) {
                Log.e("ERROR", "Seriously don't know what to do");
                Helper.showErrorDialog(this, MainActivity.class);
                return;
            }
            setup(teamId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);

        menu.findItem(R.id.action_edit).setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                done();
                return true;
            case R.id.action_delete:
                showDialog();
                return true;
            case R.id.action_add:
                Intent addMemberIntent = new Intent(EditTeamActivity.this, AddMember.class);
                addMemberIntent.putExtra(Helper.TEAMID_MESSAGE, _team.getId());
                startActivityForResult(addMemberIntent, 1);
                return true;
            case R.id.action_fight:
                Intent intent = new Intent(this, StartFight.class);
                intent.putExtra(Helper.TEAMID_MESSAGE, _team.getId());
                startActivity(intent);
                finish();
                return true;
            case R.id.action_edit:
                showEditDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setup(long teamId) {
        _team = _db.getTeamFull(teamId);

        final TextView teamName = (TextView) findViewById(R.id.teamname);
        final TextView homeworld = (TextView) findViewById(R.id.homeplanet);
        final ListView members = (ListView) findViewById(R.id.listViewMembers);
        final Button addMember = (Button) findViewById(R.id.add_member);
        final Planet planet = _db.getPlanet(_team.getPlanetId());
        final ProgressBar expProgressBar = (ProgressBar) findViewById(R.id.experience);

        final int oneTeamLevel = Member.MAX_EV / Team.MAX_LEVEL;
        final int teamExp = _team.getTeamExperience();
        final int teamLevel = Math.max((int) Math.ceil(teamExp / (double) oneTeamLevel), 1);
        int progress = teamExp - (oneTeamLevel * (teamLevel - 1));

        if (teamLevel == 1)
            progress = teamExp;

        expProgressBar.setMax(oneTeamLevel);
        expProgressBar.setProgress(progress);

        homeworld.setText(planet.getName());
        homeworld.setLongClickable(true);
        homeworld.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(EditTeamActivity.this, Wiki.class);
                intent.putExtra(Helper.WIKI_TYPE_MESSAGE, WikiType.Planet);
                intent.putExtra(Helper.WIKI_MESSAGE, planet.getId());
                startActivity(intent);
                return true;
            }
        });

        teamName.setText(String.format("%s - Lvl %d", _team.getName(), 1));
        members.setAdapter(new ArrayAdapter<Member>(
                this,
                android.R.layout.simple_list_item_1,
                _team.getMembers()
        ));
        members.setEmptyView(findViewById(R.id.empty));
        members.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parentView, View childView, int position, long id) {
                Intent intent = new Intent(EditTeamActivity.this, EditMember.class);
                intent.putExtra(Helper.MEMBERID_MESSAGE, _team.getMembers().get(position).getId());
                startActivityForResult(intent, 1);
            }
        });
//        addMember.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(EditTeamActivity.this, AddMember.class);
//                intent.putExtra(Helper.TEAMID_MESSAGE, _team.getId());
//                startActivityForResult(intent, 1);
//            }
//        });
        if (_team.getMembers().size() >= Helper.MAXMEMBERS) {
            addMember.setVisibility(View.GONE);
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_make_team, null);

        builder
            //.setView(dialogView)
            .setTitle(R.string.dialog_deleteteam_title)
            .setMessage(R.string.dialog_confirm_team_deletion)
            .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    _db.deleteTeam(_team.getId());

                    Intent intent = new Intent(EditTeamActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            })
            .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            })
            .create()
            .show();
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final Random random = new Random();
        final List<Planet> planets = _db.getAllPlanets();

        View dialogView = inflater.inflate(R.layout.dialog_make_team, null);

        final Spinner homeworldSpinner = (Spinner) dialogView.findViewById(R.id.homeplanet);
        final ArrayAdapter spnrAdapter = new ArrayAdapter<Planet>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                planets);
        homeworldSpinner.setAdapter(spnrAdapter);

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

        // Set the name and planet
        teamnameText.setText(_team.getName());

        for (int i = 0; i < spnrAdapter.getCount(); i++) {
            Planet testPlanet = (Planet) spnrAdapter.getItem(i);
            if (testPlanet.getId() == _team.getPlanetId()) {
                homeworldSpinner.setSelection(i);
                break;
            }
        }

        builder
                .setView(dialogView)
                .setTitle(R.string.dialog_maketeam_title)
                .setPositiveButton(R.string.dialog_edit_team, new DialogInterface.OnClickListener() {
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
                    Planet planet = (Planet) homeworldSpinner.getSelectedItem();

                    _team.setName(teamName);
                    _team.setPlanet(planet);
                    _db.updateTeam(_team);
                    wantToClose = true;
                }

                if (wantToClose) {
                    finish();
                    startActivity(getIntent());
                    dialog.dismiss();
                }
            }
        });
    }

    private void done() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        done();
    }
}
