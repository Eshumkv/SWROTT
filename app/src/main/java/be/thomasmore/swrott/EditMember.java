package be.thomasmore.swrott;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;

import be.thomasmore.swrott.data.DatabaseHelper;
import be.thomasmore.swrott.data.Member;

public class EditMember extends AppCompatActivity {

    private long _memberId;
    private DatabaseHelper _db;
    private Member _member = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_member);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _memberId = getIntent().getLongExtra(Helper.MEMBERID_MESSAGE, -1);
        if (_memberId == -1) {
            Log.e("ERROR", "Seriously don't know what to do");
            Helper.showErrorDialog(this, MainActivity.class);
            return;
        }

        _db = new DatabaseHelper(this);
        setup(_memberId);
    }

    private void setup(long memberId) {
        _member = _db.getMember(memberId);

        final TextView membernameText = (TextView) findViewById(R.id.member_name);
        final TextView teamnameText = (TextView) findViewById(R.id.teamname);
        final ImageView pictureView = (ImageView) findViewById(R.id.picture);
        final ProgressBar expProgressBar = (ProgressBar) findViewById(R.id.experience);

        membernameText.setText(_member.getPerson().getName());
        teamnameText.setText(_member.getTeam().getName());

        expProgressBar.setMax(_member.getExpToLevel());
        expProgressBar.setProgress(_member.getExperience());

        Helper.updateStatsPart(_member.getStats(), this);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Bitmap picture = Helper.getPicture(
                        EditMember.this,
                        _member.getPicture().getPath(),
                        350,
                        350
                );
                if (picture != null) {
                    pictureView.setImageBitmap(picture);
                    pictureView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(EditMember.this, ChoosePicture.class);
                            intent.putExtra(Helper.MEMBERID_MESSAGE, _memberId);
                            startActivityForResult(intent, 1);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // Empty for now
            }

            _memberId = data.getLongExtra(Helper.MEMBERID_MESSAGE, -1);

            if (_memberId == -1) {
                Log.e("ERROR", "Seriously don't know what to do");
                Helper.showErrorDialog(this, MainActivity.class);
                return;
            }
            setup(_memberId);
        }
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
            case android.R.id.home:
                done();
                return true;
            case R.id.action_delete:
                showDeleteDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void done() {
        if (_member == null) {
            _member = _db.getMember(_memberId);
        }

        Intent intent = new Intent();
        intent.putExtra(Helper.TEAMID_MESSAGE, _member.getTeamId());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        done();
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
            .setTitle(R.string.dialog_deletemember_title)
            .setMessage(R.string.dialog_confirm_member_deletion)
            .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    _db.deleteMember(_memberId);
                    done();
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
