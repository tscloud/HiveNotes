package net.tscloud.hivenotes.helper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by tscloud on 3/29/16.
 */
public class HiveCalendar {

    public static final String TAG = "HiveCalendar";

    private Context mCtx;
    private static final String MY_ACCOUNT_NAME = "HiveNotes User";

    public HiveCalendar(Context c) {
        mCtx = c;
    }

    public void listCalendars() {
        String[] projection =
                new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.NAME,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.ACCOUNT_TYPE};

        Cursor calCursor;

        if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            calCursor =
                    mCtx.getContentResolver().
                            query(CalendarContract.Calendars.CONTENT_URI,
                                    projection,
                                    CalendarContract.Calendars.VISIBLE + " = 1",
                                    null,
                                    CalendarContract.Calendars._ID + " ASC");

            if (calCursor.moveToFirst()) {
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

    private void createCalendar() {

    }

}
