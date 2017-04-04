package net.tscloud.hivenotes.helper;

import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by tscloud on 4/28/16.
 */
public class GetReminderTimeTaskData extends GetReminderTimeTaskRecData {
    int taskInd;
    long hive;
    Calendar cal;
    DateFormat dateFormat;
    SimpleDateFormat timeFormat;

    public GetReminderTimeTaskData(View aBtn, TextView aTxt, int aType, long aHive, int aTaskInd,
                            Calendar aCal, DateFormat aDateFormat, SimpleDateFormat aTimeFormat) {
        super(aBtn, aTxt, aType);
        this.taskInd = aTaskInd;
        this.hive = aHive;
        this.cal = aCal;
        this.dateFormat = aDateFormat;
        this.timeFormat = aTimeFormat;
    }
}
