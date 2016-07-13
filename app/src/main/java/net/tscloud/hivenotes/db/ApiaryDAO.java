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
public class ApiaryDAO extends AbstactDAO {

    public static final String TAG = "ApiaryDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_APIARY = "Apiary";
    public static final String COLUMN_APIARY_ID = "_id";
    public static final String COLUMN_APIARY_PROFILE = "profile";
    public static final String COLUMN_APIARY_NAME = "name";
    public static final String COLUMN_POSTAL_CODE = "postal_code";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    // Database fields
    private String[] mAllColumns = { COLUMN_APIARY_ID, COLUMN_APIARY_PROFILE, COLUMN_APIARY_NAME,
            COLUMN_POSTAL_CODE,COLUMN_LATITUDE,COLUMN_LONGITUDE };

    // --constructor--
    public ApiaryDAO(Context context) {
        super(context);
    }

    // --DB access methods--

    public Apiary createApiary(long profile, String name, String postalCode, float latitude,
                               float longitude) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_APIARY_PROFILE, profile);
        values.put(COLUMN_APIARY_NAME, name);
        values.put(COLUMN_POSTAL_CODE, postalCode);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        long insertId = mDatabase.insert(TABLE_APIARY, null, values);

        Apiary newApiary = null;
        if (insertId >= 0) {
            Cursor cursor = mDatabase.query(TABLE_APIARY, mAllColumns,
                    COLUMN_APIARY_ID + " = " + insertId, null, null, null, null);
            if (cursor.moveToFirst()) {
                newApiary = cursorToApiary(cursor);
                cursor.close();
            }
        }

        return newApiary;
    }

    public Apiary createApiary(Apiary aDO) {
        return createApiary(aDO.getProfile(), aDO.getName(), aDO.getPostalCode(), aDO.getLatitude(),
                              aDO.getLongitude());
    }

    public Apiary updateApiary(long id, long profile, String name, String postalCode, float latitude,
                               float longitude) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_APIARY_PROFILE, profile);
        values.put(COLUMN_APIARY_NAME, name);
        values.put(COLUMN_POSTAL_CODE, postalCode);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        int rowsUpdated = mDatabase.update(TABLE_APIARY, values, COLUMN_APIARY_ID + "=" + id, null);

        Apiary updatedApiary = null;
        if (rowsUpdated > 0) {
            Cursor cursor = mDatabase.query(TABLE_APIARY, mAllColumns,
                    COLUMN_APIARY_ID + " = " + id, null, null, null, null);
            if (cursor.moveToFirst()) {
                updatedApiary = cursorToApiary(cursor);
            }
            cursor.close();
        }

        return updatedApiary;
    }

    public Apiary updateApiary(Apiary aDO) {
        return updateApiary(aDO.getId(), aDO.getProfile(), aDO.getName(), aDO.getPostalCode(),
                            aDO.getLatitude(), aDO.getLongitude());
    }

    public void deleteApiary(Apiary apiary) {
        long id = apiary.getId();
        mDatabase.delete(TABLE_APIARY, COLUMN_APIARY_ID + " = " + id, null);
    }

    public Apiary getApiaryById(long id){
        Cursor cursor = mDatabase.query(TABLE_APIARY, mAllColumns,
                COLUMN_APIARY_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        Apiary retrievedApiary = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                retrievedApiary = cursorToApiary(cursor);
            }
            cursor.close();
        }

        return retrievedApiary;
    }

    public List<Apiary> getApiaryList(long profileId) {
        List<Apiary> listApiary = new ArrayList<Apiary>();

        Cursor cursor = mDatabase.query(TABLE_APIARY, mAllColumns,
                COLUMN_APIARY_PROFILE + " = ?",
                new String[] { String.valueOf(profileId) }, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Apiary apiary = cursorToApiary(cursor);
                    listApiary.add(apiary);
                    cursor.moveToNext();
                }
            }
            // make sure to close the cursor
            cursor.close();
        }

        return listApiary;
    }

    protected Apiary cursorToApiary(Cursor cursor) {
        Apiary apiary = new Apiary();
        apiary.setId(cursor.getLong(0));
        apiary.setProfile(cursor.getLong(1));
        apiary.setName(cursor.getString(2));
        apiary.setPostalCode(cursor.getString(3));
        apiary.setLatitude(cursor.getFloat(4));
        apiary.setLongitude(cursor.getFloat(5));

        return apiary;
    }
}
