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
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import net.tscloud.hivenotes.db.GraphableDAO;
import net.tscloud.hivenotes.db.GraphableData;
import net.tscloud.hivenotes.db.WeatherHistory;
import net.tscloud.hivenotes.helper.HiveWeather;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;


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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_graph_display, container, false);

        // Cycle thru our list of GraphableData
        for (GraphableData data : mGraphList) {
            Log.d(TAG, "about to start RetrieveDataTask AsyncTask");
            RetrieveDataTask mTask = new RetrieveDataTask(getActivity(), data, mStartDate,
                    mEndDate, v);
            // don't forget to set the reference to myself
            mTask.setTaskRef(mTask);
            mTaskList.add(mTask);
            //mTask.execute();
            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        private RetrieveDataTask taskRef;

        public void setTaskRef(RetrieveDataTask taskRef) {
            this.taskRef = taskRef;
        }

        private ProgressDialog dialog;

        public RetrieveDataTask(Context aCtx, GraphableData aData, long aStartDate, long aEndDate,
                                View aView) {
            ctx = aCtx;
            data = aData;
            startDate = aStartDate;
            endDate = aEndDate;
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
                pointSet = doWeatherHistory(data, mApiaryId, startDate ,endDate);
            }
            else if (!data.getDirective().equals("N/A")) {
                pointSet = doStandardDirective(data, mApiaryId, mHiveId, startDate ,endDate);
            }
            else {
                pointSet = doSpecialDirective(data, mApiaryId, mHiveId, startDate ,endDate);
            }

            return pointSet;
        }

        @Override
        protected void onPostExecute(DataPoint[] aPointSet) {
            Log.d(TAG, "RetrieveDataTask("+ Thread.currentThread().getId() + ") : onPostExecute");

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            Toast.makeText(ctx, "Retrieve graphable data complete...",
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
            // Get weather history - will have to read the Apiary to get location data
            //  may have to read multiple times, may not have to read if we already have the data
            //  we need
            DataPoint[] reply = null;

            HiveWeather myHiveWeather = new HiveWeather();
            WeatherHistory weatherHistoryDO = myHiveWeather.requestWundergroundHistory("02818", mStartDate);
            Log.d(TAG, "returned from wunderground WS call");

            return reply;
        }

        private DataPoint[] doStandardDirective(GraphableData aData, long aApiary, long aHive,
                                              long aStartDate, long aEndDate) {
            TreeMap<Long, Double> DAOReply = null;
            GraphableDAO myDAO = GraphableDAO.getGraphableDAO(aData, ctx);
            DAOReply = myDAO.getColDataByDateRangeForGraphing(aData.getColumn(),
                    aStartDate, aEndDate, aApiary);

            return makePointArray(DAOReply);
        }

        private DataPoint[] doSpecialDirective(GraphableData aData, long aApiary, long aHive,
                                             long aStartDate, long aEndDate) {
            DataPoint[] reply = null;

            return reply;
        }

        private DataPoint[] makePointArray(TreeMap<Long, Double> in) {
            DataPoint[] out = new DataPoint[in.size()];

            int i = 0;
            for (long k : in.navigableKeySet()) {
                // make Date objects here
                out[i++] = new DataPoint(new Date(k), in.get(k));
            }

            return out;
        }

        private void doGraph(DataPoint[] aPoints) {
            GraphView graph = (GraphView) view.findViewById(R.id.graph);
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(aPoints);

            series.setDrawDataPoints(true);

            graph.addSeries(series);

            // set date label formatter
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(ctx));
            graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

            // set manual x bounds to have nice steps
            graph.getViewport().setMinX(aPoints[0].getX());
            graph.getViewport().setMaxX(aPoints[aPoints.length-1].getX());
            graph.getViewport().setXAxisBoundsManual(true);

            // as we use dates as labels, the human rounding to nice readable numbers
            // is not nessecary
            graph.getGridLabelRenderer().setHumanRounding(false);
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