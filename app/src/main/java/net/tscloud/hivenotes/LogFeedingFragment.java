package net.tscloud.hivenotes;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import net.tscloud.hivenotes.db.HiveNotesLogDO;
import net.tscloud.hivenotes.db.LogEntryFeeding;
import net.tscloud.hivenotes.db.LogEntryFeedingDAO;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LogFeedingFragment.OnLogFeedingFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogFeedingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogFeedingFragment extends LogFragment {

    public static final String TAG = "LogFeedingFragment";

    // DO for this particular Fragment
    private LogEntryFeeding mLogEntryFeeding;

    // reference to Activity that should have started me
    private OnLogFeedingFragmentInteractionListener mListener;

    // Factory method to create a new instance of this fragment using the provided parameters.
    public static LogFeedingFragment newInstance(long hiveID, long logEntryDate, long logEntryID) {
        LogFeedingFragment fragment = new LogFeedingFragment();

        return (LogFeedingFragment)setLogFragArgs(fragment, hiveID, logEntryDate, logEntryID);
    }

    public LogFeedingFragment() {
        // Required empty public constructor
    }

    // Accessors needed by super class
    @Override
    protected HiveNotesLogDO getLogEntryDO() {
        return mLogEntryFeeding;
    }

    @Override
    protected void setLogEntryDO(HiveNotesLogDO aDataObj) {
        mLogEntryFeeding = (LogEntryFeeding) aDataObj;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // populate dataobject from Bundle
        if (savedInstanceState != null) {
            mLogEntryFeeding = new LogEntryFeeding();
            mLogEntryFeeding.setVisitDate(savedInstanceState.getLong("visitDate"));
            mLogEntryFeeding.setOneOneSugarWater(savedInstanceState.getInt("oneOneSugarWater"));
            mLogEntryFeeding.setTwoOneSugarWater(savedInstanceState.getInt("twoOneSugarWater"));
            mLogEntryFeeding.setPollenPatty(savedInstanceState.getInt("pollenPatty"));
            mLogEntryFeeding.setOther(savedInstanceState.getInt("other"));
            mLogEntryFeeding.setOtherType(savedInstanceState.getString("otherType"));
        }

        // save off arguments via super method
        saveOffArgs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_log_feeding_notes, container, false);

        // set button listener and text
        final Button b1 = (Button)v.findViewById(R.id.hiveNoteButtton);
        b1.setText(getResources().getString(R.string.done_string));

        /**
         * call super method to get DO via best means
         */
        getLogEntry(mListener);

        if (mLogEntryFeeding != null) {

            // fill the form
            final CheckBox oneOneSugarCheck = (CheckBox)v.findViewById(R.id.checkOneOneSugar);
            final CheckBox twoOneSugarCheck = (CheckBox)v.findViewById(R.id.checkTwoOneSugar);
            final CheckBox pollenPattyCheck = (CheckBox)v.findViewById(R.id.checkPollenPatty);
            final CheckBox feedingOtherCheck = (CheckBox)v.findViewById(R.id.checkFeedingOther);
            final EditText feedingOtherEdit = (EditText)v.findViewById(R.id.editTextFeedingOther);

            oneOneSugarCheck.setChecked(mLogEntryFeeding.getOneOneSugarWater() != 0);
            twoOneSugarCheck.setChecked(mLogEntryFeeding.getTwoOneSugarWater() != 0);
            pollenPattyCheck.setChecked(mLogEntryFeeding.getPollenPatty() != 0);
            feedingOtherCheck.setChecked(mLogEntryFeeding.getOther() != 0);
            feedingOtherEdit.setText(mLogEntryFeeding.getOtherType());
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

        final CheckBox oneOneSugarCheck = (CheckBox)getView().findViewById(R.id.checkOneOneSugar);
        final CheckBox twoOneSugarCheck = (CheckBox)getView().findViewById(R.id.checkTwoOneSugar);
        final CheckBox pollenPattyCheck = (CheckBox)getView().findViewById(R.id.checkPollenPatty);
        final CheckBox feedingOtherCheck = (CheckBox)getView().findViewById(R.id.checkFeedingOther);
        final EditText feedingOtherEdit = (EditText)getView().findViewById(R.id.editTextFeedingOther);

        int oneOneSugarInt = (oneOneSugarCheck.isChecked()) ? 1 : 0;
        int twoOneSugarInt = (twoOneSugarCheck.isChecked()) ? 1 : 0;
        int pollenPattyInt = (pollenPattyCheck.isChecked()) ? 1 : 0;
        int feedingOtherInt = (feedingOtherCheck.isChecked()) ? 1 : 0;

        String feedingOtherString = feedingOtherEdit.getText().toString();

        boolean emptyText = false;

        if (feedingOtherCheck.isChecked()) {
            if ((feedingOtherString == null) || (feedingOtherString.length() == 0)) {
                feedingOtherEdit.setError("Other Feeding cannot be empty");
                emptyText = true;
                Log.d(TAG, "Uh oh...Other Feeding empty");
            }
        }

        if (!emptyText) {
            LogEntryFeedingDAO logEntryFeedingDAO = new LogEntryFeedingDAO(getActivity());
            if (mLogEntryFeeding == null) {
                mLogEntryFeeding = new LogEntryFeeding();
            }

            mLogEntryFeeding.setId(mLogEntryKey);
            mLogEntryFeeding.setHive(mHiveID);
            mLogEntryFeeding.setVisitDate(mLogEntryDate);
            mLogEntryFeeding.setOneOneSugarWater(oneOneSugarInt);
            mLogEntryFeeding.setTwoOneSugarWater(twoOneSugarInt);
            mLogEntryFeeding.setPollenPatty(pollenPattyInt);
            mLogEntryFeeding.setOther(feedingOtherInt);
            mLogEntryFeeding.setOtherType(feedingOtherString);

            if (mListener != null) {
                mListener.onLogFeedingFragmentInteraction(mLogEntryFeeding);
            }
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLogFeedingFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLogFeedingFragmentInteractionListener");
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
        if (mLogEntryFeeding != null) {
            outState.putLong("visitDate", mLogEntryFeeding.getVisitDate());
            outState.putInt("oneOneSugarWater", mLogEntryFeeding.getOneOneSugarWater());
            outState.putLong("twoOneSugarWater", mLogEntryFeeding.getTwoOneSugarWater());
            outState.putInt("pollenPatty", mLogEntryFeeding.getPollenPatty());
            outState.putInt("other", mLogEntryFeeding.getOther());
            outState.putString("otherType", mLogEntryFeeding.getOtherType());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected LogEntryFeeding getLogEntryFromDB(long aKey, long aDate) {
        // read log Entry
        Log.d(TAG, "reading LogEntryFeeding table");
        LogEntryFeedingDAO logEntryFeedingDAO = new LogEntryFeedingDAO(getActivity());
        LogEntryFeeding reply = null;

        if (aKey != -1) {
            reply = logEntryFeedingDAO.getLogEntryById(aKey);
        }
        else if (aDate != -1) {
            reply = logEntryFeedingDAO.getLogEntryByDate(aDate);
        }

        logEntryFeedingDAO.close();

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
    public interface OnLogFeedingFragmentInteractionListener extends
            LogFragmentActivity {
        public void onLogFeedingFragmentInteraction(LogEntryFeeding aLogEntryFeeding);
    }

}
