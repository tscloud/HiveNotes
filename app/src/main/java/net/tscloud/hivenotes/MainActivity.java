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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.tscloud.hivenotes.db.MyDBHandler;
import net.tscloud.hivenotes.db.Profile;
import net.tscloud.hivenotes.db.ProfileDAO;

public class MainActivity extends AppCompatActivity implements
        NewApiaryFragment.OnNewApiaryFragmentInteractionListener,
        ExistingApiaryFragment.OnExistingApiaryFragmentInteractionListener,
        NewProfileFragment.OnNewProfileFragmentInteractionListener {

    // test
    private boolean new_apiary = true;
    private static final String TAG = "MainActivity";

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

        // read Profile table to see if this is first time thru or whatever
        Log.d(TAG, "reading Profile table");
        ProfileDAO profileDAO = new ProfileDAO(this);
        Profile profile = profileDAO.getProfile();
        profileDAO.close();
        if (profile == null) {
            Log.d(TAG, "No profile");
            new_apiary = true;
        }
        else{
            Log.d(TAG, "profile name: " + profile.getName());
            new_apiary = false;
        }

        Fragment fragment;

        if (new_apiary) {
            fragment = NewProfileFragment.newInstance("thingA", "thingB");
        } else {
            fragment = ExistingApiaryFragment.newInstance("thing1", "thing2");
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, fragment);
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

    @Override
    public void onNewApiaryFragmentInteraction(Uri uri) {
        Log.d(TAG, "MainActivity.onNewApiaryFragmentInteraction called..." + uri.toString());

        Fragment fragment = ExistingApiaryFragment.newInstance("thing1", "thing2");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, fragment).addToBackStack("tag1");
        ft.commit();
    }

    @Override
    public void onExistingApiaryFragmentInteraction(Uri uri) {
        Log.d(TAG, "MainActivity.onExistingApiaryFragmentInteraction called..." + uri.toString());

        // start LogEntryActivity activity
        Intent i = new Intent(this,LogEntryActivity.class);
        i.putExtra("fromMain", uri.toString());
        startActivity(i);
    }

    @Override
    public void onNewProfileFragmentInteraction(Uri uri) {
        Log.d(TAG, "MainActivity.onNewProfileFragmentInteraction called..." + uri.toString());

        Fragment fragment = NewApiaryFragment.newInstance("thing1", "thing2");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, fragment);
        ft.commit();
    }
}