package net.tscloud.hivenotes;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import net.tscloud.hivenotes.db.Hive;
import net.tscloud.hivenotes.helper.HiveListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * Activities containing this fragment MUST implement the OnEditHiveListFragmentInteractionListener
 * interface.
 */
public class EditHiveListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String TAG = "EditHiveListFragment";

    // the fragment initialization parameters
    private static final String APIARY_KEY = "apiaryKey";
    // and instance var of same - needed?
    private long mApiaryKey;
    private List<Hive> mHiveList = null;

    private String emptyTextMsg = "No hives yet";

    private OnEditHiveListFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    public static EditHiveListFragment newInstance(long apiaryKey) {
        EditHiveListFragment fragment = new EditHiveListFragment();
        Bundle args = new Bundle();

        args.putLong(APIARY_KEY, apiaryKey);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EditHiveListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mApiaryKey = getArguments().getLong(APIARY_KEY);
        }

        // Get the list of hives
        // call Activity method to get Hive list
        if (mListener != null) {
            // Note: mHiveList should never be null as HiveDAO will return empty List
            // necessary to unconditionally set reread=true?
            mHiveList = mListener.deliverHiveList(mApiaryKey, true);
        }
        else {
            emptyTextMsg = "no connection to Activity";
            mHiveList = new ArrayList<Hive>();
        }

        // --Create the Adapter--
        //mAdapter = new ArrayAdapter<Hive>(getActivity(),
        //        android.R.layout.simple_list_item_1, android.R.id.text1, mHiveList);
        //mAdapter = new ArrayAdapter<Hive>(getActivity(),
        //        R.layout.hive_edit_button, R.id.hiveEditTextView, mHiveList);
        mAdapter = new HiveListAdapter(getActivity(), mHiveList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_hive, container, false);

        // Set-up toolbar
        //TextView textToolbarHome = (TextView)v.findViewById(R.id.toolbarHomeText);
        ((TextView)view.findViewById(R.id.toolbarHomeText)).setText(R.string.new_hive_string);
        ((TextView)view.findViewById(R.id.toolbarLogText)).setText(R.string.update_apiary_string);
        ((TextView)view.findViewById(R.id.toolbarAnalysisText)).setText(R.string.weather_string);
        ((TextView)view.findViewById(R.id.toolbarReminderText)).setText(R.string.not_applicable);
        ((TextView)view.findViewById(R.id.toolbarPicsText)).setText(R.string.not_applicable);

        // --Set the Adapter--
        mListView = (AbsListView) view.findViewById(android.R.id.list); // get the ListView
        mListView.setEmptyView(view.findViewById(android.R.id.empty)); // when there is nothing to show
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter); // set the ListView's Adapter

        // Set OnItemClickListener so we can be notified on item clicks
        //  disable this when we don't want to click the ListItem, i.e. there are buttons inside it
        mListView.setOnItemClickListener(this);

        // Set text of Empty View
        if (mHiveList.isEmpty()) {
            setEmptyText(emptyTextMsg);
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnEditHiveListFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEditHiveListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {

            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onEditHiveListFragmentCreateHive(mHiveList.get(position).getId());
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnEditHiveListFragmentInteractionListener {
        // For general interaction - really just the return to the Activity
        void onEditHiveListFragmentCreateHive(long hiveID);
        void onEditHiveListFragmentUpdateApiary(long apiaryID);
        void onEditHiveListFragmentWeather(long apiaryID);

        // For getting Hive data
        List<Hive> deliverHiveList(long anApiaryKey, boolean reread);
    }

}
