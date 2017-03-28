package net.tscloud.hivenotes;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import net.tscloud.hivenotes.db.LogDateDAO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

        // Was in AsyncTask
        LogDateDAO logDateDAO = new LogDateDAO(getActivity());

        mLogDateRecyclerView = (RecyclerView) v.findViewById(R.id.logdate_recycler_view);
        mLogDateRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // create adapter w/ empty itemList
        //LogDateExpandableAdapter logDateExpandableAdapter =
        //        new LogDateExpandableAdapter(getActivity(), new ArrayList<ParentListItem>());

        // Was in AsyncTask
        LogDateExpandableAdapter logDateExpandableAdapter =
                new LogDateExpandableAdapter(getActivity(),
                        getLogDateParents(logDateDAO.getAllVisitDates(mHiveID)));

        logDateExpandableAdapter.onRestoreInstanceState(savedInstanceState);

        mLogDateRecyclerView.setAdapter(logDateExpandableAdapter);

        /** AsyncTask to get the date list - this task will be the only one to Load
         *   the list so no need to check for previous data, etc.
         */
        //mTask = new GetDatesTask(getActivity());
        //mTask.execute();

        return v;
    }

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
        ((LogDateExpandableAdapter)mLogDateRecyclerView.getAdapter()).onSaveInstanceState(outState);
    }

    // This is an Activity, not Fragment, method
    //@Override
    //protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    //    super.onRestoreInstanceState(savedInstanceState);
    //    ((LogDateExpandableAdapter)mLogDateRecyclerView.getAdapter()).onRestoreInstanceState(savedInstanceState);
    //}

    @Override
    public void onDestroy() {
        if (mTask != null) {
            mTask.cancel(false);
        }

        super.onDestroy();
    }

    interface OnLogDateListFragmentListener {
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

            mLogDateTitleTextView =
                (TextView)itemView.findViewById(R.id.parent_list_item_logdate_title_text_view);
            mParentDropDownArrow =
                (ImageButton)itemView.findViewById(R.id.parent_list_item_expand_arrow);
        }
    }

    // Child view holder
    public class LogDateChildViewHolder extends ChildViewHolder implements View.OnClickListener {

        public TextView mLogDateDateText;

        public LogDateChildViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mLogDateDateText = (TextView) itemView.findViewById(R.id.child_list_item_logdate_date_text_view);
        }

        @Override
        public void onClick(View v) {
            // Pass the Date in millis back to the calling Activity - And we're done!
            mListener.onLogDateListFragmentInteraction((long)mLogDateDateText.getTag());
        }
    }

    // Parent object
    public class LogDateParent implements ParentListItem {

        private String mMonthYear;
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

        // Getter and setter methods
        public String getMonthYear() {
            return mMonthYear;
        }

        public void setMonthYear(String aDate) {
            mMonthYear = aDate;
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
    public class LogDateExpandableAdapter extends
                ExpandableRecyclerAdapter<LogDateParentViewHolder, LogDateChildViewHolder> {

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
            logDateParentViewHolder.mLogDateTitleTextView.setText(logDateParent.getMonthYear());
        }

        @Override
        public void onBindChildViewHolder(LogDateChildViewHolder logDateChildViewHolder, int i,
                                          Object childListItem) {
            LogDateChild logDateChild = (LogDateChild) childListItem;
            logDateChildViewHolder.mLogDateDateText.setText(logDateChild.getDate().toString());
            // Set the Tag w/ the Date in millis -> we will pass this back to our calling Activity
            logDateChildViewHolder.mLogDateDateText.setTag(logDateChild.getDate().getTime());
        }
    }

    private ArrayList<ParentListItem> getLogDateParents(List<Long> aLogDateList) {
        /**
         * For each Date (Child object) => determine year/month, if
         *  corresponding year/month Parent object does not exist => create
         *  it and place this Child under it
         */
        //parent list that will returned
        ArrayList<ParentListItem> parentList = new ArrayList<>();
        //initial current parent, init String value to something that will NOT match
        LogDateParent currentParent = new LogDateParent();
        currentParent.setMonthYear("bogus");

        //date formatters
        SimpleDateFormat fMonth = new SimpleDateFormat("MMMM");
        SimpleDateFormat fYear = new SimpleDateFormat("yyyy");

        for (long logDate : aLogDateList) {
            if (currentParent.getMonthYear().contains(fMonth.format(logDate)) &&
                    currentParent.getMonthYear().contains(fYear.format(logDate))) {

                // We have a parent to stick this date under
                currentParent.getChildItemList().add(new LogDateChild(new Date(logDate)));
            }
            else {
                // We do not have a parent to put this date under => make a
                //  parent and stick this date under it & add it to the big parent list
                LogDateParent newParent = new LogDateParent();
                newParent.setMonthYear(fMonth.format(logDate) + " " + fYear.format(logDate));

                ArrayList<Object> childList = new ArrayList<>();
                childList.add(new LogDateChild(new Date(logDate)));
                newParent.setChildItemList(childList);

                parentList.add(newParent);

                currentParent = newParent;
            }
        }

        return parentList;
    }

    /** get the list of dates we will present to the user
     */
    public class GetDatesTask extends AsyncTask<Void, Void, List<ParentListItem>> {

        public static final String TAG = "GetDatesTask";

        private Context ctx;

        public GetDatesTask(Context aCtx) {
            ctx = aCtx;
            Log.d(TAG, "GetDatesTask("+ Thread.currentThread().getId() + ") : constructor");
        }

        @Override
        protected List<ParentListItem> doInBackground(Void... unused) {
            Log.d(TAG, "GetDatesTask("+ Thread.currentThread().getId() + ") : doInBackground");

            LogDateDAO logDateDAO = new LogDateDAO(ctx);

            return(getLogDateParents(logDateDAO.getAllVisitDates(mHiveID)));
        }

        @Override
        protected void onPostExecute(List<ParentListItem> dateArray) {
            Log.d(TAG, "GetDatesTask("+ Thread.currentThread().getId() + ") : onPostExecute");

            //Toast.makeText(getApplicationContext(), "DB query complete", Toast.LENGTH_SHORT).show();

            LogDateExpandableAdapter logDateExpandableAdapter =
                new LogDateExpandableAdapter(getActivity(), dateArray);
            //logDateExpandableAdapter.setCustomParentAnimationViewId(R.id.parent_list_item_expand_arrow);
            //logDateExpandableAdapter.setParentClickableViewAnimationDefaultDuration();
            //logDateExpandableAdapter.setParentAndIconExpandOnClick(true);

            mLogDateRecyclerView.setAdapter(logDateExpandableAdapter);

            // all we need to do is nullify the Task reference
            mTask = null;
        }
    }
}
