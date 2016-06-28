package net.tscloud.hivenotes;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tscloud.hivenotes.db.Apiary;

import java.util.List;


/**
 * A simple Fragment subclass.
 * Activities that contain this fragment must implement the
 * OnHomeFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the HomeFragment#newInstance factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    // the fragment initialization parameters
    private static final String PROFILE_ID = "param1";
    // and instance var of same - needed?
    long mProfileID = -1;
    List<Apiary> mApiaryList = null;

    private OnHomeFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static HomeFragment newInstance(long profileID) {
        Log.d(TAG, "getting newInstance of HomeFragment...profileID: " + profileID);

        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();

        args.putLong(PROFILE_ID, profileID);
        fragment.setArguments(args);

        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProfileID = getArguments().getLong(PROFILE_ID);
        }

        if (mProfileID != -1) {
            if (mListener != null) {
                // read apiary list from Activity
                mApiaryList = mListener.deliverApiaryList(mProfileID);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        // set some text
        final Button btnEditApiary = (Button)v.findViewById(R.id.editApiaryButton);
        final TextView textListApiary = (TextView)v.findViewById(R.id.textCreateEdit);
        final Button btnDropDB = (Button)v.findViewById(R.id.btnDropDB);

        if (mProfileID == -1){
            String s = getResources().getString(R.string.create_profile_string);
            btnEditApiary.setText(s);
            textListApiary.setText(s);
        }
        else {
            String s = getResources().getString(R.string.new_apiary_string);
            btnEditApiary.setText(s);
            textListApiary.setText(s);
        }

        // check for list of apiaries and add TextViews as necessary
        if (!(mApiaryList == null) && !(mApiaryList.isEmpty())){
            ViewGroup layout = (ViewGroup) v.findViewById(R.id.linearLayout1);
            for (Apiary a : mApiaryList){
                TextView tv = new TextView(getActivity());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                tv.setLayoutParams(layoutParams);
                tv.setText(a.getName());
                tv.setTag(a.getId());
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(25);
                layout.addView(tv);
                //add listener
                tv.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        onApiaryPressed((TextView)v);
                    }
                });

            }
            // and set some text
            textListApiary.setText(getResources().getString(R.string.apiary_list_text));
        }

        // set button listener
        btnEditApiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditApiaryButtonPressed();
            }
        });

        // set button listener
        btnDropDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDropDBButtonPressed();
            }
        });

        return v;
    }

    private void onApiaryPressed(TextView apiaryNameTextView) {
        if (mListener != null) {
            mListener.onHomeFragmentInteraction((Long)apiaryNameTextView.getTag(), false);
        }
    }

    private void onEditApiaryButtonPressed() {
        if (mListener != null) {
            mListener.onHomeFragmentInteraction(null, false);
        }
    }

    private void onDropDBButtonPressed() {
        if (mListener != null) {
            mListener.onHomeFragmentInteraction(null, true);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnHomeFragmentInteractionListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHomeFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnHomeFragmentInteractionListener {
        // For general interaction - really just the return to the Activity
        void onHomeFragmentInteraction(Long apiaryId, boolean deleteDB);

        // For getting Apiary data
        List<Apiary> deliverApiaryList(long aProfileID);
    }

}
