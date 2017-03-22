package net.tscloud.hivenotes.helper;

/**
 * Created by tscloud on 1/30/17.
 */
public class LogEditTextDialogData {

    private String title;
    private String subtitle;
    private String tag;
    private String data;
    private boolean isOtherNum = true; //note default - probably always number

    public LogEditTextDialogData (String aTitle, String aSubtitle, String aTag, String aData) {

        this.title = aTitle;
        this.subtitle = aSubtitle;
        this.tag = aTag;
        this.data = aData;
    }

    public LogEditTextDialogData (String aTitle, String aSubtitle, String aTag, String aData,
                                  boolean aIsOtherNum) {

        this(aTitle, aSubtitle, aTag, aData);
        this.isOtherNum = aIsOtherNum;
    }

    public String getTag() {
        return tag;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getData() {
        return data;
    }

    public boolean isOtherNum() {
        return isOtherNum;
    }
}