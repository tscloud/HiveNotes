package net.tscloud.hivenotes.helper;

/**
 * Created by tscloud on 1/30/17.
 */
public class LogEditTextDialogLocationData {

    private String title;
    private String tag;
    private String postalCode;
    private float lat;
    private float lon;

    public LogEditTextDialogLocationData(String aTitle, String aTag, String aPostalCode,
                                         float aLat, float aLon) {

        this.title = aTitle;
        this.tag = aTag;
        this.postalCode = aPostalCode;
        this.lat = aLat;
        this.lon = aLon;
    }

    public String getTag() {
        return tag;
    }

    public String getTitle() {
        return title;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }
}