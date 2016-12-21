package net.tscloud.hivenotes.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tscloud on 11/15/15.
 */
public class LogEntryOther implements HiveNotesLogDO, Parcelable {

    public static final String TAG = "LogEntryHiveHealth";

    /* Reminders children of Hive */
    private long id;
    private long hive;
    private long visitDate;
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

    public long getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(long visitDate) {
        this.visitDate = visitDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.hive);
        dest.writeLong(this.visitDate);
        dest.writeString(this.requeen);
        dest.writeLong(this.requeenRmndrTime);
        dest.writeLong(this.swarmRmndrTime);
        dest.writeLong(this.splitHiveRmndrTime);
    }

    public LogEntryOther() {
    }

    protected LogEntryOther(Parcel in) {
        this.id = in.readLong();
        this.hive = in.readLong();
        this.visitDate = in.readLong();
        this.requeen = in.readString();
        this.requeenRmndrTime = in.readLong();
        this.swarmRmndrTime = in.readLong();
        this.splitHiveRmndrTime = in.readLong();
    }

    public static final Parcelable.Creator<LogEntryOther> CREATOR = new Parcelable.Creator<LogEntryOther>() {
        @Override
        public LogEntryOther createFromParcel(Parcel source) {
            return new LogEntryOther(source);
        }

        @Override
        public LogEntryOther[] newArray(int size) {
            return new LogEntryOther[size];
        }
    };
}
