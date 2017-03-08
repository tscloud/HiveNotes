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
import android.widget.TextView;

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

    // the fragment initialization parameters
    private static final String PROFILE_ID = "param1";
    // and instance var of same - needed?
    long mProfileID = -1;
    Profile mProfile;

    // original button color
    //private ColorDrawable drawable;

    private OnEditProfileFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param profileID
     */
    public static EditProfileFragment newInstance(long profileID) {
        Log.d(TAG, "getting newInstance of EditProfileFragment...profileID: " + profileID);

        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();

        args.putLong(PROFILE_ID, profileID);
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
            mProfileID = getArguments().getLong(PROFILE_ID);
        }

        if (mProfileID != -1) {
            if (mListener != null) {
                // we need to get the Profile
                mProfile = mListener.deliverProfile();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        final Button b1 = (Button) v.findViewById(R.id.hiveNoteButtton);
        final TextView t1 = (TextView) v.findViewById(R.id.textNewProfile);

        // set button listener
        b1.setOnClickListener(new View.OnClickListener() {
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

        if (mProfile != null) {
            // fill the form
            EditText nameEdit = (EditText)v.findViewById(R.id.editTextName);
            EditText emailEdit = (EditText)v.findViewById(R.id.editTextEmail);

            nameEdit.setText(mProfile.getName());
            emailEdit.setText(mProfile.getEmail());

            b1.setText(getResources().getString(R.string.save_profile_string));
            t1.setText(getResources().getString(R.string.update_profile_string));
        }
        else {
            b1.setText(getResources().getString(R.string.create_profile_string));

        }

        return v;
    }

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
        // For general interaction - really just the return to the Activity
        void onEditProfileFragmentInteraction(Profile profile);

        // For getting Profile data
        Profile deliverProfile();
    }

}
