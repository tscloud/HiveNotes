package net.tscloud.hivenotes.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tscloud on 8/20/15.
 */
public class LogEntryGeneral implements HiveNotesLogDO, Parcelable {

    public static final String TAG = "LogEntryGeneral";

    private long id;
    private long hive;
    private long visitDate;
    private String population;
    private String temperament;
    private String broodPattern;
    private String queen;
    private String honeyStores;
    private String pollenStores;

    public String getBroodPattern() {
        return broodPattern;
    }

    public void setBroodPattern(String broodPattern) {
        this.broodPattern = broodPattern;
    }

    @Override
    public long getHive() {
        return hive;
    }

    @Override
    public void setHive(long hive) {
        this.hive = hive;
    }

    public String getHoneyStores() {
        return honeyStores;
    }

    public void setHoneyStores(String honeyStores) {
        this.honeyStores = honeyStores;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public String getPollenStores() {
        return pollenStores;
    }

    public void setPollenStores(String pollenStores) {
        this.pollenStores = pollenStores;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public String getQueen() {
        return queen;
    }

    public void setQueen(String queen) {
        this.queen = queen;
    }

    public String getTemperament() {
        return temperament;
    }

    public void setTemperament(String temperament) {
        this.temperament = temperament;
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
        dest.writeString(this.population);
        dest.writeString(this.temperament);
        dest.writeString(this.broodPattern);
        dest.writeString(this.queen);
        dest.writeString(this.honeyStores);
        dest.writeString(this.pollenStores);
    }

    public LogEntryGeneral() {
    }

    protected LogEntryGeneral(Parcel in) {
        this.id = in.readLong();
        this.hive = in.readLong();
        this.visitDate = in.readLong();
        this.population = in.readString();
        this.temperament = in.readString();
        this.broodPattern = in.readString();
        this.queen = in.readString();
        this.honeyStores = in.readString();
        this.pollenStores = in.readString();
    }

    public static final Parcelable.Creator<LogEntryGeneral> CREATOR = new Parcelable.Creator<LogEntryGeneral>() {
        @Override
        public LogEntryGeneral createFromParcel(Parcel source) {
            return new LogEntryGeneral(source);
        }

        @Override
        public LogEntryGeneral[] newArray(int size) {
            return new LogEntryGeneral[size];
        }
    };
}
