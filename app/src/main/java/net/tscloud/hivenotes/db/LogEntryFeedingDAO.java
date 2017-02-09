package net.tscloud.hivenotes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by tscloud on 11/10/15.
 */
public class LogEntryFeedingDAO extends AbstactDAO {

    public static final String TAG = "LogEntryFeedingDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_LOGENTRYFEEDING = "LogEntryFeeding";
    public static final String COLUMN_LOGENTRYFEEDING_ID = "_id";
    public static final String COLUMN_LOGENTRYFEEDING_HIVE = "hive";
    public static final String COLUMN_LOGENTRYFEEDING_VISIT_DATE = "visit_date";
    public static final String COLUMN_LOGENTRYFEEDING_FEEDING_TYPES = "feeding_types";

    // Database fields
    private String[] mAllColumns = {COLUMN_LOGENTRYFEEDING_ID, COLUMN_LOGENTRYFEEDING_HIVE,
            COLUMN_LOGENTRYFEEDING_VISIT_DATE, COLUMN_LOGENTRYFEEDING_FEEDING_TYPES};

    public LogEntryFeedingDAO(Context context) {
        super(context);
    }

    // --DB access methods--

    public LogEntryFeeding createLogEntry(long hive, long visitDate, String feedingTypes) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRYFEEDING_HIVE, hive);
        values.put(COLUMN_LOGENTRYFEEDING_VISIT_DATE, visitDate);
        values.put(COLUMN_LOGENTRYFEEDING_FEEDING_TYPES, feedingTypes);
        long insertId = mDatabase.insert(TABLE_LOGENTRYFEEDING, null, values);

        LogEntryFeeding newLogEntryFeeding = null;
        if (insertId >= 0) {
            Cursor cursor = mDatabase.query(TABLE_LOGENTRYFEEDING, mAllColumns,
                    COLUMN_LOGENTRYFEEDING_ID + " = " + insertId, null, null, null, null);
            if (cursor.moveToFirst()) {
                newLogEntryFeeding = cursorToLogEntry(cursor);
            }
            cursor.close();
        }
        return newLogEntryFeeding;
    }

    public LogEntryFeeding createLogEntry(LogEntryFeeding aDO) {
        return createLogEntry(aDO.getHive(), aDO.getVisitDate(), aDO.getFeedingTypes());
    }

    public LogEntryFeeding updateLogEntry(long id, long hive, long visitDate, String feedingTypes) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRYFEEDING_HIVE, hive);
        values.put(COLUMN_LOGENTRYFEEDING_VISIT_DATE, visitDate);
        values.put(COLUMN_LOGENTRYFEEDING_FEEDING_TYPES, feedingTypes);
        int rowsUpdated = mDatabase.update(TABLE_LOGENTRYFEEDING, values,
                COLUMN_LOGENTRYFEEDING_ID + "=" + id, null);

        LogEntryFeeding updatedLogEntryFeeding = null;
        if (rowsUpdated > 0) {
            Cursor cursor = mDatabase.query(TABLE_LOGENTRYFEEDING, mAllColumns,
                    COLUMN_LOGENTRYFEEDING_ID + " = " + id, null, null, null, null);
            if (cursor.moveToFirst()) {
                updatedLogEntryFeeding = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return updatedLogEntryFeeding;
    }

    public LogEntryFeeding updateLogEntry(LogEntryFeeding aDO) {
        return updateLogEntry(aDO.getId(), aDO.getHive(), aDO.getVisitDate(), aDO.getFeedingTypes());
    }

    public void deleteLogEntry(LogEntryFeeding logEntryFeeding) {
        long id = logEntryFeeding.getId();
        mDatabase.delete(TABLE_LOGENTRYFEEDING, COLUMN_LOGENTRYFEEDING_ID + " = " + id, null);
    }

    public LogEntryFeeding getLogEntryById(long id){
        Cursor cursor = mDatabase.query(TABLE_LOGENTRYFEEDING, mAllColumns,
                COLUMN_LOGENTRYFEEDING_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        LogEntryFeeding retrievedLogEntry = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                retrievedLogEntry = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return retrievedLogEntry;
    }

    public LogEntryFeeding getLogEntryByDate(long date){
        Cursor cursor = mDatabase.query(TABLE_LOGENTRYFEEDING, mAllColumns,
                COLUMN_LOGENTRYFEEDING_VISIT_DATE + " = ?",
                new String[]{String.valueOf(date)}, null, null, null);

        LogEntryFeeding retrievedLogEntry = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                retrievedLogEntry = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return retrievedLogEntry;
    }

    protected LogEntryFeeding cursorToLogEntry(Cursor cursor) {
        LogEntryFeeding logEntryFeeding = new LogEntryFeeding();
        logEntryFeeding.setId(cursor.getLong(0));
        logEntryFeeding.setHive(cursor.getLong(1));
        logEntryFeeding.setVisitDate(cursor.getLong(2));
        logEntryFeeding.setFeedingTypes(cursor.getString(3));

        return logEntryFeeding;
    }
}
