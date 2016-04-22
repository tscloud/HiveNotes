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
public class NotificationDAO {

    public static final String TAG = "NotificationDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_NOTIFICATION = "Notification";
    public static final String COLUMN_NOTIFICATION_ID = "_id";
    public static final String COLUMN_NOTIFICATION_APIARY = "apiary";
    public static final String COLUMN_NOTIFICATION_HIVE = "hive";
    public static final String COLUMN_NOTIFICATION_EVENT_ID = "event_id";
    public static final String COLUMN_NOTIFICATION_RMNDR_TYPE = "rmndr_type";

    // Database fields
    private SQLiteDatabase mDatabase;
    private MyDBHandler mDbHelper;
    private Context mContext;
    private String[] mAllColumns = {COLUMN_NOTIFICATION_ID, COLUMN_NOTIFICATION_APIARY,
            COLUMN_NOTIFICATION_HIVE, COLUMN_NOTIFICATION_EVENT_ID, COLUMN_NOTIFICATION_RMNDR_TYPE};

    public NotificationDAO(Context context) {
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

    public Notification createNotification(long apiary, long hive, long eventId, long rmndrType) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTIFICATION_APIARY, apiary);
        values.put(COLUMN_NOTIFICATION_HIVE, hive);
        values.put(COLUMN_NOTIFICATION_EVENT_ID, eventId);
        values.put(COLUMN_NOTIFICATION_RMNDR_TYPE, rmndrType);
        long insertId = mDatabase.insert(TABLE_NOTIFICATION, null, values);

        Notification newNotification = null;
        if (insertId >= 0) {
            Cursor cursor = mDatabase.query(TABLE_NOTIFICATION, mAllColumns,
                    COLUMN_NOTIFICATION_ID + " = " + insertId, null, null, null, null);
            if (cursor.moveToFirst()) {
                newNotification = cursorToNotification(cursor);
            }
            cursor.close();
        }

        return newNotification;
    }

    public Notification createNotification(Notification aDO) {
        return createNotification(aDO.getApiary(), aDO.getHive(), aDO.getEventId(), aDO.getRmndrType());
    }

    public Notification updateNotification(long id, long apiary, long hive, long eventId,
                                           long rmndrType) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTIFICATION_APIARY, apiary);
        values.put(COLUMN_NOTIFICATION_HIVE, hive);
        values.put(COLUMN_NOTIFICATION_EVENT_ID, eventId);
        values.put(COLUMN_NOTIFICATION_RMNDR_TYPE, rmndrType);
        int rowsUpdated = mDatabase.update(TABLE_NOTIFICATION, values,
                COLUMN_NOTIFICATION_ID + "=" + id, null);

        Notification updatedNotification = null;
        if (rowsUpdated > 0) {
            Cursor cursor = mDatabase.query(TABLE_NOTIFICATION, mAllColumns,
                    COLUMN_NOTIFICATION_ID + " = " + id, null, null, null, null);
            if (cursor.moveToFirst()) {
                updatedNotification = cursorToNotification(cursor);
            }
            cursor.close();
        }

        return updatedNotification;
    }

    public Notification updateNotification(Notification aDO) {
        return updateNotification(aDO.getId(), aDO.getApiary(), aDO.getHive(), aDO.getEventId(),
                aDO.getRmndrType());
    }

    public void deleteNotification(Notification notification) {
        long id = notification.getId();
        mDatabase.delete(TABLE_NOTIFICATION, COLUMN_NOTIFICATION_ID + " = " + id, null);
    }

    public Notification getNotificationById(long id){
        Cursor cursor = mDatabase.query(TABLE_NOTIFICATION, mAllColumns,
                COLUMN_NOTIFICATION_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null);

        Notification retrievedNotification = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                retrievedNotification = cursorToNotification(cursor);
            }
            cursor.close();
        }

        return retrievedNotification;
    }

    public Notification getNotificationByTypeAndHive(long type, long hive) {
        Cursor cursor = mDatabase.query(TABLE_NOTIFICATION, mAllColumns,
                COLUMN_NOTIFICATION_RMNDR_TYPE + " = ? AND " + COLUMN_NOTIFICATION_HIVE + " = ?",
                new String[] { String.valueOf(type), String.valueOf(hive) }, null, null, null);

        Notification retrievedNotification = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                retrievedNotification = cursorToNotification(cursor);
            }
            cursor.close();
        }

        return retrievedNotification;
    }

    public List<Notification> getNotificationList(long hiveId){
        List<Notification> listNotification = new ArrayList<Notification>();

        Cursor cursor = mDatabase.query(TABLE_NOTIFICATION, mAllColumns,
                COLUMN_NOTIFICATION_HIVE + " = ?",
                new String[] { String.valueOf(hiveId) }, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Notification notification = cursorToNotification(cursor);
                    listNotification.add(notification);
                    cursor.moveToNext();
                }
            }
           // make sure to close the cursor
           cursor.close();
        }

        return listNotification;
    }

    protected Notification cursorToNotification(Cursor cursor) {
        Notification notification = new Notification();
        notification.setId(cursor.getLong(0));
        notification.setApiary(cursor.getLong(1));
        notification.setHive(cursor.getLong(2));
        notification.setEventId(cursor.getLong(3));
        notification.setRmndrType(cursor.getLong(4));

        return notification;
    }
}
