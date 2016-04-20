package net.tscloud.hivenotes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by tscloud on 11/3/15.
 */
public class LogEntryPestMgmtDAO {

    public static final String TAG = "LogEntryPestMgmtDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_LOGENTRYPESTMGMT = "LogEntryPestMgmt";
    public static final String COLUMN_LOGENTRYPESTMGMT_ID = "_id";
    public static final String COLUMN_LOGENTRYPESTMGMT_HIVE = "hive";
    public static final String COLUMN_LOGENTRYPESTMGMT_VISIT_DATE = "visit_date";
    public static final String COLUMN_LOGENTRYPESTMGMT_DRONE_CELL_FNDN = "drone_cell_fndn";
    public static final String COLUMN_LOGENTRYPESTMGMT_DRONE_CELL_FNDN_RMNDR = "drone_cell_fndn_rmndr";
    public static final String COLUMN_LOGENTRYPESTMGMT_SMALL_HIVE_BEETLE_TRAP = "small_hive_beetle_trap";
    public static final String COLUMN_LOGENTRYPESTMGMT_MITES_TRTMNT = "mites_trtmnt";
    public static final String COLUMN_LOGENTRYPESTMGMT_MITES_TRTMNT_TYPE = "mites_trtmnt_type";
    public static final String COLUMN_LOGENTRYPESTMGMT_MITES_TRTMNT_RMNDR = "mites_trtmnt_rmndr";
    public static final String COLUMN_LOGENTRYPESTMGMT_SCREENED_BOTTOM_BOARD = "screened_bottom_board";
    public static final String COLUMN_LOGENTRYPESTMGMT_OTHER = "other";
    public static final String COLUMN_LOGENTRYPESTMGMT_OTHER_TYPE = "other_type";

    // Database fields
    private SQLiteDatabase mDatabase;
    private MyDBHandler mDbHelper;
    private Context mContext;
    private String[] mAllColumns = { COLUMN_LOGENTRYPESTMGMT_ID, COLUMN_LOGENTRYPESTMGMT_HIVE,
            COLUMN_LOGENTRYPESTMGMT_VISIT_DATE, COLUMN_LOGENTRYPESTMGMT_DRONE_CELL_FNDN,
            COLUMN_LOGENTRYPESTMGMT_DRONE_CELL_FNDN_RMNDR, COLUMN_LOGENTRYPESTMGMT_SMALL_HIVE_BEETLE_TRAP,
            COLUMN_LOGENTRYPESTMGMT_MITES_TRTMNT, COLUMN_LOGENTRYPESTMGMT_MITES_TRTMNT_TYPE,
            COLUMN_LOGENTRYPESTMGMT_MITES_TRTMNT_RMNDR, COLUMN_LOGENTRYPESTMGMT_SCREENED_BOTTOM_BOARD,
            COLUMN_LOGENTRYPESTMGMT_OTHER, COLUMN_LOGENTRYPESTMGMT_OTHER_TYPE };

    public LogEntryPestMgmtDAO(Context context) {
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

    public LogEntryPestMgmt createLogEntry(long hive, String visitDate, int drone_cell_fndn,
                                           long drone_cell_fndn_rmndr, int small_hive_beetle_trap,
                                           int mites_trtmnt, String mites_trtmnt_type, long mites_trtmnt_rmndr,
                                           int screened_bottom_board, int other, String otherType) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRYPESTMGMT_HIVE, hive);
        values.put(COLUMN_LOGENTRYPESTMGMT_VISIT_DATE, visitDate);
        values.put(COLUMN_LOGENTRYPESTMGMT_DRONE_CELL_FNDN, drone_cell_fndn);
        values.put(COLUMN_LOGENTRYPESTMGMT_DRONE_CELL_FNDN_RMNDR, drone_cell_fndn_rmndr);
        values.put(COLUMN_LOGENTRYPESTMGMT_SMALL_HIVE_BEETLE_TRAP, small_hive_beetle_trap);
        values.put(COLUMN_LOGENTRYPESTMGMT_MITES_TRTMNT, mites_trtmnt);
        values.put(COLUMN_LOGENTRYPESTMGMT_MITES_TRTMNT_TYPE, mites_trtmnt_type);
        values.put(COLUMN_LOGENTRYPESTMGMT_MITES_TRTMNT_RMNDR, mites_trtmnt_rmndr);
        values.put(COLUMN_LOGENTRYPESTMGMT_SCREENED_BOTTOM_BOARD, screened_bottom_board);
        values.put(COLUMN_LOGENTRYPESTMGMT_OTHER, other);
        values.put(COLUMN_LOGENTRYPESTMGMT_OTHER_TYPE, otherType);
        long insertId = mDatabase.insert(TABLE_LOGENTRYPESTMGMT, null, values);

        LogEntryPestMgmt newLogEntryPestMgmt = null;
        if (insertId >= 0) {
            Cursor cursor = mDatabase.query(TABLE_LOGENTRYPESTMGMT, mAllColumns,
                    COLUMN_LOGENTRYPESTMGMT_ID + " = " + insertId, null, null, null, null);
            if (cursor.moveToFirst()) {
                newLogEntryPestMgmt = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return newLogEntryPestMgmt;
    }

    public LogEntryPestMgmt createLogEntry(LogEntryPestMgmt aDO) {
        return createLogEntry(aDO.getHive(), aDO.getVisitDate(), aDO.getDroneCellFndn(), aDO.getDroneCellFndnRmndr(),
                aDO.getSmallHiveBeetleTrap(), aDO.getMitesTrtmnt(), aDO.getMitesTrtmntType(), aDO.getMitesTrtmntRmndr(),
                aDO.getScreenedBottomBoard(), aDO.getOther(), aDO.getOtherType());
    }

    public LogEntryPestMgmt updateLogEntry(long id, long hive, String visitDate, int drone_cell_fndn,
                                               long drone_cell_fndn_rmndr, int small_hive_beetle_trap,
                                               int mites_trtmnt, String mites_trtmnt_type, long mites_trtmnt_rmndr,
                                               int screened_bottom_board, int other, String otherType) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRYPESTMGMT_HIVE, hive);
        values.put(COLUMN_LOGENTRYPESTMGMT_VISIT_DATE, visitDate);
        values.put(COLUMN_LOGENTRYPESTMGMT_DRONE_CELL_FNDN, drone_cell_fndn);
        values.put(COLUMN_LOGENTRYPESTMGMT_DRONE_CELL_FNDN_RMNDR, drone_cell_fndn_rmndr);
        values.put(COLUMN_LOGENTRYPESTMGMT_SMALL_HIVE_BEETLE_TRAP, small_hive_beetle_trap);
        values.put(COLUMN_LOGENTRYPESTMGMT_MITES_TRTMNT, mites_trtmnt);
        values.put(COLUMN_LOGENTRYPESTMGMT_MITES_TRTMNT_TYPE, mites_trtmnt_type);
        values.put(COLUMN_LOGENTRYPESTMGMT_MITES_TRTMNT_RMNDR, mites_trtmnt_rmndr);
        values.put(COLUMN_LOGENTRYPESTMGMT_SCREENED_BOTTOM_BOARD, screened_bottom_board);
        values.put(COLUMN_LOGENTRYPESTMGMT_OTHER, other);
        values.put(COLUMN_LOGENTRYPESTMGMT_OTHER_TYPE, otherType);
        int rowsUpdated = mDatabase.update(TABLE_LOGENTRYPESTMGMT, values,
                COLUMN_LOGENTRYPESTMGMT_ID + "=" + id, null);

        LogEntryPestMgmt updatedLogEntryPestMgmt = null;
        if (rowsUpdated > 0) {
            Cursor cursor = mDatabase.query(TABLE_LOGENTRYPESTMGMT, mAllColumns,
                    COLUMN_LOGENTRYPESTMGMT_ID + " = " + id, null, null, null, null);
            if (cursor.moveToFirst()) {
                updatedLogEntryPestMgmt = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return updatedLogEntryPestMgmt;
    }

    public LogEntryPestMgmt updateLogEntry(LogEntryPestMgmt aDO) {
        return updateLogEntry(aDO.getId(), aDO.getHive(), aDO.getVisitDate(), aDO.getDroneCellFndn(),
                aDO.getDroneCellFndnRmndr(), aDO.getSmallHiveBeetleTrap(), aDO.getMitesTrtmnt(),
                aDO.getMitesTrtmntType(), aDO.getMitesTrtmntRmndr(), aDO.getScreenedBottomBoard(),
                aDO.getOther(), aDO.getOtherType());
    }

    public void deleteLogEntry(LogEntryPestMgmt logEntryPestMgmt) {
        long id = logEntryPestMgmt.getId();
        mDatabase.delete(TABLE_LOGENTRYPESTMGMT, COLUMN_LOGENTRYPESTMGMT_ID + " = " + id, null);
    }

    public LogEntryPestMgmt getLogEntryById(long id) {
        Cursor cursor = mDatabase.query(TABLE_LOGENTRYPESTMGMT, mAllColumns,
                COLUMN_LOGENTRYPESTMGMT_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        LogEntryPestMgmt retrievedLog = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                retrievedLog = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return retrievedLog;
    }

    protected LogEntryPestMgmt cursorToLogEntry(Cursor cursor) {
        LogEntryPestMgmt logEntryPestMgmt = new LogEntryPestMgmt();
        logEntryPestMgmt.setId(cursor.getLong(0));
        logEntryPestMgmt.setHive(cursor.getLong(1));
        logEntryPestMgmt.setVisitDate(cursor.getString(2));
        logEntryPestMgmt.setDroneCellFndn(cursor.getInt(3));
        logEntryPestMgmt.setDroneCellFndnRmndr(cursor.getInt(4));
        logEntryPestMgmt.setSmallHiveBeetleTrap(cursor.getInt(5));
        logEntryPestMgmt.setMitesTrtmnt(cursor.getInt(6));
        logEntryPestMgmt.setMitesTrtmntType(cursor.getString(7));
        logEntryPestMgmt.setMitesTrtmntRmndr(cursor.getInt(8));
        logEntryPestMgmt.setOther(cursor.getInt(9));
        logEntryPestMgmt.setOtherType(cursor.getString(9));

        return logEntryPestMgmt;
    }
}
