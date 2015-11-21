package net.tscloud.hivenotes;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import net.tscloud.hivenotes.db.LogEntryOther;
import net.tscloud.hivenotes.db.LogEntryOtherDAO;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLogOtherFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogOtherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogOtherFragment extends Fragment {

    public static final String TAG = "LogOtherFragment";

    private long mHiveID;
    private long mLogEntryOtherKey;
    private LogEntryOther mLogEntryOther;

    private OnLogOtherFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param hiveID Parameter 1.
     * @param logEntryID Parameter 2.
     * @return A new instance of fragment LogOtherFragment.
     */
    public static LogOtherFragment newInstance(long hiveID, long logEntryID) {
        LogOtherFragment fragment = new LogOtherFragment();
        Bundle args = new Bundle();
        args.putLong(LogEntryListActivity.INTENT_HIVE_KEY, hiveID);
        args.putLong(LogEntryListActivity.INTENT_LOGENTRY_KEY, logEntryID);
        fragment.setArguments(args);
        return fragment;
    }

    public LogOtherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mHiveID = getArguments().getLong(LogEntryListActivity.INTENT_HIVE_KEY);
            mLogEntryOtherKey = getArguments().getLong(LogEntryListActivity.INTENT_LOGENTRY_KEY);
        }

        if (mLogEntryOtherKey != -1) {
            // we need to get the Hive
            mLogEntryOther = getLogEntry(mLogEntryOtherKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_log_other_notes, container, false);

        // set button listener and text
        final Button b1 = (Button)v.findViewById(R.id.hiveNoteButtton);
        b1.setText(getResources().getString(R.string.done_string));

        if (mLogEntryOther != null) {

            // fill the form
            final Spinner requeenSpinner = (Spinner)v.findViewById(R.id.spinnerRequeen);

            requeenSpinner.setSelection(
                    ((ArrayAdapter) requeenSpinner.getAdapter()).getPosition(
                            mLogEntryOther.getRequeen()));
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

        final Spinner requeenSpinner = (Spinner)getView().findViewById(R.id.spinnerRequeen);

        String requeenText = requeenSpinner.getSelectedItem().toString();

        // check for required values - are there any?
        boolean emptyText = false;

       if (!emptyText) {
           LogEntryOtherDAO logEntryOtherDAO = new LogEntryOtherDAO(getActivity());
           if (mLogEntryOther == null) {
               mLogEntryOther = new LogEntryOther();
           }

           mLogEntryOther.setId(mLogEntryOtherKey);
           mLogEntryOther.setHive(mHiveID);
           mLogEntryOther.setVisitDate(null);
           mLogEntryOther.setRequeen(requeenText);

           if (mListener != null) {
               mListener.onLogOtherFragmentInteraction(mLogEntryOther);
           }
       }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLogOtherFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLogOtherFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // Utility method to get Profile
    LogEntryOther getLogEntry(long aLogEntryID) {
        // read log Entry
        Log.d(TAG, "reading LogEntryPestMgmt table");
        LogEntryOtherDAO logEntryOtherDAO = new LogEntryOtherDAO(getActivity());
        LogEntryOther reply = logEntryOtherDAO.getLogEntryById(aLogEntryID);
        logEntryOtherDAO.close();

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
    public interface OnLogOtherFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onLogOtherFragmentInteraction(LogEntryOther aLogEntryOther);
    }

}
