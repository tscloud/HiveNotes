package net.tscloud.hivenotes.helper;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import net.tscloud.hivenotes.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.refactor.library.SmoothCheckBox;

/**
 * Created by tscloud on 12/19/16.
 */

public class LogMultiSelectDialog extends DialogFragment {

    public static final String TAG = "LogMultiSelectDialog";

    // reference to Activity that should have started me
    private onLogMultiSelectDialogInteractionListener mListener;

    // need to make a place for Views to go so we can pass values back to caller
    //ArrayList<ViewHolder> viewholderList;

    public static LogMultiSelectDialog newInstance(LogMultiSelectDialogData aData) {
        LogMultiSelectDialog frag = new LogMultiSelectDialog();
        Bundle args = new Bundle();
        args.putString("title", aData.getTitle());
        args.putStringArray("elems", aData.getElems());
        args.putString("checkedset", aData.getCheckedSet());
        args.putString("tag", aData.getTag());
        args.putBoolean("hasOther", aData.isHasOther());
        args.putBoolean("hasReminder", aData.isHasReminder());
        args.putBoolean("isMultiselect", aData.isMultiselect());
        frag.setArguments(args);
        return frag;
    }
/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }
*/
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    //public View onCreateView(LayoutInflater inflater, ViewGroup container,
    //                         Bundle savedInstanceState) {

        final ArrayList<ViewHolder> viewholderList = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // get the Dialog Layout
        View view = getActivity().getLayoutInflater().inflate(R.layout.scb_listview4, null);
        //View view = inflater.inflate(R.layout.scb_listview4, container, false);

        // and the LinearLayout inside that Dialog that is functioning as the vertical list
        ViewGroup llItems = (ViewGroup)view.findViewById(R.id.linearLayoutScb);

        // load the "list"
        for (String s : getArguments().getStringArray("elems")) {
            ViewHolder holder = new ViewHolder();

            // ** inflate test **
            //View item = View.inflate(getActivity(), R.layout.scb_item, null);
            View item = getActivity().getLayoutInflater().inflate(R.layout.scb_item, llItems,
                    false);

            holder.tv = (TextView)item.findViewById(R.id.tv);
            holder.cb = (SmoothCheckBox)item.findViewById(R.id.scb);

            //check to see if we need to set CheckBox to "checked"
            holder.cb.setChecked(getArguments().getString("checkedset").contains(s));

            holder.tv.setText(s);
            //set the TextView's tag to the SmoothCheckBox to facilitate checking
            // when TextView is clicked
            holder.tv.setTag(holder.cb);

            // ** inflate test **
            llItems.addView(item);

            viewholderList.add(holder);

            //TextView Listener
            holder.tv.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    TextView tv = (TextView)v;
                    SmoothCheckBox scb = (SmoothCheckBox)tv.getTag();
                    scb.setChecked(!scb.isChecked(), true);
                }
            });

            //Checkbox listener - only do if we are NOT multiselect
            if (!getArguments().getBoolean("isMultiselect")) {
                setListenerForNonMultiselect(holder.cb, viewholderList);
            }
        }

        // Conditionally add the "Other" EditText at the end of the list
        if (getArguments().getBoolean("hasOther")) {
            ViewHolder holder = new ViewHolder();

            // ** inflate test **
            //View item = View.inflate(getActivity(), R.layout.scb_item_other, null);
            View item = getActivity().getLayoutInflater().inflate(R.layout.scb_item_other, llItems,
                    false);

            holder.tv = (TextView)item.findViewById(R.id.et);
            holder.cb = (SmoothCheckBox)item.findViewById(R.id.scb);
            holder.tv.setTag(holder.cb);

            // Check to see if we need to populate w/ user entered value
            fillOther(holder);

            // ** inflate test **
            llItems.addView(item);

            viewholderList.add(holder);

            //Checkbox listener - only do if we are NOT multiselect
            if (!getArguments().getBoolean("isMultiselect")) {
                setListenerForNonMultiselect(holder.cb, viewholderList);
            }
        }

        // Conditionally add reminder (will always only be at most 1)
        if (getArguments().getBoolean("hasReminder")) {
            // get the reminder item view
            view.findViewById(R.id.reminderItem).setVisibility(View.VISIBLE);

        }

        // OK/Cancel button Listeners
        final Button dialogOKBtn = (Button)view.findViewById(R.id.buttonOKScb);
        dialogOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "OK button clicked");

                List<String> resultList = new ArrayList<>();

                for (ViewHolder checkEdit:viewholderList) {
                    if (checkEdit.cb.isChecked()) {
                        resultList.add(checkEdit.tv.getText().toString());
                    }
                }

                if (mListener != null) {
                    String[] result = new String[resultList.size()];
                    result = resultList.toArray(result);
                    mListener.onLogMultiSelectDialogOK(result, getArguments().getString("tag"));
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

        //  ** TEST  **  //
        diagFragDialog.getWindow().setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //diagFragDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return diagFragDialog;
        //return view;
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

    /**
     * This is how we fill the user enterable EditText at the end if it is necessary.
     *  Check the last entry in the "checked" set -> if it's <> to anything in the
     *  "elems" => it was entered by the user & needs to be presented here
     */
    private void fillOther(ViewHolder aHolder) {
        Log.d(TAG, "checking for user entered value");

        String reply = null;

        //easier to set the "checkedset"
        if (getArguments().getString("checkedset") != null &&
                getArguments().getString("checkedset") != "") {
            String[] checkArray = getArguments().getString("checkedset").split(",");
            String checkString = null;
            //this should be the last entry of "checkedset"
            checkString = checkArray[checkArray.length - 1];
            if (!Arrays.asList(getArguments().getStringArray("elems")).contains(checkString)) {
                aHolder.tv.setText(checkString);
                aHolder.cb.setChecked(true);
            }
        }
    }

    /**
     * if doing NOT multiselect => set checkbox listener to unselect all the others
     */
    private void setListenerForNonMultiselect(SmoothCheckBox aCheckBox,
                                              final ArrayList<ViewHolder> aViewholderList) {
        aCheckBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(SmoothCheckBox view, boolean isChecked) {
                if (isChecked) {
                    for (ViewHolder vh : aViewholderList) {
                        if (vh.cb != view) {
                            vh.cb.setChecked(false);
                        }
                    }
                }
            }
        });

    }

    class ViewHolder {
        SmoothCheckBox cb;
        TextView tv;
    }

    /*
    interface to define in the Activity what should be upon OK/Cancel
     */
    public interface onLogMultiSelectDialogInteractionListener {
        void onLogMultiSelectDialogOK(String[] aResults, String aTag);
        void onLogMultiSelectDialogCancel(String aTag);
    }
}
