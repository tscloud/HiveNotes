package net.tscloud.hivenotes.db;

import java.io.Serializable;

/**
 * Created by tscloud on 11/3/15.
 */
public class LogEntryPestMgmt implements Serializable, HiveNotesLogDO {

    public static final String TAG = "LogEntryPestMgmt";

    private long id;
    private long hive;
    private String visitDate;
    private int droneCellFndn;
    private long droneCellFndnRmndr;
    private int smallHiveBeetleTrap;
    private int mitesTrtmnt;
    private String mitesTrtmntType;
    private long mitesTrtmntRmndr;
    private int screenedBottomBoard;
    private int other;
    private String otherType;

    public int getDroneCellFndn() {
        return droneCellFndn;
    }

    public void setDroneCellFndn(int droneCellFndn) {
        this.droneCellFndn = droneCellFndn;
    }

    public long getDroneCellFndnRmndr() {
        return droneCellFndnRmndr;
    }

    public void setDroneCellFndnRmndr(long droneCellFndnRmndr) {
        this.droneCellFndnRmndr = droneCellFndnRmndr;
    }

    @Override
    public long getHive() {
        return hive;
    }

    @Override
    public void setHive(long hive) {
        this.hive = hive;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public int getMitesTrtmnt() {
        return mitesTrtmnt;
    }

    public void setMitesTrtmnt(int mitesTrtmnt) {
        this.mitesTrtmnt = mitesTrtmnt;
    }

    public long getMitesTrtmntRmndr() {
        return mitesTrtmntRmndr;
    }

    public void setMitesTrtmntRmndr(long mitesTrtmntRmndr) {
        this.mitesTrtmntRmndr = mitesTrtmntRmndr;
    }

    public String getMitesTrtmntType() {
        return mitesTrtmntType;
    }

    public void setMitesTrtmntType(String mitesTrtmntType) {
        this.mitesTrtmntType = mitesTrtmntType;
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
