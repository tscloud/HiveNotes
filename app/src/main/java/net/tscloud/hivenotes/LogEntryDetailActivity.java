package net.tscloud.hivenotes;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import net.tscloud.hivenotes.db.LogEntryFeeding;
import net.tscloud.hivenotes.db.LogEntryGeneral;
import net.tscloud.hivenotes.db.LogEntryOther;
import net.tscloud.hivenotes.db.LogEntryPestMgmt;
import net.tscloud.hivenotes.db.LogEntryProductivity;

/**
 * An activity representing a single LogEntryGeneral detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link LogEntryListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link LogEntryDetailFragment}.
 */
public class LogEntryDetailActivity extends AppCompatActivity implements
        LogGeneralNotesFragment.OnLogGeneralNotesFragmentInteractionListener,
        LogProductivityFragment.OnLogProductivityFragmentInteractionListener,
        LogPestMgmtFragment.OnLogPestMgmntFragmentInteractionListener,
        LogFeedingFragment.OnLogFeedingFragmentInteractionListener,
        LogOtherFragment.OnLogOtherFragmentInteractionListener {

    public static final String TAG = "LogEntryDetailActivity";

    private long mHiveKey;
    private long mlogentryKey;

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
            mHiveKey = getIntent().getLongExtra(LogEntryListActivity.INTENT_HIVE_KEY, -1);
            mlogentryKey = getIntent().getLongExtra(LogEntryListActivity.INTENT_LOGENTRY_KEY, -1);

            Fragment fragment = null;

            switch (argItemId) {
                case "1":
                    fragment = LogGeneralNotesFragment.newInstance(mHiveKey, mlogentryKey);
                    break;
                case "2":
                    fragment = LogProductivityFragment.newInstance(mHiveKey, mlogentryKey);
                    break;
                case "3":
                    fragment = LogPestMgmtFragment.newInstance(mHiveKey, mlogentryKey);
                    break;
                case "4":
                    fragment = LogFeedingFragment.newInstance(mHiveKey, mlogentryKey);
                    break;
                case "5":
                    fragment = LogOtherFragment.newInstance(mHiveKey, mlogentryKey);
                    break;
                case "6":
                    // Save button
                    onSaveButton();
                    break;
                default:
                    fragment = new LogEntryDetailFragment();
                    break;
            }

            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.logentry_detail_container, fragment)
                        .commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, LogEntryListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
        */

        // Make the Up button perform like the Back button
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLogGeneralNotesFragmentInteraction(LogEntryGeneral aLogEntryGeneral) {
        Log.d(TAG, "return from LogGeneralNotesFragment...finish LogEntryDetailActivity");

        Intent data = new Intent();
        Bundle bundleData = new Bundle();
        bundleData.putSerializable(LogEntryListActivity.INTENT_LOGENTRY_GENERAL_DATA, aLogEntryGeneral);
        data.putExtras(bundleData);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onLogProductivityFragmentInteraction(LogEntryProductivity aLogEntryProductivity) {
        Log.d(TAG, "return from LogProductivityFragment...finish LogEntryDetailActivity");

        Intent data = new Intent();
        Bundle bundleData = new Bundle();
        bundleData.putSerializable(LogEntryListActivity.INTENT_LOGENTRY_PRODUCTIVITY_DATA, aLogEntryProductivity);
        data.putExtras(bundleData);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onLogPestMgmtFragmentInteraction(LogEntryPestMgmt alogEntryPestMgmt) {
        Log.d(TAG, "return from LogPestMgmntFragment...finish LogEntryDetailActivity");

        Intent data = new Intent();
        Bundle bundleData = new Bundle();
        bundleData.putSerializable(LogEntryListActivity.INTENT_LOGENTRY_PESTMGMT_DATA, alogEntryPestMgmt);
        data.putExtras(bundleData);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onLogFeedingFragmentInteraction(LogEntryFeeding aLogEntryFeeding) {
        Log.d(TAG, "return from LogFeedingFragment...finish LogEntryDetailActivity");

        Intent data = new Intent();
        Bundle bundleData = new Bundle();
        bundleData.putSerializable(LogEntryListActivity.INTENT_LOGENTRY_FEEDING_DATA, aLogEntryFeeding);
        data.putExtras(bundleData);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onLogOtherFragmentInteraction(LogEntryOther aLogEntryOther) {
        Log.d(TAG, "return from LogOtherFragment...finish LogEntryDetailActivity");

        Intent data = new Intent();
        Bundle bundleData = new Bundle();
        bundleData.putSerializable(LogEntryListActivity.INTENT_LOGENTRY_OTHER_DATA, aLogEntryOther);
        data.putExtras(bundleData);
        setResult(RESULT_OK, data);
        finish();
    }

    private void onSaveButton() {
        Log.d(TAG, "returning to LogEntryListActivity to perform save");
        setResult(RESULT_OK);
        finish();
    }
}
