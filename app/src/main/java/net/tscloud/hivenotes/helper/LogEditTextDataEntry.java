package net.tscloud.hivenotes.helper;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import net.tscloud.hivenotes.R;

import java.util.ArrayList;

/**
 * Created by tscloud on 2/10/17.
 */

public class LogEditTextDataEntry extends LogSuperDataEntry {

    public static final String TAG = "LogEditTextDataEntry";

    // Needed for onBackPressed() - seperate method that may get called from the Activity
    private EditText et;

    public static LogEditTextDataEntry newInstance(LogEditTextDialogData aData) {
        LogEditTextDataEntry frag = new LogEditTextDataEntry();
        Bundle args = new Bundle();
        args.putString("title", aData.getTitle());
        args.putString("tag", aData.getTag());
        args.putString("data", aData.getData());
        args.putBoolean("isOtherNum", aData.isOtherNum());
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ArrayList<RecyclerView.ViewHolder> viewholderList = new ArrayList<>();


        // get the Dialog Layout
        final View view = inflater.inflate(R.layout.scb_edittext_view, null);

        // Needed for onBackPressed() - seperate method that may get called from the Activity
        et = (EditText)view.findViewById(R.id.et);

        // if we only want numeric data => make sure that's what we get
        if (getArguments().getBoolean("isOtherNum")) {
            et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }

        et.setText(getArguments().getString("data"));

        // OK/Cancel button Listeners
        /*
        final Button dialogOKBtn = (Button)view.findViewById(R.id.buttonOKScb);
        dialogOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
        */

        return view;
    }

    @Override
    public boolean onBackPressed() {
        Log.d(TAG, "Back button clicked...save everything");
        boolean reply = false;

        if (mListener != null) {
            String[] result = new String[1];
            result[0] = et.getText().toString();
            mListener.onLogDataEntryOK(result, 0,
                    getArguments().getString("tag"));
            reply = true;
        }

        return reply;
    }

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
}