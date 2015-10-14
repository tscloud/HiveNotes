package net.tscloud.hivenotes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by tscloud on 8/15/15.
 */
public class LogEntryDAO {

    public static final String TAG = "LogEntryDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_LOGENTRY = "logEntry";
    public static final String COLUMN_LOGENTRY_ID = "_id";
    public static final String COLUMN_LOGENTRY_HIVE = "hive";
    public static final String COLUMN_LOGENTRY_VISIT_DATE = "visit_date";
    public static final String COLUMN_LOGENTRY_POPULATION = "population";
    public static final String COLUMN_LOGENTRY_TEMPERAMENT = "temperament";
    public static final String COLUMN_LOGENTRY_EGGS = "eggs";
    public static final String COLUMN_LOGENTRY_LARVAE = "larvae";
    public static final String COLUMN_LOGENTRY_CAPPED_BROOD = "capped_brood";
    public static final String COLUMN_LOGENTRY_BROOD_FRAMES = "brood_frames";
    public static final String COLUMN_LOGENTRY_BROOD_PATTERN = "brood_pattern";
    public static final String COLUMN_LOGENTRY_QUEEN_AGE = "queen_age";
    public static final String COLUMN_LOGENTRY_HONEY_STORES = "honey_stores";
    public static final String COLUMN_LOGENTRY_POLLEN_STORES = "pollen_stores";

    // Database fields
    private SQLiteDatabase mDatabase;
    private MyDBHandler mDbHelper;
    private Context mContext;
    private String[] mAllColumns = {COLUMN_LOGENTRY_ID, COLUMN_LOGENTRY_HIVE, COLUMN_LOGENTRY_VISIT_DATE,
            COLUMN_LOGENTRY_POPULATION, COLUMN_LOGENTRY_TEMPERAMENT,COLUMN_LOGENTRY_EGGS, COLUMN_LOGENTRY_LARVAE,
            COLUMN_LOGENTRY_CAPPED_BROOD, COLUMN_LOGENTRY_BROOD_FRAMES, COLUMN_LOGENTRY_BROOD_PATTERN,
            COLUMN_LOGENTRY_QUEEN_AGE, COLUMN_LOGENTRY_HONEY_STORES, COLUMN_LOGENTRY_POLLEN_STORES };

    public LogEntryDAO(Context context) {
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

    public LogEntry createLogEntry(long hive, String visitDate, String population, String temperament,
                                   long eggs, long larvae, long cappedBrood, String broodFrames,
                                   String broodPattern, String queenAge, String honeyStores,
                                   String pollenStores) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRY_HIVE, hive);
        values.put(COLUMN_LOGENTRY_VISIT_DATE, visitDate);
        values.put(COLUMN_LOGENTRY_POPULATION, population);
        values.put(COLUMN_LOGENTRY_TEMPERAMENT, temperament);
        values.put(COLUMN_LOGENTRY_EGGS, eggs);
        values.put(COLUMN_LOGENTRY_LARVAE, larvae);
        values.put(COLUMN_LOGENTRY_CAPPED_BROOD, cappedBrood);
        values.put(COLUMN_LOGENTRY_BROOD_FRAMES, broodFrames);
        values.put(COLUMN_LOGENTRY_BROOD_PATTERN, broodPattern);
        values.put(COLUMN_LOGENTRY_QUEEN_AGE, queenAge);
        values.put(COLUMN_LOGENTRY_HONEY_STORES, honeyStores);
        values.put(COLUMN_LOGENTRY_POLLEN_STORES, pollenStores);
        long insertId = mDatabase.insert(TABLE_LOGENTRY, null, values);
        Cursor cursor = mDatabase.query(TABLE_LOGENTRY, mAllColumns,
                COLUMN_LOGENTRY_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        LogEntry newLogEntry = cursorToLogEntry(cursor);
        cursor.close();

        return newLogEntry;
    }

    public LogEntry updateLogEntry(long id, long hive, String visitDate, String population,
                                   String temperament, long eggs, long larvae, long cappedBrood,
                                   String broodFrames, String broodPattern, String queenAge,
                                   String honeyStores, String pollenStores) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRY_HIVE, hive);
        values.put(COLUMN_LOGENTRY_VISIT_DATE, visitDate);
        values.put(COLUMN_LOGENTRY_POPULATION, population);
        values.put(COLUMN_LOGENTRY_TEMPERAMENT, temperament);
        values.put(COLUMN_LOGENTRY_EGGS, eggs);
        values.put(COLUMN_LOGENTRY_LARVAE, larvae);
        values.put(COLUMN_LOGENTRY_CAPPED_BROOD, cappedBrood);
        values.put(COLUMN_LOGENTRY_BROOD_FRAMES, broodFrames);
        values.put(COLUMN_LOGENTRY_BROOD_PATTERN, broodPattern);
        values.put(COLUMN_LOGENTRY_QUEEN_AGE, queenAge);
        values.put(COLUMN_LOGENTRY_HONEY_STORES, honeyStores);
        values.put(COLUMN_LOGENTRY_POLLEN_STORES, pollenStores);
        int rowsUpdated = mDatabase.update(TABLE_LOGENTRY, values, COLUMN_LOGENTRY_ID + "=" + id, null);

        LogEntry updatedLogEntry = null;
        if (rowsUpdated > 0) {
            Cursor cursor = mDatabase.query(TABLE_LOGENTRY, mAllColumns,
                    COLUMN_LOGENTRY_ID + " = " + id, null, null, null, null);
            cursor.moveToFirst();
            updatedLogEntry = cursorToLogEntry(cursor);
            cursor.close();
        }

        return updatedLogEntry;
    }

    public void deleteLogEntry(LogEntry logEntry) {
        long id = logEntry.getId();
        mDatabase.delete(TABLE_LOGENTRY, COLUMN_LOGENTRY_ID + " = " + id, null);
    }

    public LogEntry getLogEntryById(long id){
        Cursor cursor = mDatabase.query(TABLE_LOGENTRY, mAllColumns,
                COLUMN_LOGENTRY_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursorToLogEntry(cursor);
    }

    protected LogEntry cursorToLogEntry(Cursor cursor) {
        LogEntry logEntry = new LogEntry();
        logEntry.setId(cursor.getLong(0));
        logEntry.setHive(cursor.getLong(1));
        logEntry.setVisitDate(cursor.getString(2));
        logEntry.setPopulation(cursor.getString(3));
        logEntry.setTemperament(cursor.getString(4));
        logEntry.setEggs(cursor.getLong(5));
        logEntry.setLarvae(cursor.getLong(6));
        logEntry.setCappedBrood(cursor.getLong(7));
        logEntry.setBroodFrames(cursor.getString(8));
        logEntry.setBroodPattern(cursor.getString(9));
        logEntry.setQueenAge(cursor.getString(10));
        logEntry.setHoneyStores(cursor.getString(11));
        logEntry.setPollenStores(cursor.getString(12));

        return logEntry;
    }
}
