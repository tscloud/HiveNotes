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
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;

import net.tscloud.hivenotes.db.LogEntryPestMgmt;
import net.tscloud.hivenotes.db.LogEntryPestMgmtDAO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLogPestMgmntFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogPestMgmtFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogPestMgmtFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String TAG = "LogPestMgmtFragment";

    // for reminders
    private static final String TIME_PATTERN = "HH:mm";
    Calendar calendar = Calendar.getInstance();
    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
    SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());
    TextView lblDroneCellFndnRmndr;
    TextView lblMitesTrtmntRmndr;

    private long mHiveID;
    private long mLogEntryPestMgmtKey;
    private LogEntryPestMgmt mLogEntryPestMgmt;

    private OnLogPestMgmntFragmentInteractionListener mListener;

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

        if (getArguments() != null) {
            mHiveID = getArguments().getLong(LogEntryListActivity.INTENT_HIVE_KEY);
            mLogEntryPestMgmtKey = getArguments().getLong(LogEntryListActivity.INTENT_LOGENTRY_KEY);
        }

        if (mLogEntryPestMgmtKey != -1) {
            // we need to get the Hive
            mLogEntryPestMgmt = getLogEntry(mLogEntryPestMgmtKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_log_pestmgmt_notes, container, false);

        // set button listener and text
        final Button btnHiveNote = (Button)v.findViewById(R.id.hiveNoteButtton);
        btnHiveNote.setText(getResources().getString(R.string.done_string));

        // set up reminder buttons
        final Button btnSetDrone = (Button)v.findViewById(R.id.buttonDroneCellFndn);
        final Button btnSetMites = (Button)v.findViewById(R.id.buttonMitesTrtmnt);

        lblDroneCellFndnRmndr = (TextView)v.findViewById(R.id.textViewDroneCellFndnRmndr);
        lblMitesTrtmntRmndr = (TextView)v.findViewById(R.id.textViewMitesTrtmntRmndr);

        update();

        if (mLogEntryPestMgmt != null) {

            // fill the form
            final CheckBox droneCellFndnCheck = (CheckBox)v.findViewById(R.id.checkDroneCellFndn);
            final CheckBox smallHiveBeetleTrapCheck = (CheckBox)v.findViewById(R.id.checkSmallHiveBeetleTrap);
            final CheckBox mitesTrtmntCheck = (CheckBox)v.findViewById(R.id.checkMitesTrtmnt);
            final EditText mitesTrtmntEdit = (EditText)v.findViewById(R.id.editTextMitesTrtmnt);
            final CheckBox screenedBottomBoardCheck = (CheckBox)v.findViewById(R.id.checkScreenedBottomBoard);
            final CheckBox otherCheck = (CheckBox)v.findViewById(R.id.checkPestOther);
            final EditText otherEdit = (EditText)v.findViewById(R.id.editTextOther);

            droneCellFndnCheck.setChecked(mLogEntryPestMgmt.getDroneCellFndn() != 0);
            smallHiveBeetleTrapCheck.setChecked(mLogEntryPestMgmt.getSmallHiveBeetleTrap() != 0);
            mitesTrtmntCheck.setChecked(mLogEntryPestMgmt.getMitesTrtmnt() != 0);
            mitesTrtmntEdit.setText(mLogEntryPestMgmt.getMitesTrtmntType());
            screenedBottomBoardCheck.setChecked(mLogEntryPestMgmt.getScreenedBottomBoard() != 0);
            otherCheck.setChecked(mLogEntryPestMgmt.getOther() != 0);
            otherEdit.setText(mLogEntryPestMgmt.getOtherType());
        }

        // set button listener
        btnHiveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHiveNoteButtonPressed(mHiveID);
            }
        });

        btnSetDrone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSetDroneButtonPressed(mHiveID);
            }
        });

        btnSetMites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSetMitesButtonPressed(mHiveID);
            }
        });

        return v;
    }

    private void update() {
        lblDroneCellFndnRmndr.setText(dateFormat.format(calendar.getTime()) + ' ' + timeFormat.format(calendar.getTime()));
        lblMitesTrtmntRmndr.setText(dateFormat.format(calendar.getTime()) + ' ' + timeFormat.format(calendar.getTime()));
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

        int droneCellFndnInt = (droneCellFndnCheck.isChecked()) ? 1 : 0;
        int smallHiveBeetleTrapInt = (smallHiveBeetleTrapCheck.isChecked()) ? 1 : 0;
        int mitesTrtmntInt = (mitesTrtmntCheck.isChecked()) ? 1 : 0;

        String mitesTrtmntString = mitesTrtmntEdit.getText().toString();

        int screenedBottomBoardInt = (screenedBottomBoardCheck.isChecked()) ? 1 : 0;
        int otherInt = (otherCheck.isChecked()) ? 1 : 0;

        String otherString = otherEdit.getText().toString();

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

            if (mListener != null) {
                mListener.onLogPestMgmtFragmentInteraction(mLogEntryPestMgmt);
            }

        }
    }

    public void onSetDroneButtonPressed(long hiveID) {
        DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show(getChildFragmentManager(), "datePicker");
    }

    public void onSetMitesButtonPressed(long hiveID) {

    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        update();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        update();
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
        public void onLogPestMgmtFragmentInteraction(LogEntryPestMgmt alogEntryPestMgmt);
    }

}
