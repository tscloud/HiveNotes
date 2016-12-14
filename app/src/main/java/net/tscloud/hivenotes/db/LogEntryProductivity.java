package net.tscloud.hivenotes.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tscloud on 10/17/15.
 */
public class LogEntryProductivity implements HiveNotesLogDO, Parcelable {

    public static final String TAG = "LogEntryProductivity";

    private long id;
    private long hive;
    private long visitDate;
    private float extractedHoney;
    private float pollenCollected;
    private float beeswaxCollected;

    public float getBeeswaxCollected() {
        return beeswaxCollected;
    }

    public void setBeeswaxCollected(float beeswaxCollected) {
        this.beeswaxCollected = beeswaxCollected;
    }

    public float getExtractedHoney() {
        return extractedHoney;
    }

    public void setExtractedHoney(float extractedHoney) {
        this.extractedHoney = extractedHoney;
    }

    @Override
    public long getHive() {
        return hive;
    }

    @Override
    public void setHive(long hive) {
        this.hive = hive;
    }

    public float getPollenCollected() {
        return pollenCollected;
    }

    public void setPollenCollected(float pollenCollected) {
        this.pollenCollected = pollenCollected;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
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
        dest.writeFloat(this.extractedHoney);
        dest.writeFloat(this.pollenCollected);
        dest.writeFloat(this.beeswaxCollected);
    }

    public LogEntryProductivity() {
    }

    protected LogEntryProductivity(Parcel in) {
        this.id = in.readLong();
        this.hive = in.readLong();
        this.visitDate = in.readLong();
        this.extractedHoney = in.readFloat();
        this.pollenCollected = in.readFloat();
        this.beeswaxCollected = in.readFloat();
    }

    public static final Parcelable.Creator<LogEntryProductivity> CREATOR = new Parcelable.Creator<LogEntryProductivity>() {
        @Override
        public LogEntryProductivity createFromParcel(Parcel source) {
            return new LogEntryProductivity(source);
        }

        @Override
        public LogEntryProductivity[] newArray(int size) {
            return new LogEntryProductivity[size];
        }
    };
}
