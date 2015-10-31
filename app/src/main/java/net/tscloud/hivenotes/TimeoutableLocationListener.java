package net.tscloud.hivenotes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeoutableLocationListner is implementation of LocationListener.
 * If onLocationChanged isn't called within XX mili seconds, automatically remove listener.
 *
 * @author amay077 - http://twitter.com/amay077
 */
public class TimeoutableLocationListener implements LocationListener {

    public static final String TAG = "TLocationListener";

    protected Timer mTimerTimeoutGPS;
    protected Timer mTimerTimeoutNetwork;
    protected LocationManager mLocaMan = null;
    protected Location mLocation;
    private LocationListener mLocationListener;
    protected long mTimeOutMS;

    private ProgressDialog progDialog = null;


    public TimeoutableLocationListener(LocationManager locaMan, long timeOutMS) {
        mLocaMan  = locaMan;
        mTimeOutMS = timeOutMS;
        mLocationListener = this;
    }

    public void execute(final TimeoutLisener timeoutListener, Context user) {
        try {
            // -- GPS timeout--
            mTimerTimeoutGPS = new Timer();
            mTimerTimeoutGPS.schedule(new TimerTask() {

                @Override
                public void run() {
                    Log.d(TAG, "1st timer expired");
                    stopLocationUpdateAndTimer();
                    //  -- Network timeout --
                    secondTimout();

                }
            }, mTimeOutMS);

            Log.d(TAG, "about to show dialog and call requestSingleUpdate()");

            mLocaMan.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocationListener, null);

            progDialog = new ProgressDialog(user);
            progDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    progDialog.dismiss();
                    stopLocationUpdateAndTimer();
                }
            });
            progDialog.setIndeterminate(true);
            progDialog.setCancelable(true);
            progDialog.setMessage("Trying GPS Provider...");
            progDialog.show();
        }
        catch (SecurityException e) {
            Log.d(TAG, "Permission not given for location services");
        }
    }

    private void secondTimout() {
        try {
            Looper.prepare();
            final Looper myLooper = Looper.myLooper();
            mTimerTimeoutNetwork = new Timer();
            mTimerTimeoutNetwork.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "2nd timer expired");
                    stopLocationUpdateAndTimer();

                    if (progDialog != null) {
                        Log.d(TAG, "dismissing dialog");
                        progDialog.dismiss();
                    }
                }

            }, mTimeOutMS);

            mLocaMan.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, mLocationListener, myLooper);

        } catch (SecurityException e) {
            Log.d(TAG, "Permission not given for location services");
        }
    }

    private void stopLocationUpdateAndTimer() {
        Log.d(TAG, "stopLocationUpdateAndTimer() called");
        try {
            mLocaMan.removeUpdates(this);
        }
        catch (SecurityException e) {
            Log.d(TAG, "Permission not given for location services");
        }

        if (mTimerTimeoutGPS != null) {
            Log.d(TAG, "killing GPS timer");
            mTimerTimeoutGPS.cancel();
            mTimerTimeoutGPS.purge();
            mTimerTimeoutGPS = null;
        }

        if (mTimerTimeoutNetwork != null) {
            Log.d(TAG, "killing Network timer");
            mTimerTimeoutNetwork.cancel();
            mTimerTimeoutNetwork.purge();
            mTimerTimeoutNetwork = null;
        }
    }

    /***
     * Location callback.
     *
     * If override on your concrete class, must call base.onLocation().
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged() called");
        mLocation = location;
        stopLocationUpdateAndTimer();
    }

    @Override
    public void onProviderDisabled(String s) { }

    @Override
    public void onProviderEnabled(String s) { }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }

    public interface TimeoutLisener {
        void onTimeouted(LocationListener sender);
    }
}