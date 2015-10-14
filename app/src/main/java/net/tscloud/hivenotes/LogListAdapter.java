package net.tscloud.hivenotes;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by tscloud on 10/3/15.
 */
public class LogListAdapter extends BaseAdapter {

    private List<LogEntryNames.LogEntryItem> mList;
    private LayoutInflater mLayoutInflater = null;
    private Activity mContext = null;

    public LogListAdapter(Activity context, List<LogEntryNames.LogEntryItem> list) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mList = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public LogEntryNames.LogEntryItem getItem(int position) {
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
            convertView.setTag(new LogListViewHolder(logTextView, logImageImageView, logArrowImageView));
        } else {
            LogListViewHolder viewHolder = (LogListViewHolder)convertView.getTag();
            logTextView = viewHolder.logTextView;
            logImageImageView = viewHolder.logImageImageView;
            logArrowImageView = viewHolder.logArrowImageView;
        }

        LogEntryNames.LogEntryItem logEntryItem = getItem(position);

        // set text based on position
        logTextView.setText(logEntryItem.toString());

        // set image based on position
        String imageName = logEntryItem.getImageSrc();
        int imageResource = mContext.getResources().getIdentifier(imageName, "drawable", mContext.getPackageName());
        logImageImageView.setImageResource(imageResource);

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
