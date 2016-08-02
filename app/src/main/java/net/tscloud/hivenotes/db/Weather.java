package net.tscloud.hivenotes.db;

/**
 * Created by tscloud on 8/20/15.
 */
public class Weather {

    public static final String TAG = "Weather";

    private long id;
    private long apiary;
    private long snapshotDate;
    private String temperature;
    private String rainfall;
    private String pressure;
    private String weather;
    private String windDirection;
    private String windMPH;
    private String humidity;
    private String dewPoint;
    private String visibility;
    private String solarRadiation;
    private String uvIndex;
    private String pollenCount;
    private String pollution;

    public long getApiary() {
        return apiary;
    }

    public void setApiary(long apiary) {
        this.apiary = apiary;
    }

    public String getDewPoint() {
        return dewPoint;
    }

    public void setDewPoint(String dewPoint) {
        this.dewPoint = dewPoint;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getSolarRadiation() {
        return solarRadiation;
    }

    public void setSolarRadiation(String solarRadiation) {
        this.solarRadiation = solarRadiation;
    }

    public String getUvIndex() {
        return uvIndex;
    }

    public void setUvIndex(String uvIndex) {
        this.uvIndex = uvIndex;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getWindMPH() {
        return windMPH;
    }

    public void setWindMPH(String windMPH) {
        this.windMPH = windMPH;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public long getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(long snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public String getRainfall() {
        return rainfall;
    }

    public void setRainfall(String rainfall) {
        this.rainfall = rainfall;
    }

    public String getPollution() {
        return pollution;
    }

    public void setPollution(String pollution) {
        this.pollution = pollution;
    }

    public String getPollenCount() {
        return pollenCount;
    }

    public void setPollenCount(String pollenCount) {
        this.pollenCount = pollenCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
