package net.tscloud.hivenotes.db;

import java.io.Serializable;

/**
 * Created by tscloud on 8/20/15.
 */
public class LogEntryGeneral implements Serializable {

    public static final String TAG = "LogEntryGeneral";

    private long id;
    private long hive;
    private String visitDate;
    private String population;
    private String temperament;
    private String pestsDisease;
    private int broodFrames;
    private String broodPattern;
    private String queen;
    private String honeyStores;
    private String pollenStores;

    public int getBroodFrames() {
        return broodFrames;
    }

    public void setBroodFrames(int broodFrames) {
        this.broodFrames = broodFrames;
    }

    public String getBroodPattern() {
        return broodPattern;
    }

    public void setBroodPattern(String broodPattern) {
        this.broodPattern = broodPattern;
    }

    public String getPestsDisease() {
        return pestsDisease;
    }

    public void setPestsDisease(String pestsDisease) {
        this.pestsDisease = pestsDisease;
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

    public String getQueen() {
        return queen;
    }

    public void setQueen(String queen) {
        this.queen = queen;
    }

    public String getTemperament() {
        return temperament;
    }

    public void setTemperament(String temperament) {
        this.temperament = temperament;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }
}
