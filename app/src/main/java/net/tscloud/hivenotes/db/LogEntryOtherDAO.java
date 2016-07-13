package net.tscloud.hivenotes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by tscloud on 11/16/15.
 */
public class LogEntryOtherDAO extends AbstactDAO {

    public static final String TAG = "LogEntryOtherDAO";

    // Database table columns
    // columns of the Profile table
    /* Reminders children of Hive */
    public static final String TABLE_LOGENTRYOTHER = "LogEntryOther";
    public static final String COLUMN_LOGENTRYOTHER_ID = "_id";
    public static final String COLUMN_LOGENTRYOTHER_HIVE = "hive";
    public static final String COLUMN_LOGENTRYOTHER_VISIT_DATE = "visit_date";
    public static final String COLUMN_LOGENTRYOTHER_REQUEEN = "requeen";
    //public static final String COLUMN_LOGENTRYOTHER_REQUEEN_RMNDR = "requeen_rmndr";
    //public static final String COLUMN_LOGENTRYOTHER_SWARM_RMNDR = "swarm_rmndr";
    //public static final String COLUMN_LOGENTRYOTHER_SPLIT_HIVE_RMNDR = "split_hive_rmndr";

    // Database fields
    private String[] mAllColumns = {COLUMN_LOGENTRYOTHER_ID, COLUMN_LOGENTRYOTHER_HIVE,
            COLUMN_LOGENTRYOTHER_VISIT_DATE, COLUMN_LOGENTRYOTHER_REQUEEN};

    public LogEntryOtherDAO(Context context) {
        super(context);
    }

    // --DB access methods--

    public LogEntryOther createLogEntry(long hive, long visitDate, String requeen) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRYOTHER_HIVE, hive);
        values.put(COLUMN_LOGENTRYOTHER_VISIT_DATE, visitDate);
        values.put(COLUMN_LOGENTRYOTHER_REQUEEN, requeen);
        long insertId = mDatabase.insert(TABLE_LOGENTRYOTHER, null, values);

        LogEntryOther newLogEntryOther = null;
        if (insertId >= 0) {
            Cursor cursor = mDatabase.query(TABLE_LOGENTRYOTHER, mAllColumns,
                    COLUMN_LOGENTRYOTHER_ID + " = " + insertId, null, null, null, null);
            if (cursor.moveToFirst()) {
                newLogEntryOther = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return newLogEntryOther;
    }

    public LogEntryOther createLogEntry(LogEntryOther aDO) {
        return createLogEntry(aDO.getHive(), aDO.getVisitDate(), aDO.getRequeen());
    }

    public LogEntryOther updateLogEntry(long id, long hive, long visitDate, String requeen) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRYOTHER_HIVE, hive);
        values.put(COLUMN_LOGENTRYOTHER_VISIT_DATE, visitDate);
        values.put(COLUMN_LOGENTRYOTHER_REQUEEN, requeen);
        int rowsUpdated = mDatabase.update(TABLE_LOGENTRYOTHER, values,
                COLUMN_LOGENTRYOTHER_ID + "=" + id, null);

        LogEntryOther updatedLogEntryOther = null;
        if (rowsUpdated > 0) {
            Cursor cursor = mDatabase.query(TABLE_LOGENTRYOTHER, mAllColumns,
                    COLUMN_LOGENTRYOTHER_ID + " = " + id, null, null, null, null);
            if (cursor.moveToFirst()) {
                updatedLogEntryOther = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return updatedLogEntryOther;
    }

    public LogEntryOther updateLogEntry(LogEntryOther aDO) {
        return updateLogEntry(aDO.getId(), aDO.getHive(), aDO.getVisitDate(), aDO.getRequeen());
    }

    public void deleteLogEntry(LogEntryOther logEntryOther) {
        long id = logEntryOther.getId();
        mDatabase.delete(TABLE_LOGENTRYOTHER, COLUMN_LOGENTRYOTHER_ID + " = " + id, null);
    }

    public LogEntryOther getLogEntryById(long id){
        Cursor cursor = mDatabase.query(TABLE_LOGENTRYOTHER, mAllColumns,
                COLUMN_LOGENTRYOTHER_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null);

        LogEntryOther retrievedLogEntryOther = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                retrievedLogEntryOther = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return retrievedLogEntryOther;
    }

    public LogEntryOther getLogEntryByDate(long date){
        Cursor cursor = mDatabase.query(TABLE_LOGENTRYOTHER, mAllColumns,
                COLUMN_LOGENTRYOTHER_VISIT_DATE + " = ?",
                new String[] { String.valueOf(date) }, null, null, null);

        LogEntryOther retrievedLogEntryOther = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                retrievedLogEntryOther = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return retrievedLogEntryOther;
    }

    protected LogEntryOther cursorToLogEntry(Cursor cursor) {
        LogEntryOther logEntryOther = new LogEntryOther();
        logEntryOther.setId(cursor.getLong(0));
        logEntryOther.setHive(cursor.getLong(1));
        logEntryOther.setVisitDate(cursor.getLong(2));
        logEntryOther.setRequeen(cursor.getString(3));

        return logEntryOther;
    }
}
