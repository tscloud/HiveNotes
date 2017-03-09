package net.tscloud.hivenotes.helper;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.tscloud.hivenotes.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by tscloud on 3/8/17.
 */

public class LogEditTextDataEntryLocation extends LogSuperDataEntry implements
        TimeoutableLocationListener.LocationTimeoutListener {

    public static final String TAG = "DataEntryLocation";

    // Needed for onBackPressed() - seperate method that may get called from the Activity
    private EditText etPostalCode;
    private EditText etLat;
    private EditText etLon;

    // Used to get location
    LocationManager mLocationManager;

    public static LogEditTextDataEntryLocation newInstance(LogEditTextDialogData aData) {
        LogEditTextDataEntryLocation frag = new LogEditTextDataEntryLocation();
        Bundle args = new Bundle();
        args.putString("title", aData.getTitle());
        args.putString("tag", aData.getTag());
        args.putString("data", aData.getData());
        args.putBoolean("isOtherNum", aData.isOtherNum());
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // get the Dialog Layout
        final View view = inflater.inflate(R.layout.scb_location, null);

        // title
        final TextView title = (TextView)view.findViewById(R.id.texttitle);
        title.setText(getArguments().getString("title"));

        // Needed for onBackPressed() - separate method that may get called from the Activity
        etPostalCode = (EditText)view.findViewById(R.id.editTextPostalCode);
        etLat = (EditText)view.findViewById(R.id.editTextLatitude);
        etLon = (EditText)view.findViewById(R.id.editTextLongitude);

        // if we only want numeric data => make sure that's what we get
        if (getArguments().getBoolean("isOtherNum")) {
            etLat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            etLon.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }

        // data being passed in as CSV string (not the best but hey)
        String dataString = getArguments().getString("data");
        ArrayList<String> dataList = new ArrayList<String>(Arrays.asList(dataString.split(",")));

        etPostalCode.setText(dataList.get(0));
        etLat.setText(dataList.get(1));
        etLon.setText(dataList.get(2));

        // Stuff to set postal code/lat/lon
        final Button locButton = (Button)view.findViewById(R.id.buttonComputeLatLon);

        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onComputeLatLonButtonPressed();
            }
        });

        return view;
    }

    @Override
    public boolean onBackPressed() {
        Log.d(TAG, "Back button clicked...save everything");
        boolean reply = false;

        if (mListener != null) {
            String[] result = new String[3];
            result[0] = etPostalCode.getText().toString();
            result[1] = etLat.getText().toString();
            result[2] = etLon.getText().toString();
            mListener.onLogDataEntryOK(result, 0,
                    getArguments().getString("tag"));
            reply = true;
        }

        return reply;
    }

    private void onComputeLatLonButtonPressed() {
        Log.d(TAG, "trying to get lat/lon");
        try {
            mLocationManager = (LocationManager)getActivity().getSystemService(
                    Context.LOCATION_SERVICE);

            // Check LastKnownLocation - GPS
            Location location = mLocationManager.getLastKnownLocation(
                    LocationManager.GPS_PROVIDER);
            // make sure location is at least somewhat "fresh"
            if (location != null && location.getTime() > Calendar.getInstance().
                    getTimeInMillis() - 2 * 60 * 1000) {
                loadScreenLatLon((float)location.getLatitude(), (float)location.getLongitude(),
                    "From GPS: ");
            }
            else {
                // Check LastKnownLocation - Network
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                // make sure location is at least somewhat "fresh"
                if (location != null && location.getTime() > Calendar.getInstance().
                        getTimeInMillis() - 2 * 60 * 1000) {
                    loadScreenLatLon((float)location.getLatitude(),
                            (float)location.getLongitude(), "From Network: ");
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
            Log.d(TAG, "!!!Permission not given for location services!!!", e);
        }
    }

    // utility method for loading lat/lon screen fields
    private void loadScreenLatLon(float aLat, float aLon, String aCaller) {
        Log.d(TAG, String.format(aCaller + ": Lat: %f, Lon: %f", aLat, aLon));
        etLat.setText(Float.toString(aLat));
        etLon.setText(Float.toString(aLon));
    }

    @Override
    public void onLocationTimedout(Location aLocation) {
        Log.d(TAG, "onLocationTimedout() called");
        if (aLocation != null) {
            loadScreenLatLon((float)aLocation.getLatitude(), (float)aLocation.getLongitude(),
                    aLocation.getProvider());
        }
    }
}