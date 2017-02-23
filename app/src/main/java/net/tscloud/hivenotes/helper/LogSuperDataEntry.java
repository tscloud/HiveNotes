package net.tscloud.hivenotes.helper;

import android.support.v4.app.Fragment;

/**
 * Created by tscloud on 2/10/17.
 */

public abstract class LogSuperDataEntry extends Fragment {

    public static final String TAG = "LogSuperDataEntry";

    // reference to Activity that should have started me
    protected onLogDataEntryInteractionListener mListener;

    /**
     * This will get called from attached Activity when back button pressed
     * Note this works ONLY for Fragment; Dialogs intercept their own back
     *  button pressed events
     */
    public abstract boolean onBackPressed();

    /*
    interface to define in the Activity what should be upon OK/Cancel
     */
    public interface onLogDataEntryInteractionListener {
        void onLogDataEntryOK(String[] aResults, long aResultRemTime, String aTag);
        //void onLogMultiSelectDialogCancel(String aTag);
    }
}