package net.tscloud.hivenotes;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import net.tscloud.hivenotes.db.Apiary;

public class EditApiaryActivity extends AppCompatActivity implements
        EditApiaryFragment.OnEditApiaryFragmentInteractionListener {

    private static final String TAG = "EditApiaryActivity";
    private long mProfileKey = -1;
    private long mApiaryKey = -1;

    // Intent data keys
    public final static String INTENT_APIARY_DATA = "apiaryData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_apiary);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the profile key from the Intent data
        Intent intent = getIntent();
        mProfileKey = intent.getLongExtra(MainActivity.INTENT_PROFILE_KEY, -1);
        mApiaryKey = intent.getLongExtra(MainActivity.INTENT_APIARY_KEY, -1);

        Fragment fragment = EditApiaryFragment.newInstance(mProfileKey, mApiaryKey);
        String fragTag = "EDIT_PROFILE_FRAG";

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, fragment, fragTag);
        ft.commit();
    }

    @Override
    public void onEditApiaryFragmentInteraction(Apiary aApiary) {
        Log.d(TAG, "...update Apiary - return to MainActivity/EditHiveActivity");

        Intent data = null;
        if (aApiary != null) {
            data = new Intent();
            Bundle bundleData = new Bundle();
            bundleData.putParcelable(INTENT_APIARY_DATA, aApiary);
            data.putExtras(bundleData);
        }
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
