package net.tscloud.hivenotes.helper;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import net.tscloud.hivenotes.R;

import java.util.Calendar;

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
        args.putString("subtitle", aData.getSubtitle());
        args.putString("tag", aData.getTag());
        args.putString("data", aData.getData());
        args.putBoolean("isOtherNum", aData.isOtherNum());
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // get the Dialog Layout
        final View view = inflater.inflate(R.layout.scb_edittext_view, null);

        // title
        final TextView title = (TextView)view.findViewById(R.id.texttitle);
        title.setText(getArguments().getString("title"));

        // subtitle
        final TextView subtitle = (TextView)view.findViewById(R.id.textsubtitle);
        String subtitleString = getArguments().getString("subtitle");

        if (subtitleString != null) {
            // potentially deal /w the year in the subtitle if there is 1
            subtitle.setText(subtitleString.replace("YYYY", Integer.toString(calendar.get(Calendar.YEAR))));
        }
        else {
            subtitle.setVisibility(View.GONE);
        }

        // Needed for onBackPressed() - separate method that may get called from the Activity
        et = (EditText)view.findViewById(R.id.et);

        // if we only want numeric data => make sure that's what we get
        if (getArguments().getBoolean("isOtherNum")) {
            et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }

        et.setText(getArguments().getString("data"));

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
}