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

    // constants used for Dialogs
    public static final String DIALOG_TAG_HONEY = "honey";
    public static final String DIALOG_TAG_POLLEN = "pollen";
    public static final String DIALOG_TAG_WAX = "wax";

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

        // get reference to the <include>s
        final View dialogProductivityHoney = v.findViewById(R.id.buttonProductivityHoney);
        final View dialogProductivityPollen = v.findViewById(R.id.buttonProductivityPollen);
        final View dialogProductivityWax = v.findViewById(R.id.buttonProductivityWax);

        // set text of <include>s
        final TextView honeyText =
                (TextView)dialogProductivityHoney.findViewById(R.id.dialogLaunchTextView);
        honeyText.setText(R.string.productivity_honey);

        final TextView pollenText =
                (TextView)dialogProductivityPollen.findViewById(R.id.dialogLaunchTextView);
        pollenText.setText(R.string.productivity_pollen);

        final TextView waxText =
                (TextView)dialogProductivityWax.findViewById(R.id.dialogLaunchTextView);
        waxText.setText(R.string.productivity_wax);

        /**
         * call super method to get DO via best means
         */
        getLogEntry(mListener);

        // Replace the azrrow in the Dialog button w/ values that may be in DB
        if (mLogEntryProductivity != null) {
            replaceArrow(mLogEntryProductivity.getExtractedHoney(), dialogProductivityHoney);
            replaceArrow(mLogEntryProductivity.getPollenCollected(), dialogProductivityPollen);
            replaceArrow(mLogEntryProductivity.getBeeswaxCollected(), dialogProductivityWax);
        }

        // set button listener
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(mHiveID);
            }
        });

        dialogProductivityHoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    if (mLogEntryProductivity != null &&
                            mLogEntryProductivity.getExtractedHoney() != null) {
                        checked = mLogEntryProductivity.getExtractedHoney();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogEditTextDialogData(
                            getResources().getString(R.string.productivity_honey),
                            DIALOG_TAG_HONEY,
                            checked));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        dialogProductivityPollen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    if (mLogEntryProductivity != null &&
                            mLogEntryProductivity.getPollenCollected() != null) {
                        checked = mLogEntryProductivity.getPollenCollected();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogEditTextDialogData(
                            getResources().getString(R.string.productivity_pollen),
                            DIALOG_TAG_POLLEN,
                            checked));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        dialogProductivityWax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    if (mLogEntryProductivity != null &&
                            mLogEntryProductivity.getBeeswaxCollected() != null) {
                        checked = mLogEntryProductivity.getBeeswaxCollected();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogEditTextDialogData(
                            getResources().getString(R.string.productivity_wax),
                            DIALOG_TAG_WAX,
                            checked));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        return v;
    }

    public void onButtonPressed(long hiveID) {
        // get log entry data and put to DB
        Log.d(TAG, "about to persist logentry");

        boolean lNewLogEntry = false;

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

    /**
     * little routine to replace the dialog button's arrow image w/ data from DB
     **/
    private void replaceArrow(float aAmount, View aInclude) {
        if ((aAmount > 0)) {
            View replaceMe = aInclude.findViewById(R.id.dialogLaunchArrowImage);
            ViewGroup parent = (ViewGroup)replaceMe.getParent();
            int index = parent.indexOfChild(replaceMe);
            parent.removeView(replaceMe);
            replaceMe = new TextView(getActivity());
            replaceMe.setText(Float.toString(aAmount));
            parent.addView(replaceMe, index);
        }
    }

    @Override
    public void setDialogData(String[] aResults, long aResultRemTime, String aTag) {
        //may have to create the DO here - if we're a new entry and Dialog work was done before
        // anything else
        if (mLogEntryProductivity == null) {
            mLogEntryProductivity = new mLogEntryProductivity();
        }

        switch (aTag){
            case DIALOG_TAG_HONEY:
                mLogEntryProductivity.setExtractedHoney(aResults[0]);
                Log.d(TAG, "onLogLaunchDialog: setExtractedHoney: " +
                        mLogEntryProductivity.getExtractedHoney());
                break;
            case DIALOG_TAG_POLLEN:
                mLogEntryProductivity.setPollenCollected(aResults[0]);
                Log.d(TAG, "onLogLaunchDialog: setPollenCollected: " +
                        mLogEntryProductivity.getPollenCollected());
                break;
            case DIALOG_TAG_WAX:
                mLogEntryProductivity.setBeeswaxCollected(aResults[0]);
                Log.d(TAG, "onLogLaunchDialog: setBeeswaxCollected: " +
                        mLogEntryProductivity.getBeeswaxCollected());
                break;
            default:
                Log.d(TAG, "onLogLaunchDialog: unrecognized Dialog type");
        }
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
