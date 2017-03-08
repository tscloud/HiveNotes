package net.tscloud.hivenotes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by tscloud on 8/15/15.
 */
public class PictureDAO extends AbstractDAO {

    public static final String TAG = "PictureDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_PICTURE = "Picture";
    public static final String COLUMN_PICTURE_ID = "_id";
    public static final String COLUMN_PICTURE_APIARY = "apiary";
    public static final String COLUMN_PICTURE_HIVE = "hive";
    public static final String COLUMN_PICTURE_IMAGE_URI = "image_uri";

    // Database fields
    private String[] mAllColumns = { COLUMN_PICTURE_ID, COLUMN_PICTURE_APIARY, COLUMN_PICTURE_HIVE,
            COLUMN_PICTURE_IMAGE_URI };

    public PictureDAO(Context context) {
        super(context);
    }

    // --DB access methods--

    public Picture createPicture(long apiary, long hive, String imageURI) {
        ContentValues values = new ContentValues();
        // Needed b/c rows in this table can be linked to Apiary OR Hive
        values.put(COLUMN_PICTURE_APIARY, (apiary < 1) ? null : apiary);
        values.put(COLUMN_PICTURE_HIVE, (hive < 1) ? null : hive);
        values.put(COLUMN_PICTURE_IMAGE_URI, imageURI);
        long insertId = mDatabase.insert(TABLE_PICTURE, null, values);

        Picture newPicture = null;
        if (insertId >= 0) {
            Cursor cursor = mDatabase.query(TABLE_PICTURE, mAllColumns,
                    COLUMN_PICTURE_ID + " = " + insertId, null, null, null, null);
            if (cursor.moveToFirst()) {
                newPicture = cursorToPicture(cursor);
            }
            cursor.close();
        }

        return newPicture;
    }

    public Picture createPicture(Picture aDO) {
        return createPicture(aDO.getApiary(), aDO.getHive(), aDO.getImageURI());
    }

    public Picture updatePicture(long id, long apiary, long hive, String imageURI) {
        ContentValues values = new ContentValues();
        // Needed b/c rows in this table can be linked to Apiary OR Hive
        values.put(COLUMN_PICTURE_APIARY, (apiary < 1) ? null : apiary);
        values.put(COLUMN_PICTURE_HIVE, (hive < 1) ? null : hive);
        values.put(COLUMN_PICTURE_IMAGE_URI, imageURI);
        int rowsUpdated = mDatabase.update(TABLE_PICTURE, values,
                COLUMN_PICTURE_ID + "=" + id, null);

        Picture updatedPicture = null;
        if (rowsUpdated > 0) {
            Cursor cursor = mDatabase.query(TABLE_PICTURE, mAllColumns,
                    COLUMN_PICTURE_ID + " = " + id, null, null, null, null);
            if (cursor.moveToFirst()) {
                updatedPicture = cursorToPicture(cursor);
            }
            cursor.close();
        }

        return updatedPicture;
    }

    public Picture updatePicture(Picture aDO) {
        return updatePicture(aDO.getId(), aDO.getApiary(), aDO.getHive(), aDO.getImageURI());
    }

    public void deletePicture(Picture picture) {
        long id = picture.getId();
        mDatabase.delete(TABLE_PICTURE, COLUMN_PICTURE_ID + " = " + id, null);
    }

    public Picture getPictureById(long id){
        Cursor cursor = mDatabase.query(TABLE_PICTURE, mAllColumns,
                COLUMN_PICTURE_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null);

        Picture retrievedPicture = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                retrievedPicture =cursorToPicture(cursor);
            }
            cursor.close();
        }

        return retrievedPicture;
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
