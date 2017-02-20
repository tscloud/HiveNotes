package net.tscloud.hivenotes.helper;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.util.Log;

/**
 * Created by tscloud on 2/10/17.
 */

public abstract class LogSuperDialog2 extends Fragment {

    public static final String TAG = "LogSuperDialog";

    // reference to Activity that should have started me
    protected onLogMultiSelectDialogInteractionListener mListener;

    /**
     * This will get called from attached Activity when back button pressed
     * Note this works ONLY for Fragment; Dialogs intercept their own back
     *  button pressed events
     */
    public abstract boolean onBackPressed();

    /*
    interface to define in the Activity what should be upon OK/Cancel
     */
    public interface onLogMultiSelectDialogInteractionListener {
        void onLogMultiSelectDialogOK(String[] aResults, long aResultRemTime, String aTag);
        void onLogMultiSelectDialogCancel(String aTag);
    }
}