package net.tscloud.hivenotes.helper;

import android.app.Activity;
import android.support.v4.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by tscloud on 2/10/17.
 */

public abstract class LogSuperDataEntry extends Fragment {

    public static final String TAG = "LogSuperDataEntry";

    // time/date formatters
    protected static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG,
            Locale.getDefault());
    protected static final String TIME_PATTERN = "HH:mm";
    protected static final SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_PATTERN,
            Locale.getDefault());
    protected final Calendar calendar = Calendar.getInstance();

    // reference to Activity that should have started me
    protected onLogDataEntryInteractionListener mListener;

    /**
     * This will get called from attached Activity when back button pressed
     * Note this works ONLY for Fragment; Dialogs intercept their own back
     *  button pressed events
     */
    public abstract boolean onBackPressed();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onLogDataEntryInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onLogDataEntryInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /*
    interface to define in the Activity what should be upon OK/Cancel
     */
    public interface onLogDataEntryInteractionListener {
        void onLogDataEntryOK(String[] aResults, long aResultRemTime, String aTag);
        //void onLogMultiSelectDialogCancel(String aTag);
        void onLogLaunchDialog(LogMultiSelectDialogData aData);
        void onLogLaunchDialog(LogEditTextDialogData aData);
    }

    /*
    interface to define in the Fragment to set the data entered via dialog
     */
    public interface onLogDataEntrySetData {
        void setDialogData(String[] aResults, long aResultRemTime, String aTag);
        abstract boolean onFragmentSave();
    }
}