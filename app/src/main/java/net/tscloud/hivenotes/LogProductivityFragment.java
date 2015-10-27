package net.tscloud.hivenotes;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import net.tscloud.hivenotes.db.LogEntryProductivity;
import net.tscloud.hivenotes.db.LogEntryProductivityDAO;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLogProductivityFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogProductivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogProductivityFragment extends Fragment {

    public static final String TAG = "LogProductivityFragment";

    private long mHiveID;
    private long mLogEntryProductivityKey;
    private LogEntryProductivity mLogEntryProductivity;

    private OnLogProductivityFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param hiveID Parameter 1.
     * @param logEntryID Parameter 2.
     * @return A new instance of fragment LogProductivityFragment.
     */
    public static LogProductivityFragment newInstance(long hiveID, long logEntryID) {
        LogProductivityFragment fragment = new LogProductivityFragment();
        Bundle args = new Bundle();
        args.putLong(LogEntryListActivity.INTENT_HIVE_KEY, hiveID);
        args.putLong(LogEntryListActivity.INTENT_LOGENTRY_KEY, logEntryID);
        fragment.setArguments(args);
        return fragment;
    }

    public LogProductivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mHiveID = getArguments().getLong(LogEntryListActivity.INTENT_HIVE_KEY);
            mLogEntryProductivityKey = getArguments().getLong(LogEntryListActivity.INTENT_LOGENTRY_KEY);
        }

        if (mLogEntryProductivityKey != -1) {
            // we need to get the Hive
            mLogEntryProductivity = getLogEntry(mLogEntryProductivityKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_log_productivity_notes, container, false);

        // set button listener and text
        final Button b1 = (Button)v.findViewById(R.id.hiveNoteButtton);
        b1.setText(getResources().getString(R.string.done_string));

        // setup numeric Spinner values
        final Spinner addSupersSpinner = (Spinner)v.findViewById(R.id.spinnerAddSupers);
        final Spinner removeSupersSpinner = (Spinner)v.findViewById(R.id.spinnerRemoveSupers);

        String [] a = new String[6];
        for (int i=0;i<a.length;++i) {
            a[i] = Integer.toString(i);
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (getActivity(),android.R.layout.simple_spinner_item, a);

        addSupersSpinner.setAdapter(spinnerArrayAdapter);
        removeSupersSpinner.setAdapter(spinnerArrayAdapter);

        if (mLogEntryProductivity != null) {

            // fill the form
            final EditText extractedHoneyEdit = (EditText)v.findViewById(R.id.editTextExtractedHoney);
            final CheckBox pollenAddTrapCheck = (CheckBox)v.findViewById(R.id.checkPollenAddTrap);
            final CheckBox pollenRemoveTrapCheck = (CheckBox)v.findViewById(R.id.checkPollenRemoveTrap);
            final EditText pollenCollectedEdit = (EditText)v.findViewById(R.id.editTextPollenCollected);
            final EditText beeswaxCollectedEdit = (EditText)v.findViewById(R.id.editTextBeeswaxCollected);

            addSupersSpinner.setSelection(
                    ((ArrayAdapter) addSupersSpinner.getAdapter()).getPosition(
                            mLogEntryProductivity.getHoneyAddSupers()));
            removeSupersSpinner.setSelection(
                    ((ArrayAdapter) removeSupersSpinner.getAdapter()).getPosition(
                            mLogEntryProductivity.getHoneyAddSupers()));
            extractedHoneyEdit.setText(Float.toString(mLogEntryProductivity.getExtractedHoney()));
            pollenAddTrapCheck.setChecked(mLogEntryProductivity.getAddPollenTrap() != 0);
            pollenRemoveTrapCheck.setChecked(mLogEntryProductivity.getRemovePollenTrap()!=0);
            pollenCollectedEdit.setText(Float.toString(mLogEntryProductivity.getPollenCollected()));
            beeswaxCollectedEdit.setText(Float.toString(mLogEntryProductivity.getBeeswaxCollected()));
        }

        // set button listener
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(mHiveID);
            }
        });

        return v;
    }

    public void onButtonPressed(long hiveID) {
        // get log entry data and put to DB
        Log.d(TAG, "about to persist logentry");

        boolean lNewLogEntry = false;

        final Spinner addSupersSpinner = (Spinner)getView().findViewById(R.id.spinnerAddSupers);
        final Spinner removeSupersSpinner = (Spinner)getView().findViewById(R.id.spinnerRemoveSupers);
        final EditText extractedHoneyEdit = (EditText)getView().findViewById(R.id.editTextExtractedHoney);
        final CheckBox pollenAddTrapCheck = (CheckBox)getView().findViewById(R.id.checkPollenAddTrap);
        final CheckBox pollenRemoveTrapCheck = (CheckBox)getView().findViewById(R.id.checkPollenRemoveTrap);
        final EditText pollenCollectedEdit = (EditText)getView().findViewById(R.id.editTextPollenCollected);
        final EditText beeswaxCollectedEdit = (EditText)getView().findViewById(R.id.editTextBeeswaxCollected);

        String addSupersText = addSupersSpinner.getSelectedItem().toString();
        String removeSupersText = removeSupersSpinner.getSelectedItem().toString();

        String extractedHoneyString = extractedHoneyEdit.getText().toString();
        float extractedHoneyFloat = 0;
        if ((extractedHoneyString != null) && (extractedHoneyString.length() != 0)) {
            extractedHoneyFloat = Float.parseFloat(extractedHoneyString);
        }

        int pollenAddTrapInt = (pollenAddTrapCheck.isChecked()) ? 1 : 0;
        int pollenRemoveTrapInt = (pollenRemoveTrapCheck.isChecked()) ? 1 : 0;

        String pollenCollectedString = pollenCollectedEdit.getText().toString();
        float pollenCollectedFloat = 0;
        if ((pollenCollectedString != null) && (pollenCollectedString.length() != 0)) {
            pollenCollectedFloat = Float.parseFloat(pollenCollectedString);
        }

        String beeswaxCollectedString = beeswaxCollectedEdit.getText().toString();
        float beeswaxCollectedFloat = 0;
        if ((beeswaxCollectedString != null) && (beeswaxCollectedString.length() != 0)) {
            beeswaxCollectedFloat = Float.parseFloat(beeswaxCollectedString);
        }

        // check for required values - are there any?
        boolean emptyText = false;

        if (!emptyText) {
            LogEntryProductivityDAO logEntryProductivityDAO = new LogEntryProductivityDAO(getActivity());
            if (mLogEntryProductivity == null) {
                mLogEntryProductivity = new LogEntryProductivity();
            }
            /*
            LogEntryProductivity logEntryProductivity;
            if (mLogEntryProductivityKey == -1) {
                // VISIT_DATE column will be set at DB update time
                logEntryProductivity = logEntryProductivityDAO.createLogEntry(mHiveID, null,
                        addSupersText, removeSupersText, extractedHoneyFloat, pollenAddTrapInt,
                        pollenRemoveTrapInt, pollenCollectedFloat, beeswaxCollectedFloat);
                lNewLogEntry = true;
            }
            else {
                // VISIT_DATE column will be set at DB update time
                logEntryProductivity = logEntryProductivityDAO.updateLogEntry(mLogEntryProductivityKey,
                        mHiveID, null, addSupersText, removeSupersText,
                        extractedHoneyFloat, pollenAddTrapInt, pollenRemoveTrapInt, pollenCollectedFloat,
                        beeswaxCollectedFloat);
            }
            logEntryProductivityDAO.close();

            if (logEntryProductivity != null) {
                // Reset LogEntryGeneral instance vars
                mLogEntryProductivity = logEntryProductivity;
                mLogEntryProductivityKey = logEntryProductivity.getId();

                Log.d(TAG, "LogEntryProductivity ID: " + logEntryProductivity.getId());
            }
            else {
                Log.d(TAG, "BAD...LogEntryProductivity update failed");
            }
            */
            mLogEntryProductivity.setId(mLogEntryProductivityKey);
            mLogEntryProductivity.setHive(mHiveID);
            mLogEntryProductivity.setVisitDate(null);
            mLogEntryProductivity.setHoneyAddSupers(addSupersText);
            mLogEntryProductivity.setHoneyRemoveSupers(removeSupersText);
            mLogEntryProductivity.setExtractedHoney(extractedHoneyFloat);
            mLogEntryProductivity.setAddPollenTrap(pollenAddTrapInt);
            mLogEntryProductivity.setRemovePollenTrap(pollenRemoveTrapInt);
            mLogEntryProductivity.setPollenCollected(pollenCollectedFloat);
            mLogEntryProductivity.setBeeswaxCollected(beeswaxCollectedFloat);

            if (mListener != null) {
                mListener.onLogProductivityFragmentInteraction(mLogEntryProductivity);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLogProductivityFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLogProductivityFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // Utility method to get Profile
    LogEntryProductivity getLogEntry(long aLogEntryID) {
        // read log Entry
        Log.d(TAG, "reading LogEntryProductivity table");
        LogEntryProductivityDAO logEntryProductivityDAO = new LogEntryProductivityDAO(getActivity());
        LogEntryProductivity reply = logEntryProductivityDAO.getLogEntryById(aLogEntryID);
        logEntryProductivityDAO.close();

        return reply;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnLogProductivityFragmentInteractionListener {
        public void onLogProductivityFragmentInteraction(LogEntryProductivity aLogEntryProductivity);
    }

}
