package net.tscloud.hivenotes;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnHomeFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private boolean mNewProfile = false;
    private ArrayList<String> mApiaryNames = null;

    private OnHomeFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param newProfile Indicates we need to create a Profile.
     * @param apiaryNames
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(boolean newProfile, ArrayList<String> apiaryNames) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, newProfile);
        args.putStringArrayList(ARG_PARAM2, apiaryNames);
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
            mApiaryNames = getArguments().getStringArrayList(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        // set some text
        final Button b1 = (Button)v.findViewById(R.id.editApiaryButton);
        final TextView t1 = (TextView)v.findViewById(R.id.textCreateEdit);

        if (mNewProfile){
            String s = getResources().getString(R.string.create_profile_string);
            b1.setText(s);
            t1.setText(s);
        }
        else {
            String s = getResources().getString(R.string.new_apiary_string);
            b1.setText(s);
            t1.setText(s);
        }

        // check for list of apiaries and add TextViews as necessary
        if (!(mApiaryNames == null) && !(mApiaryNames.isEmpty())){
            ViewGroup layout = (ViewGroup) v.findViewById(R.id.linearLayout1);
            for (String aName : mApiaryNames){
                TextView tv = new TextView(getActivity());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                tv.setLayoutParams(layoutParams);
                tv.setText(aName);
                tv.setTextColor(Color.WHITE);
                layout.addView(tv);
                //add listener
                tv.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        onApiaryPressed(((TextView)v).getText());
                    }
                });

            }
            // and set some text
            t1.setText(getResources().getString(R.string.apiary_list_text));
        }

        // set button listener
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(Uri.parse("here I am...from Edit Apiary"));
            }
        });

        return v;
    }

    private void onApiaryPressed(CharSequence apiaryName) {
        if (mListener != null) {
            mListener.onHomeFragmentInteraction(apiaryName.toString());
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onHomeFragmentInteraction(null);
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
        public void onHomeFragmentInteraction(String apiaryName);
    }

}
