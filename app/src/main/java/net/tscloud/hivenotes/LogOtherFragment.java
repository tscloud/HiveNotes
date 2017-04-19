package net.tscloud.hivenotes;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.tscloud.hivenotes.db.HiveNotesLogDO;
import net.tscloud.hivenotes.db.LogEntryOther;
import net.tscloud.hivenotes.db.LogEntryOtherDAO;
import net.tscloud.hivenotes.db.NotificationType;
import net.tscloud.hivenotes.helper.LogMultiSelectDialogData;


/**
 * A LogFragment subclass.
 */
public class LogOtherFragment extends LogFragment {

    public static final String TAG = "LogOtherFragment";

    // DO for this particular Fragment
    private LogEntryOther mLogEntryOther;

    // constants used for Dialogs
    public static final String DIALOG_TAG_EVENTS = "events";

    // Factory method to create a new instance of this fragment using the provided parameters.
    public static LogOtherFragment newInstance(long hiveID, long logEntryDate, long logEntryID) {
        LogOtherFragment fragment = new LogOtherFragment();

        return (LogOtherFragment)setLogFragArgs(fragment, hiveID, logEntryDate, logEntryID);
    }

    public LogOtherFragment() {
        // Required empty public constructor
    }

    // Accessors needed by super class
    @Override
    protected HiveNotesLogDO getLogEntryDO() {
        return mLogEntryOther;
    }

    @Override
    protected HiveNotesLogDO setLogEntryDO(HiveNotesLogDO aDataObj) {
        mLogEntryOther = (LogEntryOther) aDataObj;
        return mLogEntryOther;
    }

    @Override
    protected HiveNotesLogDO makeLogEntryDO() {
        mLogEntryOther = new LogEntryOther();
        return mLogEntryOther;
    }

    @Override
    protected String getDOKey() {
        return LogEntryListActivity.INTENT_LOGENTRY_OTHER_DATA;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // populate dataobject from Bundle
        if (savedInstanceState != null) {
            mLogEntryOther = new LogEntryOther();
            mLogEntryOther.setVisitDate(savedInstanceState.getLong("visitDate"));
            mLogEntryOther.setRequeen(savedInstanceState.getString("requeen"));
        }

        // save off arguments via super method
        saveOffArgs();

        /*
         * call super method to get DO via best means
         */
        // This method blocks on DB operation
        getLogEntry(mListener);

        // This method uses AsyncTask on DB operation
        //getLogEntry(mListener, new LogEntryFeedingDAO(getActivity()), null, null);

        // Callback to Activity to launch a Dialog
        if (mListener != null) {
            String checked = "";
            long reminderMillis = -1;
            String remDesc = null;
            if (mLogEntryOther != null) {
                if (mLogEntryOther.getRequeen() != null) {
                    checked = mLogEntryOther.getRequeen();
                }
                reminderMillis = mLogEntryOther.getRequeenRmndrTime();
                remDesc = mLogEntryOther.getRequeenRmndrDesc();
            }
            /* Get the Activity to launch the Dialog for us
             */
            mListener.onLogLaunchDialog(new LogMultiSelectDialogData(
                    getResources().getString(R.string.events_notes_string),
                    mHiveID,
                    getResources().getStringArray(R.array.events_array),
                    checked,
                    DIALOG_TAG_EVENTS,
                    reminderMillis,
                    //tribal knowledge - don't have notType
                    true,
                    remDesc,
                    //hasOther, hasReminder, multiselect
                    true, true, false));
        }
        else {
            Log.d(TAG, "no Listener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save off values potentially entered from screen
        if (mLogEntryOther != null) {
            outState.putLong("visitDate", mLogEntryOther.getVisitDate());
            outState.putString("requeen", mLogEntryOther.getRequeen());
            outState.putLong("requeenRmndrTime", mLogEntryOther.getRequeenRmndrTime());
            outState.putLong("swarmRmndrTime", mLogEntryOther.getSwarmRmndrTime());
            outState.putLong("splitHiveRmndrTime", mLogEntryOther.getSplitHiveRmndrTime());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected LogEntryOther getLogEntryFromDB(long aKey, long aDate) {
        // read log Entry
        Log.d(TAG, "reading LogEntryOther table - by date");
        LogEntryOtherDAO logEntryOtherDAO = new LogEntryOtherDAO(getActivity());
        LogEntryOther reply = null;

        if (aKey != -1) {
            reply = logEntryOtherDAO.getLogEntryById(aKey);
        }
        else if (aDate != -1) {
            reply = logEntryOtherDAO.getLogEntryByDate(aDate);
        }

        logEntryOtherDAO.close();

        return reply;
    }

    @Override
    public void setDialogData(String[] aResults, long aResultRemTime, String aResultRemDesc,
                              String aTag) {
        //may have to create the DO here - if we're a new entry and Dialog work was done
        // before anything else
        if (mLogEntryOther == null) {
            mLogEntryOther = new LogEntryOther();
        }

        // TODO: do we need to init these values?
        mLogEntryOther.setId(mLogEntryKey);
        mLogEntryOther.setHive(mHiveID);
        mLogEntryOther.setVisitDate(mLogEntryDate);

        switch (aTag){
            case DIALOG_TAG_EVENTS:
                mLogEntryOther.setRequeen(TextUtils.join(",", aResults));
                Log.d(TAG, "onLogLaunchDialog: setRequeen: " +
                        mLogEntryOther.getRequeen());

                mLogEntryOther.setRequeenRmndrTime(aResultRemTime);
                Log.d(TAG, "onLogLaunchDialog: setRequeenRmndrTime: " +
                        mLogEntryOther.getRequeenRmndrTime());

                mLogEntryOther.setRequeenRmndrDesc(aResultRemDesc);
                Log.d(TAG, "onLogLaunchDialog: setRequeenRmndrDesc: " +
                        mLogEntryOther.getRequeenRmndrDesc());

                break;
            default:
                Log.d(TAG, "onLogLaunchDialog: unrecognized Dialog type");
        }

        // This is done in the Other/Event Fragment since there's nothing visual to show
        //  (no override of onCreateView()) so go back to the Activity
        if (mListener != null) {
            mListener.onLogFragmentInteraction(getDOKey(), mLogEntryOther);
        }

    }
}
