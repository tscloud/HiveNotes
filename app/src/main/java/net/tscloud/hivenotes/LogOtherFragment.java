package net.tscloud.hivenotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import net.tscloud.hivenotes.db.HiveNotesLogDO;
import net.tscloud.hivenotes.db.LogEntryOther;
import net.tscloud.hivenotes.db.LogEntryOtherDAO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;


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

    // time/date formatters
    private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
    private String TIME_PATTERN = "HH:mm";
    private SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());

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

        // populate dataobject from Bundle
        if (savedInstanceState != null) {
            mLogEntryOther = new LogEntryOther();
            mLogEntryOther.setVisitDate(savedInstanceState.getString("visitDate"));
            mLogEntryOther.setRequeen(savedInstanceState.getString("requeen"));
        }

        // save off arguments
        if (getArguments() != null) {
            mHiveID = getArguments().getLong(LogEntryListActivity.INTENT_HIVE_KEY);
            mLogEntryOtherKey = getArguments().getLong(LogEntryListActivity.INTENT_LOGENTRY_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_log_other_notes, container, false);

        // set button listener and text
        final Button hiveNoteBtn = (Button)v.findViewById(R.id.hiveNoteButtton);
        hiveNoteBtn.setText(getResources().getString(R.string.done_string));

        final Button requeenRmndrBtn = (Button)v.findViewById(R.id.buttonRequeenRmndr);
        final Button swarmRmndrBtn = (Button)v.findViewById(R.id.buttonSwarmRmndr);
        final Button splitHiveRmndrBtn = (Button)v.findViewById(R.id.buttonSplitHiveRmndr);

        // labels for showing reminder time; be sure to init the tag as this is what goes into the DB
        final TextView requeenRmndrText = (TextView)v.findViewById(R.id.textRequeenRmndr);
        requeenRmndrText.setTag((long)-1);
        final TextView swarmRmndrText = (TextView)v.findViewById(R.id.textSwarmRmndr);
        swarmRmndrText.setTag((long)-1);
        final TextView splitHiveRmndrText = (TextView)v.findViewById(R.id.textSplitHiveRmndr);
        splitHiveRmndrText.setTag((long)-1);

        // log entry may have something in it either already populated or populated from Bundle
        // if not => 1st check the Activity for previously entered data, if not => potentially read DB
        if (mLogEntryOther == null) {
            try {
                mLogEntryOther = (LogEntryOther)mListener.getPreviousLogData();
            }
            catch (ClassCastException e) {
                // Log the exception but continue w/ NO previous log data
                Log.e(TAG, "*** Bad Previous Log Data from Activity ***", e);
                mLogEntryOther = null;
            }
            if (mLogEntryOther == null) {
                if (mLogEntryOtherKey != -1) {
                    mLogEntryOther = getLogEntry(mLogEntryOtherKey);
                }
            }
        }

        if (mLogEntryOther != null) {

            // fill the form
            final Spinner requeenSpinner = (Spinner)v.findViewById(R.id.spinnerRequeen);

            requeenSpinner.setSelection(
                    ((ArrayAdapter) requeenSpinner.getAdapter()).getPosition(
                            mLogEntryOther.getRequeen()));
            //do Reminders
            Calendar calendar = Calendar.getInstance();

            //don't set if 0 ==> means it's not set
            if (mLogEntryOther.getRequeenRmndrTime() != 0) {
                calendar.setTimeInMillis(mLogEntryOther.getRequeenRmndrTime());
                String droneDate = dateFormat.format(calendar.getTime());
                String droneTime = timeFormat.format(calendar.getTime());
                String droneDateTime = droneDate + ' ' + droneTime;
                requeenRmndrText.setText(droneDateTime);
                // don't forget to set the tag
                requeenRmndrText.setTag(mLogEntryOther.getRequeenRmndrTime());
            }

            if (mLogEntryOther.getSwarmRmndrTime() != 0) {
                calendar.setTimeInMillis(mLogEntryOther.getSwarmRmndrTime());
                String swarmDate = dateFormat.format(calendar.getTime());
                String swarmTime = timeFormat.format(calendar.getTime());
                String swarmDateTime = swarmDate + ' ' + swarmTime;
                swarmRmndrText.setText(swarmDateTime);
                // don't forget to set the tag
                swarmRmndrText.setTag(mLogEntryOther.getSwarmRmndrTime());
            }

            if (mLogEntryOther.getSplitHiveRmndrTime() != 0) {
                calendar.setTimeInMillis(mLogEntryOther.getSplitHiveRmndrTime());
                String splitHiveDate = dateFormat.format(calendar.getTime());
                String splitHiveTime = timeFormat.format(calendar.getTime());
                String splitHiveDateTime = splitHiveDate + ' ' + splitHiveTime;
                splitHiveRmndrText.setText(splitHiveDateTime);
                // don't forget to set the tag
                splitHiveRmndrText.setTag(mLogEntryOther.getSplitHiveRmndrTime());
            }
        }

        // set button listeners
        hiveNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHiveButtonPressed(mHiveID);
            }
        });

        requeenRmndrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReminderPressed(requeenRmndrText);
            }
        });

        swarmRmndrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReminderPressed(swarmRmndrText);
            }
        });

        splitHiveRmndrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReminderPressed(splitHiveRmndrText);
            }
        });

        return v;
    }

    public void onHiveButtonPressed(long hiveID) {
        // get log entry data and put to DB
        Log.d(TAG, "about to persist logentry");

        boolean lNewLogEntry = false;

        final Spinner requeenSpinner = (Spinner)getView().findViewById(R.id.spinnerRequeen);
        final TextView requeenRmndrText = (TextView)getView().findViewById(R.id.textRequeenRmndr);
        final TextView swarmRmndrText = (TextView)getView().findViewById(R.id.textSwarmRmndr);
        final TextView splitHiveRmndrText = (TextView)getView().findViewById(R.id.textSplitHiveRmndr);

        String requeenText = requeenSpinner.getSelectedItem().toString();

        // Get the times in millis from the TextView tag
        long requeenRmndrLong = (long)requeenRmndrText.getTag();
        long swarmRmndrLong = (long)swarmRmndrText.getTag();
        long splitHiveRmndrLong = (long)splitHiveRmndrText.getTag();

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
           mLogEntryOther.setRequeenRmndrTime(requeenRmndrLong);
           mLogEntryOther.setSwarmRmndrTime(swarmRmndrLong);
           mLogEntryOther.setSplitHiveRmndrTime(splitHiveRmndrLong);

           if (mListener != null) {
               mListener.onLogOtherFragmentInteraction(mLogEntryOther);
           }
       }
    }

    public void onReminderPressed(final TextView timeLbl) {

        Log.d(TAG, "onReminderPressed");

        final View dialogView = View.inflate(getActivity(), R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePicker datePicker = (DatePicker)dialogView.findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker)dialogView.findViewById(R.id.time_picker);

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());

                long time = calendar.getTimeInMillis();
                Log.d(TAG, "Time picked: " + time);

                // label has a human readable value; tag has millis value for DB
                timeLbl.setText(dateFormat.format(calendar.getTime()) + ' ' + timeFormat.format(calendar.getTime()));
                timeLbl.setTag(time);

                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save off values potentially entered from screen
        if (mLogEntryOther != null) {
            outState.putString("visitDate", mLogEntryOther.getVisitDate());
            outState.putString("requeen", mLogEntryOther.getRequeen());
            outState.putLong("requeenRmndrTime", mLogEntryOther.getRequeenRmndrTime());
            outState.putLong("swarmRmndrTime", mLogEntryOther.getSwarmRmndrTime());
            outState.putLong("splitHiveRmndrTime", mLogEntryOther.getSplitHiveRmndrTime());
        }

        super.onSaveInstanceState(outState);
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
        void onLogOtherFragmentInteraction(LogEntryOther aLogEntryOther);
        HiveNotesLogDO getPreviousLogData();
    }

}
