package be.thomasmore.swrott;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import be.thomasmore.swrott.data.DatabaseHelper;
import be.thomasmore.swrott.data.FightHelper;
import be.thomasmore.swrott.data.FightOutcome;
import be.thomasmore.swrott.data.FightOutcomeDeath;
import be.thomasmore.swrott.data.Member;
import be.thomasmore.swrott.data.People;
import be.thomasmore.swrott.data.Team;

public class Fight extends AppCompatActivity {

    private final static int WAIT_TIME = 8000*2;

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

        setupLayout(_team, enemy);

        // Fight against each other
        final FightOutcome outcome = FightHelper.fight(_team, enemy);

        // Assign exp and all that jazz
        for (Member m : _team.getMembers()) {
            if (outcome.getExperience().containsKey(m.getId())) {
                m.addExperience(outcome.getExperience().get(m.getId()));
                _db.updateMember(m);
            }
        }

        // Done!

        // A class to use in our threads, because we need to pass some data to it
        class ChangeColor implements Runnable {
            private int _side;
            private int _memberIndex;
            private boolean[] _results;
            private int _index;

            public ChangeColor(int side, int memberIndex, boolean[] results, int index) {
                _side = side;
                _memberIndex = memberIndex;
                _results = results;
                _index = index;
            }

            @Override
            public void run() {
                final int resId = getResources().getIdentifier("f" + _side + "_member_" + _memberIndex, "id", getPackageName());
                final ImageView img = (ImageView) findViewById(resId);
                img.setBackgroundResource(R.color.colorFighterDead);
                _results[_index] = true;
            }
        }

        // Calculate the time for each 'defeat'
        int deaths = outcome.getDeaths().size();
        final int time = WAIT_TIME / deaths + 1;    // Add one extra for the one below
        final boolean[] results = new boolean[deaths];
        int f1Index = 1;
        int f2Index = 1;

        for (int i = 1; i <= deaths; i++) {
            int side = 1;
            int member = f1Index;
            int index = i - 1;
            FightOutcomeDeath fod = outcome.getDeaths().get(index);

            if (fod.getTeamId() == enemy.getId()) {
                side = 2;
                member = f2Index;
                f2Index++;
            } else {
                f1Index++;
            }

            new Handler()
                .postDelayed(
                    new ChangeColor(side, member, results, index),
                    time * i
            );
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    boolean result = true;

                    for (int i = 0; i < results.length; i++) {
                        result &= results[i];
                    }

                    if (result) {
                        SystemClock.sleep(time);
                        break;
                    }

                    SystemClock.sleep(500); // NEVER on UI-THREAD!!!!
                }

                done(outcome);
            }
        }).start();
    }

    private void done(FightOutcome outcome) {
        Intent intent = new Intent(this, FightResult.class);
        intent.putExtra(Helper.OUTCOME_MESSAGE, outcome);
        intent.putExtra(Helper.TEAMID_MESSAGE, _team.getId());
        startActivity(intent);
        finish();
    }

    private void setupLayout(Team f1, Team f2) {
        final TextView f1NameText = (TextView) findViewById(R.id.fighter1_name);
        final TextView f2NameText = (TextView) findViewById(R.id.fighter2_name);

        f1NameText.setText(f1.getName());
        f2NameText.setText(f2.getName());

        for (int i = 1; i <= f1.getMembers().size(); i++) {
            final int resId = getResources().getIdentifier("f1_member_" + i, "id", getPackageName());
            final ImageView img = (ImageView) findViewById(resId);
            img.setVisibility(View.VISIBLE);
        }

        for (int i = 1; i <= f2.getMembers().size(); i++) {
            final int resId = getResources().getIdentifier("f2_member_" + i, "id", getPackageName());
            final ImageView img = (ImageView) findViewById(resId);
            img.setVisibility(View.VISIBLE);
        }
    }
}
