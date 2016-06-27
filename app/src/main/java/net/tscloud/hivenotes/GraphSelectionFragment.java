package net.tscloud.hivenotes;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GraphSelectionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GraphSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphSelectionFragment extends Fragment {

    public static final String TAG = "EditApiaryFragment";

    // the fragment initialization parameters
    private static final String APIARY_ID = "param1";
    private static final String HIVE_ID = "param2";
    // and instance var of same - needed?
    private long mApiaryID = -1;
    private long mHiveID = -1;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param apiaryID
     * @param hiveID
     */
    public static GraphSelectionFragment newInstance(long apiaryID, long hiveID) {
        Log.d(TAG, "getting newInstance of GraphFragment...apiaryID: " + apiaryID +
            " : hiveID: " + hiveID);

        GraphSelectionFragment fragment = new GraphSelectionFragment();
        Bundle args = new Bundle();

        args.putLong(APIARY_ID, apiaryID);
        args.putLong(HIVE_ID, hiveID);
        fragment.setArguments(args);

        return fragment;
    }

    public GraphSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mApiaryID = getArguments().getLong(APIARY_ID);
            mHiveID = getArguments().getLong(HIVE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph_selection, container, false);

        // disable the stuff inside the include - let AsyncTask enable after spinner is filled
        View layoutSelector1 = view.findViewById(R.id.selector1);
        final Spinner spnSelector1 = (Spinner)layoutSelector1.findViewById(R.id.spinnerSelection);
        final Button btnSelector1 = (Button)layoutSelector1.findViewById(R.id.buttonSelection);
        spnSelector1.setEnabled(false);
        btnSelector1.setEnabled(false);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
