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
                Log.d(TAG, "Calendar" + id);
                Log.d(TAG, "--------" + id);
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

    public calendarIntent(Bundle eventData) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");

        if (eventData != null) {
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventData.getLong("time"));
            intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);

            intent.putExtra(Events.TITLE, eventData.getString("title"));
            intent.putExtra(Events.DESCRIPTION, eventData.getString("desc"));
        }

        startActivity(intent);
    }

}
