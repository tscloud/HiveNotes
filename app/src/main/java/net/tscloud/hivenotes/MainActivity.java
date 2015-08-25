package net.tscloud.hivenotes;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import net.tscloud.hivenotes.db.Apiary;
import net.tscloud.hivenotes.db.ApiaryDAO;
import net.tscloud.hivenotes.db.Profile;
import net.tscloud.hivenotes.db.ProfileDAO;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        EditApiaryFragment.OnNewApiaryFragmentInteractionListener,
        HomeFragment.OnHomeFragmentInteractionListener,
        EditProfileFragment.OnNewProfileFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    // may be needed to pass to various Fragments
    private boolean newProfile = true;
    private Profile theProfile = null;
    private List<Apiary> theApiaryList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Custom Action Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
//        View abView = getSupportActionBar().getCustomView();
//        TextView abText = (TextView)abView.findViewById(R.id.mytext);
//        abText.setText("NewHiveNites");

        // read Profile table to see if this is first time thru
        // also set instance Profile for it may be needed elsewhere
        Log.d(TAG, "reading Profile table");
        ProfileDAO profileDAO = new ProfileDAO(this);
        theProfile = profileDAO.getProfile();
        profileDAO.close();

        if (theProfile == null) {
            Log.d(TAG, "No profile");
            newProfile = true;
        }
        else{
            Log.d(TAG, "profile name: " + theProfile.getName());
            newProfile = false;

            // read Apiary table
            // will either show list or add button
            theApiaryList = getApiaryList();
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder,
                HomeFragment.newInstance(newProfile, getApiaryNames()), "HOME_FRAG");//.addToBackStack("backstacktag3");
        ft.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    @Override
    public void onBackPressed() {
        Log.d(TAG, "back button pressed");

        // check the fragment that is displayed
        Fragment myFragment = getSupportFragmentManager().findFragmentByTag("HOME_FRAG");

        if (myFragment != null && myFragment.isVisible()) {
            // add your code here
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    */

    @Override
    public void onNewApiaryFragmentInteraction(Uri uri) {
        Log.d(TAG, "MainActivity.onNewApiaryFragmentInteraction called..." + uri.toString());

        //  This is where we want to show apiary list - but we have to reread
        //    b/c we have added a new one <- the right thing to do might be
        //    to pass the apiary list to avoid a DB read
        theApiaryList = getApiaryList();

        Fragment fragment = HomeFragment.newInstance(newProfile, getApiaryNames());

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, fragment).addToBackStack("backstacktag2");
        ft.commit();
    }

    @Override
    public void onHomeFragmentInteraction(String apiaryName) {
        Log.d(TAG, "MainActivity.onHomeFragmentInteraction called...Apiary name: " + apiaryName);

        if ((apiaryName == null) || (apiaryName.length() == 0)) {
            Fragment fragment = null;
            String fragTag = null;

            if (newProfile) {
                fragment = EditProfileFragment.newInstance("thing1", "thing2");
                fragTag = "EDIT_PROFILE_FRAG";
            } else {
                fragment = EditApiaryFragment.newInstance("thing3", theProfile);
                fragTag = "EDIT_APIARY_FRAG";
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_placeholder, fragment, fragTag).addToBackStack("backstacktag1");
            ft.commit();
        }
        else {
            // IMPORTANT -- this is how we get to EditHiveActivity page viewer
            // start EditHiveActivity activity
            Intent i = new Intent(this,EditHiveActivity.class);
            i.putExtra("apiaryKey", getApiaryKey(apiaryName));
            startActivity(i);
        }
    }

    @Override
    public void onNewProfileFragmentInteraction(Uri uri, Profile profile) {
        Log.d(TAG, "MainActivity.onNewProfileFragmentInteraction called..." + uri.toString());

        // don't need to make a new Profile
        newProfile = false;
        // set the instance var w/ the Profile we just made
        theProfile = profile;

        Fragment fragment = HomeFragment.newInstance(newProfile, getApiaryNames());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, fragment);
        ft.commit();
    }

    // Utility method to get list of a Profile's apiary names
    //  use instance variables so no args and no return
    private List<Apiary> getApiaryList() {
        // read Apiary table
        Log.d(TAG, "reading Apiary table");
        ApiaryDAO apiaryDAO = new ApiaryDAO(this);
        List<Apiary> apiaryList = apiaryDAO.getApiaryList(theProfile.getId());
        apiaryDAO.close();

        if (!apiaryList.isEmpty()) {
            Log.d(TAG, "found Apiary list");
        }
        else {
            Log.d(TAG, "no Apiary list");

        }

        return apiaryList;
    }

    // Utility method to get the list of apiary names from the list of Apiaries
    private ArrayList<String> getApiaryNames () {
        ArrayList<String> apiaryNameList  = new ArrayList<String>();

        if ((theApiaryList != null) && (!theApiaryList.isEmpty())) {

            // display apiary list on HomeFragment
            for (Apiary a : theApiaryList) {
                apiaryNameList.add(a.getName());
            }
        }

        return apiaryNameList;
    }

    // Utility method to get apiary key from apiary name
    // A bit of a HACK - should consider putting index on Name in Apiary table
    private long getApiaryKey(String apiaryName) {
        long result = -1;

        for (Apiary apiary : theApiaryList) {
            if (apiary.getName().equals(apiaryName)) {
                if (result != -1) {
                    // >1 apiary w/ the same name
                    // this would bad
                    Log.d(TAG, "Apiary BADNESS...>1 apiary w/ the same name");
                    result = -1;
                    break;
                }
                else {
                    result = apiary.getId();
                }
            }
        }
        return result;
    }

}