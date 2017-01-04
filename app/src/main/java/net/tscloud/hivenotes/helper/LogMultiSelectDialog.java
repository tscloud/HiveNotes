package net.tscloud.hivenotes.helper;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.tscloud.hivenotes.R;

import java.util.ArrayList;
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

    public static LogMultiSelectDialog newInstance(String aTitle, String[] aElems,
                                                   String aChecketSet, String aTag) {
        LogMultiSelectDialog frag = new LogMultiSelectDialog();
        Bundle args = new Bundle();
        args.putString("title", aTitle);
        args.putStringArray("elems", aElems);
        args.putString("checkedset", aChecketSet);
        args.putString("tag", aTag);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final ArrayList<ViewHolder> viewholderList = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // get the Dialog Layout
        View view = getActivity().getLayoutInflater().inflate(R.layout.scb_listview3, null);

        // and the LinearLayout inside that Dialog that is functioning as the vertical list
        ViewGroup llItems = (ViewGroup)view.findViewById(R.id.linearLayoutScb);

        // load the "list"
        for (String s : getArguments().getStringArray("elems")) {
            ViewHolder holder = new ViewHolder();
            View item = View.inflate(getActivity(), R.layout.scb_item, null);
            holder.tv = (TextView)item.findViewById(R.id.tv);
            holder.cb = (SmoothCheckBox)item.findViewById(R.id.scb);

            //check to see if we need to set CheckBox to "checked"
            holder.cb.setChecked(getArguments().getString("checkedset").contains(s));

            holder.tv.setText(s);
            holder.tv.setTag(holder.cb);
            llItems.addView(item);

            viewholderList.add(holder);

            // Listeners
            //checkbox
            //holder.cb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            //    @Override
            //    public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
            //        checkBox.setChecked(isChecked, true);
            //    }
            //});

            //textview
            holder.tv.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    TextView tv = ((TextView)v);
                    SmoothCheckBox scb = (SmoothCheckBox)tv.getTag();
                    scb.setChecked(!scb.isChecked(), true);
                }
            });
        }

        // Add the "Other" EditText at the end
        ViewHolder holder = new ViewHolder();
        View item = View.inflate(getActivity(), R.layout.scb_item_other, null);
        holder.tv = (TextView)item.findViewById(R.id.et);
        holder.cb = (SmoothCheckBox)item.findViewById(R.id.scb);
        holder.tv.setTag(holder.cb);
        llItems.addView(item);

        viewholderList.add(holder);

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

        return diagFragDialog;
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
