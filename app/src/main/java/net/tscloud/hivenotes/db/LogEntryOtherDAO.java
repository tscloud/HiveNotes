package net.tscloud.hivenotes.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by tscloud on 11/16/15.
 */
public class LogEntryOtherDAO {

    public static final String TAG = "LogEntryOtherDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_LOGENTRYOTHER = "LogEntryOther";
    public static final String COLUMN_LOGENTRYOTHER_ID = "_id";
    public static final String COLUMN_LOGENTRYOTHER_HIVE = "hive";
    public static final String COLUMN_LOGENTRYOTHER_VISIT_DATE = "visit_date";
    public static final String COLUMN_LOGENTRYOTHER_REQUEEN = "requeen";
    public static final String COLUMN_LOGENTRYOTHER_REQUEEN_RMNDR = "requeen_rmndr";
    public static final String COLUMN_LOGENTRYOTHER_SWARM_RMNDR = "swarm_rmndr";
    public static final String COLUMN_LOGENTRYOTHER_SPLIT_HIVE_RMNDR = "split_hive_rmndr";

    // Database fields
    private SQLiteDatabase mDatabase;
    private MyDBHandler mDbHelper;
    private Context mContext;
    private String[] mAllColumns = {COLUMN_LOGENTRYOTHER_ID, COLUMN_LOGENTRYOTHER_HIVE,
            COLUMN_LOGENTRYOTHER_VISIT_DATE, COLUMN_LOGENTRYOTHER_REQUEEN, COLUMN_LOGENTRYOTHER_REQUEEN_RMNDR,
            COLUMN_LOGENTRYOTHER_SWARM_RMNDR, COLUMN_LOGENTRYOTHER_SPLIT_HIVE_RMNDR};

    public LogEntryOtherDAO(Context context) {
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
}
