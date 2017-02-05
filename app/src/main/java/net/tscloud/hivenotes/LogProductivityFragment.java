package net.tscloud.hivenotes;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import net.tscloud.hivenotes.db.HiveNotesLogDO;
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
public class LogProductivityFragment extends LogFragment {

    public static final String TAG = "LogProductivityFragment";

    // DO for this particular Fragment
    private LogEntryProductivity mLogEntryProductivity;

    // reference to Activity that should have started me
    private OnLogProductivityFragmentInteractionListener mListener;

    // Factory method to create a new instance of this fragment using the provided parameters.
    public static LogProductivityFragment newInstance(long hiveID, long logEntryDate, long logEntryID) {
        LogProductivityFragment fragment = new LogProductivityFragment();

        return (LogProductivityFragment)setLogFragArgs(fragment, hiveID, logEntryDate, logEntryID);
    }

    public LogProductivityFragment() {
        // Required empty public constructor
    }

    // Accessors needed by super class
    @Override
    protected HiveNotesLogDO getLogEntryDO() {
        return mLogEntryProductivity;
    }

    @Override
    protected void setLogEntryDO(HiveNotesLogDO aDataObj) {
        mLogEntryProductivity = (LogEntryProductivity)aDataObj;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // populate dataobject from Bundle
        if (savedInstanceState != null) {
            mLogEntryProductivity = new LogEntryProductivity();
            mLogEntryProductivity.setVisitDate(savedInstanceState.getLong("visitDate"));
            mLogEntryProductivity.setExtractedHoney(savedInstanceState.getInt("extractedHoney"));
            mLogEntryProductivity.setPollenCollected(savedInstanceState.getLong("pollenCollected"));
            mLogEntryProductivity.setBeeswaxCollected(savedInstanceState.getInt("beeswaxCollected"));
        }

        // save off arguments via super method
        saveOffArgs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_log_productivity_notes, container, false);

        // set button listener and text
        final Button b1 = (Button)v.findViewById(R.id.hiveNoteButtton);
        b1.setText(getResources().getString(R.string.done_string));

        /**
         * call super method to get DO via best means
         */
        getLogEntry(mListener);

        if (mLogEntryProductivity != null) {

            // fill the form
            final EditText extractedHoneyEdit = (EditText)v.findViewById(R.id.editTextExtractedHoney);
            final EditText pollenCollectedEdit = (EditText)v.findViewById(R.id.editTextPollenCollected);
            final EditText beeswaxCollectedEdit = (EditText)v.findViewById(R.id.editTextBeeswaxCollected);

            extractedHoneyEdit.setText(Float.toString(mLogEntryProductivity.getExtractedHoney()));
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

        final EditText extractedHoneyEdit = (EditText)getView().findViewById(R.id.editTextExtractedHoney);
        final EditText pollenCollectedEdit = (EditText)getView().findViewById(R.id.editTextPollenCollected);
        final EditText beeswaxCollectedEdit = (EditText)getView().findViewById(R.id.editTextBeeswaxCollected);

        String extractedHoneyString = extractedHoneyEdit.getText().toString();
        float extractedHoneyFloat = 0;
        if (extractedHoneyString.length() != 0) {
            extractedHoneyFloat = Float.parseFloat(extractedHoneyString);
        }

        String pollenCollectedString = pollenCollectedEdit.getText().toString();
        float pollenCollectedFloat = 0;
        if (pollenCollectedString.length() != 0) {
            pollenCollectedFloat = Float.parseFloat(pollenCollectedString);
        }

        String beeswaxCollectedString = beeswaxCollectedEdit.getText().toString();
        float beeswaxCollectedFloat = 0;
        if (beeswaxCollectedString.length() != 0) {
            beeswaxCollectedFloat = Float.parseFloat(beeswaxCollectedString);
        }

        // check for required values - are there any?
        boolean emptyText = false;

        if (!emptyText) {
            LogEntryProductivityDAO logEntryProductivityDAO = new LogEntryProductivityDAO(getActivity());
            if (mLogEntryProductivity == null) {
                mLogEntryProductivity = new LogEntryProductivity();
            }

            mLogEntryProductivity.setId(mLogEntryKey);
            mLogEntryProductivity.setHive(mHiveID);
            mLogEntryProductivity.setVisitDate(mLogEntryDate);
            mLogEntryProductivity.setExtractedHoney(extractedHoneyFloat);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save off values potentially entered from screen
        if (mLogEntryProductivity != null) {
            outState.putLong("visitDate", mLogEntryProductivity.getVisitDate());
            outState.putFloat("extractedHoney", mLogEntryProductivity.getExtractedHoney());
            outState.putFloat("pollenCollected", mLogEntryProductivity.getPollenCollected());
            outState.putFloat("beeswaxCollected", mLogEntryProductivity.getBeeswaxCollected());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected LogEntryProductivity getLogEntryFromDB(long aKey, long aDate) {
        // read log Entry
        Log.d(TAG, "reading LogEntryProductivity table");
        LogEntryProductivityDAO logEntryProductivityDAO = new LogEntryProductivityDAO(getActivity());
        LogEntryProductivity reply = null;

        if (aKey != -1) {
            reply = logEntryProductivityDAO.getLogEntryById(aKey);
        }
        else if (aDate != -1) {
            reply = logEntryProductivityDAO.getLogEntryByDate(aDate);
        }

        logEntryProductivityDAO.close();

        return reply;
    }

    @Override
    public void setDialogData(String[] aResults, long aResultRemTime, String aTag) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnLogProductivityFragmentInteractionListener extends
            LogFragmentActivity {
        public void onLogProductivityFragmentInteraction(LogEntryProductivity aLogEntryProductivity);
    }

}
