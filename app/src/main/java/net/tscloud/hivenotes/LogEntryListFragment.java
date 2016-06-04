package net.tscloud.hivenotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import net.tscloud.hivenotes.helper.LogEntryNames;
import net.tscloud.hivenotes.helper.LogListAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * A list fragment representing a list of LogEntries. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link LogEntryDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class LogEntryListFragment extends ListFragment {

    public static final String TAG = "LogEntryListFragment";

    private static final int LOG_DATE_REQ_CODE = 6;

    // time/date formatters
    private static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
    private static final String TIME_PATTERN = "HH:mm";
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());
    private final Calendar calendar = Calendar.getInstance();

    // We'll need to set this when we get a date back from LogDateListActivity
    private TextView mTextViewLogDate = null;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
        public long getHiveKey();
        public void setActivityLogDate(long aDate);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }

        @Override
        public long getHiveKey() {
            return -1;
        }

        @Override
        public void setActivityLogDate(long data) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LogEntryListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new LogListAdapter(getActivity(), LogEntryNames.ITEMS));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_log_entry_list, container, false);

        // Set up log date text view
        mTextViewLogDate = (TextView)v.findViewById(R.id.textLogDate);

        // Initially set to current time
        long time = setTextViewDate(-1);
        Log.d(TAG, "Set to current Time (initial): " + time);
        //set the activity's LogDate
        mCallbacks.setActivityLogDate(time);

        mTextViewLogDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getActivity(), mTextViewLogDate);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_log_date, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(getActivity(),"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();

                        switch (item.getItemId()) {
                            case R.id.itemEnterDate:
                                // launch date/time picker
                                onReminderPressed(mTextViewLogDate);
                                // IMPORTANT: set the Activity's log date so it can be used thoughout the universe
                                mCallbacks.setActivityLogDate((long)mTextViewLogDate.getTag());
                                break;
                            case R.id.itemSelectDate:
                                // launch LogDateListActivity
                                Intent i = new Intent(getActivity(),LogDateListActivity.class);
                                i.putExtra(MainActivity.INTENT_HIVE_KEY, mCallbacks.getHiveKey());
                                startActivityForResult(i, LOG_DATE_REQ_CODE);
                                break;
                            case R.id.itemSetDateCurrent:
                                // set to current date
                                long time = System.currentTimeMillis();
                                calendar.setTimeInMillis(time);
                                Log.d(TAG, "Set to current Time (menu): " + time);

                                // label has a human readable value; tag has millis value for DB
                                String timeString = dateFormat.format(calendar.getTime()) + ' ' +
                                        timeFormat.format(calendar.getTime());
                                mTextViewLogDate.setText(timeString);
                                mTextViewLogDate.setTag(time);
                                // IMPORTANT: set the Activity's log date so it can be used thoughout the universe
                                mCallbacks.setActivityLogDate((long)mTextViewLogDate.getTag());
                                break;
                        }

                        return true;
                    }
                });

                popup.show();//showing popup menu
           }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Previously we had been setting this whenever it changed
        //mCallbacks.setActivityLogDate((long)mTextViewLogDate.getTag());

        /**
         * Don't like doing this - if the Save btn hit (only know this by id=6) -> we have to reset
         *  the date.  If we don't -> the next log entries will use the date we just used to save a
         *  log entry.  This will result in attempt to read the log w/ the date we just used and we
         *  will get a hit (we just saved an entry w/ this date) and we will be updating that entry
         *  instead of creating a new one.  Also note that the Activity's logDate is NOT set.
         */
        //if (LogEntryNames.ITEMS.get(position).getId().equals("6")) {
        //    long time = setTextViewDate(-1);
        //    Log.d(TAG, "Set to current Time (prior to Save): " + time);
        //}

        /**
         * OR - indicate to the user that if update after saving -> they will we updating the entry
         *  they just created/updated
         */
        if (LogEntryNames.ITEMS.get(position).getId().equals("6")) {
            if (!mTextViewLogDate.getText().toString().contains(getResources().getString(R.string.existing))) {
                mTextViewLogDate.setText(mTextViewLogDate.getText() + getResources().getString(R.string.existing));
            }
        }

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(LogEntryNames.ITEMS.get(position).getId());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    private long setTextViewDate(long aTime) {

        if (aTime == -1) {
            aTime = System.currentTimeMillis();
        }
        // Initially set to current time
        calendar.setTimeInMillis(aTime);
        Log.d(TAG, "Set to current Time: " + aTime);

        // label has a human readable value; tag has millis value for DB
        String timeString = dateFormat.format(calendar.getTime()) + ' ' +
                timeFormat.format(calendar.getTime());
        mTextViewLogDate.setText(timeString);
        mTextViewLogDate.setTag(aTime);

        return aTime;
    }

    public void onReminderPressed(final TextView timeLbl) {
        Log.d(TAG, "onReminderPressed");

        final View dialogView = View.inflate(getActivity(), R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        // set the text of the "unset" button of our standard date/time picker
        Button unsetBtn = (Button)dialogView.findViewById(R.id.date_time_unset);
        unsetBtn.setText(getResources().getString(R.string.set_to_current));

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

                long time = System.currentTimeMillis();
                calendar.setTimeInMillis(time);
                Log.d(TAG, "Set to current Time (Dialog): " + time);

                // label has a human readable value; tag has millis value for DB
                String timeString = dateFormat.format(calendar.getTime()) + ' ' +
                        timeFormat.format(calendar.getTime());
                timeLbl.setText(timeString);
                timeLbl.setTag(time);

                alertDialog.dismiss();
            }
        });

        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == LOG_DATE_REQ_CODE) && (resultCode == Activity.RESULT_OK)) {
            Log.d(TAG, "Returned from requestCode = " + requestCode);

            if (data == null) {
                // can this still happen?
                Log.d(TAG, "No data from LogDateListActivity  - NOOP...!Bad to get here!");
            }
            else {
                if (mTextViewLogDate != null) {
                    long time = data.getLongExtra(LogDateListActivity.INTENT_LOGENTRY_HISTORY_TIME, -1);
                    calendar.setTimeInMillis(time);
                    Log.d(TAG, "Historic Time received: " + time);

                    // label has a human readable value; tag has millis value for DB
                    String timeString = dateFormat.format(calendar.getTime()) + ' ' +
                            timeFormat.format(calendar.getTime());
                    mTextViewLogDate.setText(timeString + getResources().getString(R.string.existing));
                    mTextViewLogDate.setTag(time);

                    // IMPORTANT: set the Activity's log date so it can be used thoughout the universe
                    mCallbacks.setActivityLogDate(time);
                }
            }
        }
    }
}
