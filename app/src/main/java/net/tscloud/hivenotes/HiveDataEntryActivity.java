package net.tscloud.hivenotes;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import net.tscloud.hivenotes.helper.LogEditTextDataEntry;
import net.tscloud.hivenotes.helper.LogEditTextDataEntryLocation;
import net.tscloud.hivenotes.helper.LogEditTextDialogData;
import net.tscloud.hivenotes.helper.LogEditTextDialogLocationData;
import net.tscloud.hivenotes.helper.LogMultiSelectDataEntry;
import net.tscloud.hivenotes.helper.LogMultiSelectDialogData;
import net.tscloud.hivenotes.helper.LogSuperDataEntry;

/**
 * Created by tscloud on 3/3/17.
 */

public abstract class HiveDataEntryActivity extends AppCompatActivity implements
        LogSuperDataEntry.onLogDataEntryInteractionListener {

    public static final String TAG = "HiveDataEntryActivity";

    // needed for things like Dialog dismissal after its return w/ w/o data
    protected LogSuperDataEntry diagFragment;

    // Need a reference to the Fragment that we're going to launch as we may need to pass back data
    //  collected by Dialog
    protected HiveDataEntryFragment fragment = null;

    // Provide a reference to a containerViewId that the dialog will replace
    protected abstract int getContainerViewId();

    @Override
    public void onBackPressed() {
        // Do the stuff we need to do in the dialog - essentially we're done =>
        //  so save everything <-- do this when back button pressed from DataEntry dialog
        if (diagFragment == null || !diagFragment.onBackPressed()) {
            // Do the stuff we need to do in the fragment - essentially we're done =>
            //  so save everything <-- do this when back button pressed from Fragment
            if (fragment == null || !fragment.onFragmentSave()) {
                // neither dialog nor fragment consumed event
                super.onBackPressed();
            }
        }
    }

    // Dialog w/ checkboxes
    @Override
    public void onLogLaunchDialog(LogMultiSelectDialogData aData) {
        //diagFragment = LogMultiSelectDialog.newInstance(aData);
        //diagFragment.show(getSupportFragmentManager(), aData.getTag());
        diagFragment = LogMultiSelectDataEntry.newInstance(aData);
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(aData.getTag())
                .replace(getContainerViewId(), diagFragment, aData.getTag())
                .commit();
    }

    // Dialog w/ edittext
    @Override
    public void onLogLaunchDialog(LogEditTextDialogData aData) {
        //diagFragment = LogEditTextDialog.newInstance(aData);
        //diagFragment.show(getSupportFragmentManager(), aData.getTag());
        diagFragment = LogEditTextDataEntry.newInstance(aData);
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(aData.getTag())
                .replace(getContainerViewId(), diagFragment, aData.getTag())
                .commit();
    }

    // Dialog w/ location getter
    @Override
    public void onLogLaunchDialog(LogEditTextDialogLocationData aData) {
        //diagFragment = LogEditTextDialog.newInstance(aData);
        //diagFragment.show(getSupportFragmentManager(), aData.getTag());
        diagFragment = LogEditTextDataEntryLocation.newInstance(aData);
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(aData.getTag())
                .replace(getContainerViewId(), diagFragment, aData.getTag())
                .commit();
    }

    @Override
    public void onLogDataEntryOK(String[] aResults, long aResultRemTime, String aResultRemDesc,
                                 String aTag) {
        Log.d(TAG, "onLogDataEntryOK: OK button clicked");

        for (String s: aResults) {
            Log.d(TAG, s);
        }

        // TODO: nulling the diagFragment necessary/required/desired?
        diagFragment = null;

        fragment.setDialogData(aResults, aResultRemTime, aResultRemDesc, aTag);
        //diagFragment.dismiss();
        getSupportFragmentManager().popBackStack();
    }
}
