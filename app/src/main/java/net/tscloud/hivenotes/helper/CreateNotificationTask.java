package net.tscloud.hivenotes.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import net.tscloud.hivenotes.db.Notification;
import net.tscloud.hivenotes.db.NotificationDAO;
import net.tscloud.hivenotes.db.NotificationType;

/**
 * Created by tscloud on 4/7/17.
 */

public abstract class CreateNotificationTask extends AsyncTask<Void, Void, Void> {

    public final String TAG = "CreateNotificationTask";

    protected Context mCtx;
    protected int mTaskInd;

    public CreateNotificationTask(Context aCtx, int aTaskInd) {
        mCtx = aCtx;
        mTaskInd = aTaskInd;

        Log.d(TAG, "CreateNotificationTask("+ Thread.currentThread().getId() + ") : constructor");
    }

    @Override
    protected Void doInBackground(Void... unused) {
        Log.d(TAG, "CreateNotificationTask(" + Thread.currentThread().getId() + ") : doInBackground");

        return(null);
    }

    protected long createNotification(long aStartTime, int aNotType, String aEventDesc) {
        Log.d(TAG, "in createNotification()");

        long notificationId = -1;
        long eventId = -1;

        // Do the Notification magic
        Notification wNot;
        NotificationDAO wNotDAO = new NotificationDAO(mCtx);

        // read Notification by Type and Hive Id
        Log.d(TAG, "getNotificationByTypeAndHive(): aNotType:" + aNotType + " aHiveKey:" + getHiveId());
        wNot = wNotDAO.getNotificationByTypeAndHive(aNotType, getHiveId());

        // delete the corresponding Event <- ** handle errors **
        // we are doing this indiscriminately as we have to read the Event anyway to determine if
        // the times match and we can skip the update
        if ((wNot != null) && (wNot.getEventId() > 0)) {
            Log.d(TAG, "deleteEvent(): wNot.getEventId():" + wNot.getEventId());
            HiveCalendar.deleteEvent(mCtx, wNot.getEventId());
        }

        // Notification/Event Desc -
        //  being persisted w/ Event in Calendar
        String locEventDesc = NotificationType.getDesc(aNotType);
        //  being persisted w/ Notification <-- should be null if user did not
        //   specify
        String locNotDesc = null;

        if (aStartTime > -1) {
            // figure out event description
            if (aEventDesc != null) {
                locEventDesc = aEventDesc;
                locNotDesc = aEventDesc;
            }
            // create new Event - hardcode endtime
            eventId = HiveCalendar.addEntryPublic(mCtx,
                    aStartTime,
                    aStartTime+600000,
                    NotificationType.NOTIFICATION_TITLE,
                    locEventDesc,
                    getHiveName());
        }

        // create/update/delete Notification
        if (wNot == null) {
            // we don't have a Notification -> make a new one
            Log.d(TAG, "createNotification(): eventId:" + eventId);
            wNot = wNotDAO.createNotification(-1, getHiveId(), eventId, aNotType,
                    locEventDesc);
        } else if (aStartTime > -1){
            // we already have a Notification -> update it w/ new event id
            Log.d(TAG, "updateNotification(): eventId:" + eventId);
            wNot.setEventId(eventId);
            wNot.setRmndrDesc(locNotDesc);
            wNot = wNotDAO.updateNotification(wNot);
        }
        else {
            // we want to delete the Notification
            Log.d(TAG, "updateNotification(): eventId:" + eventId);
            wNotDAO.deleteNotification(wNot);
        }

        // return the Notification Id
        if (wNot != null) {
            notificationId = wNot.getId();
        }

        Log.d(TAG, "return: notificationId:" + notificationId);

        return notificationId;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        nullifyTaskRef(mTaskInd);
    }

    protected abstract long getHiveId();

    protected abstract String getHiveName();

    protected abstract void nullifyTaskRef(int taskRef);
}
