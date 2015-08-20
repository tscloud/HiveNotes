package net.tscloud.hivenotes.db;

/**
 * Created by tscloud on 8/20/15.
 */
public class Notification {

    public static final String TAG = "Notification";

    private long id;
    private long hive;
    private String alarmURI;

    public String getAlarmURI() {
        return alarmURI;
    }

    public void setAlarmURI(String alarmURI) {
        this.alarmURI = alarmURI;
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
}
