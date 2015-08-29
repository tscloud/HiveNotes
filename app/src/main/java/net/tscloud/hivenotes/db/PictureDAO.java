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
public class PictureDAO {

    public static final String TAG = "PictureDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_PICTURE = "Picture";
    public static final String COLUMN_PICTURE_ID = "_id";
    public static final String COLUMN_PICTURE_APIARY = "apiary";
    public static final String COLUMN_PICTURE_HIVE = "hive";
    public static final String COLUMN_PICTURE_IMAGE_URI = "image_uri";

    // Database fields
    private SQLiteDatabase mDatabase;
    private MyDBHandler mDbHelper;
    private Context mContext;
    private String[] mAllColumns = { COLUMN_PICTURE_ID, COLUMN_PICTURE_APIARY, COLUMN_PICTURE_HIVE,
            COLUMN_PICTURE_IMAGE_URI };

    public PictureDAO(Context context) {
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

    public Picture createPicture(long apiary, long hive, String type, String pictureText) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PICTURE_APIARY, apiary);
        values.put(COLUMN_PICTURE_HIVE, hive);
        values.put(COLUMN_PICTURE_IMAGE_URI, type);
        long insertId = mDatabase.insert(TABLE_PICTURE, null, values);
        Cursor cursor = mDatabase.query(TABLE_PICTURE, mAllColumns,
                COLUMN_PICTURE_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Picture newPicture = cursorToPicture(cursor);
        cursor.close();

        return newPicture;
    }

    public void deletePicture(Picture picture) {
        long id = picture.getId();
        mDatabase.delete(TABLE_PICTURE, COLUMN_PICTURE_ID + " = " + id, null);
    }

    public Picture getPictureById(long id){
        Cursor cursor = mDatabase.query(TABLE_PICTURE, mAllColumns,
                COLUMN_PICTURE_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursorToPicture(cursor);
    }

    protected Picture cursorToPicture(Cursor cursor) {
        Picture picture = new Picture();
        picture.setId(cursor.getLong(0));
        picture.setApiary(cursor.getLong(1));
        picture.setHive(cursor.getLong(2));
        picture.setImageURI(cursor.getString(3));

        return picture;
    }
}