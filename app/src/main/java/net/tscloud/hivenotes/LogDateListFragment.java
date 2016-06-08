package net.tscloud.hivenotes;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import net.tscloud.hivenotes.db.LogDateDAO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class LogDateListFragment extends Fragment {

    public static final String TAG = "LogDateListFragment";

    // need Hive to read each Log table
    private long mHiveID;

    // reference to Activity that should have started me
    private OnLogDateListFragmentListener mListener;

    // task references - needed to kill tasks on Fragment Destroy
    private GetDatesTask mTask = null;

    // the RecyclerView
    private RecyclerView mLogDateRecyclerView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param aHiveKey Parameter 1.
     * @return A new instance of fragment LogDateListFragment.
     */
    public static LogDateListFragment newInstance(long aHiveKey) {
        LogDateListFragment fragment = new LogDateListFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.INTENT_HIVE_KEY, aHiveKey);
        fragment.setArguments(args);
        return fragment;
    }

    public LogDateListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // save off arguments
        if (getArguments() != null) {
            mHiveID = getArguments().getLong(MainActivity.INTENT_HIVE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_log_date_list, container, false);

        mLogDateRecyclerView = (RecyclerView) v.findViewById(R.id.logdate_recycler_view);
        mLogDateRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /** AsyncTask to get the date list - this task will be the only one to Load
         *   the list so no need to check for previous data, etc.
         */
        mTask = new GetDatesTask(getActivity());

        // set button listener
        //final Button b1 = (Button)v.findViewById(R.id.btnFragLogDateList);

        //b1.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        onButtonPressed();
        //    }
        //});

        return v;
    }

    //public void onButtonPressed() {
    //    Log.d(TAG, "in onButtonPressed");

    //    Calendar calendar = new GregorianCalendar(2013,0,31);

    //    mListener.onLogDateListFragmentInteraction(calendar.getTimeInMillis());
    //}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLogDateListFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLogDateListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save off values potentially entered from screen
        super.onSaveInstanceState(outState);
        ((LogDateExpandableAdapter) mLogDateRecyclerView.getAdapter()).onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (mTask != null) {
            mTask.cancel(false);
        }

        super.onDestroy();
    }

    public interface OnLogDateListFragmentListener {
        public void onLogDateListFragmentInteraction(long logDate);
    }

    /** Stuff needed by expandablerecyclerview
     */
    // Parent view holder
    public class LogDateParentViewHolder extends ParentViewHolder {

        public TextView mLogDateTitleTextView;
        public ImageButton mParentDropDownArrow;

        public LogDateParentViewHolder(View itemView) {
            super(itemView);

            mLogDateTitleTextView = (TextView) itemView.findViewById(R.id.parent_list_item_logdate_title_text_view);
            mParentDropDownArrow = (ImageButton) itemView.findViewById(R.id.parent_list_item_expand_arrow);
        }
    }

    // Child view holder
    public class LogDateChildViewHolder extends ChildViewHolder {

        public TextView mLogDateDateText;

        public LogDateChildViewHolder(View itemView) {
            super(itemView);

            mLogDateDateText = (TextView) itemView.findViewById(R.id.child_list_item_logdate_date_text_view);
        }
    }

    // Parent object
    public class LogDateParent implements ParentListItem {

        private Date mDate;
        private List<Object> mChildrenList;

        public LogDateParent() {
        }

        @Override
        public List<Object> getChildItemList() {
            return mChildrenList;
        }

        public void setChildItemList(List<Object> list) {
            mChildrenList = list;
        }

        public Date getDate() {
            return mDate;
        }

        public void setDate(Date mDate) {
            this.mDate = mDate;
        }

        @Override
        public boolean isInitiallyExpanded() {
            return false;
        }
    }

    // Child object
    public class LogDateChild {

        private Date mDate;

        public LogDateChild(Date date) {
            mDate = date;
        }

        // Getter and setter methods
        public Date getDate() {
            return mDate;
        }

        public void setDate(Date mDate) {
            this.mDate = mDate;
        }

    }

    // Adapter
    public class LogDateExpandableAdapter extends ExpandableRecyclerAdapter<LogDateParentViewHolder,
                LogDateChildViewHolder> {

        private LayoutInflater mInflater;

        public LogDateExpandableAdapter(Context context, List<ParentListItem> itemList) {
            super(itemList);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public LogDateParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
            View view = mInflater.inflate(R.layout.list_item_logdate_parent, viewGroup, false);
            return new LogDateParentViewHolder(view);
        }

        @Override
        public LogDateChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
            View view = mInflater.inflate(R.layout.list_item_logdate_child, viewGroup, false);
            return new LogDateChildViewHolder(view);
        }

        @Override
        public void onBindParentViewHolder(LogDateParentViewHolder logDateParentViewHolder, int i,
                                           ParentListItem parentListItem) {
            LogDateParent logDateParent = (LogDateParent) parentListItem;
            logDateParentViewHolder.mLogDateTitleTextView.setText(logDateParent.getDate().toString());
        }

        @Override
        public void onBindChildViewHolder(LogDateChildViewHolder logDateChildViewHolder, int i,
                                          Object childListItem) {
            LogDateChild logDateChild = (LogDateChild) childListItem;
            logDateChildViewHolder.mLogDateDateText.setText(logDateChild.getDate().toString());
        }
    }

    /** get the list of dates we will present to the user
     */
    public class GetDatesTask extends AsyncTask<Void, Void, List<Long>> {

        public static final String TAG = "GetDatesTask";

        private Context ctx;

        public GetDatesTask(Context aCtx) {
            ctx = aCtx;
            Log.d(TAG, "GetDatesTask("+ Thread.currentThread().getId() + ") : constructor");
        }

        @Override
        protected List<Long> doInBackground(Void... unused) {
            Log.d(TAG, "GetDatesTask("+ Thread.currentThread().getId() + ") : doInBackground");

            LogDateDAO logDateDAO = new LogDateDAO(ctx);
            List<Long> logDateList = logDateDAO.getAllVisitDates(mHiveID);

            return(logDateList);
        }

        @Override
        protected void onPostExecute(List<Long> dateArray) {
            Log.d(TAG, "UpdateDBTask("+ Thread.currentThread().getId() + ") : onPostExecute");

            //Toast.makeText(getApplicationContext(), "DB query complete", Toast.LENGTH_SHORT).show();

            LogDateExpandableAdapter logDateExpandableAdapter =
                new LogDateExpandableAdapter(getActivity(), getLogDateParents(dateArray));
            //logDateExpandableAdapter.setCustomParentAnimationViewId(R.id.parent_list_item_expand_arrow);
            //logDateExpandableAdapter.setParentClickableViewAnimationDefaultDuration();
            //logDateExpandableAdapter.setParentAndIconExpandOnClick(true);

            //logDateExpandableAdapter.onRestoreInstanceState(savedInstanceState);

            mLogDateRecyclerView.setAdapter(logDateExpandableAdapter);

            // all we need to do is nullify the Task reference
            mTask = null;
        }

        private ArrayList<ParentListItem> getLogDateParents(List<Long> aLogDateList) {
            // For each Date (Child object) => determine year/month, if
            //  corresponding year/month Parent object does not exist => create
            //  it and place this Child under it
            ArrayList<ParentListItem> reply = null;

            for (long logDate : aLogDateList) {

            }

            return reply;
        }
    }
}
