package net.tscloud.hivenotes;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class EditHiveSingleActivity extends AppCompatActivity implements
        EditHiveSingleFragment.OnEditHiveSingleFragmentInteractionListener {

    private static final String TAG = "EditHiveSingleActivity";
    private long mApiaryKey = -1;
    private long mHiveKey = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hive_single);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the profile key from the Intent data
        Intent intent = getIntent();
        mApiaryKey = intent.getLongExtra(MainActivity.INTENT_APIARY_KEY, -1);
        mHiveKey = intent.getLongExtra(MainActivity.INTENT_HIVE_KEY, -1);

        Fragment fragment = EditHiveSingleFragment.newInstance(mApiaryKey, mHiveKey);
        String fragTag = "EDIT_HIVE_SINGLE_FRAG";

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, fragment, fragTag);
        ft.commit();
    }

    @Override
    public void onEditHiveSingleFragmentInteraction(long hiveID, boolean newHive) {
        Log.d(TAG, "...new Hive - return to EditHiveActivity");

        Intent data = new Intent();
        data.putExtra(MainActivity.INTENT_HIVE_KEY, hiveID);
        data.putExtra(MainActivity.INTENT_NEW_HIVE, newHive);
        setResult(RESULT_OK, data);
        finish();
    }

    // Make the Up button perform like the Back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
