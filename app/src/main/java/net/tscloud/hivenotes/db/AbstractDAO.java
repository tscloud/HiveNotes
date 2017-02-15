package net.tscloud.hivenotes.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by tscloud on 7/9/16.
 */
public abstract class AbstractDAO {

    public static final String TAG = "AbstractDAO";

    protected SQLiteDatabase mDatabase;
    protected MyDBHandler mDbHelper;
    protected Context mContext;

    public AbstractDAO(Context context) {
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
}
