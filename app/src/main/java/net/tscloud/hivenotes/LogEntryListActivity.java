package net.tscloud.hivenotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import net.tscloud.hivenotes.db.Hive;
import net.tscloud.hivenotes.db.HiveDAO;
import net.tscloud.hivenotes.db.LogEntryFeeding;
import net.tscloud.hivenotes.db.LogEntryFeedingDAO;
import net.tscloud.hivenotes.db.LogEntryGeneral;
import net.tscloud.hivenotes.db.LogEntryGeneralDAO;
import net.tscloud.hivenotes.db.LogEntryPestMgmt;
import net.tscloud.hivenotes.db.LogEntryPestMgmtDAO;
import net.tscloud.hivenotes.db.LogEntryProductivity;
import net.tscloud.hivenotes.db.LogEntryProductivityDAO;

import java.util.Date;


/**
 * An activity representing a list of LogEntries. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link LogEntryDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link LogEntryListFragment} and the item details
 * (if present) is a {@link LogEntryDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link LogEntryListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class LogEntryListActivity extends AppCompatActivity implements
        LogEntryListFragment.Callbacks,
        LogGeneralNotesFragment.OnLogGeneralNotesFragmentInteractionListener,
        LogProductivityFragment.OnLogProductivityFragmentInteractionListener,
        LogPestMgmtFragment.OnLogPestMgmntFragmentInteractionListener,
        LogFeedingFragent.OnLogFeedingFragmentInteractionListener {

    public static final String TAG = "LogEntryListActivity";

    // starting LogEntryDetailFragment as subactivity
    private static final int LOG_DETAIL_REQ_CODE = 1;

    public static String INTENT_ITEM_ID = "itemId";
    public static String INTENT_HIVE_KEY = "hiveKey";
    public static String INTENT_LOGENTRY_KEY = "logentryKey";

    private long mHiveKey;

    // Need to a reference to each of the Log Entry data objects
    public static String INTENT_LOGENTRY_GENERAL_DATA = "logentryGeneralData";
    LogEntryGeneral mLogEntryGeneralData;
    public static String INTENT_LOGENTRY_PRODUCTIVITY_DATA = "logentryProductivityData";
    LogEntryProductivity mLogEntryProductivityData;
    public static String INTENT_LOGENTRY_PESTMGMT_DATA = "logentryPestMGMTData";
    LogEntryPestMgmt mLogEntryPestMgmtData;
    public static String INTENT_LOGENTRY_FEEDING_DATA = "logentryFeedingData";
    LogEntryFeeding mLogEntryFeedingData;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logentry_list);

        // Custom Action Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the hive key from the Intent data
        Intent intent = getIntent();
        mHiveKey = intent.getLongExtra(MainActivity.INTENT_HIVE_KEY, -1);

        Log.d(TAG, "Called w/ hive key: " + mHiveKey);

        // need the Hive name for the tile bar
        Log.d(TAG, "reading Hive table");
        HiveDAO hiveDAO = new HiveDAO(this);
        Hive hiveForName = hiveDAO.getHiveById(mHiveKey);
        hiveDAO.close();

        View abView = getSupportActionBar().getCustomView();
        TextView abText = (TextView)abView.findViewById(R.id.mytext);
        abText.setText(hiveForName.getName());

        if (findViewById(R.id.logentry_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((LogEntryListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.logentry_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link LogEntryListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.

            Bundle arguments = new Bundle();
            arguments.putString(LogEntryDetailFragment.ARG_ITEM_ID, id);

            Fragment fragment = null;

            switch (id) {
                case "1":
                    // will this always be a new logentry? so pass -1?
                    fragment = LogGeneralNotesFragment.newInstance(mHiveKey, -1);
                    break;
                case "2":
                    // will this always be a new logentry? so pass -1?
                    fragment = LogProductivityFragment.newInstance(mHiveKey, -1);
                    break;
                case "3":
                    // will this always be a new logentry? so pass -1?
                    fragment = LogPestMgmtFragment.newInstance(mHiveKey, -1);
                    break;
                case "4":
                    // will this always be a new logentry? so pass -1?
                    fragment = LogFeedingFragent.newInstance(mHiveKey, -1);
                    break;
                case "6":
                    // Save button
                    updateDB(mLogEntryGeneralData, mLogEntryProductivityData, mLogEntryPestMgmtData,
                            mLogEntryFeedingData);
                    break;
                default:
                    fragment = new LogEntryDetailFragment();
                    break;
            }

            if (fragment != null) {
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.logentry_detail_container, fragment)
                        .commit();
            }

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent intent = new Intent(this, LogEntryDetailActivity.class);
            intent.putExtra(INTENT_ITEM_ID, id);
            intent.putExtra(INTENT_HIVE_KEY, mHiveKey);
            startActivityForResult(intent, LOG_DETAIL_REQ_CODE);
        }
    }

    @Override
    public void onLogGeneralNotesFragmentInteraction(LogEntryGeneral aLogEntryGeneral) {
        Log.d(TAG, "received LogEntryGeneral data object");
        mLogEntryGeneralData = aLogEntryGeneral;
    }

    @Override
    public void onLogProductivityFragmentInteraction(LogEntryProductivity aLogEntryProductivity) {
        Log.d(TAG, "received LogEntryProductivity data object");
        mLogEntryProductivityData = aLogEntryProductivity;
    }

    @Override
    public void onLogPestMgmtFragmentInteraction(LogEntryPestMgmt alogEntryPestMgmt) {
        Log.d(TAG, "received LogEntryPestMgmt data object");
        mLogEntryPestMgmtData = alogEntryPestMgmt;
    }

    @Override
    public void onLogFeedingFragmentInteraction(LogEntryFeeding aLogEntryFeeding) {
        Log.d(TAG, "received LogEntryFeeding data object");
        mLogEntryFeedingData = aLogEntryFeeding;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == LOG_DETAIL_REQ_CODE) && (resultCode == RESULT_OK)) {
            Log.d(TAG, "Returned from requestCode = " + requestCode);

            if (data == null) {
                Log.d(TAG, "Save button pressed from LogEntryDetailActivity...perform save");
                updateDB(mLogEntryGeneralData, mLogEntryProductivityData, mLogEntryPestMgmtData,
                        mLogEntryFeedingData);
            }
            else {
                Bundle bundleData = data.getExtras();
                if (bundleData.keySet().contains(INTENT_LOGENTRY_GENERAL_DATA)) {
                    Log.d(TAG, "received LogEntryGeneral data object");
                    mLogEntryGeneralData =
                            (LogEntryGeneral) bundleData.getSerializable(INTENT_LOGENTRY_GENERAL_DATA);
                } else if (bundleData.keySet().contains(INTENT_LOGENTRY_PRODUCTIVITY_DATA)) {
                    Log.d(TAG, "received LogEntryProductivity data object");
                    mLogEntryProductivityData =
                            (LogEntryProductivity) bundleData.getSerializable(INTENT_LOGENTRY_PRODUCTIVITY_DATA);
                } else if (bundleData.keySet().contains(INTENT_LOGENTRY_PESTMGMT_DATA)) {
                    Log.d(TAG, "received LogEntryPestMgmt data object");
                    mLogEntryPestMgmtData =
                            (LogEntryPestMgmt) bundleData.getSerializable(INTENT_LOGENTRY_PESTMGMT_DATA);
                } else if (bundleData.keySet().contains(INTENT_LOGENTRY_FEEDING_DATA)) {
                    Log.d(TAG, "received LogEntryFeeding data object");
                    mLogEntryFeedingData =
                            (LogEntryFeeding) bundleData.getSerializable(INTENT_LOGENTRY_FEEDING_DATA);
                }
            }
        }
    }

    private void updateDB(LogEntryGeneral aLogEntryGeneral, LogEntryProductivity aLogEntryProductivity,
                          LogEntryPestMgmt aLogEntryPestMgmt, LogEntryFeeding aLogEntryFeeding) {
        // This is the date that will be used for all the VISIT_DATE columns
        //  set it to Now in case there's nothing from LogEntryGeneral
        String generalDate = new Date().toString();

        if (aLogEntryGeneral != null) {
            //assume date is set from LogEttryGeneral?
            generalDate = aLogEntryGeneral.getVisitDate();

            LogEntryGeneralDAO aLogEntryGeneralDAO = new LogEntryGeneralDAO(this);
            if (aLogEntryGeneral.getId() == -1) {
                aLogEntryGeneralDAO.createLogEntry(aLogEntryGeneral);
            }
            else {
                aLogEntryGeneralDAO.updateLogEntry(aLogEntryGeneral);
            }
            aLogEntryGeneralDAO.close();
        }

        if (aLogEntryProductivity != null) {
            LogEntryProductivityDAO aLogEntryProductivityDAO = new LogEntryProductivityDAO(this);
            // set VISIT_DATE to value from LogEntryGeneral or Now
            aLogEntryProductivity.setVisitDate(generalDate);

            if (aLogEntryProductivity.getId() == -1) {
                aLogEntryProductivityDAO.createLogEntry(aLogEntryProductivity);
            }
            else {
                aLogEntryProductivityDAO.updateLogEntry(aLogEntryProductivity);
            }
            aLogEntryProductivityDAO.close();
        }

        if (aLogEntryPestMgmt != null) {
            LogEntryPestMgmtDAO aLogEntryPestMgmtDAO = new LogEntryPestMgmtDAO(this);
            // set VISIT_DATE to value from LogEntryGeneral or Now
            aLogEntryPestMgmt.setVisitDate(generalDate);

            if (aLogEntryPestMgmt.getId() == -1) {
                aLogEntryPestMgmtDAO.createLogEntry(aLogEntryPestMgmt);
            }
            else {
                aLogEntryPestMgmtDAO.updateLogEntry(aLogEntryPestMgmt);
            }
            aLogEntryPestMgmtDAO.close();
        }

        if (aLogEntryFeeding != null) {
            LogEntryFeedingDAO aLogEntryFeedingDAO = new LogEntryFeedingDAO(this);
            // set VISIT_DATE to value from LogEntryGeneral or Now
            aLogEntryFeeding.setVisitDate(generalDate);

            if (aLogEntryFeeding.getId() == -1) {
                aLogEntryFeedingDAO.createLogEntry(aLogEntryFeeding);
            }
            else {
                aLogEntryFeedingDAO.updateLogEntry(aLogEntryFeeding);
            }
            aLogEntryFeedingDAO.close();
        }

        /*
        finish the Activity?
         */
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