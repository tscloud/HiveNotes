package net.tscloud.hivenotes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by tscloud on 11/3/15.
 */
public class LogEntryHiveHealthDAO extends AbstactLogDAO {

    public static final String TAG = "LogEntryHiveHealthDAO";

    // Database table columns
    // columns of the Profile table
    /* Reminders children of Hive */
    public static final String TABLE_LOGENTRYPESTMGMT = "LogEntryHiveHealth";
    public static final String COLUMN_LOGENTRYPESTMGMT_ID = "_id";
    public static final String COLUMN_LOGENTRYPESTMGMT_HIVE = "hive";
    public static final String COLUMN_LOGENTRYPESTMGMT_VISIT_DATE = "visit_date";
    public static final String COLUMN_LOGENTRYPESTMGMT_PESTS_DETECTED = "pests_detected";
    public static final String COLUMN_LOGENTRYPESTMGMT_DISEASE_DETECTED = "disease_detected";
    public static final String COLUMN_LOGENTRYPESTMGMT_VARROA_TREATMENT = "varroa_treatment";

    // Database fields
    private String[] mAllColumns = { COLUMN_LOGENTRYPESTMGMT_ID, COLUMN_LOGENTRYPESTMGMT_HIVE,
            COLUMN_LOGENTRYPESTMGMT_VISIT_DATE, COLUMN_LOGENTRYPESTMGMT_PESTS_DETECTED,
            COLUMN_LOGENTRYPESTMGMT_DISEASE_DETECTED, COLUMN_LOGENTRYPESTMGMT_VARROA_TREATMENT };

    public LogEntryHiveHealthDAO(Context context) {
        super(context);
    }

    // --DB access methods--

    public LogEntryHiveHealth createLogEntry(long hive, long visitDate, String pestsDetected,
                                             String diseaseDetected, String varroaTreatment) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRYPESTMGMT_HIVE, hive);
        values.put(COLUMN_LOGENTRYPESTMGMT_VISIT_DATE, visitDate);
        values.put(COLUMN_LOGENTRYPESTMGMT_PESTS_DETECTED, pestsDetected);
        values.put(COLUMN_LOGENTRYPESTMGMT_DISEASE_DETECTED, diseaseDetected);
        values.put(COLUMN_LOGENTRYPESTMGMT_VARROA_TREATMENT, varroaTreatment);
        long insertId = mDatabase.insert(TABLE_LOGENTRYPESTMGMT, null, values);

        LogEntryHiveHealth newLogEntryHiveHealth = null;
        if (insertId >= 0) {
            Cursor cursor = mDatabase.query(TABLE_LOGENTRYPESTMGMT, mAllColumns,
                    COLUMN_LOGENTRYPESTMGMT_ID + " = " + insertId, null, null, null, null);
            if (cursor.moveToFirst()) {
                newLogEntryHiveHealth = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return newLogEntryHiveHealth;
    }

    public LogEntryHiveHealth createLogEntry(LogEntryHiveHealth aDO) {
        return createLogEntry(aDO.getHive(), aDO.getVisitDate(), aDO.getPestsDetected(),
                aDO.getDiseaseDetected(), aDO.getVarroaTreatment());
    }

    public LogEntryHiveHealth updateLogEntry(long id, long hive, long visitDate, String pestsDetected,
                                             String diseaseDetected, String varroaTreatment) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRYPESTMGMT_HIVE, hive);
        values.put(COLUMN_LOGENTRYPESTMGMT_VISIT_DATE, visitDate);
        values.put(COLUMN_LOGENTRYPESTMGMT_PESTS_DETECTED, pestsDetected);
        values.put(COLUMN_LOGENTRYPESTMGMT_DISEASE_DETECTED, diseaseDetected);
        values.put(COLUMN_LOGENTRYPESTMGMT_VARROA_TREATMENT, varroaTreatment);
        int rowsUpdated = mDatabase.update(TABLE_LOGENTRYPESTMGMT, values,
                COLUMN_LOGENTRYPESTMGMT_ID + "=" + id, null);

        LogEntryHiveHealth updatedLogEntryHiveHealth = null;
        if (rowsUpdated > 0) {
            Cursor cursor = mDatabase.query(TABLE_LOGENTRYPESTMGMT, mAllColumns,
                    COLUMN_LOGENTRYPESTMGMT_ID + " = " + id, null, null, null, null);
            if (cursor.moveToFirst()) {
                updatedLogEntryHiveHealth = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return updatedLogEntryHiveHealth;
    }

    public LogEntryHiveHealth updateLogEntry(LogEntryHiveHealth aDO) {
        return updateLogEntry(aDO.getId(), aDO.getHive(), aDO.getVisitDate(), aDO.getPestsDetected(),
                aDO.getDiseaseDetected(), aDO.getVarroaTreatment());
    }

    public void deleteLogEntry(LogEntryHiveHealth logEntryHiveHealth) {
        long id = logEntryHiveHealth.getId();
        mDatabase.delete(TABLE_LOGENTRYPESTMGMT, COLUMN_LOGENTRYPESTMGMT_ID + " = " + id, null);
    }

    public LogEntryHiveHealth getLogEntryById(long id) {
        Cursor cursor = mDatabase.query(TABLE_LOGENTRYPESTMGMT, mAllColumns,
                COLUMN_LOGENTRYPESTMGMT_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        LogEntryHiveHealth retrievedLog = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                retrievedLog = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return retrievedLog;
    }

    public LogEntryHiveHealth getLogEntryByDate(long date) {
        Cursor cursor = mDatabase.query(TABLE_LOGENTRYPESTMGMT, mAllColumns,
                COLUMN_LOGENTRYPESTMGMT_VISIT_DATE + " = ?",
                new String[]{String.valueOf(date)}, null, null, null);

        LogEntryHiveHealth retrievedLog = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                retrievedLog = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return retrievedLog;
    }

    protected LogEntryHiveHealth cursorToLogEntry(Cursor cursor) {
        LogEntryHiveHealth logEntryHiveHealth = new LogEntryHiveHealth();
        logEntryHiveHealth.setId(cursor.getLong(0));
        logEntryHiveHealth.setHive(cursor.getLong(1));
        logEntryHiveHealth.setVisitDate(cursor.getLong(2));
        logEntryHiveHealth.setPestsDetected(cursor.getString(3));
        logEntryHiveHealth.setDiseaseDetected(cursor.getString(4));
        logEntryHiveHealth.setVarroaTreatment(cursor.getString(5));

        return logEntryHiveHealth;
    }
}
