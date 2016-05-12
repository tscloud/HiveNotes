package net.tscloud.hivenotes.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tscloud on 11/3/15.
 */
public class LogEntryPestMgmt implements HiveNotesLogDO, Parcelable {

    public static final String TAG = "LogEntryPestMgmt";

    private long id;
    private long hive;
    private String visitDate;
    private int droneCellFndn;
    private int smallHiveBeetleTrap;
    private int mitesTrtmnt;
    private String mitesTrtmntType;
    private int screenedBottomBoard;
    private int other;
    private String otherType;

    // These hold reminder times -- they will NOT be persisted
    private long droneCellFndnRmndrTime = -1;
    private long mitesTrtmntRmndrTime = -1;

    public int getDroneCellFndn() {
        return droneCellFndn;
    }

    public void setDroneCellFndn(int droneCellFndn) {
        this.droneCellFndn = droneCellFndn;
    }

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

    public int getMitesTrtmnt() {
        return mitesTrtmnt;
    }

    public void setMitesTrtmnt(int mitesTrtmnt) {
        this.mitesTrtmnt = mitesTrtmnt;
    }

    public String getMitesTrtmntType() {
        return mitesTrtmntType;
    }

    public void setMitesTrtmntType(String mitesTrtmntType) {
        this.mitesTrtmntType = mitesTrtmntType;
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

    public int getScreenedBottomBoard() {
        return screenedBottomBoard;
    }

    public void setScreenedBottomBoard(int screenedBottomBoard) {
        this.screenedBottomBoard = screenedBottomBoard;
    }

    public int getSmallHiveBeetleTrap() {
        return smallHiveBeetleTrap;
    }

    public void setSmallHiveBeetleTrap(int smallHiveBeetleTrap) {
        this.smallHiveBeetleTrap = smallHiveBeetleTrap;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public long getDroneCellFndnRmndrTime() {
        return droneCellFndnRmndrTime;
    }

    public void setDroneCellFndnRmndrTime(long droneCellFndnRmndrTime) {
        this.droneCellFndnRmndrTime = droneCellFndnRmndrTime;
    }

    public long getMitesTrtmntRmndrTime() {
        return mitesTrtmntRmndrTime;
    }

    public void setMitesTrtmntRmndrTime(long mitesTrtmntRmndrTime) {
        this.mitesTrtmntRmndrTime = mitesTrtmntRmndrTime;
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
        dest.writeInt(this.droneCellFndn);
        dest.writeInt(this.smallHiveBeetleTrap);
        dest.writeInt(this.mitesTrtmnt);
        dest.writeString(this.mitesTrtmntType);
        dest.writeInt(this.screenedBottomBoard);
        dest.writeInt(this.other);
        dest.writeString(this.otherType);
        dest.writeLong(this.droneCellFndnRmndrTime);
        dest.writeLong(this.mitesTrtmntRmndrTime);
    }

    public LogEntryPestMgmt() {
    }

    protected LogEntryPestMgmt(Parcel in) {
        this.id = in.readLong();
        this.hive = in.readLong();
        this.visitDate = in.readString();
        this.droneCellFndn = in.readInt();
        this.smallHiveBeetleTrap = in.readInt();
        this.mitesTrtmnt = in.readInt();
        this.mitesTrtmntType = in.readString();
        this.screenedBottomBoard = in.readInt();
        this.other = in.readInt();
        this.otherType = in.readString();
        this.droneCellFndnRmndrTime = in.readLong();
        this.mitesTrtmntRmndrTime = in.readLong();
    }

    public static final Parcelable.Creator<LogEntryPestMgmt> CREATOR = new Parcelable.Creator<LogEntryPestMgmt>() {
        @Override
        public LogEntryPestMgmt createFromParcel(Parcel source) {
            return new LogEntryPestMgmt(source);
        }

        @Override
        public LogEntryPestMgmt[] newArray(int size) {
            return new LogEntryPestMgmt[size];
        }
    };
}
