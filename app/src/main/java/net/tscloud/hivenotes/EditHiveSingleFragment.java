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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnEditHiveSingleFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditHiveSingleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditHiveSingleFragment extends Fragment {

    public static final String TAG = "EditHiveSingleFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_APIARY_KEY = "apiaryKey";

    // TODO: Rename and change types of parameters
    private long theApiaryKey;

    private OnEditHiveSingleFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param apiaryKey Parameter 2.
     * @return A new instance of fragment EditHiveSingleFragment.
     */
    public static EditHiveSingleFragment newInstance(long apiaryKey) {
        // Profile object passed in at newInstance create time
        // but only putting the profileID in the Bundle

        EditHiveSingleFragment fragment = new EditHiveSingleFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM_APIARY_KEY, apiaryKey);

        fragment.setArguments(args);

        return fragment;
    }

    public EditHiveSingleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            theApiaryKey = getArguments().getLong(ARG_PARAM_APIARY_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_single_hive, container, false);

        // set button listener and text
        final Button b1 = (Button)v.findViewById(R.id.hiveNoteButtton);
        b1.setText(getResources().getString(R.string.create_hive_string));
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(theApiaryKey);
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(long apiaryID) {
        // get name and email and put to DB
        Log.d(TAG, "about to persist hive");

        EditText nameEdit = (EditText)getView().findViewById(R.id.editTextHiveName);
        EditText speciesEdit = (EditText)getView().findViewById(R.id.editTextHiveSpecies);
        EditText foundationTypeEdit = (EditText)getView().findViewById(R.id.editTextHiveFoundationType);
        String nameText = nameEdit.getText().toString();
        String speciesText = speciesEdit.getText().toString();
        String foundationTypeText = speciesEdit.getText().toString();

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
            Hive hive = hiveDAO.createHive(apiaryID, nameText, speciesText, foundationTypeText);
            hiveDAO.close();

            Log.d(TAG, "Hive Name: " + hive.getName() + " persisted");
            Log.d(TAG, "Hive Species: " + hive.getSpecies() + " persisted");
            Log.d(TAG, "Hive Foundation Type: " + hive.getFoundationType() + " persisted");

            if (mListener != null) {
                mListener.onEditHiveSingleFragmentInteraction(theApiaryKey);
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
    public interface OnEditHiveSingleFragmentInteractionListener {
        public void onEditHiveSingleFragmentInteraction(long apiaryId);
    }

}
