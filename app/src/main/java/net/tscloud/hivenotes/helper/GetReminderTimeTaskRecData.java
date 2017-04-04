package net.tscloud.hivenotes.helper;

import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by tscloud on 4/28/16.
 */
public class GetReminderTimeTaskRecData {
    View btn;
    TextView txt;
    int type;

    public GetReminderTimeTaskRecData(View aBtn, TextView aTxt, int aType) {
        this.btn = aBtn;
        this.txt = aTxt;
        this.type = aType;
    }
}
