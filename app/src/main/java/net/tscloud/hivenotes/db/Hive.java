package net.tscloud.hivenotes.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tscloud on 8/20/15.
 */
public class Hive implements Parcelable {

    public static final String TAG = "Hive";

    private long id;
    private long apiary;
    private String name;
    private String species;
    private String requeen;
    private String foundationType;
    private String note;


    public long getApiary() {
        return apiary;
    }

    public void setApiary(long apiary) {
        this.apiary = apiary;
    }

    public String getFoundationType() {
        return foundationType;
    }

    public void setFoundationType(String foundationType) {
        this.foundationType = foundationType;
    }

    public String getRequeen() {
        return requeen;
    }

    public void setRequeen(String requeen) {
        this.requeen = requeen;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getNote() { return note; }

    public void setNote(String note) { this.note = note; }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.apiary);
        dest.writeString(this.name);
        dest.writeString(this.species);
        dest.writeString(this.requeen);
        dest.writeString(this.foundationType);
        dest.writeString(this.note);
    }

    public Hive() {
    }

    protected Hive(Parcel in) {
        this.id = in.readLong();
        this.apiary = in.readLong();
        this.name = in.readString();
        this.species = in.readString();
        this.requeen = in.readString();
        this.foundationType = in.readString();
        this.note = in.readString();
    }

    public static final Parcelable.Creator<Hive> CREATOR = new Parcelable.Creator<Hive>() {
        @Override
        public Hive createFromParcel(Parcel source) {
            return new Hive(source);
        }

        @Override
        public Hive[] newArray(int size) {
            return new Hive[size];
        }
    };
}
