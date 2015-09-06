package net.tscloud.hivenotes;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import net.tscloud.hivenotes.db.Apiary;
import net.tscloud.hivenotes.db.ApiaryDAO;
import net.tscloud.hivenotes.db.Profile;
import net.tscloud.hivenotes.db.ProfileDAO;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        EditApiaryFragment.OnEditApiaryFragmentInteractionListener,
        HomeFragment.OnHomeFragmentInteractionListener,
        EditProfileFragment.OnEditProfileFragmentInteractionListener,
        EditHiveSingleFragment.OnEditHiveSingleFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    // starting EditHiveActivity as subactivity
    private static final int request_code = 5;

    private Profile mProfile = null;
    private List<Apiary> mApiaryList = null;

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
        mProfile = getProfile();

        if (mProfile == null) {
            Log.d(TAG, "No profile");
        }
        else{
            Log.d(TAG, "profile name: " + mProfile.getName());

            // read Apiary table
            // will either show list or add button
            mApiaryList = getApiaryList(mProfile.getId());
        }
        presentHome();
    }

    private void presentHome() {
        // Home screen display - may have to do this at other times besides onCreate
        long fragProfileID = -1;
        if (mProfile != null) {
            fragProfileID = mProfile.getId();
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder,
                HomeFragment.newInstance(fragProfileID), "HOME_FRAG");//.addToBackStack("backstacktag3");
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
    public void onEditApiaryFragmentInteraction() {
        Log.d(TAG, "MainActivity.onEditApiaryFragmentInteraction called...");

        //  This is where we want to show apiary list - but we have to reread
        //    b/c we have added a new one <- the right thing to do might be
        //    to pass the apiary list to avoid a DB read
        mApiaryList = getApiaryList(mProfile.getId());

        presentHome();
    }

    @Override
    public void onHomeFragmentInteraction(Long apiaryId, boolean dbDeleted) {
        Log.d(TAG, "MainActivity.onHomeFragmentInteraction called...Apiary name: " + apiaryId);


        if (dbDeleted){
            // redisplay Home screen
            Toast.makeText(getApplicationContext(), "DB successfully deleted =)",
                    Toast.LENGTH_LONG).show();
            // clear instance vars
            mProfile = null;
            mApiaryList = null;

            presentHome();
        }
        else if (apiaryId == null) {
            Fragment fragment = null;
            String fragTag = null;

            if (mProfile == null) {
                // we should ot be able to get here to edit an exiting Profile
                // so always set profileID = -1
                fragment = EditProfileFragment.newInstance(-1);
                fragTag = "EDIT_PROFILE_FRAG";
            }
            else {
                fragment = EditApiaryFragment.newInstance(mProfile.getId(), -1);
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
            i.putExtra("apiaryKey", apiaryId);
            startActivityForResult(i, request_code);
        }
    }

    @Override
    public void onEditProfileFragmentInteraction(Profile profile) {
        Log.d(TAG, "MainActivity.onEditProfileFragmentInteraction called...");

        // don't need to make a new Profile
        // set the instance var w/ the Profile we just made
        mProfile = profile;

        presentHome();
    }

    @Override
    public void onEditHiveSingleFragmentInteraction(long hiveID, boolean newHive) {
        Log.d(TAG, "MainActivity.onEditHiveSingleFragmentInteraction called...");

        // IMPORTANT -- this is how we get to EditHiveActivity page viewer
        // start EditHiveActivity activity
        Intent i = new Intent(this,EditHiveActivity.class);
        i.putExtra("apiaryKey", hiveID);
        startActivityForResult(i, request_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == request_code) && (resultCode == RESULT_OK)) {
            Log.d(TAG, "Returned from requestCode = " + requestCode);
            /*
            boolean showNewHiveScreen = data.getExtras().getBoolean("showNewHiveScreen");
            long apiaryKey = data.getExtras().getLong("apiaryKey");

            if (showNewHiveScreen) {
                Fragment fragment = EditHiveSingleFragment.newInstance(apiaryKey, );

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_placeholder, fragment, "EDIT_HIVE_SINGLE_FRAG").addToBackStack("backstacktagB");
                ft.commit();
            }
            */
        }
    }

    @Override
    public Profile deliverProfile() {
        Profile reply = mProfile;

        if (reply == null) {
            reply = getProfile();
        }
        return reply;
    }

    @Override
    public List<Apiary> deliverApiaryList(long aProfileID) {
        List<Apiary> reply = mApiaryList;

        if ((reply == null) || (reply.isEmpty())) {
            reply = getApiaryList(aProfileID);
        }

        return reply;
    }

    // Utility method to get Profile
    private Profile getProfile() {
        // read Profile
        Log.d(TAG, "reading Profile table");
        ProfileDAO profileDAO = new ProfileDAO(this);
        Profile reply = profileDAO.getProfile();
        profileDAO.close();

        return reply;
    }

    // Utility method to get list of a Profile's apiary
    private List<Apiary> getApiaryList(long aProfileID) {
        // read Apiary table
        Log.d(TAG, "reading Apiary table");
        ApiaryDAO apiaryDAO = new ApiaryDAO(this);
        List<Apiary> reply = apiaryDAO.getApiaryList(aProfileID);
        apiaryDAO.close();

        if (!reply.isEmpty()) {
            Log.d(TAG, "found Apiary list");
        }
        else {
            Log.d(TAG, "no Apiary list");
        }

        return reply;
    }
}