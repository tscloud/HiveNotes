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
    private String visitDate;
    private String honeyAddSupers;
    private String honeyRemoveSupers;
    private float extractedHoney;
    private int addPollenTrap;
    private int removePollenTrap;
    private float pollenCollected;
    private float beeswaxCollected;

    public int getAddPollenTrap() {
        return addPollenTrap;
    }

    public void setAddPollenTrap(int addPollenTrap) {
        this.addPollenTrap = addPollenTrap;
    }

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

    public String getHoneyAddSupers() {
        return honeyAddSupers;
    }

    public void setHoneyAddSupers(String honeyAddSupers) {
        this.honeyAddSupers = honeyAddSupers;
    }

    public float getPollenCollected() {
        return pollenCollected;
    }

    public void setPollenCollected(float pollenCollected) {
        this.pollenCollected = pollenCollected;
    }

    public String getHoneyRemoveSupers() {
        return honeyRemoveSupers;
    }

    public void setHoneyRemoveSupers(String honeyRemoveSupers) {
        this.honeyRemoveSupers = honeyRemoveSupers;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public int getRemovePollenTrap() {
        return removePollenTrap;
    }

    public void setRemovePollenTrap(int removePollenTrap) {
        this.removePollenTrap = removePollenTrap;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
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
        dest.writeString(this.visitDate);
        dest.writeString(this.honeyAddSupers);
        dest.writeString(this.honeyRemoveSupers);
        dest.writeFloat(this.extractedHoney);
        dest.writeInt(this.addPollenTrap);
        dest.writeInt(this.removePollenTrap);
        dest.writeFloat(this.pollenCollected);
        dest.writeFloat(this.beeswaxCollected);
    }

    public LogEntryProductivity() {
    }

    protected LogEntryProductivity(Parcel in) {
        this.id = in.readLong();
        this.hive = in.readLong();
        this.visitDate = in.readString();
        this.honeyAddSupers = in.readString();
        this.honeyRemoveSupers = in.readString();
        this.extractedHoney = in.readFloat();
        this.addPollenTrap = in.readInt();
        this.removePollenTrap = in.readInt();
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
