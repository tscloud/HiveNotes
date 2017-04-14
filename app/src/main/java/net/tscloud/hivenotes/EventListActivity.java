package net.tscloud.hivenotes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import net.tscloud.hivenotes.db.Hive;
import net.tscloud.hivenotes.db.HiveDAO;
import net.tscloud.hivenotes.db.NotificationType;
import net.tscloud.hivenotes.helper.CreateNotificationTask;
import net.tscloud.hivenotes.helper.GetReminderTimeTaskData;
import net.tscloud.hivenotes.helper.GetReminderTimeTask;
import net.tscloud.hivenotes.helper.GetReminderTimeTaskRecData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by tscloud on 4/1/17.
 */

public class EventListActivity extends AppCompatActivity {

    private static final String TAG = "EventListActivity";

    private long mHiveId = -1;

    // constants used for Dialogs
    public static final String DIALOG_TAG_SPRINGINSPECTION = "springinspection";
    public static final String DIALOG_TAG_ADDHONEYSUPERS = "addhoneysupers";
    public static final String DIALOG_TAG_REMOVEDRONECOMB = "removedronecomb";
    public static final String DIALOG_TAG_FEEDSUGERSYRUP = "feedsugersyrup";
    public static final String DIALOG_TAG_TREATFORMITES = "treatformites";
    public static final String DIALOG_TAG_ADDMOUSEGUARD = "addmouseguard";

    // task references - needed to kill tasks on Activity Destroy
    private GetReminderTimeTask mGetRemTaskId = null;
    private CreateNotificationTask mCreateRemTaskId = null;
    private static final int GET_TASK_ID = 0;
    private static final int CREATE_TASK_ID = 1;

    // Hashmap to keep click views keyed on Notification type
    private HashMap<Integer, ChangedView> mViewHash;

    // Hashmap to keep reminder descriptions keyed on Notification type
    private HashMap<Integer, String> mRemDescMap;

    // time/date formatters
    protected static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG,
            Locale.getDefault());
    protected static final String TIME_PATTERN = "HH:mm";
    protected static final SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_PATTERN,
            Locale.getDefault());
    protected final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the profile key from the Intent data
        Intent intent = getIntent();
        mHiveId = intent.getLongExtra(MainActivity.INTENT_HIVE_KEY, -1);

        /* >>> NWO
         */
        // this is the big ol' hash that will contain all thgood stuff
        mViewHash = new HashMap<>(9);

        loadViewHash(mViewHash,
                NotificationType.notificationTypeLookup.get(DIALOG_TAG_SPRINGINSPECTION),
                R.string.spring_inspection, findViewById(R.id.buttonEventSpringInspection));

        loadViewHash(mViewHash,
                NotificationType.notificationTypeLookup.get(DIALOG_TAG_ADDHONEYSUPERS),
                R.string.add_honey_supers, findViewById(R.id.buttonEventAddHoneySupers));

        loadViewHash(mViewHash,
                NotificationType.notificationTypeLookup.get(DIALOG_TAG_REMOVEDRONECOMB),
                R.string.remove_drone_comb, findViewById(R.id.buttonEventRemoveDroneComb));

        loadViewHash(mViewHash,
                NotificationType.notificationTypeLookup.get(DIALOG_TAG_FEEDSUGERSYRUP),
                R.string.feed_sugar_syrup, findViewById(R.id.buttonEventFeedSugarSyrup));

        loadViewHash(mViewHash,
                NotificationType.notificationTypeLookup.get(DIALOG_TAG_TREATFORMITES),
                R.string.treat_for_mites, findViewById(R.id.buttonEventTreatForMites));

        loadViewHash(mViewHash,
                NotificationType.notificationTypeLookup.get(LogHiveHealthFragment.DIALOG_TAG_VARROA),
                R.string.remove_mite_treatment, findViewById(R.id.buttonEventRemoveMiteTreatment));

        loadViewHash(mViewHash,
                NotificationType.notificationTypeLookup.get(LogGeneralNotesFragment.DIALOG_TAG_QUEEN),
                R.string.other_split_hive_rmndr, findViewById(R.id.buttonEventCheckLayingQueen));

        loadViewHash(mViewHash,
                NotificationType.notificationTypeLookup.get(DIALOG_TAG_ADDMOUSEGUARD),
                R.string.add_mouse_guard, findViewById(R.id.buttonEventAddMouseGuard));

        loadViewHash(mViewHash,
                NotificationType.notificationTypeLookup.get(LogOtherFragment.DIALOG_TAG_EVENTS),
                R.string.other_notes_string, findViewById(R.id.buttonEventOther));

        /* <<< NWO
         */
        /* setup and execute task
         */
        //create desc map
        mRemDescMap = new HashMap<>();

        //data array to pass to timer task
        GetReminderTimeTaskRecData[] timerDataArray = new GetReminderTimeTaskRecData[9];

        // loop thru the view hash to set up args to GetReminderTimeTask & set listener
        int count = 0;
        for (final Integer notType : mViewHash.keySet()) {

            //View data
            final ChangedView clickChangedView = mViewHash.get(notType);
            final View clickView = clickChangedView.cvView;
            final TextView cvTitleText = (TextView)clickView.findViewById(R.id.dtpLaunchTextView);

            //build array to send to timer task
            GetReminderTimeTaskRecData recData = new GetReminderTimeTaskRecData(
                    clickView, (TextView)clickView.findViewById(R.id.textDTPTime), notType);
            timerDataArray[count++] = recData;

            //set listener
            clickView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReminderPressed(clickChangedView, cvTitleText.getText().toString(),
                            notType);
                }
            });
        }

        /* create task & execute
         */
        mGetRemTaskId = new MyGetReminderTimeTask(timerDataArray, GET_TASK_ID, mHiveId,
                        calendar, dateFormat, timeFormat, this);
        // All AsynchTasks executed serially on same background Thread
        mGetRemTaskId.execute();
        // Each AsyncTask executes on its own Thread
        //mGetRemTaskId.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onDestroy() {
        if (mGetRemTaskId != null) {
            mGetRemTaskId.cancel(false);
        }

        if (mCreateRemTaskId != null) {
            mCreateRemTaskId.cancel(false);
        }

        super.onDestroy();
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
        Log.d(TAG, "Back button clicked...save everything");

        // create arg Hash from View Hash
        HashMap<Integer, Long> argHash = new HashMap<>(mViewHash.size());
        for (Integer hashNotType : mViewHash.keySet()) {

            //View data
            final ChangedView clickChangedView = mViewHash.get(hashNotType);
            final View clickView = clickChangedView.cvView;
            final TextView cvTimeText = (TextView)clickView.findViewById(R.id.textDTPTime);

            // remember to only create new Notification if we should
            if (clickChangedView.cvHasChanged) {
                argHash.put(hashNotType, (Long)cvTimeText.getTag());
            }
        }

        /mCreateRemTaskId = new MyCreateNotificationTask(this, CREATE_TASK_ID, argHash);

        // All AsynchTasks executed serially on same background Thread
        mCreateRemTaskId.execute();
        // Each AsyncTask executes on its own Thread
        //mCreateRemTaskId.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        super.onBackPressed();
    }

    public void onReminderPressed(final ChangedView aTimeLbl, final String aTitle,
                                  final Integer aNotType) {
        Log.d(TAG, "onReminderPressed");

        //get the stuff we need from the ChangedView passed in
        final View clickView = aTimeLbl.cvView;
        final TextView cvTimeText = (TextView)clickView.findViewById(R.id.textDTPTime);

        final View dialogView = View.inflate(this, R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        //set title
        TextView dialogTitle = (TextView) dialogView.findViewById(R.id.titleDTPicker);
        dialogTitle.setText(aTitle);

        // optionally allow user to enter description for reminder
        //  Display desc possibly previously entered by user
        View linLayRemDesc = dialogView.findViewById(R.id.linearLayoutReminderDesc);
        final EditText remDescEdit = (EditText)linLayRemDesc.findViewById(R.id.editTextReminderDesc);
        remDescEdit.setText(mRemDescMap.get(aNotType));

        if (mRemDescMap.get(aNotType) != null) {
            linLayRemDesc.setVisibility(View.VISIBLE);
        }

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker datePicker = (DatePicker)dialogView.findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker)dialogView.findViewById(R.id.time_picker);

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());

                long time = calendar.getTimeInMillis();

                // label has a human readable value; tag has millis value for DB
                String timeString = dateFormat.format(calendar.getTime()) + ' ' +
                        timeFormat.format(calendar.getTime());
                cvTimeText.setText(timeString);
                cvTimeText.setTag(time);
                //don't forget to indicate change
                aTimeLbl.cvHasChanged = true;

                Log.d(TAG, aTitle + " : Set to CHOSEN Time: " + cvTimeText.getTag());

                // optionally allow user to enter description for reminder
                //  Set desc possibly entered by user - set the member hash
                if ((remDescEdit.getText() != null) && (remDescEdit.length() != 0)) {
                    mRemDescMap.put(aNotType, remDescEdit.getText().toString());
                }

                alertDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.date_time_unset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Time UNpicked: ");

                cvTimeText.setText(R.string.no_reminder_set);
                // IMPORTANT: -2 indicator of occurrence of UNSET operation
                cvTimeText.setTag((long)-2);
                //don't forget to indicate change
                aTimeLbl.cvHasChanged = true;

                alertDialog.dismiss();
            }
        });

        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    /* Utility method to load the view hash
     */
    private void loadViewHash(HashMap<Integer, ChangedView> aViewHash,
                              Integer aNotType, Integer aTitleRef, View aView) {

        //set the text of the proper TextView to supplied title
        ((TextView)aView.findViewById(R.id.dtpLaunchTextView)).setText(aTitleRef);

        //put Hash entry NotificationType -> clickable View
        aViewHash.put(aNotType, new ChangedView(aView));

        //disable the clickable View
        aView.setEnabled(false);
    }

    /** little class of a View & a boolean that indicates that it has been changed
     */
    private class ChangedView {

        private View cvView;
        private boolean cvHasChanged = false;

        private ChangedView ( View aView) {
            cvView = aView;
        }
    }

    /** subclass of the GetReminderTimeTask
     */
    private class MyGetReminderTimeTask extends GetReminderTimeTask {

        MyGetReminderTimeTask(GetReminderTimeTaskData aData, Context aCtx) {
            super(aData, aCtx);
        }

        MyGetReminderTimeTask(GetReminderTimeTaskRecData[] aData, int aTaskInd, long aHive,
                            Calendar aCal, DateFormat aDateFormat,
                            SimpleDateFormat aTimeFormat, Context aCtx) {
            super(aData, aTaskInd, aHive, aCal, aDateFormat, aTimeFormat, aCtx);
        }

        @Override
        protected void nullifyTaskRef(int taskRef) {
            Log.d(TAG, "in overridden GetReminderTimeTask.nullifyTaskRef(): taskRef:" + taskRef);
            switch (taskRef) {
                case GET_TASK_ID:
                    mGetRemTaskId = null;
                    break;
            }
        }

        @Override
        protected void setRemDesc(String aDesc, int aNotType) {
            mRemDescMap.put(aNotType, aDesc);
        }
    }

    /** subclass of the CreateNotificationTask
     */
    private class MyCreateNotificationTask extends CreateNotificationTask {

        private String mHiveName = null;
        private HashMap<Integer, Long> mTimeHash;

        private MyCreateNotificationTask(Context aCtx, int aTaskInd,
                                        HashMap<Integer, Long> aTimeHash) {
            super(aCtx, aTaskInd);
            mTimeHash = aTimeHash;
        }

        @Override
        protected Void doInBackground(Void... unused) {
            // loop thru the view hash to call super class createNotification() for each entry
            for (Integer notType : mTimeHash.keySet()) {
                createNotification(mTimeHash.get(notType),
                    notType, mRemDescMap.get(notType));
            }

            return(null);
        }

        @Override
        protected long getHiveId() {
            return mHiveId;
        }

        @Override
        protected String getHiveName() {
            if (mHiveName == null) {
                // need the Hive name
                Log.d(TAG, "reading Hive table");
                HiveDAO hiveDAO = new HiveDAO(mCtx);
                Hive hiveForName = hiveDAO.getHiveById(getHiveId());
                hiveDAO.close();

                mHiveName = hiveForName.getName();
            }

            return mHiveName;
        }

        @Override
        protected void nullifyTaskRef(int taskRef) {
            Log.d(TAG, "in overridden GetReminderTimeTask.nullifyTaskRef(): taskRef:" + taskRef);
            switch (taskRef) {
                case CREATE_TASK_ID:
                    mCreateRemTaskId = null;
                    break;
            }
        }
    }
}
