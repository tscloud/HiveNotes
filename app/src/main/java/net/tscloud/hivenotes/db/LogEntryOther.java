package net.tscloud.hivenotes.db;

import java.io.Serializable;

/**
 * Created by tscloud on 11/15/15.
 */
public class LogEntryOther implements Serializable, HiveNotesLogDO {

    public static final String TAG = "LogEntryPestMgmt";

    private long id;
    private long hive;
    private String visitDate;
    private String requeen;
    private long requeenRmndr;
    private long swarmRmndr;
    private long splitHiveRmndr;

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

    public long getRequeenRmndr() {
        return requeenRmndr;
    }

    public void setRequeenRmndr(long requeenRmndr) {
        this.requeenRmndr = requeenRmndr;
    }

    public long getSplitHiveRmndr() {
        return splitHiveRmndr;
    }

    public void setSplitHiveRmndr(long splitHiveRmndr) {
        this.splitHiveRmndr = splitHiveRmndr;
    }

    public long getSwarmRmndr() {
        return swarmRmndr;
    }

    public void setSwarmRmndr(long swarmRmndr) {
        this.swarmRmndr = swarmRmndr;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }
}
