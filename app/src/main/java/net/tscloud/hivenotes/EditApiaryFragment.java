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

import net.tscloud.hivenotes.db.Apiary;
import net.tscloud.hivenotes.db.ApiaryDAO;
import net.tscloud.hivenotes.db.Profile;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnEditApiaryFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditApiaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditApiaryFragment extends Fragment {

    public static final String TAG = "EditApiaryFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private long mParam2;

    private OnEditApiaryFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param profile Parameter 2.
     * @return A new instance of fragment EditApiaryFragment.
     */
    public static EditApiaryFragment newInstance(String param1, Profile profile) {
        // Profile object passed in at newInstance create time
        // but only putting the profileID in the Bundle

        EditApiaryFragment fragment = new EditApiaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putLong(ARG_PARAM2, profile.getId());

        fragment.setArguments(args);

        return fragment;
    }

    public EditApiaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getLong(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_apiary, container, false);

        // set button listener and text
        final Button b1 = (Button)v.findViewById(R.id.hiveNoteButtton);
        b1.setText(getResources().getString(R.string.create_apiary_string));
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(mParam2);
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(long profileID) {
        // get name and email and put to DB
        Log.d(TAG, "about to persist apiary");

        EditText nameEdit = (EditText)getView().findViewById(R.id.editTextApiaryName);
        EditText postalCodeEdit = (EditText)getView().findViewById(R.id.editTextApiaryPostalCode);
        String nameText = nameEdit.getText().toString();
        String postalCodeText = postalCodeEdit.getText().toString();

        // neither EditText can be empty
        boolean emptyText = false;

        if (nameText.length() == 0){
            nameEdit.setError("Name cannot be empty");
            emptyText = true;
            Log.d(TAG, "Uh oh...Name empty");
        }

        if (postalCodeText.length() == 0){
            postalCodeEdit.setError("Postal Code cannot be empty");
            emptyText = true;
            Log.d(TAG, "Uh oh...Name empty");
        }

        if (!emptyText) {
            ApiaryDAO apiaryDAO = new ApiaryDAO(getActivity());
            Apiary apiary = apiaryDAO.createApiary(profileID, nameText, postalCodeText);
            apiaryDAO.close();

            Log.d(TAG, "Apiary Name: " + apiary.getName() + " persisted");
            Log.d(TAG, "Apiary Postal Code: " + apiary.getPostalCode() + " persisted");

            if (mListener != null) {
                mListener.onEditApiaryFragmentInteraction();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnEditApiaryFragmentInteractionListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEditApiaryFragmentInteractionListener");
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
    public interface OnEditApiaryFragmentInteractionListener {
        public void onEditApiaryFragmentInteraction();
    }

}
