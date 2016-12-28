package be.thomasmore.rott;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import be.thomasmore.rott.data.FightOutcome;

public class FightDetails extends AppCompatActivity {

    private FightOutcome _outcome;
    private long _teamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _outcome = (FightOutcome) getIntent().getSerializableExtra(Helper.OUTCOME_MESSAGE);

        if (_outcome == null) {
            Log.e("HELPER ERROR", "Seriously don't know what to do");
            Helper.showErrorDialog(this, MainActivity.class);
            return;
        }

        _teamId = Helper.getLongExtra(this, Helper.TEAMID_MESSAGE, null);
        if (_teamId == -1) return;

        final ListView logView = (ListView) findViewById(R.id.log);
        logView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _outcome.getLog()));
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
                done();
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
        Intent intent = new Intent(FightDetails.this, FightResult.class);
        intent.putExtra(Helper.OUTCOME_MESSAGE, _outcome);
        intent.putExtra(Helper.TEAMID_MESSAGE, _teamId);
        startActivity(intent);
        finish();
    }
}
