package net.tscloud.hivenotes;

import android.content.Context;
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
import android.widget.Toast;

import net.tscloud.hivenotes.db.Hive;
import net.tscloud.hivenotes.db.HiveDAO;
import net.tscloud.hivenotes.db.HiveNotesLogDO;
import net.tscloud.hivenotes.db.LogEntryFeeding;
import net.tscloud.hivenotes.db.LogEntryFeedingDAO;
import net.tscloud.hivenotes.db.LogEntryGeneral;
import net.tscloud.hivenotes.db.LogEntryGeneralDAO;
import net.tscloud.hivenotes.db.LogEntryHiveHealthDAO;
import net.tscloud.hivenotes.db.LogEntryOther;
import net.tscloud.hivenotes.db.LogEntryOtherDAO;
import net.tscloud.hivenotes.db.LogEntryHiveHealth;
import net.tscloud.hivenotes.db.LogEntryProductivity;
import net.tscloud.hivenotes.db.LogEntryProductivityDAO;
import net.tscloud.hivenotes.db.Notification;
import net.tscloud.hivenotes.db.NotificationDAO;
import net.tscloud.hivenotes.db.NotificationType;
import net.tscloud.hivenotes.helper.HiveCalendar;
import net.tscloud.hivenotes.helper.LogEditTextDialogData;
import net.tscloud.hivenotes.helper.LogMultiSelectDialog;
import net.tscloud.hivenotes.helper.LogMultiSelectDialogData;
import net.tscloud.hivenotes.helper.LogSuperDataEntry;


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
        LogFragment.LogFragmentActivity,
        LogSuperDataEntry.onLogDataEntryInteractionListener {

    public static final String TAG = "LogEntryListActivity";

    // starting LogEntryDetailFragment as subactivity
    private static final int LOG_DETAIL_REQ_CODE = 5;
    private static final int GRAPH_REQ_CODE = 7;

    public final static String INTENT_ITEM_ID = "itemId";
    //public final static String INTENT_HIVE_KEY = "hiveKey";
    public final static String INTENT_LOGENTRY_KEY = "logentryKey";
    public final static String INTENT_LOGENTRY_DATE = "logentryDate";

    private long mHiveKey;
    // need the Hive to get the name for the titlebar and other
    private Hive mHiveForName;

    // set by ListFragment
    private long mLogDate = -1;

    // not currently passed in
    private long mLogKey;

    // Need to a reference to each of the Log Entry data objects
    public static final String INTENT_LOGENTRY_GENERAL_DATA = "logentryGeneralData";
    private LogEntryGeneral mLogEntryGeneralData;
    public static final String INTENT_LOGENTRY_PRODUCTIVITY_DATA = "logentryProductivityData";
    private LogEntryProductivity mLogEntryProductivityData;
    public static final String INTENT_LOGENTRY_PESTMGMT_DATA = "logentryPestMGMTData";
    private LogEntryHiveHealth mLogEntryHiveHealthData;
    public static final String INTENT_LOGENTRY_FEEDING_DATA = "logentryFeedingData";
    private LogEntryFeeding mLogEntryFeedingData;
    public static final String INTENT_LOGENTRY_OTHER_DATA = "logentryOtherData";
    private LogEntryOther mLogEntryOtherData;

    // This is what gets returned on call to get getPreviousLogData()
    public static String INTENT_PREVIOUS_DATA = "logentryPreviousData";
    private HiveNotesLogDO mPreviousLogData;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    // task references - needed to kill tasks on Activity Destroy
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
                    fragment = LogGeneralNotesFragment.newInstance(mHiveKey, mLogDate, -1);
                    mPreviousLogData = mLogEntryGeneralData;
                    break;
                case "2":
                    fragment = LogProductivityFragment.newInstance(mHiveKey, mLogDate, -1);
                    mPreviousLogData = mLogEntryProductivityData;
                    break;
                case "3":
                    fragment = LogHiveHealthFragment.newInstance(mHiveKey, mLogDate, -1);
                    mPreviousLogData = mLogEntryHiveHealthData;
                    break;
                case "4":
                    fragment = LogFeedingFragment.newInstance(mHiveKey, mLogDate, -1);
                    mPreviousLogData = mLogEntryFeedingData;
                    break;
                case "5":
                    fragment = LogOtherFragment.newInstance(mHiveKey, mLogDate, -1);
                    mPreviousLogData = mLogEntryOtherData;
                    break;
                case "6":
                    // Save button
                    mTask = new UpdateDBTask(this);
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
            intent.putExtra(INTENT_LOGENTRY_KEY, (long)-1);
            intent.putExtra(MainActivity.INTENT_HIVE_KEY, mHiveKey);
            intent.putExtra(INTENT_LOGENTRY_DATE, mLogDate);
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
                    intent.putExtra(INTENT_PREVIOUS_DATA, mLogEntryHiveHealthData);
                    break;
                case "4":
                    intent.putExtra(INTENT_PREVIOUS_DATA, mLogEntryFeedingData);
                    break;
                case "5":
                    intent.putExtra(INTENT_PREVIOUS_DATA, mLogEntryOtherData);
                    break;
                case "6":
                    // Save button - do not start Detail activity, just update and screw
                    mTask = new UpdateDBTask(this);
                    mTask.execute();
                    break;
            }
            // Save button - do not start Detail activity, just update and screw
            if (!id.equals("6")) {
                startActivityForResult(intent, LOG_DETAIL_REQ_CODE);
            }
        }
    }

    /**
     * Toolbar click handlers
     */
    public void hiveFeedingToolClickHandler (View v) {
        Log.d(TAG, "hiveFeedingToolClickHandler called");
        // Launch the Calendar Intent
        HiveCalendar.calendarIntent(this, System.currentTimeMillis());
    }

    public void hiveGeneralToolClickHandler (View v) {
        // Launch the Graph Activity
        Log.d(TAG, "hiveGeneralToolClickHandler called");
        Intent i = new Intent(this,GraphActivity.class);
        i.putExtra(MainActivity.INTENT_APIARY_KEY, mHiveForName.getApiary());
        i.putExtra(MainActivity.INTENT_HIVE_KEY, mHiveKey);
        startActivityForResult(i, GRAPH_REQ_CODE);
    }

    public void hiveOtherToolClickHandler (View v) {
        Toast.makeText(getApplicationContext(), R.string.na_msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "hiveOtherToolClickHandler called");
    }

    public void hiveProductionToolClickHandler (View v) {
        Toast.makeText(getApplicationContext(), R.string.na_msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "hiveProductionToolClickHandler called");
    }

    public void hivePestToolClickHandler (View v) {
        Toast.makeText(getApplicationContext(), R.string.na_msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "hivePestToolClickHandler called");
    }

    /*
    Coming back from LogFragment - set member var
     */

    //NWO
    @Override
    public void onLogFragmentInteraction(String aDOKey, HiveNotesLogDO aLogEntryDO) {
        Log.d(TAG, "received LogFragment data object...key: " + aDOKey);

        try {
            switch (aDOKey){
                case INTENT_LOGENTRY_GENERAL_DATA:
                    mLogEntryGeneralData = (LogEntryGeneral)aLogEntryDO;
                    break;
                case INTENT_LOGENTRY_PRODUCTIVITY_DATA:
                    mLogEntryProductivityData = (LogEntryProductivity)aLogEntryDO;
                    break;
                case INTENT_LOGENTRY_PESTMGMT_DATA:
                    mLogEntryHiveHealthData = (LogEntryHiveHealth)aLogEntryDO;
                    break;
                case INTENT_LOGENTRY_FEEDING_DATA:
                    mLogEntryFeedingData = (LogEntryFeeding)aLogEntryDO;
                    break;
                case INTENT_LOGENTRY_OTHER_DATA:
                    mLogEntryOtherData = (LogEntryOther)aLogEntryDO;
                    break;
            }
        } catch (ClassCastException e) {
            Log.d(TAG, "you passed me a LogDO w/ a mismatched key...WHAT ARE YOU THINKING?");
        }
    }

    /*
    1) Launch Dialogs - Fragment we're coming from will tell us which 1 to throw up
    2) Come back from Dialogs - via OK or Cancel
     */
    @Override
    public void onLogLaunchDialog(LogMultiSelectDialogData aData) {

    }

    @Override
    public void onLogLaunchDialog(LogEditTextDialogData aData) {

    }

    @Override
    public void onLogDataEntryOK(String[] aResults, long aResultRemTime, String aTag) {

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
            }
            else {
                Bundle bundleData = data.getExtras();
                if (bundleData.keySet().contains(INTENT_LOGENTRY_GENERAL_DATA)) {
                    Log.d(TAG, "received LogEntryGeneral data object");
                    mLogEntryGeneralData = bundleData.getParcelable(INTENT_LOGENTRY_GENERAL_DATA);
                } else if (bundleData.keySet().contains(INTENT_LOGENTRY_PRODUCTIVITY_DATA)) {
                    Log.d(TAG, "received LogEntryProductivity data object");
                    mLogEntryProductivityData = bundleData.getParcelable(INTENT_LOGENTRY_PRODUCTIVITY_DATA);
                } else if (bundleData.keySet().contains(INTENT_LOGENTRY_PESTMGMT_DATA)) {
                    Log.d(TAG, "received LogEntryHiveHealth data object");
                    mLogEntryHiveHealthData = bundleData.getParcelable(INTENT_LOGENTRY_PESTMGMT_DATA);
                } else if (bundleData.keySet().contains(INTENT_LOGENTRY_FEEDING_DATA)) {
                    Log.d(TAG, "received LogEntryFeeding data object");
                    mLogEntryFeedingData = bundleData.getParcelable(INTENT_LOGENTRY_FEEDING_DATA);
                } else if (bundleData.keySet().contains(INTENT_LOGENTRY_OTHER_DATA)) {
                    Log.d(TAG, "received LogEntryOther data object");
                    mLogEntryOtherData = bundleData.getParcelable(INTENT_LOGENTRY_OTHER_DATA);
                }
            }
        }
    }

    @Override
    /** Used by fragments to get the data w/ which they may have been dealing */
    public HiveNotesLogDO getPreviousLogData() {
        return mPreviousLogData;
    }

    @Override
    public long getHiveKey() {
        return mHiveKey;
    }

    @Override
    public void setActivityLogDate(long aLogDate) {
        Log.d(TAG, "setActivityLogDate:mLogDate: " + aLogDate);
        mLogDate = aLogDate;
        // Do we need to do this every time we set mLogDate?
        mLogEntryGeneralData = null;
        mLogEntryProductivityData = null;
        mLogEntryHiveHealthData = null;
        mLogEntryFeedingData = null;
        mLogEntryOtherData = null;
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

        public static final String TAG = "UpdateDBTask";

        private Context ctx;

        public UpdateDBTask(Context aCtx) {
            ctx = aCtx;
            Log.d(TAG, "UpdateDBTask("+ Thread.currentThread().getId() + ") : constructor");
        }

        @Override
        protected Void doInBackground(Void... unused) {
            Log.d(TAG, "UpdateDBTask("+ Thread.currentThread().getId() + ") : doInBackground");

            // This is the date that will be used for all the VISIT_DATE columns
            //  set it to Now in case there's nothing from LogEntryGeneral
            // NOW use mLogDate <-- should always be set via Fragment?
            //long generalDate = System.currentTimeMillis();

            if (mLogEntryGeneralData != null) {
                Log.d(TAG, "about to persist LogEntryGeneral");
                LogEntryGeneralDAO mLogEntryGeneralDAO = new LogEntryGeneralDAO(ctx);
                // set VISIT_DATE mLogDate - this should always be set properly by Fragment?
                mLogEntryGeneralData.setVisitDate(mLogDate);

                // need to potentially do Notification 1st as its key may need
                //   creation prior to Log entry write
                createNotification(
                        mLogEntryGeneralData.getQueenRmndrTime(),
                        NotificationType.NOTIFY_GENERAL_LAYING_QUEEN,
                        mHiveKey);

                // ** Notification time cleanup **
                //  Could be -2 indicating an UNSET operation had occurred. This cannot be persisted
                //  however.  It either needs to be a real time or -1 indicating that a Notification
                //  table read is necessary as a real time may have been set elsewhere.
                if (mLogEntryGeneralData.getQueenRmndrTime() == -2) {
                    mLogEntryGeneralData.setQueenRmndrTime(-1);
                }

                if (mLogEntryGeneralData.getId() == -1) {
                    mLogEntryGeneralDAO.createLogEntry(mLogEntryGeneralData);
                }
                else {
                    mLogEntryGeneralDAO.updateLogEntry(mLogEntryGeneralData);
                }
                mLogEntryGeneralDAO.close();

                //clear member vars
                mLogEntryGeneralData = null;
            }

            if (mLogEntryProductivityData != null) {
                Log.d(TAG, "about to persist LogEntryProductivity");
                LogEntryProductivityDAO mLogEntryProductivityDAO = new LogEntryProductivityDAO(ctx);
                // set VISIT_DATE mLogDate - this should always be set properly by Fragment?
                mLogEntryProductivityData.setVisitDate(mLogDate);

                if (mLogEntryProductivityData.getId() == -1) {
                    mLogEntryProductivityDAO.createLogEntry(mLogEntryProductivityData);
                }
                else {
                    mLogEntryProductivityDAO.updateLogEntry(mLogEntryProductivityData);
                }
                mLogEntryProductivityDAO.close();

                //clear member vars
                mLogEntryProductivityData = null;
            }

            if (mLogEntryHiveHealthData != null) {
                Log.d(TAG, "about to persist LogEntryHiveHealth");
                LogEntryHiveHealthDAO mLogEntryHiveHealthDAO = new LogEntryHiveHealthDAO(ctx);
                // set VISIT_DATE to value from LogEntryGeneral or Now
                mLogEntryHiveHealthData.setVisitDate(mLogDate);

                // need to potentially do Notification 1st as its key may need
                //   creation prior to Log entry write
                createNotification(
                        mLogEntryHiveHealthData.getVarroaTrtmntRmndrTime(),
                        NotificationType.NOTIFY_HEALTH_REMOVE_MITE,
                        mHiveKey);

                // ** Notification time cleanup **
                //  Could be -2 indicating an UNSET operation had occurred. This cannot be persisted
                //  however.  It either needs to be a real time or -1 indicating that a Notification
                //  table read is necessary as a real time may have been set elsewhere.
                if (mLogEntryHiveHealthData.getVarroaTrtmntRmndrTime() == -2) {
                    mLogEntryHiveHealthData.setVarroaTrtmntRmndrTime(-1);
                }

                if (mLogEntryHiveHealthData.getId() == -1) {
                    mLogEntryHiveHealthDAO.createLogEntry(mLogEntryHiveHealthData);
                }
                else {
                    mLogEntryHiveHealthDAO.updateLogEntry(mLogEntryHiveHealthData);
                }
                mLogEntryHiveHealthDAO.close();

                //clear member vars
                mLogEntryHiveHealthData = null;
            }

            if (mLogEntryFeedingData != null) {
                Log.d(TAG, "about to persist LogEntryFeeding");
                LogEntryFeedingDAO mLogEntryFeedingDAO = new LogEntryFeedingDAO(ctx);
                // set VISIT_DATE to value from LogEntryGeneral or Now
                mLogEntryFeedingData.setVisitDate(mLogDate);

                if (mLogEntryFeedingData.getId() == -1) {
                    mLogEntryFeedingDAO.createLogEntry(mLogEntryFeedingData);
                }
                else {
                    mLogEntryFeedingDAO.updateLogEntry(mLogEntryFeedingData);
                }
                mLogEntryFeedingDAO.close();

                //clear member vars
                mLogEntryFeedingData = null;
            }

            if (mLogEntryOtherData != null) {
                Log.d(TAG, "about to persist LogEntryOther");
                LogEntryOtherDAO mLogEntryOtherDAO = new LogEntryOtherDAO(ctx);
                // set VISIT_DATE to value from LogEntryGeneral or Now
                mLogEntryOtherData.setVisitDate(mLogDate);

                // need to potentially do Notification 1st as its key may need
                //   creation prior to Log entry write
                createNotification(
                        mLogEntryOtherData.getRequeenRmndrTime(),
                        NotificationType.NOTIFY_OTHER_OTHER,
                        mHiveKey);

                // ** Notification time cleanup **
                //  Could be -2 indicating an UNSET operation had occurred. This cannot be persisted
                //  however.  It either needs to be a real time or -1 indicating that a Notification
                //  table read is necessary as a real time may have been set elsewhere.
                if (mLogEntryOtherData.getRequeenRmndrTime() == -2) {
                    mLogEntryOtherData.setRequeenRmndrTime(-1);
                }

                if (mLogEntryOtherData.getId() == -1) {
                    mLogEntryOtherDAO.createLogEntry(mLogEntryOtherData);
                }
                else {
                    mLogEntryOtherDAO.updateLogEntry(mLogEntryOtherData);
                }
                mLogEntryOtherDAO.close();

                //clear member vars
                mLogEntryOtherData = null;
            }

            /*
            finish the Activity?
             */
            //finish();

            return(null);
        }

        private long createNotification(long aStartTime, int aNotType, long aHiveKey) {
            Log.d(TAG, "in createNotification()");

            long notificationId = -1;
            long eventId = -1;

            // Do the Notification magic
            Notification wNot;
            NotificationDAO wNotDAO = new NotificationDAO(ctx);

            // read Notification by Type and Hive Id
            Log.d(TAG, "getNotificationByTypeAndHive(): aNotType:" + aNotType + " aHiveKey:" + aHiveKey);
            wNot = wNotDAO.getNotificationByTypeAndHive(aNotType, aHiveKey);

            // delete the corresponding Event <- ** handle errors **
            // we are doing this indiscriminately as we have to read the Event anyway to determine if
            // the times match and we can skip the update
            if ((wNot != null) && (wNot.getEventId() > 0)) {
                Log.d(TAG, "deleteEvent(): wNot.getEventId():" + wNot.getEventId());
                HiveCalendar.deleteEvent(ctx, wNot.getEventId());
            }

            if (aStartTime > -1) {
                // create new Event - hardcode endtime
                eventId = HiveCalendar.addEntryPublic(ctx,
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
            } else if (aStartTime > -1){
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
            Log.d(TAG, "UpdateDBTask("+ Thread.currentThread().getId() + ") : onPostExecute");

            Toast.makeText(getApplicationContext(), "DB update complete", Toast.LENGTH_SHORT).show();

            // all we need to do is nullify the Task reference
            mTask = null;
        }
    }
}
