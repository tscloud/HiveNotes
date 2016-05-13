package net.tscloud.hivenotes;

import android.content.Intent;
import android.os.AsyncTask;
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
import net.tscloud.hivenotes.db.HiveNotesLogDO;
import net.tscloud.hivenotes.db.LogEntryFeeding;
import net.tscloud.hivenotes.db.LogEntryFeedingDAO;
import net.tscloud.hivenotes.db.LogEntryGeneral;
import net.tscloud.hivenotes.db.LogEntryGeneralDAO;
import net.tscloud.hivenotes.db.LogEntryOther;
import net.tscloud.hivenotes.db.LogEntryOtherDAO;
import net.tscloud.hivenotes.db.LogEntryPestMgmt;
import net.tscloud.hivenotes.db.LogEntryPestMgmtDAO;
import net.tscloud.hivenotes.db.LogEntryProductivity;
import net.tscloud.hivenotes.db.LogEntryProductivityDAO;
import net.tscloud.hivenotes.db.Notification;
import net.tscloud.hivenotes.db.NotificationDAO;
import net.tscloud.hivenotes.db.NotificationType;
import net.tscloud.hivenotes.helper.HiveCalendar;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        LogFeedingFragment.OnLogFeedingFragmentInteractionListener,
        LogOtherFragment.OnLogOtherFragmentInteractionListener {

    public static final String TAG = "LogEntryListActivity";

    // starting LogEntryDetailFragment as subactivity
    private static final int LOG_DETAIL_REQ_CODE = 1;

    public static String INTENT_ITEM_ID = "itemId";
    public static String INTENT_HIVE_KEY = "hiveKey";
    public static String INTENT_LOGENTRY_KEY = "logentryKey";

    private long mHiveKey;
    // need the Hive to get the name for the titlebar and other
    Hive mHiveForName;

    // Need to a reference to each of the Log Entry data objects
    public static String INTENT_LOGENTRY_GENERAL_DATA = "logentryGeneralData";
    LogEntryGeneral mLogEntryGeneralData;
    public static String INTENT_LOGENTRY_PRODUCTIVITY_DATA = "logentryProductivityData";
    LogEntryProductivity mLogEntryProductivityData;
    public static String INTENT_LOGENTRY_PESTMGMT_DATA = "logentryPestMGMTData";
    LogEntryPestMgmt mLogEntryPestMgmtData;
    public static String INTENT_LOGENTRY_FEEDING_DATA = "logentryFeedingData";
    LogEntryFeeding mLogEntryFeedingData;
    public static String INTENT_LOGENTRY_OTHER_DATA = "logentryOtherData";
    LogEntryOther mLogEntryOtherData;

    // This is what gets returned on call to get getPreviousLogData()
    public static String INTENT_PREVIOUS_DATA = "logentryPreviousData";
    private HiveNotesLogDO mPreviousLogData;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    // task references - needed to kill tasks on Fragment Destroy
    private UpdateDBTask mTask = null;

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

        // need the Hive name for the tile bar and other things
        Log.d(TAG, "reading Hive table");
        HiveDAO hiveDAO = new HiveDAO(this);
        mHiveForName = hiveDAO.getHiveById(mHiveKey);
        hiveDAO.close();

        View abView = getSupportActionBar().getCustomView();
        TextView abText = (TextView)abView.findViewById(R.id.mytext);
        abText.setText(mHiveForName.getName());

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
                    fragment = LogGeneralNotesFragment.newInstance(mHiveKey, -1);
                    mPreviousLogData = mLogEntryGeneralData;
                    break;
                case "2":
                    fragment = LogProductivityFragment.newInstance(mHiveKey, -1);
                    mPreviousLogData = mLogEntryProductivityData;
                    break;
                case "3":
                    fragment = LogPestMgmtFragment.newInstance(mHiveKey, -1);
                    mPreviousLogData = mLogEntryPestMgmtData;
                    break;
                case "4":
                    fragment = LogFeedingFragment.newInstance(mHiveKey, -1);
                    mPreviousLogData = mLogEntryFeedingData;
                    break;
                case "5":
                    fragment = LogOtherFragment.newInstance(mHiveKey, -1);
                    mPreviousLogData = mLogEntryOtherData;
                    break;
                case "6":
                    // Save button
                    //updateDB(mLogEntryGeneralData, mLogEntryProductivityData, mLogEntryPestMgmtData,
                    //        mLogEntryFeedingData, mLogEntryOtherData);
                    mTask = new UpdateDBTask();
                    mTask.execute();
                    break;
                default:
                    // should this be here? - LogEntryDetailFragment if really just
                    //  a dummy Fragment
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
            // w/ the selected item ID.
            Intent intent = new Intent(this, LogEntryDetailActivity.class);
            intent.putExtra(INTENT_ITEM_ID, id);
            intent.putExtra(INTENT_HIVE_KEY, mHiveKey);
            /*
            Need to pass an appropriate DO so it can potentially be accessed by fragment
             */
            switch (id) {
                case "1":
                    intent.putExtra(INTENT_PREVIOUS_DATA, mLogEntryGeneralData);
                    break;
                case "2":
                    intent.putExtra(INTENT_PREVIOUS_DATA, mLogEntryProductivityData);
                    break;
                case "3":
                    intent.putExtra(INTENT_PREVIOUS_DATA, mLogEntryPestMgmtData);
                    break;
                case "4":
                    intent.putExtra(INTENT_PREVIOUS_DATA, mLogEntryFeedingData);
                    break;
                case "5":
                    intent.putExtra(INTENT_PREVIOUS_DATA, mLogEntryOtherData);
                    break;
                case "6":
                    // Save button
                    //updateDB(mLogEntryGeneralData, mLogEntryProductivityData, mLogEntryPestMgmtData,
                    //       mLogEntryFeedingData, mLogEntryOtherData);
                    mTask = new UpdateDBTask();
                    mTask.execute();

                    break;
            }
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
    public void onLogOtherFragmentInteraction(LogEntryOther aLogEntryOther) {
        Log.d(TAG, "received LogEntryOther data object");
        mLogEntryOtherData = aLogEntryOther;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == LOG_DETAIL_REQ_CODE) && (resultCode == RESULT_OK)) {
            Log.d(TAG, "Returned from requestCode = " + requestCode);

            if (data == null) {
                // can this still happen -- there no longer is a Save button in
                //  LogEntryDetailActivity
                Log.d(TAG, "Save button pressed from LogEntryDetailActivity...perform save" +
                    "NOOP...!Bad to get here!");
                //updateDB(mLogEntryGeneralData, mLogEntryProductivityData, mLogEntryPestMgmtData,
                //        mLogEntryFeedingData, mLogEntryOtherData);
            }
            else {
                Bundle bundleData = data.getExtras();
                if (bundleData.keySet().contains(INTENT_LOGENTRY_GENERAL_DATA)) {
                    Log.d(TAG, "received LogEntryGeneral data object");
                    mLogEntryGeneralData =
                            (LogEntryGeneral) bundleData.getParcelable(INTENT_LOGENTRY_GENERAL_DATA);
                } else if (bundleData.keySet().contains(INTENT_LOGENTRY_PRODUCTIVITY_DATA)) {
                    Log.d(TAG, "received LogEntryProductivity data object");
                    mLogEntryProductivityData =
                            (LogEntryProductivity) bundleData.getParcelable(INTENT_LOGENTRY_PRODUCTIVITY_DATA);
                } else if (bundleData.keySet().contains(INTENT_LOGENTRY_PESTMGMT_DATA)) {
                    Log.d(TAG, "received LogEntryPestMgmt data object");
                    mLogEntryPestMgmtData =
                            (LogEntryPestMgmt) bundleData.getParcelable(INTENT_LOGENTRY_PESTMGMT_DATA);
                } else if (bundleData.keySet().contains(INTENT_LOGENTRY_FEEDING_DATA)) {
                    Log.d(TAG, "received LogEntryFeeding data object");
                    mLogEntryFeedingData =
                            (LogEntryFeeding) bundleData.getParcelable(INTENT_LOGENTRY_FEEDING_DATA);
                } else if (bundleData.keySet().contains(INTENT_LOGENTRY_OTHER_DATA)) {
                    Log.d(TAG, "received LogEntryOther data object");
                    mLogEntryOtherData =
                            (LogEntryOther) bundleData.getParcelable(INTENT_LOGENTRY_OTHER_DATA);
                }
            }
        }
    }

    @Override
    /** Used by fragments to get the data w/ which they may have been dealing */
    public HiveNotesLogDO getPreviousLogData() {
        return mPreviousLogData;
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
    public void onDestroy() {
        if (mTask != null) {
            mTask.cancel(false);
        }

        super.onDestroy();
    }

    /** do big update of all log tables in background
     */
    public class UpdateDBTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... unused) {
            Log.d(TAG, "UpdateDBTask("+ Thread.currentThread().getId() +
                ") : doInBackground");

            // This is the date that will be used for all the VISIT_DATE columns
            //  set it to Now in case there's nothing from LogEntryGeneral
            String generalDate = new Date().toString();

            if (mLogEntryGeneral != null) {
                Log.d(TAG, "about to persist LogEntryGeneral");
                //assume date is set from LogEntryGeneral?
                generalDate = mLogEntryGeneral.getVisitDate();

                LogEntryGeneralDAO mLogEntryGeneralDAO = new LogEntryGeneralDAO(this);
                if (mLogEntryGeneral.getId() == -1) {
                    mLogEntryGeneralDAO.createLogEntry(mLogEntryGeneral);
                }
                else {
                    mLogEntryGeneralDAO.updateLogEntry(mLogEntryGeneral);
                }
                mLogEntryGeneralDAO.close();
            }

            if (mLogEntryProductivity != null) {
                Log.d(TAG, "about to persist LogEntryProductivity");
                LogEntryProductivityDAO mLogEntryProductivityDAO = new LogEntryProductivityDAO(this);
                // set VISIT_DATE to value from LogEntryGeneral or Now
                mLogEntryProductivity.setVisitDate(generalDate);

                if (mLogEntryProductivity.getId() == -1) {
                    mLogEntryProductivityDAO.createLogEntry(mLogEntryProductivity);
                }
                else {
                    mLogEntryProductivityDAO.updateLogEntry(mLogEntryProductivity);
                }
                mLogEntryProductivityDAO.close();
            }

            if (mLogEntryPestMgmt != null) {
                Log.d(TAG, "about to persist LogEntryPestMgmt");
                LogEntryPestMgmtDAO mLogEntryPestMgmtDAO = new LogEntryPestMgmtDAO(this);
                // set VISIT_DATE to value from LogEntryGeneral or Now
                mLogEntryPestMgmt.setVisitDate(generalDate);

                // need to potentially do Notification 1st as its key may need
                //   creation prior to Log entry write
                createNotification(
                        mLogEntryPestMgmt.getDroneCellFndnRmndrTime(),
                        NotificationType.NOTIFY_PEST_REMOVE_DRONE,
                        mHiveKey);

                createNotification(
                        mLogEntryPestMgmt.getMitesTrtmntRmndrTime(),
                        NotificationType.NOTIFY_PEST_REMOVE_MITES,
                        mHiveKey);

                if (mLogEntryPestMgmt.getId() == -1) {
                    mLogEntryPestMgmtDAO.createLogEntry(mLogEntryPestMgmt);
                }
                else {
                    mLogEntryPestMgmtDAO.updateLogEntry(mLogEntryPestMgmt);
                }
                mLogEntryPestMgmtDAO.close();
            }

            if (mLogEntryFeeding != null) {
                Log.d(TAG, "about to persist LogEntryFeeding");
                LogEntryFeedingDAO mLogEntryFeedingDAO = new LogEntryFeedingDAO(this);
                // set VISIT_DATE to value from LogEntryGeneral or Now
                mLogEntryFeeding.setVisitDate(generalDate);

                if (mLogEntryFeeding.getId() == -1) {
                    mLogEntryFeedingDAO.createLogEntry(mLogEntryFeeding);
                }
                else {
                    mLogEntryFeedingDAO.updateLogEntry(mLogEntryFeeding);
                }
                mLogEntryFeedingDAO.close();
            }

            if (mLogEntryOther != null) {
                Log.d(TAG, "about to persist LogEntryOther");
                LogEntryOtherDAO mLogEntryOtherDAO = new LogEntryOtherDAO(this);
                // set VISIT_DATE to value from LogEntryGeneral or Now
                mLogEntryOther.setVisitDate(generalDate);

                // need to potentially do Notification 1st as its key may need
                //   creation prior to Log entry write
                createNotification(
                        mLogEntryOther.getRequeenRmndrTime(),
                        NotificationType.NOTIFY_OTHER_REQUEEN,
                        mHiveKey);

                createNotification(
                        mLogEntryOther.getSwarmRmndrTime(),
                        NotificationType.NOTIFY_OTHER_SWARM,
                        mHiveKey);

                createNotification(
                        mLogEntryOther.getSplitHiveRmndrTime(),
                        NotificationType.NOTIFY_OTHER_SPLIT_HIVE,
                        mHiveKey);

                if (mLogEntryOther.getId() == -1) {
                    mLogEntryOtherDAO.createLogEntry(mLogEntryOther);
                }
                else {
                    mLogEntryOtherDAO.updateLogEntry(mLogEntryOther);
                }
                mLogEntryOtherDAO.close();
            }

            /*
            finish the Activity?
             */
            //finish();
        }

        private long createNotification(long aStartTime, int aNotType, long aHiveKey) {
            Log.d(TAG, "in createNotification()");

            long notificationId = -1;
            long eventId = -1;

            // Do the Notification magic
            Notification wNot;
            NotificationDAO wNotDAO = new NotificationDAO(this);

            // read Notification by Type and Hive Id
            Log.d(TAG, "getNotificationByTypeAndHive(): aNotType:" + aNotType + " aHiveKey:" + aHiveKey);
            wNot = wNotDAO.getNotificationByTypeAndHive(aNotType, aHiveKey);

            // delete the corresponding Event <- ** handle errors **
            // we are doing this indiscriminately as we have to read the Event anyway to determine if
            // the times match and we can skip the update
            if ((wNot != null) && (wNot.getEventId() > 0)) {
                Log.d(TAG, "deleteEvent(): wNot.getEventId():" + wNot.getEventId());
                HiveCalendar.deleteEvent(this, wNot.getEventId());
            }

            if (aStartTime != -1) {
                // create new Event - hardcode endtime
                eventId = HiveCalendar.addEntryPublic(this,
                        aStartTime,
                        aStartTime+600000,
                        NotificationType.NOTIFICATION_TITLE,
                        NotificationType.getDesc(aNotType),
                        mHiveForName.getName());
            }

            // create/update/delete Notification
            if (wNot == null) {
                // we don't have a Notification -> make a new one
                Log.d(TAG, "createNotification(): eventId:" + eventId);
                wNot = wNotDAO.createNotification(-1, aHiveKey, eventId, aNotType);
            } else if (aStartTime != -1){
                // we already have a Notification -> update it w/ new event id
                Log.d(TAG, "updateNotification(): eventId:" + eventId);
                wNot.setEventId(eventId);
                wNot = wNotDAO.updateNotification(wNot);
            }
            else {
                // we want to delete the Notification
                Log.d(TAG, "updateNotification(): eventId:" + eventId);
                wNotDAO.deleteNotification(wNot);
            }

            // return the Notification Id
            if (wNot != null) {
                notificationId = wNot.getId();
            }

            Log.d(TAG, "return: notificationId:" + notificationId);

            return notificationId;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.d(TAG, "UpdateDBTask("+ Thread.currentThread().getId() +
                ") : onPostExecute");

            Toast.makeText(getApplicationContext(), "DB update complete", Toast.LENGTH_LONG).show();

            // all we need to do is nullify the Task reference
            mTask = null;
        }
    }
}
