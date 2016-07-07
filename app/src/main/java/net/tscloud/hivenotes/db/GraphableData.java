package net.tscloud.hivenotes.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tscloud on 6/26/16.
 */
public class GraphableData implements Parcelable {

    public static final String TAG = "GraphableData";

    private long id;
    private String directive;
    private String column;
    private String prettyName;
    private String category;
    private String keyLevel;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getDirective() {
        return directive;
    }

    public void setDirective(String directive) {
        this.directive = directive;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    public String getKeyLevel() {
        return keyLevel;
    }

    public void setKeyLevel(String keyLevel) {
        this.keyLevel = keyLevel;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.directive);
        dest.writeString(this.column);
        dest.writeString(this.prettyName);
        dest.writeString(this.category);
        dest.writeString(this.keyLevel);
    }

    public GraphableData() {
    }

    protected GraphableData(Parcel in) {
        this.id = in.readLong();
        this.directive = in.readString();
        this.column = in.readString();
        this.prettyName = in.readString();
        this.category = in.readString();
        this.keyLevel = in.readString();
    }

    public static final Creator<GraphableData> CREATOR = new Creator<GraphableData>() {
        @Override
        public GraphableData createFromParcel(Parcel source) {
            return new GraphableData(source);
        }

        @Override
        public GraphableData[] newArray(int size) {
            return new GraphableData[size];
        }
    };
}
