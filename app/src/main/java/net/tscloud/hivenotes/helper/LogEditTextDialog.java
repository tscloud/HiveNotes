package net.tscloud.hivenotes.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import net.tscloud.hivenotes.LogHiveHealthFragment;
import net.tscloud.hivenotes.R;
import net.tscloud.hivenotes.db.NotificationType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.refactor.library.SmoothCheckBox;

/**
 * Created by tscloud on 2/10/17.
 */

public class LogEditTextDialog extends LogSuperDialog2 {

    public static final String TAG = "LogEditTextDialog";

    // Needed for onBackPressed() - seperate method that may get called from the Activity
    EditText et;

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
    //public Dialog onCreateDialog(Bundle savedInstanceState) {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ArrayList<RecyclerView.ViewHolder> viewholderList = new ArrayList<>();

        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // get the Dialog Layout
        //final View view = getActivity().getLayoutInflater().inflate(R.layout.scb_edittext_view, null);
        final View view = inflater.inflate(R.layout.scb_edittext_view, null);

        // Needed for onBackPressed() - seperate method that may get called from the Activity
        et = (EditText)view.findViewById(R.id.et);
        et.setText(getArguments().getString("data"));

        // OK/Cancel button Listeners
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

        //builder.setTitle(getArguments().getString("title")).setView(view);

        //AlertDialog diagFragDialog = builder.create();

        //return diagFragDialog;
        return view;
    }

    @Override
    public boolean onBackPressed() {
        Log.d(TAG, "Back button clicked...save everything");
        boolean reply = false;

        if (mListener != null) {
            String[] result = new String[1];
            result[0] = et.getText().toString();
            mListener.onLogMultiSelectDialogOK(result, 0,
                    getArguments().getString("tag"));
            reply = true;
        }

        return reply;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onLogMultiSelectDialogInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onLogMultiSelectDialogInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}