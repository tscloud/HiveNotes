package net.tscloud.hivenotes;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import net.tscloud.hivenotes.db.GraphableData;
import net.tscloud.hivenotes.db.GraphableDataDAO;
import net.tscloud.hivenotes.db.Hive;
import net.tscloud.hivenotes.db.HiveDAO;
import net.tscloud.hivenotes.helper.LogMultiSelectDialogData;
import net.tscloud.hivenotes.helper.LogSuperDataEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
public class GraphSelectionFragment extends HiveDataEntryFragment {

    public static final String TAG = "GraphSelectionFragment";

    // the fragment initialization parameters
    private static final String APIARY_ID = "param1";
    private static final String HIVE_ID = "param2";

    // constants used for Dialogs
    public static final String DIALOG_TAG_HIVES = "pests";
    public static final String DIALOG_TAG_WEATHER = "disease";

    private static String myFormat = "MM/dd/yy"; //In which you need put here

    // and instance var of same - needed?
    private long mApiaryID = -1;
    private long mHiveID = -1;

    // stuff that was potentially selected via dialogs - should be a collection but dialog class
    //  requires concatenated String, the construct used by DB DOs
    private String mHiveSelected = "";
    private String mWeatherSelected = "";

    // List of potential GraphableData - only weather
    private List<GraphableData> mGraphableWeatherList;
    // List of potential Hive
    private List<Hive> mHiveList;

    // task references - needed to kill tasks on Activity Destroy
    private GetGraphableData mTask = null;

    private OnGraphSelectionFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
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

        // get reference to the <include>s
        final View dialogHive = view.findViewById(R.id.buttonSelectHives);
        final View dialogWeather = view.findViewById(R.id.buttonSelectWeather);

        // set text of <include>s
        final TextView hiveText =
                (TextView)dialogHive.findViewById(R.id.dialogLaunchTextView);
        hiveText.setText(R.string.select_hive);

        final TextView weatherText =
                (TextView)dialogWeather.findViewById(R.id.dialogLaunchTextView);
        weatherText.setText(R.string.select_weather);

        // references to the rest of the stuff
        final ImageButton imgGraphStartDate = (ImageButton) view.findViewById(R.id.imageButtonStartDate);
        final ImageButton imgGraphEndDate = (ImageButton)view.findViewById(R.id.imageButtonEndDate);
        final EditText edtGraphStartDate = (EditText) view.findViewById(R.id.editTextGraphStartDate);
        final EditText edtGraphEndDate = (EditText) view.findViewById(R.id.editTextGraphEndDate);
        final Button btnGraph = (Button)view.findViewById(R.id.btnGraph);

        // disable some stuff - let AsyncTask enable after spinner is filled
        dialogHive.setEnabled(false);
        dialogWeather.setEnabled(false);
        btnGraph.setEnabled(false);

        /*
         * Listeners
         */
        // dialog button listeners
        dialogHive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    long reminderMillis = -1;
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogMultiSelectDialogData(
                            getResources().getString(R.string.select_hive),
                            mHiveID,
                            getHivePrettyNames(mHiveList),
                            mHiveSelected,
                            DIALOG_TAG_HIVES,
                            reminderMillis,
                            //hasOther, hasReminder, multiselect
                            false, false, true));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        dialogWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    long reminderMillis = -1;
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogMultiSelectDialogData(
                            getResources().getString(R.string.select_weather),
                            mHiveID,
                            getWeatherGraphableDataPrettyNames(mGraphableWeatherList),
                            mWeatherSelected,
                            DIALOG_TAG_WEATHER,
                            reminderMillis,
                            //hasOther, hasReminder, multiselect
                            false, false, true));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        // date and graph button listeners
        imgGraphStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDateEditClicked(edtGraphStartDate);
            }
        });

        imgGraphEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDateEditClicked(edtGraphEndDate);
            }
        });

        btnGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGraphButtonPressed();
            }
        });

        /*
         * AsyncTask to get pretty names from GraphableData
         */
        mTask = new GetGraphableData(getActivity(), dialogHive, dialogWeather, btnGraph);
        mTask.execute();

        return view;
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

    /*
     * Click Button -
     *  go back to Activity w/ relevant data
     */
    private void onGraphButtonPressed() {
        Log.d(TAG, "get ready to go back to Activity w/ data from this form");

        List<GraphableData> returnList = new ArrayList<>();
        List<Hive> returnHiveList = new ArrayList<>();

        final EditText edtGraphStartDate = (EditText)getView().findViewById(R.id.editTextGraphStartDate);
        final EditText edtGraphEndDate = (EditText)getView().findViewById(R.id.editTextGraphEndDate);

        /* if the pretty name of the GraphableData matches an entry in the list created from user
             selection => save off the GraphableData to send back to the Actvity - same for Hive List
        */
        // Make our CSVs of selected values into Lists to better do comparisons
        List<String> listWeatherSel = Arrays.asList(mWeatherSelected.split("\\s*,\\s*"));
        List<String> listHivesSel = Arrays.asList(mHiveSelected.split("\\s*,\\s*"));

        for (GraphableData elemGDWeather : mGraphableWeatherList) {
            for (String elemSelectedWeather : listWeatherSel) {
                if (elemSelectedWeather.equals(elemGDWeather.getPrettyName())) {
                    returnList.add(elemGDWeather);
                    break;
                }
            }
        }

        for (Hive elemHive : mHiveList) {
            for (String elemSelectedHive : listHivesSel) {
                if (elemSelectedHive.equals(elemHive.getName())) {
                    returnHiveList.add(elemHive);
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
                //add # of millis in a day to make the end date inclusive
                endTime = endTime + 86400000;
            }
            catch (ParseException e) {
                String parseProb = "Parse Error encountered on start/end date";
                edtGraphStartDate.setError(parseProb);
                edtGraphEndDate.setError(parseProb);
                emptyText = true;
            }

            if (!emptyText) {
                // Back to Activity
                mListener.onGraphSelectionFragmentInteraction(returnList, returnHiveList,
                    startTime, endTime);
            }
        }
    }

    /** used by those that need the prettyNames from a List of GraphableData or Hive
     */
    private static String[] getWeatherGraphableDataPrettyNames(List<GraphableData> aGraphableDataList) {
        ArrayList<String> replyList = new ArrayList<>();

        for (GraphableData dataElem : aGraphableDataList) {
            if (dataElem.getDirective().equals("WeatherHistory")) {
                replyList.add(dataElem.getPrettyName());
            }
        }

        return replyList.toArray(new String[replyList.size()]);
    }

    private static String[] getHivePrettyNames(List<Hive> aHiveList) {
        String[] reply = new String[aHiveList.size()];

        int i = 0;
        for (Hive dataElem : aHiveList) {
            reply[i++] = dataElem.getName();
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

    @Override
    public void setDialogData(String[] aResults, long aResultRemTime, String aResultRemDesc,
                              String aTag) {
        switch (aTag){
            case DIALOG_TAG_HIVES:
                mHiveSelected = TextUtils.join(",", aResults);
                Log.d(TAG, "onLogLaunchDialog: mHiveSelected: " + mHiveSelected);
                break;
            case DIALOG_TAG_WEATHER:
                mWeatherSelected = TextUtils.join(",", aResults);
                Log.d(TAG, "onLogLaunchDialog: mWeatherSelected: " + mWeatherSelected);
                break;
        }
    }

    @Override
    public boolean onFragmentSave() {
        return false;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnGraphSelectionFragmentInteractionListener extends
            LogSuperDataEntry.onLogDataEntryInteractionListener{
        void onGraphSelectionFragmentInteraction(List<GraphableData> aToGraphList, List<Hive> aHiveList,
                long aStartDate, long aEndDate);
    }

    /**
     * Inner Class - Get GraphableData AsyncTask
     */
    private class GetGraphableData extends AsyncTask<Void, Void, Void> {

        public static final String TAG = "GetGraphableData";

        private Context ctx;
        private View btnHive;
        private View btnWeather;
        private View btnGraph;

        GetGraphableData(Context aCtx, View aBtnHive, View aBtnWeather, View aBtnGraph) {
            Log.d(TAG, "GetGraphableData("+ Thread.currentThread().getId() + ") : constructor");
            ctx = aCtx;
            btnHive = aBtnHive;
            btnWeather = aBtnWeather;
            btnGraph = aBtnGraph;
        }

        @Override
        protected Void doInBackground(Void... unused) {
            Log.d(TAG, "GetGraphableData("+ Thread.currentThread().getId() + ") : doInBackground");

            GraphableDataDAO daoGraphableData = new GraphableDataDAO(ctx);
            HiveDAO daoHive = new HiveDAO(ctx);

            // Set the member var for holding GraphableData List
            mGraphableWeatherList = daoGraphableData.getGraphableDataList();

            // Set the member var for holding GraphableData List
            mGraphableWeatherList = daoGraphableData.getGraphableDataList();
            // Set the member var for holding Hive List
            mHiveList = daoHive.getHiveList(mApiaryID);

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            Log.d(TAG, "GetGraphableData("+ Thread.currentThread().getId() + ") : onPostExecute");

            btnHive.setEnabled(true);
            btnWeather.setEnabled(true);
            btnGraph.setEnabled(true);

            // all we need to do is nullify the Task reference
            mTask = null;
        }
    }
}
