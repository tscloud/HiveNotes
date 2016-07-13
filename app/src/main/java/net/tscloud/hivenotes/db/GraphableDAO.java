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

    protected abstract String[] getSpecialCols();

    protected abstract Double processSpecialCol(Cursor aCur);

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
                new String[] { String.valueOf(aStartDate), String.valueOf(aEndDate),
                        String.valueOf(aKey) },
                null, null, getColSnapshotDate() + " ASC");

        TreeMap<Long, Double> reply = new TreeMap<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    // need to call a method w/ knowledge of the individual
                    //  column so that it can be converted to Double
                    reply.put(cursor.getLong(0), scourToDouble(aCol, cursor));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }

        return reply;
    }

    /** With knowledge of each column, we can return Double properly
     *  IMPORTANT: if cols added or col types changed => this method MUST change
     *  in kind
     */
    @Override
    protected Double scourToDouble(String aCol, Cursor aCur) throws SQLException {
        Double reply = null;

        try {
            // What we're interested in will always be at pos 1
            if (isSpecialCol(aCol)) {
                reply = processSpecialCol(aCur);
            }
            else if (aCur.getType(1) == Cursor.FIELD_TYPE_STRING) {
                reply = Double.valueOf(aCur.getString(1));
            }
            else if (aCur.getType(1) == Cursor.FIELD_TYPE_FLOAT) {
                reply = Double.valueOf(aCur.getFloat(1));
            }
            else if (aCur.getType(1) == Cursor.FIELD_TYPE_INTEGER) {
                reply = Double.valueOf(aCur.getInt(1));
            }
            else {
                // col not found - weird?
                throw new SQLException("Table: " + getTable() + " : Column: " +
                    aCol + " unexpected datatype found");
            }
        }
        catch (NumberFormatException e) {
            Log.d(TAG, "Unexpected data/datatype found");
        }

        return reply;
    }

    protected boolean isSpecialCol(String aCol) {
        return Arrays.asList(getSpecialCols()).contains(aCol));
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
