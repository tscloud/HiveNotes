package net.tscloud.hivenotes;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.tscloud.hivenotes.db.HiveNotesLogDO;
import net.tscloud.hivenotes.db.LogEntryGeneral;
import net.tscloud.hivenotes.db.LogEntryGeneralDAO;
import net.tscloud.hivenotes.helper.LogMultiSelectDialogData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLogGeneralNotesFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogGeneralNotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogGeneralNotesFragment extends LogFragment {

    public static final String TAG = "LogGeneralNotesFragment";

    // DO for this particular Fragment
    private LogEntryGeneral mLogEntryGeneral;

    // reference to Activity that should have started me
    private OnLogGeneralNotesFragmentInteractionListener mListener;

    // constants used for Dialogs
    public static final String DIALOG_TAG_POPULATION = "population";
    public static final String DIALOG_TAG_TEMPERAMENT = "temperament";
    public static final String DIALOG_TAG_QUEEN = "queen";
    public static final String DIALOG_TAG_BROODPATTERN = "broodpattern";
    public static final String DIALOG_TAG_HONEYSTORES = "honeystores";
    public static final String DIALOG_TAG_POLLENSTORES = "pollenstores";

    // Factory method to create a new instance of this fragment using the provided parameters.
    public static LogGeneralNotesFragment newInstance(long hiveID, long logEntryDate, long logEntryID) {
        LogGeneralNotesFragment fragment = new LogGeneralNotesFragment();

        return (LogGeneralNotesFragment)setLogFragArgs(fragment, hiveID, logEntryDate, logEntryID);
    }

    public LogGeneralNotesFragment() {
        // Required empty public constructor
    }

    // Accessors needed by super class
    @Override
    protected HiveNotesLogDO getLogEntryDO() {
        return mLogEntryGeneral;
    }

    @Override
    protected void setLogEntryDO(HiveNotesLogDO aDataObj) {
        mLogEntryGeneral = (LogEntryGeneral)aDataObj;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // populate dataobject from Bundle
        if (savedInstanceState != null) {
            mLogEntryGeneral = new LogEntryGeneral();
            mLogEntryGeneral.setVisitDate(savedInstanceState.getLong("visitDate"));
            mLogEntryGeneral.setPopulation(savedInstanceState.getString("population"));
            mLogEntryGeneral.setTemperament(savedInstanceState.getString("temperament"));
            mLogEntryGeneral.setBroodPattern(savedInstanceState.getString("broodPattern"));
            mLogEntryGeneral.setQueen(savedInstanceState.getString("queen"));
            mLogEntryGeneral.setHoneyStores(savedInstanceState.getString("honeyStores"));
            mLogEntryGeneral.setPollenStores(savedInstanceState.getString("pollenStores"));
        }

        // save off arguments via super method
        saveOffArgs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_log_general_notes, container, false);

        // set button listener and text
        final Button hiveNoteBtn = (Button)v.findViewById(R.id.hiveNoteButtton);
        hiveNoteBtn.setText(getResources().getString(R.string.done_string));

        // get reference to the <include>s
        final View dialogLogPopulation = v.findViewById(R.id.buttonLogPopulation);
        final View dialogLogTemperament = v.findViewById(R.id.buttonLogTemperament);
        final View dialogLogQueen = v.findViewById(R.id.buttonLogQueen);
        final View dialogLogBroodPattern = v.findViewById(R.id.buttonLogBroodPattern);
        final View dialogLogHoneyStores = v.findViewById(R.id.buttonLogHoneyStores);
        final View dialogLogPollenStores = v.findViewById(R.id.buttonLogPollenStores);
        final View dialogAddPhoto = v.findViewById(R.id.buttonAddPhoto);

        // set text of <include>s
        final TextView populationText =
                (TextView)dialogLogPopulation.findViewById(R.id.dialogLaunchTextView);
        populationText.setText(R.string.general_notes_population_text);

        final TextView temperamentText =
                (TextView)dialogLogTemperament.findViewById(R.id.dialogLaunchTextView);
        temperamentText.setText(R.string.general_notes_temperament_text);

        final TextView queenText =
                (TextView)dialogLogQueen.findViewById(R.id.dialogLaunchTextView);
        queenText.setText(R.string.general_notes_queen_text);

        final TextView broodPatternText =
                (TextView)dialogLogBroodPattern.findViewById(R.id.dialogLaunchTextView);
        broodPatternText.setText(R.string.general_notes_broodpattern_text);

        final TextView honeyStoresText =
                (TextView)dialogLogHoneyStores.findViewById(R.id.dialogLaunchTextView);
        honeyStoresText.setText(R.string.general_notes_honeystores_text);

        final TextView pollenStoresText =
                (TextView)dialogLogPollenStores.findViewById(R.id.dialogLaunchTextView);
        pollenStoresText.setText(R.string.general_notes_pollenstores_text);

        final TextView addPhotoText =
                (TextView)dialogAddPhoto.findViewById(R.id.dialogLaunchTextView);
        addPhotoText.setText(R.string.add_photo);

        /**
         * call super method to get DO via best means
         */
        getLogEntry(mListener);

        // set button listeners
        hiveNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHiveNoteBtnButtonPressed();
            }
        });

        dialogAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddPhotoBtnButtonPressed();
            }
        });

        dialogLogPopulation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    if (mLogEntryGeneral != null &&
                            mLogEntryGeneral.getPopulation() != null) {
                        checked = mLogEntryGeneral.getPopulation();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogMultiSelectDialogData(
                            getResources().getString(R.string.general_notes_population_text),
                            mHiveID,
                            getResources().getStringArray(R.array.population_array),
                            checked,
                            DIALOG_TAG_POPULATION,
                            -1,
                            //hasOther, hasReminder, multiselect
                            false, false, false));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        dialogLogTemperament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    if (mLogEntryGeneral != null &&
                            mLogEntryGeneral.getTemperament() != null) {
                        checked = mLogEntryGeneral.getTemperament();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogMultiSelectDialogData(
                            getResources().getString(R.string.general_notes_temperament_text),
                            mHiveID,
                            getResources().getStringArray(R.array.temperament_array),
                            checked,
                            DIALOG_TAG_TEMPERAMENT,
                            -1,
                            //hasOther, hasReminder, multiselect
                            false, false, false));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        dialogLogQueen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    long reminderMillis = -1;
                    if (mLogEntryGeneral != null) {
                            if (mLogEntryGeneral.getQueen() != null) {
                                checked = mLogEntryGeneral.getQueen();
                            }
                            reminderMillis = mLogEntryGeneral.getQueenRmndrTime();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogMultiSelectDialogData(
                            getResources().getString(R.string.general_notes_queen_text),
                            mHiveID,
                            getResources().getStringArray(R.array.queen_array),
                            checked,
                            DIALOG_TAG_QUEEN,
                            reminderMillis,
                            //hasOther, hasReminder, multiselect
                            false, true, false));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        dialogLogBroodPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    if (mLogEntryGeneral != null &&
                            mLogEntryGeneral.getBroodPattern() != null) {
                        checked = mLogEntryGeneral.getBroodPattern();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogMultiSelectDialogData(
                            getResources().getString(R.string.general_notes_broodpattern_text),
                            mHiveID,
                            getResources().getStringArray(R.array.brood_pattern_array),
                            checked,
                            DIALOG_TAG_BROODPATTERN,
                            -1,
                            //hasOther, hasReminder, multiselect
                            false, false, false));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        dialogLogHoneyStores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    if (mLogEntryGeneral != null &&
                            mLogEntryGeneral.getHoneyStores() != null) {
                        checked = mLogEntryGeneral.getHoneyStores();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogMultiSelectDialogData(
                            getResources().getString(R.string.general_notes_honeystores_text),
                            mHiveID,
                            getResources().getStringArray(R.array.stores_array),
                            checked,
                            DIALOG_TAG_HONEYSTORES,
                            -1,
                            //hasOther, hasReminder, multiselect
                            false, false, false));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        dialogLogPollenStores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    if (mLogEntryGeneral != null &&
                            mLogEntryGeneral.getPollenStores() != null) {
                        checked = mLogEntryGeneral.getPollenStores();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogMultiSelectDialogData(
                            getResources().getString(R.string.general_notes_pollenstores_text),
                            mHiveID,
                            getResources().getStringArray(R.array.stores_array),
                            checked,
                            DIALOG_TAG_POLLENSTORES,
                            -1,
                            //hasOther, hasReminder, multiselect
                            false, false, false));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        return v;
    }

    private void onHiveNoteBtnButtonPressed() {
        // get log entry data and put to DB
        Log.d(TAG, "about to persist logentry");

        boolean lNewLogEntry = false;

        // check for required values - are there any?
        boolean emptyText = false;

        if (!emptyText) {
            LogEntryGeneralDAO logEntryGeneralDAO = new LogEntryGeneralDAO(getActivity());
            if (mLogEntryGeneral == null) {
                mLogEntryGeneral = new LogEntryGeneral();
            }

            mLogEntryGeneral.setId(mLogEntryKey);
            mLogEntryGeneral.setHive(mHiveID);
            mLogEntryGeneral.setVisitDate(mLogEntryDate);

            if (mListener != null) {
                mListener.onLogGeneralNotesFragmentInteraction(mLogEntryGeneral);
            }
        }
    }

    private void onAddPhotoBtnButtonPressed() {
        Toast.makeText(getActivity(),"Add Photo button clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLogGeneralNotesFragmentInteractionListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLogGeneralNotesFragmentInteractionListener");
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
        if (mLogEntryGeneral != null) {
            outState.putLong("visitDate", mLogEntryGeneral.getVisitDate());
            outState.putString("population", mLogEntryGeneral.getPopulation());
            outState.putString("temperament", mLogEntryGeneral.getTemperament());
            outState.putString("broodPattern", mLogEntryGeneral.getBroodPattern());
            outState.putString("queen", mLogEntryGeneral.getQueen());
            outState.putString("honeyStores", mLogEntryGeneral.getHoneyStores());
            outState.putString("pollenStores", mLogEntryGeneral.getPollenStores());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected LogEntryGeneral getLogEntryFromDB(long aKey, long aDate) {
        // read log Entry
        Log.d(TAG, "reading LogEntryGeneral table");
        Log.d(TAG, "aKey: " + aKey + "....." + "aDate: " + aDate);
        LogEntryGeneralDAO logEntryGeneralDAO = new LogEntryGeneralDAO(getActivity());
        LogEntryGeneral reply = null;

        if (aKey != -1) {
            reply = logEntryGeneralDAO.getLogEntryById(aKey);
        }
        else if (aDate != -1) {
            reply = logEntryGeneralDAO.getLogEntryByDate(aDate);
        }

        logEntryGeneralDAO.close();

        if (reply != null) {
            Log.d(TAG, "DO date: " + reply.getVisitDate());
        }

        return reply;
    }

    @Override
    public void setDialogData(String[] aResults, long aResultRemTime, String aTag) {
        //may have to create the DO here - if we're a new entry and Dialog work was done before
        // anything else
        if (mLogEntryGeneral == null) {
            mLogEntryGeneral = new LogEntryGeneral();
        }

        switch (aTag){
            case DIALOG_TAG_POPULATION:
                mLogEntryGeneral.setPopulation(TextUtils.join(",", aResults));
                Log.d(TAG, "onLogLaunchDialog: setPopulation: " +
                        mLogEntryGeneral.getPopulation());
                break;
            case DIALOG_TAG_TEMPERAMENT:
                mLogEntryGeneral.setTemperament(TextUtils.join(",", aResults));
                Log.d(TAG, "onLogLaunchDialog: setTemperament: " +
                        mLogEntryGeneral.getTemperament());
                break;
            case DIALOG_TAG_QUEEN:
                mLogEntryGeneral.setQueen(TextUtils.join(",", aResults));
                Log.d(TAG, "onLogLaunchDialog: setQueen: " +
                        mLogEntryGeneral.getQueen());

                mLogEntryGeneral.setQueenRmndrTime(aResultRemTime);
                Log.d(TAG, "onLogLaunchDialog: setQueenRmndrTime: " +
                        mLogEntryGeneral.getQueenRmndrTime());

                break;
            case DIALOG_TAG_BROODPATTERN:
                mLogEntryGeneral.setBroodPattern(TextUtils.join(",", aResults));
                Log.d(TAG, "onLogLaunchDialog: setBroodPattern: " +
                        mLogEntryGeneral.getBroodPattern());
                break;
            case DIALOG_TAG_HONEYSTORES:
                mLogEntryGeneral.setHoneyStores(TextUtils.join(",", aResults));
                Log.d(TAG, "onLogLaunchDialog: setHoneyStores: " +
                        mLogEntryGeneral.getHoneyStores());
                break;
            case DIALOG_TAG_POLLENSTORES:
                mLogEntryGeneral.setPollenStores(TextUtils.join(",", aResults));
                Log.d(TAG, "onLogLaunchDialog: setPollenStores: " +
                        mLogEntryGeneral.getPollenStores());
                break;
            default:
                Log.d(TAG, "onLogLaunchDialog: unrecognized Dialog type");
        }
    }

    @Override
    public void setDialogDataCancel(String aTag) {
        // NOOP - just re-present fragment
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnLogGeneralNotesFragmentInteractionListener extends
            LogFragmentActivity {
        void onLogGeneralNotesFragmentInteraction(LogEntryGeneral aLogEntryGeneral);
    }

}
