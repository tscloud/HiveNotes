package net.tscloud.hivenotes.helper;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.tscloud.hivenotes.R;

import java.io.Serializable;
import java.util.ArrayList;

import cn.refactor.library.SmoothCheckBox;

/**
 * Created by tscloud on 12/19/16.
 */

public class MultiSelectOtherDialogNoAdapter extends DialogFragment {

    public static final String TAG = "MultiSelectOtherDialogNoAdapter";

    public static MultiSelectOtherDialogNoAdapter newInstance(String aTitle, String[] aElems) {
        MultiSelectOtherDialogNoAdapter frag = new MultiSelectOtherDialogNoAdapter();
        Bundle args = new Bundle();
        args.putString("title", aTitle);
        args.putStringArray("elems", aElems);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

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
            holder.tv.setText(s);
            holder.tv.setTag(holder.cb);
            llItems.addView(item);

            // Listeners
            //checkbox
            holder.cb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                    checkBox.setChecked(isChecked, true);
                }
            });

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

        builder.setTitle(getArguments().getString("title")).setView(view);

        AlertDialog diagFragDialog = builder.create();

        return diagFragDialog;
    }

    class ViewHolder {
        SmoothCheckBox cb;
        TextView tv;
    }
}
