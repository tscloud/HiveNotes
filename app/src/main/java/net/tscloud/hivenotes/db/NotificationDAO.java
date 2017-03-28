package net.tscloud.hivenotes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tscloud on 8/15/15.
 */
public class NotificationDAO extends AbstractDAO {

    public static final String TAG = "NotificationDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_NOTIFICATION = "Notification";
    public static final String COLUMN_NOTIFICATION_ID = "_id";
    public static final String COLUMN_NOTIFICATION_APIARY = "apiary";
    public static final String COLUMN_NOTIFICATION_HIVE = "hive";
    public static final String COLUMN_NOTIFICATION_EVENT_ID = "event_id";
    public static final String COLUMN_NOTIFICATION_RMNDR_TYPE = "rmndr_type";
    public static final String COLUMN_NOTIFICATION_RMNDR_DESC = "rmndr_desc";

    // Database fields
    private String[] mAllColumns = {COLUMN_NOTIFICATION_ID, COLUMN_NOTIFICATION_APIARY,
            COLUMN_NOTIFICATION_HIVE, COLUMN_NOTIFICATION_EVENT_ID, COLUMN_NOTIFICATION_RMNDR_TYPE,
            COLUMN_NOTIFICATION_RMNDR_DESC};

    public NotificationDAO(Context context) {
        super(context);
    }

    // --DB access methods--

    public Notification createNotification(long apiary, long hive, long eventId, long rmndrType,
                                           String rmndrDesc) {
        ContentValues values = new ContentValues();
        // Needed b/c rows in this table can be linked to Apiary OR Hive
        values.put(COLUMN_NOTIFICATION_APIARY, (apiary < 1) ? null : apiary);
        values.put(COLUMN_NOTIFICATION_HIVE, (hive < 1) ? null : hive);
        values.put(COLUMN_NOTIFICATION_EVENT_ID, eventId);
        values.put(COLUMN_NOTIFICATION_RMNDR_TYPE, rmndrType);
        values.put(COLUMN_NOTIFICATION_RMNDR_DESC, rmndrDesc);
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
        return createNotification(aDO.getApiary(), aDO.getHive(), aDO.getEventId(),
                                aDO.getRmndrType(), aDO.getRmndrDesc());
    }

    public Notification updateNotification(long id, long apiary, long hive, long eventId,
                                           long rmndrType, String rmndrDesc) {
        ContentValues values = new ContentValues();
        // Needed b/c rows in this table can be linked to Apiary OR Hive
        values.put(COLUMN_NOTIFICATION_APIARY, (apiary < 1) ? null : apiary);
        values.put(COLUMN_NOTIFICATION_HIVE, (hive < 1) ? null : hive);
        values.put(COLUMN_NOTIFICATION_EVENT_ID, eventId);
        values.put(COLUMN_NOTIFICATION_RMNDR_TYPE, rmndrType);
        values.put(COLUMN_NOTIFICATION_RMNDR_DESC, rmndrDesc);
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
                aDO.getRmndrType(), aDO.getRmndrDesc());
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
        notification.setRmndrType(cursor.getInt(4));
        notification.setRmndrDesc(cursor.getString(5));

        return notification;
    }
}
