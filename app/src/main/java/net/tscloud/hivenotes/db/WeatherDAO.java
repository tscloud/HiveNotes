package net.tscloud.hivenotes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by tscloud on 8/15/15.
 */
public class WeatherDAO {

    public static final String TAG = "WeatherDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_WEATHER = "Weather";
    public static final String COLUMN_WEATHER_ID = "_id";
    public static final String COLUMN_WEATHER_SNAPSHOT_DATE = "snapshot_date";
    public static final String COLUMN_WEATHER_TEMPERATURE = "temperature";
    public static final String COLUMN_WEATHER_RAINFALL = "rainfall";
    public static final String COLUMN_WEATHER_POLLEN_COUNT = "pollen_count";
    public static final String COLUMN_WEATHER_POLLUTION = "pollution";

    // Database fields
    private SQLiteDatabase mDatabase;
    private MyDBHandler mDbHelper;
    private Context mContext;
    private String[] mAllColumns = { COLUMN_WEATHER_ID, COLUMN_WEATHER_SNAPSHOT_DATE, COLUMN_WEATHER_TEMPERATURE,
            COLUMN_WEATHER_RAINFALL, COLUMN_WEATHER_POLLEN_COUNT, COLUMN_WEATHER_POLLUTION };

    public WeatherDAO(Context context) {
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

    public Weather createWeather(long hive, String alarm_uri) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_WEATHER_SNAPSHOT_DATE, hive);
        values.put(COLUMN_WEATHER_TEMPERATURE, hive);
        values.put(COLUMN_WEATHER_RAINFALL, hive);
        values.put(COLUMN_WEATHER_POLLEN_COUNT, hive);
        values.put(COLUMN_WEATHER_POLLUTION, hive);
        long insertId = mDatabase.insert(TABLE_WEATHER, null, values);

        Weather newWeather = null;
        if (insertId >= 0) {
            Cursor cursor = mDatabase.query(TABLE_WEATHER, mAllColumns,
                    COLUMN_WEATHER_ID + " = " + insertId, null, null, null, null);
            if (cursor.moveToFirst()) {
                newWeather = cursorToWeather(cursor);
            }
            cursor.close();
        }

        return newWeather;
    }

    public void deleteWeather(Weather weather) {
        long id = weather.getId();
        mDatabase.delete(TABLE_WEATHER, COLUMN_WEATHER_ID + " = " + id, null);
    }

    public Weather getWeatherById(long id){
        Cursor cursor = mDatabase.query(TABLE_WEATHER, mAllColumns,
                COLUMN_WEATHER_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null);

        Weather retrievedWeather = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                retrievedWeather = cursorToWeather(cursor);
            }
            cursor.close();
        }

        return cursorToWeather(cursor);
    }

    protected Weather cursorToWeather(Cursor cursor) {
        Weather weather = new Weather();
        weather.setId(cursor.getLong(0));
        weather.setSnapshotDate(cursor.getString(1));
        weather.setTemperature(cursor.getString(2));
        weather.setRainfall(cursor.getString(3));
        weather.setPollenCount(cursor.getString(4));
        weather.setPollution(cursor.getString(5));

        return weather;
    }
}
