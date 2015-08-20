package net.tscloud.hivenotes.db;

/**
 * Created by tscloud on 8/20/15.
 */
public class LogEntry {

    public static final String TAG = "LogEntry";

    private long id;
    private long hive;
    private String visitDate;
    private String population;
    private String temperament;
    private String eggs;
    private String larvae;
    private String cappedBrood;
    private String broodFrames;
    private String broodPattern;
    private String queenAge;
    private String honeyStores;
    private String pollenStores;
    private String temperature;
    private String rainfall;
    private String pollenCount;
    private String pollution;

    public String getBroodFrames() {
        return broodFrames;
    }

    public void setBroodFrames(String broodFrames) {
        this.broodFrames = broodFrames;
    }

    public String getBroodPattern() {
        return broodPattern;
    }

    public void setBroodPattern(String broodPattern) {
        this.broodPattern = broodPattern;
    }

    public String getCappedBrood() {
        return cappedBrood;
    }

    public void setCappedBrood(String cappedBrood) {
        this.cappedBrood = cappedBrood;
    }

    public String getEggs() {
        return eggs;
    }

    public void setEggs(String eggs) {
        this.eggs = eggs;
    }

    public long getHive() {
        return hive;
    }

    public void setHive(long hive) {
        this.hive = hive;
    }

    public String getHoneyStores() {
        return honeyStores;
    }

    public void setHoneyStores(String honeyStores) {
        this.honeyStores = honeyStores;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLarvae() {
        return larvae;
    }

    public void setLarvae(String larvae) {
        this.larvae = larvae;
    }

    public String getPollenCount() {
        return pollenCount;
    }

    public void setPollenCount(String pollenCount) {
        this.pollenCount = pollenCount;
    }

    public String getPollenStores() {
        return pollenStores;
    }

    public void setPollenStores(String pollenStores) {
        this.pollenStores = pollenStores;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public String getPollution() {
        return pollution;
    }

    public void setPollution(String pollution) {
        this.pollution = pollution;
    }

    public String getQueenAge() {
        return queenAge;
    }

    public void setQueenAge(String queenAge) {
        this.queenAge = queenAge;
    }

    public String getRainfall() {
        return rainfall;
    }

    public void setRainfall(String rainfall) {
        this.rainfall = rainfall;
    }

    public String getTemperament() {
        return temperament;
    }

    public void setTemperament(String temperament) {
        this.temperament = temperament;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }
}
