package net.tscloud.hivenotes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by tscloud on 6/25/16.
 */
public class WeatherHistoryDAO extends GraphableDAO {
    public static final String TAG = "WeatherHistoryDAO";

    // Database table columns
    // columns of the WeatherHistory table
    public static final String TABLE_WEATHERHISTORY = "WeatherHistory";
    public static final String COLUMN_WEATHERHISTORY_ID = "_id";
    public static final String COLUMN_WEATHERHISTORY_APIARY = "apiary";
    public static final String COLUMN_WEATHERHISTORY_SNAPSHOT_DATE = "snapshot_date";
    public static final String COLUMN_WEATHERHISTORY_FOG = "fog";
    public static final String COLUMN_WEATHERHISTORY_RAIN = "rain";
    public static final String COLUMN_WEATHERHISTORY_SNOW = "snow";
    public static final String COLUMN_WEATHERHISTORY_THUNDER = "thunder";
    public static final String COLUMN_WEATHERHISTORY_HAIL = "hail";
    public static final String COLUMN_WEATHERHISTORY_MAXTEMPI = "maxtempi";
    public static final String COLUMN_WEATHERHISTORY_MINTEMPI = "mintempi";
    public static final String COLUMN_WEATHERHISTORY_MAXDEWPTI = "maxdewpti";
    public static final String COLUMN_WEATHERHISTORY_MINDEWPTI = "mindewpti";
    public static final String COLUMN_WEATHERHISTORY_MAXPRESSUREI = "maxpressurei";
    public static final String COLUMN_WEATHERHISTORY_MINPRESSUREI = "minpressurei";
    public static final String COLUMN_WEATHERHISTORY_MAXWSPDI = "maxwspdi";
    public static final String COLUMN_WEATHERHISTORY_MINWSPDI = "minwspdi";
    public static final String COLUMN_WEATHERHISTORY_MEANWDIRD = "meanwdird";
    public static final String COLUMN_WEATHERHISTORY_MAXHUMIDITY = "maxhumidity";
    public static final String COLUMN_WEATHERHISTORY_MINHUMIDITY = "minhumidity";
    public static final String COLUMN_WEATHERHISTORY_MAXVISI = "maxvisi";
    public static final String COLUMN_WEATHERHISTORY_MINVISI = "minvisi";
    public static final String COLUMN_WEATHERHISTORY_PRECIPI = "precipi";
    public static final String COLUMN_WEATHERHISTORY_COOLINGDEGREEDAYS = "coolingdegreedays";
    public static final String COLUMN_WEATHERHISTORY_HEATINGDEGREEDAYS = "heatingdegreedays";

    // Database fields
    private String[] mAllColumns = { COLUMN_WEATHERHISTORY_ID, COLUMN_WEATHERHISTORY_APIARY,
            COLUMN_WEATHERHISTORY_SNAPSHOT_DATE, COLUMN_WEATHERHISTORY_FOG, COLUMN_WEATHERHISTORY_RAIN,
            COLUMN_WEATHERHISTORY_SNOW, COLUMN_WEATHERHISTORY_THUNDER, COLUMN_WEATHERHISTORY_HAIL,
            COLUMN_WEATHERHISTORY_MAXTEMPI, COLUMN_WEATHERHISTORY_MINTEMPI, COLUMN_WEATHERHISTORY_MAXDEWPTI,
            COLUMN_WEATHERHISTORY_MINDEWPTI, COLUMN_WEATHERHISTORY_MAXPRESSUREI,
            COLUMN_WEATHERHISTORY_MINPRESSUREI, COLUMN_WEATHERHISTORY_MAXWSPDI,
            COLUMN_WEATHERHISTORY_MINWSPDI, COLUMN_WEATHERHISTORY_MEANWDIRD,
            COLUMN_WEATHERHISTORY_MAXHUMIDITY, COLUMN_WEATHERHISTORY_MINHUMIDITY,
            COLUMN_WEATHERHISTORY_MAXVISI, COLUMN_WEATHERHISTORY_MINVISI, COLUMN_WEATHERHISTORY_PRECIPI,
            COLUMN_WEATHERHISTORY_COOLINGDEGREEDAYS, COLUMN_WEATHERHISTORY_HEATINGDEGREEDAYS };

    // Columns that require special processing
    private String[] specialCols = {};

    // --constructor--
    public WeatherHistoryDAO(Context context) {
        super(context);
    }

    // --implement abstract--
    @Override
    protected String getTable() {
        return TABLE_WEATHERHISTORY;
    }

    @Override
    protected String getColGraphKey() {
        return COLUMN_WEATHERHISTORY_APIARY;
    }

    @Override
    protected String getColSnapshotDate() {
        return COLUMN_WEATHERHISTORY_SNAPSHOT_DATE;
    }

    @Override
    protected String[] getSpecialCols() {
        return specialCols;
    }

    @Override
    protected Double processSpecialCol(Cursor aCur) {
        // there are no special cols
        return null;
    }

    // --DB access methods--

    public WeatherHistory createWeatherHistory(long apiary, long snapshot_date, String fog, String rain,
                                               String snow, String thunder, String hail, String maxtempi,
                                               String mintempi, String maxdewpti, String mindewpti,
                                               String maxpressurei, String minpressurei, String maxwspdi,
                                               String minwspdi, String meanwdird, String maxhumidity,
                                               String minhumidity, String maxvisi, String minvisi,
                                               String precipi, String coolingdegreedays, String heatingdegreedays) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_WEATHERHISTORY_APIARY, apiary);
        values.put(COLUMN_WEATHERHISTORY_SNAPSHOT_DATE, snapshot_date);
        values.put(COLUMN_WEATHERHISTORY_FOG, fog);
        values.put(COLUMN_WEATHERHISTORY_RAIN, rain);
        values.put(COLUMN_WEATHERHISTORY_SNOW, snow);
        values.put(COLUMN_WEATHERHISTORY_THUNDER, thunder);
        values.put(COLUMN_WEATHERHISTORY_HAIL, hail);
        values.put(COLUMN_WEATHERHISTORY_MAXTEMPI, maxtempi);
        values.put(COLUMN_WEATHERHISTORY_MINTEMPI, mintempi);
        values.put(COLUMN_WEATHERHISTORY_MAXDEWPTI, maxdewpti);
        values.put(COLUMN_WEATHERHISTORY_MINDEWPTI, mindewpti);
        values.put(COLUMN_WEATHERHISTORY_MAXPRESSUREI, maxpressurei);
        values.put(COLUMN_WEATHERHISTORY_MINPRESSUREI, minpressurei);
        values.put(COLUMN_WEATHERHISTORY_MAXWSPDI, maxwspdi);
        values.put(COLUMN_WEATHERHISTORY_MINWSPDI, minwspdi);
        values.put(COLUMN_WEATHERHISTORY_MEANWDIRD, meanwdird);
        values.put(COLUMN_WEATHERHISTORY_MAXHUMIDITY, maxhumidity);
        values.put(COLUMN_WEATHERHISTORY_MINHUMIDITY, minhumidity);
        values.put(COLUMN_WEATHERHISTORY_MAXVISI, maxvisi);
        values.put(COLUMN_WEATHERHISTORY_MINVISI, minvisi);
        values.put(COLUMN_WEATHERHISTORY_PRECIPI, precipi);
        values.put(COLUMN_WEATHERHISTORY_COOLINGDEGREEDAYS, coolingdegreedays);
        values.put(COLUMN_WEATHERHISTORY_HEATINGDEGREEDAYS, heatingdegreedays);
        long insertId = mDatabase.insert(TABLE_WEATHERHISTORY, null, values);

        WeatherHistory newWeatherHistory = null;
        if (insertId >= 0) {
            Cursor cursor = mDatabase.query(TABLE_WEATHERHISTORY, mAllColumns,
                    COLUMN_WEATHERHISTORY_ID + " = " + insertId, null, null, null, null);
            if (cursor.moveToFirst()) {
                newWeatherHistory = cursorToWeatherHistory(cursor);
            }
            cursor.close();
        }

        return newWeatherHistory;
    }

    public WeatherHistory createWeatherHistory(WeatherHistory aDO) {
        return createWeatherHistory(aDO.getApiary(), aDO.getSnapshot_date(), aDO.getFog(), aDO.getRain(),
                aDO.getSnow(), aDO.getThunder(), aDO.getHail(), aDO.getMaxtempi(), aDO.getMintempi(),
                aDO.getMaxdewpti(), aDO.getMindewpti(), aDO.getMaxpressurei(), aDO.getMinpressurei(),
                aDO.getMaxwspdi(), aDO.getMinwspdi(), aDO.getMeanwdird(), aDO.getMaxhumidity(),
                aDO.getMinhumidity(), aDO.getMaxvisi(), aDO.getMinvisi(), aDO.getPrecipi(),
                aDO.getCoolingdegreedays(), aDO.getHeatingdegreedays());
    }

    public WeatherHistory updateWeatherHistory(long id, long apiary, long snapshot_date, String fog,
                                              String rain, String snow, String thunder, String hail,
                                              String maxtempi, String mintempi, String maxdewpti,
                                              String mindewpti, String maxpressurei, String minpressurei,
                                              String maxwspdi, String minwspdi, String meanwdird,
                                              String maxhumidity, String minhumidity, String maxvisi,
                                              String minvisi, String precipi, String coolingdegreedays,
                                              String heatingdegreedays) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_WEATHERHISTORY_APIARY, apiary);
        values.put(COLUMN_WEATHERHISTORY_SNAPSHOT_DATE, snapshot_date);
        values.put(COLUMN_WEATHERHISTORY_FOG, fog);
        values.put(COLUMN_WEATHERHISTORY_RAIN, rain);
        values.put(COLUMN_WEATHERHISTORY_SNOW, snow);
        values.put(COLUMN_WEATHERHISTORY_THUNDER, thunder);
        values.put(COLUMN_WEATHERHISTORY_HAIL, hail);
        values.put(COLUMN_WEATHERHISTORY_MAXTEMPI, maxtempi);
        values.put(COLUMN_WEATHERHISTORY_MINTEMPI, mintempi);
        values.put(COLUMN_WEATHERHISTORY_MAXDEWPTI, maxdewpti);
        values.put(COLUMN_WEATHERHISTORY_MINDEWPTI, mindewpti);
        values.put(COLUMN_WEATHERHISTORY_MAXPRESSUREI, maxpressurei);
        values.put(COLUMN_WEATHERHISTORY_MINPRESSUREI, minpressurei);
        values.put(COLUMN_WEATHERHISTORY_MAXWSPDI, maxwspdi);
        values.put(COLUMN_WEATHERHISTORY_MINWSPDI, minwspdi);
        values.put(COLUMN_WEATHERHISTORY_MEANWDIRD, meanwdird);
        values.put(COLUMN_WEATHERHISTORY_MAXHUMIDITY, maxhumidity);
        values.put(COLUMN_WEATHERHISTORY_MINHUMIDITY, minhumidity);
        values.put(COLUMN_WEATHERHISTORY_MAXVISI, maxvisi);
        values.put(COLUMN_WEATHERHISTORY_MINVISI, minvisi);
        values.put(COLUMN_WEATHERHISTORY_PRECIPI, precipi);
        values.put(COLUMN_WEATHERHISTORY_COOLINGDEGREEDAYS, coolingdegreedays);
        values.put(COLUMN_WEATHERHISTORY_HEATINGDEGREEDAYS, heatingdegreedays);
        int rowsUpdated = mDatabase.update(TABLE_WEATHERHISTORY, values,
                COLUMN_WEATHERHISTORY_ID + "=" + id, null);

        WeatherHistory updatedWeatherHistory = null;
        if (rowsUpdated > 0) {
            Cursor cursor = mDatabase.query(TABLE_WEATHERHISTORY, mAllColumns,
                    COLUMN_WEATHERHISTORY_ID + " = " + id, null, null, null, null);
            if (cursor.moveToFirst()) {
                updatedWeatherHistory = cursorToWeatherHistory(cursor);
            }
            cursor.close();
        }

        return updatedWeatherHistory;
    }

    public WeatherHistory updateWeatherHistory(WeatherHistory aDO) {
        return updateWeatherHistory(aDO.getId(), aDO.getApiary(), aDO.getSnapshot_date(), aDO.getFog(),
                aDO.getRain(), aDO.getSnow(), aDO.getThunder(), aDO.getHail(), aDO.getMaxtempi(),
                aDO.getMintempi(), aDO.getMaxdewpti(), aDO.getMindewpti(), aDO.getMaxpressurei(),
                aDO.getMinpressurei(), aDO.getMaxwspdi(), aDO.getMinwspdi(), aDO.getMeanwdird(),
                aDO.getMaxhumidity(), aDO.getMinhumidity(), aDO.getMaxvisi(), aDO.getMinvisi(),
                aDO.getPrecipi(), aDO.getCoolingdegreedays(), aDO.getHeatingdegreedays());
    }

    public void deleteWeatherHistory(WeatherHistory weatherHistory) {
        long id = weatherHistory.getId();
        mDatabase.delete(TABLE_WEATHERHISTORY, COLUMN_WEATHERHISTORY_ID + " = " + id, null);
    }

    public WeatherHistory getWeatherHistoryById(long id) {
        Cursor cursor = mDatabase.query(TABLE_WEATHERHISTORY, mAllColumns,
                COLUMN_WEATHERHISTORY_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null);

        WeatherHistory retrievedWeatherHistory = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                retrievedWeatherHistory = cursorToWeatherHistory(cursor);
            }
            cursor.close();
        }

        return cursorToWeatherHistory(cursor);
    }

    public void createWeatherHistorySet(ArrayList<WeatherHistory> aDOSet) {
        // do multiple insertions under transaction - or not
        for (WeatherHistory w : aDOSet) {
            createWeatherHistory(w);
        }
    }

    protected WeatherHistory cursorToWeatherHistory(Cursor cursor) {
        WeatherHistory weatherHistory = new WeatherHistory();
        weatherHistory.setId(cursor.getLong(0));
        weatherHistory.setApiary(cursor.getLong(1));
        weatherHistory.setSnapshot_date(cursor.getLong(2));
        weatherHistory.setFog(cursor.getString(3));
        weatherHistory.setRain(cursor.getString(4));
        weatherHistory.setSnow(cursor.getString(5));
        weatherHistory.setThunder(cursor.getString(6));
        weatherHistory.setHail(cursor.getString(7));
        weatherHistory.setMaxtempi(cursor.getString(8));
        weatherHistory.setMintempi(cursor.getString(9));
        weatherHistory.setMaxdewpti(cursor.getString(10));
        weatherHistory.setMindewpti(cursor.getString(11));
        weatherHistory.setMaxpressurei(cursor.getString(12));
        weatherHistory.setMinpressurei(cursor.getString(13));
        weatherHistory.setMaxwspdi(cursor.getString(14));
        weatherHistory.setMinwspdi(cursor.getString(15));
        weatherHistory.setMeanwdird(cursor.getString(16));
        weatherHistory.setMaxhumidity(cursor.getString(17));
        weatherHistory.setMinhumidity(cursor.getString(18));
        weatherHistory.setMaxvisi(cursor.getString(19));
        weatherHistory.setMinvisi(cursor.getString(20));
        weatherHistory.setPrecipi(cursor.getString(21));
        weatherHistory.setCoolingdegreedays(cursor.getString(22));
        weatherHistory.setHeatingdegreedays(cursor.getString(23));

        return weatherHistory;
    }

}
