package net.tscloud.hivenotes;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import net.tscloud.hivenotes.db.GraphableData;
import net.tscloud.hivenotes.db.GraphableDataDAO;
import net.tscloud.hivenotes.helper.HiveUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GraphSelectionFragment.OnGraphSelectionFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GraphSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphSelectionFragment extends Fragment {

    public static final String TAG = "GraphSelectionFragment";

    // the fragment initialization parameters
    private static final String APIARY_ID = "param1";
    private static final String HIVE_ID = "param2";

    private static String myFormat = "MM/dd/yy"; //In which you need put here

    // and instance var of same - needed?
    private long mApiaryID = -1;
    private long mHiveID = -1;

    // List pretty names for GraphableData
    private List<GraphableData> mGraphableDataList;
    // List of graph directives that have been selected
    private HashMap<SpinnerAdapter, Integer> mSpinnerSelectionHash = new HashMap<>();
    // stack of Spinners used to know which to add after as well as to have
    //  reference to newly added
    //private int mAddAfterThisSpinner;
    private Deque<Integer> mSpinnerIdStack = new ArrayDeque<>();

    // task references - needed to kill tasks on Activity Destroy
    private GetGraphableData mTask = null;

    private OnGraphSelectionFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param apiaryID
     * @param hiveID
     */
    public static GraphSelectionFragment newInstance(long apiaryID, long hiveID) {
        Log.d(TAG, "getting newInstance of GraphSelectionFragment...apiaryID: " + apiaryID +
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
        final View view = inflater.inflate(R.layout.fragment_graph_selection, container, false);

        // disable the stuff inside the include - let AsyncTask enable after spinner is filled
        final Spinner spnSelector1 = (Spinner)view.findViewById(R.id.spinnerSelection1);
        final Button btnSelector1 = (Button)view.findViewById(R.id.buttonSelection1);
        final EditText edtGraphStartDate = (EditText)view.findViewById(R.id.editTextGraphStartDate);
        final EditText edtGraphEndDate = (EditText)view.findViewById(R.id.editTextGraphEndDate);
        final Button btnGraph = (Button)view.findViewById(R.id.btnGraph);
        spnSelector1.setEnabled(false);
        btnSelector1.setEnabled(false);

        // push the 1st Spinner onto our save stack
        mSpinnerIdStack.addFirst(R.id.spinnerSelection1);

        /**
         * Listeners
         */
        btnSelector1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectorButtonPressed((ViewGroup)view, 2);
            }
        });

        spnSelector1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                mSpinnerSelectionHash.put(spnSelector1.getAdapter(), position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // NOOP
            }

        });

        edtGraphStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDateEditClicked(v);
            }
        });

        edtGraphEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDateEditClicked(v);
            }
        });

        btnGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGraphButtonPressed();
            }
        });

        /**
         * AsyncTask to get pretty names from GraphableData
         */
        mTask = new GetGraphableData(getActivity(), spnSelector1, btnSelector1);
        mTask.execute();

        return view;
    }

    /**
     * Click Button -
     *  make new Selector group
     */
    private void onSelectorButtonPressed(ViewGroup aTopLevelView, int bntNumber) {
        Log.d(TAG, "Creating another Graph Selector");

        /*
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        View newSel = inflater.inflate(R.layout.graph_selector, null);
        */

        // Get reference id for spinner to make use & make visible
        int spnRId = getResources().getIdentifier("spinnerSelection" + bntNumber, "id",
                getContext().getPackageName());
        final Spinner spnSelectorNew = (Spinner)aTopLevelView.findViewById(spnRId);

        // Set the Spinner id - use local gererator of View Ids - Google does
        //  not provide one until API 17
        //newSel.setId(HiveUtil.generateViewId());

        ArrayAdapter<String> spinnerArrayAdapter = new DisableableArrayAdapter<String>
            (getActivity(), android.R.layout.simple_spinner_dropdown_item,
                getPrettyNames(mGraphableDataList), mSpinnerSelectionHash);

        spnSelectorNew.setAdapter(spinnerArrayAdapter);

        // determine 1st unused entry of spinner's selected values
        for (int i = 0; i < mGraphableDataList.size(); i++) {
            if (!mSpinnerSelectionHash.values().contains(i)) {
                spnSelectorNew.setSelection(i);
                break;
            }
        }

        // Listener for new Spinner
        spnSelectorNew.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                mSpinnerSelectionHash.put(spnSelectorNew.getAdapter(), position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // NOOP
            }

        });

        // Add new Spinner to base RelativeLayout - use LayoutParams to set the
        //  the stuff normally set up in XML
        /*
        RelativeLayout rl = (RelativeLayout)aTopLevelView.findViewById(R.id.relLayGraphSelection);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, mSpinnerIdStack.peekFirst());

        rl.addView(newSel, params);
        */

        // Make the spinnner visible
        spnSelectorNew.setVisibility(View.VISIBLE);

        // set the new spinner after
        mSpinnerIdStack.addFirst(spnSelectorNew.getId());
    }

    /**
     * Click EditTexts for dates -
     *  throw up a DatePicker
     */
    private void onDateEditClicked(final View aDateEditText) {
        Log.d(TAG, "throw up DatePickers for graph date bounds");

        final Calendar myCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                // Note setTag() is not used to store value in millis -- it cannot survive fragment
                //  reload, e.g. orientation change, return from backstack, etc.
                ((EditText)aDateEditText).setText(sdf.format(myCalendar.getTime()));
            }
        };

        new DatePickerDialog(getActivity(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * Click Button -
     *  go back to Activity w/ relevant data
     */
    private void onGraphButtonPressed() {
        Log.d(TAG, "get ready to go back to Activity w/ data from this form");

        List<GraphableData> returnList = new ArrayList<>();

        final EditText edtGraphStartDate = (EditText)getView().findViewById(R.id.editTextGraphStartDate);
        final EditText edtGraphEndDate = (EditText)getView().findViewById(R.id.editTextGraphEndDate);

        // if the pretty name of the GraphableData matches the Spinner's
        //  selected item String => save off the GraphableData to send
        //  back to the Actvity
        for ( ; !mSpinnerIdStack.isEmpty(); ) {
            View v = getView().findViewById(mSpinnerIdStack.removeFirst());
            Spinner s = (Spinner)v.findViewById(R.id.spinnerSelection);
            for (GraphableData g : mGraphableDataList) {
                if (g.getPrettyName().equals(s.getSelectedItem().toString())) {
                    returnList.add(g);
                    break;
                }
            }
        }

        // check for required values - are there any?
        boolean emptyText = false;

        if (edtGraphStartDate.getText() == null) {
            edtGraphStartDate.setError("Must set start date");
            emptyText = true;
            Log.d(TAG, "Uh oh...Start Date empty");
        }

        if (edtGraphEndDate.getText() == null) {
            edtGraphEndDate.setError("Must set end date");
            emptyText = true;
            Log.d(TAG, "Uh oh...End Date empty");
        }

        // All this necessary b/c cannot store millis in tag
        if (!emptyText) {
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            long startTime = 0;
            long endTime = 0;
            try {
                startTime = sdf.parse(edtGraphStartDate.getText().toString()).getTime();
                endTime = sdf.parse(edtGraphEndDate.getText().toString()).getTime();
            }
            catch (ParseException e) {
                String parseProb = "Parse Error encountered on start/end date";
                edtGraphStartDate.setError(parseProb);
                edtGraphEndDate.setError(parseProb);
                emptyText = true;
            }

            if (!emptyText) {
                // Need to clear the spinner selection hash so we can start anew upon return from
                //  backstack, orientation change, etc.
                mSpinnerSelectionHash.clear();
                // Back to Activity
                mListener.onGraphSelectionFragmentInteraction(returnList, startTime, endTime);
            }
        }
    }

    /** used by those that need the prettyNames from a List of GraphableData
     */
    private String[] getPrettyNames(List<GraphableData> aGraphableDataList) {
        String[] reply = new String[aGraphableDataList.size()];

        int i = 0;
        for (GraphableData dataElem : aGraphableDataList) {
            reply[i++] = dataElem.getPrettyName();
        }

        return reply;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGraphSelectionFragmentInteractionListener) {
            mListener = (OnGraphSelectionFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGraphSelectionFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save off values potentially entered from screen
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
    public interface OnGraphSelectionFragmentInteractionListener {
        void onGraphSelectionFragmentInteraction(
                List<GraphableData> aToGraphList,
                long aStartDate,
                long aEndDate);
    }

    /**
     * Inner Class - Get GraphableData AsyncTask
     */
    public class GetGraphableData extends AsyncTask<Void, Void, Void> {

        public static final String TAG = "GetGraphableData";

        private Context ctx;
        private Spinner spinner;
        private Button button;

        public GetGraphableData(Context aCtx, Spinner aSpinner, Button aButton) {
            Log.d(TAG, "GetGraphableData("+ Thread.currentThread().getId() + ") : constructor");
            ctx = aCtx;
            spinner = aSpinner;
            button = aButton;
        }

        @Override
        protected Void doInBackground(Void... unused) {
            Log.d(TAG, "GetGraphableData("+ Thread.currentThread().getId() + ") : doInBackground");

            GraphableDataDAO dao = new GraphableDataDAO(ctx);
            // Set the member var for holding GraphableData List
            mGraphableDataList = dao.getGraphableDataList();

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.d(TAG, "GetGraphableData("+ Thread.currentThread().getId() + ") : onPostExecute");

            ArrayAdapter<String> spinnerArrayAdapter = new DisableableArrayAdapter<String>
                    (ctx, android.R.layout.simple_spinner_dropdown_item,
                        getPrettyNames(mGraphableDataList), mSpinnerSelectionHash);

            spinner.setAdapter(spinnerArrayAdapter);
            spinner.setEnabled(true);

            button.setEnabled(true);

            // all we need to do is nullify the Task reference
            mTask = null;
        }
    }

    /**
     * Inner Class - Custom ArrayAdapter to handle disabled items
     */
    public class DisableableArrayAdapter<String> extends ArrayAdapter<String> {

        HashMap<SpinnerAdapter, Integer> spinnerSelectionHash = new HashMap<>();

        public DisableableArrayAdapter(Context aCtx, int textViewResourceId,
                                       String[] objects,
                                       HashMap<SpinnerAdapter, Integer> aHash) {
            super(aCtx, textViewResourceId, objects);
            spinnerSelectionHash = aHash;
        }

        @Override
        public boolean isEnabled(int position) {
            return !checkItemSelected(position);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View mView;

            mView = super.getDropDownView(position, convertView, parent);
            TextView mTextView = (TextView) mView;
            if (checkItemSelected(position)) {
                mTextView.setTextColor(Color.GRAY);
            } else {
                mTextView.setTextColor(Color.BLACK);
            }

            return mView;
        }

        /** Checks those values that are selected by other Spinner/SpinnerAdapters
         */
        private boolean checkItemSelected(int pos) {
            boolean reply = false;

            for (SpinnerAdapter a : spinnerSelectionHash.keySet()) {
                if (a != this) {
                    if (pos == spinnerSelectionHash.get(a)) {
                        reply = true;
                    }
                }
            }

            return reply;
        }
    }
}
