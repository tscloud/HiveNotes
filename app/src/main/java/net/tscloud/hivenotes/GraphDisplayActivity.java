package net.tscloud.hivenotes;

import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
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

        // go to the GraphDisplayFragment
        //<<<<<
        FragmentManager fm = getSupportFragmentManager();
        GraphDisplayFragment graphFrag = (GraphDisplayFragment)fm.findFragmentByTag(FRAG_TAG);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (graphFrag == null) {
            Log.d(TAG, "about to create newInstance of GraphDisplayFragment");

            // Get the apiary and hive key from the Intent data
            Intent intent = getIntent();

            List<GraphableData> iGraphList =
                    intent.getParcelableArrayListExtra(GraphActivity.INTENT_GRAPHABLE_LIST);
            List<Hive> iHiveList =
                    intent.getParcelableArrayListExtra(GraphActivity.INTENT_HIVE_LIST);
            long iStartDate = intent.getLongExtra(GraphActivity.INTENT_GRAPH_START_DATE, -1);
            long iEndDate = intent.getLongExtra(GraphActivity.INTENT_GRAPH_END_DATE, -1);
            long iApiary = intent.getLongExtra(GraphActivity.INTENT_APIARY_KEY, -1);

            // Possible orientation change based on whether we have multiple types of graph to do
            GraphableData prevGData = null;
            boolean doLandscape = true;

            for (GraphableData gData : iGraphList) {
                if (prevGData == null) {
                    prevGData = gData;
                } else if (!gData.getDirective().equals(prevGData.getDirective())) {
                    doLandscape = false;
                    break;
                }
            }

            if (doLandscape) {
                if (orientationChanged(Configuration.ORIENTATION_LANDSCAPE)) {
                    this.setRequestedOrientation(
                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }

            graphFrag = GraphDisplayFragment.newInstance(iGraphList, iStartDate, iEndDate,
                iApiary, iHiveList);
            fm.beginTransaction().replace(R.id.graph_container, graphFrag, FRAG_TAG).commit();
        }
        else {
            Log.d(TAG, "retained GraphDisplayFragment found");
        }
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