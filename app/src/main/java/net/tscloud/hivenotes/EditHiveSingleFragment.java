package net.tscloud.hivenotes;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.tscloud.hivenotes.db.Hive;
import net.tscloud.hivenotes.db.HiveDAO;
import net.tscloud.hivenotes.helper.HiveDeleteDialog;
import net.tscloud.hivenotes.helper.LogEditTextDialogData;
import net.tscloud.hivenotes.helper.LogMultiSelectDialogData;
import net.tscloud.hivenotes.helper.LogSuperDataEntry;


/**
 * A simple subclass.
 * Activities that contain this fragment must implement the
 * OnEditHiveSingleFragmentInteractionListener interface
 * to handle interaction events.
 * Use the EditHiveSingleFragment#newInstance factory method to
 * create an instance of this fragment.
 */
public class EditHiveSingleFragment extends HiveDataEntryFragment {

    public static final String TAG = "EditHiveSingleFragment";

    // constants used for Dialogs
    public static final String DIALOG_TAG_NAME = "name";
    public static final String DIALOG_TAG_SPECIES = "species";
    public static final String DIALOG_TAG_QUEEN = "queen";
    public static final String DIALOG_TAG_FOUNDATION = "foundation";
    public static final String DIALOG_TAG_NOTES = "notes";

    // the fragment initialization parameters
    private static final String APIARY_KEY = "apiaryKey";
    private static final String HIVE_KEY = "hiveKey";
    // and instance var of same - needed?
    private long mApiaryKey;
    private long mHiveKey;
    private Hive mHive;

    private OnEditHiveSingleFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static EditHiveSingleFragment newInstance(long apiaryKey, long hiveKey) {
        Log.d(TAG, "getting newInstance of EditHiveSingleFragment...");

        EditHiveSingleFragment fragment = new EditHiveSingleFragment();
        Bundle args = new Bundle();

        args.putLong(APIARY_KEY, apiaryKey);
        args.putLong(HIVE_KEY, hiveKey);
        fragment.setArguments(args);

        return fragment;
    }

    public EditHiveSingleFragment() {
        // Required empty public constructor
    }

    // To properly identify a Hive fragment
    public long getHiveKey() {
        return mHiveKey;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mApiaryKey = getArguments().getLong(APIARY_KEY);
            mHiveKey = getArguments().getLong(HIVE_KEY);
        }

        if (mHiveKey != -1) {
            Log.d(TAG, "received Hive key...try to get Hive for update");
            // we need to get the Hive
            mHive = getHive(mHiveKey);
        }
        else {
            Log.d(TAG, "no Hive key received...will be creating new Hive");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_single_hive, container, false);

        // Delete button and title stuff
        final TextView textNew = (TextView)v.findViewById(R.id.textNewHive);
        final View viewDelete = v.findViewById(R.id.deleteHiveButton);
        final Button btnDelete = (Button)viewDelete.findViewById(R.id.hiveNoteButtton);
        btnDelete.setText(getResources().getString(R.string.delete_hive_string));

        // get reference to the <include>s
        final View dialogHiveName = v.findViewById(R.id.buttonHiveName);
        final View dialogHiveSpecies = v.findViewById(R.id.buttonHiveSpecies);
        final View dialogHiveQueen = v.findViewById(R.id.buttonHiveQueen);
        final View dialogHiveFoundation = v.findViewById(R.id.buttonHiveFoundation);
        final View dialogHiveNotes = v.findViewById(R.id.buttonHiveNotes);

        // set text of <include>s
        final TextView nameText =
                (TextView)dialogHiveName.findViewById(R.id.dialogLaunchTextView);
        nameText.setText(R.string.hive_name_string);

        final TextView speciesText =
                (TextView)dialogHiveSpecies.findViewById(R.id.dialogLaunchTextView);
        speciesText.setText(R.string.hive_species_string);

        final TextView queenText =
                (TextView)dialogHiveQueen.findViewById(R.id.dialogLaunchTextView);
        queenText.setText(R.string.general_notes_queen_text);

        final TextView foundationText =
                (TextView)dialogHiveFoundation.findViewById(R.id.dialogLaunchTextView);
        foundationText.setText(R.string.hive_foundation_type_string);

        final TextView notesText =
                (TextView)dialogHiveNotes.findViewById(R.id.dialogLaunchTextView);
        notesText.setText(R.string.hive_note_string);

        if (mHive != null) {
            Log.d(TAG, "successfully retrieved Hive data");
        }
        else {
            textNew.setText(getResources().getString(R.string.new_hive_string));
            //get rid of Delete button
            btnDelete.setEnabled(false);
            btnDelete.setTextColor(Color.GRAY);
        }

        // set delete button listener
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HiveHiveDeleteDialog().doDeleteDialog();
            }
        });

        // set dialog button listeners
        dialogHiveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    if (mHive != null) {
                        checked = mHive.getName();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogEditTextDialogData(
                            getResources().getString(R.string.hive_name_string),
                            DIALOG_TAG_NAME,
                            checked,
                            false));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        dialogHiveSpecies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    if (mHive != null &&
                            mHive.getSpecies() != null) {
                        checked = mHive.getSpecies();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogMultiSelectDialogData(
                            getResources().getString(R.string.hive_species_string),
                            mHiveKey,
                            getResources().getStringArray(R.array.species_array),
                            checked,
                            DIALOG_TAG_SPECIES,
                            -1,
                            //hasOther, hasReminder, multiselect
                            false, false, false));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        dialogHiveQueen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    if (mHive != null &&
                            mHive.getRequeen() != null) {
                        checked = mHive.getRequeen();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogMultiSelectDialogData(
                            getResources().getString(R.string.general_notes_queen_text),
                            mHiveKey,
                            getResources().getStringArray(R.array.requeen_array),
                            checked,
                            DIALOG_TAG_QUEEN,
                            -1,
                            //hasOther, hasReminder, multiselect
                            false, false, false));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        dialogHiveFoundation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    if (mHive != null &&
                            mHive.getFoundationType() != null) {
                        checked = mHive.getFoundationType();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogMultiSelectDialogData(
                            getResources().getString(R.string.hive_foundation_type_string),
                            mHiveKey,
                            getResources().getStringArray(R.array.foundation_array),
                            checked,
                            DIALOG_TAG_FOUNDATION,
                            -1,
                            //hasOther, hasReminder, multiselect
                            false, false, false));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        dialogHiveNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    if (mHive != null) {
                        checked = mHive.getNote();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogEditTextDialogData(
                            getResources().getString(R.string.hive_note_string),
                            DIALOG_TAG_NOTES,
                            checked,
                            false));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        return v;
    }

    @Override
    public boolean onFragmentSave() {
        // get name and email and put to DB
        Log.d(TAG, "about to persist hive");

        boolean reply = false;
        boolean lNewHive = false;

        if (mHive == null) {
            mHive = new Hive();
        }

        HiveDAO hiveDAO = new HiveDAO(getActivity());
        Hive hive;

        mHive.setApiary(mApiaryKey);

        if (mHiveKey == -1) {
            hive = hiveDAO.createHive(mHive);
            lNewHive = true;
        }
        else {
            mHive.setId(mHiveKey);
            hive = hiveDAO.updateHive(mHive);
        }
        hiveDAO.close();

        if (hive != null) {
            // Reset Hive instance vars
            mHive = hive;
            mHiveKey = hive.getId();

            Log.d(TAG, "Hive Name: " + hive.getName() + " persisted");
            Log.d(TAG, "Hive Species: " + hive.getSpecies() + " persisted");
            Log.d(TAG, "Hive Requeen: " + hive.getRequeen() + " persisted");
            Log.d(TAG, "Hive Foundation Type: " + hive.getFoundationType() + " persisted");
        }
        else  {
            Log.d(TAG, "BAD...Hive update failed");
        }

        if (mListener != null) {
            mListener.onEditHiveSingleFragmentInteraction(hive.getId(), lNewHive, false);
            reply = true;
        }

        return reply;
    }

    private void onDeleteButtonPressed() {
        // delete hive from DB
        Log.d(TAG, "about to delete hive");
        HiveDAO hiveDAO = new HiveDAO(getActivity());
        Hive hive = new Hive();
        hive.setId(mHiveKey);
        hiveDAO.deleteHive(hive);
        hiveDAO.close();

        if (mListener != null) {
            mListener.onEditHiveSingleFragmentInteraction(hive.getId(), false, true);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (EditHiveSingleActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEditHiveSingleFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // Utility method to get Profile
    private Hive getHive(long aHiveID) {
        // read Hive
        Log.d(TAG, "reading Hive table");
        HiveDAO hiveDAO = new HiveDAO(getActivity());
        Hive reply = hiveDAO.getHiveById(aHiveID);
        hiveDAO.close();

        return reply;
    }

    public class HiveHiveDeleteDialog extends HiveDeleteDialog {

        protected HiveHiveDeleteDialog() {
            super(getActivity(), "Are you sure you want to delete this Hive?");
        }

        protected void doDelete(){
            onDeleteButtonPressed();
        }

    }

    @Override
    public void setDialogData(String[] aResults, long aResultRemTime, String aTag) {
        //may have to create the DO here - if we're a new entry and Dialog work was done before
        // anything else
        if (mHive == null) {
            mHive = new Hive();
        }

        switch (aTag){
            case DIALOG_TAG_NAME:
                mHive.setName(aResults[0]);
                Log.d(TAG, "onLogLaunchDialog: setName: " +
                        mHive.getName());
                break;
            case DIALOG_TAG_SPECIES:
                mHive.setSpecies(aResults[0]);
                Log.d(TAG, "onLogLaunchDialog: setSpecies: " +
                        mHive.getSpecies());
                break;
            case DIALOG_TAG_QUEEN:
                mHive.setRequeen(aResults[0]);
                Log.d(TAG, "onLogLaunchDialog: setRequeen: " +
                        mHive.getRequeen());
                break;
            case DIALOG_TAG_FOUNDATION:
                mHive.setFoundationType(aResults[0]);
                Log.d(TAG, "onLogLaunchDialog: setFoundationType: " +
                        mHive.getFoundationType());
                break;
            case DIALOG_TAG_NOTES:
                mHive.setNote(aResults[0]);
                Log.d(TAG, "onLogLaunchDialog: setNote: " +
                        mHive.getNote());
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
    public interface OnEditHiveSingleFragmentInteractionListener extends
        LogSuperDataEntry.onLogDataEntryInteractionListener {
        // For general interaction - really just the return to the Activity
        void onEditHiveSingleFragmentInteraction(long hiveID, boolean newHive, boolean deleteHive);
    }
}
