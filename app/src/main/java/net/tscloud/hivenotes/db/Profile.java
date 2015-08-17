package net.tscloud.hivenotes.db;

import java.io.Serializable;

/**
 * Created by tscloud on 8/15/15.
 */
public class Profile {

    public static final String TAG = "Profile";

    private long mId;
    private long mTableId;
    private String mName;
    private String mEmail;

    public Profile(){}

    public Profile(String mName, String mEmail) {
        this.mEmail = mEmail;
        this.mName = mName;
    }

    public long getTableId() {
        return mTableId;
    }

    public void setTableId(long mTableId) {
        this.mTableId = mTableId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

}
