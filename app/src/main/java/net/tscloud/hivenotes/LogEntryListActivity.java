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
import net.tscloud.hivenotes.db.LogEntryGeneral;
import net.tscloud.hivenotes.db.LogEntryProductivity;


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
public class LogEntryListActivity extends AppCompatActivity
        implements LogEntryListFragment.Callbacks {

    public static final String TAG = "LogEntryListActivity";

    // starting LogEntryDetailFragment as subactivity
    private static final int LOG_DETAIL_REQ_CODE = 1;

    public static String INTENT_ITEM_ID = "itemId";
    public static String INTENT_HIVE_KEY = "hiveKey";
    public static String INTENT_LOGENTRY_KEY = "logentryKey";

    private long mHiveKey;

    // Need to a reference to each of the Log Entry data objects
    public static String INTENT_LOGENTRY_GENERAL_DATA = "logentryData";
    LogEntryGeneral mLogEntryGeneralData;
    public static String INTENT_LOGENTRY_PRODUCTIVITY_DATA = "logentryData";
    LogEntryProductivity mLogEntryProductivityData;

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
                default:
                    fragment = new LogEntryDetailFragment();
                    break;
            }

            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.logentry_detail_container, fragment)
                    .commit();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == LOG_DETAIL_REQ_CODE) && (resultCode == RESULT_OK)) {
            Log.d(TAG, "Returned from requestCode = " + requestCode);

            Bundle bundleData = data.getExtras();
            if (bundleData.keySet().contains(INTENT_LOGENTRY_GENERAL_DATA)) {
                mLogEntryGeneralData =
                        (LogEntryGeneral)bundleData.getSerializable(INTENT_LOGENTRY_GENERAL_DATA);
                Log.d(TAG, "received LogEntryGeneral data object");
            }
            else if (bundleData.keySet().contains(INTENT_LOGENTRY_PRODUCTIVITY_DATA)) {
                mLogEntryProductivityData =
                        (LogEntryProductivity)bundleData.getSerializable(INTENT_LOGENTRY_PRODUCTIVITY_DATA);
                Log.d(TAG, "received LogEntryProductivity data object");
            }
        }
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