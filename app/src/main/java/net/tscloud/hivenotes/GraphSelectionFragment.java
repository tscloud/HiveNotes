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

    // List pretty names for GraphableData
    String[] mPrettyNames;
    // List of graph directives that have been selected
    ArrayList<Integer> mSelectedGraphItems;
    // Position to add the next Graph Selector
    // - in the initial layout this will be the 3rd elem in the root
    //   RelativeLayout
    // - this is NOT necessarily a great way to go
    int mGraphSelectorPos = 2;
    int mMaxGraphSelectorPos = 5;

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
        final EditText edtGraphStartDate = (EditText)layoutSelector1.findViewById(R.id.editTextGraphStartDate);
        final EditText edtGraphEndDate = (EditText)layoutSelector1.findViewById(R.id.editTextGraphEndDate);
        spnSelector1.setEnabled(false);
        btnSelector1.setEnabled(false);

        /**
         * Listeners
         */
        btnSelector1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectorButtonPressed((ViewGroup)view, inflater);
            }
        });

        spnSelector1.setOnItemSelectedListener(new OnItemSelectedListener() {
            // NOTE: this should not be able to be selected prior to creation
            //  of the array that holds the selected item positions, it's greyed
            //  until thus happens
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                onSpinnerItemSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // do I need this?
            }

        });

        edtGraphStartDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onDateEditClicked(v);
            }
        });

        edtGraphEndDate.setOnClickListener(new OnClickListener() {
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
    private void onSelectorButtonPressed(ViewGroup aTopLevelView,
                                         LayoutInflater aInflater) {
        Log.d(TAG, "Creating another Graph Selector");

        if (mGraphSelectorPos < mMaxGraphSelectorPos) {
            View newSel = aInflater.inflate(R.layout.graph_selector, null);
            aTopLevelView.addView(newSel, mGraphSelectorPos);
            mGraphSelectorPos++;

            final Spinner spnSelectorNew = (Spinner)newSel.findViewById(R.id.spinnerSelection);

            ArrayAdapter<String> spinnerArrayAdapter = new DisableableArrayAdapter<String>
            (getActivity(), android.R.layout.simple_spinner_dropdown_item, mPrettyNames);
            //remember to set the selected items of the adapter
            spinnerArrayAdapter.setSelectedItems(mSelectedGraphItems);

            spnSelectorNew.setAdapter(spinnerArrayAdapter);
        }
    }

    /**
     * Click Spinner Item -
     *  add this item position to the list of those items that cannot be
     *  selected by future Spinners
     */
    private void onSpinnerItemSelected(int aPosition) {
        Log.d(TAG, "Graph Selector Spinner item selected");

        mSelectedGraphItems.add(aPosition);
    }

    /**
     * Click EditTexts for dates -
     *  throw up a DatePicker
     */
    private void onDateEditClicked(View aDateEditText) {
        Log.d(TAG, "throw up DatePickers for graph date bounds");

        Calendar myCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(aDateEditText);
            }
        };

        new DatePickerDialog(classname.this, date, myCalendar
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

            //create the instance var array that will contain the selected item positions
            mSelectedGraphItems = new ArrayList<Integer>(mPrettyNames.length);

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.d(TAG, "GetGraphableData("+ Thread.currentThread().getId() + ") : onPostExecute");

            ArrayAdapter<String> spinnerArrayAdapter = new DisableableArrayAdapter<String>
                    (ctx, android.R.layout.simple_spinner_dropdown_item, mPrettyNames);
            //remember to set the selected items of the adapter
            spinnerArrayAdapter.setSelectedItems(mSelectedGraphItems);

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
    public class DisableableArrayAdapter extends ArrayAdapter {

        private ArrayList<Integer> selectedItems;

        public ArrayList<Integer> getSelectedItems() {
            return selectedItems;
        }

        public void setSelectedItems(ArrayList<Integer> aSelectedItems) {
            this.selectedItems = aSelectedItems;
        }

        @Override
        public boolean isEnabled(int position) throws IllegalStateException {
            if (selectedItems == null) {
                throw new IllegalStateException("selectedItems of
                    DisableableArrayAdapter must not be null");
            }
            else if (selectedItems.contains(position)) {
                return false;
            }

            return true;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) throws IllegalStateException {
            if (selectedItems == null) {
                throw new IllegalStateException("selectedItems of
                    DisableableArrayAdapter must not be null");
            }
            else {
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                if (selectedItems.contains(position)) {
                    mTextView.setTextColor(Color.GRAY);
                } else {
                    mTextView.setTextColor(Color.BLACK);
                }
            }

            return mView;
        }
    }
}
