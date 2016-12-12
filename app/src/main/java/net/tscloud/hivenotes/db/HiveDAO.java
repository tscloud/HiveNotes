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
public class HiveDAO extends AbstactDAO {

    public static final String TAG = "HiveDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_HIVE = "Hive";
    public static final String COLUMN_HIVE_ID = "_id";
    public static final String COLUMN_HIVE_APIARY = "apiary";
    public static final String COLUMN_HIVE_NAME = "name";
    public static final String COLUMN_HIVE_SPECIES = "species";
    public static final String COLUMN_HIVE_REQUEEN = "requeen";
    public static final String COLUMN_HIVE_FOUNDATION_TYPE = "foundation_type";
    public static final String COLUMN_HIVE_NOTE = "note";

    // Database fields
    private String[] mAllColumns = {COLUMN_HIVE_ID, COLUMN_HIVE_APIARY, COLUMN_HIVE_NAME,
            COLUMN_HIVE_SPECIES, COLUMN_HIVE_REQUEEN, COLUMN_HIVE_FOUNDATION_TYPE, COLUMN_HIVE_NOTE};

    public HiveDAO(Context context) {
        super(context);
    }

    // --DB access methods--

    public Hive createHive(long apiary, String name, String species, String requeen,
                           String foundationType, String note) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_HIVE_APIARY, apiary);
        values.put(COLUMN_HIVE_NAME, name);
        values.put(COLUMN_HIVE_SPECIES, species);
        values.put(COLUMN_HIVE_REQUEEN, requeen);
        values.put(COLUMN_HIVE_FOUNDATION_TYPE, foundationType);
        values.put(COLUMN_HIVE_NOTE, note);
        long insertId = mDatabase.insert(TABLE_HIVE, null, values);

        Hive newHive = null;
        if (insertId >= 0) {
            Cursor cursor = mDatabase.query(TABLE_HIVE, mAllColumns,
                    COLUMN_HIVE_ID + " = " + insertId, null, null, null, null);
            if (cursor.moveToFirst()) {
                newHive = cursorToHive(cursor);
                cursor.close();
            }
        }

        return newHive;
    }

    public Hive createHive(Hive aDO) {
        return createHive(aDO.getApiary(), aDO.getName(), aDO.getSpecies(), aDO.getRequeen(),
                          aDO.getFoundationType(), aDO.getNote());
    }

    public Hive updateHive(long id, long apiary, String name, String species, String requeen,
                           String foundationType, String note) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_HIVE_APIARY, apiary);
        values.put(COLUMN_HIVE_NAME, name);
        values.put(COLUMN_HIVE_SPECIES, species);
        values.put(COLUMN_HIVE_REQUEEN, requeen);
        values.put(COLUMN_HIVE_FOUNDATION_TYPE, foundationType);
        values.put(COLUMN_HIVE_NOTE, note);
        int rowsUpdated = mDatabase.update(TABLE_HIVE, values, COLUMN_HIVE_ID + "=" + id, null);

        Hive updatedHive = null;
        if (rowsUpdated > 0) {
            Cursor cursor = mDatabase.query(TABLE_HIVE, mAllColumns,
                    COLUMN_HIVE_ID + " = " + id, null, null, null, null);
            if (cursor.moveToFirst()) {
                updatedHive = cursorToHive(cursor);
            }
            cursor.close();
        }

        return updatedHive;
    }

    public Hive updateHive(Hive aDO) {
        return updateHive(aDO.getId(), aDO.getApiary(), aDO.getName(), aDO.getSpecies(), aDO.getRequeen(),
                                  aDO.getFoundationType(), aDO.getNote());
    }

    public void deleteHive(Hive hive) {
        long id = hive.getId();
        mDatabase.delete(TABLE_HIVE, COLUMN_HIVE_ID + " = " + id, null);
    }

    public Hive getHiveById(long id){
        Cursor cursor = mDatabase.query(TABLE_HIVE, mAllColumns,
                COLUMN_HIVE_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        Hive retrievedHive = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                retrievedHive = cursorToHive(cursor);
            }
            cursor.close();
        }

        return retrievedHive;
    }

    public List<Hive> getHiveList(long apiaryId) {
        List<Hive> listHive = new ArrayList<Hive>();

        Cursor cursor = mDatabase.query(TABLE_HIVE, mAllColumns,
                COLUMN_HIVE_APIARY + " = ?",
                new String[] { String.valueOf(apiaryId) }, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Hive hive = cursorToHive(cursor);
                    listHive.add(hive);
                    cursor.moveToNext();
                }
            }
            // make sure to close the cursor
            cursor.close();
        }

        return listHive;
    }

    protected Hive cursorToHive(Cursor cursor) {
        Hive hive = new Hive();
        hive.setId(cursor.getLong(0));
        hive.setApiary(cursor.getLong(1));
        hive.setName(cursor.getString(2));
        hive.setSpecies(cursor.getString(3));
        hive.setRequeen(cursor.getString(4));
        hive.setFoundationType(cursor.getString(5));
        hive.setNote(cursor.getString(6));

        return hive;
    }
}
