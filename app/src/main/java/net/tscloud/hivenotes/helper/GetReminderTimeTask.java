package net.tscloud.hivenotes.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by tscloud on 4/28/16.
 */
abstract public class GetReminderTimeTask extends AsyncTask<Void, Void, String[][]> {

    public static final String TAG = "GetReminderTimeTask";

    private GetReminderTimeTaskRecData[] data;
    private int taskInd;
    private long hive;
    private Calendar cal;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private Context ctx;

    // Legacy Constructor
    public GetReminderTimeTask(GetReminderTimeTaskData aData, Context aCtx) {
        //make a single data entry
        GetReminderTimeTaskRecData dataEntry =
                new GetReminderTimeTaskRecData(aData.btn, aData.txt, aData.type);
        //create the member data array and add the entry
        this.data = new GetReminderTimeTaskRecData[1];
        this.data[0] = dataEntry;

        //set the other stuff
        this.taskInd = aData.taskInd;
        this.hive = aData.hive;
        this.cal = aData.cal;
        this.dateFormat = aData.dateFormat;
        this.timeFormat = aData.timeFormat;
        this.ctx = aCtx;

        Log.d(TAG, "GetReminderTimeTask(:" + Thread.currentThread().getId() +
            ") : constructor");
    }

    public GetReminderTimeTask(GetReminderTimeTaskRecData[] aData, int aTaskInd, long aHive,
                               Calendar aCal, DateFormat aDateFormat,
                               SimpleDateFormat aTimeFormat, Context aCtx) {
        this.data = aData;
        this.taskInd = aTaskInd;
        this.hive = aHive;
        this.cal = aCal;
        this.dateFormat = aDateFormat;
        this.timeFormat = aTimeFormat;
        this.ctx = aCtx;
    }

    @Override
    protected String[][] doInBackground(Void... unused) {
        Log.d(TAG, "GetReminderTimeTask(:" + Thread.currentThread().getId() +
            ") : doInBackground");

        //pause to simulate work
        //try {
        //    Thread.sleep(2000);
        //}
        //catch (InterruptedException e) {
        //    Log.d(TAG, "GetReminderTimeTask(" + data.taskInd + ":" + Thread.currentThread().getId() +
        //        ") : InterruptedException");
        //}

        // perform I/O operation - the reason we're using an AsyncTask
        //  do so for each entry in the data[]
        String[][] reply = new String[data.length][2];
        String[] subReply;
        int count = 0;

        for (GetReminderTimeTaskRecData aData : data) {
            subReply = new String[2];
            subReply = HiveCalendar.getReminderTimeAndDesc(ctx, aData.type, hive);
            Log.d(TAG, "GetReminderTimeTask(" + taskInd + ":" + Thread.currentThread().getId() +
                    ") : doInBackground : reminderMillis: " + subReply[0] + " : reminderDesc: " +
                    subReply[1]);
            reply[count++] = subReply;
        }

        return reply;
    }

    @Override
    protected void onPostExecute(String[][] timeDesc) {
        Log.d(TAG, "GetReminderTimeTask(" + Thread.currentThread().getId() +
            ") : onPostExecute");

        long longTime = -1;
        int count = 0;

        for (GetReminderTimeTaskRecData aData : data) {
            try {
                longTime = Long.parseLong(timeDesc[count][0]);
            } catch (NumberFormatException e) {
                // NOOP - keep value as -1
            }

            if (longTime != -1) {
                cal.setTimeInMillis(longTime);
                String fDate = dateFormat.format(cal.getTime());
                String fTime = timeFormat.format(cal.getTime());
                String fDateTime = fDate + ' ' + fTime;
                Log.d(TAG, "GetReminderTimeTask(" + Thread.currentThread().getId() +
                        ") : onPostExecute : fDateTime: " + fDateTime);

                aData.txt.setTag(longTime);
                aData.txt.setText(fDateTime);
            }

            // call abstract method to set reminder description & reenable button
            setRemDesc(timeDesc[count][1], aData.type);
            aData.btn.setEnabled(true);

            //be sure to reset longTime to default
            longTime = -1;
        }
        nullifyTaskRef(taskInd);
    }

    /** Override to take care of setting the task referenece maintained in the calling class to null
     */
    protected abstract void nullifyTaskRef(int taskRef);

    /** Override to update member var for reminder description
     */
    protected abstract void setRemDesc(String aDesc, int aNotType);
}
