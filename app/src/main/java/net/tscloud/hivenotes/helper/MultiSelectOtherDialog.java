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

public class MultiSelectOtherDialog extends DialogFragment {

    public static final String TAG = "MultiSelectOtherDialog";

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
        //int mListLen = (getArguments().getStringArray("elems").length);
        for (int i = 0; i < mListLen; i++) {
            mList.add(new Bean());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.scb_listview2, null);

        ListView listViewItems = (ListView)view.findViewById(R.id.lvScb);
        listViewItems.setAdapter(new MultiSelectOtherAdapter());
        listViewItems.setOnItemClickListener(new OnItemClickListenerListViewItem());

        builder.setTitle(getArguments().getString("title")).setView(view);

        AlertDialog diagFragDialog = builder.create();

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
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            convertView = null;
            //if (convertView == null) {
                holder = new ViewHolder();
                // needed for the "Other" EditText at the end
                if (position < mList.size() - 1) {
                    convertView = View.inflate(getActivity(), R.layout.scb_item, null);
                    holder.tv = (TextView) convertView.findViewById(R.id.tv);
                    Log.d(TAG, "1)");
                }
                else {
                    convertView = View.inflate(getActivity(), R.layout.scb_item_other, null);
                    holder.tv = (TextView) convertView.findViewById(R.id.et);
                    Log.d(TAG, "2)");
                }
                holder.cb = (SmoothCheckBox) convertView.findViewById(R.id.scb);
            //    convertView.setTag(holder);
            //} else {
            //    holder = (ViewHolder) convertView.getTag();
            //    Log.d(TAG, "3)");
            //}

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
                Log.d(TAG, "4)");
            }
            else {
                //convertView = View.inflate(getActivity(), R.layout.scb_item_other, null);
                //holder.tv = (TextView) convertView.findViewById(R.id.et);
                //holder.cb = (SmoothCheckBox) convertView.findViewById(R.id.scb);
                //convertView.setTag(holder);
                Log.d(TAG, "5)");
            }

            holder.cb.setChecked(bean.isChecked);
            Log.d(TAG, "6)");
            Log.d(TAG, "holder.tv: " + holder.tv.getText());
            Log.d(TAG, "holder.cb: " + holder.cb.isChecked());

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
