package be.thomasmore.swrott;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.List;

import be.thomasmore.swrott.data.DatabaseHelper;
import be.thomasmore.swrott.data.FightHelper;
import be.thomasmore.swrott.data.FightOutcome;
import be.thomasmore.swrott.data.Member;
import be.thomasmore.swrott.data.People;
import be.thomasmore.swrott.data.Team;

public class Fight extends AppCompatActivity {

    private final static int WAIT_TIME = 3000;

    private DatabaseHelper _db;
    private Team _team;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        long teamId = Helper.getLongExtra(this, Helper.TEAMID_MESSAGE, null);
        if (teamId == -1) return;

        _db = new DatabaseHelper(this);
        _team = _db.getTeamFull(teamId);

        // Does the team have members?
        if (_team.getMembers().size() < 1) {
            Helper.showErrorDialog(
                    this,
                    R.string.dialog_error_no_members_in_team,
                    MainActivity.class
            );
            return;
        }

        // Create random team within parameters
        Team enemy = FightHelper.getEnemy(_team, _db.getAllPeople());
        enemy = _db.insertTeamFull(enemy);

        // Fight against each other
        final FightOutcome outcome = FightHelper.fight(_team, enemy);

        // Assign exp and all that jazz

        // Done!
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                done(outcome);
            }
        }, WAIT_TIME);
    }

    private void done(FightOutcome outcome) {
        Intent intent = new Intent(this, FightResult.class);
        intent.putExtra(Helper.OUTCOME_MESSAGE, outcome);
        startActivity(intent);
        finish();
    }
}
