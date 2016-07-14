package net.tscloud.hivenotes.db;

import java.util.HashMap;

/**
 * Created by tscloud on 6/25/16.
 */
public class WeatherHistory {

    public static final String TAG = "WeatherHistory";

    private long id;
    private long apiary;
    private long snapshot_date;
    private long fog;
    private long rain;
    private long snow;
    private long thunder;
    private long hail;
    private long maxtempi;
    private long mintempi;
    private long maxdewpti;
    private long mindewpti;
    private float maxpressurei;
    private float minpressurei;
    private long maxwspdi;
    private long minwspdi;
    private long meanwdird;
    private long maxhumidity;
    private long minhumidity;
    private float maxvisi;
    private float minvisi;
    private float precipi;
    private long coolingdegreedays;
    private long heatingdegreedays;

    public long getApiary() {
        return apiary;
    }

    public void setApiary(long apiary) {
        this.apiary = apiary;
    }

    public long getCoolingdegreedays() {
        return coolingdegreedays;
    }

    public void setCoolingdegreedays(long coolingdegreedays) {
        this.coolingdegreedays = coolingdegreedays;
    }

    public long getFog() {
        return fog;
    }

    public void setFog(long fog) {
        this.fog = fog;
    }

    public long getHail() {
        return hail;
    }

    public void setHail(long hail) {
        this.hail = hail;
    }

    public long getHeatingdegreedays() {
        return heatingdegreedays;
    }

    public void setHeatingdegreedays(long heatingdegreedays) {
        this.heatingdegreedays = heatingdegreedays;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMaxdewpti() {
        return maxdewpti;
    }

    public void setMaxdewpti(long maxdewpti) {
        this.maxdewpti = maxdewpti;
    }

    public long getMaxhumidity() {
        return maxhumidity;
    }

    public void setMaxhumidity(long maxhumidity) {
        this.maxhumidity = maxhumidity;
    }

    public float getMaxpressurei() {
        return maxpressurei;
    }

    public void setMaxpressurei(float maxpressurei) {
        this.maxpressurei = maxpressurei;
    }

    public long getMaxtempi() {
        return maxtempi;
    }

    public void setMaxtempi(long maxtempi) {
        this.maxtempi = maxtempi;
    }

    public float getMaxvisi() {
        return maxvisi;
    }

    public void setMaxvisi(float maxvisi) {
        this.maxvisi = maxvisi;
    }

    public long getMaxwspdi() {
        return maxwspdi;
    }

    public void setMaxwspdi(long maxwspdi) {
        this.maxwspdi = maxwspdi;
    }

    public long getMeanwdird() {
        return meanwdird;
    }

    public void setMeanwdird(long meanwdird) {
        this.meanwdird = meanwdird;
    }

    public long getMindewpti() {
        return mindewpti;
    }

    public void setMindewpti(long mindewpti) {
        this.mindewpti = mindewpti;
    }

    public long getMinhumidity() {
        return minhumidity;
    }

    public void setMinhumidity(long minhumidity) {
        this.minhumidity = minhumidity;
    }

    public float getMinpressurei() {
        return minpressurei;
    }

    public void setMinpressurei(float minpressurei) {
        this.minpressurei = minpressurei;
    }

    public long getMintempi() {
        return mintempi;
    }

    public void setMintempi(long mintempi) {
        this.mintempi = mintempi;
    }

    public float getMinvisi() {
        return minvisi;
    }

    public void setMinvisi(float minvisi) {
        this.minvisi = minvisi;
    }

    public long getMinwspdi() {
        return minwspdi;
    }

    public void setMinwspdi(long minwspdi) {
        this.minwspdi = minwspdi;
    }

    public float getPrecipi() {
        return precipi;
    }

    public void setPrecipi(float precipi) {
        this.precipi = precipi;
    }

    public long getRain() {
        return rain;
    }

    public void setRain(long rain) {
        this.rain = rain;
    }

    public long getSnapshot_date() {
        return snapshot_date;
    }

    public void setSnapshot_date(long snapshot_date) {
        this.snapshot_date = snapshot_date;
    }

    public long getSnow() {
        return snow;
    }

    public void setSnow(long snow) {
        this.snow = snow;
    }

    public long getThunder() {
        return thunder;
    }

    public void setThunder(long thunder) {
        this.thunder = thunder;
    }

    /** Take a DB column name and return the DO data as Double
     *   Needed by graphing routines
     */
    //make a big case statement <- crappy
    public Double getCol(String aColName) {
        Double reply = null;

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

        return reply;
    }

}
