package net.tscloud.hivenotes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import net.tscloud.hivenotes.db.HiveNotesLogDO;
import net.tscloud.hivenotes.helper.LogSuperDataEntry;

/**
 * An activity representing a single LogEntryGeneral detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link LogEntryListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link LogEntryDetailFragment}.
 */
public class LogEntryDetailActivity extends HiveDataEntryActivity implements
        LogFragment.LogFragmentActivity,
        LogSuperDataEntry.onLogDataEntryInteractionListener {

    public static final String TAG = "LogEntryDetailActivity";

    // This is what gets returned on call to get getPreviousLogData()
    private HiveNotesLogDO mPreviousLogData;

    // needed for things like Dialog dismissal after its return w/ w/o data
    //private LogSuperDataEntry diagFragment;

    // Need a reference to the Fragment that we're going to launch as we may need to pass back data
    //  collected by Dialog
    //private LogFragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logentry_detail);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            String argItemId = getIntent().getStringExtra(LogEntryListActivity.INTENT_ITEM_ID);
            long hiveKey = getIntent().getLongExtra(MainActivity.INTENT_HIVE_KEY, -1);
            long logKey = getIntent().getLongExtra(LogEntryListActivity.INTENT_LOGENTRY_KEY, -1);
            long logEntryDate = getIntent().getLongExtra(LogEntryListActivity.INTENT_LOGENTRY_DATE, -1);

            try {
                mPreviousLogData = (HiveNotesLogDO)getIntent().getParcelableExtra(
                        LogEntryListActivity.INTENT_PREVIOUS_DATA);
            }
            catch (ClassCastException e) {
                // Log the exception but continue w/ NO previous log data
                Log.e(TAG, "*** Bad Previous Log Data passed in ***", e);
                mPreviousLogData = null;
            }

            String fragTag = null;

            switch (argItemId) {
                case "1":
                    fragment = LogGeneralNotesFragment.newInstance(hiveKey, logEntryDate, logKey);
                    fragTag = "LOG_GENERAL_FRAG";
                    break;
                case "2":
                    fragment = LogProductivityFragment.newInstance(hiveKey, logEntryDate, logKey);
                    fragTag = "LOG_PRODUCTIVITY_FRAG";
                    break;
                case "3":
                    fragment = LogHiveHealthFragment.newInstance(hiveKey, logEntryDate, logKey);
                    fragTag = "LOG_PEST_FRAG";
                    break;
                case "4":
                    fragment = LogFeedingFragment.newInstance(hiveKey, logEntryDate, logKey);
                    fragTag = "LOG_FEEDING_FRAG";
                    break;
                case "5":
                    fragment = LogOtherFragment.newInstance(hiveKey, logEntryDate, logKey);
                    fragTag = "LOG_OTHER_FRAG";
                    break;
                case "6":
                    // Save button - should we really ever be able to get here?
                    onSaveButton();
                    break;
                default:
                    //fragment = new LogEntryDetailFragment();
                    Log.d(TAG, "What are you doing ?!  I cannot decipher argItemId into proper Fragment type");
                    break;
            }

            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        //.addToBackStack(null)
                        .add(R.id.logentry_detail_container, fragment, fragTag)
                        .commit();
            }
        }
    }

    @Override
    protected int getContainerViewId() {
        return R.id.logentry_detail_container;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Make the Up button perform like the Back button
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
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
    */

    @Override
    public HiveNotesLogDO getPreviousLogData() {
        return mPreviousLogData;
    }

    /*
    Coming back from LogFragment - return w/ DO to LogEntryListActivity
     */

    //NWO
    @Override
    public void onLogFragmentInteraction(String aDOKey, HiveNotesLogDO aLogEntryDO) {

        Log.d(TAG, "return from a LogFragment...key: " + aDOKey +
            "...finish LogEntryDetailActivity");

        Intent data = new Intent();
        Bundle bundleData = new Bundle();
        bundleData.putParcelable(aDOKey, aLogEntryDO);
        data.putExtras(bundleData);
        setResult(RESULT_OK, data);
        finish();
    }

    // Save button - should we really ever be able to get here?
    private void onSaveButton() {
        Log.d(TAG, "returning to LogEntryListActivity to perform save");
        setResult(RESULT_OK);
        finish();
    }

    /*
    1) Launch Dialogs - Fragment we're coming from will tell us which 1 to throw up
    2) Come back from Dialogs - via OK or Cancel
     */

    /*
    // Dialog w/ checkboxes
    @Override
    public void onLogLaunchDialog(LogMultiSelectDialogData aData) {
        //diagFragment = LogMultiSelectDialog.newInstance(aData);
        //diagFragment.show(getSupportFragmentManager(), aData.getTag());
        diagFragment = LogMultiSelectDataEntry.newInstance(aData);
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(aData.getTag())
                .replace(R.id.logentry_detail_container, diagFragment, aData.getTag())
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
                .replace(R.id.logentry_detail_container, diagFragment, aData.getTag())
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
