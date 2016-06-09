package net.tscloud.hivenotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LogDateListActivity extends AppCompatActivity implements
        LogDateListFragment.OnLogDateListFragmentListener {

    public static final String TAG = "LogDateListActivity";

    // Activity codes
    private static final int LOG_LIST_REQ_CODE = 4;

    private long mHiveKey;

    // reference to historic date/time
    public static String INTENT_LOGENTRY_HISTORY_TIME = "historyTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_date_list);

        // Custom Action Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the hive key from the Intent data
        Intent intent = getIntent();
        mHiveKey = intent.getLongExtra(MainActivity.INTENT_HIVE_KEY, -1);

        Fragment fragment = LogDateListFragment.newInstance(mHiveKey);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.log_date_list_container, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Make the Up button perform like the Back button
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLogDateListFragmentInteraction(long logDate) {
        Log.d(TAG, "date received from fragment: " + logDate);

        /** We've got a datetime --> pass it off to LogEntryListActivity so we
         *   can display/update an old log entry instead of adding a new one
         */
        //Intent i = new Intent(this,LogEntryListActivity.class);
        //i.putExtra(MainActivity.INTENT_HIVE_KEY, mHiveKey);
        //i.putExtra(LogEntryListActivity.INTENT_LOGENTRY_DATE, logDate);
        //startActivityForResult(i, LOG_LIST_REQ_CODE);

        /** We've got a datetime --> pass it back to whomever called us
         */
        Intent data = new Intent();
        data.putExtra(INTENT_LOGENTRY_HISTORY_TIME, logDate);
        setResult(RESULT_OK, data);
        finish();

    }
}
