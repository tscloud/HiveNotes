package net.tscloud.hivenotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import net.tscloud.hivenotes.db.HiveNotesLogDO;
import net.tscloud.hivenotes.db.LogEntryHiveHealth;
import net.tscloud.hivenotes.db.LogEntryHiveHealthDAO;
import net.tscloud.hivenotes.db.NotificationType;
import net.tscloud.hivenotes.helper.GetReminderTimeTaskData;
import net.tscloud.hivenotes.helper.GetReminderTimeTask;
import net.tscloud.hivenotes.helper.MultiSelectOtherDialog;

import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLogPestMgmntFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogHiveHealthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogHiveHealthFragment extends LogFragment {

    public static final String TAG = "LogHiveHealthFragment";

    // DO for this particular Fragment
    private LogEntryHiveHealth mLogEntryHiveHealth;

    // reference to Activity that should have started me
    private OnLogPestMgmntFragmentInteractionListener mListener;

    // task references - needed to kill tasks on Fragment Destroy
    private GetReminderTimeTask mTaskDrone = null;
    private static final int TASK_DRONE = 0;
    private GetReminderTimeTask mTaskMites = null;
    private static final int TASK_MITES = 1;

    // Factory method to create a new instance of this fragment using the provided parameters.
    public static LogHiveHealthFragment newInstance(long hiveID, long logEntryDate, long logEntryID) {
        LogHiveHealthFragment fragment = new LogHiveHealthFragment();

        return (LogHiveHealthFragment)setLogFragArgs(fragment, hiveID, logEntryDate, logEntryID);
    }

    public LogHiveHealthFragment() {
        // Required empty public constructor
    }

    // Accessors needed by super class
    @Override
    protected HiveNotesLogDO getLogEntryDO() {
        return mLogEntryHiveHealth;
    }

    @Override
    protected void setLogEntryDO(HiveNotesLogDO aDataObj) {
        mLogEntryHiveHealth = (LogEntryHiveHealth)aDataObj;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // populate dataobject from Bundle
        if (savedInstanceState != null) {
            mLogEntryHiveHealth = new LogEntryHiveHealth();
            mLogEntryHiveHealth.setVisitDate(savedInstanceState.getLong("visitDate"));
            mLogEntryHiveHealth.setPestsDetected(savedInstanceState.getString("pestsDetected"));
            mLogEntryHiveHealth.setDiseaseDetected(savedInstanceState.getString("diseaseDetected"));
            mLogEntryHiveHealth.setPestsDetected(savedInstanceState.getString("pestsDetected"));
        }

        // save off arguments via super method
        saveOffArgs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_log_hivehealth_notes, container, false);

        // set button listeners and text
        final Button hiveNoteBtn = (Button)v.findViewById(R.id.hiveNoteButtton);
        hiveNoteBtn.setText(getResources().getString(R.string.done_string));

        final Button droneCellFndnBtn = (Button)v.findViewById(R.id.buttonDroneCellFndn);
        final Button mitesTrtmntBtn = (Button)v.findViewById(R.id.buttonMitesTrtmnt);

        // **TEST**
        final Button dialogTestBtn = (Button)v.findViewById(R.id.buttonDiagTest);

        // labels for showing reminder time; be sure to init the tag as this is what goes into the DB
        final TextView droneCellFndnRmndrText = (TextView)v.findViewById(R.id.textViewDroneCellFndnRmndr);
        droneCellFndnRmndrText.setTag((long)-1);
        final TextView mitesTrtmntRmndrText = (TextView)v.findViewById(R.id.textViewMitesTrtmntRmndr);
        mitesTrtmntRmndrText.setTag((long)-1);

        /**
         * call super method to get DO via best means
         */
        getLogEntry(mListener);

        if (mLogEntryHiveHealth != null) {

            // fill the form

            // do Reminders

            // If we have a time --> use it...
            //  it could be -2 indicating that an UNSET operation has occurred
            if (mLogEntryHiveHealth.getDroneCellFndnRmndrTime() == -2) {
                droneCellFndnRmndrText.setText(R.string.no_reminder_set);
                // don't forget to set the tag
                droneCellFndnRmndrText.setTag(mLogEntryHiveHealth.getDroneCellFndnRmndrTime());
            }
            else if (mLogEntryHiveHealth.getDroneCellFndnRmndrTime() != -1) {
                calendar.setTimeInMillis(mLogEntryHiveHealth.getDroneCellFndnRmndrTime());
                String droneDate = dateFormat.format(calendar.getTime());
                String droneTime = timeFormat.format(calendar.getTime());
                String droneDateTime = droneDate + ' ' + droneTime;
                droneCellFndnRmndrText.setText(droneDateTime);
                // don't forget to set the tag
                droneCellFndnRmndrText.setTag(mLogEntryHiveHealth.getDroneCellFndnRmndrTime());
            }

            if (mLogEntryHiveHealth.getMitesTrtmntRmndrTime() == -2) {
                mitesTrtmntRmndrText.setText(R.string.no_reminder_set);
                // don't forget to set the tag
                mitesTrtmntRmndrText.setTag(mLogEntryHiveHealth.getMitesTrtmntRmndrTime());
            }
            else if (mLogEntryHiveHealth.getMitesTrtmntRmndrTime() != -1) {
                calendar.setTimeInMillis(mLogEntryHiveHealth.getMitesTrtmntRmndrTime());
                String mitesDate = dateFormat.format(calendar.getTime());
                String mitesTime = timeFormat.format(calendar.getTime());
                String mitesDateTime = mitesDate + ' ' + mitesTime;
                mitesTrtmntRmndrText.setText(mitesDateTime);
                // don't forget to set the tag
                mitesTrtmntRmndrText.setTag(mLogEntryHiveHealth.getMitesTrtmntRmndrTime());
            }
        }

        // ...Otherwise --> spin up a task to get and set
        //  this check need be made regardless of nullness of DO -> Reminders are at the Hive level
        //   and may exist even if a log entry has not been made yet
        if ((mLogEntryHiveHealth == null) || (mLogEntryHiveHealth.getDroneCellFndnRmndrTime() == -1)) {
            //disable the button until task is thru
            droneCellFndnBtn.setEnabled(false);

            //setup and execute task
            mTaskDrone = new MyGetReminderTimeTask(
                    new GetReminderTimeTaskData(droneCellFndnBtn, droneCellFndnRmndrText,
                            NotificationType.NOTIFY_PEST_REMOVE_DRONE, mHiveID, TASK_DRONE,
                            calendar, dateFormat, timeFormat), getActivity());
            // All AsynchTasks executed serially on same background Thread
            //mTaskDrone.execute();
            // Each AsyncTask executes on its own Thread
            mTaskDrone.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        if ((mLogEntryHiveHealth == null) || (mLogEntryHiveHealth.getMitesTrtmntRmndrTime() == -1)) {
            //disable the button until task is thru
            mitesTrtmntBtn.setEnabled(false);

            //setup and execute task
            mTaskMites = new MyGetReminderTimeTask(
                    new GetReminderTimeTaskData(mitesTrtmntBtn, mitesTrtmntRmndrText,
                            NotificationType.NOTIFY_PEST_REMOVE_MITES, mHiveID, TASK_MITES,
                            calendar, dateFormat, timeFormat), getActivity());
            // All AsynchTasks executed serially on same background Thread
            //mTaskMites.execute();
            // Each AsyncTask executes on its own Thread
            mTaskMites.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                //HiveCalendar.calendarIntent(getActivity(), System.currentTimeMillis());
            }
        });

        mitesTrtmntBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReminderPressed(mitesTrtmntRmndrText);
            }
        });

        dialogTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = getResources().getString(R.string.hivehealth_notes_string);
                String[] elems = getResources().getStringArray(R.array.test_array);

                MultiSelectOtherDialog diagFragment = MultiSelectOtherDialog.newInstance(title, elems);
                diagFragment.show(getActivity().getSupportFragmentManager(), "missiles");

                /*
                String names[] ={"A","B","C","D","D","D","D","D","D","D","D","D","D","D","D","D"};
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View convertView = (View) inflater.inflate(R.layout.scb_listview2, null);
                alertDialog.setView(convertView);
                alertDialog.setTitle("List");
                ListView lv = (ListView) convertView.findViewById(R.id.lvScb);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,names);
                lv.setAdapter(adapter);
                alertDialog.show();
                */
            }
        });

        return v;
    }

    public void onHiveNoteButtonPressed(long hiveID) {
        // get log entry data and put to DB
        Log.d(TAG, "about to persist logentry");

        boolean lNewLogEntry = false;

        final TextView droneCellFndnRmndrText = (TextView)getView().findViewById(R.id.textViewDroneCellFndnRmndr);
        final TextView mitesTrtmntRmndrText = (TextView)getView().findViewById(R.id.textViewMitesTrtmntRmndr);

        // Get the times in millis from the TextView tag
        long droneCellFndnRmndrLong = (long)droneCellFndnRmndrText.getTag();
        long mitesTrtmntRmndrLong = (long)mitesTrtmntRmndrText.getTag();

        // check for required values - are there any?
        boolean emptyText = false;

        if (!emptyText) {
            LogEntryHiveHealthDAO logEntryHiveHealthDAO = new LogEntryHiveHealthDAO(getActivity());
            if (mLogEntryHiveHealth == null) {
                mLogEntryHiveHealth = new LogEntryHiveHealth();
            }

            mLogEntryHiveHealth.setId(mLogEntryKey);
            mLogEntryHiveHealth.setHive(mHiveID);
            mLogEntryHiveHealth.setVisitDate(mLogEntryDate);
            mLogEntryHiveHealth.setDroneCellFndnRmndrTime(droneCellFndnRmndrLong);
            mLogEntryHiveHealth.setMitesTrtmntRmndrTime(mitesTrtmntRmndrLong);

            if (mListener != null) {
                mListener.onLogPestMgmtFragmentInteraction(mLogEntryHiveHealth);
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
                String timeString = dateFormat.format(calendar.getTime()) + ' ' +
                        timeFormat.format(calendar.getTime());
                timeLbl.setText(timeString);
                timeLbl.setTag(time);

                alertDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.date_time_unset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Time UNpicked: ");

                timeLbl.setText(R.string.no_reminder_set);
                // IMPORTANT: -2 indicator of occurrence of UNSET operation
                timeLbl.setTag((long)-2);

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
        if (mLogEntryHiveHealth != null) {
            outState.putLong("visitDate", mLogEntryHiveHealth.getVisitDate());
            outState.putString("mitesTrtmntType", mLogEntryHiveHealth.getPestsDetected());
            outState.putString("diseaseDetected", mLogEntryHiveHealth.getDiseaseDetected());
            outState.putString("VarroaTreatment", mLogEntryHiveHealth.getVarroaTreatment());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (mTaskDrone != null) {
            mTaskDrone.cancel(false);
        }

        if (mTaskMites != null) {
            mTaskMites.cancel(false);
        }

        super.onDestroy();
    }

    @Override
    protected LogEntryHiveHealth getLogEntryFromDB(long aKey, long aDate) {
        // read log Entry
        Log.d(TAG, "reading LogEntryHiveHealth table");
        LogEntryHiveHealthDAO logEntryHiveHealthDAO = new LogEntryHiveHealthDAO(getActivity());
        LogEntryHiveHealth reply = null;

        if (aKey != -1) {
            reply = logEntryHiveHealthDAO.getLogEntryById(aKey);
        }
        else if (aDate != -1) {
            reply = logEntryHiveHealthDAO.getLogEntryByDate(aDate);
        }

        logEntryHiveHealthDAO.close();

        return reply;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnLogPestMgmntFragmentInteractionListener extends
            LogFragment.PreviousLogDataProvider {
        void onLogPestMgmtFragmentInteraction(LogEntryHiveHealth alogEntryHiveHealth);
    }

    /** subclass of the GetReminderTimeTask
     */
    class MyGetReminderTimeTask extends GetReminderTimeTask {

        public MyGetReminderTimeTask(GetReminderTimeTaskData aData, Context aCtx) {
           super(aData, aCtx);
        }

        protected void nullifyTaskRef(int taskRef) {
            Log.d(TAG, "in overridden GetReminderTimeTask.nullifyTaskRef(): taskRef:" + taskRef);
            switch (taskRef) {
                case TASK_DRONE:
                    mTaskDrone = null;
                    break;
                case TASK_MITES:
                    mTaskMites = null;
            }
        }
    }

}
