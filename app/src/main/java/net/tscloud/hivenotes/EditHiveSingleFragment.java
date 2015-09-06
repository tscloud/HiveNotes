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

import net.tscloud.hivenotes.db.Hive;
import net.tscloud.hivenotes.db.HiveDAO;


/**
 * A simple subclass.
 * Activities that contain this fragment must implement the
 * OnEditHiveSingleFragmentInteractionListener interface
 * to handle interaction events.
 * Use the EditHiveSingleFragment#newInstance factory method to
 * create an instance of this fragment.
 */
public class EditHiveSingleFragment extends Fragment {

    public static final String TAG = "EditHiveSingleFragment";

    // the fragment initialization parameters
    private static final String APIARY_KEY = "apiaryKey";
    private static final String HIVE_KEY = "hiveKey";
    // and instance var of same - needed?
    private long mApiaryKey;
    private long mHiveKey;
    private  Hive mHive;

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
            if (mListener != null) {
                // we need to get the Hive
                mHive = getHive(mHiveKey);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_single_hive, container, false);

        final Button b1 = (Button)v.findViewById(R.id.hiveNoteButtton);

        if (mHive != null) {
            b1.setText(getResources().getString(R.string.save_hive_string));

            // fill the form
            EditText nameEdit = (EditText)v.findViewById(R.id.editTextHiveName);
            EditText speciesEdit = (EditText)v.findViewById(R.id.editTextHiveSpecies);
            EditText foundationTypeEdit = (EditText)v.findViewById(R.id.editTextHiveFoundationType);

            nameEdit.setText(mHive.getName());
            speciesEdit.setText(mHive.getSpecies());
            foundationTypeEdit.setText(mHive.getFoundationType());
        }
        else {
            b1.setText(getResources().getString(R.string.create_hive_string));
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

    public void onButtonPressed() {
        // get name and email and put to DB
        Log.d(TAG, "about to persist hive");

        boolean lNewHive = false;

        EditText nameEdit = (EditText)getView().findViewById(R.id.editTextHiveName);
        EditText speciesEdit = (EditText)getView().findViewById(R.id.editTextHiveSpecies);
        EditText foundationTypeEdit = (EditText)getView().findViewById(R.id.editTextHiveFoundationType);
        String nameText = nameEdit.getText().toString();
        String speciesText = speciesEdit.getText().toString();
        String foundationTypeText = foundationTypeEdit.getText().toString();

        // neither EditText can be empty
        boolean emptyText = false;

        if (nameText.length() == 0){
            nameEdit.setError("Name cannot be empty");
            emptyText = true;
            Log.d(TAG, "Uh oh...Name empty");
        }

        if (speciesText.length() == 0){
            speciesEdit.setError("Species cannot be empty");
            emptyText = true;
            Log.d(TAG, "Uh oh...Species empty");
        }

        if (!emptyText) {
            HiveDAO hiveDAO = new HiveDAO(getActivity());
            Hive hive;
            if (mHiveKey == -1) {
                hive = hiveDAO.createHive(mApiaryKey, nameText, speciesText, foundationTypeText);
                lNewHive = true;
            }
            else {
                hive = hiveDAO.updateHive(mHiveKey, mApiaryKey, nameText, speciesText, foundationTypeText);
            }
            hiveDAO.close();

            if (hive != null) {
                Log.d(TAG, "Hive Name: " + hive.getName() + " persisted");
                Log.d(TAG, "Hive Species: " + hive.getSpecies() + " persisted");
                Log.d(TAG, "Hive Foundation Type: " + hive.getFoundationType() + " persisted");
            }
            else  {
                Log.d(TAG, "BAD...Hive update failed");
            }

            if (mListener != null) {
                mListener.onEditHiveSingleFragmentInteraction(hive.getId(), lNewHive);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnEditHiveSingleFragmentInteractionListener)activity;
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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnEditHiveSingleFragmentInteractionListener {
        public void onEditHiveSingleFragmentInteraction(long hiveID, boolean newHive);
    }

}
