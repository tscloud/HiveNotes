package net.tscloud.hivenotes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by tscloud on 10/17/15.
 */
public class LogEntryProductivityDAO {

    public static final String TAG = "LogEntryGeneralDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_LOGENTRYPRODUCTIVITY = "LogEntryProductivity";
    public static final String COLUMN_LOGENTRYPRODUCTIVITY_ID = "_id";
    public static final String COLUMN_LOGENTRYPRODUCTIVITY_HIVE = "hive";
    public static final String COLUMN_LOGENTRYPRODUCTIVITY_VISIT_DATE = "visit_date";
    public static final String COLUMN_LOGENTRYPRODUCTIVITY_HONEY_ADD_SUPERS = "honey_add_supers";
    public static final String COLUMN_LOGENTRYPRODUCTIVITY_HONEY_REMOVE_SUPERS = "honey_remove_supers";
    public static final String COLUMN_LOGENTRYPRODUCTIVITY_EXTRACTED_HONEY = "extracted_honey";
    public static final String COLUMN_LOGENTRYPRODUCTIVITY_ADD_POLLEN_TRAP = "add_pollen_trap";
    public static final String COLUMN_LOGENTRYPRODUCTIVITY_REMOVE_POLLEN_TRAP = "remove_pollen_trap";
    public static final String COLUMN_LOGENTRYPRODUCTIVITY_POLLEN_COLLECTED = "pollen_collected";
    public static final String COLUMN_LOGENTRYPRODUCTIVITY_BEESWAX_COLLECTED = "beeswax_collected";

    // Database fields
    private SQLiteDatabase mDatabase;
    private MyDBHandler mDbHelper;
    private Context mContext;
    private String[] mAllColumns = { COLUMN_LOGENTRYPRODUCTIVITY_ID, COLUMN_LOGENTRYPRODUCTIVITY_HIVE,
            COLUMN_LOGENTRYPRODUCTIVITY_VISIT_DATE, COLUMN_LOGENTRYPRODUCTIVITY_HONEY_ADD_SUPERS,
            COLUMN_LOGENTRYPRODUCTIVITY_HONEY_REMOVE_SUPERS, COLUMN_LOGENTRYPRODUCTIVITY_EXTRACTED_HONEY,
            COLUMN_LOGENTRYPRODUCTIVITY_ADD_POLLEN_TRAP, COLUMN_LOGENTRYPRODUCTIVITY_REMOVE_POLLEN_TRAP,
            COLUMN_LOGENTRYPRODUCTIVITY_POLLEN_COLLECTED, COLUMN_LOGENTRYPRODUCTIVITY_BEESWAX_COLLECTED};

    public LogEntryProductivityDAO(Context context) {
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

    public LogEntryProductivity createLogEntry(long hive, String visitDate, String honeyAddSupers,
                                               String honeyRemoveSupers, float extractedHoney,
                                               int addPollenTrap, int removePollenTrap,
                                               float pollenCollected, float beeswaxCollected) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_HIVE ,hive);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_VISIT_DATE ,visitDate);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_HONEY_ADD_SUPERS, honeyAddSupers);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_HONEY_REMOVE_SUPERS, honeyRemoveSupers);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_EXTRACTED_HONEY,extractedHoney);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_ADD_POLLEN_TRAP, addPollenTrap);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_REMOVE_POLLEN_TRAP, removePollenTrap);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_POLLEN_COLLECTED, pollenCollected);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_BEESWAX_COLLECTED, beeswaxCollected);
        long insertId = mDatabase.insert(TABLE_LOGENTRYPRODUCTIVITY, null, values);
        Cursor cursor = mDatabase.query(TABLE_LOGENTRYPRODUCTIVITY, mAllColumns,
                COLUMN_LOGENTRYPRODUCTIVITY_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        LogEntryProductivity newLogEntryProductivity = cursorToLogEntry(cursor);
        cursor.close();

        return newLogEntryProductivity;
    }

    public LogEntryProductivity createLogEntry(LogEntryProductivity aDO) {
        return createLogEntry(aDO.getHive(), aDO.getVisitDate(), aDO.getHoneyAddSupers(),
                aDO.getHoneyRemoveSupers(), aDO.getExtractedHoney(), aDO.getAddPollenTrap(),
                aDO.getRemovePollenTrap(), aDO.getPollenCollected(), aDO.getBeeswaxCollected());
    }

    public LogEntryProductivity updateLogEntry(long id, long hive, String visitDate, String honeyAddSupers,
                                               String honeyRemoveSupers, float extractedHoney,
                                               int addPollenTrap, int removePollenTrap,
                                               float pollenColected, float beeswaxColected) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_HIVE, hive);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_VISIT_DATE, visitDate);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_HONEY_ADD_SUPERS, honeyAddSupers);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_HONEY_REMOVE_SUPERS, honeyRemoveSupers);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_EXTRACTED_HONEY,extractedHoney);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_ADD_POLLEN_TRAP, addPollenTrap);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_REMOVE_POLLEN_TRAP, removePollenTrap);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_POLLEN_COLLECTED, pollenColected);
        values.put(COLUMN_LOGENTRYPRODUCTIVITY_BEESWAX_COLLECTED, beeswaxColected);
        int rowsUpdated = mDatabase.update(TABLE_LOGENTRYPRODUCTIVITY, values, COLUMN_LOGENTRYPRODUCTIVITY_ID + "=" + id, null);

        LogEntryProductivity updatedLogEntryProductivity = null;
        if (rowsUpdated > 0) {
            Cursor cursor = mDatabase.query(TABLE_LOGENTRYPRODUCTIVITY, mAllColumns,
                    COLUMN_LOGENTRYPRODUCTIVITY_ID + " = " + id, null, null, null, null);
            cursor.moveToFirst();
            updatedLogEntryProductivity = cursorToLogEntry(cursor);
            cursor.close();
        }

        return updatedLogEntryProductivity;
    }

    public LogEntryProductivity updateLogEntry(LogEntryProductivity aDO) {
        return updateLogEntry(aDO.getId(), aDO.getHive(), aDO.getVisitDate(), aDO.getHoneyAddSupers(),
                aDO.getHoneyRemoveSupers(), aDO.getExtractedHoney(), aDO.getAddPollenTrap(),
                aDO.getRemovePollenTrap(), aDO.getPollenCollected(), aDO.getBeeswaxCollected());
    }

    public void deleteLogEntry(LogEntryProductivity logEntryProductivity) {
        long id = logEntryProductivity.getId();
        mDatabase.delete(TABLE_LOGENTRYPRODUCTIVITY, COLUMN_LOGENTRYPRODUCTIVITY_ID + " = " + id, null);
    }

    public LogEntryProductivity getLogEntryById(long id){
        Cursor cursor = mDatabase.query(TABLE_LOGENTRYPRODUCTIVITY, mAllColumns,
                COLUMN_LOGENTRYPRODUCTIVITY_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursorToLogEntry(cursor);
    }

    protected LogEntryProductivity cursorToLogEntry(Cursor cursor) {
        LogEntryProductivity logEntryProductivity = new LogEntryProductivity();
        logEntryProductivity.setId(cursor.getLong(0));
        logEntryProductivity.setHive(cursor.getLong(1));
        logEntryProductivity.setVisitDate(cursor.getString(2));
        logEntryProductivity.setHoneyAddSupers(cursor.getString(3));
        logEntryProductivity.setHoneyRemoveSupers(cursor.getString(4));
        logEntryProductivity.setExtractedHoney(cursor.getFloat(5));
        logEntryProductivity.setAddPollenTrap(cursor.getInt(6));
        logEntryProductivity.setRemovePollenTrap(cursor.getInt(7));
        logEntryProductivity.setPollenCollected(cursor.getFloat(8));
        logEntryProductivity.setBeeswaxCollected(cursor.getFloat(9));

        return logEntryProductivity;
    }

};

