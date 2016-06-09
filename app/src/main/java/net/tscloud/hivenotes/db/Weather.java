package net.tscloud.hivenotes.db;

/**
 * Created by tscloud on 8/20/15.
 */
public class Weather {

    public static final String TAG = "Weather";

    private long id;
    private long snapshotDate;
    private float temperature;
    private float rainfall;
    private float pressure;
    private String weather;
    private String windDirection;
    private float windMPH;
    private String humidity;
    private float dewPoint;
    private String visibility;
    private String solarRadiation;
    private String uvIndex;
    private float pollenCount;
    private float pollution;

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public long getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(long snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public float getRainfall() {
        return rainfall;
    }

    public void setRainfall(float rainfall) {
        this.rainfall = rainfall;
    }

    public float getPollution() {
        return pollution;
    }

    public void setPollution(float pollution) {
        this.pollution = pollution;
    }

    public float getPollenCount() {
        return pollenCount;
    }

    public void setPollenCount(float pollenCount) {
        this.pollenCount = pollenCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
