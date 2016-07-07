package net.tscloud.hivenotes.db;

import android.content.Context;
import android.util.Log;

import java.util.TreeMap;

/**
 * Created by tscloud on 7/5/16.
 */
public abstract class GraphableDAO {

    public static final String TAG = "GraphableDAO";

    /** The method responsible for getting the time based data out of whatever table
     *
     *   Return an AbstractMap as implementation may use Hash or Tree (or other?)
     *   aKey will usually be apiary but could be hive or anything appropriate
     */
    public abstract TreeMap<Long, Double> getColDataByDateRangeForGraphing(String aCol, long aStartDate,
                                                                           long aEndDate, long aKey);

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
        }

        return reply;
    }
}
