package net.tscloud.hivenotes.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tscloud on 11/3/15.
 */
public class LogEntryHiveHealth implements HiveNotesLogDO, Parcelable {

    public static final String TAG = "LogEntryHiveHealth";

    private long id;
    private long hive;
    private long visitDate;
    private String pestsDetected;
    private String diseaseDetected;
    private String varroaTreatment;

    // These hold reminder times -- they will NOT be persisted
    private long varroaTrtmntRmndrTime = -1;

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

    public String getDiseaseDetected() {
        return diseaseDetected;
    }

    public void setDiseaseDetected(String diseaseDetected) {
        this.diseaseDetected = diseaseDetected;
    }

    public String getPestsDetected() {
        return pestsDetected;
    }

    public void setPestsDetected(String pestsDetected) {
        this.pestsDetected = pestsDetected;
    }

    public long getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(long visitDate) {
        this.visitDate = visitDate;
    }

    public String getVarroaTreatment() {
        return varroaTreatment;
    }

    public void setVarroaTreatment(String varroaTreatment) {
        this.varroaTreatment = varroaTreatment;
    }

    public long getVarroaTrtmntRmndrTime() {
        return varroaTrtmntRmndrTime;
    }

    public void setVarroaTrtmntRmndrTime(long varroaTrtmntRmndrTime) {
        this.varroaTrtmntRmndrTime = varroaTrtmntRmndrTime;
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
        dest.writeString(this.pestsDetected);
        dest.writeString(this.diseaseDetected);
        dest.writeString(this.varroaTreatment);
        dest.writeLong(this.varroaTrtmntRmndrTime);
    }

    public LogEntryHiveHealth() {
    }

    protected LogEntryHiveHealth(Parcel in) {
        this.id = in.readLong();
        this.hive = in.readLong();
        this.visitDate = in.readLong();
        this.pestsDetected = in.readString();
        this.diseaseDetected = in.readString();
        this.varroaTreatment = in.readString();
        this.varroaTrtmntRmndrTime = in.readLong();
    }

    public static final Parcelable.Creator<LogEntryHiveHealth> CREATOR = new Parcelable.Creator<LogEntryHiveHealth>() {
        @Override
        public LogEntryHiveHealth createFromParcel(Parcel source) {
            return new LogEntryHiveHealth(source);
        }

        @Override
        public LogEntryHiveHealth[] newArray(int size) {
            return new LogEntryHiveHealth[size];
        }
    };
}
