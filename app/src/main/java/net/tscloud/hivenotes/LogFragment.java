package net.tscloud.hivenotes;

public abstract class LogFragment extends Fragment {

    private abstract HiveNotesLogDO getLogEntryDO();
    private abstract void setLogEntryDO(HiveNotesLogDO do);

    private abstract long getLogEntryKey();

    private abstract HiveNotesLogDO getLogEntryFromDB(long key);

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