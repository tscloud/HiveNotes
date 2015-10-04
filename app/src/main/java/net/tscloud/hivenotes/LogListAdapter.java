package net.tscloud.hivenotes;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.tscloud.hivenotes.db.LogEntry;

import java.util.List;

/**
 * Created by tscloud on 10/3/15.
 */
public class LogListAdapter extends BaseAdapter {

    private List<LogEntryNames.DummyItem> mList;
    private LayoutInflater mLayoutInflater = null;

    public LogListAdapter(Activity context, List<LogEntryNames.DummyItem> list) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public LogEntryNames.DummyItem getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView logTextView;
        ImageView logImageImageView;
        ImageView logArrowImageView;

        if (convertView == null) {
            convertView = mLayoutInflater
                    .inflate(R.layout.log_edit_button, parent, false);

            logTextView = (TextView) convertView.findViewById(R.id.logTextView);
            logImageImageView = (ImageView)convertView.findViewById(R.id.logImage);
            logArrowImageView = (ImageView)convertView.findViewById(R.id.logArrowImage);
        } else {
            LogListViewHolder viewHolder = (LogListViewHolder)convertView.getTag();
            logTextView = viewHolder.logTextView;
            logImageImageView = viewHolder.logImageImageView;
            logArrowImageView = viewHolder.logArrowImageView;
        }

        LogEntryNames.DummyItem textItem = getItem(position);
        logTextView.setText(textItem.toString());

        return convertView;
    }

    private static class LogListViewHolder {

        public final TextView logTextView;
        public final ImageView logImageImageView;
        public final ImageView logArrowImageView;

        public LogListViewHolder(TextView logTextView, ImageView logImageImageView, ImageView logArrowImageView) {
            this.logTextView = logTextView;
            this.logImageImageView = logImageImageView;
            this.logArrowImageView = logArrowImageView;
        }
    }
}
