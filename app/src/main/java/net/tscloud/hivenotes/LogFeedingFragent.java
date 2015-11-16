package net.tscloud.hivenotes;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import net.tscloud.hivenotes.db.LogEntryFeeding;
import net.tscloud.hivenotes.db.LogEntryFeedingDAO;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LogFeedingFragent.OnLogFeedingFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogFeedingFragent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogFeedingFragent extends Fragment {

    public static final String TAG = "LogFeedingFragent";

    private long mHiveID;
    private long mLogEntryFeedingKey;
    private LogEntryFeeding mLogEntryFeeding;

    private OnLogFeedingFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param hiveID Parameter 1.
     * @param logEntryID Parameter 2.
     * @return A new instance of fragment LogFeedingFragent.
     */
    public static LogFeedingFragent newInstance(long hiveID, long logEntryID) {
        LogFeedingFragent fragment = new LogFeedingFragent();
        Bundle args = new Bundle();
        args.putLong(LogEntryListActivity.INTENT_HIVE_KEY, hiveID);
        args.putLong(LogEntryListActivity.INTENT_LOGENTRY_KEY, logEntryID);
        fragment.setArguments(args);
        return fragment;
    }

    public LogFeedingFragent() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mHiveID = getArguments().getLong(LogEntryListActivity.INTENT_HIVE_KEY);
            mLogEntryFeedingKey = getArguments().getLong(LogEntryListActivity.INTENT_LOGENTRY_KEY);
        }

        if (mLogEntryFeedingKey != -1) {
            // we need to get the Hive
            mLogEntryFeeding = getLogEntry(mLogEntryFeedingKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_log_feeding_notes, container, false);

        // set button listener and text
        final Button b1 = (Button)v.findViewById(R.id.hiveNoteButtton);
        b1.setText(getResources().getString(R.string.done_string));

        if (mLogEntryFeeding != null) {

            // fill the form
            final CheckBox oneOneSugarCheck = (CheckBox)v.findViewById(R.id.checkOneOneSugar);
            final CheckBox twoOneSugarCheck = (CheckBox)v.findViewById(R.id.checkTwoOneSugar);
            final CheckBox feedingOtherCheck = (CheckBox)v.findViewById(R.id.checkFeedingOther);
            final EditText feedingOtherEdit = (EditText)v.findViewById(R.id.editTextFeedingOther);

            oneOneSugarCheck.setChecked(mLogEntryFeeding.getOneOneSugarWater() != 0);
            twoOneSugarCheck.setChecked(mLogEntryFeeding.getTwoOneSugarWater() != 0);
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
        final CheckBox feedingOtherCheck = (CheckBox)getView().findViewById(R.id.checkFeedingOther);
        final EditText feedingOtherEdit = (EditText)getView().findViewById(R.id.editTextFeedingOther);

        int oneOneSugarInt = (oneOneSugarCheck.isChecked()) ? 1 : 0;
        int twoOneSugarInt = (twoOneSugarCheck.isChecked()) ? 1 : 0;
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

            mLogEntryFeeding.setId(mLogEntryFeedingKey);
            mLogEntryFeeding.setHive(mHiveID);
            mLogEntryFeeding.setVisitDate(null);
            mLogEntryFeeding.setOneOneSugarWater(oneOneSugarInt);
            mLogEntryFeeding.setTwoOneSugarWater(twoOneSugarInt);
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

    // Utility method to get Profile
    LogEntryFeeding getLogEntry(long aLogEntryID) {
        // read log Entry
        Log.d(TAG, "reading LogEntryFeeding table");
        LogEntryFeedingDAO logEntryFeedingDAO = new LogEntryFeedingDAO(getActivity());
        LogEntryFeeding reply = logEntryFeedingDAO.getLogEntryById(aLogEntryID);
        logEntryFeedingDAO.close();

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
    public interface OnLogFeedingFragmentInteractionListener {
        public void onLogFeedingFragmentInteraction(LogEntryFeeding aLogEntryFeeding);
    }

}