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
    private int oneOneSugarWater;
    private int twoOneSugarWater;
    private int pollenPatty;
    private int other;
    private String otherType;

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
        dest.writeInt(this.oneOneSugarWater);
        dest.writeInt(this.twoOneSugarWater);
        dest.writeInt(this.pollenPatty);
        dest.writeInt(this.other);
        dest.writeString(this.otherType);
    }

    public LogEntryFeeding() {
    }

    protected LogEntryFeeding(Parcel in) {
        this.id = in.readLong();
        this.hive = in.readLong();
        this.visitDate = in.readLong();
        this.oneOneSugarWater = in.readInt();
        this.twoOneSugarWater = in.readInt();
        this.pollenPatty = in.readInt();
        this.other = in.readInt();
        this.otherType = in.readString();
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
