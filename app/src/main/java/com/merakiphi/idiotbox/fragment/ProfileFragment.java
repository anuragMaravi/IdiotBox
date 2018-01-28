package com.merakiphi.idiotbox.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.merakiphi.idiotbox.R;
import com.merakiphi.idiotbox.other.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import static com.merakiphi.idiotbox.other.Contract.ACCOUNT_DETAILS;
import static com.merakiphi.idiotbox.other.Contract.API_KEY;

/**
 * Created by anuragmaravi on 14/03/17.
 */

public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String PREFS_NAME = "LOGIN";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TvShowsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private View rootView;
    private static SharedPreferences sharedPreferences;
    private TextView textView;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment
        sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        textView = (TextView) rootView.findViewById(R.id.textView);

        String sessionId = sharedPreferences.getString("SESSION_ID", null);



        StringRequest stringRequest = new StringRequest(Request.Method.GET, ACCOUNT_DETAILS + "&session_id=" + sessionId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("Volley", "onResponse(Account Details): " + response);
                            JSONObject parentObject = new JSONObject(response);
                            textView.setText(parentObject.getString("name"));
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("ACCOUNT_ID",String.valueOf(parentObject.getInt("id")));
                            editor.apply();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        String accountId = sharedPreferences.getString("ACCOUNT_ID", null);
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, "https://api.themoviedb.org/3/account/" + accountId + "/favorite/movies?api_key=" + API_KEY + "&session_id=" + sessionId + "&sort_by=created_at.asc",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("Volley", "onResponse(Favorite Movies): " + response);
                            JSONObject parentObject = new JSONObject(response);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest1);

        return rootView;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
