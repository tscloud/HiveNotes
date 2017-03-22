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

import net.tscloud.hivenotes.db.Profile;
import net.tscloud.hivenotes.db.ProfileDAO;
import net.tscloud.hivenotes.helper.HiveDeleteDialog;
import net.tscloud.hivenotes.helper.LogEditTextDialogData;
import net.tscloud.hivenotes.helper.LogSuperDataEntry;


/**
 * A simple Fragment subclass.
 * Activities that contain this fragment must implement the
 * OnEditProfileFragmentInteractionListener interface
 * to handle interaction events.
 * Use the EditProfileFragment#newInstance factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends HiveDataEntryFragment {

    public static final String TAG = "EditProfileFragment";

    // constants used for Dialogs
    public static final String DIALOG_TAG_NAME = "name";
    public static final String DIALOG_TAG_EMAIL = "email";

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

        // Delete button and title stuff
        final TextView textNew = (TextView)v.findViewById(R.id.textNewProfile);
        final View viewDelete = v.findViewById(R.id.deleteProfileButton);
        final Button btnDelete = (Button)viewDelete.findViewById(R.id.hiveNoteButtton);
        btnDelete.setText(getResources().getString(R.string.delete_profile_string));

        // get reference to the <include>s
        final View dialogProfileName = v.findViewById(R.id.buttonProfileName);
        final View dialogProfileEmail = v.findViewById(R.id.buttonProfileEmail);

        // set text of <include>s
        final TextView nameText =
                (TextView)dialogProfileName.findViewById(R.id.dialogLaunchTextView);
        nameText.setText(R.string.new_profile_name_text);

        final TextView emailText =
                (TextView)dialogProfileEmail.findViewById(R.id.dialogLaunchTextView);
        emailText.setText(R.string.new_profile_email_text);

        if (mProfile != null) {
            Log.d(TAG, "successfully retrieved Profile data");
        }
        else {
            textNew.setText(getResources().getString(R.string.new_profile_string));
            //get rid of Delete button
            btnDelete.setEnabled(false);
            btnDelete.setTextColor(Color.GRAY);
        }

        // set delete button listener
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HiveProfileDeleteDialog().doDeleteDialog();
            }
        });

        // set dialog button listeners
        dialogProfileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    if (mProfile != null) {
                        checked = mProfile.getName();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogEditTextDialogData(
                            getResources().getString(R.string.new_profile_name_text),
                            null,
                            DIALOG_TAG_NAME,
                            checked,
                            false));
                }
                else {
                    Log.d(TAG, "no Listener");
                }
            }
        });

        dialogProfileEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    if (mProfile != null) {
                        checked = mProfile.getEmail();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogEditTextDialogData(
                            getResources().getString(R.string.new_profile_email_text),
                            null,
                            DIALOG_TAG_EMAIL,
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

    public boolean onFragmentSave() {
        // get name and email and put to DB
        Log.d(TAG, "about to persist profile");

        boolean reply = false;

        if (mProfile == null) {
            mProfile = new Profile();
        }

        ProfileDAO profileDAO = new ProfileDAO(getActivity());
        Profile profile;

        if (mProfileID == -1) {
            profile = profileDAO.createProfile(mProfile);
        }
        else {
            mProfile.setId(mProfileID);
            profile = profileDAO.updateProfile(mProfile);
        }
        profileDAO.close();

        if (profile != null) {
            // Reset Hive instance vars
            mProfile = profile;
            mProfileID = profile.getId();

            Log.d(TAG, "Profile Name: " + profile.getName() + " persisted");
            Log.d(TAG, "Profile Email: " + profile.getEmail() + " persisted");

            if (mListener != null) {
                mListener.onEditProfileFragmentInteraction(profile);
                reply = true;
            }
        }

        return reply;
    }

    private void onDeleteButtonPressed() {
        // delete Profile from DB
        Log.d(TAG, "about to delete Profile");
        ProfileDAO profileDAO = new ProfileDAO(getActivity());
        Profile profile = new Profile();
        profile.setId(mProfileID);
        profileDAO.deleteProfile(profile);
        profileDAO.close();

        if (mListener != null) {
            mListener.onEditProfileFragmentInteraction(profile);
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

    private class HiveProfileDeleteDialog extends HiveDeleteDialog {

        HiveProfileDeleteDialog() {
            super(getActivity(), "Are you sure you want to delete this Profile?");
        }

        protected void doDelete(){
            onDeleteButtonPressed();
        }
    }

    @Override
    public void setDialogData(String[] aResults, long aResultRemTime, String aTag) {
        //may have to create the DO here - if we're a new entry and Dialog work was done before
        // anything else
        if (mProfile == null) {
            mProfile = new Profile();
        }

        switch (aTag){
            case DIALOG_TAG_NAME:
               mProfile.setName(aResults[0]);
               Log.d(TAG, "onLogLaunchDialog: setName: " +
                       mProfile.getName());
               break;
            case DIALOG_TAG_EMAIL:
               mProfile.setEmail(aResults[0]);
               Log.d(TAG, "onLogLaunchDialog: setEmail: " +
                       mProfile.getEmail());
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
    interface OnEditProfileFragmentInteractionListener extends
            LogSuperDataEntry.onLogDataEntryInteractionListener{
        // For general interaction - really just the return to the Activity
        void onEditProfileFragmentInteraction(Profile profile);

        // For getting Profile data
        Profile deliverProfile();
    }

}
