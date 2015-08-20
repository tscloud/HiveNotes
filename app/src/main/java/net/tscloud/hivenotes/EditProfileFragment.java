package net.tscloud.hivenotes;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import net.tscloud.hivenotes.db.Profile;
import net.tscloud.hivenotes.db.ProfileDAO;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnNewProfileFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {

    public static final String TAG = "EditProfileFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // original button color
    private ColorDrawable drawable;

    private OnNewProfileFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // set button listener
        final Button b1 = (Button)v.findViewById(R.id.newProfileButtton);
        b1.setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View v) {
                onButtonPressed(Uri.parse("here I am...from New Profile"), b1);
            }
        });
        /*
        b1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    // change button background but save original color 1st
                    drawable = (ColorDrawable)b1.getBackground();
                    b1.setBackgroundDrawable(new ColorDrawable(Color.GREEN));

                    // call handler
                    onButtonPressed(Uri.parse("here I am...from New Profile"), b1);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    b1.setBackgroundDrawable(drawable);
                }
                return true;
            }
        });
        */
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri, Button b) {
        // get name and email and put to DB
        Log.d(TAG, "about to persist profile");

        EditText nameEdit = (EditText)getView().findViewById(R.id.editTextName);
        EditText emailEdit = (EditText)getView().findViewById(R.id.editTextEmail);
        String nameText = nameEdit.getText().toString();
        String emailText = emailEdit.getText().toString();

        // neither EditText can be empty
        boolean emptyText = false;

        if (nameText.length() == 0){
            nameEdit.setError("Name cannot be empty");
            emptyText = true;
            Log.d(TAG, "Uh oh...Name empty");
        }

        if (emailText.length() == 0){
            emailEdit.setError("Email cannot be empty");
            emptyText = true;
            Log.d(TAG, "Uh oh...Name empty");
        }

        if (!emptyText) {
            ProfileDAO profileDAO = new ProfileDAO(getActivity());
            Profile profile = profileDAO.createProfile(nameText, emailText);
            profileDAO.close();

            Log.d(TAG, "Profile Name: " + profile.getName() + " persisted");
            Log.d(TAG, "Profile Email: " + profile.getEmail() + " persisted");

            if (mListener != null) {
                mListener.onNewProfileFragmentInteraction(uri);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnNewProfileFragmentInteractionListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNewProfileFragmentInteractionListener");
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
    public interface OnNewProfileFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onNewProfileFragmentInteraction(Uri uri);
    }

}
