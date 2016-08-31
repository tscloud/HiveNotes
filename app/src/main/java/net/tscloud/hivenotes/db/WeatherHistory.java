package net.tscloud.hivenotes.db;

import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by tscloud on 6/25/16.
 */
public class WeatherHistory {

    public static final String TAG = "WeatherHistory";

    private long id;
    private long apiary;
    private long snapshotDate;
    private String fog;
    private String rain;
    private String snow;
    private String thunder;
    private String hail;
    private String maxtempi;
    private String mintempi;
    private String maxdewpti;
    private String mindewpti;
    private String maxpressurei;
    private String minpressurei;
    private String maxwspdi;
    private String minwspdi;
    private String meanwdird;
    private String maxhumidity;
    private String minhumidity;
    private String maxvisi;
    private String minvisi;
    private String precipi;
    private String coolingdegreedays;
    private String heatingdegreedays;

    public long getApiary() {
        return apiary;
    }

    public void setApiary(long apiary) {
        this.apiary = apiary;
    }

    public String getCoolingdegreedays() {
        return coolingdegreedays;
    }

    public void setCoolingdegreedays(String coolingdegreedays) {
        this.coolingdegreedays = coolingdegreedays;
    }

    public String getFog() {
        return fog;
    }

    public void setFog(String fog) {
        this.fog = fog;
    }

    public String getHail() {
        return hail;
    }

    public void setHail(String hail) {
        this.hail = hail;
    }

    public String getHeatingdegreedays() {
        return heatingdegreedays;
    }

    public void setHeatingdegreedays(String heatingdegreedays) {
        this.heatingdegreedays = heatingdegreedays;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMaxdewpti() {
        return maxdewpti;
    }

    public void setMaxdewpti(String maxdewpti) {
        this.maxdewpti = maxdewpti;
    }

    public String getMaxhumidity() {
        return maxhumidity;
    }

    public void setMaxhumidity(String maxhumidity) {
        this.maxhumidity = maxhumidity;
    }

    public String getMaxpressurei() {
        return maxpressurei;
    }

    public void setMaxpressurei(String maxpressurei) {
        this.maxpressurei = maxpressurei;
    }

    public String getMaxtempi() {
        return maxtempi;
    }

    public void setMaxtempi(String maxtempi) {
        this.maxtempi = maxtempi;
    }

    public String getMaxvisi() {
        return maxvisi;
    }

    public void setMaxvisi(String maxvisi) {
        this.maxvisi = maxvisi;
    }

    public String getMaxwspdi() {
        return maxwspdi;
    }

    public void setMaxwspdi(String maxwspdi) {
        this.maxwspdi = maxwspdi;
    }

    public String getMeanwdird() {
        return meanwdird;
    }

    public void setMeanwdird(String meanwdird) {
        this.meanwdird = meanwdird;
    }

    public String getMindewpti() {
        return mindewpti;
    }

    public void setMindewpti(String mindewpti) {
        this.mindewpti = mindewpti;
    }

    public String getMinhumidity() {
        return minhumidity;
    }

    public void setMinhumidity(String minhumidity) {
        this.minhumidity = minhumidity;
    }

    public String getMinpressurei() {
        return minpressurei;
    }

    public void setMinpressurei(String minpressurei) {
        this.minpressurei = minpressurei;
    }

    public String getMintempi() {
        return mintempi;
    }

    public void setMintempi(String mintempi) {
        this.mintempi = mintempi;
    }

    public String getMinvisi() {
        return minvisi;
    }

    public void setMinvisi(String minvisi) {
        this.minvisi = minvisi;
    }

    public String getMinwspdi() {
        return minwspdi;
    }

    public void setMinwspdi(String minwspdi) {
        this.minwspdi = minwspdi;
    }

    public String getPrecipi() {
        return precipi;
    }

    public void setPrecipi(String precipi) {
        this.precipi = precipi;
    }

    public String getRain() {
        return rain;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public long getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(long snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public String getSnow() {
        return snow;
    }

    public void setSnow(String snow) {
        this.snow = snow;
    }

    public String getThunder() {
        return thunder;
    }

    public void setThunder(String thunder) {
        this.thunder = thunder;
    }

    /** Take a DB column name and return the DO data as Double
     *   Needed by graphing routines
     */
    //make a big case statement <- crappy
    public Double getCol(String aColName) {
        Double reply = null;
        // For rounding
        int PLACES = 2;

        try {
            switch (aColName) {
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_FOG:
                    reply = Double.valueOf(getFog());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_RAIN:
                    reply = Double.valueOf(getRain());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_SNOW:
                    reply = Double.valueOf(getSnow());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_THUNDER:
                    reply = Double.valueOf(getThunder());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_HAIL:
                    reply = Double.valueOf(getHail());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_MAXTEMPI:
                    reply = Double.valueOf(getMaxtempi());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_MINTEMPI:
                    reply = Double.valueOf(getMintempi());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_MAXDEWPTI:
                    reply = Double.valueOf(getMaxdewpti());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_MINDEWPTI:
                    reply = Double.valueOf(getMindewpti());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_MAXPRESSUREI:
                    reply = Double.valueOf(getMaxpressurei());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_MINPRESSUREI:
                    reply = Double.valueOf(getMinpressurei());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_MAXWSPDI:
                    reply = Double.valueOf(getMaxwspdi());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_MINWSPDI:
                    reply = Double.valueOf(getMinwspdi());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_MEANWDIRD:
                    reply = Double.valueOf(getMeanwdird());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_MAXHUMIDITY:
                    reply = Double.valueOf(getMaxhumidity());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_MINHUMIDITY:
                    reply = Double.valueOf(getMinhumidity());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_MAXVISI:
                    reply = Double.valueOf(getMaxvisi());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_MINVISI:
                    reply = Double.valueOf(getMinvisi());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_PRECIPI:
                    reply = Double.valueOf(getPrecipi());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_COOLINGDEGREEDAYS:
                    reply = Double.valueOf(getCoolingdegreedays());
                    break;
                case WeatherHistoryDAO.COLUMN_WEATHERHISTORY_HEATINGDEGREEDAYS:
                    reply = Double.valueOf(getHeatingdegreedays());
                    break;
            }

            //round please: needed for graphing
            if (reply != null) {
                reply = new BigDecimal(reply).setScale(PLACES, RoundingMode.HALF_UP).doubleValue();
            }
        }

        catch (NumberFormatException e) {
            Log.d(TAG, "Data trying to be returned from getCol() cannot be converted to Double");
        }

        return reply;
    }

}
