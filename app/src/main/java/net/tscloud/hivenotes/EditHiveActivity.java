package net.tscloud.hivenotes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
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

    /**
     * The PagerAdapter that will provide fragments for each of the
     * sections. We use a FragmentPagerAdapter derivative, which will
     * keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a FragmentStatePagerAdapter.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The ViewPager that will host the section contents.
     */
    ViewPager mViewPager;

    private static final String TAG = "EditHiveActivity";

    // starting LogEntryListActivity as subactivity
    private static final int request_code = 5;

    private long mApiaryKey;
    private List<Hive> mHiveList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hive);

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

        // Get the Hive list
        mHiveList = deliverHiveList(mApiaryKey, false);

        //List for all out fragments
        List<Fragment> fragments = getFragments(mApiaryKey, mHiveList);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), fragments);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    private List<Fragment> getFragments(long aApiaryKey, List<Hive> aHiveList){
        List<Fragment> fList = new ArrayList<Fragment>();

        fList.add(EditHiveListFragment.newInstance(aApiaryKey));

        for (Hive aHive : aHiveList) {
            fList.add(EditHiveSingleFragment.newInstance(mApiaryKey, aHive.getId()));
        }

        // for the last page => put in a blank add Hive frag
        fList.add(EditHiveSingleFragment.newInstance(mApiaryKey, -1));

        //fList.add(PlaceholderFragment.newInstance("Fragment 2", R.layout.fragment2_log_entry));
        //fList.add(PlaceholderFragment.newInstance("Fragment 3", R.layout.fragment3_log_entry));

        return fList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     *Handlers of buttons defined in hive_edit_button, i.e. buttons in each of the list items
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
        //i.putExtra("apiaryKey", apiaryId);
        startActivityForResult(i, request_code);
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
            // if id is not -1 => we selected something
            // else we're making a new Hive
            if (hiveID == -1) {
                Log.d(TAG, "...add new Hive");
                // Do new Hive stuff
                mViewPager.setCurrentItem(mViewPager.getAdapter().getCount() - 1, false);
            } else {
                Log.d(TAG, "...edit existing Hive");
                // Do selected Hive stuff
                int hivePos = 0;
                for (int i = 0; i < mHiveList.size(); i++) {
                    if (hiveID == (mHiveList.get(i).getId())) {
                        hivePos = i;
                    }
                }
                // setCurrentItem "1" based?
                mViewPager.setCurrentItem(hivePos + 1, false);
            }
        }
        else {
            Log.d(TAG, "...update Apiary - return to MainActivity");

            Intent data = new Intent();
            data.putExtra("apiaryKey", apiaryID);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onEditHiveSingleFragmentInteraction(long hiveID, boolean newHive) {
        Log.d(TAG, "Back from EditHiveSingleFragment: update Apiary");

        // Do some stuff w/ the adapter
        SectionsPagerAdapter adapter = (SectionsPagerAdapter)mViewPager.getAdapter();

        // reshow the list of Hives
        Fragment fragAdd = EditHiveListFragment.newInstance(mApiaryKey);
        adapter.setItem(fragAdd, 0);

        if (newHive) {
            // just add a blank fragment at the end for Hive adding
            adapter.addItem(EditHiveSingleFragment.newInstance(mApiaryKey, -1));
        }

        adapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(0, false);
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

    /**
     * A FragmentPagerAdapter that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragments;

        public SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        public void setItem(Fragment frag, int pos) {
            this.fragments.set(pos, frag);
        }

        public void addItem(Fragment frag) {
            // should always be a blank frag for Hive adding?
            this.fragments.add(frag);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            // ***** Will this prevent the Pager from going to the last page?
            //return this.fragments.size() - 1;
            return this.fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
        public static final String FRAG_LAYOUT = "FRAG_LAYOUT";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(String message, int fragment_layout) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(EXTRA_MESSAGE, message);
            args.putInt(FRAG_LAYOUT, fragment_layout);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            String message = getArguments().getString(EXTRA_MESSAGE);
            int fragment_layout = getArguments().getInt(FRAG_LAYOUT);
            View rootView = inflater.inflate(fragment_layout, container, false);

            return rootView;
        }
    }

}
