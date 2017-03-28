package net.tscloud.hivenotes.db;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by tscloud on 8/20/15.
 */
public class Notification implements Parcelable {

    public static final String TAG = "Notification";

    private long id;
    private long apiary;
    private long hive;
    private long eventId;
    private int rmndrType;
    private String rmndrDesc;

    public String getRmndrDesc() {
        return rmndrDesc;
    }

    public void setRmndrDesc(String rmndrDesc) {
        this.rmndrDesc = rmndrDesc;
    }

    public int getRmndrType() {
        return rmndrType;
    }

    public void setRmndrType(int rmndrType) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.apiary);
        dest.writeLong(this.hive);
        dest.writeLong(this.eventId);
        dest.writeInt(this.rmndrType);
    }

    public Notification() {
    }

    protected Notification(Parcel in) {
        this.id = in.readLong();
        this.apiary = in.readLong();
        this.hive = in.readLong();
        this.eventId = in.readLong();
        this.rmndrType = in.readInt();
    }

    public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel source) {
            return new Notification(source);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };
}
