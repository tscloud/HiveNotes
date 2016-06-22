package net.tscloud.hivenotes;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class GraphActivity extends AppCompatActivity implements
        GraphSelectionFragment.OnFragmentInteractionListener {

    private static final String TAG = "GraphActivity";
    private long mApiaryKey = -1;
    private long mHiveKey = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the apiary and hive key from the Intent data
        Intent intent = getIntent();
        mApiaryKey = intent.getLongExtra(MainActivity.INTENT_APIARY_KEY, -1);
        mHiveKey = intent.getLongExtra(MainActivity.INTENT_HIVE_KEY, -1);

        Fragment fragment = GraphSelectionFragment.newInstance(mApiaryKey, mHiveKey);
        String fragTag = "GRAPH_SELECTION_FRAG";

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.graph_container, fragment, fragTag);
        ft.commit();
    }

    // Make the Up button perform like the Back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
