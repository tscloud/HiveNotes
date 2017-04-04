package net.tscloud.hivenotes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import net.tscloud.hivenotes.db.NotificationType;
import net.tscloud.hivenotes.helper.GetReminderTimeTaskData;
import net.tscloud.hivenotes.helper.GetReminderTimeTask;
import net.tscloud.hivenotes.helper.GetReminderTimeTaskRecData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    // task references - needed to kill tasks on Fragment Destroy
    private GetReminderTimeTask mTaskId = null;
    private static final int TASK_ID = 0;

    // Hashmap to keep reminder descriptions
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

        // get reference to the <include>s
        final View clickEventSpringInspection = findViewById(R.id.buttonEventSpringInspection);
        final View clickEventAddHoneySupers = findViewById(R.id.buttonEventAddHoneySupers);

        // set text of <include>s
        final TextView springInspectionText =
                (TextView)clickEventSpringInspection.findViewById(R.id.dtpLaunchTextView);
        springInspectionText.setText(R.string.spring_inspection);

        final TextView addHoneySupersText =
                (TextView)clickEventAddHoneySupers.findViewById(R.id.dtpLaunchTextView);
        addHoneySupersText.setText(R.string.add_honey_supers);

        // get TextViews that will hold time
        final TextView springInspectionTime =
                (TextView)clickEventSpringInspection.findViewById(R.id.textDTPTime);

        final TextView addHoneySupersTime =
                (TextView)clickEventAddHoneySupers.findViewById(R.id.textDTPTime);

        //disable the button until task is thru
        clickEventSpringInspection.setEnabled(false);
        clickEventAddHoneySupers.setEnabled(false);

        /** setup and execute task
         */
        //create desc map
        mRemDescMap = new HashMap<>();

        //data array to pass to timer task
        GetReminderTimeTaskRecData[] timerDataArray = new GetReminderTimeTaskRecData[1];

        //spring inspection
        GetReminderTimeTaskRecData recData1 = new GetReminderTimeTaskRecData(
                clickEventSpringInspection, springInspectionTime,
                NotificationType.notificationTypeLookup.get(DIALOG_TAG_SPRINGINSPECTION));

        //add data to array
        timerDataArray[0] = recData1;

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
