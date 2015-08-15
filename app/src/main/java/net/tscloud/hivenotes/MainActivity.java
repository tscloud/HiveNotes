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

        // Custom Action Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
//        View abView = getSupportActionBar().getCustomView();
//        TextView abText = (TextView)abView.findViewById(R.id.mytext);
//        abText.setText("NewHiveNites");

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

        Log.d(TAG, "creating DB");
        MyDBHandler dbHandler = MyDBHandler.getInstance(this);

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