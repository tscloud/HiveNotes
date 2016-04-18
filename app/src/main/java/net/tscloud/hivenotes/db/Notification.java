package net.tscloud.hivenotes.db;

import java.io.Serializable;

/**
 * Created by tscloud on 8/20/15.
 */
public class Notification implements Serializable {

    public static final String TAG = "Notification";

    private long id;
    private long apiary;
    private long hive;
    private long eventId;
    private long rmndrType;

    public long getRmndrType() {
        return rmndrType;
    }

    public void setRmndrType(long rmndrType) {
        this.rmndrType = rmndrType;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getHive() {
        return hive;
    }

    public void setHive(long hive) {
        this.hive = hive;
    }

    public long getApiary() {
        return apiary;
    }

    public void setApiary(long apiary) {
        this.apiary = apiary;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
