package net.tscloud.hivenotes;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import net.tscloud.hivenotes.db.GraphableData;
import net.tscloud.hivenotes.db.Hive;

import java.util.List;

public class GraphDisplayActivity extends AppCompatActivity implements
        GraphDisplayFragment.OnGraphDisplayFragmentInteractionListener {

    private static final String TAG = "GraphDisplayActivity";

    private static final String FRAG_TAG = "GRAPH_DISPLAY_FRAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the apiary and hive key from the Intent data
        Intent intent = getIntent();

        List<GraphableData> iGraphList =
                intent.getParcelableArrayListExtra(GraphActivity.INTENT_GRAPHABLE_LIST);
        List<Hive> iHiveList =
                intent.getParcelableArrayListExtra(GraphActivity.INTENT_HIVE_LIST);
        long iStartDate = intent.getLongExtra(GraphActivity.INTENT_GRAPH_START_DATE, -1);
        long iEndDate = intent.getLongExtra(GraphActivity.INTENT_GRAPH_END_DATE, -1);
        long iApiary = intent.getLongExtra(GraphActivity.INTENT_APIARY_KEY, -1);

        // Method #1
        // Determine orientation based on graph types
        /*
        boolean stayPortrait = false;
        String firstDir = null;
        for (GraphableData g : iGraphList) {
            if (firstDir == null) {
                firstDir = g.getDirective();
            }
            else {
                if (!firstDir.equals(g.getDirective())) {
                    stayPortrait = true;
                    break;
                }
            }
        }

        if (!stayPortrait) {
            this.setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        */

        // Method #2
        // Determine orientation based on how many graphs we need to do
        if ((iGraphList.size() < 2) &&
                (orientationChanged(Configuration.ORIENTATION_LANDSCAPE))) {
            this.setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        // go to the GraphDisplayFragment
        Log.d(TAG, "about to get newInstance of GraphDisplayFragment");

        //<<<<<
        FragmentManager fm = getFragmentManager();
        Fragment fragment = (GraphDisplayFragment)fm.findFragmentByTag(FRAG_TAG);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (fragment == null) {
            fragment = GraphDisplayFragment.newInstance(iGraphList, iStartDate, iEndDate,
                iApiary, iHiveList);
            fm.beginTransaction().replace(R.id.graph_container, fragment, FRAG_TAG).commit();
        }
        //>>>>>
    }

    // Helper method(s)
    private boolean orientationChanged(int checkThis) {
        int orientation = getResources().getConfiguration().orientation;

        return !(orientation == checkThis);
    }

    // Make the Up button perform like the Back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // This should be intercepted when back button pressed from Fragment - always set back
        //  to PORTRAIT?
        if (orientationChanged(Configuration.ORIENTATION_LANDSCAPE)) {
            this.setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onBackPressed();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}