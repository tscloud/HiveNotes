package net.tscloud.hivenotes;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A placeholder fragment containing a simple view.
 */
public class LogDateListFragment extends Fragment {

    public static final String TAG = "LogDateListFragment";

    private long mHiveID;

    private OnLogDateListFragmentListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param logDate Parameter 1.
     * @return A new instance of fragment LogDateListFragment.
     */
    public static LogDateListFragment newInstance(long logDate) {
        LogDateListFragment fragment = new LogDateListFragment();
        Bundle args = new Bundle();
        args.putLong("hiveID", logDate);
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
            mHiveID = getArguments().getLong("hiveID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_log_date_list, container, false);

        // set button listener
        final Button b1 = (Button)v.findViewById(R.id.btnFragLogDateList);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed();
            }
        });

        return v;
    }

    public void onButtonPressed() {
        Log.d(TAG, "in onButtonPressed");

        mListener.onLogDateListFragmentInteraction(System.currentTimeMillis());
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
    }

    public interface OnLogDateListFragmentListener {
        public void onLogDateListFragmentInteraction(long logDate);
    }
}
