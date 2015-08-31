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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import net.tscloud.hivenotes.db.Hive;
import net.tscloud.hivenotes.db.HiveDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnEditHiveListFragmentInteractionListener}
 * interface.
 */
public class EditHiveListFragment extends Fragment implements AbsListView.OnItemClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private long mApiaryKey;
    private String mParam2;

    private List<Hive> theHiveList = null;

    String emptyTextMsg = "No hives yet";

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

    // logcat filter
    private static final String TAG = "EditHiveListFragment";

    public static EditHiveListFragment newInstance(long apiaryKey) {
        EditHiveListFragment fragment = new EditHiveListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, apiaryKey);
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
            mApiaryKey = getArguments().getLong(ARG_PARAM1);
        }

        // Get the list of hives
        // Do it local
        /*
        Log.d(TAG, "reading Hive table");
        HiveDAO hiveDAO = new HiveDAO(getActivity());
        theHiveList = hiveDAO.getHiveList(mApiaryKey);
        hiveDAO.close();
        */

        // Do it remote
        if (mListener != null) {
            // Note: theHiveList should ever be null as HiveDAO will return empty List
            theHiveList = mListener.getTheHiveList(mApiaryKey);
        }
        else {
            emptyTextMsg = "no connection to Activity";
            theHiveList = new ArrayList<Hive>();
        }

        mAdapter = new ArrayAdapter<Hive>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, theHiveList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_hive, container, false);

        final Button btnEditHive = (Button)view.findViewById(R.id.hiveNoteButtton);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setEmptyView(view.findViewById(android.R.id.empty));
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        // Set Empty View
        if (theHiveList.isEmpty()) {
            setEmptyText(emptyTextMsg);
        }

        // set button text
        btnEditHive.setText(getResources().getString(R.string.create_hive_string));

        // set button listener
        btnEditHive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditHiveButtonPressed();
            }
        });
        return view;
    }

    public void onEditHiveButtonPressed() {
        if (mListener != null) {
            // means we want to make a new Hive
            mListener.onEditHiveListFragmentInteraction(-1);
        }
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
            mListener.onEditHiveListFragmentInteraction(theHiveList.get(position).getId());
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnEditHiveListFragmentInteractionListener {
        // For general interaction - really just the return to the Activity
        public void onEditHiveListFragmentInteraction(long id);

        // For getting Hive data
        public List<Hive> getTheHiveList(long anApiaryKey);
    }

}
