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
public class LogEntryFeedingDAO {

    public static final String TAG = "LogEntryFeedingDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_LOGENTRYFEEDING = "LogEntryFeeding";
    public static final String COLUMN_LOGENTRYFEEDING_ID = "_id";
    public static final String COLUMN_LOGENTRYFEEDING_HIVE = "hive";
    public static final String COLUMN_LOGENTRYFEEDING_VISIT_DATE = "visit_date";
    public static final String COLUMN_LOGENTRYFEEDING_ONE_ONE_SUGAR_WATER = "one_one_sugar_water";
    public static final String COLUMN_LOGENTRYFEEDING_TWO_ONE_SUGAR_WATER = "two_one_sugar_water";
    public static final String COLUMN_LOGENTRYFEEDING_POLLEN_PATTY = "pollen_patty";
    public static final String COLUMN_LOGENTRYFEEDING_OTHER = "other";
    public static final String COLUMN_LOGENTRYFEEDING_OTHER_TYPE = "other_type";

    // Database fields
    private SQLiteDatabase mDatabase;
    private MyDBHandler mDbHelper;
    private Context mContext;
    private String[] mAllColumns = {COLUMN_LOGENTRYFEEDING_ID, COLUMN_LOGENTRYFEEDING_HIVE,
            COLUMN_LOGENTRYFEEDING_VISIT_DATE, COLUMN_LOGENTRYFEEDING_ONE_ONE_SUGAR_WATER,
            COLUMN_LOGENTRYFEEDING_TWO_ONE_SUGAR_WATER, COLUMN_LOGENTRYFEEDING_POLLEN_PATTY,
            COLUMN_LOGENTRYFEEDING_OTHER, COLUMN_LOGENTRYFEEDING_OTHER_TYPE};

    public LogEntryFeedingDAO(Context context) {
        this.mContext = context;
        mDbHelper = MyDBHandler.getInstance(context);
        // open the database
        try {
            open();
        } catch (SQLException e) {
            Log.e(TAG, "SQLException on openning database " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }

    // --DB access methods--

    public LogEntryFeeding createLogEntry(long hive, long visitDate, int oneOneSugarWater,
                                          int oneTwoSugarWater, int pollenPatty, int other, String otherType) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRYFEEDING_HIVE, hive);
        values.put(COLUMN_LOGENTRYFEEDING_VISIT_DATE, visitDate);
        values.put(COLUMN_LOGENTRYFEEDING_ONE_ONE_SUGAR_WATER, oneOneSugarWater);
        values.put(COLUMN_LOGENTRYFEEDING_TWO_ONE_SUGAR_WATER, oneTwoSugarWater);
        values.put(COLUMN_LOGENTRYFEEDING_POLLEN_PATTY, pollenPatty);
        values.put(COLUMN_LOGENTRYFEEDING_OTHER, other);
        values.put(COLUMN_LOGENTRYFEEDING_OTHER_TYPE, otherType);
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
        return createLogEntry(aDO.getHive(), aDO.getVisitDate(), aDO.getOneOneSugarWater(),
                aDO.getTwoOneSugarWater(), aDO.getPollenPatty(), aDO.getOther(), aDO.getOtherType());
    }

    public LogEntryFeeding updateLogEntry(long id, long hive, long visitDate, int oneOneSugarWater,
                                          int oneTwoSugarWater, int pollenPatty, int other, String otherType) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRYFEEDING_HIVE, hive);
        values.put(COLUMN_LOGENTRYFEEDING_VISIT_DATE, visitDate);
        values.put(COLUMN_LOGENTRYFEEDING_ONE_ONE_SUGAR_WATER, oneOneSugarWater);
        values.put(COLUMN_LOGENTRYFEEDING_TWO_ONE_SUGAR_WATER, oneTwoSugarWater);
        values.put(COLUMN_LOGENTRYFEEDING_POLLEN_PATTY, pollenPatty);
        values.put(COLUMN_LOGENTRYFEEDING_OTHER, other);
        values.put(COLUMN_LOGENTRYFEEDING_OTHER_TYPE, otherType);
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
        return updateLogEntry(aDO.getId(), aDO.getHive(), aDO.getVisitDate(), aDO.getOneOneSugarWater(),
                aDO.getTwoOneSugarWater(), aDO.getPollenPatty(), aDO.getOther(), aDO.getOtherType());
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

    protected LogEntryFeeding cursorToLogEntry(Cursor cursor) {
        LogEntryFeeding logEntryFeeding = new LogEntryFeeding();
        logEntryFeeding.setId(cursor.getLong(0));
        logEntryFeeding.setHive(cursor.getLong(1));
        logEntryFeeding.setVisitDate(cursor.getLong(2));
        logEntryFeeding.setOneOneSugarWater(cursor.getInt(3));
        logEntryFeeding.setTwoOneSugarWater(cursor.getInt(4));
        logEntryFeeding.setPollenPatty(cursor.getInt(5));
        logEntryFeeding.setOther(cursor.getInt(6));
        logEntryFeeding.setOtherType(cursor.getString(7));

        return logEntryFeeding;
    }
}
