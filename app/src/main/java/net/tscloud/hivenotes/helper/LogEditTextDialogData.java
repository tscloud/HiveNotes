package net.tscloud.hivenotes.helper;

/**
 * Created by tscloud on 1/30/17.
 */
public class LogEditTextDialogData {

    private String title;
    private String tag;
    private String data;
    private boolean isOtherNum = true; //note default - probably always number

    public LogEditTextDialogData (String aTitle, String aTag, String aData) {

        this.title = aTitle;
        this.tag = aTag;
        this.data = aData;
    }

    public LogEditTextDialogData (String aTitle, String aTag, String aData, boolean aIsOtherNum) {

        this.title = aTitle;
        this.tag = aTag;
        this.data = aData;
        this.isOtherNum = aIsOtherNum;
    }

    public String getTag() {
        return tag;
    }

    public String getTitle() {
        return title;
    }

    public String getData() {
        return data;
    }

    public boolean isOtherNum() {
        return isOtherNum;
    }
}