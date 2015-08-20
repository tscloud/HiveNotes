package net.tscloud.hivenotes.db;

/**
 * Created by tscloud on 8/15/15.
 */
public class Apiary {

    public static final String TAG = "Apiary";

    private long id;
    private long profile;
    private String name;
    private String postalCode;

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
}
