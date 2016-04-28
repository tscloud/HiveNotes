package net.tscloud.hivenotes.helper;

import android.widget.Button;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by tscloud on 4/28/16.
 */
public static class GetReminderTimeTaskData {
    Button btn;
    TextView txt;
    int type;
    long hive;
    int taskInd;
    Calendar cal;
    DateFormat dateFormat;
    SimpleDateFormat timeFormat;

    GetReminderTimeTaskData(Button aBtn, TextView aTxt, int aType, long aHive, int aTaskInd,
                            Calendar aCal, DateFormat aDateFormat, SimpleDateFormat aTimeFormat) {
        this.btn = aBtn;
        this.txt = aTxt;
        this.type = aType;
        this.hive = aHive;
        this.taskInd = aTaskInd;
        this.cal = aCal;
        this.dateFormat = aDateFormat;
        this.timeFormat = aTimeFormat;
    }
}
