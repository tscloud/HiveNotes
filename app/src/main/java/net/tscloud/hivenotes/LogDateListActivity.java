package net.tscloud.hivenotes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LogDateListActivity extends AppCompatActivity implements
        LogDateListFragment.OnLogDateListFragmentListener {

    public static final String TAG = "LogDateListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_date_list);

        // Custom Action Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Fragment fragment = new LogDateListFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.log_date_list_container, fragment)
                .commit();
    }

    @Override
    public void onLogDateListFragmentInteraction(long logDate) {
        Log.d(TAG, "onLogDateListFragmentInteraction");

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String TIME_PATTERN = "HH:mm";
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(logDate);
        String formattedDate = dateFormat.format(calendar.getTime());
        String formattedTime = timeFormat.format(calendar.getTime());
        String formattedDateTime = formattedDate + ' ' + formattedTime;

        Log.d(TAG, "date received from fragment: " + formattedDateTime);
    }
}
