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
    public static final String COLUMN_APIARY_TABLE_ID = "_table_id";
    public static final String COLUMN_APIARY_PROFILE = "profile";
    public static final String COLUMN_APIARY_PROFILE_TABLE = "profile_table";
    public static final String COLUMN_APIARY_NAME = "name";

    // Database fields
    private SQLiteDatabase mDatabase;
    private MyDBHandler mDbHelper;
    private Context mContext;
    private String[] mAllColumns = { COLUMN_APIARY_ID, COLUMN_APIARY_TABLE_ID,
            COLUMN_APIARY_PROFILE, COLUMN_APIARY_PROFILE_TABLE, COLUMN_APIARY_NAME };

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

    public Apiary createApiary(String name, String profile) {
        // Apiary has only one parent (Profile) but we'll pass in an indicator anyway
        ContentValues values = new ContentValues();
        values.put(COLUMN_APIARY_PROFILE, profile);
        values.put(COLUMN_APIARY_PROFILE_TABLE, Profile.TABLE_ID);
        values.put(COLUMN_APIARY_NAME, name);
        long insertId = mDatabase.insert(TABLE_APIARY, null, values);
        Cursor cursor = mDatabase.query(TABLE_APIARY, mAllColumns,
                COLUMN_APIARY_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Apiary newApiary = cursorToApiary(cursor);
        cursor.close();

        return newApiary;
    }

    public void deleteApiary(Apiary apiary) {
        long id = apiary.getId();
        mDatabase.delete(TABLE_APIARY, COLUMN_APIARY_ID + " = " + id, null);
    }

    public Apiary getApiaryById(long id){
        Cursor cursor = mDatabase.query(TABLE_APIARY, mAllColumns,
                COLUMN_APIARY_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursorToApiary(cursor);
    }

    protected Apiary cursorToApiary(Cursor cursor) {
        Apiary apiary = new Apiary();
        apiary.setId(cursor.getLong(0));
        apiary.setTableId(cursor.getLong(1));
        apiary.setProfile(cursor.getLong(2));
        apiary.setProfileTable(cursor.getLong(3));
        apiary.setName(cursor.getString(4));

        return apiary;
    }
}
