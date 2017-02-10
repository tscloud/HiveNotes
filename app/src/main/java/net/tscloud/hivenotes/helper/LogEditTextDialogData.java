package net.tscloud.hivenotes.helper;

/**
 * Created by tscloud on 1/30/17.
 */
public class LogEditTextDialogData {

    private String title;
    private String tag;
    private String data;

    public LogMultiSelectDialogData (String aTitle, String aTag, String aData) {

        this.title = aTitle;
        this.tag = aTag;
        this.tag = aData;
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

}