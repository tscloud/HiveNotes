package net.tscloud.hivenotes.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import java.util.TreeMap;

/**
 * Created by tscloud on 7/5/16.
 */
public abstract class GraphableDAO extends AbstactDAO {

    public static final String TAG = "GraphableDAO";

    public GraphableDAO(Context ctx) {
        super(ctx);
    }

    protected abstract String getTable();

    protected abstract String getColSnapshotDate();

    protected abstract String getColGraphKey();

    protected abstract Double scourToDouble(String aCol, Cursor aCur);

    /** The method responsible for getting the time based data out of whatever table
     *
     *   Return an TreeMap
     *   aKey will usually be apiary but could be hive or anything appropriate
     */
    public TreeMap<Long, Double> getColDataByDateRangeForGraphing(String aCol, long aStartDate,
                                                                  long aEndDate, long aKey)
            throws SQLException {

        Cursor cursor = mDatabase.query(getTable(), new String[] { getColSnapshotDate(), aCol },
                getColSnapshotDate() + " >= ? AND " + getColSnapshotDate() + " <= ? AND " +
                        getColGraphKey() + " = ?",
                new String[] { String.valueOf(aStartDate), String.valueOf(aEndDate), String.valueOf(aKey) },
                null, null, getColSnapshotDate() + " ASC");

        TreeMap<Long, Double> reply = new TreeMap<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    // need to call a method w/ knowledge of the individual column so that it can be
                    //  converted to Double
                    reply.put(cursor.getLong(0), scourToDouble(aCol, cursor));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }

        return reply;
    }

    /** Take the table name from the data to deliver the proper DAO - other check such as column
     *   existence can be made here
     */
    public static GraphableDAO getGraphableDAO(GraphableData aData, Context aCtx) {
        Log.d(TAG, "calling getGraphableDAO()");

        GraphableDAO reply = null;

        // case stmnt to determine proper DAO to deliver - a lot of this should be externalized
        switch (aData.getDirective()) {
            case "Weather":
                reply = new WeatherDAO(aCtx);
                break;
            case "WeatherHistory":
                reply = new WeatherHistoryDAO(aCtx);
                break;
        }

        return reply;
    }
}
