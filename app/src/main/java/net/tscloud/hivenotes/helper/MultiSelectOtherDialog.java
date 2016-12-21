package net.tscloud.hivenotes.helper;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
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

public class MultiSelectOtherDialog extends DialogFragment {

    private ArrayList<Bean> mList = new ArrayList<>();

    public static MultiSelectOtherDialog newInstance(String aTitle, String[] aElems) {
        MultiSelectOtherDialog frag = new MultiSelectOtherDialog();
        Bundle args = new Bundle();
        args.putString("title", aTitle);
        args.putStringArray("elems", aElems);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // length of this list determines how many items to present - need an extra for the EditText
        //  at the end for "Other"
        int mListLen = (getArguments().getStringArray("elems").length) + 1;
        for (int i = 0; i < mListLen; i++) {
            mList.add(new Bean());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        ListView listViewItems = new ListView(getActivity());
        listViewItems.setAdapter(new MultiSelectOtherAdapter());
        listViewItems.setOnItemClickListener(new OnItemClickListenerListViewItem());

        builder.setTitle(R.string.hivehealth_notes_string)
                .setView(listViewItems)
                .setPositiveButton(R.string.ok_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                    }
                })
                .setNegativeButton(R.string.cancel_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog diagFragDialog = builder.create();

        /*
        diagFragDialog.show();

        final Button positiveButton = diagFragDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveButtonLL.gravity = Gravity.CENTER;
        positiveButton.setLayoutParams(positiveButtonLL);
        */

        return diagFragDialog;
    }

    public class MultiSelectOtherAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                // needed for the "Other" EditText at the end
                if (position < mList.size() - 1) {
                    convertView = View.inflate(getActivity(), R.layout.scb_item, null);
                }
                else {
                    convertView = View.inflate(getActivity(), R.layout.scb_item_other, null);
                }
                holder.tv = (TextView) convertView.findViewById(R.id.tv);
                holder.cb = (SmoothCheckBox) convertView.findViewById(R.id.scb);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Bean bean = mList.get(position);
            holder.cb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                    bean.isChecked = isChecked;
                }
            });
            // needed for the "Other" EditText at the end
            if (position < getArguments().getStringArray("elems").length) {
                String text = getArguments().getStringArray("elems")[position];
                holder.tv.setText(text);
            }
            holder.cb.setChecked(bean.isChecked);

            return convertView;
        }

        class ViewHolder {
            SmoothCheckBox cb;
            TextView tv;
        }
    }

    public class OnItemClickListenerListViewItem implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Bean bean = (Bean) parent.getAdapter().getItem(position);
            bean.isChecked = !bean.isChecked;
            SmoothCheckBox checkBox = (SmoothCheckBox) view.findViewById(R.id.scb);
            checkBox.setChecked(bean.isChecked, true);
        }
    }

    class Bean implements Serializable {
        boolean isChecked;
    }
}
