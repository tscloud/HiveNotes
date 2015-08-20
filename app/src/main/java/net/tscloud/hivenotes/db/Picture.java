package net.tscloud.hivenotes.db;

/**
 * Created by tscloud on 8/20/15.
 */
public class Picture {

    public static final String TAG = "Note";

    private long id;
    private long apiary;
    private long hive;
    private String imageURI;

    public long getApiary() {
        return apiary;
    }

    public void setApiary(long apiary) {
        this.apiary = apiary;
    }

    public long getHive() {
        return hive;
    }

    public void setHive(long hive) {
        this.hive = hive;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }
}
