package net.tscloud.hivenotes.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.TreeMap;

/**
 * Created by tscloud on 7/5/16.
 */
public abstract class GraphableDAO extends AbstractDAO {

    public static final String TAG = "GraphableDAO";

    public GraphableDAO(Context ctx) {
        super(ctx);
    }

    protected abstract String getTable();

    protected abstract String getColGraphDate();

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

        Cursor cursor = mDatabase.query(getTable(), new String[] { getColGraphDate(), aCol },
                getColGraphDate() + " >= ? AND " + getColGraphDate() + " <= ? AND " +
                        getColGraphKey() + " = ?",
                new String[] { String.valueOf(aStartDate), String.valueOf(aEndDate),
                        String.valueOf(aKey) },
                null, null, getColGraphDate() + " ASC");

        TreeMap<Long, Double> reply = new TreeMap<>();
        Double retDouble = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    // need to call a method w/ knowledge of the individual
                    //  column so that it can be converted to Double
                    //1st need to check if query returned null
                    // if so => just throw out that value <- is this OK?
                    retDouble = scourToDouble(aCol, cursor);
                    if (retDouble != null) {
                        reply.put(cursor.getLong(0), retDouble);
                    }
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
    protected Double scourToDouble(String aCol, Cursor aCur) throws SQLException {
        Double reply = null;
        // For rounding
        int PLACES = 2;

        try {
            //--DEBUG--
            //Log.d(TAG, "aCur.getType(1): " + aCur.getType(1));
            // What we're interested in will always be at pos 1
            if (isSpecialCol(aCol)) {
                reply = processSpecialCol(aCur);
            }
            else if (aCur.getType(1) == Cursor.FIELD_TYPE_NULL) {
                // NOOP
            }
            else if (aCur.getType(1) == Cursor.FIELD_TYPE_STRING) {
                reply = new BigDecimal(Double.valueOf(aCur.getString(1)))
                        .setScale(PLACES, RoundingMode.HALF_UP).doubleValue();
            }
            else if (aCur.getType(1) == Cursor.FIELD_TYPE_FLOAT) {
                reply = new BigDecimal(Double.valueOf(aCur.getFloat(1)))
                    .setScale(PLACES, RoundingMode.HALF_UP).doubleValue();
            }
            else if (aCur.getType(1) == Cursor.FIELD_TYPE_INTEGER) {
                reply = new BigDecimal(Double.valueOf(aCur.getInt(1)))
                    .setScale(PLACES, RoundingMode.HALF_UP).doubleValue();
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
        return Arrays.asList(getSpecialCols()).contains(aCol);
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
            case "LogEntryProductivity":
                reply = new LogEntryProductivityDAO(aCtx);
                break;
        }

        return reply;
    }
}
