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
public class LogEntryGeneralDAO {

    public static final String TAG = "LogEntryGeneralDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_LOGENTRYGENERAL = "LogEntryGeneral";
    public static final String COLUMN_LOGENTRYGENERAL_ID = "_id";
    public static final String COLUMN_LOGENTRYGENERAL_HIVE = "hive";
    public static final String COLUMN_LOGENTRYGENERAL_VISIT_DATE = "visit_date";
    public static final String COLUMN_LOGENTRYGENERAL_POPULATION = "population";
    public static final String COLUMN_LOGENTRYGENERAL_TEMPERAMENT = "temperament";
    public static final String COLUMN_LOGENTRYGENERAL_PESTS_DISEASE = "pests_disease";
    public static final String COLUMN_LOGENTRYGENERAL_BROOD_FRAMES = "brood_frames";
    public static final String COLUMN_LOGENTRYGENERAL_BROOD_PATTERN = "brood_pattern";
    public static final String COLUMN_LOGENTRYGENERAL_QUEEN = "queen";
    public static final String COLUMN_LOGENTRYGENERAL_HONEY_STORES = "honey_stores";
    public static final String COLUMN_LOGENTRYGENERAL_POLLEN_STORES = "pollen_stores";

    // Database fields
    private SQLiteDatabase mDatabase;
    private MyDBHandler mDbHelper;
    private Context mContext;
    private String[] mAllColumns = {COLUMN_LOGENTRYGENERAL_ID, COLUMN_LOGENTRYGENERAL_HIVE, COLUMN_LOGENTRYGENERAL_VISIT_DATE,
            COLUMN_LOGENTRYGENERAL_POPULATION, COLUMN_LOGENTRYGENERAL_TEMPERAMENT, COLUMN_LOGENTRYGENERAL_PESTS_DISEASE,
            COLUMN_LOGENTRYGENERAL_BROOD_FRAMES, COLUMN_LOGENTRYGENERAL_BROOD_PATTERN, COLUMN_LOGENTRYGENERAL_QUEEN,
            COLUMN_LOGENTRYGENERAL_HONEY_STORES, COLUMN_LOGENTRYGENERAL_POLLEN_STORES };

    public LogEntryGeneralDAO(Context context) {
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

    public LogEntryGeneral createLogEntry(long hive, long visitDate, String population, String temperament,
                                   String pestsDisease, int broodFrames, String broodPattern,
                                   String queen, String honeyStores, String pollenStores) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRYGENERAL_HIVE, hive);
        values.put(COLUMN_LOGENTRYGENERAL_VISIT_DATE, visitDate);
        values.put(COLUMN_LOGENTRYGENERAL_POPULATION, population);
        values.put(COLUMN_LOGENTRYGENERAL_TEMPERAMENT, temperament);
        values.put(COLUMN_LOGENTRYGENERAL_PESTS_DISEASE, pestsDisease);
        values.put(COLUMN_LOGENTRYGENERAL_BROOD_FRAMES, broodFrames);
        values.put(COLUMN_LOGENTRYGENERAL_BROOD_PATTERN, broodPattern);
        values.put(COLUMN_LOGENTRYGENERAL_QUEEN, queen);
        values.put(COLUMN_LOGENTRYGENERAL_HONEY_STORES, honeyStores);
        values.put(COLUMN_LOGENTRYGENERAL_POLLEN_STORES, pollenStores);
        long insertId = mDatabase.insert(TABLE_LOGENTRYGENERAL, null, values);

        LogEntryGeneral newLogEntryGeneral = null;
        if (insertId >= 0) {
            Cursor cursor = mDatabase.query(TABLE_LOGENTRYGENERAL, mAllColumns,
                    COLUMN_LOGENTRYGENERAL_ID + " = " + insertId, null, null, null, null);
            if (cursor.moveToFirst()) {
                newLogEntryGeneral = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return newLogEntryGeneral;
    }

    public LogEntryGeneral createLogEntry(LogEntryGeneral aDO) {
        return createLogEntry(aDO.getHive(), aDO.getVisitDate(), aDO.getPopulation(), aDO.getTemperament(),
                aDO.getPestsDisease(), aDO.getBroodFrames(), aDO.getBroodPattern(),aDO.getQueen(),
                aDO.getHoneyStores(), aDO.getPollenStores());
    }

    public LogEntryGeneral updateLogEntry(long id, long hive, long visitDate, String population,
                                   String temperament, String pestsDisease, int broodFrames,
                                   String broodPattern, String queen, String honeyStores,
                                   String pollenStores) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGENTRYGENERAL_HIVE, hive);
        values.put(COLUMN_LOGENTRYGENERAL_VISIT_DATE, visitDate);
        values.put(COLUMN_LOGENTRYGENERAL_POPULATION, population);
        values.put(COLUMN_LOGENTRYGENERAL_TEMPERAMENT, temperament);
        values.put(COLUMN_LOGENTRYGENERAL_PESTS_DISEASE, pestsDisease);
        values.put(COLUMN_LOGENTRYGENERAL_BROOD_FRAMES, broodFrames);
        values.put(COLUMN_LOGENTRYGENERAL_BROOD_PATTERN, broodPattern);
        values.put(COLUMN_LOGENTRYGENERAL_QUEEN, queen);
        values.put(COLUMN_LOGENTRYGENERAL_HONEY_STORES, honeyStores);
        values.put(COLUMN_LOGENTRYGENERAL_POLLEN_STORES, pollenStores);
        int rowsUpdated = mDatabase.update(TABLE_LOGENTRYGENERAL, values,
                COLUMN_LOGENTRYGENERAL_ID + "=" + id, null);

        LogEntryGeneral updatedLogEntryGeneral = null;
        if (rowsUpdated > 0) {
            Cursor cursor = mDatabase.query(TABLE_LOGENTRYGENERAL, mAllColumns,
                    COLUMN_LOGENTRYGENERAL_ID + " = " + id, null, null, null, null);
            if (cursor.moveToFirst()) {
                updatedLogEntryGeneral = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return updatedLogEntryGeneral;
    }

    public LogEntryGeneral updateLogEntry(LogEntryGeneral aDO) {
        return updateLogEntry(aDO.getId(), aDO.getHive(), aDO.getVisitDate(), aDO.getPopulation(), aDO.getTemperament(),
                aDO.getPestsDisease(), aDO.getBroodFrames(), aDO.getBroodPattern(),aDO.getQueen(),
                aDO.getHoneyStores(), aDO.getPollenStores());
    }

    public void deleteLogEntry(LogEntryGeneral logEntryGeneral) {
        long id = logEntryGeneral.getId();
        mDatabase.delete(TABLE_LOGENTRYGENERAL, COLUMN_LOGENTRYGENERAL_ID + " = " + id, null);
    }

    public LogEntryGeneral getLogEntryById(long id){
        Cursor cursor = mDatabase.query(TABLE_LOGENTRYGENERAL, mAllColumns,
                COLUMN_LOGENTRYGENERAL_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null);

        LogEntryGeneral retrievedLogEntryGeneral = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                retrievedLogEntryGeneral = cursorToLogEntry(cursor);
            }
            cursor.close();
        }

        return cursorToLogEntry(cursor);
    }

    protected LogEntryGeneral cursorToLogEntry(Cursor cursor) {
        LogEntryGeneral logEntryGeneral = new LogEntryGeneral();
        logEntryGeneral.setId(cursor.getLong(0));
        logEntryGeneral.setHive(cursor.getLong(1));
        logEntryGeneral.setVisitDate(cursor.getLong(2));
        logEntryGeneral.setPopulation(cursor.getString(3));
        logEntryGeneral.setTemperament(cursor.getString(4));
        logEntryGeneral.setPestsDisease(cursor.getString(5));
        logEntryGeneral.setBroodFrames(cursor.getInt(6));
        logEntryGeneral.setBroodPattern(cursor.getString(7));
        logEntryGeneral.setQueen(cursor.getString(8));
        logEntryGeneral.setHoneyStores(cursor.getString(9));
        logEntryGeneral.setPollenStores(cursor.getString(10));

        return logEntryGeneral;
    }
}
