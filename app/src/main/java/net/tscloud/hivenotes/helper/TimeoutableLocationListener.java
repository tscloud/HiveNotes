package net.tscloud.hivenotes.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Kernel derived from work of below author - drastically changed
 *
 * TimeoutableLocationListner is implementation of LocationListener.
 * If onLocationChanged isn't called within XX mili seconds, automatically remove listener.
 *
 * @author amay077 - http://twitter.com/amay077
 */
public class TimeoutableLocationListener implements LocationListener {

    public static final String TAG = "TLocationListener";

    private Timer mTimerTimeoutGPS;
    private Timer mTimerTimeoutNetwork;
    private LocationManager mLocaMan = null;
    private Location mLocation;
    private LocationListener mLocationListener;
    private long mTimeOutMS;
    private LocationTimeoutListener mCaller;

    private ProgressDialog progDialog = null;

    /*
    Handler stuff
     */
    private static class MyHandler extends Handler {
        private final WeakReference<TimeoutableLocationListener> mOuterClass;

        public MyHandler(TimeoutableLocationListener aOuterClass) {
            mOuterClass = new WeakReference<TimeoutableLocationListener>(aOuterClass);
        }

        @Override
        public void handleMessage(Message msg) {
            TimeoutableLocationListener outerClass = mOuterClass.get();
            if (outerClass != null) {
                // --Dialog Processing--
                String dialogText = (String)msg.obj;
                if (dialogText != null) {
                    outerClass.progDialog.setMessage(dialogText);
                }
                else {
                    outerClass.progDialog.dismiss();
                }
            }
        }
    }

    private final MyHandler mHandler = new MyHandler(this);

    /*
    Constructor
     */
    public TimeoutableLocationListener(LocationManager locaMan, long timeOutMS, LocationTimeoutListener caller) {
        mLocaMan  = locaMan;
        mTimeOutMS = timeOutMS;
        mLocationListener = this;
        mCaller = caller;
    }

    /*
    Main Entry Point
     */
    public void execute(Context aUser) {
        try {
            // -- GPS timeout--
            mTimerTimeoutGPS = new Timer();
            mTimerTimeoutGPS.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "1st timer expired");
                    stopLocationUpdateAndTimer(false);
                    //  -- Network timeout --
                    secondTimeout();

                }
            }, mTimeOutMS);

            progDialog = new ProgressDialog(aUser);
            progDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    //progDialog.dismiss();
                    stopLocationUpdateAndTimer(true);
                }
            });
            progDialog.setIndeterminate(true);
            progDialog.setCancelable(true);
            progDialog.setMessage("Trying GPS Provider...");
            progDialog.show();

            Log.d(TAG, "about to show dialog and call requestSingleUpdate()");
            // callback for this request will be on the UI thread, hence setLooper arg to null
            mLocaMan.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocationListener, null);
        }
        catch (SecurityException e) {
            Log.d(TAG, "Permission not given for location services");
        }
    }

    private void secondTimeout() {
        try {
            // Update the dialog on the UI thread
            Message msg = Message.obtain();
            msg.obj = "Trying Network Provider...";
            mHandler.sendMessage(msg);

            Looper.prepare();
            final Looper myLooper = Looper.myLooper();
            mTimerTimeoutNetwork = new Timer();
            mTimerTimeoutNetwork.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "2nd timer expired");
                    stopLocationUpdateAndTimer(true);
                }

            }, mTimeOutMS);

            // callback for this request will be on the 1st timer thread, hence need for myLooper
            mLocaMan.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, mLocationListener, myLooper);

        } catch (SecurityException e) {
            Log.d(TAG, "Permission not given for location services");
        }
    }

    private void stopLocationUpdateAndTimer(boolean allDone) {
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

        if (allDone) {
            // Update the dialog on the UI thread
            Message msg = Message.obtain();
            msg.obj = null;
            mHandler.sendMessage(msg);

            // this should be the only invoke of the callback
            if (mCaller != null) {
                mCaller.onLocationTimedout(mLocation);
            }
        }
    }

    @Override
    public void onLocationChanged(Location aLocation) {
        Log.d(TAG, "onLocationChanged() called");
        mLocation = aLocation;
        stopLocationUpdateAndTimer(true);
    }

    @Override
    public void onProviderDisabled(String s) { }

    @Override
    public void onProviderEnabled(String s) { }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }

    public interface LocationTimeoutListener {
        void onLocationTimedout(Location aLocation);
    }
}