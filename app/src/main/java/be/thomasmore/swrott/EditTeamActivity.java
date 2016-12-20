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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import be.thomasmore.swrott.data.DatabaseHelper;
import be.thomasmore.swrott.data.Member;
import be.thomasmore.swrott.data.Team;

public class EditTeamActivity extends AppCompatActivity {

    private Team team;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);

        long teamId = getIntent().getLongExtra(Helper.TEAMID_MESSAGE, -1);

        if (teamId == -1) {
            Log.e("ERROR", "Seriously don't know what to do");
            return;
        }

        team = db.getTeam(teamId);

        TextView teamName = (TextView) findViewById(R.id.teamname);
        ListView members = (ListView) findViewById(R.id.listViewMembers);
        Button addMember = (Button) findViewById(R.id.add_member);

        teamName.setText(team.getName());
        members.setAdapter(new ArrayAdapter<Member>(
                this,
                android.R.layout.simple_list_item_1,
                db.getMembers(team.getId())
        ));
        members.setEmptyView(findViewById(R.id.empty));
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditTeamActivity.this, AddMember.class);
                intent.putExtra(Helper.TEAMID_MESSAGE, team.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                    db.deleteTeam(team.getId());

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
}
