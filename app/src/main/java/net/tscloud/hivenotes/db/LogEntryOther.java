package net.tscloud.hivenotes.db;

import java.io.Serializable;

/**
 * Created by tscloud on 11/15/15.
 */
public class LogEntryOther implements Serializable, HiveNotesLogDO {

    public static final String TAG = "LogEntryPestMgmt";

    /* Reminders children of Hive */
    private long id;
    private long hive;
    private String visitDate;
    private String requeen;
    //private long requeenRmndr;
    //private long swarmRmndr;
    //private long splitHiveRmndr;

    // These hold reminder times -- they will NOT be persisted
    private long requeenRmndrTime = -1;
    private long swarmRmndrTime = -1;
    private long splitHiveRmndrTime = -1;

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

    public String getRequeen() {
        return requeen;
    }

    public void setRequeen(String requeen) {
        this.requeen = requeen;
    }

    public long getRequeenRmndrTime() {
        return requeenRmndrTime;
    }

    public void setRequeenRmndrTime(long requeenRmndrTime) {
        this.requeenRmndrTime = requeenRmndrTime;
    }

    public long getSplitHiveRmndrTime() {
        return splitHiveRmndrTime;
    }

    public void setSplitHiveRmndrTime(long splitHiveRmndrTime) {
        this.splitHiveRmndrTime = splitHiveRmndrTime;
    }

    public long getSwarmRmndrTime() {
        return swarmRmndrTime;
    }

    public void setSwarmRmndrTime(long swarmRmndrTime) {
        this.swarmRmndrTime = swarmRmndrTime;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }
}
