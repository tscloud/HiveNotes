package net.tscloud.hivenotes;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import net.tscloud.hivenotes.db.LogEntryGeneral;
import net.tscloud.hivenotes.db.LogEntryGeneralDAO;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLogGeneralNotesFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogGeneralNotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogGeneralNotesFragment extends Fragment {

    public static final String TAG = "LogGeneralNotesFragment";

    private long mHiveID;
    private long mLogEntryGeneralKey;
    private LogEntryGeneral mLogEntryGeneral;

    private OnLogGeneralNotesFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param hiveID Parameter 1.
     * @param logEntryID Parameter 2.
     * @return A new instance of fragment LogGeneralNotesFragment.
     */
    public static LogGeneralNotesFragment newInstance(long hiveID, long logEntryID) {
        LogGeneralNotesFragment fragment = new LogGeneralNotesFragment();
        Bundle args = new Bundle();
        args.putLong(LogEntryListActivity.INTENT_HIVE_KEY, hiveID);
        args.putLong(LogEntryListActivity.INTENT_LOGENTRY_KEY, logEntryID);
        fragment.setArguments(args);
        return fragment;
    }

    public LogGeneralNotesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mHiveID = getArguments().getLong(LogEntryListActivity.INTENT_HIVE_KEY);
            mLogEntryGeneralKey = getArguments().getLong(LogEntryListActivity.INTENT_LOGENTRY_KEY);
        }

        if (mLogEntryGeneralKey != -1) {
            // we need to get the Hive
            mLogEntryGeneral = getLogEntry(mLogEntryGeneralKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_log_general_notes, container, false);

        // set button listener and text
        final Button b1 = (Button)v.findViewById(R.id.hiveNoteButtton);

        // enable "Other" EditText only if corresponding CheckBox checked
        final CheckBox otherCheck = (CheckBox)v.findViewById(R.id.checkOther);
        final EditText otherEdit = (EditText)v.findViewById(R.id.editTextOther);

        otherCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOtherCheckChecked(v, otherEdit);
            }
        });

        // setup numeric Spinner values
        final Spinner broodFramesSpinner = (Spinner)v.findViewById(R.id.spinnerBroodFrames);

        String [] a = new String[31];
        for (int i=0;i<a.length;++i) {
            a[i] = Integer.toString(i);
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (getActivity(),android.R.layout.simple_spinner_item, a);

        broodFramesSpinner.setAdapter(spinnerArrayAdapter);

        // date must be set if we are performing update
        final EditText dateEdit = (EditText)v.findViewById(R.id.editTextDate);

        if (mLogEntryGeneral != null) {
            b1.setText(getResources().getString(R.string.save_logentry_string));

            // setup for disease checkboxes
            String diseaseSetDBString = mLogEntryGeneral.getPestsDisease();
            //List<String> diseaseSetDBList = Arrays.asList(diseaseSetString.split("\\s*,\\s*"));
            //String[] diseaseSetConst = getResources().getStringArray(R.array.pests_disease_array);

            // fill the form
            final Spinner populationSpinner = (Spinner)v.findViewById(R.id.spinnerPopulation);
            final Spinner temperamentSpinner = (Spinner)v.findViewById(R.id.spinnerTemperament);
            final Spinner broodPatternSpinner = (Spinner)v.findViewById(R.id.spinnerBroodPattern);
            final Spinner queenSpinner = (Spinner)v.findViewById(R.id.spinnerQueen);
            final Spinner honeyStoresSpinner = (Spinner)v.findViewById(R.id.spinnerHoneyStores);
            final Spinner pollenStoresSpinner = (Spinner)v.findViewById(R.id.spinnerPollenStores);
            final CheckBox varroaMiteCheck = (CheckBox)v.findViewById(R.id.checkVarroaMite);
            final CheckBox smallHiveBeetleCheck = (CheckBox)v.findViewById(R.id.checkSmallHiveBeetle);
            final CheckBox waxMothCheck = (CheckBox)v.findViewById(R.id.checkWaxMoth);
            final CheckBox deformedWingCheck = (CheckBox)v.findViewById(R.id.checkDeformedWing);
            final CheckBox americanFoulbroodCheck = (CheckBox)v.findViewById(R.id.checkAmericalFoulbrood);
            final CheckBox europeanFoulbroodCheck = (CheckBox)v.findViewById(R.id.checkEuropeanFoulbrood);
            final CheckBox chalkbroodCheck = (CheckBox)v.findViewById(R.id.checkChalkbrood);

            dateEdit.setText(mLogEntryGeneral.getVisitDate());
            populationSpinner.setSelection(
                    ((ArrayAdapter) populationSpinner.getAdapter()).getPosition(
                            mLogEntryGeneral.getPopulation()));
            temperamentSpinner.setSelection(
                    ((ArrayAdapter) temperamentSpinner.getAdapter()).getPosition(
                            mLogEntryGeneral.getTemperament()));
            broodFramesSpinner.setSelection(
                    ((ArrayAdapter) broodFramesSpinner.getAdapter()).getPosition(
                            mLogEntryGeneral.getBroodFrames()));
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

            varroaMiteCheck.setChecked(diseaseSetDBString.contains(varroaMiteCheck.getText()));
            smallHiveBeetleCheck.setChecked(diseaseSetDBString.contains(smallHiveBeetleCheck.getText()));
            waxMothCheck.setChecked(diseaseSetDBString.contains(waxMothCheck.getText()));
            deformedWingCheck.setChecked(diseaseSetDBString.contains(deformedWingCheck.getText()));
            americanFoulbroodCheck.setChecked(diseaseSetDBString.contains(americanFoulbroodCheck.getText()));
            europeanFoulbroodCheck.setChecked(diseaseSetDBString.contains(europeanFoulbroodCheck.getText()));
            chalkbroodCheck.setChecked(diseaseSetDBString.contains(chalkbroodCheck.getText()));
            otherCheck.setChecked(diseaseSetDBString.contains(otherCheck.getText()));
        }
        else {
            b1.setText(getResources().getString(R.string.create_logentry_string));

            // default to todays date
            dateEdit.setText(new Date().toString());
        }

        // set button listener
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed();
            }
        });

        return v;
    }

    private void onButtonPressed() {
        // get log entry data and put to DB
        Log.d(TAG, "about to persist logentry");

        boolean lNewLogEntry = false;

        final EditText dateEdit = (EditText)getView().findViewById(R.id.editTextDate);
        final Spinner populationSpinner = (Spinner)getView().findViewById(R.id.spinnerPopulation);
        final Spinner temperamentSpinner = (Spinner)getView().findViewById(R.id.spinnerTemperament);
        final Spinner broodFramesSpinner = (Spinner)getView().findViewById(R.id.spinnerBroodFrames);
        final Spinner broodPatternSpinner = (Spinner)getView().findViewById(R.id.spinnerBroodPattern);
        final Spinner queenSpinner = (Spinner)getView().findViewById(R.id.spinnerQueen);
        final Spinner honeyStoresSpinner = (Spinner)getView().findViewById(R.id.spinnerHoneyStores);
        final Spinner pollenStoresSpinner = (Spinner)getView().findViewById(R.id.spinnerPollenStores);
        final CheckBox varroaMiteCheck = (CheckBox)getView().findViewById(R.id.checkVarroaMite);
        final CheckBox smallHiveBeetleCheck = (CheckBox)getView().findViewById(R.id.checkSmallHiveBeetle);
        final CheckBox waxMothCheck = (CheckBox)getView().findViewById(R.id.checkWaxMoth);
        final CheckBox deformedWingCheck = (CheckBox)getView().findViewById(R.id.checkDeformedWing);
        final CheckBox americanFoulbroodCheck = (CheckBox)getView().findViewById(R.id.checkAmericalFoulbrood);
        final CheckBox europeanFoulbroodCheck = (CheckBox)getView().findViewById(R.id.checkEuropeanFoulbrood);
        final CheckBox chalkbroodCheck = (CheckBox)getView().findViewById(R.id.checkChalkbrood);
        final CheckBox otherCheck = (CheckBox)getView().findViewById(R.id.checkOther);
        final EditText otherEdit = (EditText)getView().findViewById(R.id.editTextOther);

        String dateText = dateEdit.getText().toString();
        String populationText = populationSpinner.getSelectedItem().toString();
        String temperamentText = temperamentSpinner.getSelectedItem().toString();

        String broodFramesText = broodFramesSpinner.getSelectedItem().toString();
        int broodFramesInt = Integer.parseInt(broodFramesText);

        String broodPatternText = broodPatternSpinner.getSelectedItem().toString();
        String queenText = queenSpinner.getSelectedItem().toString();
        String honeyStoresText = honeyStoresSpinner.getSelectedItem().toString();
        String pollenStoresText = pollenStoresSpinner.getSelectedItem().toString();
        String otherText = otherEdit.getText().toString();

        StringBuffer pestsDiseaseTextBuf = new StringBuffer();
        if (varroaMiteCheck.isChecked()) {
            pestsDiseaseTextBuf.append(varroaMiteCheck.getText());
            pestsDiseaseTextBuf.append(",");
        }
        if (smallHiveBeetleCheck.isChecked()) {
            pestsDiseaseTextBuf.append(smallHiveBeetleCheck.getText());
            pestsDiseaseTextBuf.append(",");
        }
        if (waxMothCheck.isChecked()) {
            pestsDiseaseTextBuf.append(waxMothCheck.getText());
            pestsDiseaseTextBuf.append(",");
        }
        if (deformedWingCheck.isChecked()) {
            pestsDiseaseTextBuf.append(deformedWingCheck.getText());
            pestsDiseaseTextBuf.append(",");
        }
        if (americanFoulbroodCheck.isChecked()) {
            pestsDiseaseTextBuf.append(americanFoulbroodCheck.getText());
            pestsDiseaseTextBuf.append(",");
        }
        if (europeanFoulbroodCheck.isChecked()) {
            pestsDiseaseTextBuf.append(europeanFoulbroodCheck.getText());
            pestsDiseaseTextBuf.append(",");
        }
        if (chalkbroodCheck.isChecked()) {
            pestsDiseaseTextBuf.append(chalkbroodCheck.getText());
            pestsDiseaseTextBuf.append(",");
        }

        // check for required values - are there any?
        boolean emptyText = false;

        // cannot go if otherCheck is checked and otherEdit empty
        if (otherCheck.isChecked()) {
            if (otherText.length() == 0) {
                otherEdit.setError("Must provide Other disease");
                emptyText = true;
                Log.d(TAG, "Uh oh...Other disease empty");
            }
            else {
                pestsDiseaseTextBuf.append(otherText);
            }
        }

        if (!emptyText) {
            LogEntryGeneralDAO logEntryGeneralDAO = new LogEntryGeneralDAO(getActivity());
            if (mLogEntryGeneral == null) {
                mLogEntryGeneral = new LogEntryGeneral();
            }
            /*
            LogEntryGeneral logEntryGeneral;
            if (mLogEntryGeneralKey == -1) {
                logEntryGeneral = logEntryGeneralDAO.createLogEntry(mHiveID, dateText, populationText,
                        temperamentText, pestsDiseaseTextBuf.toString().replaceAll(" ,$", ""),
                        broodFramesInt, broodPatternText, queenText, honeyStoresText, pollenStoresText);
                lNewLogEntry = true;
            }
            else {
                logEntryGeneral = logEntryGeneralDAO.updateLogEntry(mLogEntryGeneralKey, mHiveID, dateText,
                        populationText, temperamentText, pestsDiseaseTextBuf.toString().replaceAll(" ,$", ""),
                        broodFramesInt, broodPatternText, queenText, honeyStoresText, pollenStoresText);
            }
            logEntryGeneralDAO.close();

            if (logEntryGeneral != null) {
                // Reset LogEntryGeneral instance vars
                mLogEntryGeneral = logEntryGeneral;
                mLogEntryGeneralKey = logEntryGeneral.getId();

                Log.d(TAG, "LogEntryGeneral Date: " + logEntryGeneral.getVisitDate());
            }
            else {
                Log.d(TAG, "BAD...LogEntryGeneral update failed");
            }
            */
            mLogEntryGeneral.setId(mLogEntryGeneralKey);
            mLogEntryGeneral.setHive(mHiveID);
            mLogEntryGeneral.setVisitDate(dateText);
            mLogEntryGeneral.setPopulation(populationText);
            mLogEntryGeneral.setTemperament(temperamentText);
            mLogEntryGeneral.setPestsDisease(pestsDiseaseTextBuf.toString().replaceAll(" ,$", ""));
            mLogEntryGeneral.setBroodFrames(broodFramesInt);
            mLogEntryGeneral.setBroodPattern(broodPatternText);
            mLogEntryGeneral.setQueen(queenText);
            mLogEntryGeneral.setHoneyStores(honeyStoresText);
            mLogEntryGeneral.setPollenStores(pollenStoresText);

            if (mListener != null) {
                mListener.onLogGeneralNotesFragmentInteraction(mLogEntryGeneral);
            }
        }

    }

    private void onOtherCheckChecked(View v, EditText e) {
        if (((CheckBox)v).isChecked()) {
            e.setFocusable(true);
            e.setFocusableInTouchMode(true);
            e.setClickable(true);
            e.setBackgroundColor(Color.RED);
        }
        else {
            e.setFocusable(false);
            e.setFocusableInTouchMode(false);
            e.setClickable(false);
            e.setBackgroundColor(Color.WHITE);
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLogGeneralNotesFragmentInteractionListener) activity;
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

    // Utility method to get Profile
    LogEntryGeneral getLogEntry(long aLogEntryID) {
        // read log Entry
        Log.d(TAG, "reading LogEntryGeneral table");
        LogEntryGeneralDAO logEntryGeneralDAO = new LogEntryGeneralDAO(getActivity());
        LogEntryGeneral reply = logEntryGeneralDAO.getLogEntryById(aLogEntryID);
        logEntryGeneralDAO.close();

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
    public interface OnLogGeneralNotesFragmentInteractionListener {
        public void onLogGeneralNotesFragmentInteraction(LogEntryGeneral aLogEntryGeneral);
    }

}
