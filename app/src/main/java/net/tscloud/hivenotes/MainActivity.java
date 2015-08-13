package net.tscloud.hivenotes;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        NewApiaryFragment.OnNewApiaryFragmentInteractionListener,
        ExistingApiaryFragment.OnExistingApiaryFragmentInteractionListener {

    // test
    private static final boolean new_apiary = true;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragment;

        if (new_apiary) {
            fragment = NewApiaryFragment.newInstance("thingA", "thingB");
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
        //Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "MainActivity.onNewApiaryFragmentInteraction called..." + uri.toString());

        Fragment fragment = ExistingApiaryFragment.newInstance("thing1", "thing2");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, fragment);
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
}