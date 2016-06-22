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
    public static final String COLUMN_WEATHER_APIARY = "apiary";
    public static final String COLUMN_WEATHER_SNAPSHOT_DATE = "snapshot_date";
    public static final String COLUMN_WEATHER_TEMPERATURE = "temperature";
    public static final String COLUMN_WEATHER_RAINFALL = "rainfall";
    public static final String COLUMN_WEATHER_PRESSURE = "pressure";
    public static final String COLUMN_WEATHER_WEATHER = "weather";
    public static final String COLUMN_WEATHER_WINDDIRECTION = "windDirection";
    public static final String COLUMN_WEATHER_WINDMPH = "windMPH";
    public static final String COLUMN_WEATHER_HUMIDITY = "humidity";
    public static final String COLUMN_WEATHER_DEWPOINT = "dewPoint";
    public static final String COLUMN_WEATHER_VISIBILITY = "visibility";
    public static final String COLUMN_WEATHER_SOLARRADIATION = "solarRadiation";
    public static final String COLUMN_WEATHER_UVINDEX = "uvIndex";
    public static final String COLUMN_WEATHER_POLLEN_COUNT = "pollen_count";
    public static final String COLUMN_WEATHER_POLLUTION = "pollution";
    public static final String COLUMN_WEATHER_DATA_ORIGIN = "data_origin";

    // Database fields
    private SQLiteDatabase mDatabase;
    private MyDBHandler mDbHelper;
    private Context mContext;
    private String[] mAllColumns = { COLUMN_WEATHER_ID, COLUMN_WEATHER_APIARY,
            COLUMN_WEATHER_SNAPSHOT_DATE, COLUMN_WEATHER_TEMPERATURE, COLUMN_WEATHER_RAINFALL,
            COLUMN_WEATHER_PRESSURE, COLUMN_WEATHER_WEATHER, COLUMN_WEATHER_WINDDIRECTION,
            COLUMN_WEATHER_WINDMPH, COLUMN_WEATHER_HUMIDITY, COLUMN_WEATHER_DEWPOINT,
            COLUMN_WEATHER_VISIBILITY, COLUMN_WEATHER_SOLARRADIATION, COLUMN_WEATHER_UVINDEX,
            COLUMN_WEATHER_POLLEN_COUNT, COLUMN_WEATHER_POLLUTION,COLUMN_WEATHER_DATA_ORIGIN };

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

    public Weather createWeather(long apiary, long snapshotDate, float temperature,
                                float rainfall, float pressure, String weather,
                                String windDirection, float windMPH, String humidity,
                                float dewPoint, String visibility, String solarRadiation,
                                String uvIndex, float pollenCount, String pollution) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_WEATHER_APIARY, apiary);
        values.put(COLUMN_WEATHER_SNAPSHOT_DATE, snapshotDate);
        values.put(COLUMN_WEATHER_TEMPERATURE, temperature);
        values.put(COLUMN_WEATHER_RAINFALL, rainfall);
        values.put(COLUMN_WEATHER_PRESSURE, pressure);
        values.put(COLUMN_WEATHER_WEATHER, weather);
        values.put(COLUMN_WEATHER_WINDDIRECTION, windDirection);
        values.put(COLUMN_WEATHER_WINDMPH, windMPH);
        values.put(COLUMN_WEATHER_HUMIDITY, humidity);
        values.put(COLUMN_WEATHER_DEWPOINT, dewPoint);
        values.put(COLUMN_WEATHER_VISIBILITY, visibility);
        values.put(COLUMN_WEATHER_SOLARRADIATION, solarRadiation);
        values.put(COLUMN_WEATHER_UVINDEX, uvIndex);
        values.put(COLUMN_WEATHER_POLLEN_COUNT, pollenCount);
        values.put(COLUMN_WEATHER_POLLUTION, pollution);
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

    public Weather createWeather(Weather aDO) {
        return createWeather(aDO.getApiary(), aDO.getSnapshotDate(), aDO.getTemperature(),
                aDO.getRainfall(), aDO.getPressure(), aDO.getWeather(),
                aDO.getWindDirection(), aDO.getWindMPH(), aDO.getHumidity(),
                aDO.getDewPoint(), aDO.getVisibility(), aDO.getSolarRadiation(),
                aDO.getUvIndex(), aDO.getPollenCount(), aDO.getPollution();
    }

    public Weather updateWeather(long id, long apiary, long snapshotDate, float temperature,
                                float rainfall, float pressure, String weather,
                                String windDirection, float windMPH, String humidity,
                                float dewPoint, String visibility, String solarRadiation,
                                String uvIndex, float pollenCount, String pollution) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_WEATHER_APIARY, apiary);
        values.put(COLUMN_WEATHER_SNAPSHOT_DATE, snapshotDate);
        values.put(COLUMN_WEATHER_TEMPERATURE, temperature);
        values.put(COLUMN_WEATHER_RAINFALL, rainfall);
        values.put(COLUMN_WEATHER_PRESSURE, pressure);
        values.put(COLUMN_WEATHER_WEATHER, weather);
        values.put(COLUMN_WEATHER_WINDDIRECTION, windDirection);
        values.put(COLUMN_WEATHER_WINDMPH, windMPH);
        values.put(COLUMN_WEATHER_HUMIDITY, humidity);
        values.put(COLUMN_WEATHER_DEWPOINT, dewPoint);
        values.put(COLUMN_WEATHER_VISIBILITY, visibility);
        values.put(COLUMN_WEATHER_SOLARRADIATION, solarRadiation);
        values.put(COLUMN_WEATHER_UVINDEX, uvIndex);
        values.put(COLUMN_WEATHER_POLLEN_COUNT, pollenCount);
        values.put(COLUMN_WEATHER_POLLUTION, pollution);
        int rowsUpdated = mDatabase.update(TABLE_WEATHER, values,
                COLUMN_WEATHER_ID + "=" + id, null);

        Weather updatedWeather = null;
        if (rowsUpdated > 0) {
            Cursor cursor = mDatabase.query(TABLE_WEATHER, mAllColumns,
                    COLUMN_WEATHER_ID + " = " + id, null, null, null, null);
            if (cursor.moveToFirst()) {
                updatedWeather = cursorToWeather(cursor);
            }
            cursor.close();
        }

        return updatedWeather;
    }

    public Weather updateWeather(Weather aDO) {
        return updateWeather(aDO.getId(), aDO.getApiary(), aDO.getSnapshotDate(),
                aDO.getTemperature(), aDO.getRainfall(), aDO.getPressure(),
                aDO.getWeather(), aDO.getWindDirection(), aDO.getWindMPH(),
                aDO.getHumidity(), aDO.getDewPoint(), aDO.getVisibility(),
                aDO.getSolarRadiation(), aDO.getUvIndex(), aDO.getPollenCount(),
                aDO.getPollution());
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
        weather.setSnapshotDate(cursor.getLong(1));
        weather.setTemperature(cursor.getFloat(2));
        weather.setRainfall(cursor.getFloat(3));
        weather.setPressure(cursor.getFloat(4));
        weather.setWeather(cursor.getString(5));
        weather.setWindDirection(cursor.getString(6));
        weather.setWindMPH(cursor.getFloat(7));
        weather.setHumidity(cursor.getString(8));
        weather.setDewPoint(cursor.getFloat(9));
        weather.setVisibility(cursor.getString(10));
        weather.setSolarRadiation(cursor.getString(11));
        weather.setUvIndex(cursor.getString(12));
        weather.setPollenCount(cursor.getFloat(13));
        weather.setPollution(cursor.getString(14));

        return weather;
    }
}
