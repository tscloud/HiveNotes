package net.tscloud.hivenotes.db;

/**
 * Created by tscloud on 8/20/15.
 */
public class Weather {

    public static final String TAG = "Weather";

    private long id;
    private String snapshotDate;
    private String temperature;
    private String rainfall;
    private String pollenCount;
    private String pollution;

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(String snapshotDate) {
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
