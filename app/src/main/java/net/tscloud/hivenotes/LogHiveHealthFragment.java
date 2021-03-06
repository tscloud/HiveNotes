package net.tscloud.hivenotes;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.tscloud.hivenotes.db.HiveNotesLogDO;
import net.tscloud.hivenotes.db.LogEntryHiveHealth;
import net.tscloud.hivenotes.db.LogEntryHiveHealthDAO;
import net.tscloud.hivenotes.helper.LogMultiSelectDialogData;

import java.util.ArrayList;


/**
 * A LogFragment subclass.
 */
public class LogHiveHealthFragment extends LogFragment {

    public static final String TAG = "LogHiveHealthFragment";

    // DO for this particular Fragment
    private LogEntryHiveHealth mLogEntryHiveHealth;

    // constants used for Dialogs
    public static final String DIALOG_TAG_PESTS = "pests";
    public static final String DIALOG_TAG_DISEASE = "disease";
    public static final String DIALOG_TAG_VARROA = "varroa";

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
    protected HiveNotesLogDO setLogEntryDO(HiveNotesLogDO aDataObj) {
        mLogEntryHiveHealth = (LogEntryHiveHealth)aDataObj;
        return mLogEntryHiveHealth;
    }

    @Override
    protected HiveNotesLogDO makeLogEntryDO() {
        mLogEntryHiveHealth = new LogEntryHiveHealth();
        return mLogEntryHiveHealth;
    }

    @Override
    protected String getDOKey() {
        return LogEntryListActivity.INTENT_LOGENTRY_PESTMGMT_DATA;
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
            mLogEntryHiveHealth.setVarroaTreatment(savedInstanceState.getString("varroaTreatment"));
        }

        // save off arguments via super method
        saveOffArgs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_log_hivehealth_notes, container, false);

        // get reference to the <include>s and...
        // ...put them in the disable/enable List used by GetLogData AsyncTask
        ArrayList<View> disEnThese = new ArrayList<>();

        final View dialogHiveHealthPest = v.findViewById(R.id.buttonHiveHealthPest);
        disEnThese.add(dialogHiveHealthPest);

        final View dialogHiveHealthDisease = v.findViewById(R.id.buttonHiveHealthDisease);
        disEnThese.add(dialogHiveHealthDisease);

        final View dialogHiveHealthVarroa = v.findViewById(R.id.buttonHiveHealthVarroa);
        disEnThese.add(dialogHiveHealthVarroa);

        // set text of <include>s
        final TextView pestText =
                (TextView)dialogHiveHealthPest.findViewById(R.id.dialogLaunchTextView);
        pestText.setText(R.string.pests_detected);

        final TextView diseaseText =
                (TextView)dialogHiveHealthDisease.findViewById(R.id.dialogLaunchTextView);
        diseaseText.setText(R.string.disease_detected);

        final TextView varroaText =
                (TextView)dialogHiveHealthVarroa.findViewById(R.id.dialogLaunchTextView);
        varroaText.setText(R.string.varroa_treatment);

        /**
         * call super method to get DO via best means
         */
        // This method blocks on DB operation
        //getLogEntry(mListener);

        // This method uses AsyncTask on DB operation
        getLogEntry(mListener, new LogEntryHiveHealthDAO(getActivity()), disEnThese,
            getActivity());

        dialogHiveHealthPest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    long reminderMillis = -1;
                    if (mLogEntryHiveHealth != null) {
                            if (mLogEntryHiveHealth.getPestsDetected() != null) {
                                checked = mLogEntryHiveHealth.getPestsDetected();
                            }
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogMultiSelectDialogData(
                            getResources().getString(R.string.pests_detected),
                            mHiveID,
                            getResources().getStringArray(R.array.pests_array),
                            checked,
                            DIALOG_TAG_PESTS,
                            reminderMillis,
                            //hasOther, hasReminder, multiselect
                            true, false, true));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        dialogHiveHealthDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    if (mLogEntryHiveHealth != null &&
                            mLogEntryHiveHealth.getDiseaseDetected() != null) {
                        checked = mLogEntryHiveHealth.getDiseaseDetected();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogMultiSelectDialogData(
                            getResources().getString(R.string.disease_detected),
                            mHiveID,
                            getResources().getStringArray(R.array.disease_array),
                            checked,
                            DIALOG_TAG_DISEASE,
                            -1,
                            //hasOther, hasReminder, multiselect
                            true, false, false));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        dialogHiveHealthVarroa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    long reminderMillis = -1;
                    if (mLogEntryHiveHealth != null) {
                        if (mLogEntryHiveHealth.getVarroaTreatment() != null) {
                            checked = mLogEntryHiveHealth.getVarroaTreatment();
                        }
                        reminderMillis = mLogEntryHiveHealth.getVarroaTrtmntRmndrTime();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogMultiSelectDialogData(
                            getResources().getString(R.string.varroa_treatment),
                            mHiveID,
                            getResources().getStringArray(R.array.varroa_array),
                            checked,
                            DIALOG_TAG_VARROA,
                            reminderMillis,
                            //hasOther, hasReminder, multiselect
                            true, true, true));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save off values potentially entered from screen
        if (mLogEntryHiveHealth != null) {
            outState.putLong("visitDate", mLogEntryHiveHealth.getVisitDate());
            outState.putString("mitesTrtmntType", mLogEntryHiveHealth.getPestsDetected());
            outState.putString("diseaseDetected", mLogEntryHiveHealth.getDiseaseDetected());
            outState.putString("varroaTreatment", mLogEntryHiveHealth.getVarroaTreatment());
        }

        super.onSaveInstanceState(outState);
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
     * Method that passes data back to Fragment that was collected by Dialog
     */
    @Override
    public void setDialogData(String[] aResults, long aResultRemTime, String aResultRemDesc, String aTag) {
        //may have to create the DO here - if we're a new entry and Dialog work was done before
        // anything else
        if (mLogEntryHiveHealth == null) {
            mLogEntryHiveHealth = new LogEntryHiveHealth();
        }

        switch (aTag){
            case DIALOG_TAG_PESTS:
                mLogEntryHiveHealth.setPestsDetected(TextUtils.join(",", aResults));
                Log.d(TAG, "onLogLaunchDialog: setPestsDetected: " +
                        mLogEntryHiveHealth.getPestsDetected());
                break;
            case DIALOG_TAG_DISEASE:
                mLogEntryHiveHealth.setDiseaseDetected(TextUtils.join(",", aResults));
                Log.d(TAG, "onLogLaunchDialog: setDiseaseDetected: " +
                        mLogEntryHiveHealth.getDiseaseDetected());
                break;
            case DIALOG_TAG_VARROA:
                mLogEntryHiveHealth.setVarroaTreatment(TextUtils.join(",", aResults));
                Log.d(TAG, "onLogLaunchDialog: setVarroaTreatment: " +
                        mLogEntryHiveHealth.getVarroaTreatment());

                mLogEntryHiveHealth.setVarroaTrtmntRmndrTime(aResultRemTime);
                Log.d(TAG, "onLogLaunchDialog: setVarroaTrtmntRmndrTime: " +
                        mLogEntryHiveHealth.getVarroaTrtmntRmndrTime());

                break;
            default:
                Log.d(TAG, "onLogLaunchDialog: unrecognized Dialog type");
        }
    }
}
