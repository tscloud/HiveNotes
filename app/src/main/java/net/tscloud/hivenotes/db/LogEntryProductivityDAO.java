package net.tscloud.hivenotes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by tscloud on 10/17/15.
 */
public class LogEntryProductivityDAO extends GraphableDAO {

    public static final String TAG = "LogEntryGeneralDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_LOGENTRYPRODUCTIVITY = "LogEntryProductivity";
    public static final String COLUMN_LOGENTRYPRODUCTIVITY_ID = "_id";
    public static final String COLUMN_LOGENTRYPRODUCTIVITY_HIVE = "hive";
    public static final String COLUMN_LOGENTRYPRODUCTIVITY_VISIT_DATE = "visit_date";
    public static final String COLUMN_LOGENTRYPRODUCTIVITY_EXTRACTED_HONEY = "extracted_honey";
    public static final String COLUMN_LOGENTRYPRODUCTIVITY_POLLEN_COLLECTED = "pollen_collected";
    public static final String COLUMN_LOGENTRYPRODUCTIVITY_BEESWAX_COLLECTED = "beeswax_collected";

    // Database fields
    private String[] mAllColumns = { COLUMN_LOGENTRYPRODUCTIVITY_ID, COLUMN_LOGENTRYPRODUCTIVITY_HIVE,
            COLUMN_LOGENTRYPRODUCTIVITY_VISIT_DATE, COLUMN_LOGENTRYPRODUCTIVITY_EXTRACTED_HONEY,
            COLUMN_LOGENTRYPRODUCTIVITY_POLLEN_COLLECTED, COLUMN_LOGENTRYPRODUCTIVITY_BEESWAX_COLLECTED };

    // Columns that require special processing
    private String[] specialCols = {};

    // --constructor--
    public LogEntryProductivityDAO(Context context) {
        super(context);
    }

    // --implement abstract--
    @Override
    protected String getTable() {
        return TABLE_LOGENTRYPRODUCTIVITY;
    }

    @Override
    protected String getColGraphKey() {
        return COLUMN_LOGENTRYPRODUCTIVITY_HIVE;
    }

    @Override
    protected String getColGraphDate() {
        return COLUMN_LOGENTRYPRODUCTIVITY_VISIT_DATE;
    }

    @Override
    protected String[] getSpecialCols() {
        return specialCols;
    }

    @Override
    protected Double processSpecialCol(Cursor aCur) {
        // there are no special cols
        return null;
    }

    // --DB access methods--

    public LogEntryProductivity createLogEntry(long hive, long visitDate, float extractedHoney,
                                               float pollenCollected, float beeswaxCollected) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_HIVE, hive);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_VISIT_DATE, visitDate);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_EXTRACTED_HONEY,extractedHoney);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_POLLEN_COLLECTED, pollenCollected);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_BEESWAX_COLLECTED, beeswaxCollected);
        long insertId = mDatabase.insert(TABLE_LOGENTRYPRODUCTIVITY, null, values);

        LogEntryProductivity newLogEntryProductivity = null;
        if (insertId >= 0) {
            Cursor cursor = mDatabase.query(TABLE_LOGENTRYPRODUCTIVITY, mAllColumns,
                    COLUMN_LOGENTRYPRODUCTIVITY_ID + " = " + insertId, null, null, null, null);
            if (cursor.moveToFirst()) {
                newLogEntryProductivity = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return newLogEntryProductivity;
    }

    public LogEntryProductivity createLogEntry(LogEntryProductivity aDO) {
        return createLogEntry(aDO.getHive(), aDO.getVisitDate(), aDO.getExtractedHoney(),
                aDO.getPollenCollected(), aDO.getBeeswaxCollected());
    }

    public LogEntryProductivity updateLogEntry(long id, long hive, long visitDate, float extractedHoney,
                                               float pollenColected, float beeswaxColected) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_HIVE, hive);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_VISIT_DATE, visitDate);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_EXTRACTED_HONEY,extractedHoney);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_POLLEN_COLLECTED, pollenColected);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_BEESWAX_COLLECTED, beeswaxColected);
        int rowsUpdated = mDatabase.update(TABLE_LOGENTRYPRODUCTIVITY, values,
                COLUMN_LOGENTRYPRODUCTIVITY_ID + "=" + id, null);

        LogEntryProductivity updatedLogEntryProductivity = null;
        if (rowsUpdated > 0) {
            Cursor cursor = mDatabase.query(TABLE_LOGENTRYPRODUCTIVITY, mAllColumns,
                    COLUMN_LOGENTRYPRODUCTIVITY_ID + " = " + id, null, null, null, null);
            if (cursor.moveToFirst()) {
                updatedLogEntryProductivity = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return updatedLogEntryProductivity;
    }

    public LogEntryProductivity updateLogEntry(LogEntryProductivity aDO) {
        return updateLogEntry(aDO.getId(), aDO.getHive(), aDO.getVisitDate(), aDO.getExtractedHoney(),
                aDO.getPollenCollected(), aDO.getBeeswaxCollected());
    }

    public void deleteLogEntry(LogEntryProductivity logEntryProductivity) {
        long id = logEntryProductivity.getId();
        mDatabase.delete(TABLE_LOGENTRYPRODUCTIVITY, COLUMN_LOGENTRYPRODUCTIVITY_ID + " = " + id, null);
    }

    public LogEntryProductivity getLogEntryById(long id){
        Cursor cursor = mDatabase.query(TABLE_LOGENTRYPRODUCTIVITY, mAllColumns,
                COLUMN_LOGENTRYPRODUCTIVITY_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        LogEntryProductivity retrievedLogEntryProductivity = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                retrievedLogEntryProductivity = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return retrievedLogEntryProductivity;
    }

    public LogEntryProductivity getLogEntryByDate(long date){
        Cursor cursor = mDatabase.query(TABLE_LOGENTRYPRODUCTIVITY, mAllColumns,
                COLUMN_LOGENTRYPRODUCTIVITY_VISIT_DATE + " = ?",
                new String[]{String.valueOf(date)}, null, null, null);

        LogEntryProductivity retrievedLogEntryProductivity = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                retrievedLogEntryProductivity = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return retrievedLogEntryProductivity;
    }

    protected LogEntryProductivity cursorToLogEntry(Cursor cursor) {
        LogEntryProductivity logEntryProductivity = new LogEntryProductivity();
        logEntryProductivity.setId(cursor.getLong(0));
        logEntryProductivity.setHive(cursor.getLong(1));
        logEntryProductivity.setVisitDate(cursor.getLong(2));
        logEntryProductivity.setExtractedHoney(cursor.getFloat(3));
        logEntryProductivity.setPollenCollected(cursor.getFloat(4));
        logEntryProductivity.setBeeswaxCollected(cursor.getFloat(5));

        return logEntryProductivity;
    }

};

