package net.tscloud.hivenotes;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class EditApiaryActivity extends AppCompatActivity implements
        EditApiaryFragment.OnEditApiaryFragmentInteractionListener {

    private static final String TAG = "EditApiaryActivity";
    private long mProfileKey = -1;
    private long mApiaryKey = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_apiary);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the profile key from the Intent data
        Intent intent = getIntent();
        mProfileKey = intent.getLongExtra(MainActivity.INTENT_PROFILE_KEY, -1);
        mApiaryKey = intent.getLongExtra(MainActivity.INTENT_APIARY_KEY, -1);

        Fragment fragment = EditApiaryFragment.newInstance(mProfileKey, mApiaryKey);
        String fragTag = "EDIT_PROFILE_FRAG";

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, fragment, fragTag);
        ft.commit();
    }

    @Override
    public void onEditApiaryFragmentInteraction() {
        Log.d(TAG, "...update Apiary - return to MainActivity");

        Intent data = new Intent();
        //data.putExtra(MainActivity.INTENT_PROFILE_KEY, mProfile.getId());
        setResult(RESULT_OK, data);
        finish();

    }
}
