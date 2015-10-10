package net.tscloud.hivenotes;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import net.tscloud.hivenotes.db.Profile;
import net.tscloud.hivenotes.db.ProfileDAO;

public class EditProfileActivity extends AppCompatActivity implements
        EditProfileFragment.OnEditProfileFragmentInteractionListener {

    private static final String TAG = "EditProfileActivity";
    private long mProfileKey = -1;
    private Profile mProfile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the profile key from the Intent data
        Intent intent = getIntent();
        mProfileKey = intent.getLongExtra(MainActivity.INTENT_PROFILE_KEY, -1);

        Fragment fragment = EditProfileFragment.newInstance(mProfileKey);
        String fragTag = "EDIT_PROFILE_FRAG";

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, fragment, fragTag);
        ft.commit();
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
    public void onEditProfileFragmentInteraction(Profile profile) {
        // don't need to make a new Profile
        // set the instance var w/ the Profile we just made
        mProfile = profile;

        Log.d(TAG, "...update Profile - return to MainActivity");

        Intent data = new Intent();
        data.putExtra(MainActivity.INTENT_PROFILE_KEY, mProfile.getId());
        setResult(RESULT_OK, data);
        finish();

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
}
