package net.tscloud.hivenotes.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;

/**
 * Created by tscloud on 8/14/15.
 */
public class MyDBHandler extends SQLiteOpenHelper {

    private static MyDBHandler ourInstance;

    private static final String DATABASE_NAME = "hivenotes_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "MyDBHandler";
    private static final String CREATEFILE = "create.sql";
    private static final String SQL_DIR = "sql" ;
    private Context context;
    private SQLiteDatabase db;

    public static MyDBHandler getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new MyDBHandler(context.getApplicationContext());
        }
        return ourInstance;
    }

    private MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "create database");
            execSqlFile(CREATEFILE, db);
        } catch(IOException exception) {
            throw new RuntimeException("Database creation failed", exception);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "upgrade database");
        db.execSQL("select 'drop table ' || name || ';' from sqlite_master where type = 'table';");
        // recreate the tables

        onCreate(db);
    }

    protected void execSqlFile(String sqlFile, SQLiteDatabase db) throws SQLException, IOException {
        Log.d(TAG, "  exec sql file: " + sqlFile);
        for( String sqlInstruction : SqlParser.parseSqlFile(SQL_DIR + "/" + sqlFile, this.context.getAssets())) {
            Log.d(TAG, "    sql: " + sqlInstruction);
            db.execSQL(sqlInstruction);
        }
    }
}
