package net.tscloud.hivenotes.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.tscloud.hivenotes.R;

import java.util.ArrayList;

/**
 * Created by tscloud on 2/10/17.
 */

public class LogEditTextDialog extends LogSuperDialog {

    public static final String TAG = "LogEditTextDialog";

    public static LogEditTextDialog newInstance(LogEditTextDialogData aData) {
        LogEditTextDialog frag = new LogEditTextDialog();
        Bundle args = new Bundle();
        args.putString("title", aData.getTitle());
        args.putString("tag", aData.getTag());
        args.putString("data", aData.getData());
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final ArrayList<RecyclerView.ViewHolder> viewholderList = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // get the Dialog Layout
        final View view = getActivity().getLayoutInflater().inflate(R.layout.scb_edittext_view, null);

        // OK/Cancel button Listeners
        final Button dialogOKBtn = (Button)view.findViewById(R.id.buttonOKScb);
        dialogOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "OK button clicked");

                if (mListener != null) {
                    final EditText et = (EditText)view.findViewById(R.id.et);
                    String[] result = new String[1];
                    result[0] = et.getText().toString();
                    mListener.onLogMultiSelectDialogOK(result, 0,
                            getArguments().getString("tag"));
                }
            }
        });

        final Button dialogCancelBtn = (Button)view.findViewById(R.id.buttonCancelScb);
        dialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Cancel button clicked");

                if (mListener != null) {
                    mListener.onLogMultiSelectDialogCancel(getArguments().getString("tag"));
                }
            }
        });

        builder.setTitle(getArguments().getString("title")).setView(view);

        AlertDialog diagFragDialog = builder.create();

        return diagFragDialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onLogMultiSelectDialogInteractionListener) activity;
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
}