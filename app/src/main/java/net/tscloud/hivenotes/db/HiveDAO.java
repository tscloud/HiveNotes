package net.tscloud.hivenotes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tscloud on 8/15/15.
 */
public class HiveDAO {

    public static final String TAG = "HiveDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_HIVE = "Hive";
    public static final String COLUMN_HIVE_ID = "_id";
    public static final String COLUMN_HIVE_APIARY = "apiary";
    public static final String COLUMN_HIVE_NAME = "name";
    public static final String COLUMN_HIVE_SPECIES = "species";
    public static final String COLUMN_HIVE_FOUNDATION_TYPE = "foundation_type";
    public static final String COLUMN_HIVE_NOTE = "note";

    // Database fields
    private SQLiteDatabase mDatabase;
    private MyDBHandler mDbHelper;
    private Context mContext;
    private String[] mAllColumns = {COLUMN_HIVE_ID, COLUMN_HIVE_APIARY, COLUMN_HIVE_NAME,
            COLUMN_HIVE_SPECIES, COLUMN_HIVE_FOUNDATION_TYPE, COLUMN_HIVE_NOTE};

    public HiveDAO(Context context) {
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

    public Hive createHive(long apiary, String name, String species, String foundationType, String note) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_HIVE_APIARY, apiary);
        values.put(COLUMN_HIVE_NAME, name);
        values.put(COLUMN_HIVE_SPECIES, species);
        values.put(COLUMN_HIVE_FOUNDATION_TYPE, foundationType);
        values.put(COLUMN_HIVE_NOTE, note);
        long insertId = mDatabase.insert(TABLE_HIVE, null, values);
        Cursor cursor = mDatabase.query(TABLE_HIVE, mAllColumns,
                COLUMN_HIVE_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Hive newHive = cursorToHive(cursor);
        cursor.close();

        return newHive;
    }

    public Hive updateHive(long id, long apiary, String name, String species, String foundationType, String note) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_HIVE_APIARY, apiary);
        values.put(COLUMN_HIVE_NAME, name);
        values.put(COLUMN_HIVE_SPECIES, species);
        values.put(COLUMN_HIVE_FOUNDATION_TYPE, foundationType);
        values.put(COLUMN_HIVE_NOTE, note);
        int rowsUpdated = mDatabase.update(TABLE_HIVE, values, COLUMN_HIVE_ID + "=" + id, null);

        Hive updatedHive = null;
        if (rowsUpdated > 0) {
            Cursor cursor = mDatabase.query(TABLE_HIVE, mAllColumns,
                    COLUMN_HIVE_ID + " = " + id, null, null, null, null);
            cursor.moveToFirst();
            updatedHive = cursorToHive(cursor);
            cursor.close();
        }

        return updatedHive;
    }

    public void deleteHive(Hive hive) {
        long id = hive.getId();
        mDatabase.delete(TABLE_HIVE, COLUMN_HIVE_ID + " = " + id, null);
    }

    public Hive getHiveById(long id){
        Cursor cursor = mDatabase.query(TABLE_HIVE, mAllColumns,
                COLUMN_HIVE_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursorToHive(cursor);
    }

    public List<Hive> getHiveList(long apiaryId) {
        List<Hive> listHive = new ArrayList<Hive>();

        Cursor cursor = mDatabase.query(TABLE_HIVE, mAllColumns,
                COLUMN_HIVE_APIARY + " = ?",
                new String[] { String.valueOf(apiaryId) }, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Hive hive = cursorToHive(cursor);
            listHive.add(hive);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return listHive;
    }

    protected Hive cursorToHive(Cursor cursor) {
        Hive hive = new Hive();
        hive.setId(cursor.getLong(0));
        hive.setApiary(cursor.getLong(1));
        hive.setName(cursor.getString(2));
        hive.setSpecies(cursor.getString(3));
        hive.setFoundationType(cursor.getString(4));
        hive.setNote(cursor.getString(5));

        return hive;
    }
}
