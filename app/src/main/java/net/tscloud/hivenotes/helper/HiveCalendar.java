package net.tscloud.hivenotes.helper;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
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

import java.util.TimeZone;

/**
 * Created by tscloud on 3/29/16.
 */
public class HiveCalendar {

    public static final String TAG = "HiveCalendar";

    private static final String ACCOUNT_NAME = "HiveNotes User";
    private static final String CALENDAR_NAME = "HiveNotes Calendar";

    /**The main/basic URI for the android calendars table*/
    private static final Uri CAL_URI = CalendarContract.Calendars.CONTENT_URI;
    private static long CAL_ID = -1;

    /**The main/basic URI for the android events table*/
    private static final Uri EVENT_URI = CalendarContract.Events.CONTENT_URI;
    private static long EVENT_ID = -1;

    //
    //Static methods used for creating Calendar -- Thank You Derek Bekoe
    //

    /**Launch the Calendar Intent for user to create the reminder will seed w/ certain values*/
    private static void calendarIntent(Context aCtx, Bundle eventData) {
        Intent intent = new Intent(Intent.ACTION_INSERT);

        if (eventData != null) {
            // do I want to do setType or...
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventData.getLong("time"));
            intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);

            intent.putExtra(CalendarContract.Events.TITLE, eventData.getString("title"));
            intent.putExtra(CalendarContract.Events.DESCRIPTION, eventData.getString("desc"));
        }
        else {
            // ...setData???
            //intent.setData(CalendarContract.Events.CONTENT_URI);
            intent.setData(ContentUris.withAppendedId(buildUri(CAL_URI), CAL_ID));
        }

        aCtx.startActivity(intent);
    }

    /**Builds the Uri in android database (as a Sync Adapter)*/
    private static Uri buildUri(Uri aUri) {
        return aUri
                .buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE,
                        CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();
    }

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

    /**Create and insert new calendar into android database
     * @param aCtx The context (e.g. activity)
     */
    private static long createCalendar(Context aCtx) {
        ContentResolver cr = aCtx.getContentResolver();
        final ContentValues cv = buildNewCalContentValues();
        Uri calUri = buildUri(CAL_URI);
        //insert the calendar into the database
        Uri newUri = cr.insert(calUri, cv);
        return Long.parseLong(newUri.getLastPathSegment());
    }

    /**Permanently deletes our calendar from database (along with all events)*/
    private static void deleteCalendar(Context aCtx, long aCalId) {
        ContentResolver cr = aCtx.getContentResolver();
        Uri calUri = ContentUris.withAppendedId(buildUri(CAL_URI), aCalId);
        cr.delete(calUri, null, null);
    }

    /**Create event - Add an event to our calendar
     * @param dtstart Event start time (in millis)
     * @param dtend Event end time (in millis)
     */
    private static long addEvent(Context aCtx, long aCalId, String title, String description,
                                 String location, long dtstart, long dtend) {
        ContentResolver cr = aCtx.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Events.CALENDAR_ID, aCalId);
        cv.put(CalendarContract.Events.TITLE, title);
        cv.put(CalendarContract.Events.DTSTART, dtstart);
        cv.put(CalendarContract.Events.DTEND, dtend);
        cv.put(CalendarContract.Events.EVENT_LOCATION, location);
        cv.put(CalendarContract.Events.DESCRIPTION, description);
        //cv.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");
        cv.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());
        Uri newUri = cr.insert(buildUri(EVENT_URI), cv);
        return Long.parseLong(newUri.getLastPathSegment());
    }

    /**Finds an event based on the ID
     * @param ctx The context (e.g. activity)
     * @param id The id of the event to be found
     */
    private static Bundle getEventByID(Context ctx, long id) {
        Bundle eventData = null;
        ContentResolver cr = ctx.getContentResolver();
        //Projection array for query (the values you want)
        final String[] PROJECTION = new String[] {
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
        };
        final int ID_INDEX = 0, TITLE_INDEX = 1, DESC_INDEX = 2, LOCATION_INDEX = 3,
                START_INDEX = 4, END_INDEX = 5;
        long start_millis=0, end_millis=0;
        String title=null, description=null, location=null;
        final String selection = "("+ CalendarContract.Events.OWNER_ACCOUNT+" = ? AND "+ CalendarContract.Events._ID+" = ?)";
        final String[] selectionArgs = new String[] {ACCOUNT_NAME, id+""};
        Cursor cursor = cr.query(buildUri(EVENT_URI), PROJECTION, selection, selectionArgs, null);
        //at most one event will be returned because event ids are unique in the table
        if (cursor.moveToFirst()) {
            eventData = new Bundle(6);
            id = cursor.getLong(ID_INDEX);
            eventData.putLong("id", id);
            title = cursor.getString(TITLE_INDEX);
            eventData.putString("title", title);
            description = cursor.getString(DESC_INDEX);
            eventData.putString("description", description);
            location = cursor.getString(LOCATION_INDEX);
            eventData.putString("location", location);
            start_millis = cursor.getLong(START_INDEX);
            eventData.putLong("start_millis", start_millis);
            end_millis = cursor.getLong(END_INDEX);
            eventData.putLong("end_millis", end_millis);

            //do something with the values...
            Log.d(TAG, "Event");
            Log.d(TAG, "--------");
            Log.d(TAG, "id: " + id);
            Log.d(TAG, "title: " + title);
            Log.d(TAG, "description: " + description);
            Log.d(TAG, "location: " + location);
            Log.d(TAG, "start_millis: " + start_millis);
            Log.d(TAG, "end_millis: " + end_millis);
        }
        cursor.close();

        return eventData;
    }

    public static long getEventTime(Context aCtx, long id) {
        long reply = 0;
        Bundle data = getEventByID(aCtx, id);

        return data.getLong("start_millis");
    }

    /**List all the visible Calendars - and optionally delete them all*/
    public static void listCalendars(Context aCtx, boolean del) {
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

            Log.d(TAG, "Calendar");
            Log.d(TAG, "--------");
            if (calCursor.moveToFirst()) {
                do {
                    long id = calCursor.getLong(0);
                    String displayName = calCursor.getString(1);
                    Log.d(TAG, "id: " + id);
                    Log.d(TAG, "displayName: " + displayName);
                    if (del) {
                        deleteCalendar(aCtx, id);
                    }
                } while (calCursor.moveToNext());
            }
            else {
                Log.d(TAG, "No Calendars found");
            }
        }
    }

    /**Find the Calendar that we need*/
    private static long findCalendar(Context aCtx) {
        // Calendar ID that we're interested in
        long result = -1;

        final String[] projection =
                new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.NAME,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.ACCOUNT_TYPE};

        final String selection = "("+ CalendarContract.Calendars.ACCOUNT_NAME+" = ? AND "+
                                        CalendarContract.Calendars.NAME+" = ?)";

        final String[] selectionArgs = new String[] {ACCOUNT_NAME, CALENDAR_NAME};

        Cursor calCursor;

        if (ActivityCompat.checkSelfPermission(aCtx, Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            calCursor =
                    aCtx.getContentResolver().
                            query(CalendarContract.Calendars.CONTENT_URI,
                                    projection,
                                    selection,
                                    selectionArgs,
                                    null);

            Log.d(TAG, "Calendar");
            Log.d(TAG, "--------");
            if (calCursor.moveToFirst()) {
                do {
                    result = calCursor.getLong(0);
                    String displayName = calCursor.getString(1);
                    Log.d(TAG, "id: " + result);
                    Log.d(TAG, "displayName: " + displayName);
                } while (calCursor.moveToNext());
            }
            else {
                Log.d(TAG, "No Calendars found");
            }
        }

        return result;
    }

    /**public method to create an event*/
    public static long addEntryPublic(Context aCtx, long aEventTime, String aTitle,
            String aDesc, String aLoc) {
        long calId = -1;
        long eventId = -1;

        // Find proper Calendar
        calId = findCalendar(aCtx);

        if (calId == -1) {
            calId = createCalendar(aCtx);
            listCalendars(aCtx, false);
        }
        eventId = addEvent(aCtx, calId, aTitle, aDesc, aLoc, aEventTime, aEventTime);
        getEventByID(aCtx, eventId);

        return eventId;
    }

}
