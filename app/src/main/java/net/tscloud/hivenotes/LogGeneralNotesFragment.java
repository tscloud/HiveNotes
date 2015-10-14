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

import net.tscloud.hivenotes.db.LogEntry;
import net.tscloud.hivenotes.db.LogEntryDAO;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LogGeneralNotesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogGeneralNotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogGeneralNotesFragment extends Fragment {

    public static final String TAG = "LogGeneralNotesFragment";

    private long mHiveID;
    private long mLogEntryKey;
    private LogEntry mLogEntry;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param hiveID Parameter 1.
     * @param logEntryID
     * @return A new instance of fragment LogGeneralNotesFragment.
     */
    public static LogGeneralNotesFragment newInstance(long hiveID, long logEntryID) {
        LogGeneralNotesFragment fragment = new LogGeneralNotesFragment();
        Bundle args = new Bundle();
        args.putLong(LogEntryListActivity.INTENT_HIVE_KEY, hiveID);
        args.putLong(LogEntryListActivity.INTENT_LOGENTRY_KEY, logEntryID);
        fragment.setArguments(args);
        return fragment;
    }

    public LogGeneralNotesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mHiveID = getArguments().getLong(LogEntryListActivity.INTENT_HIVE_KEY);
            mLogEntryKey = getArguments().getLong(LogEntryListActivity.INTENT_LOGENTRY_KEY);
        }

        if (mLogEntryKey != -1) {
            // we need to get the Hive
            mLogEntry = getLogEntry(mLogEntryKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_log_general_notes, container, false);

        // set button listener and text
        final Button b1 = (Button)v.findViewById(R.id.hiveNoteButtton);

        if (mLogEntry != null) {
            b1.setText(getResources().getString(R.string.create_logentry_string));

            // fill the form
            EditText dateEdit = (EditText)v.findViewById(R.id.editTextDate);
            Spinner populationSpinner = (Spinner)v.findViewById(R.id.spinnerPopulation);
            Spinner temperamentSpinner = (Spinner)v.findViewById(R.id.spinnerTemperment);
            CheckBox eggsCheck = (CheckBox)v.findViewById(R.id.checkBoxEggs);
            CheckBox larvaeCheck = (CheckBox)v.findViewById(R.id.checkBoxLarvae);
            CheckBox cappedBroodCheck = (CheckBox)v.findViewById(R.id.checkBoxCappedBrood);
            EditText broodFramesEdit = (EditText)v.findViewById(R.id.editTextBroodFrames);
            EditText honeyStoresEdit = (EditText)v.findViewById(R.id.editTextHoneyStores);
            EditText pollenStoresEdit = (EditText)v.findViewById(R.id.editTextPollenStores);

            // safe to cast? values in LogEntry table all TEXT
            dateEdit.setText(mLogEntry.getVisitDate());
            populationSpinner.setSelection(((ArrayAdapter) populationSpinner.getAdapter()).getPosition(mLogEntry.getPopulation()));
            temperamentSpinner.setSelection(((ArrayAdapter) temperamentSpinner.getAdapter()).getPosition(mLogEntry.getTemperament()));
            eggsCheck.setChecked(mLogEntry.getEggs() != 0);
            larvaeCheck.setChecked(mLogEntry.getLarvae() != 0);
            cappedBroodCheck.setChecked(mLogEntry.getCappedBrood() != 0);
            broodFramesEdit.setText(mLogEntry.getBroodFrames());
            honeyStoresEdit.setText(mLogEntry.getHoneyStores());
            pollenStoresEdit.setText(mLogEntry.getPollenStores());
        }
        else {
            // default to todays date
            EditText dateEdit = (EditText)v.findViewById(R.id.editTextDate);
            dateEdit.setText(new Date().toString());

            b1.setText(getResources().getString(R.string.save_logentry_string));
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

        EditText dateEdit = (EditText)getView().findViewById(R.id.editTextDate);
        Spinner populationSpinner = (Spinner)getView().findViewById(R.id.spinnerPopulation);
        Spinner temperamentSpinner = (Spinner)getView().findViewById(R.id.spinnerTemperment);
        CheckBox eggsCheck = (CheckBox)getView().findViewById(R.id.checkBoxEggs);
        CheckBox larvaeCheck = (CheckBox)getView().findViewById(R.id.checkBoxLarvae);
        CheckBox cappedBroodCheck = (CheckBox)getView().findViewById(R.id.checkBoxCappedBrood);
        EditText broodFramesEdit = (EditText)getView().findViewById(R.id.editTextBroodFrames);
        EditText honeyStoresEdit = (EditText)getView().findViewById(R.id.editTextHoneyStores);
        EditText pollenStoresEdit = (EditText)getView().findViewById(R.id.editTextPollenStores);

        String dateText = dateEdit.getText().toString();
        String populationText = populationSpinner.getSelectedItem().toString();
        String temperamentText = temperamentSpinner.getSelectedItem().toString();

        long eggsValue = eggsCheck.isChecked()?1:0;
        long larvaeValue = larvaeCheck.isChecked()?1:0;
        long cappedBroodValue = cappedBroodCheck.isChecked()?1:0;

        String broodFramesText = broodFramesEdit.getText().toString();
        String honeyStoresText = honeyStoresEdit.getText().toString();
        String pollenStoresText = pollenStoresEdit.getText().toString();

        // check for required values - are there any?
        boolean emptyText = false;

        if (!emptyText) {
            LogEntryDAO logEntryDAO = new LogEntryDAO(getActivity());
            LogEntry logEntry;
            if (mLogEntryKey == -1) {
                logEntry = logEntryDAO.createLogEntry(mHiveID, (new Date()).toString(), populationText,
                        temperamentText, eggsValue, larvaeValue, cappedBroodValue, broodFramesText, null, null,
                        honeyStoresText, pollenStoresText);
                lNewLogEntry = true;
            }
            // else --> there is not update method yet in LogEntryDAO
            else {
                logEntry = logEntryDAO.updateLogEntry(mLogEntryKey, mHiveID, (new Date()).toString(),
                        populationText, temperamentText, eggsValue, larvaeValue, cappedBroodValue,
                        broodFramesText, null, null, honeyStoresText, pollenStoresText);
            }
            logEntryDAO.close();

            if (logEntry != null) {
                // Reset LogEntry instnce vars
                mLogEntry = logEntry;
                mLogEntryKey = logEntry.getId();

                Log.d(TAG, "LogEntry Date: " + logEntry.getVisitDate());
            }
            else {
                Log.d(TAG, "BAD...LogEntry update failed");
            }
        }

        if (mListener != null) {
            mListener.onLogGeneralNotesFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // Utility method to get Profile
    LogEntry getLogEntry(long aLogEntryID) {
        // read log Entry
        Log.d(TAG, "reading LogEntry table");
        LogEntryDAO logEntryDAO = new LogEntryDAO(getActivity());
        LogEntry reply = logEntryDAO.getLogEntryById(aLogEntryID);
        logEntryDAO.close();

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
    public interface OnFragmentInteractionListener {
        public void onLogGeneralNotesFragmentInteraction();
    }

}
