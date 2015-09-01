package net.tscloud.hivenotes;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
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
 * A simple Fragment subclass.
 * Activities that contain this fragment must implement the
 * OnEditProfileFragmentInteractionListener interface
 * to handle interaction events.
 * Use the EditProfileFragment#newInstance factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {

    public static final String TAG = "EditProfileFragment";

    // original button color
    //private ColorDrawable drawable;

    private OnEditProfileFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static EditProfileFragment newInstance() {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // set button listener
        final Button b1 = (Button)v.findViewById(R.id.hiveNoteButtton);
        b1.setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View v) {
                onButtonPressed(b1);
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
                    onButtonPressed(b1);

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
    public void onButtonPressed(Button b) {
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
                mListener.onEditProfileFragmentInteraction(profile);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnEditProfileFragmentInteractionListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEditProfileFragmentInteractionListener");
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
     */
    public interface OnEditProfileFragmentInteractionListener {
        public void onEditProfileFragmentInteraction(Profile profile);
    }

}
