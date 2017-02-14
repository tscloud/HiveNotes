package net.tscloud.hivenotes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import net.tscloud.hivenotes.db.HiveNotesLogDO;
import net.tscloud.hivenotes.helper.LogEditTextDialogData;
import net.tscloud.hivenotes.helper.LogMultiSelectDialogData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by tscloud on 3/31/16.
 */

public abstract class LogFragment extends Fragment {

    public static final String TAG = "LogFragment";

    // member vars common to all Log Fragments
    protected long mHiveID;
    protected long mLogEntryKey;
    protected long mLogEntryDate;

    // task references - needed to kill tasks on Activity Destroy
    private GetLogData mGetLogDataTask = null;

    // time/date formatters
    protected static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
    protected static final String TIME_PATTERN = "HH:mm";
    protected static final SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());
    protected final Calendar calendar = Calendar.getInstance();

    // abstract methods
    protected abstract HiveNotesLogDO getLogEntryDO();

    protected abstract void setLogEntryDO(HiveNotesLogDO aDataObj);

    protected abstract HiveNotesLogDO getLogEntryFromDB(long aKey, long aDate);

    public abstract void setDialogData(String[] aResults, long aResultRemTime, String aTag);

    // Override this method if you want a LogFragment to do something on Dialog cancel
    public void setDialogDataCancel(String aTag) {
        // NOOP - just re-present fragment
    }

    // concrete static methods
    public static LogFragment setLogFragArgs(LogFragment aFrag, long aHiveID, long aLogEntryDate, long aLogEntryID) {
        Log.d(TAG, "in setLogFragArgs()");

        Bundle args = new Bundle();
        args.putLong(MainActivity.INTENT_HIVE_KEY, aHiveID);
        args.putLong(LogEntryListActivity.INTENT_LOGENTRY_DATE, aLogEntryDate);
        args.putLong(LogEntryListActivity.INTENT_LOGENTRY_KEY, aLogEntryID);
        aFrag.setArguments(args);
        return aFrag;
    }

    // concrete methods
    protected void getLogEntry(LogFragmentActivity aListener) {
        Log.d(TAG, "in getLogEntry()");

        // log entry may have something in it either already populated or populated from Bundle
        // if not => 1st check the Activity for previously entered data, if not => potentially read DB
        //  on id 1st, date 2nd
        if (getLogEntryDO() == null) {
            try {
                setLogEntryDO(aListener.getPreviousLogData());
            }
            catch (ClassCastException e) {
                // Log the exception but continue w/ NO previous log data
                Log.e(TAG, "*** Bad Previous Log Data from Activity ***", e);
                setLogEntryDO(null);
            }
            if (getLogEntryDO() == null) {
                setLogEntryDO(getLogEntryFromDB(mLogEntryKey, mLogEntryDate));
            }
        }

        // If we got a DO => set the Key
        if (getLogEntryDO() != null) {
            mLogEntryKey = getLogEntryDO().getId();
        }
    }

    protected void getLogEntry(LogFragmentActivity aListener, AbstractLogDAO aDOA, List<View> aViewList, Context aCtx) {
        Log.d(TAG, "in getLogEntry()");

        if (getLogEntryDO() == null) {
            try {
                setLogEntryDO(aListener.getPreviousLogData());
            }
            catch (ClassCastException e) {
                // Log the exception but continue w/ NO previous log data
                Log.e(TAG, "*** Bad Previous Log Data from Activity ***", e);
                setLogEntryDO(null);
            }
            if (getLogEntryDO() == null) {
                mGetLogDataTask = new GetLogData(aDOA, aViewList, aCtx);
                mTask.execute();
            }
        }
    }

    protected void saveOffArgs() {
        Log.d(TAG, "in saveOffArgs()");

        if (getArguments() != null) {
            mHiveID = getArguments().getLong(MainActivity.INTENT_HIVE_KEY);
            mLogEntryKey = getArguments().getLong(LogEntryListActivity.INTENT_LOGENTRY_KEY);
            mLogEntryDate = getArguments().getLong(LogEntryListActivity.INTENT_LOGENTRY_DATE);
        }

    }

    @Override
    public void onDestroy() {
        if (mGetLogDataTask != null) {
            mGetLogDataTask.cancel(false);
        }

        super.onDestroy();
    }
    // necessary interfaces
    public interface LogFragmentActivity {
        HiveNotesLogDO getPreviousLogData();
        void onLogLaunchDialog(LogMultiSelectDialogData aData);
        void onLogLaunchDialog(LogEditTextDialogData aData);
    }

    /**
     * Inner Class - Get Log data AsyncTask
     */
    public class GetLogData extends AsyncTask<Void, Void, Void> {

        public static final String TAG = "GetGraphableData";

        private Context mCtx;
        private AbstractLogDAO mDOA;
        private List<View> mViewList;

        private ProgressDialog dialog =
                new ProgressDialog(mCtx);

        public GetLogData(AbstractLogDAO aDOA, List<View> aViewList, aCtx) {
            Log.d(TAG, "GetLogData("+ Thread.currentThread().getId() + ") : constructor");
            mDOA = aDOA;
            mViewList = aViewList;
            mCtx = aCtx;
        }

        @Override
        protected void onPreExecute() {
            // disable all the Views that were passed in...
            if (mViewList != null) {
                for (View v : mViewList) {
                    v.setEnabled(false);
                }
            }
            // ...or throw up a ProgressDialog
            else {
                dialog.setMessage("Waiting...");
                dialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... unused) {
            Log.d(TAG, "GetLogData("+ Thread.currentThread().getId() + ") : doInBackground");

            //pause to simulate work
            //try {
            //    Thread.sleep(2000);
            //}
            //catch (InterruptedException e) {
            //    Log.d(TAG, "GetReminderTimeTask(" + data.taskInd + ":" + Thread.currentThread().getId() +
            //        ") : InterruptedException");
            //}

            HiveNotesLogDO reply = null;

            if (mLogEntryKey != -1) {
                reply = mDAO.getLogEntryById(mLogEntryKey);
            }
            else if (mLogEntryDate != -1) {
                reply = mDAO.getLogEntryByDate(mLogEntryDate);
            }

            mDAO.close();

            setLogEntryDO(reply);

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.d(TAG, "GetLogData("+ Thread.currentThread().getId() + ") : onPostExecute");

            // If we got a DO => set the Key
            if (getLogEntryDO() != null) {
                mLogEntryKey = getLogEntryDO().getId();
            }

            // disable all the Views that were passed in...
            if (mViewList != null) {
                for (View v : mViewList) {
                    v.setEnabled(true);
                }
            }

            // ...or dismiss a ProgressDialog
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            // all we need to do is nullify the Task reference
            mGetLogDataTask = null;
        }
    }
}