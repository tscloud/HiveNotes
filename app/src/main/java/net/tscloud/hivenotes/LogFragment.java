package net.tscloud.hivenotes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import net.tscloud.hivenotes.db.AbstractLogDAO;
import net.tscloud.hivenotes.db.HiveNotesLogDO;
import net.tscloud.hivenotes.helper.LogEditTextDialogData;
import net.tscloud.hivenotes.helper.LogMultiSelectDialogData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by tscloud on 3/31/16.
 */

public abstract class LogFragment extends Fragment {

    public static final String TAG = "LogFragment";

    // reference to Activity that should have started me
    protected LogFragmentActivity mListener;

    // member vars common to all Log Fragments
    protected long mHiveID;
    protected long mLogEntryKey;
    protected long mLogEntryDate;

    // task references - needed to kill tasks on Activity Destroy
    private GetLogData mGetLogDataTask = null;

    // time/date formatters
    protected static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG,
            Locale.getDefault());
    protected static final String TIME_PATTERN = "HH:mm";
    protected static final SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_PATTERN,
            Locale.getDefault());
    protected final Calendar calendar = Calendar.getInstance();

    // abstract methods
    protected abstract HiveNotesLogDO getLogEntryDO();

    protected abstract HiveNotesLogDO setLogEntryDO(HiveNotesLogDO aDataObj);

    protected abstract HiveNotesLogDO makeLogEntryDO();

    protected abstract HiveNotesLogDO getLogEntryFromDB(long aKey, long aDate);

    protected abstract void setDialogData(String[] aResults, long aResultRemTime, String aTag);

    protected abstract String getDOKey();

    // concrete static methods
    public static LogFragment setLogFragArgs(LogFragment aFrag, long aHiveID, long aLogEntryDate,
                                             long aLogEntryID) {
        Log.d(TAG, "in setLogFragArgs()");

        Bundle args = new Bundle();
        args.putLong(MainActivity.INTENT_HIVE_KEY, aHiveID);
        args.putLong(LogEntryListActivity.INTENT_LOGENTRY_DATE, aLogEntryDate);
        args.putLong(LogEntryListActivity.INTENT_LOGENTRY_KEY, aLogEntryID);
        aFrag.setArguments(args);
        return aFrag;
    }

    // concrete methods
    protected void setLogEntryDOKeys(HiveNotesLogDO aDataObj) {
        Log.d(TAG, "in setLogEntryDOKeys()");

        // if we're given an honest to goodness DO => set the keys to the values
        //  contained therein
        if (aDataObj != null) {
            mHiveID = aDataObj.getHive();
            mLogEntryKey = aDataObj.getId();
            mLogEntryDate = aDataObj.getVisitDate();
        }

        // call abstract method to set DO in each individual Fragment
        setLogEntryDO(aDataObj);
    }

    protected void getLogEntry(LogFragmentActivity aListener) {
        Log.d(TAG, "in getLogEntry()");

        // log entry may have something in it either already populated or populated from Bundle
        // if not => 1st check the Activity for previously entered data, if not => potentially
        //  read DB on id 1st, date 2nd
        if (getLogEntryDO() == null) {
            try {
                setLogEntryDOKeys(aListener.getPreviousLogData());
            }
            catch (ClassCastException e) {
                // Log the exception but continue w/ NO previous log data
                Log.e(TAG, "*** Bad Previous Log Data from Activity ***", e);
                setLogEntryDOKeys(null);
            }
            if (getLogEntryDO() == null) {
                setLogEntryDOKeys(getLogEntryFromDB(mLogEntryKey, mLogEntryDate));
            }
        }
    }

    protected void getLogEntry(LogFragmentActivity aListener, AbstractLogDAO aDOA,
                               List<View> aViewList, Context aCtx) {
        Log.d(TAG, "in getLogEntry()");

        // essentially the same as the above method but read DB in an AsyncTask
        if (getLogEntryDO() == null) {
            try {
                setLogEntryDOKeys(aListener.getPreviousLogData());
            }
            catch (ClassCastException e) {
                // Log the exception but continue w/ NO previous log data
                Log.e(TAG, "*** Bad Previous Log Data from Activity ***", e);
                setLogEntryDOKeys(null);
            }
            if (getLogEntryDO() == null) {
                mGetLogDataTask = new GetLogData(aDOA, aViewList, aCtx);
                mGetLogDataTask.execute();
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

    public boolean onFragmentSave() {
        // get log entry data and put to DB
        Log.d(TAG, "about to persist logentry");
        boolean reply = false;

        if (getLogEntryDO() == null) {
            makeLogEntryDO();
        }

        getLogEntryDO().setId(mLogEntryKey);
        getLogEntryDO().setHive(mHiveID);
        getLogEntryDO().setVisitDate(mLogEntryDate);

        if (mListener != null) {
            mListener.onLogFragmentInteraction(getDOKey(), getLogEntryDO());
            reply = true;
        }

        return reply;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (LogFragmentActivity)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LogFragmentActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        void onLogFragmentInteraction(String aDOKey, HiveNotesLogDO aLogEntryDO);
    }

    /**
     * Inner Class - Get Log data AsyncTask
     */
    public class GetLogData extends AsyncTask<Void, Void, Void> {

        public static final String TAG = "GetLogData";

        private Context mCtx; //presence or absence will determine decision to throw up Dialog
        private AbstractLogDAO mDAO;
        private List<View> mViewList;

        private ProgressDialog dialog = null;

        GetLogData(AbstractLogDAO aDAO, List<View> aViewList, Context aCtx) {
            Log.d(TAG, "GetLogData("+ Thread.currentThread().getId() + ") : constructor");
            mDAO = aDAO;
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
            // ...or MAYBE throw up a ProgressDialog
            else if (mCtx != null) {
                dialog = new ProgressDialog(mCtx);
                dialog.setMessage("Waiting...");
                Log.d(TAG, "about to dialog.show()");
                dialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... unused) {
            Log.d(TAG, "GetLogData("+ Thread.currentThread().getId() + ") : doInBackground");

            //pause to simulate work
            /*
            boolean doSleep = false;
            if (doSleep) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Log.d(TAG, "GetReminderTimeTask(" + mGetLogDataTask + ":" + Thread.currentThread().getId() +
                            ") : InterruptedException");
                }
            }
            */

            HiveNotesLogDO reply = null;

            if (mLogEntryKey != -1) {
                reply = mDAO.getLogEntryById(mLogEntryKey);
            }
            else if (mLogEntryDate != -1) {
                reply = mDAO.getLogEntryByDate(mLogEntryDate);
            }

            mDAO.close();

            setLogEntryDOKeys(reply);

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.d(TAG, "GetLogData("+ Thread.currentThread().getId() + ") : onPostExecute");

            // disable all the Views that were passed in...
            if (mViewList != null) {
                for (View v : mViewList) {
                    v.setEnabled(true);
                }
            }

            // ...or MAYBE dismiss a ProgressDialog
            if ((dialog != null) && (dialog.isShowing())) {
                Log.d(TAG, "about to dialog.dismiss()");
                dialog.dismiss();
            }

            // all we need to do is nullify the Task reference
            mGetLogDataTask = null;
        }
    }
}