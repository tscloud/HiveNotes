package net.tscloud.hivenotes;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.tscloud.hivenotes.db.Hive;

import java.util.List;

/**
 * Created by tscloud on 9/16/15.
 */
public class HiveListAdapter extends BaseAdapter {

    private List<Hive> mList;
    private LayoutInflater mLayoutInflater = null;

    public HiveListAdapter(Activity context, List<Hive> list) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Hive getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView hiveTextView;
        ImageView hiveFeedingImageView;
        ImageView hiveGeneralImageView;
        ImageView hiveOtherImageView;

        if (convertView == null) {
            convertView = mLayoutInflater
                    .inflate(R.layout.hive_edit_button, parent, false);
            hiveTextView = (TextView) convertView.findViewById(R.id.hiveEditTextView);
            hiveFeedingImageView = (ImageView) convertView.findViewById(R.id.hiveFeedingImage);
            hiveGeneralImageView = (ImageView) convertView.findViewById(R.id.hiveGeneralImage);
            hiveOtherImageView = (ImageView) convertView.findViewById(R.id.hiveOtherImage);
            convertView.setTag(new HiveListViewHolder(hiveTextView, hiveFeedingImageView, hiveGeneralImageView, hiveOtherImageView));
        } else {
            HiveListViewHolder viewHolder = (HiveListViewHolder) convertView.getTag();
            hiveTextView = viewHolder.hiveEditView;
            hiveFeedingImageView = viewHolder.hiveFeedingImageView;
            hiveGeneralImageView = viewHolder.hiveGeneralImageView;
            hiveOtherImageView = viewHolder.hiveOtherImageView;
        }

        Hive hive = getItem(position);
        hiveTextView.setText(hive.getName());
        // Will need this to read Hive when something clicked
        hiveTextView.setTag(hive.getId());

        return convertView;
    }

    private static class HiveListViewHolder {

        public final TextView hiveEditView;
        public final ImageView hiveFeedingImageView;
        public final ImageView hiveGeneralImageView;
        public final ImageView hiveOtherImageView;

        public HiveListViewHolder(TextView hiveEditView, ImageView hiveFeedingImageView, ImageView hiveGeneralImageView, ImageView hiveOtherImageView) {
            this.hiveEditView = hiveEditView;
            this.hiveFeedingImageView = hiveFeedingImageView;
            this.hiveGeneralImageView = hiveGeneralImageView;
            this.hiveOtherImageView = hiveOtherImageView;
        }
    }
}
