package net.tscloud.hivenotes;

import android.support.v7.app.AppCompatActivity;

public class GraphDisplayActivity extends AppCompatActivity {

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

        // go to the GraphDisplayFragment
        Fragment fragment = GraphDisplayFragment.newInstance(
            intent.getParcelableArrayListExtra(GraphActivity.INTENT_GRAPHABLE_LIST),
            intent.getLongExtra(GraphActivity.INTENT_GRAPH_START_DATE, -1),
            intent.getLongExtra(GraphActivity.INTENT_GRAPH_END_DATE, -1),
            intent.getLongExtra(GraphActivity.INTENT_APIARY_KEY, -1),
            intent.getParcelableArrayListExtra(GraphActivity.INTENT_HIVE_LIST));

        String fragTag = "GRAPH_DISPLAY_FRAG";

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.graph_container, fragment, fragTag).addToBackStack(null);
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
}