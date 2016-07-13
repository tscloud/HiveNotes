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
public class LogDateDAO extends AbstactDAO {

    public static final String TAG = "LogDateDAO";

    // compile time constant for big union query
    private static final String q = "SELECT VISIT_DATE FROM LogEntryGeneral " +
            "WHERE HIVE = ? " +
            "UNION " +
            "SELECT VISIT_DATE FROM LogEntryProductivity " +
            "WHERE HIVE = ? " +
            "UNION " +
            "SELECT VISIT_DATE FROM LogEntryPestMgmt " +
            "WHERE HIVE = ? " +
            "UNION " +
            "SELECT VISIT_DATE FROM LogEntryFeeding " +
            "WHERE HIVE = ? " +
            "UNION " +
            "SELECT VISIT_DATE FROM LogEntryOther " +
            "WHERE HIVE = ? " +
            "ORDER BY VISIT_DATE DESC";

    public LogDateDAO(Context context) {
        super(context);
    }

    // --DB access methods--

    public List<Long> getAllVisitDates(long aHiveId) {
        List<Long> reply = null;
        String hIdString = Long.toString(aHiveId);

        Cursor cursor = mDatabase.rawQuery(q,
                new String[]{hIdString, hIdString, hIdString, hIdString, hIdString});

        if (cursor != null) {
            reply = new ArrayList<Long>(cursor.getCount());
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    reply.add(cursor.getLong(0));
                    cursor.moveToNext();
                }
            }
            // make sure to close the cursor
            cursor.close();
        }

        return reply;
    }
}