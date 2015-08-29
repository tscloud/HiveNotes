package net.tscloud.hivenotes;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
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

import java.util.LinkedHashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnHomeFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private boolean mNewProfile = false;
    private LinkedHashMap<Long, String> mApiaryNameMap = null;

    private OnHomeFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param newProfile Indicates we need to create a Profile.
     * @param apiaryNameMap
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(boolean newProfile, LinkedHashMap<Long, String> apiaryNameMap) {
        Log.d(TAG, "getting newInstance of HomeFragment");

        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();

        args.putBoolean(ARG_PARAM1, newProfile);

        // split the Hashmap of Apiary id -> name => 2 ArrayLists
        if ((apiaryNameMap != null) && !apiaryNameMap.isEmpty()) {
            long[] apiaryIds = new long[apiaryNameMap.size()];
            String[] apiaryNames = new String[apiaryNameMap.size()];
            int i = 0;
            for (long id : apiaryNameMap.keySet()) {
                apiaryIds[i] = id;
                apiaryNames[i] = apiaryNameMap.get(id);
                ++i;
            }

            args.putLongArray(ARG_PARAM2, apiaryIds);
            args.putStringArray(ARG_PARAM3, apiaryNames);
        }

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

            mNewProfile = getArguments().getBoolean(ARG_PARAM1);

            // reconstitute LinkedHashMap
            long[] argApiaryIds = getArguments().getLongArray(ARG_PARAM2);
            String[] argApiaryNames = getArguments().getStringArray(ARG_PARAM3);

            if (!(argApiaryIds == null) && !(argApiaryNames == null)) {
                // this is onCreate so we know we want to make a new LinkedHashMap
                mApiaryNameMap = new LinkedHashMap<>(argApiaryIds.length);

                for (int i = 0; i < argApiaryIds.length; i++) {
                    mApiaryNameMap.put(argApiaryIds[i], argApiaryNames[i]);
                }
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

        if (mNewProfile){
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
        if (!(mApiaryNameMap == null) && !(mApiaryNameMap.isEmpty())){
            ViewGroup layout = (ViewGroup) v.findViewById(R.id.linearLayout1);
            for (Long aId : mApiaryNameMap.keySet()){
                TextView tv = new TextView(getActivity());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                tv.setLayoutParams(layoutParams);
                tv.setText(mApiaryNameMap.get(aId));
                tv.setTag(aId);
                tv.setTextColor(Color.WHITE);
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
                onEdirApiaryButtonPressed(Uri.parse("here I am...from Edit Apiary"));
            }
        });

        // set button listener
        btnDropDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDropDBButtonPressed(Uri.parse("here I am...from Edit Apiary"));
            }
        });

        return v;
    }

    private void onApiaryPressed(TextView apiaryNameTextView) {
        if (mListener != null) {
            mListener.onHomeFragmentInteraction((Long)apiaryNameTextView.getTag(), false);
        }
    }

    public void onEdirApiaryButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onHomeFragmentInteraction(null, false);
        }
    }

    public void onDropDBButtonPressed(Uri uri) {
        // FOR TESTING ONLY
        boolean dbDeleted = getActivity().deleteDatabase("hivenotes_db");

        if (mListener != null) {
            mListener.onHomeFragmentInteraction(null, dbDeleted);
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnHomeFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onHomeFragmentInteraction(Long apiaryId, boolean dbDeleted);
    }

}
