package be.thomasmore.swrott;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import be.thomasmore.swrott.data.DatabaseHelper;
import be.thomasmore.swrott.data.Member;
import be.thomasmore.swrott.data.Picture;

public class ChoosePicture extends AppCompatActivity {

    private long _memberId;
    private DatabaseHelper _db;
    private Member _member = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_picture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _memberId = Helper.getLongExtra(this, Helper.MEMBERID_MESSAGE, null);

        if (_memberId == -1) return;

        _db = new DatabaseHelper(this);
        _member = _db.getMember(_memberId);

        final GridView gridView = (GridView) findViewById(R.id.grid);
        final TextView charnameText = (TextView) findViewById(R.id.member_name);
        final TextView teamnameText = (TextView) findViewById(R.id.teamname);

        charnameText.setText(_member.getPerson().getName());
        teamnameText.setText(_member.getTeam().getName());

        gridView.setAdapter(new ImageAdapter(this, Helper.PICTURES));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = Helper.PICTURES.get(position);
                Picture picture = _db.getPicture(path);

                _member.setPicture(picture);
                _db.updateMember(_member);

                done();
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
        Intent intent = new Intent();
        intent.putExtra(Helper.MEMBERID_MESSAGE, _memberId);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void toon(String text) {
        Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
    }
}
