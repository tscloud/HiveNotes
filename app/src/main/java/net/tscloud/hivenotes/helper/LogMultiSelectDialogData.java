package net.tscloud.hivenotes.helper;

/**
 * Created by tscloud on 1/30/17.
 */
public class LogMultiSelectDialogData {

    private String title;
    private String[] elems;
    private String checkedSet;
    private String tag;
    private boolean hasOther = false;
    private boolean hasReminder = false;
    private boolean isMultiselect = true;

    public LogMultiSelectDialogData (String aTitle, String[] aElems,
                                     String aCheckedSet, String aTag,
                                     boolean aHasOther, boolean aHasReminder,
                                     boolean aIsMultiselect) {

        this.title = aTitle;
        this.elems = aElems;
        this.checkedSet = aCheckedSet;
        this.tag = aTag;
        this.hasOther = aHasOther;
        this.hasReminder = aHasReminder;
        this.isMultiselect = aIsMultiselect;
    }

    public String getCheckedSet() {
        return checkedSet;
    }

    public String[] getElems() {
        return elems;
    }

    public boolean isHasOther() {
        return hasOther;
    }

    public boolean isHasReminder() {
        return hasReminder;
    }

    public boolean isMultiselect() {
        return isMultiselect;
    }

    public String getTag() {
        return tag;
    }

    public String getTitle() {
        return title;
    }
}