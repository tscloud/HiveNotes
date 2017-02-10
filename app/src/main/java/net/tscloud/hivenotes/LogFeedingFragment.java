package net.tscloud.hivenotes;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import net.tscloud.hivenotes.helper.LogMultiSelectDialogData;


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

    // constants used for Dialogs
    public static final String DIALOG_TAG_FEEDING = "feeding";

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
            mLogEntryFeeding.setFeedingTypes(savedInstanceState.getString("feedingTypes"));
        }

        // save off arguments via super method
        saveOffArgs();

        /**
         * call super method to get DO via best means
         */
        getLogEntry(mListener);

        // Callback to Activity to launch a Dialog
        if (mListener != null) {
            String checked = "";
            if (mLogEntryFeeding != null &&
                    mLogEntryFeeding.getFeedingTypes() != null) {
                checked = mLogEntryFeeding.getFeedingTypes();
            }
            /* Get the Activity to launch the Dialog for us
             */
            mListener.onLogLaunchDialog(new LogMultiSelectDialogData(
                    getResources().getString(R.string.feeding_notes_string),
                    mHiveID,
                    getResources().getStringArray(R.array.feeding_array),
                    checked,
                    DIALOG_TAG_FEEDING,
                    -1,
                    //hasOther, hasReminder, multiselect
                    true, false, true));
        }
        else {
            Log.d(TAG, "no Listener");
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
            outState.putString("feedingTypes", mLogEntryFeeding.getFeedingTypes());
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
        //may have to create the DO here - if we're a new entry and Dialog work was done before
        // anything else
        if (mLogEntryFeeding == null) {
            mLogEntryFeeding = new LogEntryFeeding();
        }

        mLogEntryFeeding.setId(mLogEntryKey);
        mLogEntryFeeding.setHive(mHiveID);
        mLogEntryFeeding.setVisitDate(mLogEntryDate);

        switch (aTag){
            case DIALOG_TAG_FEEDING:
                mLogEntryFeeding.setFeedingTypes(TextUtils.join(",", aResults));
                Log.d(TAG, "onLogLaunchDialog: setFeedingTypes: " +
                        mLogEntryFeeding.getFeedingTypes());
                break;
            default:
                Log.d(TAG, "onLogLaunchDialog: unrecognized Dialog type");
        }

        if (mListener != null) {
            mListener.onLogHiveHealthFragmentInteraction(mLogEntryHiveHealth);
        }
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
