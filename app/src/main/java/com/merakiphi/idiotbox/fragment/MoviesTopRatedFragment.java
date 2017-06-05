package com.merakiphi.idiotbox.fragment;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
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
import com.merakiphi.idiotbox.adapter.MoviesAdapter;
import com.merakiphi.idiotbox.model.Movie;
import com.merakiphi.idiotbox.other.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.merakiphi.idiotbox.other.Contract.API_IMAGE_BASE_URL;
import static com.merakiphi.idiotbox.other.Contract.LANGUAGE;
import static com.merakiphi.idiotbox.other.Contract.MOVIE_TOP_RATED_REQUEST;
import static com.merakiphi.idiotbox.other.Contract.REGION;

/**
 * Created by anuragmaravi on 14/03/17.
 */

public class MoviesTopRatedFragment extends Fragment {
    private String tvShowDetailsRequest;
    private RecyclerView recyclerViewMoviesTopRated;
    private MoviesAdapter adapter;
    private List<Movie> movieListTopRated;
    private View rootView;
    private ProgressBar progressBar;


    public MoviesTopRatedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_movie_top_rated, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);

        //Get the region from the settings and append to the request
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String region = prefs.getString("country", "IN"); //Default: India
        String language = prefs.getString("language", "en"); //Default: English
        final String poster_quality = prefs.getString("poster_size", "w342/"); //Default: Medium

        /**
         * Top Rated Movies
         */
        recyclerViewMoviesTopRated = (RecyclerView) rootView.findViewById(R.id.recyclerViewMoviesTopRated);
        movieListTopRated= new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerViewMoviesTopRated.setLayoutManager(mLayoutManager);
        recyclerViewMoviesTopRated.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerViewMoviesTopRated.setItemAnimator(new DefaultItemAnimator());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, MOVIE_TOP_RATED_REQUEST +
                    //Language parameter
                    LANGUAGE + language +
                    //Region parameter
                    REGION + region,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.i("Volley", "onResponse(MovieTopRated): " + response);
                                JSONObject parentObject = new JSONObject(response);
                                JSONArray parentArray = parentObject.getJSONArray("results");
                                for(int i=0;i<parentArray.length();i++){
                                    JSONObject finalObject = parentArray.getJSONObject(i);
                                    Movie movieModel = new Movie();
                                    movieModel.setId(finalObject.getString("id"));
                                    movieModel.setPosterPath(API_IMAGE_BASE_URL + poster_quality + finalObject.getString("poster_path"));

                                    movieListTopRated.add(movieModel);
                                }
                                adapter = new MoviesAdapter(getActivity(), movieListTopRated);
                                recyclerViewMoviesTopRated.setAdapter(adapter);
                                recyclerViewMoviesTopRated.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "Some Error Occurred", Toast.LENGTH_SHORT).show();
                }
            });
            // Add the request to the RequestQueue.
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        return rootView;
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
