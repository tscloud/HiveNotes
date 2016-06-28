package net.tscloud.hivenotes;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import net.tscloud.hivenotes.db.GraphableData;
import net.tscloud.hivenotes.db.GraphableDataDAO;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GraphSelectionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GraphSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphSelectionFragment extends Fragment {

    public static final String TAG = "EditApiaryFragment";

    // the fragment initialization parameters
    private static final String APIARY_ID = "param1";
    private static final String HIVE_ID = "param2";
    // and instance var of same - needed?
    private long mApiaryID = -1;
    private long mHiveID = -1;

    // This is the list pretty names for GraphableData
    String[] mPrettyNames;

    // task references - needed to kill tasks on Activity Destroy
    private GetGraphableData mTask = null;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param apiaryID
     * @param hiveID
     */
    public static GraphSelectionFragment newInstance(long apiaryID, long hiveID) {
        Log.d(TAG, "getting newInstance of GraphFragment...apiaryID: " + apiaryID +
            " : hiveID: " + hiveID);

        GraphSelectionFragment fragment = new GraphSelectionFragment();
        Bundle args = new Bundle();

        args.putLong(APIARY_ID, apiaryID);
        args.putLong(HIVE_ID, hiveID);
        fragment.setArguments(args);

        return fragment;
    }

    public GraphSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mApiaryID = getArguments().getLong(APIARY_ID);
            mHiveID = getArguments().getLong(HIVE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph_selection, container, false);

        // disable the stuff inside the include - let AsyncTask enable after spinner is filled
        View layoutSelector1 = view.findViewById(R.id.selector1);
        final Spinner spnSelector1 = (Spinner)layoutSelector1.findViewById(R.id.spinnerSelection);
        final Button btnSelector1 = (Button)layoutSelector1.findViewById(R.id.buttonSelection);
        spnSelector1.setEnabled(false);
        btnSelector1.setEnabled(false);

        // Listeners
        btnSelector1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectorButtonPressed(spnSelector1.getSelectedItemPosition());
            }
        });

        // AsyncTask to get pretty names from GraphableData
        mTask = new GetGraphableData(getActivity(), spnSelector1, btnSelector1);
        mTask.execute();

        return view;
    }

    // Make new Selector group
    private void onSelectorButtonPressed(int itemToKnockOut) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        if (mTask != null) {
            mTask.cancel(false);
        }

        super.onDestroy();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Get GraphableData
     */
    public class GetGraphableData extends AsyncTask<Void, Void, Void> {

        public static final String TAG = "GetGraphableData";

        private Context ctx;
        private Spinner spinner;
        private Button button;

        public GetGraphableData(Context aCtx, Spinner aSpinner, Button aButton) {
            ctx = aCtx;
            spinner = aSpinner;
            button = aButton;
            Log.d(TAG, "GetGraphableData("+ Thread.currentThread().getId() + ") : constructor");
        }

        @Override
        protected Void doInBackground(Void... unused) {
            Log.d(TAG, "GetGraphableData("+ Thread.currentThread().getId() + ") : doInBackground");

            GraphableDataDAO dao = new GraphableDataDAO(ctx);
            List<GraphableData> listData = dao.getGraphableDataList();

            // loading the member var for pretty names - this will be used now and later for other
            //  spinners
            mPrettyNames = new String[listData.size()];
            int i = 0;
            for (GraphableData dataElem : listData) {
                mPrettyNames[i++] = dataElem.getPrettyName();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.d(TAG, "GetGraphableData("+ Thread.currentThread().getId() + ") : onPostExecute");

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                    (ctx, android.R.layout.simple_spinner_item, mPrettyNames);

            spinner.setAdapter(spinnerArrayAdapter);
            spinner.setEnabled(true);

            button.setEnabled(true);

            // all we need to do is nullify the Task reference
            mTask = null;
        }

    }
}
