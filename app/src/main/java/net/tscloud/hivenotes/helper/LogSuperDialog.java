package net.tscloud.hivenotes.helper;

/**
 * Created by tscloud on 2/10/17.
 */

public class LogSuperDialog extends DialogFragment {

    public static final String TAG = "LogSuperDialog";

    /*
    interface to define in the Activity what should be upon OK/Cancel
     */
    public interface onLogMultiSelectDialogInteractionListener {
        void onLogMultiSelectDialogOK(String[] aResults, long aResultRemTime, String aTag);
        void onLogMultiSelectDialogCancel(String aTag);
    }
}