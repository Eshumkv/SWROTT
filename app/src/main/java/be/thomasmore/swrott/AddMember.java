package be.thomasmore.swrott;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import be.thomasmore.swrott.data.DatabaseHelper;
import be.thomasmore.swrott.data.Member;
import be.thomasmore.swrott.data.People;
import be.thomasmore.swrott.data.Stats;
import be.thomasmore.swrott.data.Team;

public class AddMember extends AppCompatActivity {

    private DatabaseHelper _db;
    private long _teamId;
    private List<People> _people;
    private List<Member> _currentMembers;
    private int _peoplePos;
    private HashMap<Long, Stats> _cachedStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _teamId = getIntent().getLongExtra(Helper.TEAMID_MESSAGE, -1);
        _peoplePos = -1;

        if (_teamId == -1) {
            Log.e("ERROR", "Seriously don't know what to do");
            Helper.showErrorDialog(this, MainActivity.class);
            return;
        }

        _db = new DatabaseHelper(this);
        _people = _db.getAllPeople();
        _currentMembers = _db.getMembers(_teamId);
        _cachedStats = new HashMap<>();
        final Team t = _db.getTeam(_teamId);

        final TextView headerText = (TextView) findViewById(R.id.textView);
        headerText.setText(Helper.getXmlString(this, R.string.add_member_header) + " (" + t.getName() + ")");

        final Spinner characterSpinner = (Spinner) findViewById(R.id.character);
        characterSpinner.setAdapter(new ArrayAdapter<People>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                _people));
        characterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                People person = _people.get(position);
                _peoplePos = position;
                Helper.updateStatsPart(getStats(person.getId()), AddMember.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Button addMemberBtn = (Button) findViewById(R.id.add_member);
        addMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Member member = new Member();
                People person = _people.get(_peoplePos);

                if (containsPerson(_currentMembers, person.getId())) {
                    new AlertDialog.Builder(AddMember.this)
                        .setTitle(R.string.dialog_error_title)
                        .setMessage(R.string.dialog_error_char_already_in_team)
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create()
                        .show();
                    return;
                }

                member.setTeamId(_teamId);
                member.setPeopleId(person.getId());
                member.setStats(getStats(person.getId()));
                member.setPictureId(1);

                _db.insertMember(member);

                Intent intent = new Intent();
                intent.putExtra(Helper.TEAMID_MESSAGE, _teamId);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private boolean containsPerson(List<Member> members, long peopleId) {
        for (Member m: members) {
            if (m.getPeopleId() == peopleId) {
                return true;
            }
        }

        return false;
    }

    private Stats getStats(long charId) {
        if (_cachedStats.containsKey(charId)) {
            return _cachedStats.get(charId);
        }

        Stats stats = new Stats();

        stats.setSpeed(Helper.randomBetween(8, 20));
        stats.setAttack(Helper.randomBetween(8, 20));
        stats.setDefense(Helper.randomBetween(8, 20));

        stats.setHealthPoints(Helper.randomBetween(20, 40));
        stats.setExperience(0);
        stats.setExpToLevel(Helper.randomBetween(8, 20));

        // level has a 1% chance to be a level higher
        if (Helper.randomBetween(0, 100) == 100)
            stats.setLevel(2);
        else
            stats.setLevel(1);

        _cachedStats.put(charId, stats);

        return stats;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Helper.TEAMID_MESSAGE, _teamId);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_back, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra(Helper.TEAMID_MESSAGE, _teamId);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
