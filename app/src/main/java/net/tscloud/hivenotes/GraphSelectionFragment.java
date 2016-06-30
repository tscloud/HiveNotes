package net.tscloud.hivenotes;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.Toast;

import net.tscloud.hivenotes.db.GraphableData;
import net.tscloud.hivenotes.db.GraphableDataDAO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


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

    // List pretty names for GraphableData
    String[] mPrettyNames;
    // List of graph directives that have been selected
    HashMap<SpinnerAdapter, Integer> mSpinnerSelectionHash = new HashMap<>();
    // the spinner which the next added spinned will be added after
    int mAddAfterThisSpinner;

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
        final View view = inflater.inflate(R.layout.fragment_graph_selection, container, false);

        // disable the stuff inside the include - let AsyncTask enable after spinner is filled
        View layoutSelector1 = view.findViewById(R.id.selector1);
        final Spinner spnSelector1 = (Spinner)layoutSelector1.findViewById(R.id.spinnerSelection);
        final Button btnSelector1 = (Button)view.findViewById(R.id.buttonSelection);
        final EditText edtGraphStartDate = (EditText)view.findViewById(R.id.editTextGraphStartDate);
        final EditText edtGraphEndDate = (EditText)view.findViewById(R.id.editTextGraphEndDate);
        spnSelector1.setEnabled(false);
        btnSelector1.setEnabled(false);

        // set mAddAfterThisSpinner
        mAddAfterThisSpinner = R.id.selector1;

        /**
         * Listeners
         */
        btnSelector1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectorButtonPressed((ViewGroup)view);
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
    private void onSelectorButtonPressed(ViewGroup aTopLevelView) {
        Log.d(TAG, "Creating another Graph Selector");

        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        View newSel = inflater.inflate(R.layout.graph_selector, null);

        final Spinner spnSelectorNew = (Spinner)newSel.findViewById(R.id.spinnerSelection);

        // Set the Spinner id - of course this is bad
        newSel.setId(mAddAfterThisSpinner + 111);

        ArrayAdapter<String> spinnerArrayAdapter = new DisableableArrayAdapter<String>
        (getActivity(), android.R.layout.simple_spinner_dropdown_item, mPrettyNames);

        spnSelectorNew.setAdapter(spinnerArrayAdapter);

        // determine other spinner's selected values
        for (int i = 0; i < mPrettyNames.length; i++) {
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

        // Add new Spinner to base RelativeLayout
        RelativeLayout rl = (RelativeLayout)aTopLevelView.findViewById(R.id.relLayGraphSelection);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, mAddAfterThisSpinner);

        rl.addView(newSel, params);

        // set the new spiner after
        mAddAfterThisSpinner = newSel.getId();
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
                updateLabel(aDateEditText, myCalendar);
            }
        };

        new DatePickerDialog(getActivity(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Needed by above method
    private void updateLabel(View aDateEditText, Calendar aCal) {

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        ((EditText)aDateEditText).setText(sdf.format(aCal.getTime()));
        //set the tag to time in millis for use later on down the road
        ((EditText)aDateEditText).setTag(aCal.getTimeInMillis());
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

            ArrayAdapter<String> spinnerArrayAdapter = new DisableableArrayAdapter<String>
                    (ctx, android.R.layout.simple_spinner_dropdown_item, mPrettyNames);

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

        public DisableableArrayAdapter(Context aCtx, int textViewResourceId, String[] objects) {
            super(aCtx, textViewResourceId, objects);
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

        private boolean checkItemSelected(int pos) {
            boolean reply = false;

            for (SpinnerAdapter a : mSpinnerSelectionHash.keySet()) {
                if (a != this) {
                    if (pos == mSpinnerSelectionHash.get(a)) {
                        reply = true;
                    }
                }
            }

            return reply;
        }

    }
}
