package net.tscloud.hivenotes.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tscloud on 8/15/15.
 */
public class Apiary implements Parcelable {

    public static final String TAG = "Apiary";

    private long id;
    private long profile;
    private String name;
    private String postalCode;
    private float latitude;
    private float longitude;

    public Apiary() {
    }

    public Apiary(String name, long profile) {
        this.name = name;
        this.profile = profile;
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

    public long getProfile() {
        return profile;
    }

    public void setProfile(long profile) {
        this.profile = profile;
    }

    public String getPostalCode() {
        return postalCode; }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode; }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.profile);
        dest.writeString(this.name);
        dest.writeString(this.postalCode);
        dest.writeFloat(this.latitude);
        dest.writeFloat(this.longitude);
    }

    protected Apiary(Parcel in) {
        this.id = in.readLong();
        this.profile = in.readLong();
        this.name = in.readString();
        this.postalCode = in.readString();
        this.latitude = in.readFloat();
        this.longitude = in.readFloat();
    }

    public static final Parcelable.Creator<Apiary> CREATOR = new Parcelable.Creator<Apiary>() {
        @Override
        public Apiary createFromParcel(Parcel source) {
            return new Apiary(source);
        }

        @Override
        public Apiary[] newArray(int size) {
            return new Apiary[size];
        }
    };
}
