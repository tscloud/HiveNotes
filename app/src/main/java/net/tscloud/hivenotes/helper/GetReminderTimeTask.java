package net.tscloud.hivenotes.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by tscloud on 4/28/16.
 */
public abstract class GetReminderTimeTask extends AsyncTask<Void, Void, Long> {

    public static final String TAG = "GetReminderTimeTask";

    private GetReminderTimeTaskData data;
    private Context ctx;

    public GetReminderTimeTask(GetReminderTimeTaskData aData, Context aCtx) {
        this.data = aData;
        this.ctx = aCtx;
        Log.d(TAG, "GetReminderTimeTask(" + data.taskInd + ":" + Thread.currentThread().getId() +
            ") : constructor");
    }

    @Override
    protected Long doInBackground(Void... unused) {
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
        long reminderMillis = HiveCalendar.getReminderTime(ctx, data.type, data.hive);
        Log.d(TAG, "GetReminderTimeTask(" + data.taskInd + ":" + Thread.currentThread().getId() +
            ") : doInBackground : reminderMillis: " + reminderMillis);

        return reminderMillis;
    }

    @Override
    protected void onPostExecute(Long time) {
        Log.d(TAG, "GetReminderTimeTask(" + data.taskInd + ":" + Thread.currentThread().getId() +
            ") : onPostExecute");
        if (time != -1) {
            data.cal.setTimeInMillis(time);
            String fDate = data.dateFormat.format(data.cal.getTime());
            String fTime = data.timeFormat.format(data.cal.getTime());
            String fDateTime = fDate + ' ' + fTime;
            Log.d(TAG, "GetReminderTimeTask(" + data.taskInd + ":" + Thread.currentThread().getId() +
                ") : onPostExecute : fDateTime: " + fDateTime);

            data.txt.setTag(time);
            data.txt.setText(fDateTime);
        }

        data.btn.setEnabled(true);

        nullifyTaskRef(data.taskInd);
    }

    /** Override to take care of setting the task referenece maintained in the calling class to null
     */
    protected abstract void nullifyTaskRef(int taskRef);

}
