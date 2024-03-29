package com.merakiphi.idiotbox.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.merakiphi.idiotbox.R;
import com.merakiphi.idiotbox.adapter.TvShowAdapter;
import com.merakiphi.idiotbox.model.TvShow;
import com.merakiphi.idiotbox.other.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;
import static com.merakiphi.idiotbox.other.Contract.API_IMAGE_BASE_URL;
import static com.merakiphi.idiotbox.other.Contract.LANGUAGE;
import static com.merakiphi.idiotbox.other.Contract.REGION;
import static com.merakiphi.idiotbox.other.Contract.TV_AIRING_TODAY_REQUEST;

/**
 * Created by anuragmaravi on 02/02/17.
 */

public class TvShowFragmentAiringToday extends Fragment {
    View rootView;
    //Tv Shows
    private RecyclerView recyclerViewTvShows;
    private List<TvShow> tvShowsList;
    private  RecyclerView.Adapter adapterTvShows;
    private RecyclerView.LayoutManager layoutManagerTvShows;
    private ProgressBar progressBar;



    public TvShowFragmentAiringToday() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tvshow_popular, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.tv_show_accent), android.graphics.PorterDuff.Mode.MULTIPLY);

        //Get the region from the settings and append to the request
        String locale = getResources().getConfiguration().locale.getCountry();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String region = prefs.getString("country", locale); //Default: India
        String language = prefs.getString("language", "en"); //Default: English
        final String poster_quality = prefs.getString("poster_size", "w342/"); //Default: Medium

        /**
         * Popular Tv shows
         */
        tvShowsList= new ArrayList<>();
        layoutManagerTvShows =  new GridLayoutManager(getActivity(), 2);
        recyclerViewTvShows = (RecyclerView) rootView.findViewById(R.id.recyclerViewTvShows);
        recyclerViewTvShows.setLayoutManager(layoutManagerTvShows);
        StringRequest stringRequestTvShowDetails = new StringRequest(Request.Method.GET, TV_AIRING_TODAY_REQUEST +
                //Language parameter
                LANGUAGE + language +
                //Region parameter
                REGION + region,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse(TvShowToday): " + response);
                        try {
                            JSONObject parentObject = new JSONObject(response);
                            JSONArray parentArray = parentObject.getJSONArray("results");
                            for (int i = 0; i < parentArray.length(); i++) {
                                JSONObject finalObject = parentArray.getJSONObject(i);
                                TvShow tvShow = new TvShow();
                                tvShow.setTvShowName(finalObject.getString("original_name"));
                                tvShow.setTvShowId(finalObject.getString("id"));
//                                tvShow.setTvShowFirstAirDate(finalObject.getString("first_air_date"));
//                                tvShow.setTvShowBackdropPath(finalObject.getString("backdrop_path"));
                                tvShow.setTvShowPosterPath(API_IMAGE_BASE_URL + poster_quality + finalObject.getString("poster_path"));
                                tvShowsList.add(tvShow);

                            }
                            adapterTvShows = new TvShowAdapter(getContext(), tvShowsList);
                            recyclerViewTvShows.setAdapter(adapterTvShows);
                            recyclerViewTvShows.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Some Error Occured", Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequestTvShowDetails);
        // Inflate the layout for this fragment
        return rootView;
    }


}
