package net.tscloud.hivenotes.helper;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.util.Log;

/**
 * Created by tscloud on 2/10/17.
 */

public abstract class LogSuperDialog extends DialogFragment {

    public static final String TAG = "LogSuperDialog";

    // reference to Activity that should have started me
    protected onLogMultiSelectDialogInteractionListener mListener;

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(TAG, "Back button clicked");

        if (mListener != null) {
            mListener.onLogMultiSelectDialogCancel(getArguments().getString("tag"));
        }
    }

    /*
    interface to define in the Activity what should be upon OK/Cancel
     */
    public interface onLogMultiSelectDialogInteractionListener {
        void onLogMultiSelectDialogOK(String[] aResults, long aResultRemTime, String aTag);
        void onLogMultiSelectDialogCancel(String aTag);
    }
}