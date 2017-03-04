package net.tscloud.hivenotes;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import net.tscloud.hivenotes.helper.LogEditTextDataEntry;
import net.tscloud.hivenotes.helper.LogEditTextDialogData;
import net.tscloud.hivenotes.helper.LogMultiSelectDataEntry;
import net.tscloud.hivenotes.helper.LogMultiSelectDialogData;
import net.tscloud.hivenotes.helper.LogSuperDataEntry;

public class EditHiveSingleActivity extends HiveDataEntryActivity implements
        EditHiveSingleFragment.OnEditHiveSingleFragmentInteractionListener {

    private static final String TAG = "EditHiveSingleActivity";
    private long mApiaryKey = -1;
    private long mHiveKey = -1;

    // needed for things like Dialog dismissal after its return w/ w/o data
    //private LogSuperDataEntry diagFragment;

    // Need a reference to the Fragment that we're going to launch as we may need to pass back data
    //  collected by Dialog
    //private EditHiveSingleFragment fragment = null;

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

        fragment = EditHiveSingleFragment.newInstance(mApiaryKey, mHiveKey);
        String fragTag = "EDIT_HIVE_SINGLE_FRAG";

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, fragment, fragTag);
        ft.commit();
    }

    @Override
    protected int getContainerViewId() {
        return R.id.fragment_placeholder;
    }

    @Override
    public void onEditHiveSingleFragmentInteraction(long hiveID, boolean newHive, boolean deleteHive) {
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

    @Override
    public void onBackPressed() {
        // Do the stuff we need to do in the dialog - essentially we're done =>
        //  so save everything
        if (diagFragment == null || !diagFragment.onBackPressed()) {
            // Do the stuff we need to do in the fragment - essentially we're done =>
            //  so save everything
            if (fragment == null || !fragment.onFragmentSave()) {
                // neither dialog nor fragment consumed event
                super.onBackPressed();
            }
        }
    }

    // Dialog w/ checkboxes
    /*
    @Override
    public void onLogLaunchDialog(LogMultiSelectDialogData aData) {
        //diagFragment = LogMultiSelectDialog.newInstance(aData);
        //diagFragment.show(getSupportFragmentManager(), aData.getTag());
        diagFragment = LogMultiSelectDataEntry.newInstance(aData);
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(aData.getTag())
                .replace(R.id.fragment_placeholder, diagFragment, aData.getTag())
                .commit();
    };

    // Dialog w/ edittext
    @Override
    public void onLogLaunchDialog(LogEditTextDialogData aData) {
        //diagFragment = LogEditTextDialog.newInstance(aData);
        //diagFragment.show(getSupportFragmentManager(), aData.getTag());
        diagFragment = LogEditTextDataEntry.newInstance(aData);
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(aData.getTag())
                .replace(R.id.fragment_placeholder, diagFragment, aData.getTag())
                .commit();

    };

    @Override
    public void onLogDataEntryOK(String[] aResults, long aResultRemTime, String aTag) {
        Log.d(TAG, "onLogDataEntryOK: OK button clicked");

        for (String s: aResults) {
            Log.d(TAG, s);
        }

        // TODO: nulling the diagFragment necessary/required/desired?
        diagFragment = null;

        fragment.setDialogData(aResults, aResultRemTime, aTag);
        //diagFragment.dismiss();
        getSupportFragmentManager().popBackStack();
    }
    */
}
