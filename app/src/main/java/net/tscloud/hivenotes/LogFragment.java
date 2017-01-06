package net.tscloud.hivenotes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import net.tscloud.hivenotes.db.HiveNotesLogDO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public abstract class LogFragment extends Fragment {

    public static final String TAG = "LogFragment";

    // member vars common to all Log Fragments
    protected long mHiveID;
    protected long mLogEntryKey;
    protected long mLogEntryDate;

    // time/date formatters
    protected static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
    protected static final String TIME_PATTERN = "HH:mm";
    protected static final SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());
    protected final Calendar calendar = Calendar.getInstance();

    // constants used for Dialogs
    protected static final String DIALOG_TAG_PESTS = "pests";

    // abstract methods
    protected abstract HiveNotesLogDO getLogEntryDO();

    protected abstract void setLogEntryDO(HiveNotesLogDO aDataObj);

    protected abstract HiveNotesLogDO getLogEntryFromDB(long aKey, long aDate);

    public abstract void setDialogData(String[] aResults, String aTag);

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

    protected void saveOffArgs() {
        Log.d(TAG, "in saveOffArgs()");

        if (getArguments() != null) {
            mHiveID = getArguments().getLong(MainActivity.INTENT_HIVE_KEY);
            mLogEntryKey = getArguments().getLong(LogEntryListActivity.INTENT_LOGENTRY_KEY);
            mLogEntryDate = getArguments().getLong(LogEntryListActivity.INTENT_LOGENTRY_DATE);
        }

    }

    // necessary interfaces
    public interface LogFragmentActivity {
        HiveNotesLogDO getPreviousLogData();
        void onLogLaunchDialog(String aTitle, String[] aElems, String aCheckedSet, String aTag);
    }
}