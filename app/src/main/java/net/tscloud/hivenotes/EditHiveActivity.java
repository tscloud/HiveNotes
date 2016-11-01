package net.tscloud.hivenotes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.tscloud.hivenotes.db.Apiary;
import net.tscloud.hivenotes.db.ApiaryDAO;
import net.tscloud.hivenotes.db.Hive;
import net.tscloud.hivenotes.db.HiveDAO;
import net.tscloud.hivenotes.db.Weather;
import net.tscloud.hivenotes.db.WeatherDAO;
import net.tscloud.hivenotes.helper.HivePollen;
import net.tscloud.hivenotes.helper.HiveWeather;

public class EditHiveActivity extends AppCompatActivity implements
        EditHiveListFragment.OnEditHiveListFragmentInteractionListener,
        EditHiveSingleFragment.OnEditHiveSingleFragmentInteractionListener {

    private static final String TAG = "EditHiveActivity";

    // Activity codes
    private static final int LOG_LIST_REQ_CODE = 8;
    private static final int HIVE_SINGLE_REQ_CODE = 9;
    private static final int APIARY_REQ_CODE = 10;

    private long mApiaryKey = -1;
    private List<Hive> mHiveList;
    private Apiary mApiary;

    // task references - needed to kill tasks on Activity Destroy
    private WeatherCallTask mTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hive_list);

        // Custom Action Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the apiary key from the Intent data
        Intent intent = getIntent();
        mApiaryKey = intent.getLongExtra("apiaryKey", -1);

        Log.d(TAG, "Called w/ apiary key: " + mApiaryKey);

        // need the Apiary name for the tile bar
        Log.d(TAG, "reading Apiary table");
        ApiaryDAO apiaryDAO = new ApiaryDAO(this);
        mApiary = apiaryDAO.getApiaryById(mApiaryKey);
        apiaryDAO.close();

        View abView = getSupportActionBar().getCustomView();
        TextView abText = (TextView)abView.findViewById(R.id.mytext);
        abText.setText(mApiary.getName());

        // Get the Hive list - necessary here?
        mHiveList = deliverHiveList(mApiaryKey, false);

        //Create List Fragment and present
        Fragment listFrag = EditHiveListFragment.newInstance(mApiaryKey);
        getSupportFragmentManager().beginTransaction()
                //.addToBackStack(null)
                .replace(R.id.hive_list_container, listFrag)
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Hive list image button click handlers
     */
    public void hiveFeedingClickHandler (View v) {
        Log.d(TAG, "hiveFeedingClickHandler called");

        LinearLayout linLay1 = (LinearLayout)v.getParent();
        TextView tv = (TextView)linLay1.findViewById(R.id.hiveEditTextView);

        Log.d(TAG, "HiveName: " + tv.getText());
        Log.d(TAG, "HiveID: " + tv.getTag());

        // IMPORTANT -- this is how we get to LogEntryListActivity log activity
        // start LogEntryListActivity activity
        Intent i = new Intent(this,LogEntryListActivity.class);
        i.putExtra(MainActivity.INTENT_HIVE_KEY, (long)tv.getTag());
        startActivityForResult(i, LOG_LIST_REQ_CODE);
    }

    public void hiveGeneralClickHandler (View v) {
        Log.d(TAG, "hiveGeneralClickHandler called");

        LinearLayout linLay1 = (LinearLayout)v.getParent();
        TextView tv = (TextView)linLay1.findViewById(R.id.hiveEditTextView);

        Log.d(TAG, "HiveName: " + tv.getText());

        // IMPORTANT -- this is how we get to LogDateListActivity log activity
        // start LogEntryListActivity activity
        //Intent i = new Intent(this,LogDateListActivity.class);
        //i.putExtra(MainActivity.INTENT_HIVE_KEY, (long)tv.getTag());
        //startActivityForResult(i, LOG_DATE_REQ_CODE);
    }

    public void hiveOtherClickHandler (View v) {
        Log.d(TAG, "hiveOtherClickHandler called");

        LinearLayout linLay1 = (LinearLayout)v.getParent();
        TextView tv = (TextView)linLay1.findViewById(R.id.hiveEditTextView);

        Log.d(TAG, "HiveName: " + tv.getText());
    }

    /**
     * Toolbar click handlers
     *  Note: Functions these click handlers provide originally implemented in click
     *        handlers defined in fragment attached to buttons replaced by toolbar.
     *        Fragment defined interface specifying these methods not really necessary
     *        but we'll keep it around.
     */
   public void hiveFeedingToolClickHandler (View v) {
        // means we want to make a new Hive
        Log.d(TAG, "hiveFeedingToolClickHandler called");
        onEditHiveListFragmentCreateHive(-1);
    }

    public void hiveGeneralToolClickHandler (View v) {
        // means we want to update the Apiary
        Log.d(TAG, "hiveGeneralToolClickHandler called");
        onEditHiveListFragmentUpdateApiary(mApiaryKey);
    }

    public void hiveOtherToolClickHandler (View v) {
        // means we want do some weather
        Log.d(TAG, "hiveOtherToolClickHandler called");
        onEditHiveListFragmentWeather(mApiaryKey);
    }

    public void hiveProductionToolClickHandler (View v) {
        Toast.makeText(getApplicationContext(), R.string.na_msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "hiveProductionToolClickHandler called");
    }

    public void hivePestToolClickHandler (View v) {
        Toast.makeText(getApplicationContext(), R.string.na_msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "hivePestToolClickHandler called");
    }

    @Override
    public void onEditHiveListFragmentCreateHive(long hiveID) {
        Log.d(TAG, "Back from EditHiveListFragment...");
        Log.d(TAG, "...add/update Hive - start intent EditHiveSingleActivity");

        //doesn't matter if we are updating a Hive or creating a new one --> call
        // edit Hive Intent w/ keys or lack thereof
        Intent i = new Intent(this,EditHiveSingleActivity.class);
        i.putExtra(MainActivity.INTENT_APIARY_KEY, mApiaryKey);
        i.putExtra(MainActivity.INTENT_HIVE_KEY, hiveID);
        startActivityForResult(i, HIVE_SINGLE_REQ_CODE);
    }

    @Override
    public void onEditHiveListFragmentUpdateApiary(long apiaryID) {
        Log.d(TAG, "Back from EditHiveListFragment...");
        Log.d(TAG, "...update Apiary - start intent EditApiaryActivity");

        Intent i = new Intent(this, EditApiaryActivity.class);
        i.putExtra(MainActivity.INTENT_APIARY_KEY, apiaryID);
        startActivityForResult(i, APIARY_REQ_CODE);
    }

    @Override
    public void onEditHiveListFragmentWeather(long apiaryID) {
        Log.d(TAG, "Back from EditHiveListFragment...");
        Log.d(TAG, "...do some weather - start intent EditApiaryActivity");

        String wuQuery;

        if ((mApiary.getLatitude() == 0) && (mApiary.getLongitude() == 0)) {
            wuQuery = mApiary.getPostalCode();
        }
        else {
            wuQuery = mApiary.getLatitude() + "," + mApiary.getLongitude();
        }

        Log.d(TAG, "about to start WeatherCallTask AsyncTask");
        mTask = new WeatherCallTask(this, mApiary.getPostalCode(),
                                    mApiary.getLatitude(),
                                    mApiary.getLongitude());
        mTask.execute();
    }

    @Override
    public void onEditHiveSingleFragmentInteraction(long hiveID, boolean newHive, boolean deleteHive) {
        Log.d(TAG, "Back from EditHiveSingleFragment: update Apiary");

        //Create List Fragment and present
        Fragment listFrag = EditHiveListFragment.newInstance(mApiaryKey);
        getSupportFragmentManager().beginTransaction()
                //.addToBackStack(null)
                .replace(R.id.hive_list_container, listFrag)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (APIARY_REQ_CODE):
                boolean doBackPressed = true;

                // Intent <- Bundle <- Parcelable (Apiary)
                // 3 scenarios:
                //  Intent is null => back button pressed => present Home
                //  Parcelable (Apiary) is null => Apiary deleted => present Home
                //  Parcelable (Apiary) is not null => Apiary updated => present HiveList

                if (data != null) {
                    Bundle bundleData = data.getExtras();
                    if (bundleData.keySet().contains(EditApiaryActivity.INTENT_APIARY_DATA)) {
                        Log.d(TAG, "received Apiary data object");
                        mApiary = bundleData.getParcelable(EditApiaryActivity.INTENT_APIARY_DATA);
                        if (mApiary != null) {
                            mApiaryKey = mApiary.getId();
                            doBackPressed = false;
                        }
                        else {
                            mApiaryKey = -1;
                        }
                    }
                }

                if (doBackPressed) {
                    // do what would happen if back button pressed
                    mApiary = null;
                    this.onBackPressed();
                }
                else {
                    // create List Fragment and present
                    Fragment listFrag = EditHiveListFragment.newInstance(mApiaryKey);
                    getSupportFragmentManager().beginTransaction()
                            //.addToBackStack(null)
                            .replace(R.id.hive_list_container, listFrag)
                            .commit();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mTask != null) {
            mTask.cancel(false);
        }

        super.onDestroy();
    }

    /** This is strictly for testing and curiosity **
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();

        if (count != 0) {
            for (int i=0; i<count; i++) {
                FragmentManager.BackStackEntry backEntry = fm.getBackStackEntryAt(i);
                Log.d(TAG, "onBackPressed(): backEntry name: " + backEntry.getName());
            }
        }
    }

    @Override
    public List<Hive> deliverHiveList(long anApiaryKey, boolean reread) {
        // Read the Hive list for the fragments
        if ((mHiveList == null) || (mHiveList.isEmpty()) || reread) {
            Log.d(TAG, "reading Hive table");
            HiveDAO hiveDAO = new HiveDAO(this);
            mHiveList = hiveDAO.getHiveList(mApiaryKey);
            hiveDAO.close();
        }

        return mHiveList;
    }

    /** call weather WS to get weather data and persist
     */
    public class WeatherCallTask extends AsyncTask<Void, Void, Integer> {

        public static final String TAG = "WeatherCallTask";

        private Context ctx;
        private String postalCode;
        float lat = 0;
        float lon = 0;

        private ProgressDialog dialog =
                new ProgressDialog(EditHiveActivity.this);

        public WeatherCallTask(Context aCtx, String aPostalCode, float aLat, float aLon) {
            ctx = aCtx;
            postalCode = aPostalCode;
            lat = aLat;
            lon = aLon;
            Log.d(TAG, "WeatherCallTask(" + Thread.currentThread().getId() + ") : constructor");
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getResources().getString(R.string.weather_service_msg_string));
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Void... unused) {
            Integer reply = 0;

            Log.d(TAG, "WeatherCallTask("+ Thread.currentThread().getId() + ") : doInBackground");
            // Call the weather service
            // build query string <-lat/lon should be present unless lat/lon
            //  & zip are not present
            String queryString = null;

            if ((lat != 0) && (lon != 0)) {
                queryString = lat + "," + lon;
            }
            else if ((postalCode != null) && (postalCode.length() != 0)) {
                queryString = postalCode;
            }

            if (queryString != null) {
                Log.d(TAG, "about to make weather service calls");

                // Call the weather service
                HiveWeather myHiveWeather = new HiveWeather();
                Weather weatherDO = myHiveWeather.requestWundergroundExtended(queryString);
                Log.d(TAG, "returned from wunderground WS call");

                // Call the pollen "service"
                // postalCode should not by null - check anyway?
                HivePollen hivePollen = new HivePollen(postalCode);
                Log.d(TAG, "returned from pollen page call");
                /*
                Log.d(TAG, "Today: " + hivePollen.getDateToday());
                Log.d(TAG, "Pollen Today: " + hivePollen.getPollenIndexToday());
                Log.d(TAG, "Type: " + hivePollen.getPollenType());
                Log.d(TAG, "City: " + hivePollen.getCity());
                Log.d(TAG, "Zip: " + hivePollen.getZipcode());
                int i = 0;
                for (Date d : hivePollen.getCorrespondingDate()) {
                    Log.d(TAG, "Date "+ i++ + ": " + d);
                }
                i = 0;
                for (String s : hivePollen.getPollenIndex()) {
                    Log.d(TAG, "Pollen Index "+ i++ + ": " + s);
                }
                */

                // Load DO w/ pollen data
                weatherDO.setPollenCount(hivePollen.getPollenIndexToday());
                weatherDO.setPollution(hivePollen.getPollenType());

                // Persist what comes back
                WeatherDAO weatherDAO = new WeatherDAO(ctx);
                weatherDO.setApiary(mApiaryKey);
                weatherDAO.createWeather(weatherDO);
            }
            else {
                reply = -1;
            }

            return(reply);
        }

        @Override
        protected void onPostExecute(Integer ret) {
            Log.d(TAG, "WeatherCallTask("+ Thread.currentThread().getId() + ") : onPostExecute");

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            String msg = null;
            if (ret >= 0) {
                msg = "Weather service call complete...data persisted";
            }
            else {
                msg = "no loaction data : unable to make weather service calls";
            }
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();


            // all we need to do is nullify the Task reference
            mTask = null;
        }
    }
}
