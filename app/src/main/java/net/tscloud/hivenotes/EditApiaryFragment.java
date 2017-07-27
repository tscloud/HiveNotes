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

import net.tscloud.hivenotes.db.Apiary;
import net.tscloud.hivenotes.db.ApiaryDAO;
import net.tscloud.hivenotes.helper.HiveDeleteDialog;
import net.tscloud.hivenotes.helper.LogEditTextDialogData;
import net.tscloud.hivenotes.helper.LogEditTextDialogLocationData;
import net.tscloud.hivenotes.helper.LogSuperDataEntry;


/**
 * A simple Fragment subclass.
 * Activities that contain this fragment must implement the
 * OnEditApiaryFragmentInteractionListener interface
 * to handle interaction events.
 * Use the EditApiaryFragment#newInstance factory method to
 * create an instance of this fragment.
 */
public class EditApiaryFragment extends HiveDataEntryFragment {

    public static final String TAG = "EditApiaryFragment";

    // constants used for Dialogs
    public static final String DIALOG_TAG_NAME = "name";
    public static final String DIALOG_TAG_LOCATION = "location";

    // the fragment initialization parameters
    private static final String PROFILE_ID = "param1";
    private static final String APIARY_ID = "param2";
    // and instance var of same - needed?
    private long mProfileID = -1;
    private long mApiaryID = -1;
    private Apiary mApiary;

    private OnEditApiaryFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static EditApiaryFragment newInstance(long profileID, long apiaryID) {
        Log.d(TAG, "getting newInstance of EditApiaryFragment...profileID: " + profileID);

        EditApiaryFragment fragment = new EditApiaryFragment();
        Bundle args = new Bundle();

        args.putLong(PROFILE_ID, profileID);
        args.putLong(APIARY_ID, apiaryID);
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
            mProfileID = getArguments().getLong(PROFILE_ID);
            mApiaryID = getArguments().getLong(APIARY_ID);
        }

        if (mApiaryID != -1) {
            if (mListener != null) {
                // we need to get the Apiary
                mApiary = getApiary(mApiaryID);
                mProfileID = mApiary.getProfile();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_apiary, container, false);

        // set button listener and text
        final TextView textNew = (TextView)v.findViewById(R.id.textNewApiary);
        final View viewDelete = v.findViewById(R.id.deleteApiaryButton);
        final Button btnDelete = (Button)viewDelete.findViewById(R.id.hiveNoteButtton);

        btnDelete.setText(getResources().getString(R.string.delete_apiary_string));

        // get reference to the <include>s
        final View dialogApiaryName = v.findViewById(R.id.buttonApiaryName);
        final View dialogApiaryLocation = v.findViewById(R.id.buttonApiaryLocation);

        // set text of <include>s
        final TextView nameText =
                (TextView)dialogApiaryName.findViewById(R.id.dialogLaunchTextView);
        nameText.setText(R.string.new_apiary_name_string);

        final TextView locationText =
                (TextView)dialogApiaryLocation.findViewById(R.id.dialogLaunchTextView);
        locationText.setText(R.string.apiary_location);

        if (mApiary != null) {
            Log.d(TAG, "successfully retrieved Apiary data");
            textNew.setText(getResources().getString(R.string.update_apiary_string));
        }
        else {
            //get rid of Delete button
            btnDelete.setEnabled(false);
            btnDelete.setTextColor(Color.GRAY);
        }

        // set delete button listener
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HiveApiaryDeleteDialog().doDeleteDialog();
            }
        });

        // set dialog button listeners
        dialogApiaryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String checked = "";
                    if (mApiary != null) {
                        checked = mApiary.getName();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogEditTextDialogData(
                            getResources().getString(R.string.new_apiary_name_string),
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

        dialogApiaryLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Callback to Activity to launch a Dialog
                if (mListener != null) {
                    String aPostalCode = "";
                    float aLat = 0;
                    float aLon = 0;
                    if (mApiary != null) {
                        aPostalCode = mApiary.getPostalCode();
                        aLat = mApiary.getLatitude();
                        aLon = mApiary.getLongitude();
                    }
                    /* Get the Activity to launch the Dialog for us
                     */
                    mListener.onLogLaunchDialog(new LogEditTextDialogLocationData(
                            getResources().getString(R.string.apiary_location),
                            DIALOG_TAG_LOCATION,
                            aPostalCode,
                            aLat,
                            aLon));
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
        Log.d(TAG, "about to persist apiary");

        boolean reply = false;

        if (mApiary == null) {
            mApiary = new Apiary();
        }

        ApiaryDAO apiaryDAO = new ApiaryDAO(getActivity());
        Apiary apiary;

        mApiary.setProfile(mProfileID);

        if (mApiaryID == -1) {
            apiary = apiaryDAO.createApiary(mApiary);
        }
        else {
            mApiary.setId(mApiaryID);
            apiary = apiaryDAO.updateApiary(mApiary);
        }
        apiaryDAO.close();

        if (apiary != null) {
            // Reset Hive instance vars
            mApiary = apiary;
            mApiaryID = apiary.getId();

            Log.d(TAG, "Apiary Name: " + apiary.getName() + " persisted");
            Log.d(TAG, "Apiary Postal Code: " + apiary.getPostalCode() + " persisted");
        }
        else {
            Log.d(TAG, "BAD...Apiary update failed");
        }

        if (mListener != null) {
            mListener.onEditApiaryFragmentInteraction(apiary);
            reply = true;
        }

        return reply;
    }

    private void onDeleteButtonPressed() {
        // delete hive from DB
        Log.d(TAG, "about to delete apiary");
        ApiaryDAO apiaryDAO = new ApiaryDAO(getActivity());
        Apiary apiary = new Apiary();
        apiary.setId(mApiaryID);
        apiaryDAO.deleteApiary(apiary);
        apiaryDAO.close();

        if (mListener != null) {
            // return w/ null => this indicates a delete as apposed to an update
            mListener.onEditApiaryFragmentInteraction(null);
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

    // Utility method to get Profile
    private Apiary getApiary(long aApiaryID) {
        // read Profile
        Log.d(TAG, "reading Apiary table");
        ApiaryDAO apiaryDAO = new ApiaryDAO(getActivity());
        Apiary reply = apiaryDAO.getApiaryById(aApiaryID);
        apiaryDAO.close();

        return reply;
    }

    private class HiveApiaryDeleteDialog extends HiveDeleteDialog {

        HiveApiaryDeleteDialog() {
            super(getActivity(), "Are you sure you want to delete this Apiary?");
        }

        protected void doDelete(){
            onDeleteButtonPressed();
        }
    }

    @Override
    public void setDialogData(String[] aResults, long aResultRemTime, String aResultRemDesc, String aTag) {
        //may have to create the DO here - if we're a new entry and Dialog work was done before
        // anything else
        if (mApiary == null) {
            mApiary = new Apiary();
        }

        switch (aTag){
            case DIALOG_TAG_NAME:
                mApiary.setName(aResults[0]);
                Log.d(TAG, "onLogLaunchDialog: setName: " +
                        mApiary.getName());
                break;
            case DIALOG_TAG_LOCATION:
                /* Before we write the Apiary, we need zip/latlon checks */
                String[] locData = zipLatLon(aResults[0], aResults[1], aResults[2]);

                mApiary.setPostalCode(locData[0]);
                mApiary.setLatitude(Float.valueOf(locData[1]));
                mApiary.setLongitude(Float.valueOf(locData[2]));

                Log.d(TAG, "onLogLaunchDialog: setPostalCode: " +
                        mApiary.getPostalCode());
                break;
            default:
                Log.d(TAG, "onLogLaunchDialog: unrecognized Dialog type");
        }
    }

    /** Before we write the Apiary, we need zip/latlon checks
     *   if lat/lon => geocode to get zip potentially overwriting an
     *    existing zip <- these need to match as weather data will be
     *    got by lat/lon (it may be more accurate) but pollen data can
     *    only be got by zip
     *   if zip & no lat/lon => geocode to get lat/lon <- technically not
     *    necessary but we can consistently retrieve weather by lat/lon
     */
    private String[] zipLatLon(String aZip, String aLat, String aLon) {
        // good thing to init all reply entries to zero?
        String[] reply = {"0", "0", "0"};

        double latDoub = 0;
        double lonDoub = 0;

        // should the UI ensure that lat/lon entries passed in always numeric?
        if ((aLat != null) && (aLat.length() != 0)){
            latDoub = Double parseDouble(aLat);
        }

        if ((aLon != null) && (aLon.length() != 0)){
            lonDoub = Double parseDouble(aLon);
        }

        if ((latDoub != 0 ) && (lonDoub != 0)) {
            // use lat/lon to get postal code
            final Geocoder geocoder = new Geocoder(getActivity());
            try {
                List<Address> addresses = geocoder.getFromLocation(latDoub, lonDoub, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    // Use the address as needed
                    reply[0] = address.getPostalCode();
                    reply[1] = aLat;
                    reply[2] = aLon;
                } else {
                    // Display appropriate message when Geocoder services are not available
                    Log.d(TAG, "no find postal code");
                }
            } catch (IOException | IllegalStateException e) {
                // handle exception
                Log.d(TAG, "IOException getting postal code from lat/lon: " + e.getMessage());
            }
        }
        else if (aZip.length() != 0) {
            // use postal code to get lat/lon
            final Geocoder geocoder = new Geocoder(getActivity());
            try {
                List<Address> addresses = geocoder.getFromLocationName(aZip, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    // Use the address as needed
                    reply[0] = aZip;
                    reply[1] = Double.toString(getLatitude());
                    reply[2] = Double.toString(getLongitude());
                } else {
                    // Display appropriate message when Geocoder services are not available
                    Log.d(TAG, "no find lat/lon");
                }
            } catch (IOException | IllegalStateException e) {
                // handle exception
                Log.d(TAG, "IOException getting lat/lon from postal code: " + e.getMessage());
            }
        }

        return reply;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnEditApiaryFragmentInteractionListener extends
            LogSuperDataEntry.onLogDataEntryInteractionListener {
        // For general interaction - really just the return to the Activity
        void onEditApiaryFragmentInteraction(Apiary aApiary);
    }
}
