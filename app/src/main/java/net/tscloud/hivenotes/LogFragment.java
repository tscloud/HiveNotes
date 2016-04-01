package net.tscloud.hivenotes;

import android.support.v4.app.Fragment;
import android.util.Log;

import net.tscloud.hivenotes.db.HiveNotesLogDO;

public abstract class LogFragment extends Fragment {

    public static final String TAG = "LogFragment";

    abstract HiveNotesLogDO getLogEntryDO();
    abstract void setLogEntryDO(HiveNotesLogDO dataObj);

    abstract long getLogEntryKey();

    abstract HiveNotesLogDO getLogEntryFromDB(long key);

    protected void getLogEntry(PreviousLogDataProvider aListener) {
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

    public interface PreviousLogDataProvider{
            HiveNotesLogDO getPreviousLogData();
    }
}