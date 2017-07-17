package net.tscloud.hivenotes;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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

import java.util.ArrayList;
import java.util.List;

public class GraphDisplayActivity extends AppCompatActivity implements
        GraphDisplayFragment.OnGraphDisplayFragmentInteractionListener {

    private static final String TAG = "GraphDisplayActivity";

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
        if ((iGraphList.size() < 2) && (getOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)) {
            this.setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        // go to the GraphDisplayFragment
        Log.d(TAG, "about to get newInstance of GraphDisplayFragment");

        Fragment fragment = GraphDisplayFragment.newInstance(iGraphList, iStartDate, iEndDate,
                iApiary, iHiveList);

        String fragTag = "GRAPH_DISPLAY_FRAG";

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.graph_container, fragment, fragTag);
        ft.commit();
    }

    // Helper method(s)
    private int getOrientation() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getOrientation();

        return orientation;
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