package net.tscloud.hivenotes;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String APIARY_KEY = "param1";

    private long mApiaryKey;

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

    private static final String TAG = "EditHiveListFragment";

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
        // Do it local
        /*
        Log.d(TAG, "reading Hive table");
        HiveDAO hiveDAO = new HiveDAO(getActivity());
        theHiveList = hiveDAO.getHiveList(mApiaryKey);
        hiveDAO.close();
        */

        // Do it remote
        if (mListener != null) {
            // Note: theHiveList should never be null as HiveDAO will return empty List
            // necessary to unconditionally set reread=true?
            theHiveList = mListener.deliverHiveList(mApiaryKey, true);
        }
        else {
            emptyTextMsg = "no connection to Activity";
            theHiveList = new ArrayList<Hive>();
        }

        //mAdapter = new ArrayAdapter<Hive>(getActivity(),
        //        android.R.layout.simple_list_item_1, android.R.id.text1, theHiveList);
        mAdapter = new ArrayAdapter<Hive>(getActivity(),
                R.layout.hive_edit_button, R.id.hiveEditTextView, theHiveList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_hive, container, false);

        // must qualify b/c button inside include
        View layoutCreateHive = view.findViewById(R.id.newHiveListButton);
        final Button btnCreateHive = (Button)layoutCreateHive.findViewById(R.id.hiveNoteButtton);
        View layoutUpdateApiary = view.findViewById(R.id.updateApiaryListButton);
        final Button btnUpdateApiary = (Button)layoutUpdateApiary.findViewById(R.id.hiveNoteButtton);

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
        btnCreateHive.setText(getResources().getString(R.string.create_hive_string));
        btnUpdateApiary.setText(getResources().getString(R.string.update_apiary_string));

        // set button listener
        btnCreateHive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateHiveButtonPressed();
            }
        });

        btnUpdateApiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateApiaryButtonPressed();
            }
        });

        return view;
    }

    public void onCreateHiveButtonPressed() {
        if (mListener != null) {
            // means we want to make a new Hive
            mListener.onEditHiveListFragmentInteraction(mApiaryKey, -1, false);
        }
    }

    public void onUpdateApiaryButtonPressed() {
        if (mListener != null) {
            // means we want to make a new Hive
            mListener.onEditHiveListFragmentInteraction(mApiaryKey, -1, true);
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
            mListener.onEditHiveListFragmentInteraction(mApiaryKey, theHiveList.get(position).getId(), false);
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
        public void onEditHiveListFragmentInteraction(long apiaryID, long hiveID, boolean updateApiary);

        // For getting Hive data
        public List<Hive> deliverHiveList(long anApiaryKey, boolean reread);
    }

}
