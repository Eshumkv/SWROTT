package be.thomasmore.swrott;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.List;

import be.thomasmore.swrott.data.DatabaseHelper;
import be.thomasmore.swrott.data.Team;

public class StartFight extends AppCompatActivity {

    private DatabaseHelper _db;
    private List<Team> _teams;
    private long _selectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_fight);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _db = new DatabaseHelper(this);
        _teams = _db.getAllTeamsWithMembers();
        _selectedId = -1;

        if (_teams.size() <= 0) {
            Helper.showErrorDialog(this, R.string.dialog_error_no_members_in_team, MainActivity.class);
            return;
        }

        RadioGroup rgroup = (RadioGroup) findViewById(R.id.teams);
        Button fightButton = (Button) findViewById(R.id.fight);

        for(int i = 0; i < _teams.size(); i++) {
            View rbView = getLayoutInflater().inflate(R.layout.teamlistradioview, null);
            RadioButton rb = (RadioButton) rbView.findViewById(R.id.radio_button);
            Team team = _teams.get(i);

            rb.setText(team.getName());
            rb.setTag(team.getId());
            rb.setId(9000 + i);
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RadioButton rb = (RadioButton)v;
                    _selectedId = (long) rb.getTag();
                }
            });

            // Check the first option
            if (i == 0) {
                rb.setChecked(true);
                _selectedId = (long) rb.getTag();
            }

            rgroup.addView(rbView);
        }

        fightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Fight.class);
                intent.putExtra(Helper.TEAMID_MESSAGE, _selectedId);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_back, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
