package net.tscloud.hivenotes.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import net.tscloud.hivenotes.LogHiveHealthFragment;
import net.tscloud.hivenotes.R;
import net.tscloud.hivenotes.db.NotificationType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.refactor.library.SmoothCheckBox;

/**
 * Created by tscloud on 12/19/16.
 */

public class LogMultiSelectDialog extends LogSuperDialog {

    public static final String TAG = "LogMultiSelectDialog";

    // reference to Activity that should have started me
    private onLogMultiSelectDialogInteractionListener mListener;

    // the TextEdit that ill hold reminder time
    TextView mReminderText = null;

    // task references - needed to kill tasks on Fragment Destroy
    private GetReminderTimeTask mTaskId = null;
    private static final int TASK_ID = 0;

    // time/date formatters
    private static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG,
            Locale.getDefault());
    private static final String TIME_PATTERN = "HH:mm";
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_PATTERN,
            Locale.getDefault());
    private final Calendar calendar = Calendar.getInstance();

    public static LogMultiSelectDialog newInstance(LogMultiSelectDialogData aData) {
        LogMultiSelectDialog frag = new LogMultiSelectDialog();
        Bundle args = new Bundle();
        args.putString("title", aData.getTitle());
        args.putLong("hiveid", aData.getHiveID());
        args.putStringArray("elems", aData.getElems());
        args.putString("checkedset", aData.getCheckedSet());
        args.putString("tag", aData.getTag());
        args.putLong("reminderMillis", aData.getReminderMillis());
        args.putBoolean("hasOther", aData.isHasOther());
        args.putBoolean("hasReminder", aData.isHasReminder());
        args.putBoolean("isMultiselect", aData.isMultiselect());
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final ArrayList<ViewHolder> viewholderList = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // get the Dialog Layout
        View view = getActivity().getLayoutInflater().inflate(R.layout.scb_listview4, null);

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

            View item = getActivity().getLayoutInflater().inflate(R.layout.scb_item_other, llItems,
                    false);

            holder.tv = (TextView)item.findViewById(R.id.et);
            holder.cb = (SmoothCheckBox)item.findViewById(R.id.scb);
            holder.tv.setTag(holder.cb);

            // Check to see if we need to populate w/ user entered value
            fillOther(holder);

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
            final View reminderInclude = view.findViewById(R.id.reminderItem);
            reminderInclude.setVisibility(View.VISIBLE);

            // labels for showing reminder time; be sure to init the tag as this is what goes
            //   into the DB
            mReminderText =
                    (TextView)reminderInclude.findViewById(R.id.textViewDialogRmndr);
            final Button reminderButton =
                    (Button) reminderInclude.findViewById(R.id.buttonDialogRmndr);

            // If we have a time --> use it...
            //  it could be -2 indicating that an UNSET operation has occurred
            if (getArguments().getLong("reminderMillis") == -2) {
                mReminderText.setText(R.string.no_reminder_set);
                // don't forget to set the tag
                mReminderText.setTag(getArguments().getLong("reminderMillis"));
            }
            else if (getArguments().getLong("reminderMillis") != -1) {
                calendar.setTimeInMillis(getArguments().getLong("reminderMillis"));
                String droneDate = dateFormat.format(calendar.getTime());
                String droneTime = timeFormat.format(calendar.getTime());
                String droneDateTime = droneDate + ' ' + droneTime;
                mReminderText.setText(droneDateTime);
                // don't forget to set the tag
                mReminderText.setTag(getArguments().getLong("reminderMillis"));
            }
            else {
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

        // OK/Cancel button Listeners
        final Button dialogOKBtn = (Button)view.findViewById(R.id.buttonOKScb);
        dialogOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "OK button clicked");

                // get the checked stuff
                List<String> resultList = new ArrayList<>();
                for (ViewHolder checkEdit:viewholderList) {
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
                    mListener.onLogMultiSelectDialogOK(result, resultRemMillis,
                            getArguments().getString("tag"));
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

        String reply = null;

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

    class ViewHolder {
        SmoothCheckBox cb;
        TextView tv;
    }

    /** subclass of the GetReminderTimeTask
     */
    class MyGetReminderTimeTask extends GetReminderTimeTask {

        MyGetReminderTimeTask(GetReminderTimeTaskData aData, Context aCtx) {
            super(aData, aCtx);
        }

        protected void nullifyTaskRef(int taskRef) {
            Log.d(TAG, "in overridden GetReminderTimeTask.nullifyTaskRef(): taskRef:" + taskRef);
            switch (taskRef) {
                case TASK_ID:
                    mTaskId = null;
                    break;
            }
        }
    }
}
