package net.tscloud.hivenotes;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import net.tscloud.hivenotes.db.Apiary;
import net.tscloud.hivenotes.db.ApiaryDAO;
import net.tscloud.hivenotes.db.GraphableDAO;
import net.tscloud.hivenotes.db.GraphableData;
import net.tscloud.hivenotes.db.WeatherHistory;
import net.tscloud.hivenotes.db.WeatherHistoryDAO;
import net.tscloud.hivenotes.helper.HiveUtil;
import net.tscloud.hivenotes.helper.HiveWeather;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnGraphDisplayFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GraphDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphDisplayFragment extends Fragment {

    public static final String TAG = "GraphDisplayFragment";

    // the fragment initialization parameters
    private static final String GRAPH_LIST = "param1";
    private static final String START_DATE = "param2";
    private static final String END_DATE = "param3";
    private static final String APIARY_ID = "param4";
    private static final String HIVE_ID = "param5";
    // and instance var of same - needed?
    private List<GraphableData> mGraphList;
    private long mStartDate = -1;
    private long mEndDate = -1;
    private long mApiaryId = -1;
    private long mHiveId = -1;

    // task references - needed to kill tasks on Activity Destroy
    private List<RetrieveDataTask> mTaskList = new ArrayList<>();

    private OnGraphDisplayFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static GraphDisplayFragment newInstance(List<GraphableData> aGraphList, long aStartDate,
                                                   long aEndDate, long aApiary, long aHive) {
        Log.d(TAG, "getting newInstance of GraphDisplayFragment...Start Date: " + aStartDate +
                " : End Date: " + aEndDate);

        GraphDisplayFragment fragment = new GraphDisplayFragment();
        Bundle args = new Bundle();

        try {
            args.putParcelableArrayList(GRAPH_LIST, (ArrayList)aGraphList);
        }
        catch (ClassCastException e) {
            Log.d(TAG, "List of stuff to graph passed in as something other that ArrayList");
        }
        args.putLong(START_DATE, aStartDate);
        args.putLong(END_DATE, aEndDate);
        args.putLong(APIARY_ID, aApiary);
        args.putLong(HIVE_ID, aHive);
        fragment.setArguments(args);

        return fragment;
    }

    public GraphDisplayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mGraphList = getArguments().getParcelableArrayList(GRAPH_LIST);
            mStartDate = getArguments().getLong(START_DATE);
            mEndDate = getArguments().getLong(END_DATE);
            mApiaryId = getArguments().getLong(APIARY_ID);
            mHiveId = getArguments().getLong(HIVE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_graph_display, container, false);

        // Set up the title(s)
        final TextView textTitle1 = (TextView)v.findViewById(R.id.textTitle1);
        final TextView textTitle2 = (TextView)v.findViewById(R.id.textTitle2);
        //there can be 1 to 4 graphable things - to set up title(s) we need to
        //  know where we're at
        int item = 0;

        // Cycle thru our list of GraphableData
        for (GraphableData data : mGraphList) {
            Log.d(TAG, "about to start RetrieveDataTask AsyncTask");

            //this can be null if, for instance, we want to display 1 upper and 1
            // lower graph
            if (data == null) {
                //don't forget to increment the item counter
                item++;
                continue;
            }

            // Set up the title(s)
            switch (item) {
                case (0):
                    textTitle1.setText(data.getPrettyName());
                    break;
                case (1):
                    String newText1 = textTitle1.getText() + "/" + data.getPrettyName();
                    textTitle1.setText(newText1);
                    break;
                case (2):
                    textTitle2.setText(data.getPrettyName());
                    textTitle2.setVisibility(View.VISIBLE);
                    break;
                case (3):
                    String newText2 = textTitle2.getText() + "/" + data.getPrettyName();
                    textTitle2.setText(newText2);
            }

            RetrieveDataTask mTask = new RetrieveDataTask(getActivity(), data, mStartDate,
                    mEndDate, item, v);
            // don't forget to set the reference to myself
            mTask.setTaskRef(mTask);
            mTaskList.add(mTask);
            // Could execute on multiple threads but if doing WeatherHistory =>
            //  we don't want them making redundant calls
            mTask.execute();
            //mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            //don't forget to increment the item counter
            item++;
        }

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGraphDisplayFragmentInteractionListener) {
            mListener = (OnGraphDisplayFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGraphDisplayFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        for (RetrieveDataTask task : mTaskList) {
            if (task != null) {
                task.cancel(false);
            }
        }

        super.onDestroy();
    }

    /** collect data
     */
    public class RetrieveDataTask extends AsyncTask<Void, Void, DataPoint[]> {

        public static final String TAG = "RetrieveDataTask";

        private Context ctx;
        private GraphableData data;
        private long startDate;
        private long endDate;
        private View view;
        private int itemNum = 0;
        private RetrieveDataTask taskRef;

        public void setTaskRef(RetrieveDataTask taskRef) {
            this.taskRef = taskRef;
        }

        private ProgressDialog dialog;

        public RetrieveDataTask(Context aCtx, GraphableData aData, long aStartDate, long aEndDate,
                                int aItem, View aView) {
            ctx = aCtx;
            data = aData;
            startDate = aStartDate;
            endDate = aEndDate;
            itemNum = aItem;
            view = aView;
            Log.d(TAG, "RetrieveDataTask(" + Thread.currentThread().getId() + ") : constructor");
        }


        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(ctx);
            dialog.setMessage(getResources().getString(R.string.retrieve_graph_data));
            dialog.show();
        }

        @Override
        protected DataPoint[] doInBackground(Void... unused) {
            Log.d(TAG, "RetrieveDataTask("+ Thread.currentThread().getId() + ") : doInBackground");

            DataPoint[] pointSet = null;

            // Determine the "type" of data we need to gather
            if (data.getDirective().equals("WeatherHistory")) {
                // need to take special action w/ WeatherHistory
                pointSet = doWeatherHistory(data, mApiaryId, startDate, endDate);
            }
            else if (!data.getDirective().equals("N/A")) {
                pointSet = doStandardDirective(data, mApiaryId, mHiveId, startDate ,endDate);
            }
            else {
                pointSet = doSpecialDirective(data, mApiaryId, mHiveId, startDate, endDate);
            }

            return pointSet;
        }

        @Override
        protected void onPostExecute(DataPoint[] aPointSet) {
            Log.d(TAG, "RetrieveDataTask("+ Thread.currentThread().getId() + ") : onPostExecute");

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            Toast.makeText(ctx, R.string.retrieve_graph_data_complete,
                    Toast.LENGTH_SHORT).show();

            // Draw the graph
            doGraph(aPointSet);

            // all we need to do is nullify the Task reference
            taskRef = null;
        }

        /**
         * Set of methods used by RetrieveDataTask
         */
        private DataPoint[] doWeatherHistory(GraphableData aData, long aApiary,
                                           long aStartDate, long aEndDate) {
            Log.d(TAG, "RetrieveDataTask : doWeatherHistory()");

            //--TESTING
            DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());

            //--TESTING
            Log.d(TAG, "aStartDate: " + formatter.format(new Date(aStartDate)) + " : " + aStartDate);
            Log.d(TAG, "aEndDate: " + formatter.format(new Date(aEndDate)) + " : " + aEndDate);

            //align dates to midnight
            long startDateMN = HiveUtil.alignDateToMidnight(aStartDate);
            long endDateMN = HiveUtil.alignDateToMidnight(aEndDate);

            //--TESTING
            Log.d(TAG, "startDateMN: " + formatter.format(new Date(startDateMN)) +
                    " : " + startDateMN);
            Log.d(TAG, "endDateMN: " + formatter.format(new Date(endDateMN)) +
                    " : " + endDateMN);

            /** read WeatherHistory table to get the data we have
             */
            WeatherHistoryDAO myDAO = (WeatherHistoryDAO)GraphableDAO.getGraphableDAO(aData, ctx);
            TreeMap<Long, Double> daoReply = myDAO.getColDataByDateRangeForGraphing(aData.getColumn(),
                    startDateMN, endDateMN, aApiary);
            myDAO.close();

            /** determine where the gaps are
             */

            /** RESTRICT total # of service calls/session **/
            int GOV_THRESH = 10;
            int callCount = 0;

            //list of WeatherHistory DOs that we will be persisting later
            ArrayList<WeatherHistory> listWeatherHistory = new ArrayList<>();

            //need to make a new set of keys to iterate over as we may need to:
            // 1) add new keys (dates) that correspond w/ endDateMN and/or startDateMN
            // 2) modify the underlying TreeMap w/ new entries
            TreeSet<Long> newKeySet = new TreeSet(daoReply.navigableKeySet());

            //--TESTING
            Log.d(TAG, "newKeySet before start/end time append");
            for (Long l : newKeySet) {
                Log.d(TAG, "---: " + formatter.format(new Date(l)) + " : " + l);
            }

            if (newKeySet.isEmpty()) {
                //potentially add start/end dates & retrieve data
                newKeySet.add(startDateMN);
                callCount = performWeatherHistory(startDateMN, aApiary, callCount,
                        listWeatherHistory, daoReply, aData);
                if (startDateMN != endDateMN) {
                    newKeySet.add(endDateMN);
                    callCount = performWeatherHistory(endDateMN, aApiary, callCount,
                            listWeatherHistory, daoReply, aData);
                }
            }
            else {
                if (TimeUnit.MILLISECONDS.toDays(startDateMN) < TimeUnit.MILLISECONDS.toDays(newKeySet.first())){
                    newKeySet.add(startDateMN);
                    //retrieve data for new dates
                    callCount = performWeatherHistory(startDateMN, aApiary, callCount,
                            listWeatherHistory, daoReply, aData);
                }
                if (TimeUnit.MILLISECONDS.toDays(endDateMN) > TimeUnit.MILLISECONDS.toDays(newKeySet.last())){
                    newKeySet.add(endDateMN);
                    //retrieve data for new dates
                    callCount = performWeatherHistory(endDateMN, aApiary, callCount,
                            listWeatherHistory, daoReply, aData);
                }
            }

            //--TESTING
            Log.d(TAG, "newKeySet after start/end time append");
            for (long l : newKeySet) {
                Log.d(TAG, "---: " + formatter.format(new Date(l)) + " : " + l);
            }

            //iterate over all but last element in set
            for (long k : newKeySet.headSet(newKeySet.last())) {
                //***check to see if we have exceeded our call count
                if (callCount >= GOV_THRESH) { break; }
                long nextKey = newKeySet.higher(k);
                //determine how many days b/w present key & next key
                // but subtract 1 as we do need not to get data for the greater value again
                long diffDays = (TimeUnit.MILLISECONDS.toDays(nextKey) -
                        TimeUnit.MILLISECONDS.toDays(k)) - 1;
                //--TESTING
                Log.d(TAG, "diffDays: " + diffDays);
                //for every "gap day"...
                for (int i = 0; i < diffDays; i++) {
                    //***check to see if we have exceeded our call count
                    if (callCount >= GOV_THRESH) { break; }
                    //for every day we do not have WeatherHistory -> call weather service
                    long reqDate = k + TimeUnit.DAYS.toMillis(i+1);
                    //--TESTING
                    Log.d(TAG, "reqDate: " + formatter.format(new Date(reqDate)) + " : " + reqDate);
                    //call the weather service & set all necessary stuff
                    callCount = performWeatherHistory(reqDate, aApiary, callCount,
                        listWeatherHistory, daoReply, aData);
                }
            }

            //persist any new WeatherHistory
            myDAO = new WeatherHistoryDAO(ctx);
            //myDAO.open();
            myDAO.createWeatherHistorySet(listWeatherHistory);
            myDAO.close();

            return makePointArray(daoReply);
        }

        private int performWeatherHistory(long aDate, long aApiary, int aCallCount,
                                          ArrayList<WeatherHistory> aListWeatherHistory,
                                          TreeMap<Long, Double> aDaoReply,
                                          GraphableData aGraphableData) {
            Log.d(TAG, "RetrieveDataTask : performWeatherHistory()");

            if (aDate > HiveUtil.alignDateToMidnight(System.currentTimeMillis())) {
                Log.d(TAG, "RetrieveDataTask : date > today...ignoring");
            }
            else {
                Log.d(TAG, "RetrieveDataTask : date < today...processing");
                //call the weather service
                WeatherHistory newWeatherHistory = getWeatherHistoryForDay(aDate);
                //set the apiary
                newWeatherHistory.setApiary(aApiary);
                aCallCount++;
                //add the result to the list
                aListWeatherHistory.add(newWeatherHistory);
                //update data to be graphed
                aDaoReply.put(aDate, newWeatherHistory.getCol(aGraphableData.getColumn()));
            }

            return aCallCount;
        }

        private WeatherHistory getWeatherHistoryForDay(long aDate) {
            Log.d(TAG, "RetrieveDataTask : getWeatherHistoryForDay()");

            HiveWeather myHiveWeather = new HiveWeather();
            WeatherHistory reply = myHiveWeather.requestWundergroundHistory(getLocation(), aDate);
            Log.d(TAG, "returned from wunderground WS call");

            return reply;
        }

        private String getLocation() {
            Log.d(TAG, "RetrieveDataTask : getLocation()");

            String reply = null;
            // read apiary to get location data
            ApiaryDAO apiaryDAO = new ApiaryDAO(ctx);
            Apiary myApiary = apiaryDAO.getApiaryById(mApiaryId);
            apiaryDAO.close();

            // build query string <-lat/lon should be present unless lat/lon
            //  & zip are not present
            if ((myApiary.getLatitude() != 0) && (myApiary.getLongitude() != 0)) {
                reply = myApiary.getLatitude() + "," + myApiary.getLongitude();
            }
            else if ((myApiary.getPostalCode() != null) && (myApiary.getPostalCode().length() != 0)) {
                reply = myApiary.getPostalCode();
            }

            return reply;
        }

        private DataPoint[] doStandardDirective(GraphableData aData, long aApiary, long aHive,
                                              long aStartDate, long aEndDate) {
            Log.d(TAG, "RetrieveDataTask : doStandardDirective()");

            // use DAOs that extend GraphableDAO - no need to know anything else about the data
            GraphableDAO myDAO = GraphableDAO.getGraphableDAO(aData, ctx);

            // determine which key to use (Apiary, Hive, ..)
            long readKey = 0;
            switch (aData.getKeyLevel()) {
                case "A":
                    readKey = aApiary;
                    break;
                case "H":
                    readKey = aHive;
                    break;
            }

            // do the read
            TreeMap<Long, Double> daoReply = myDAO.getColDataByDateRangeForGraphing(aData.getColumn(),
                    aStartDate, aEndDate, readKey);
            myDAO.close();

            return makePointArray(daoReply);
        }

        private DataPoint[] doSpecialDirective(GraphableData aData, long aApiary, long aHive,
                                             long aStartDate, long aEndDate) {
            Log.d(TAG, "RetrieveDataTask : doSpecialDirective()");
            DataPoint[] reply = null;

            return reply;
        }

        private DataPoint[] makePointArray(TreeMap<Long, Double> in) {
            Log.d(TAG, "RetrieveDataTask : makePointArray()");

            DataPoint[] out = new DataPoint[in.size()];

            int i = 0;
            for (long k : in.keySet()) {
                // make Date objects here
                out[i++] = new DataPoint(new Date(k), in.get(k));
            }

            return out;
        }

        private void doGraph(DataPoint[] aPoints) {
            Log.d(TAG, "RetrieveDataTask : doGraph()");

            // determine which graph to draw upon based on item number
            //  & make visible even if it already has
            GraphView graph;
            if (itemNum < 2) {
                graph = (GraphView)view.findViewById(R.id.graph1);
                graph.setVisibility(View.VISIBLE);
            }
            else {
                graph = (GraphView)view.findViewById(R.id.graph2);
                graph.setVisibility(View.VISIBLE);
            }
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(aPoints);

            series.setDrawDataPoints(true);

            graph.addSeries(series);

            // set date label formatter
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(ctx));
            graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

            /**/
            if (aPoints.length != 0) {
                // set manual x bounds
                graph.getViewport().setMinX(aPoints[0].getX());
                graph.getViewport().setMaxX(aPoints[aPoints.length - 1].getX());
                graph.getViewport().setXAxisBoundsManual(true);

                // set scrollable viewport
                //graph.getViewport().setScrollable(true);
            }
            /**/

            // as we use dates as labels, the human rounding to nice readable numbers
            // is not necessary
            //graph.getGridLabelRenderer().setHumanRounding(false);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnGraphDisplayFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
