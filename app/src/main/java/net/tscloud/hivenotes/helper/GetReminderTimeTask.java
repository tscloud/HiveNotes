package net.tscloud.hivenotes.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by tscloud on 4/28/16.
 */
abstract class GetReminderTimeTask extends AsyncTask<Void, Void, String[]> {

    public static final String TAG = "GetReminderTimeTask";

    private GetReminderTimeTaskData data;
    private Context ctx;

    GetReminderTimeTask(GetReminderTimeTaskData aData, Context aCtx) {
        this.data = aData;
        this.ctx = aCtx;
        Log.d(TAG, "GetReminderTimeTask(" + data.taskInd + ":" + Thread.currentThread().getId() +
            ") : constructor");
    }

    @Override
    protected String[] doInBackground(Void... unused) {
        Log.d(TAG, "GetReminderTimeTask(" + data.taskInd + ":" + Thread.currentThread().getId() +
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
        String[] reply = HiveCalendar.getReminderTimeAndDesc(ctx, data.type, data.hive);
        Log.d(TAG, "GetReminderTimeTask(" + data.taskInd + ":" + Thread.currentThread().getId() +
            ") : doInBackground : reminderMillis: " + reply[0] + " : reminderDesc: " + reply[1]);

        return reply;
    }

    @Override
    protected void onPostExecute(String[] time) {
        Log.d(TAG, "GetReminderTimeTask(" + data.taskInd + ":" + Thread.currentThread().getId() +
            ") : onPostExecute");

        long longTime = -1;

        try {
            longTime = Long.parseLong(time[0]);
        }
        catch (NumberFormatException e) {
            // NOOP - keep value as -1
        }

        if (longTime != -1) {
            data.cal.setTimeInMillis(longTime);
            String fDate = data.dateFormat.format(data.cal.getTime());
            String fTime = data.timeFormat.format(data.cal.getTime());
            String fDateTime = fDate + ' ' + fTime;
            Log.d(TAG, "GetReminderTimeTask(" + data.taskInd + ":" + Thread.currentThread().getId() +
                ") : onPostExecute : fDateTime: " + fDateTime);

            data.txt.setTag(longTime);
            data.txt.setText(fDateTime);
        }

        // call abstract method to set reminder description
        setRemDesc(time[1]);

        data.btn.setEnabled(true);

        nullifyTaskRef(data.taskInd);
    }

    /** Override to take care of setting the task referenece maintained in the calling class to null
     */
    protected abstract void nullifyTaskRef(int taskRef);

    /** Override to update member var for reminder description
     */
    protected abstract void setRemDesc(String aDesc);
}
