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
public class LogEntryOtherDAO {

    public static final String TAG = "LogEntryOtherDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_LOGENTRYOTHER = "LogEntryOther";
    public static final String COLUMN_LOGENTRYOTHER_ID = "_id";
    public static final String COLUMN_LOGENTRYOTHER_HIVE = "hive";
    public static final String COLUMN_LOGENTRYOTHER_VISIT_DATE = "visit_date";
    public static final String COLUMN_LOGENTRYOTHER_REQUEEN = "requeen";
    public static final String COLUMN_LOGENTRYOTHER_REQUEEN_RMNDR = "requeen_rmndr";
    public static final String COLUMN_LOGENTRYOTHER_SWARM_RMNDR = "swarm_rmndr";
    public static final String COLUMN_LOGENTRYOTHER_SPLIT_HIVE_RMNDR = "split_hive_rmndr";

    // Database fields
    private SQLiteDatabase mDatabase;
    private MyDBHandler mDbHelper;
    private Context mContext;
    private String[] mAllColumns = {COLUMN_LOGENTRYOTHER_ID, COLUMN_LOGENTRYOTHER_HIVE,
            COLUMN_LOGENTRYOTHER_VISIT_DATE, COLUMN_LOGENTRYOTHER_REQUEEN, COLUMN_LOGENTRYOTHER_REQUEEN_RMNDR,
            COLUMN_LOGENTRYOTHER_SWARM_RMNDR, COLUMN_LOGENTRYOTHER_SPLIT_HIVE_RMNDR};

    public LogEntryOtherDAO(Context context) {
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

    public LogEntryOther createLogEntry(long hive, String visitDate, String requeen, int requeenRmndr,
                                 int swarmRmndr, int splitHiveRmndr) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRYOTHER_HIVE, hive);
        values.put(COLUMN_LOGENTRYOTHER_VISIT_DATE, visitDate);
        values.put(COLUMN_LOGENTRYOTHER_REQUEEN, requeen);
        values.put(COLUMN_LOGENTRYOTHER_REQUEEN_RMNDR, requeenRmndr);
        values.put(COLUMN_LOGENTRYOTHER_SWARM_RMNDR, swarmRmndr);
        values.put(COLUMN_LOGENTRYOTHER_SPLIT_HIVE_RMNDR, splitHiveRmndr);
        long insertId = mDatabase.insert(TABLE_LOGENTRYOTHER, null, values);
        Cursor cursor = mDatabase.query(TABLE_LOGENTRYOTHER, mAllColumns,
                COLUMN_LOGENTRYOTHER_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        LogEntryOther newLogEntryOther = cursorToLogEntry(cursor);
        cursor.close();

        return newLogEntryOther;
    }

    public LogEntryOther createLogEntry(LogEntryOther aDO) {
        return createLogEntry(aDO.getHive(), aDO.getVisitDate(), aDO.getRequeen(), aDO.getRequeenRmndr(),
                aDO.getSplitHiveRmndr(), aDO.getSwarmRmndr());
    }

    public LogEntryOther updateLogEntry(long id, long hive, String visitDate, String requeen, int requeenRmndr,
                                 int swarmRmndr, int splitHiveRmndr) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRYOTHER_HIVE, hive);
        values.put(COLUMN_LOGENTRYOTHER_VISIT_DATE, visitDate);
        values.put(COLUMN_LOGENTRYOTHER_REQUEEN, requeen);
        values.put(COLUMN_LOGENTRYOTHER_REQUEEN_RMNDR, requeenRmndr);
        values.put(COLUMN_LOGENTRYOTHER_SWARM_RMNDR, swarmRmndr);
        values.put(COLUMN_LOGENTRYOTHER_SPLIT_HIVE_RMNDR, splitHiveRmndr);
        int rowsUpdated = mDatabase.update(TABLE_LOGENTRYOTHER, values, COLUMN_LOGENTRYOTHER_ID + "=" + id, null);

        LogEntryOther updatedLogEntryOther = null;
        if (rowsUpdated > 0) {
            Cursor cursor = mDatabase.query(TABLE_LOGENTRYOTHER, mAllColumns,
                    COLUMN_LOGENTRYOTHER_ID + " = " + id, null, null, null, null);
            cursor.moveToFirst();
            updatedLogEntryOther = cursorToLogEntry(cursor);
            cursor.close();
        }

        return updatedLogEntryOther;
    }

    public LogEntryOther updateLogEntry(LogEntryOther aDO) {
        return updateLogEntry(aDO.getId(), aDO.getHive(), aDO.getVisitDate(), aDO.getRequeen(), aDO.getRequeenRmndr(),
                aDO.getSplitHiveRmndr(), aDO.getSwarmRmndr());
    }

    public void deleteLogEntry(LogEntryOther logEntryOther) {
        long id = logEntryOther.getId();
        mDatabase.delete(TABLE_LOGENTRYOTHER, COLUMN_LOGENTRYOTHER_ID + " = " + id, null);
    }

    public LogEntryOther getLogEntryById(long id){
        Cursor cursor = mDatabase.query(TABLE_LOGENTRYOTHER, mAllColumns,
                COLUMN_LOGENTRYOTHER_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursorToLogEntry(cursor);
    }

    protected LogEntryOther cursorToLogEntry(Cursor cursor) {
        LogEntryOther logEntryOther = new LogEntryOther();
        logEntryOther.setId(cursor.getLong(0));
        logEntryOther.setHive(cursor.getLong(1));
        logEntryOther.setVisitDate(cursor.getString(2));
        logEntryOther.setRequeen(cursor.getString(3));
        logEntryOther.setRequeenRmndr(cursor.getInt(4));
        logEntryOther.setSplitHiveRmndr(cursor.getInt(5));
        logEntryOther.setSwarmRmndr(cursor.getInt(6));

        return logEntryOther;
    }
}
 