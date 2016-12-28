package be.thomasmore.swrott;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import be.thomasmore.swrott.data.DatabaseHelper;
import be.thomasmore.swrott.data.FightHelper;
import be.thomasmore.swrott.data.FightOutcome;
import be.thomasmore.swrott.data.FightOutcomeDeath;
import be.thomasmore.swrott.data.Member;
import be.thomasmore.swrott.data.People;
import be.thomasmore.swrott.data.Picture;
import be.thomasmore.swrott.data.Team;

public class Fight extends AppCompatActivity {

    private final static int WAIT_TIME = 7500;

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
        // Technically you shouldn't be able to get here if there aren't any members
        if (_team.getMembers().size() < 1) {
            Helper.showErrorDialog(
                    this,
                    R.string.dialog_error_no_members_in_team,
                    MainActivity.class
            );
            return;
        }

        // Get the picture for all the members in your team.
        for (Member m : _team.getMembers()) {
            m.setPicture(_db.getPicture(m.getPictureId()));
        }

        // Create random team within parameters
        Team enemy = FightHelper.getEnemy(_team, _db.getAllPeople());
        enemy = _db.insertTeamFull(enemy);

        setupLayout(_team, enemy);

        // Fight against each other
        final FightOutcome outcome = FightHelper.fight(_team, enemy);

        // Calculate the average base stats of the enemy team
        // This will be added to the EVs of our team
        int avgBaseStats = enemy.getAverageBaseStats();

        // Assign exp and all that jazz
        for (Member m : _team.getMembers()) {
            if (outcome.getExperience().containsKey(m.getId())) {
                m.addEv(avgBaseStats);
                m.addExperience(outcome.getExperience().get(m.getId()));

                _db.updateMember(m);
            }
        }

        // Done!

        // A class to use in our threads, because we need to pass some data to it
        class ChangeColor implements Runnable {
            private long _memberId;
            private boolean[] _results;
            private int _index;

            public ChangeColor(long memberId, boolean[] results, int index) {
                _memberId = memberId;
                _results = results;
                _index = index;
            }

            @Override
            public void run() {
                //final int resId = getResources().getIdentifier("f" + _side + "_member_" + _memberIndex, "id", getPackageName());
                final View parent = findViewById(R.id.usedForSearch);
                final View row = parent.findViewWithTag("id_" + _memberId);
                final ImageView img = (ImageView) row.findViewById(R.id.member_image);
                img.setBackgroundResource(R.color.colorFighterDead);
                _results[_index] = true;
            }
        }

        // Calculate the time for each 'defeat'
        int deaths = outcome.getDeaths().size();
        final int time = WAIT_TIME / (deaths + 1);    // Add one, because we wait an extra time
        final boolean[] results = new boolean[deaths];

        for (int i = 1; i <= deaths; i++) {
            int index = i - 1;
            FightOutcomeDeath fod = outcome.getDeaths().get(index);

            new Handler()
                .postDelayed(
                    new ChangeColor(fod.getFighterId(), results, index),
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
                        SystemClock.sleep(time / 2);
                        break;
                    }

                    SystemClock.sleep(500); // NEVER on UI-THREAD!!!!
                }

                done(outcome);
            }
        }).start();
    }


    @Override
    public void onBackPressed() {
        // It's sad but we have to disable it
    }

    private void done(FightOutcome outcome) {
        Intent intent = new Intent(this, FightResult.class);
        intent.putExtra(Helper.OUTCOME_MESSAGE, outcome);
        intent.putExtra(Helper.TEAMID_MESSAGE, _team.getId());
        startActivity(intent);
        finish();
    }

    /**
     * Setup the team layout for both teams
     * @param f1 The first team
     * @param f2 The second team
     */
    private void setupLayout(Team f1, Team f2) {
        final TextView f1NameText = (TextView) findViewById(R.id.fighter1_name);
        final TextView f2NameText = (TextView) findViewById(R.id.fighter2_name);

        f1NameText.setText(f1.getName());
        f2NameText.setText(f2.getName());

        // Get an XML array
        // This contains the images we're going to use for the enemy
        TypedArray array = getResources().obtainTypedArray(R.array.fight_icons);
        List<Integer> icons = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            int resId = array.getResourceId(i, 0);
            if (resId != 0)
                icons.add(resId);
        }

        // For memory reasons, we need to recycle this array
        array.recycle();

        GridView f1Grid = (GridView) findViewById(R.id.fighter1_members);
        f1Grid.setAdapter(new FightAdapter(
                this, f1.getMembers(), R.color.colorFighter1, false, icons));

        GridView f2Grid = (GridView) findViewById(R.id.fighter2_members);
        f2Grid.setAdapter(new FightAdapter(
                this, f2.getMembers(), R.color.colorFighter2, true, icons));
    }
}
