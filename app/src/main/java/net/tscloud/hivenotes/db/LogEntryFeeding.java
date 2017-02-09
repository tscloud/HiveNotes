package net.tscloud.hivenotes.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tscloud on 11/10/15.
 */
public class LogEntryFeeding implements HiveNotesLogDO, Parcelable {

    public static final String TAG = "LogEntryFeeding";

    private long id;
    private long hive;
    private long visitDate;
    private String feedingTypes;

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

    public String getOtherType() {
        return otherType;
    }

    public void setOtherType(String otherType) {
        this.otherType = otherType;
    }

    public long getFeedingTypes() {
        return feedingTypes;
    }

    public void setFeedingTypes(long feedingTypes) {
        this.feedingTypes = feedingTypes;
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
        dest.writeString(this.feedingTypes);
    }

    public LogEntryFeeding() {
    }

    protected LogEntryFeeding(Parcel in) {
        this.id = in.readLong();
        this.hive = in.readLong();
        this.visitDate = in.readLong();
        this.feedingTypes = in.readString();
    }

    public static final Parcelable.Creator<LogEntryFeeding> CREATOR = new Parcelable.Creator<LogEntryFeeding>() {
        @Override
        public LogEntryFeeding createFromParcel(Parcel source) {
            return new LogEntryFeeding(source);
        }

        @Override
        public LogEntryFeeding[] newArray(int size) {
            return new LogEntryFeeding[size];
        }
    };
}
