package net.tscloud.hivenotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import net.tscloud.hivenotes.db.HiveNotesLogDO;
import net.tscloud.hivenotes.db.LogEntryPestMgmt;
import net.tscloud.hivenotes.db.LogEntryPestMgmtDAO;
import net.tscloud.hivenotes.helper.HiveCalendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLogPestMgmntFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogPestMgmtFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogPestMgmtFragment extends Fragment {

    public static final String TAG = "LogPestMgmtFragment";

    private long mHiveID;
    private long mLogEntryPestMgmtKey;
    private LogEntryPestMgmt mLogEntryPestMgmt;

    private OnLogPestMgmntFragmentInteractionListener mListener;

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
     * @return A new instance of fragment LogPestMgmtFragment.
     */
    public static LogPestMgmtFragment newInstance(long hiveID, long logEntryID) {
        LogPestMgmtFragment fragment = new LogPestMgmtFragment();
        Bundle args = new Bundle();
        args.putLong(LogEntryListActivity.INTENT_HIVE_KEY, hiveID);
        args.putLong(LogEntryListActivity.INTENT_LOGENTRY_KEY, logEntryID);
        fragment.setArguments(args);
        return fragment;
    }

    public LogPestMgmtFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // populate dataobject from Bundle
        if (savedInstanceState != null) {
            mLogEntryPestMgmt = new LogEntryPestMgmt();
            mLogEntryPestMgmt.setVisitDate(savedInstanceState.getString("visitDate"));
            mLogEntryPestMgmt.setDroneCellFndn(savedInstanceState.getInt("droneCellFndn"));
            mLogEntryPestMgmt.setDroneCellFndnRmndr(savedInstanceState.getLong("droneCellFndnRmndr"));
            mLogEntryPestMgmt.setSmallHiveBeetleTrap(savedInstanceState.getInt("smallHiveBeetleTrap"));
            mLogEntryPestMgmt.setMitesTrtmnt(savedInstanceState.getInt("mitesTrtmnt"));
            mLogEntryPestMgmt.setMitesTrtmntType(savedInstanceState.getString("mitesTrtmntType"));
            mLogEntryPestMgmt.setMitesTrtmntRmndr(savedInstanceState.getLong("mitesTrtmntRmndr"));
            mLogEntryPestMgmt.setScreenedBottomBoard(savedInstanceState.getInt("screenedBottomBoard"));
            mLogEntryPestMgmt.setOther(savedInstanceState.getInt("other"));
            mLogEntryPestMgmt.setOtherType(savedInstanceState.getString("otherType"));
        }

        // save off arguments
        if (getArguments() != null) {
            mHiveID = getArguments().getLong(LogEntryListActivity.INTENT_HIVE_KEY);
            mLogEntryPestMgmtKey = getArguments().getLong(LogEntryListActivity.INTENT_LOGENTRY_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_log_pestmgmt_notes, container, false);

        // set button listeners and text
        final Button hiveNoteBtn = (Button)v.findViewById(R.id.hiveNoteButtton);
        hiveNoteBtn.setText(getResources().getString(R.string.done_string));

        final Button droneCellFndnBtn = (Button)v.findViewById(R.id.buttonDroneCellFndn);
        final Button mitesTrtmntBtn = (Button)v.findViewById(R.id.buttonMitesTrtmnt);

        // labels for showing reminder time; be sure to init the tag as this is what goes into the DB
        final TextView droneCellFndnRmndrText = (TextView)v.findViewById(R.id.textViewDroneCellFndnRmndr);
        droneCellFndnRmndrText.setTag((long)0);
        final TextView mitesTrtmntRmndrText = (TextView)v.findViewById(R.id.textViewMitesTrtmntRmndr);
        mitesTrtmntRmndrText.setTag((long)0);

        // log entry may have something in it either already populated or populated from Bundle
        // if not => 1st check the Activity for previously entered data, if not => potentially read DB
        if (mLogEntryPestMgmt == null) {
            try {
                mLogEntryPestMgmt = (LogEntryPestMgmt)mListener.getPreviousLogData();
            }
            catch (ClassCastException e) {
                // Log the exception but continue w/ NO previous log data
                Log.e(TAG, "*** Bad Previous Log Data from Activity ***", e);
                mLogEntryPestMgmt = null;
            }
            if (mLogEntryPestMgmt == null) {
                if (mLogEntryPestMgmtKey != -1) {
                    mLogEntryPestMgmt = getLogEntry(mLogEntryPestMgmtKey);
                }
            }
        }

        if (mLogEntryPestMgmt != null) {

            // fill the form
            final CheckBox droneCellFndnCheck = (CheckBox)v.findViewById(R.id.checkDroneCellFndn);
            final CheckBox smallHiveBeetleTrapCheck = (CheckBox)v.findViewById(R.id.checkSmallHiveBeetleTrap);
            final CheckBox mitesTrtmntCheck = (CheckBox)v.findViewById(R.id.checkMitesTrtmnt);
            final EditText mitesTrtmntEdit = (EditText)v.findViewById(R.id.editTextMitesTrtmnt);
            final CheckBox screenedBottomBoardCheck = (CheckBox)v.findViewById(R.id.checkScreenedBottomBoard);
            final CheckBox otherCheck = (CheckBox)v.findViewById(R.id.checkPestOther);
            final EditText otherEdit = (EditText)v.findViewById(R.id.editTextPestOther);

            droneCellFndnCheck.setChecked(mLogEntryPestMgmt.getDroneCellFndn() != 0);
            smallHiveBeetleTrapCheck.setChecked(mLogEntryPestMgmt.getSmallHiveBeetleTrap() != 0);
            mitesTrtmntCheck.setChecked(mLogEntryPestMgmt.getMitesTrtmnt() != 0);
            mitesTrtmntEdit.setText(mLogEntryPestMgmt.getMitesTrtmntType());
            screenedBottomBoardCheck.setChecked(mLogEntryPestMgmt.getScreenedBottomBoard() != 0);
            otherCheck.setChecked(mLogEntryPestMgmt.getOther() != 0);
            otherEdit.setText(mLogEntryPestMgmt.getOtherType());

            //do Reminders
            Calendar calendar = Calendar.getInstance();

            //don't set if 0 ==> means it's not set
            if (mLogEntryPestMgmt.getDroneCellFndnRmndr() != 0) {
                calendar.setTimeInMillis(mLogEntryPestMgmt.getDroneCellFndnRmndr());
                String droneDate = dateFormat.format(calendar.getTime());
                String droneTime = timeFormat.format(calendar.getTime());
                String droneDateTime = droneDate + ' ' + droneTime;
                droneCellFndnRmndrText.setText(droneDateTime);
            }

            if (mLogEntryPestMgmt.getMitesTrtmntRmndr() != 0) {
                calendar.setTimeInMillis(mLogEntryPestMgmt.getMitesTrtmntRmndr());
                String mitesDate = dateFormat.format(calendar.getTime());
                String mitesTime = timeFormat.format(calendar.getTime());
                String mitesDateTime = mitesDate + ' ' + mitesTime;
                mitesTrtmntRmndrText.setText(mitesDateTime);
            }
        }

        // set button listeners
        hiveNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHiveNoteButtonPressed(mHiveID);
            }
        });

        droneCellFndnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReminderPressed(droneCellFndnRmndrText);
            }
        });

        mitesTrtmntBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReminderPressed(mitesTrtmntRmndrText);
            }
        });

        return v;
    }

    public void onHiveNoteButtonPressed(long hiveID) {
        // get log entry data and put to DB
        Log.d(TAG, "about to persist logentry");

        boolean lNewLogEntry = false;

        final CheckBox droneCellFndnCheck = (CheckBox)getView().findViewById(R.id.checkDroneCellFndn);
        final CheckBox smallHiveBeetleTrapCheck = (CheckBox)getView().findViewById(R.id.checkSmallHiveBeetleTrap);
        final CheckBox mitesTrtmntCheck = (CheckBox)getView().findViewById(R.id.checkMitesTrtmnt);
        final EditText mitesTrtmntEdit = (EditText)getView().findViewById(R.id.editTextMitesTrtmnt);
        final CheckBox screenedBottomBoardCheck = (CheckBox)getView().findViewById(R.id.checkScreenedBottomBoard);
        final CheckBox otherCheck = (CheckBox)getView().findViewById(R.id.checkPestOther);
        final EditText otherEdit = (EditText)getView().findViewById(R.id.editTextPestOther);
        final TextView droneCellFndnRmndrText = (TextView)getView().findViewById(R.id.textViewDroneCellFndnRmndr);
        final TextView mitesTrtmntRmndrText = (TextView)getView().findViewById(R.id.textViewMitesTrtmntRmndr);

        int droneCellFndnInt = (droneCellFndnCheck.isChecked()) ? 1 : 0;
        int smallHiveBeetleTrapInt = (smallHiveBeetleTrapCheck.isChecked()) ? 1 : 0;
        int mitesTrtmntInt = (mitesTrtmntCheck.isChecked()) ? 1 : 0;

        String mitesTrtmntString = mitesTrtmntEdit.getText().toString();

        int screenedBottomBoardInt = (screenedBottomBoardCheck.isChecked()) ? 1 : 0;
        int otherInt = (otherCheck.isChecked()) ? 1 : 0;

        String otherString = otherEdit.getText().toString();

        // Get the times in millis from the TextView tag
        long droneCellFndnRmndrLong = (long)droneCellFndnRmndrText.getTag();
        long mitesTrtmntRmndrLong = (long)mitesTrtmntRmndrText.getTag();

        // check for required values - are there any?
        boolean emptyText = false;

        if (mitesTrtmntCheck.isChecked()) {
            if ((mitesTrtmntString == null) || (mitesTrtmntString.length() == 0)) {
                mitesTrtmntEdit.setError("Mites Treatment cannot be empty");
                emptyText = true;
                Log.d(TAG, "Uh oh...Mites Treatment empty");
            }
        }

        if (otherCheck.isChecked()) {
            if ((otherString == null) || (otherString.length() == 0)) {
                otherEdit.setError("Other Treatment cannot be empty");
                emptyText = true;
                Log.d(TAG, "Uh oh...Other Treatment empty");
            }
        }

        if (!emptyText) {
            LogEntryPestMgmtDAO logEntryPestMgmtDAO = new LogEntryPestMgmtDAO(getActivity());
            if (mLogEntryPestMgmt == null) {
                mLogEntryPestMgmt = new LogEntryPestMgmt();
            }

            mLogEntryPestMgmt.setId(mLogEntryPestMgmtKey);
            mLogEntryPestMgmt.setHive(mHiveID);
            mLogEntryPestMgmt.setVisitDate(null);
            mLogEntryPestMgmt.setDroneCellFndn(droneCellFndnInt);
            mLogEntryPestMgmt.setSmallHiveBeetleTrap(smallHiveBeetleTrapInt);
            mLogEntryPestMgmt.setMitesTrtmnt(mitesTrtmntInt);
            mLogEntryPestMgmt.setMitesTrtmntType(mitesTrtmntString);
            mLogEntryPestMgmt.setScreenedBottomBoard(screenedBottomBoardInt);
            mLogEntryPestMgmt.setOther(otherInt);
            mLogEntryPestMgmt.setOtherType(otherString);
            mLogEntryPestMgmt.setDroneCellFndnRmndr(droneCellFndnRmndrLong);
            mLogEntryPestMgmt.setMitesTrtmntRmndr(mitesTrtmntRmndrLong);

            if (mListener != null) {
                mListener.onLogPestMgmtFragmentInteraction(mLogEntryPestMgmt);
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
                //timeLbl.setTag(time);

                /*
                --Let's do some Calendar stuff--
                 */
                long eventId = HiveCalendar.addEntryPublic(getActivity(), time, "Title", "Desc", "Loc");

                // tag really has Event ID of Event we just created
                timeLbl.setTag(eventId);

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
            mListener = (OnLogPestMgmntFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLogPestMgmntFragmentInteractionListener");
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
        if (mLogEntryPestMgmt != null) {
            outState.putString("visitDate", mLogEntryPestMgmt.getVisitDate());
            outState.putInt("droneCellFndn", mLogEntryPestMgmt.getDroneCellFndn());
            outState.putLong("droneCellFndnRmndr", mLogEntryPestMgmt.getDroneCellFndnRmndr());
            outState.putInt("smallHiveBeetleTrap", mLogEntryPestMgmt.getSmallHiveBeetleTrap());
            outState.putInt("mitesTrtmnt", mLogEntryPestMgmt.getMitesTrtmnt());
            outState.putString("mitesTrtmntType", mLogEntryPestMgmt.getMitesTrtmntType());
            outState.putLong("mitesTrtmntRmndr", mLogEntryPestMgmt.getMitesTrtmntRmndr());
            outState.putInt("screenedBottomBoard", mLogEntryPestMgmt.getScreenedBottomBoard());
            outState.putInt("other", mLogEntryPestMgmt.getOther());
            outState.putString("otherType", mLogEntryPestMgmt.getOtherType());
        }

        super.onSaveInstanceState(outState);
    }

    // Utility method to get Profile
    LogEntryPestMgmt getLogEntry(long aLogEntryID) {
        // read log Entry
        Log.d(TAG, "reading LogEntryPestMgmt table");
        LogEntryPestMgmtDAO logEntryPestMgmtDAO = new LogEntryPestMgmtDAO(getActivity());
        LogEntryPestMgmt reply = logEntryPestMgmtDAO.getLogEntryById(aLogEntryID);
        logEntryPestMgmtDAO.close();

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
    public interface OnLogPestMgmntFragmentInteractionListener {
        void onLogPestMgmtFragmentInteraction(LogEntryPestMgmt alogEntryPestMgmt);
        HiveNotesLogDO getPreviousLogData();
    }
}
