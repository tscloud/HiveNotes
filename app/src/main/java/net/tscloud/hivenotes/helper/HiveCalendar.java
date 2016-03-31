package net.tscloud.hivenotes.helper;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by tscloud on 3/29/16.
 */
public class HiveCalendar {

    public static final String TAG = "HiveCalendar";

    private static final String ACCOUNT_NAME = "HiveNotes User";
    private static final String CALENDAR_NAME = "HiveNotes Calendar";
    /**The main/basic URI for the android calendars table*/
    private static final Uri CAL_URI = CalendarContract.Calendars.CONTENT_URI;

    /*
    Static methods used for creating Calendar -- Thank You Derek Bekoe
     */
    /**Creates the values the new calendar will have*/
    private static ContentValues buildNewCalContentValues() {
        final ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_NAME);
        cv.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        cv.put(CalendarContract.Calendars.NAME, CALENDAR_NAME);
        cv.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_NAME);
        cv.put(CalendarContract.Calendars.CALENDAR_COLOR, 0xEA8561);
        //user can only read the calendar
        cv.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_READ);
        cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, ACCOUNT_NAME);
        cv.put(CalendarContract.Calendars.VISIBLE, 1);
        cv.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        return cv;
    }

    /**Builds the Uri for your Calendar in android database (as a Sync Adapter)*/
    private static Uri buildCalUri() {
        return CAL_URI
                .buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE,
                        CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();
    }

    /**Create and insert new calendar into android database
     * @param aCtx The context (e.g. activity)
     */
    public static void createCalendar(Context aCtx) {
        ContentResolver cr = aCtx.getContentResolver();
        final ContentValues cv = buildNewCalContentValues();
        Uri calUri = buildCalUri();
        //insert the calendar into the database
        cr.insert(calUri, cv);
    }

    /*
    Launch the Calendar Intent for user to create the reminder
     will seed w/ certain values
     */
    public static void calendarIntent(Context aCtx, Bundle eventData) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");

        if (eventData != null) {
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventData.getLong("time"));
            intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);

            intent.putExtra(CalendarContract.Events.TITLE, eventData.getString("title"));
            intent.putExtra(CalendarContract.Events.DESCRIPTION, eventData.getString("desc"));
        }

        aCtx.startActivity(intent);
    }

    /*
    List all the visible Calendars
     */
    public static void listCalendars(Context aCtx) {
        String[] projection =
                new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.NAME,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.ACCOUNT_TYPE};

        Cursor calCursor;

        if (ActivityCompat.checkSelfPermission(aCtx, Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            calCursor =
                    aCtx.getContentResolver().
                            query(CalendarContract.Calendars.CONTENT_URI,
                                    projection,
                                    CalendarContract.Calendars.VISIBLE + " = 1",
                                    null,
                                    CalendarContract.Calendars._ID + " ASC");

            if (calCursor.moveToFirst()) {
                Log.d(TAG, "Calendar");
                Log.d(TAG, "--------");
                do {
                    long id = calCursor.getLong(0);
                    String displayName = calCursor.getString(1);
                    Log.d(TAG, "id: " + id);
                    Log.d(TAG, "displayName: " + displayName);
                } while (calCursor.moveToNext());
            }
            else {

            }
        }
    }

}
