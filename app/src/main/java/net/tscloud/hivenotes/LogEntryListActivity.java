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

    // Map to hold all the Notifications for a Hive so we just do 1 DB read
    private Map<Integer, Notification> mHiveNotifications;

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
                    // anything w/ Reminders needs to potentially get some timestamps
                    //  we get them here as opposed to the Fragment as there are only a set # of
                    //  Reminders per Hive (currently 5) -> we don't want to read them for every Log Entry
                    if (mLogEntryPestMgmtData == null) {
                        mLogEntryPestMgmtData = new LogEntryPestMgmt();
                        mLogEntryPestMgmtData.setDroneCellFndnRmndrTime(
                            getReminderTimes(
                                NotificationType.NOTIFY_PEST_REMOVE_DRONE, mHiveKey));
                        mLogEntryPestMgmtData.setMitesTrtmntRmndrTime(
                            getReminderTimes(
                                NotificationType.NOTIFY_PEST_REMOVE_MITES, mHiveKey));
                    }
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
                    updateDB(mLogEntryGeneralData, mLogEntryProductivityData, mLogEntryPestMgmtData,
                            mLogEntryFeedingData, mLogEntryOtherData);
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
                    if (mLogEntryPestMgmtData == null) {
                        mLogEntryPestMgmtData = new LogEntryPestMgmt();
                        mLogEntryPestMgmtData.setDroneCellFndnRmndrTime(
                                getReminderTimes(
                                        NotificationType.NOTIFY_PEST_REMOVE_DRONE, mHiveKey));
                        mLogEntryPestMgmtData.setMitesTrtmntRmndrTime(
                                getReminderTimes(
                                        NotificationType.NOTIFY_PEST_REMOVE_MITES, mHiveKey));
                    }
                    mPreviousLogData = mLogEntryPestMgmtData;
                    intent.putExtra(INTENT_PREVIOUS_DATA, mLogEntryPestMgmtData);
                    break;
                case "4":
                    intent.putExtra(INTENT_PREVIOUS_DATA, mLogEntryFeedingData);
                    break;
                case "5":
                    intent.putExtra(INTENT_PREVIOUS_DATA, mLogEntryOtherData);
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
                Log.d(TAG, "Save button pressed from LogEntryDetailActivity...perform save");
                updateDB(mLogEntryGeneralData, mLogEntryProductivityData, mLogEntryPestMgmtData,
                        mLogEntryFeedingData, mLogEntryOtherData);
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
                } else if (bundleData.keySet().contains(INTENT_LOGENTRY_OTHER_DATA)) {
                    Log.d(TAG, "received LogEntryOther data object");
                    mLogEntryOtherData =
                            (LogEntryOther) bundleData.getSerializable(INTENT_LOGENTRY_OTHER_DATA);
                }
            }
        }
    }

    @Override
    /** Used by fragments to get the data w/ which they may have been dealing */
    public HiveNotesLogDO getPreviousLogData() {
        return mPreviousLogData;
    }

    private void updateDB(LogEntryGeneral aLogEntryGeneral, LogEntryProductivity aLogEntryProductivity,
                          LogEntryPestMgmt aLogEntryPestMgmt, LogEntryFeeding aLogEntryFeeding,
                          LogEntryOther aLogEntryOther) {
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

            // need to potentially do Notification 1st as its key may need
            //   creation prior to Log entry write
            if (aLogEntryPestMgmt.getDroneCellFndnRmndrTime() != -1) {
                //Create Notification
                createNotification(
                    aLogEntryPestMgmt.getDroneCellFndnRmndrTime(),
                    NotificationType.NOTIFY_PEST_REMOVE_DRONE,
                    mHiveKey);
            }

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

        if (aLogEntryOther != null) {
            LogEntryOtherDAO aLogEntryOtherDAO = new LogEntryOtherDAO(this);
            // set VISIT_DATE to value from LogEntryGeneral or Now
            aLogEntryOther.setVisitDate(generalDate);

            // TODO: Notification stuff goes here

            if (aLogEntryOther.getId() == -1) {
                aLogEntryOtherDAO.createLogEntry(aLogEntryOther);
            }
            else {
                aLogEntryOtherDAO.updateLogEntry(aLogEntryOther);
            }
            aLogEntryOtherDAO.close();
        }

        /*
        finish the Activity?
         */
        finish();
    }

    private long createNotification(long aStartTime, int aNotType, long aHiveKey) {

        long notificationId = -1;
        long eventId = -1;

        // Do the Notification magic
        Notification wNot;
        NotificationDAO wNotDAO = new NotificationDAO(this);

        // read Notification by Type and Hive Id
        wNot = wNotDAO.getNotificationByTypeAndHive(aNotType, aHiveKey);

        // delete the corresponding Event <- ** handle errors **
        if ((wNot != null) && (wNot.getEventId() > 0)) {
            HiveCalendar.deleteEvent(this, wNot.getEventId());
        }

        // create new Event & Reminder - hardcode endtime
        eventId = HiveCalendar.addEntryPublic(this,
            aStartTime,
            aStartTime+600000,
            NotificationType.NOTIFICATION_TITLE,
            NotificationType.getDesc(aNotType),
            mHiveForName.getName());

        if (eventId != -1) { //should work pretty much all the time, right?
            if (wNot == null) {
                // we don't have a Notification -> make a new one
                wNot = wNotDAO.createNotification(-1, aHiveKey, eventId, aNotType);
            } else {
                // we already have a Notification -> update it w/ new event id
                wNot.setEventId(eventId);
                wNot = wNotDAO.updateNotification(wNot);
            }
        }

        // return the Notification Id
        if (wNot != null) {
            notificationId = wNot.getId();
        }

        return notificationId;
    }

    private long getReminderTimes(int aType, long aHiveKey) {
        // Returns a reference to a Notification based on Notification Type
        //  and Hive Id
        long reply;
        List<Notification> listNotification;

        if (mHiveNotifications == null) {
            NotificationDAO notDAO = new NotificationDAO(this);
            listNotification = notDAO.getNotificationList(aHiveKey);
            if (listNotification != null) {
                mHiveNotifications = new HashMap<Integer, Notification>(5);
                for (Notification n : listNotification) {
                    mHiveNotifications.put(n.getRmndrType(), n);
                }
            }
        }

        Notification wNotification = mHiveNotifications.get(aType);
        reply = HiveCalendar.getEventTime(this, wNotification.getEventId());

        return reply;
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

/**
 * AsyncTask to handle Reminder getting
 */
//class GetReminderTimesTask extends AsyncTask<Void, Void, Void> {

//}