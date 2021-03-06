package net.tscloud.hivenotes;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import net.tscloud.hivenotes.db.GraphableData;
import net.tscloud.hivenotes.db.Hive;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends HiveDataEntryActivity implements
        GraphSelectionFragment.OnGraphSelectionFragmentInteractionListener,
        GraphDisplayFragment.OnGraphDisplayFragmentInteractionListener {

    private static final String TAG = "GraphActivity";

    // Intent data keys
    public final static String INTENT_GRAPHABLE_LIST = "graphableList";
    public final static String INTENT_HIVE_LIST = "hiveList";
    public final static String INTENT_GRAPH_START_DATE = "startDate";
    public final static String INTENT_GRAPH_END_DATE = "endDate";
    public final static String INTENT_APIARY_KEY = "apiaryKey";

    // graph display subactivity
    private static final int GRAPH_DISPLAY_REQ_CODE = 11;

    private long mApiaryKey = -1;
    private long mHiveKey = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "GraphActivity onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the apiary and hive key from the Intent data
        Intent intent = getIntent();
        mApiaryKey = intent.getLongExtra(MainActivity.INTENT_APIARY_KEY, -1);
        mHiveKey = intent.getLongExtra(MainActivity.INTENT_HIVE_KEY, -1);

        // go to the GraphSelectionFragment
        fragment = GraphSelectionFragment.newInstance(mApiaryKey, mHiveKey);
        String fragTag = "GRAPH_SELECTION_FRAG";

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.graph_container, fragment, fragTag);
        ft.commit();
    }

    @Override
    protected int getContainerViewId() {
        return R.id.graph_container;
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
    public void onGraphSelectionFragmentInteraction(List<GraphableData> aToGraphList,
                                                    List<Hive> aHiveList, long aStartDate,
                                                    long aEndDate) {
        Log.d(TAG, "back from GraphSelectionFragment...Start Date: " + aStartDate +
            " End Date: " + aEndDate);

        // go to the GraphDisplayFragment
        /*
        GraphDisplayFragment fragment = GraphDisplayFragment.newInstance(
                aToGraphList, aStartDate, aEndDate, mApiaryKey, aHiveList);
        String fragTag = "GRAPH_DISPLAY_FRAG";

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.graph_container, fragment, fragTag).addToBackStack(null);
        ft.commit();
        */

        // use seperate Activity to display GraphDisplayFragment so we can enforce
        //  landscape mode
        Intent i = new Intent(this,GraphDisplayActivity.class);
        i.putParcelableArrayListExtra(INTENT_GRAPHABLE_LIST, (ArrayList)aToGraphList);
        i.putParcelableArrayListExtra(INTENT_HIVE_LIST, (ArrayList)aHiveList);
        i.putExtra(INTENT_GRAPH_START_DATE, aStartDate);
        i.putExtra(INTENT_GRAPH_END_DATE, aEndDate);
        i.putExtra(INTENT_APIARY_KEY, mApiaryKey);
        //startActivityForResult(i, GRAPH_DISPLAY_REQ_CODE);
        startActivity(i);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
