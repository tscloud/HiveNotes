package net.tscloud.hivenotes;

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
    private long mHiveID;
    private long mLogEntryGeneralKey;
    private long mLogEntryGeneralDate;
    private HiveNotesLogDO mLogEntryDO;

    // time/date formatters
    private static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
    private static final String TIME_PATTERN = "HH:mm";
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());
    private final Calendar calendar = Calendar.getInstance();

    // abstract methods
    abstract HiveNotesLogDO getLogEntryDO();

    abstract void setLogEntryDO(HiveNotesLogDO dataObj);

    abstract long getLogEntryKey();

    abstract HiveNotesLogDO getLogEntryFromDB(long key);

    // concrete methods
    protected void getLogEntry(PreviousLogDataProvider aListener) {
        Log.d(TAG, "in getLogEntry()");

        // log entry may have something in it either already populated or populated from Bundle
        // if not => 1st check the Activity for previously entered data, if not => potentially read DB
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
                if (getLogEntryKey() != -1) {
                    setLogEntryDO(getLogEntryFromDB(getLogEntryKey()));
                }
            }
        }
    }

    // necessary interfaces
    public interface PreviousLogDataProvider{
            HiveNotesLogDO getPreviousLogData();
    }
}