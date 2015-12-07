package net.tscloud.hivenotes.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


public class FetchCoordinates extends AsyncTask<String, Integer, String> implements LocationListener {
    ProgressDialog progDialog = null;

    public static final String TAG = "FetchCoordinates";
    public static final long GPS_TIMOUT = 20000; // 20 sec
    public static final long NETWORK_TIMOUT = 10000; // 10 sec

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
        progDialog.setMessage("Trying GPS Provider...");
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
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        progDialog.setMessage("Trying Network Provider...");
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            // --GPS--
            final LocationListener myListener = this;

            Looper.prepare();
            final Looper myLooper = Looper.myLooper();
            final Handler myHandler = new Handler(myLooper);

            Log.d(TAG, "about to set GPS thread");

            locationManager.requestSingleUpdate(
                    LocationManager.GPS_PROVIDER, myListener, myLooper);

            myHandler.postDelayed(new Runnable() {
                public void run() {
                    try {
                        locationManager.removeUpdates(myListener);
                        publishProgress();
                        doNetworkProvider();
                    } catch (SecurityException e) {
                        Log.d(TAG, "Permission not given for location services");
                    }
                }
            }, GPS_TIMOUT);
        }
        catch (SecurityException e) {
            Log.d(TAG, "Permission not given for location services");
        }

        return null;
    }

    private void doNetworkProvider() {
        try {
            // --Network--
            final LocationListener myListener = this;

            Looper.prepare();
            final Looper myLooper = Looper.myLooper();
            final Handler myHandler = new Handler(myLooper);

            Log.d(TAG, "about to set Network thread");

            locationManager.requestSingleUpdate(
                    LocationManager.NETWORK_PROVIDER, myListener, myLooper);

            myHandler.postDelayed(new Runnable() {
                public void run() {
                    try {
                        locationManager.removeUpdates(myListener);
                    } catch (SecurityException e) {
                        Log.d(TAG, "Permission not given for location services");
                    }
                }
            }, NETWORK_TIMOUT);
        }
        catch (SecurityException e) {
            Log.d(TAG, "Permission not given for location services");
        }
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