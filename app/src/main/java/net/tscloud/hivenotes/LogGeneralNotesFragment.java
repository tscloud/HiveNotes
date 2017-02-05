package net.tscloud.hivenotes;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import net.tscloud.hivenotes.db.HiveNotesLogDO;
import net.tscloud.hivenotes.db.LogEntryGeneral;
import net.tscloud.hivenotes.db.LogEntryGeneralDAO;

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

        final Button addPhotoBtn = (Button)v.findViewById(R.id.buttonAddPhoto);

        /**
         * call super method to get DO via best means
         */
        getLogEntry(mListener);

        if (mLogEntryGeneral != null) {

            // fill the form
            final Spinner populationSpinner = (Spinner)v.findViewById(R.id.spinnerPopulation);
            final Spinner temperamentSpinner = (Spinner)v.findViewById(R.id.spinnerTemperament);
            final Spinner broodPatternSpinner = (Spinner)v.findViewById(R.id.spinnerBroodPattern);
            final Spinner queenSpinner = (Spinner)v.findViewById(R.id.spinnerQueen);
            final Spinner honeyStoresSpinner = (Spinner)v.findViewById(R.id.spinnerHoneyStores);
            final Spinner pollenStoresSpinner = (Spinner)v.findViewById(R.id.spinnerPollenStores);

            populationSpinner.setSelection(
                    ((ArrayAdapter) populationSpinner.getAdapter()).getPosition(
                            mLogEntryGeneral.getPopulation()));
            temperamentSpinner.setSelection(
                    ((ArrayAdapter) temperamentSpinner.getAdapter()).getPosition(
                            mLogEntryGeneral.getTemperament()));
            broodPatternSpinner.setSelection(
                    ((ArrayAdapter) broodPatternSpinner.getAdapter()).getPosition(
                            mLogEntryGeneral.getBroodPattern()));
            queenSpinner.setSelection(
                    ((ArrayAdapter) queenSpinner.getAdapter()).getPosition(
                            mLogEntryGeneral.getQueen()));
            honeyStoresSpinner.setSelection(
                    ((ArrayAdapter) honeyStoresSpinner.getAdapter()).getPosition(
                            mLogEntryGeneral.getHoneyStores()));
            pollenStoresSpinner.setSelection(
                    ((ArrayAdapter) pollenStoresSpinner.getAdapter()).getPosition(
                            mLogEntryGeneral.getPollenStores()));
        }

        // set button listeners
        hiveNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHiveNoteBtnButtonPressed();
            }
        });

        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddPhotoBtnButtonPressed();
            }
        });

        return v;
    }

    private void onHiveNoteBtnButtonPressed() {
        // get log entry data and put to DB
        Log.d(TAG, "about to persist logentry");

        boolean lNewLogEntry = false;

        final Spinner populationSpinner = (Spinner)getView().findViewById(R.id.spinnerPopulation);
        final Spinner temperamentSpinner = (Spinner)getView().findViewById(R.id.spinnerTemperament);
        final Spinner broodPatternSpinner = (Spinner)getView().findViewById(R.id.spinnerBroodPattern);
        final Spinner queenSpinner = (Spinner)getView().findViewById(R.id.spinnerQueen);
        final Spinner honeyStoresSpinner = (Spinner)getView().findViewById(R.id.spinnerHoneyStores);
        final Spinner pollenStoresSpinner = (Spinner)getView().findViewById(R.id.spinnerPollenStores);

        String populationText = populationSpinner.getSelectedItem().toString();
        String temperamentText = temperamentSpinner.getSelectedItem().toString();

        String broodPatternText = broodPatternSpinner.getSelectedItem().toString();
        String queenText = queenSpinner.getSelectedItem().toString();
        String honeyStoresText = honeyStoresSpinner.getSelectedItem().toString();
        String pollenStoresText = pollenStoresSpinner.getSelectedItem().toString();

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
            mLogEntryGeneral.setPopulation(populationText);
            mLogEntryGeneral.setTemperament(temperamentText);
            mLogEntryGeneral.setBroodPattern(broodPatternText);
            mLogEntryGeneral.setQueen(queenText);
            mLogEntryGeneral.setHoneyStores(honeyStoresText);
            mLogEntryGeneral.setPollenStores(pollenStoresText);

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
