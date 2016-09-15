package net.tscloud.hivenotes;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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
import net.tscloud.hivenotes.helper.TimeoutableLocationListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;


/**
 * A simple Fragment subclass.
 * Activities that contain this fragment must implement the
 * OnEditApiaryFragmentInteractionListener interface
 * to handle interaction events.
 * Use the EditApiaryFragment#newInstance factory method to
 * create an instance of this fragment.
 */
public class EditApiaryFragment extends Fragment implements
        TimeoutableLocationListener.LocationTimeoutListener {

    public static final String TAG = "EditApiaryFragment";

    // the fragment initialization parameters
    private static final String PROFILE_ID = "param1";
    private static final String APIARY_ID = "param2";
    // and instance var of same - needed?
    private long mProfileID = -1;
    private long mApiaryID = -1;
    private Apiary mApiary;

    private OnEditApiaryFragmentInteractionListener mListener;

    // Used to get location
    LocationManager mLocationManager;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param profileID
     * @param apiaryID
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
        final TextView t1 = (TextView)v.findViewById(R.id.textNewApiary);
        final Button b1 = (Button)v.findViewById(R.id.hiveNoteButtton);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateApiaryButtonPressed(mProfileID);
            }
        });

        final Button bComputeLatLon = (Button)v.findViewById(R.id.buttonComputeLatLon);

        bComputeLatLon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onComputeLatLonButtonPressed(mProfileID);
            }
        });

        if (mApiary != null) {
            // fill the form
            final EditText nameEdit = (EditText)v.findViewById(R.id.editTextApiaryName);
            final EditText postalCodeEdit = (EditText)v.findViewById(R.id.editTextApiaryPostalCode);
            final EditText latitudeEdit = (EditText)v.findViewById(R.id.editTextApiaryLatitude);
            final EditText longitudeEdit = (EditText)v.findViewById(R.id.editTextApiaryLongitude);

            nameEdit.setText(mApiary.getName());
            postalCodeEdit.setText(mApiary.getPostalCode());
            latitudeEdit.setText(Float.toString(mApiary.getLatitude()));
            longitudeEdit.setText(Float.toString(mApiary.getLongitude()));

            b1.setText(getResources().getString(R.string.save_apiary_button_text));
        }
        else {
            b1.setText(getResources().getString(R.string.create_apiary_string));

        }

        return v;
    }

    public void onCreateApiaryButtonPressed(long profileID) {
        // get name and email and put to DB
        Log.d(TAG, "about to persist apiary");

        boolean lNewApiary = false;

        final EditText nameEdit = (EditText)getView().findViewById(R.id.editTextApiaryName);
        final EditText postalCodeEdit = (EditText)getView().findViewById(R.id.editTextApiaryPostalCode);
        final EditText latitudeEdit = (EditText)getView().findViewById(R.id.editTextApiaryLatitude);
        final EditText longitudeEdit = (EditText)getView().findViewById(R.id.editTextApiaryLongitude);

        String nameText = nameEdit.getText().toString();
        String postalCodeText = postalCodeEdit.getText().toString();

        String latitudeString = latitudeEdit.getText().toString();
        float latitudeFloat = 0;
        if ((latitudeString != null) && (latitudeString.length() != 0)) {
            latitudeFloat = Float.parseFloat(latitudeString);
        }

        String longitudeString = longitudeEdit.getText().toString();
        float longitudeFloat = 0;
        if ((longitudeString != null) && (longitudeString.length() != 0)) {
            longitudeFloat = Float.parseFloat(longitudeString);
        }

        boolean emptyText = false;

        // Name EditText can be empty
        if (nameText.length() == 0) {
            nameEdit.setError("Name cannot be empty");
            emptyText = true;
            Log.d(TAG, "Uh oh...Name empty");
        }

        // Lat/Lon - if we have 1 => we need the other
        if ((latitudeString.length() != 0) && (longitudeString.length() == 0)) {
            longitudeEdit.setError("Need lat AND lon");
            emptyText = true;
            Log.d(TAG, "Uh oh...Longitude empty");
        }

        if ((longitudeString.length() != 0) && (latitudeString.length() == 0)) {
            latitudeEdit.setError("Need lat AND lon");
            emptyText = true;
            Log.d(TAG, "Uh oh...Latitude empty");
        }

        if (!emptyText) {

            /** Before we write the Apiary, we need zip/latlon checks
             *   if lat/lon => geocode to get zip potentially overwriting an
             *    existing zip <- these need to match at weather data will be
             *    got be lat/lon (it may be more accurate) but pollen data can
             *    only be got be zip
             *   if zip & no lat/lon => geocode to get lat/lon <- technically not
             *    necessary but we can consistently retrieve weather by lat/lon
             */

            if ((latitudeFloat != 0 ) && (longitudeFloat != 0)) {
                // use lat/lon to get postal code
                final Geocoder geocoder = new Geocoder(getActivity());
                try {
                    List<Address> addresses = geocoder.getFromLocation((double)latitudeFloat,
                            (double)longitudeFloat, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        // Use the address as needed
                        postalCodeText = address.getPostalCode();
                    } else {
                        // Display appropriate message when Geocoder services are not available
                        Log.d(TAG, "no find postal code");
                    }
                } catch (IOException e) {
                    // handle exception
                    Log.d(TAG, "IOException getting postal code from lat/lon: " + e.getMessage());
                }
            }
            else if (postalCodeText.length() != 0) {
                // use postal code to get lat/lon
                final Geocoder geocoder = new Geocoder(getActivity());
                try {
                    List<Address> addresses = geocoder.getFromLocationName(postalCodeText, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        // Use the address as needed
                        latitudeFloat = (float)address.getLatitude();
                        longitudeFloat = (float)address.getLongitude();
                    } else {
                        // Display appropriate message when Geocoder services are not available
                        Log.d(TAG, "no find lat/lon");
                    }
                } catch (IOException e) {
                    // handle exception
                    Log.d(TAG, "IOException getting lat/lon from postal code: " + e.getMessage());
                }
            }

            ApiaryDAO apiaryDAO = new ApiaryDAO(getActivity());
            Apiary apiary;
            if (mApiaryID == -1) {
                apiary = apiaryDAO.createApiary(profileID, nameText, postalCodeText, latitudeFloat,
                        longitudeFloat);
                lNewApiary = true;
            }
            else {
                apiary = apiaryDAO.updateApiary(mApiaryID, profileID, nameText, postalCodeText,
                        latitudeFloat, longitudeFloat);
            }
            apiaryDAO.close();

            if (apiary != null) {
                Log.d(TAG, "Apiary Name: " + apiary.getName() + " persisted");
                Log.d(TAG, "Apiary Postal Code: " + apiary.getPostalCode() + " persisted");
            }
            else {
                Log.d(TAG, "BAD...Apiary update failed");
            }

            if (mListener != null) {
                mListener.onEditApiaryFragmentInteraction(apiary);
            }
        }
    }

    public void onComputeLatLonButtonPressed(long profileID) {
        Log.d(TAG, "trying to get lat/lon");
        try {
            mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            // Check LastKnownLocation - GPS
            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // make sure location is at least somewhat "fresh"
            if (location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
                loadScreenLatLon((float)location.getLatitude(), (float)location.getLongitude(),
                    "From GPS: ");
            }
            else {
                // Check LastKnownLocation - Network
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                // make sure location is at least somewhat "fresh"
                if (location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
                    loadScreenLatLon((float)location.getLatitude(), (float)location.getLongitude(),
                            "From Network: ");
                }
                else {
                    // Crank up GPS and/or Network
                    TimeoutableLocationListener tLocListner =
                            new TimeoutableLocationListener(mLocationManager, 1000*5, this);
                    tLocListner.execute(getActivity());
                    //loadScreenLatLon((float)tLocListner.mLocation.getLatitude(), (float)tLocListner.mLocation.getLongitude(),
                    //        tLocListner.mLocation.getProvider());
                }
            }
        }
        catch (SecurityException e) {
            Log.d(TAG, "Permission not given for location services", e);
        }
    }

    // utility method for loading lat/lon screen fields
    private void loadScreenLatLon(float aLat, float aLon, String aCaller) {
        Log.d(TAG, String.format(aCaller + ": Lat: %f, Lon: %f", aLat, aLon));
        final EditText latitudeEdit = (EditText)getView().findViewById(R.id.editTextApiaryLatitude);
        final EditText longitudeEdit = (EditText)getView().findViewById(R.id.editTextApiaryLongitude);
        latitudeEdit.setText(Float.toString(aLat));
        longitudeEdit.setText(Float.toString(aLon));
    }

    @Override
    public void onLocationTimedout(Location aLocation) {
        Log.d(TAG, "onLocationTimedout() called");
        if (aLocation != null) {
            loadScreenLatLon((float)aLocation.getLatitude(), (float)aLocation.getLongitude(),
                    aLocation.getProvider());
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnEditApiaryFragmentInteractionListener {
        // For general interaction - really just the return to the Activity
        void onEditApiaryFragmentInteraction(Apiary aApiary);

        // For getting Apiary data
        //List<Apiary> deliverApiaryList(long aProfileID);
    }

}
