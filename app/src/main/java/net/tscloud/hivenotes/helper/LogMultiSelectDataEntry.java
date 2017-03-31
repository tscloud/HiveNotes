package net.tscloud.hivenotes.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import net.tscloud.hivenotes.R;
import net.tscloud.hivenotes.db.NotificationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import cn.refactor.library.SmoothCheckBox;

import static net.tscloud.hivenotes.R.id.et;

/**
 * Created by tscloud on 12/19/16.
 */

public class LogMultiSelectDataEntry extends LogSuperDataEntry {

    public static final String TAG = "LogMultiSelectDataEntry";

    final ArrayList<ViewHolder> mViewholderList = new ArrayList<>();

    // View & String that will hold reminder time & description
    TextView mReminderText = null;
    String mReminderDesc = ""; //<-- used in date/time picker

    GetReminderTimeTaskData mRemData;

    // task references - needed to kill tasks on Fragment Destroy
    private GetReminderTimeTask mTaskId = null;
    private static final int TASK_ID = 0;

    public static LogMultiSelectDataEntry newInstance(LogMultiSelectDialogData aData) {
        LogMultiSelectDataEntry frag = new LogMultiSelectDataEntry();
        Bundle args = new Bundle();
        args.putString("title", aData.getTitle());
        args.putLong("hiveid", aData.getHiveID());
        args.putStringArray("elems", aData.getElems());
        args.putString("checkedset", aData.getCheckedSet());
        args.putString("tag", aData.getTag());
        args.putLong("reminderMillis", aData.getReminderMillis());
        args.putBoolean("hasOther", aData.hasOther());
        args.putBoolean("hasReminder", aData.hasReminder());
        args.putBoolean("isMultiselect", aData.isMultiselect());
        args.putBoolean("hasRmndrDesc", aData.hasRmndrDesc());
        args.putString("rmndrDesc", aData.getRmndrDesc());
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // get the Dialog Layout
        View view = getActivity().getLayoutInflater().inflate(R.layout.scb_listview4, null);

        // title
        final TextView title = (TextView)view.findViewById(R.id.titleScb);
        title.setText(getArguments().getString("title"));

        // and the LinearLayout inside that Dialog that is functioning as the vertical list
        ViewGroup llItems = (ViewGroup)view.findViewById(R.id.linearLayoutScb);

        // load the "list"
        for (String s : getArguments().getStringArray("elems")) {
            ViewHolder holder = new ViewHolder();

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

            llItems.addView(item);

            mViewholderList.add(holder);

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
                setListenerForNonMultiselect(holder.cb, mViewholderList);
            }
        }

        // Conditionally add the "Other" EditText at the end of the list
        if (getArguments().getBoolean("hasOther")) {
            ViewHolder holder = new ViewHolder();

            View item = getActivity().getLayoutInflater().inflate(R.layout.scb_item_other, llItems,
                    false);

            holder.tv = (TextView)item.findViewById(et);
            holder.cb = (SmoothCheckBox)item.findViewById(R.id.scb);
            holder.tv.setTag(holder.cb);

            // if we only want numeric data => make sure that's what we get
            if (getArguments().getBoolean("isOtherNum")) {
                holder.tv.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }

            // Check to see if we need to populate w/ user entered value
            fillOther(holder);

            llItems.addView(item);

            mViewholderList.add(holder);

            //Checkbox listener - only do if we are NOT multiselect
            if (!getArguments().getBoolean("isMultiselect")) {
                setListenerForNonMultiselect(holder.cb, mViewholderList);
            }
        }

        // Conditionally add reminder (will always only be at most 1)
        if (getArguments().getBoolean("hasReminder")) {
            // get the reminder item view
            final View reminderInclude = view.findViewById(R.id.reminderItem);
            reminderInclude.setVisibility(View.VISIBLE);

            // labels for showing reminder time; be sure to init the tag as this is what goes
            //   into the DB
            mReminderText =
                    (TextView)reminderInclude.findViewById(R.id.textViewDialogRmndr);
            final Button reminderButton =
                    (Button) reminderInclude.findViewById(R.id.buttonDialogRmndr);

            // If we have a time --> use it...
            //  ...it could be -2 indicating that an UNSET operation has occurred...
            if (getArguments().getLong("reminderMillis") == -2) {
                mReminderText.setText(R.string.no_reminder_set);
                // don't forget to set the tag
                mReminderText.setTag(getArguments().getLong("reminderMillis"));
                mReminderDesc = getArguments().getString("rmndrDesc");
            }
            // ...if it's not -1 => it should be a real time...
            else if (getArguments().getLong("reminderMillis") != -1) {
                calendar.setTimeInMillis(getArguments().getLong("reminderMillis"));
                String droneDate = dateFormat.format(calendar.getTime());
                String droneTime = timeFormat.format(calendar.getTime());
                String droneDateTime = droneDate + ' ' + droneTime;
                mReminderText.setText(droneDateTime);
                // don't forget to set the tag
                mReminderText.setTag(getArguments().getLong("reminderMillis"));
                mReminderDesc = getArguments().getString("rmndrDesc");
            }
            else {
                // ...otherwise => read Notification table
                //disable the button until task is thru
                reminderButton.setEnabled(false);

                //setup and execute task
                mTaskId = new MyGetReminderTimeTask(
                        new GetReminderTimeTaskData(reminderButton, mReminderText,
                                NotificationType.notificationTypeLookup.get(
                                        getArguments().getString("tag")),
                                getArguments().getLong("hiveid"), TASK_ID,
                                calendar, dateFormat, timeFormat),
                        getActivity());
                // All AsynchTasks executed serially on same background Thread
                mTaskId.execute();
                // Each AsyncTask executes on its own Thread
                //mTaskDrone.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            reminderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReminderPressed(mReminderText);
                }
            });
        }

        return view;
    }

    @Override
    public boolean onBackPressed() {
        Log.d(TAG, "Back button clicked...save everything");

        boolean reply = false;

        // get the checked stuff
        List<String> resultList = new ArrayList<>();
        for (ViewHolder checkEdit : mViewholderList) {
            if (checkEdit.cb.isChecked()) {
                resultList.add(checkEdit.tv.getText().toString());
            }
        }

        // don't forget to get the reminder time from the TextEdit tag
        if (mListener != null) {
            String[] result = new String[resultList.size()];
            result = resultList.toArray(result);
            //  BUT ONLY if we're doing reminders
            long resultRemMillis = -1;
            if ((mReminderText != null) && (mReminderText.getTag() != null)) {
                resultRemMillis = (long)mReminderText.getTag();
            }

            // AND the reminder description - should always be something there as there is a default
            mListener.onLogDataEntryOK(result, resultRemMillis, mReminderDesc,
                    getArguments().getString("tag"));

            reply = true;
        }

        return reply;
    }

    @Override
    public void onDestroy() {
        if (mTaskId != null) {
            mTaskId.cancel(false);
        }
        super.onDestroy();
    }

    /**
     * This is how we fill the user enterable EditText at the end if it is necessary.
     *  Check the last entry in the "checked" set -> if it's <> to anything in the
     *  "elems" => it was entered by the user & needs to be presented here
     */
    private void fillOther(ViewHolder aHolder) {
        Log.d(TAG, "checking for user entered value");

        //easier to set the "checkedset"
        if (getArguments().getString("checkedset") != null &&
                getArguments().getString("checkedset").length() != 0) {
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

    /**
     * throw up the DateTime picker to get a Reminder date/time
     */
    private void onReminderPressed(final TextView timeLbl) {
        Log.d(TAG, "onReminderPressed");

        final View dialogView = View.inflate(getActivity(), R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        // optionally allow user to enter description for reminder
        //  Display desc possibly previously entered by user
        final EditText remDescEdit;
        if (getArguments().getBoolean("hasRmndrDesc")) {
            View linLayRemDesc = dialogView.findViewById(R.id.linearLayoutReminderDesc);
            remDescEdit = (EditText)linLayRemDesc.findViewById(R.id.editTextReminderDesc);
            remDescEdit.setText(mReminderDesc);

            linLayRemDesc.setVisibility(View.VISIBLE);
        }

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker datePicker = (DatePicker)dialogView.findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker)dialogView.findViewById(R.id.time_picker);

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());

                long time = calendar.getTimeInMillis();
                Log.d(TAG, "Time picked: " + time);

                // label has a human readable value; tag has millis value for DB
                String timeString = dateFormat.format(calendar.getTime()) + ' ' +
                        timeFormat.format(calendar.getTime());
                timeLbl.setText(timeString);
                timeLbl.setTag(time);

                // optionally allow user to enter description for reminder
                //  Set desc possibly entered by user
                if (getArguments().getBoolean("hasRmndrDesc")) {
                    mReminderDesc = remDescEdit.getText().toString();
                }

                alertDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.date_time_unset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Time UNpicked: ");

                timeLbl.setText(R.string.no_reminder_set);
                // IMPORTANT: -2 indicator of occurrence of UNSET operation
                timeLbl.setTag((long)-2);

                alertDialog.dismiss();
            }
        });

        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    private class ViewHolder {
        SmoothCheckBox cb;
        TextView tv;
    }

    /** subclass of the GetReminderTimeTask
     */
    private class MyGetReminderTimeTask extends GetReminderTimeTask {

        MyGetReminderTimeTask(GetReminderTimeTaskData aData, Context aCtx) {
            super(aData, aCtx);
        }

        @Override
        protected void nullifyTaskRef(int taskRef) {
            Log.d(TAG, "in overridden GetReminderTimeTask.nullifyTaskRef(): taskRef:" + taskRef);
            switch (taskRef) {
                case TASK_ID:
                    mTaskId = null;
                    break;
            }
        }

        @Override
        protected void setRemDesc(String aDesc) {
            mReminderDesc = aDesc;
        }
    }
}
