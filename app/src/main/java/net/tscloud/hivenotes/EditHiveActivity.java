package net.tscloud.hivenotes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
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

import net.tscloud.hivenotes.db.Apiary;
import net.tscloud.hivenotes.db.ApiaryDAO;
import net.tscloud.hivenotes.db.Hive;
import net.tscloud.hivenotes.db.HiveDAO;

public class EditHiveActivity extends AppCompatActivity implements
        EditHiveListFragment.OnEditHiveListFragmentInteractionListener,
        EditHiveSingleFragment.OnEditHiveSingleFragmentInteractionListener {

    private static final String TAG = "EditHiveActivity";

    // starting LogEntryListActivity as subactivity
    private static final int LOG_LIST_REQ_CODE = 1;
    private static final int HIVE_SINGLE_REQ_CODE = 2;
    private static final int APIARY_REQ_CODE = 3;

    private long mApiaryKey;
    private List<Hive> mHiveList;

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
        Apiary apiaryForName = apiaryDAO.getApiaryById(mApiaryKey);
        apiaryDAO.close();

        View abView = getSupportActionBar().getCustomView();
        TextView abText = (TextView)abView.findViewById(R.id.mytext);
        abText.setText(apiaryForName.getName());

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
    }

    public void hiveOtherClickHandler (View v) {
        Log.d(TAG, "hiveOtherClickHandler called");

        LinearLayout linLay1 = (LinearLayout)v.getParent();
        TextView tv = (TextView)linLay1.findViewById(R.id.hiveEditTextView);

        Log.d(TAG, "HiveName: " + tv.getText());
    }

    @Override
    public void onEditHiveListFragmentInteraction(long apiaryID, long hiveID, boolean updateApiary) {
        Log.d(TAG, "Back from EditHiveListFragment...");

        if (!updateApiary) {
            //doesn't matter if we are updating a Hive or creating a new one --> call
            // edit Hive Intent w/ keys or lack thereof
            Log.d(TAG, "...add/update Hive - start intent EditHiveSingleActivity");
            Intent i = new Intent(this,EditHiveSingleActivity.class);
            i.putExtra(MainActivity.INTENT_APIARY_KEY, mApiaryKey);
            i.putExtra(MainActivity.INTENT_HIVE_KEY, hiveID);
            startActivityForResult(i, HIVE_SINGLE_REQ_CODE);
        }
        else {
            Log.d(TAG, "...update Apiary - start intent EditApiaryActivity");
            Intent i = new Intent(this, EditApiaryActivity.class);
            i.putExtra(MainActivity.INTENT_APIARY_KEY, apiaryID);
            startActivityForResult(i, APIARY_REQ_CODE);
        }
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

        //Create List Fragment and present
        Fragment listFrag = EditHiveListFragment.newInstance(mApiaryKey);
        getSupportFragmentManager().beginTransaction()
                //.addToBackStack(null)
                .replace(R.id.hive_list_container, listFrag)
                .commit();
    }
    /** This is strictly for testing and curiosity **
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        FragmentManager fm = getFragmentManager();
        int count = fm.getBackStackEntryCount();

        if (count == 0) {
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
}
