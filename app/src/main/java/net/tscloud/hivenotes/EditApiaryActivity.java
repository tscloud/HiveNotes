package net.tscloud.hivenotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import net.tscloud.hivenotes.db.Apiary;

public class EditApiaryActivity extends HiveDataEntryActivity implements
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

        fragment = EditApiaryFragment.newInstance(mProfileKey, mApiaryKey);
        String fragTag = "EDIT_PROFILE_FRAG";

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(getContainerViewId(), fragment, fragTag);
        ft.commit();
    }

    @Override
    protected int getContainerViewId() {
        return R.id.fragment_placeholder;
    }

    @Override
    public void onEditApiaryFragmentInteraction(Apiary aApiary) {
        Log.d(TAG, "...update Apiary - return to MainActivity");

        Intent data = new Intent();
        Bundle bundleData = new Bundle();
        bundleData.putParcelable(INTENT_APIARY_DATA, aApiary);
        data.putExtras(bundleData);
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
