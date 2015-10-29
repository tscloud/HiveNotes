package net.tscloud.hivenotes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;


public class FetchCoordinates extends AsyncTask<String, Integer, String> implements LocationListener {
    ProgressDialog progDialog = null;

    public static final String TAG = "FetchCoordinates";

    private double lati = 0.0;
    private double longi = 0.0;
    String provider;

    private LocationManager locationManager;
    private Context user;

    public double getLati() {
        return lati;
    }

    public double getLongi() {
        return longi;
    }

    public String getProvider() {
        return provider;
    }

    public FetchCoordinates(LocationManager locationManager, Context user) {
        this.locationManager = locationManager;
        this.user = user;
    }

    @Override
    protected void onPreExecute() {
        progDialog = new ProgressDialog(user);
        progDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                FetchCoordinates.this.cancel(true);
            }
        });
        progDialog.setIndeterminate(true);
        progDialog.setCancelable(true);
        progDialog.show();
    }

    @Override
    protected void onCancelled(){
        Log.d(TAG, "Cancelled by user!");
        progDialog.dismiss();

        // be sure to removeUpdates() - onPostExecute() not invoked if onCancelled() is
        try {
            locationManager.removeUpdates(this);
        }
        catch (SecurityException e) {
            Log.d(TAG, "Permission not given for location services");
        }
    }

    @Override
    protected void onPostExecute(String result) {
        progDialog.dismiss();

        try {
            locationManager.removeUpdates(this);
        }
        catch (SecurityException e) {
            Log.d(TAG, "Permission not given for location services");
        }
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            // --GPS--
            progDialog.setMessage("Trying GPS Provider...");

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0,
                    this);

            Thread.sleep(1000 * 10);
            // be sure to removeUpdates()
            locationManager.removeUpdates(this);

            // --Network--
            progDialog.setMessage("Trying Network Provider...");

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0,
                    this);

            Thread.sleep(1000 * 10);
            // final removeUpdates() call in onPostExecute()
            //locationManager.removeUpdates(this);
        }
        catch (SecurityException e) {
            Log.d(TAG, "Permission not given for location services");
        }

        catch (InterruptedException e) {
            Log.d(TAG, "doInBackground() Thread.sleep interrupted...");
        }

        return null;
    }


    @Override
    public void onLocationChanged(Location location) {
        /* final removeUpdates() call in onPostExecute()
        try {
            locationManager.removeUpdates(this);
        }
        catch (SecurityException e) {
            Log.d(TAG, "Permission not given for location services");
        }
        */

        if (location != null) {
            provider = location.getProvider();
            lati = location.getLatitude();
            longi = location.getLongitude();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i("OnProviderDisabled", "OnProviderDisabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i("onProviderEnabled", "onProviderEnabled");
    }

    @Override
    public void onStatusChanged(String provider, int status,
                                Bundle extras) {
        Log.i("onStatusChanged", "onStatusChanged");
    }

}