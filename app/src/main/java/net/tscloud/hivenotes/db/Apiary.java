package net.tscloud.hivenotes.db;

/**
 * Created by tscloud on 8/15/15.
 */
public class Apiary {

    public static final String TAG = "Apiary";
    // This should be set at runtime so not final
    public static long TABLE_ID = 0;

    private long mId;
    private long mTableId;
    private long mProfile;
    private long mProfileTable;
    private String mName;

    public Apiary() {
    }

    public Apiary(String mName, long mProfile, long mProfileTable) {
        this.mName = mName;
        this.mProfile = mProfile;
        this.mProfileTable = mProfileTable;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public long getProfile() {
        return mProfile;
    }

    public void setProfile(long mProfile) {
        this.mProfile = mProfile;
    }

    public long getProfileTable() {
        return mProfileTable;
    }

    public void setProfileTable(long mProfileTable) {
        this.mProfileTable = mProfileTable;
    }

    public long getTableId() {
        return mTableId;
    }

    public void setTableId(long mTableId) {
        this.mTableId = mTableId;
    }
}
