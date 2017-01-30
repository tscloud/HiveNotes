package net.tscloud.hivenotes.helper;

/**
 * Created by tscloud on 1/30/17.
 */
public class LogMultiSelectDialogData {

    String title;
    String[] elems;
    String checketSet;
    String tag;
    boolean hasOther = false;
    boolean hasReminder = false;
    boolean isMultiselect = true;

    public LogMultiSelectDialogData (String aTitle, String[] aElems,
                                     String aChecketSet, String aTag,
                                     boolean aHasOther, boolean aHasReminder,
                                     boolean aIsMultiselect) {

        this.title = aTitle;
        this.elems = aElems;
        this.checketSet = aChecketSet;
        this.tag = aTag;
        this.hasOther = aHasOther;
        this.hasReminder = aHasReminder;
        this.isMultiselect = aIsMultiselect;
    }
}