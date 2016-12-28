package be.thomasmore.rott;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import be.thomasmore.rott.data.DatabaseHelper;
import be.thomasmore.rott.data.FightOutcome;
import be.thomasmore.rott.data.Member;
import be.thomasmore.rott.data.Team;

public class FightResult extends AppCompatActivity {

    private DatabaseHelper _db;
    private Team _team;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setup();
    }

    /**
     * Setup this activity
     */
    private void setup() {
        final FightOutcome outcome = (FightOutcome) getIntent().getSerializableExtra(Helper.OUTCOME_MESSAGE);

        // We didn't get an outcome.
        // Surely something went wrong
        if (outcome == null) {
            Log.e("HELPER ERROR", "Seriously don't know what to do");
            Helper.showErrorDialog(this, MainActivity.class);
            return;
        }

        long teamId = Helper.getLongExtra(this, Helper.TEAMID_MESSAGE, null);
        if (teamId == -1) return;

        _db = new DatabaseHelper(this);
        _team = _db.getTeamFull(teamId);

        final TextView scoreText = (TextView) findViewById(R.id.score);
        final TextView nameText = (TextView) findViewById(R.id.winner);
        final Button logButton = (Button) findViewById(R.id.view_log);
        LinearLayout membersLinearLayout = (LinearLayout) findViewById(R.id.members);
        LayoutInflater inflater = getLayoutInflater();

        scoreText.setText(String.format("%d - %d", outcome.getWinnerScore(), outcome.getLoserScore()));

        if (outcome.getWinner() == _team.getId()) {
            nameText.setText(_team.getName());
            nameText.setBackgroundResource(R.color.colorResultWin);
        } else {
            Team enemy = _db.getTeam(outcome.getWinner());
            nameText.setText(enemy.getName());
            nameText.setBackgroundResource(R.color.colorResultLose);
        }

        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FightResult.this, FightDetails.class);
                intent.putExtra(Helper.OUTCOME_MESSAGE, outcome);
                intent.putExtra(Helper.TEAMID_MESSAGE, _team.getId());
                startActivity(intent);
                finish();
            }
        });

        for (Member m : _team.getMembers()) {
            View memberExpView = inflater.inflate(R.layout.member_exp_view, null);

            final TextView memberNameText = (TextView) memberExpView.findViewById(R.id.member_name);
            final TextView memberExpText = (TextView) memberExpView.findViewById(R.id.member_exp);

            int exp = 0 ;
            if (outcome.getExperience().containsKey(m.getId())) {
                exp = outcome.getExperience().get(m.getId());
            }

            memberNameText.setText(m.getPerson().getName());
            memberExpText.setText(exp + "");

            membersLinearLayout.addView(memberExpView);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            setup();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fightresult, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                done();
                return true;
            case R.id.action_fight:
                _db.cleanUpTeamAndMembers();
                Intent intent = new Intent(this, StartFight.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        done();
    }

    private void done() {
        _db.cleanUpTeamAndMembers();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
