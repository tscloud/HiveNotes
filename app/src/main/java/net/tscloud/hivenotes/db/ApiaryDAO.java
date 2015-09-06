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
public class ApiaryDAO {

    public static final String TAG = "ApiaryDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_APIARY = "Apiary";
    public static final String COLUMN_APIARY_ID = "_id";
    public static final String COLUMN_APIARY_PROFILE = "profile";
    public static final String COLUMN_APIARY_NAME = "name";
    public static final String COLUMN_POSTAL_CODE = "postal_code";

    // Database fields
    private SQLiteDatabase mDatabase;
    private MyDBHandler mDbHelper;
    private Context mContext;
    private String[] mAllColumns = { COLUMN_APIARY_ID, COLUMN_APIARY_PROFILE, COLUMN_APIARY_NAME,
            COLUMN_POSTAL_CODE };

    public ApiaryDAO(Context context) {
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

    public Apiary createApiary(long profile, String name, String postalCode) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_APIARY_PROFILE, profile);
        values.put(COLUMN_APIARY_NAME, name);
        values.put(COLUMN_POSTAL_CODE, postalCode);
        long insertId = mDatabase.insert(TABLE_APIARY, null, values);
        Cursor cursor = mDatabase.query(TABLE_APIARY, mAllColumns,
                COLUMN_APIARY_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Apiary newApiary = cursorToApiary(cursor);
        cursor.close();

        return newApiary;
    }

    public Apiary updateApiary(long id, long profile, String name, String postalCode) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_APIARY_PROFILE, profile);
        values.put(COLUMN_APIARY_NAME, name);
        values.put(COLUMN_POSTAL_CODE, postalCode);
        int rowsUpdated = mDatabase.update(TABLE_APIARY, values, COLUMN_APIARY_ID + "=" + id, null);

        Apiary updatedApiary = null;
        if (rowsUpdated > 0) {
            Cursor cursor = mDatabase.query(TABLE_APIARY, mAllColumns,
                    COLUMN_APIARY_ID + " = " + id, null, null, null, null);
            cursor.moveToFirst();
            updatedApiary = cursorToApiary(cursor);
            cursor.close();
        }

        return updatedApiary;
    }

    public void deleteApiary(Apiary apiary) {
        long id = apiary.getId();
        mDatabase.delete(TABLE_APIARY, COLUMN_APIARY_ID + " = " + id, null);
    }

    public Apiary getApiaryById(long id){
        Cursor cursor = mDatabase.query(TABLE_APIARY, mAllColumns,
                COLUMN_APIARY_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursorToApiary(cursor);
    }

    public List<Apiary> getApiaryList(long profileId) {
        List<Apiary> listApiary = new ArrayList<Apiary>();

        Cursor cursor = mDatabase.query(TABLE_APIARY, mAllColumns,
                COLUMN_APIARY_PROFILE + " = ?",
                new String[] { String.valueOf(profileId) }, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Apiary apiary = cursorToApiary(cursor);
            listApiary.add(apiary);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return listApiary;
    }

    protected Apiary cursorToApiary(Cursor cursor) {
        Apiary apiary = new Apiary();
        apiary.setId(cursor.getLong(0));
        apiary.setProfile(cursor.getLong(1));
        apiary.setName(cursor.getString(2));
        apiary.setPostalCode(cursor.getString(3));

        return apiary;
    }
}
