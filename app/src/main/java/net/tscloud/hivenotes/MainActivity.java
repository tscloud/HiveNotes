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
    private ArrayList<String> apiaryNameList = null;

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
            getApiaryNames();
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder,
                HomeFragment.newInstance(newProfile, apiaryNameList), "HOME_FRAG");//.addToBackStack("backstacktag3");
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
        getApiaryNames();

        Fragment fragment = HomeFragment.newInstance(newProfile, apiaryNameList);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, fragment).addToBackStack("backstacktag2");
        ft.commit();
    }

    @Override
    public void onHomeFragmentInteraction(String apiaryName) {
        Log.d(TAG, "MainActivity.onHomeFragmentInteraction called...Apiary name: " + apiaryName);

        if ((apiaryName == null) || (apiaryName.length() != 0)) {
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
            // IMPORTANT -- this is how we get to LogEntry page viewer
            // start EditHiveActivity activity
            Intent i = new Intent(this,EditHiveActivity.class);
            i.putExtra("apiaryName", apiaryName);
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

        Fragment fragment = HomeFragment.newInstance(newProfile, apiaryNameList);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, fragment);
        ft.commit();
    }

    // Utility method to get list of a Profile's apiary names
    //  use instance variables so no args and no return
    private void getApiaryNames(){
        // read Apiary table
        // will either show list or add button
        Log.d(TAG, "reading Apiary table");
        ApiaryDAO apiaryDAO = new ApiaryDAO(this);
        List<Apiary> apiaryList = apiaryDAO.getApiaryList(theProfile.getId());
        apiaryDAO.close();

        if (!apiaryList.isEmpty()){
            Log.d(TAG, "found Apiary list");

            // display apiary list on HomeFragment
            apiaryNameList = new ArrayList<>();
            for (Apiary a : apiaryList){
                apiaryNameList.add(a.getName());
            }
        }
        else {
            Log.d(TAG, "no Apiary list");
        }
    }

}