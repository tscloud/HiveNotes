package net.tscloud.hivenotes.helper;

/**
 * Created by tscloud on 1/30/17.
 */
public class LogMultiSelectDialogData {

    private String title;
    private long hiveID;
    private String[] elems;
    private String checkedSet;
    private String tag;
    private long reminderMillis = -1;
    private boolean hasOther = false;
    private boolean isOtherNum = false; //note default - probably always text
    private boolean hasReminder = false;
    private boolean isMultiselect = true;

    public LogMultiSelectDialogData (String aTitle, long aHiveID, String[] aElems,
                                     String aCheckedSet, String aTag, long aReminderMillis,
                                     boolean aHasOther, boolean aHasReminder,
                                     boolean aIsMultiselect) {

        this.title = aTitle;
        this.hiveID = aHiveID;
        this.elems = aElems;
        this.checkedSet = aCheckedSet;
        this.tag = aTag;
        this.reminderMillis = aReminderMillis;
        this.hasOther = aHasOther;
        this.hasReminder = aHasReminder;
        this.isMultiselect = aIsMultiselect;
    }

    public LogMultiSelectDialogData (String aTitle, long aHiveID, String[] aElems,
                                     String aCheckedSet, String aTag, long aReminderMillis,
                                     boolean aHasOther, boolean aIsOtherNum, boolean aHasReminder,
                                     boolean aIsMultiselect) {

        this.title = aTitle;
        this.hiveID = aHiveID;
        this.elems = aElems;
        this.checkedSet = aCheckedSet;
        this.tag = aTag;
        this.reminderMillis = aReminderMillis;
        this.hasOther = aHasOther;
        this.isOtherNum = aIsOtherNum;
        this.hasReminder = aHasReminder;
        this.isMultiselect = aIsMultiselect;
    }

    public String getCheckedSet() {
        return checkedSet;
    }

    public String[] getElems() {
        return elems;
    }

    public boolean hasOther() {
        return hasOther;
    }

    public boolean isOtherNum() {
        return isOtherNum;
    }

    public boolean hasReminder() {
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

    public long getHiveID() {
        return hiveID;
    }

    public long getReminderMillis() {
        return reminderMillis;
    }
}