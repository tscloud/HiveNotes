package net.tscloud.hivenotes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import net.tscloud.hivenotes.db.NotificationType;
import net.tscloud.hivenotes.helper.GetReminderTimeTaskData;
import net.tscloud.hivenotes.helper.GetReminderTimeTask;
import net.tscloud.hivenotes.helper.GetReminderTimeTaskRecData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

    // task references - needed to kill tasks on Fragment Destroy
    private GetReminderTimeTask mTaskId = null;
    private static final int TASK_ID = 0;

    // Hashmap to keep reminder descriptions
    private SparseArray<String> mRemDescMap;

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
        HashMap<Integer, View> viewHash = new LinkedHashMap<>(9);

        loadViewHash(viewHash,
                NotificationType.notificationTypeLookup.get(DIALOG_TAG_SPRINGINSPECTION),
                R.string.spring_inspection, findViewById(R.id.buttonEventSpringInspection));

        loadViewHash(viewHash,
                NotificationType.notificationTypeLookup.get(DIALOG_TAG_ADDHONEYSUPERS),
                R.string.add_honey_supers, findViewById(R.id.buttonEventAddHoneySupers));

        loadViewHash(viewHash,
                NotificationType.notificationTypeLookup.get(DIALOG_TAG_REMOVEDRONECOMB),
                R.string.remove_drone_comb, findViewById(R.id.buttonEventRemoveDroneComb));

        loadViewHash(viewHash,
                NotificationType.notificationTypeLookup.get(DIALOG_TAG_FEEDSUGERSYRUP),
                R.string.feed_sugar_syrup, findViewById(R.id.buttonEventFeedSugarSyrup));

        loadViewHash(viewHash,
                NotificationType.notificationTypeLookup.get(DIALOG_TAG_TREATFORMITES),
                R.string.treat_for_mites, findViewById(R.id.buttonEventTreatForMites));

        loadViewHash(viewHash,
                NotificationType.notificationTypeLookup.get(LogHiveHealthFragment.DIALOG_TAG_VARROA),
                R.string.remove_mite_treatment, findViewById(R.id.buttonEventRemoveMiteTreatment));

        loadViewHash(viewHash,
                NotificationType.notificationTypeLookup.get(LogGeneralNotesFragment.DIALOG_TAG_QUEEN),
                R.string.other_split_hive_rmndr, findViewById(R.id.buttonEventCheckLayingQueen));

        loadViewHash(viewHash,
                NotificationType.notificationTypeLookup.get(DIALOG_TAG_ADDMOUSEGUARD),
                R.string.add_mouse_guard, findViewById(R.id.buttonEventAddMouseGuard));

        loadViewHash(viewHash,
                NotificationType.notificationTypeLookup.get(LogOtherFragment.DIALOG_TAG_EVENTS),
                R.string.other_notes_string, findViewById(R.id.buttonEventOther));

        /* <<< NWO
         */
        /* setup and execute task
         */
        //create desc map
        mRemDescMap = new SparseArray<>();

        //data array to pass to timer task
        GetReminderTimeTaskRecData[] timerDataArray = new GetReminderTimeTaskRecData[9];

        // loop thru the view hash to set up args to GetReminderTimeTask & set listener
        int count = 0;
        for (Integer notType : viewHash.keySet()) {
            //View data
            View clickView = viewHash.get(notType);
            final TextView cvTitleText = (TextView)clickView.findViewById(R.id.dtpLaunchTextView);
            final TextView cvTimeText = (TextView)clickView.findViewById(R.id.textDTPTime);

            //build array to ent to timer task
            GetReminderTimeTaskRecData recData = new GetReminderTimeTaskRecData(
                    clickView, (TextView)clickView.findViewById(R.id.textDTPTime), notType);
            timerDataArray[count++] = recData;

            //set listener
            clickView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReminderPressed(cvTimeText, cvTitleText.getText().toString());
                }
            });
        }

        /* create task & execute
         */
        mTaskId = new MyGetReminderTimeTask(timerDataArray, TASK_ID, mHiveId,
                        calendar, dateFormat, timeFormat, this);
        // All AsynchTasks executed serially on same background Thread
        mTaskId.execute();
        // Each AsyncTask executes on its own Thread
        //mTaskDrone.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onDestroy() {
        if (mTaskId != null) {
            mTaskId.cancel(false);
        }
        super.onDestroy();
    }

    public void onReminderPressed(final TextView timeLbl, final String title) {
        Log.d(TAG, "onReminderPressed");

        final View dialogView = View.inflate(this, R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        //set title
        TextView dialogTitle = (TextView) dialogView.findViewById(R.id.titleDTPicker);
        dialogTitle.setText(title);

        // set the text of the "unset" button of our standard date/time picker
        //Button unsetBtn = (Button)dialogView.findViewById(R.id.date_time_unset);
        //unsetBtn.setText(getResources().getString(R.string.set_to_current));

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
                timeLbl.setText(timeString);
                timeLbl.setTag(time);

                Log.d(TAG, title + " : Set to CHOSEN Time: " + timeLbl.getTag());

                alertDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.date_time_unset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Time UNpicked: ");

                long time = System.currentTimeMillis();
                calendar.setTimeInMillis(time);
                Log.d(TAG, "Set to current Time (Dialog): " + time);

                // label has a human readable value; tag has millis value for DB
                String timeString = dateFormat.format(calendar.getTime()) + ' ' +
                        timeFormat.format(calendar.getTime());
                timeLbl.setText(timeString);
                timeLbl.setTag(time);

                alertDialog.dismiss();
            }
        });

        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    /* Utility method to load the view hash
     */
    private void loadViewHash(HashMap<Integer, View> aViewHash,
                              Integer aNotType, Integer aTitleRef, View aView) {

        //set the text of the proper TextView to supplied title
        ((TextView)aView.findViewById(R.id.dtpLaunchTextView)).setText(aTitleRef);

        //put Hash entry NotificationType -> clickable View
        aViewHash.put(aNotType, aView);

        //disable the clickable View
        aView.setEnabled(false);
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
                case TASK_ID:
                    mTaskId = null;
                    break;
            }
        }

        @Override
        protected void setRemDesc(String aDesc, int aNotType) {
            mRemDescMap.put(aNotType, aDesc);
        }
    }

}
