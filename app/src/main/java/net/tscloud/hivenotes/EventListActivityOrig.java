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
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import net.tscloud.hivenotes.db.NotificationType;
import net.tscloud.hivenotes.helper.GetReminderTimeTask;
import net.tscloud.hivenotes.helper.GetReminderTimeTaskData;
import net.tscloud.hivenotes.helper.GetReminderTimeTaskRecData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by tscloud on 4/1/17.
 */

public class EventListActivityOrig extends AppCompatActivity {

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

        // get reference to the <include>s
        final View clickEventSpringInspection = findViewById(R.id.buttonEventSpringInspection);
        final View clickEventAddHoneySupers = findViewById(R.id.buttonEventAddHoneySupers);
        final View clickEventRemoveDroneComb = findViewById(R.id.buttonEventRemoveDroneComb);
        final View clickEventFeedSugarSyrup = findViewById(R.id.buttonEventFeedSugarSyrup);
        final View clickEventTreatForMites = findViewById(R.id.buttonEventTreatForMites);
        final View clickEventRemoveMiteTreatment = findViewById(R.id.buttonEventRemoveMiteTreatment);
        final View clickEventCheckLayingQueen = findViewById(R.id.buttonEventCheckLayingQueen);
        final View clickEventAddMouseGuard = findViewById(R.id.buttonEventAddMouseGuard);
        final View clickEventOther = findViewById(R.id.buttonEventOther);

        // set text of <include>s
        final TextView springInspectionText =
                (TextView)clickEventSpringInspection.findViewById(R.id.dtpLaunchTextView);
        springInspectionText.setText(R.string.spring_inspection);

        final TextView addHoneySupersText =
                (TextView)clickEventAddHoneySupers.findViewById(R.id.dtpLaunchTextView);
        addHoneySupersText.setText(R.string.add_honey_supers);

        final TextView removeDroneCombText =
                (TextView)clickEventRemoveDroneComb.findViewById(R.id.dtpLaunchTextView);
        removeDroneCombText.setText(R.string.remove_drone_comb);

        final TextView feedSugarSyrupText =
                (TextView)clickEventFeedSugarSyrup.findViewById(R.id.dtpLaunchTextView);
        feedSugarSyrupText.setText(R.string.feed_sugar_syrup);

        final TextView treatForMitesText =
                (TextView)clickEventTreatForMites.findViewById(R.id.dtpLaunchTextView);
        treatForMitesText.setText(R.string.feed_sugar_syrup);

        final TextView removeMiteTreatmentText =
                (TextView)clickEventRemoveMiteTreatment.findViewById(R.id.dtpLaunchTextView);
        removeMiteTreatmentText.setText(R.string.remove_mite_treatment);

        final TextView checkLayingQueenText =
                (TextView)clickEventCheckLayingQueen.findViewById(R.id.dtpLaunchTextView);
        checkLayingQueenText.setText(R.string.other_split_hive_rmndr);

        final TextView addMouseGuardText =
                (TextView)clickEventAddMouseGuard.findViewById(R.id.dtpLaunchTextView);
        addMouseGuardText.setText(R.string.add_mouse_guard);

        final TextView otherText =
                (TextView)clickEventOther.findViewById(R.id.dtpLaunchTextView);
        otherText.setText(R.string.other_notes_string);

        // get TextViews that will hold time
        final TextView springInspectionTime =
                (TextView)clickEventSpringInspection.findViewById(R.id.textDTPTime);

        final TextView addHoneySupersTime =
                (TextView)clickEventAddHoneySupers.findViewById(R.id.textDTPTime);

        final TextView removeDroneCombTime =
                (TextView)clickEventRemoveDroneComb.findViewById(R.id.textDTPTime);

        final TextView feedSugarSyrupTime =
                (TextView)clickEventFeedSugarSyrup.findViewById(R.id.textDTPTime);

        final TextView treatForMitesTime =
                (TextView)clickEventTreatForMites.findViewById(R.id.textDTPTime);

        final TextView removeMiteTreatmentTime =
                (TextView)clickEventRemoveMiteTreatment.findViewById(R.id.textDTPTime);

        final TextView checkLayingQueenTime =
                (TextView)clickEventCheckLayingQueen.findViewById(R.id.textDTPTime);

        final TextView addMouseGuardTime =
                (TextView)clickEventAddMouseGuard.findViewById(R.id.textDTPTime);

        final TextView otherTime =
                (TextView)clickEventOther.findViewById(R.id.textDTPTime);

        //disable the button until task is thru
        clickEventSpringInspection.setEnabled(false);
        clickEventAddHoneySupers.setEnabled(false);
        clickEventRemoveDroneComb.setEnabled(false);
        clickEventFeedSugarSyrup.setEnabled(false);
        clickEventTreatForMites.setEnabled(false);
        clickEventRemoveMiteTreatment.setEnabled(false);
        clickEventCheckLayingQueen.setEnabled(false);
        clickEventAddMouseGuard.setEnabled(false);
        clickEventOther.setEnabled(false);

        /* setup and execute task
         */
        //create desc map
        mRemDescMap = new SparseArray<>();

        //data array to pass to timer task
        GetReminderTimeTaskRecData[] timerDataArray = new GetReminderTimeTaskRecData[9];

        //--spring inspection
        GetReminderTimeTaskRecData recData1 = new GetReminderTimeTaskRecData(
                clickEventSpringInspection, springInspectionTime,
                NotificationType.notificationTypeLookup.get(DIALOG_TAG_SPRINGINSPECTION));
        //add data to array
        timerDataArray[0] = recData1;

        //--add honey supers
        GetReminderTimeTaskRecData recData2 = new GetReminderTimeTaskRecData(
                clickEventAddHoneySupers, addHoneySupersTime,
                NotificationType.notificationTypeLookup.get(DIALOG_TAG_ADDHONEYSUPERS));
        //add data to array
        timerDataArray[1] = recData2;

        //--remove drone comb
        GetReminderTimeTaskRecData recData3 = new GetReminderTimeTaskRecData(
                clickEventRemoveDroneComb, removeDroneCombTime,
                NotificationType.notificationTypeLookup.get(DIALOG_TAG_REMOVEDRONECOMB));
        //add data to array
        timerDataArray[2] = recData3;

        //--feed suger syrup
        GetReminderTimeTaskRecData recData4 = new GetReminderTimeTaskRecData(
                clickEventFeedSugarSyrup, feedSugarSyrupTime,
                NotificationType.notificationTypeLookup.get(DIALOG_TAG_FEEDSUGERSYRUP));
        //add data to array
        timerDataArray[3] = recData4;

        //--treat for mites
        GetReminderTimeTaskRecData recData5 = new GetReminderTimeTaskRecData(
                clickEventTreatForMites, treatForMitesTime,
                NotificationType.notificationTypeLookup.get(DIALOG_TAG_TREATFORMITES));
        //add data to array
        timerDataArray[4] = recData5;

        //--remove mite treatment
        GetReminderTimeTaskRecData recData6 = new GetReminderTimeTaskRecData(
                clickEventRemoveMiteTreatment, removeMiteTreatmentTime,
                NotificationType.notificationTypeLookup.get(LogHiveHealthFragment.DIALOG_TAG_VARROA));
        //add data to array
        timerDataArray[5] = recData6;

        //--check for laying queen
        GetReminderTimeTaskRecData recData7 = new GetReminderTimeTaskRecData(
                clickEventCheckLayingQueen, checkLayingQueenTime,
                NotificationType.notificationTypeLookup.get(LogGeneralNotesFragment.DIALOG_TAG_QUEEN));
        //add data to array
        timerDataArray[6] = recData7;

        //--add mouse guard
        GetReminderTimeTaskRecData recData8 = new GetReminderTimeTaskRecData(
                clickEventAddMouseGuard, addMouseGuardTime,
                NotificationType.notificationTypeLookup.get(DIALOG_TAG_ADDMOUSEGUARD));
        //add data to array
        timerDataArray[7] = recData8;

        //--other
        GetReminderTimeTaskRecData recData9 = new GetReminderTimeTaskRecData(
                clickEventOther, otherTime,
                NotificationType.notificationTypeLookup.get(LogOtherFragment.DIALOG_TAG_EVENTS));
        //add data to array
        timerDataArray[8] = recData9;

        /* create task & execute
         */
        mTaskId = new MyGetReminderTimeTask(timerDataArray, TASK_ID, mHiveId,
                        calendar, dateFormat, timeFormat, this);
        // All AsynchTasks executed serially on same background Thread
        mTaskId.execute();
        // Each AsyncTask executes on its own Thread
        //mTaskDrone.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        /* set click listeners
         */
        clickEventSpringInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReminderPressed(springInspectionTime, springInspectionText.getText().toString());
            }
        });
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
