package net.tscloud.hivenotes.db;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tscloud on 6/26/16.
 */
public class GraphableDataDAO extends AbstractDAO {
    public static final String TAG = "GraphableDataDAO";

    // Database table columns
    // columns of the WeatherHistory table
    public static final String TABLE_GRAPHABLEDATA = "GraphableData";
    public static final String COLUMN_GRAPHABLEDATA_ID = "_id";
    public static final String COLUMN_GRAPHABLEDATA_DIRECTIVE = "directive";
    public static final String COLUMN_GRAPHABLEDATA_COLUMN = "column";
    public static final String COLUMN_GRAPHABLEDATA_PRETTYNAME = "pretty_name";
    public static final String COLUMN_GRAPHABLEDATA_CATEGORY = "category";
    public static final String COLUMN_GRAPHABLEDATA_KEYLEVEL = "key_level";

    // Database fields
    private String[] mAllColumns = {COLUMN_GRAPHABLEDATA_ID, COLUMN_GRAPHABLEDATA_DIRECTIVE,
            COLUMN_GRAPHABLEDATA_COLUMN, COLUMN_GRAPHABLEDATA_PRETTYNAME,
            COLUMN_GRAPHABLEDATA_CATEGORY, COLUMN_GRAPHABLEDATA_KEYLEVEL };

    // --constructor--
    public GraphableDataDAO(Context context) {
        super(context);
    }

    // --DB access methods--

    public List<GraphableData> getGraphableDataList() {
        List<GraphableData> listData = new ArrayList<>();

        Cursor cursor = mDatabase.query(TABLE_GRAPHABLEDATA, mAllColumns, null, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    GraphableData data = cursorToGraphableData(cursor);
                    listData.add(data);
                    cursor.moveToNext();
                }
            }
            // make sure to close the cursor
            cursor.close();
        }

        return listData;
    }

    protected GraphableData cursorToGraphableData(Cursor cursor) {
        GraphableData data = new GraphableData();
        data.setId(cursor.getLong(0));
        data.setDirective(cursor.getString(1));
        data.setColumn(cursor.getString(2));
        data.setPrettyName(cursor.getString(3));
        data.setCategory(cursor.getString(4));
        data.setKeyLevel(cursor.getString(5));

        return data;
    }
}
