package net.tscloud.hivenotes.db;

import java.io.Serializable;

/**
 * Created by tscloud on 11/10/15.
 */
public class LogEntryFeeding implements Serializable {

    public static final String TAG = "LogEntryFeeding";

    private long id;
    private long hive;
    private String visitDate;
    private int oneOneSugarWater;
    private int twoOneSugarWater;
    private int pollenPatty;
    private int other;
    private String otherType;

    public long getHive() {
        return hive;
    }

    public void setHive(long hive) {
        this.hive = hive;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getOneOneSugarWater() {
        return oneOneSugarWater;
    }

    public void setOneOneSugarWater(int oneOneSugarWater) {
        this.oneOneSugarWater = oneOneSugarWater;
    }

    public int getTwoOneSugarWater() {
        return twoOneSugarWater;
    }

    public void setTwoOneSugarWater(int twoOneSugarWater) {
        this.twoOneSugarWater = twoOneSugarWater;
    }

    public int getOther() {
        return other;
    }

    public void setOther(int other) {
        this.other = other;
    }

    public String getOtherType() {
        return otherType;
    }

    public void setOtherType(String otherType) {
        this.otherType = otherType;
    }

    public int getPollenPatty() {
        return pollenPatty;
    }

    public void setPollenPatty(int pollenPatty) {
        this.pollenPatty = pollenPatty;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }
}
