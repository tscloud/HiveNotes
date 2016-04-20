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
public class ProfileDAO{

    public static final String TAG = "ProfileDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_PROFILES = "Profile";
    public static final String COLUMN_PROFILE_ID = "_id";
    public static final String COLUMN_PROFILE_NAME = "name";
    public static final String COLUMN_PROFILE_EMAIL = "email";

    // Database fields
    private SQLiteDatabase mDatabase;
    private MyDBHandler mDbHelper;
    private Context mContext;
    private String[] mAllColumns = { COLUMN_PROFILE_ID, COLUMN_PROFILE_NAME, COLUMN_PROFILE_EMAIL };

    public ProfileDAO(Context context) {
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

    public Profile createProfile(String name, String email) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_NAME, name);
        values.put(COLUMN_PROFILE_EMAIL, email);
        long insertId = mDatabase.insert(TABLE_PROFILES, null, values);

        Profile newProfile = null;
        if (insertId >= 0) {
            Cursor cursor = mDatabase.query(TABLE_PROFILES, mAllColumns,
                    COLUMN_PROFILE_ID + " = " + insertId, null, null, null, null);
            if (cursor.moveToFirst()) {
                newProfile = cursorToProfile(cursor);
            }
            cursor.close();
        }

        return newProfile;
    }

    public Profile createProfile(Profile aDO) {
        return createProfile(aDO.getName(), aDO.getEmail());
    }

    public Profile updateProfile(long id, String name, String email) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_NAME, name);
        values.put(COLUMN_PROFILE_EMAIL, email);
        int rowsUpdated = mDatabase.update(TABLE_PROFILES, values,
                COLUMN_PROFILE_ID + "=" + id, null);

        Profile updatedProfile = null;
        if (rowsUpdated > 0) {
            Cursor cursor = mDatabase.query(TABLE_PROFILES, mAllColumns,
                    COLUMN_PROFILE_ID + " = " + id, null, null, null, null);
            if (cursor.moveToFirst()) {
                updatedProfile = cursorToProfile(cursor);
            }
            cursor.close();
        }

        return updatedProfile;
    }

    public Profile updateProfile(Profile aDO) {
        return updateProfile(aDO.getId(), aDO.getName(), aDO.getEmail());
    }

    public void deleteProfile(Profile profile) {
        long id = profile.getId();
        mDatabase.delete(TABLE_PROFILES, COLUMN_PROFILE_ID + " = " + id, null);
    }

    public Profile getProfile(){
        // There should be only 1 but we'll select as if there may be more
        List<Profile> listProfile = new ArrayList<Profile>();
        // This is the 1 we'll return --> it'll be the last 1 in the cursor
        Profile profile = null;

        Cursor cursor = mDatabase.query(TABLE_PROFILES, mAllColumns, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    profile = cursorToProfile(cursor);
                    listProfile.add(profile);
                    cursor.moveToNext();
                }
            }
            // make sure to close the cursor
            cursor.close();
        }
        return profile;
    }

    protected Profile cursorToProfile(Cursor cursor) {
        Profile profile = new Profile();
        profile.setId(cursor.getLong(0));
        profile.setName(cursor.getString(1));
        profile.setEmail(cursor.getString(2));

        return profile;
    }
}
