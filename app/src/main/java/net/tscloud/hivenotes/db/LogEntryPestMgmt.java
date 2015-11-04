package net.tscloud.hivenotes.db;

import java.io.Serializable;

/**
 * Created by tscloud on 11/3/15.
 */
public class LogEntryPestMgmt implements Serializable {

    public static final String TAG = "LogEntryPestMgmt";

    private long id;
    private long hive;
    private String visitDate;
    private int droneCellFndn;
    private int droneCellFndnRmndr;
    private int smallHiveBeetleTrap;
    private int mitesTrtmnt;
    private String mitesTrtmntType;
    private int mitesTrtmntRmndr;
    private int screenedBottomBoard;
    private String other;

    public int getDroneCellFndn() {
        return droneCellFndn;
    }

    public void setDroneCellFndn(int droneCellFndn) {
        this.droneCellFndn = droneCellFndn;
    }

    public int getDroneCellFndnRmndr() {
        return droneCellFndnRmndr;
    }

    public void setDroneCellFndnRmndr(int droneCellFndnRmndr) {
        this.droneCellFndnRmndr = droneCellFndnRmndr;
    }

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

    public int getMitesTrtmnt() {
        return mitesTrtmnt;
    }

    public void setMitesTrtmnt(int mitesTrtmnt) {
        this.mitesTrtmnt = mitesTrtmnt;
    }

    public int getMitesTrtmntRmndr() {
        return mitesTrtmntRmndr;
    }

    public void setMitesTrtmntRmndr(int mitesTrtmntRmndr) {
        this.mitesTrtmntRmndr = mitesTrtmntRmndr;
    }

    public String getMitesTrtmntType() {
        return mitesTrtmntType;
    }

    public void setMitesTrtmntType(String mitesTrtmntType) {
        this.mitesTrtmntType = mitesTrtmntType;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public int getScreenedBottomBoard() {
        return screenedBottomBoard;
    }

    public void setScreenedBottomBoard(int screenedBottomBoard) {
        this.screenedBottomBoard = screenedBottomBoard;
    }

    public int getSmallHiveBeetleTrap() {
        return smallHiveBeetleTrap;
    }

    public void setSmallHiveBeetleTrap(int smallHiveBeetleTrap) {
        this.smallHiveBeetleTrap = smallHiveBeetleTrap;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }
}
