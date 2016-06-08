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
public class LogDateDAO {

    public static final String TAG = "LogDateDAO";

    // Database fields
    private SQLiteDatabase mDatabase;
    private MyDBHandler mDbHelper;
    private Context mContext;

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
            "WHERE HIVE = ?";

    public LogDateDAO(Context context) {
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