package com.merakiphi.idiotbox.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.merakiphi.idiotbox.R;
import com.merakiphi.idiotbox.adapter.SimilarTvShowAdapter;
import com.merakiphi.idiotbox.adapter.TvShowCastingAdapter;
import com.merakiphi.idiotbox.adapter.TvShowSeasonsAdapter;
import com.merakiphi.idiotbox.model.Movie;
import com.merakiphi.idiotbox.model.TvShow;
import com.merakiphi.idiotbox.other.CheckInternet;
import com.merakiphi.idiotbox.other.Contract;
import com.merakiphi.idiotbox.other.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.merakiphi.idiotbox.other.Contract.API_IMAGE_BASE_URL;
import static com.merakiphi.idiotbox.other.Contract.API_IMAGE_SIZE_XXL;
import static com.merakiphi.idiotbox.other.Contract.API_KEY;
import static com.merakiphi.idiotbox.other.Contract.API_TV;
import static com.merakiphi.idiotbox.other.Contract.API_URL;
import static com.merakiphi.idiotbox.other.Contract.OMDB_BASE_URL;

/**
 * Created by anuragmaravi on 01/02/17.
 */

public class TvShowDetailsActivity extends AppCompatActivity {
    String TAG, tvShowId, tvShowDetailsRequest, tvShowExternalIdsRequest, tvShowCastingRequest;

    private TextView textViewDirector,
            textViewTitle,
            textViewVoteAverage,
            textViewReleaseDateRuntime,
            textViewOverview,
            textViewMovieOrTvShow,
            textViewYear,
            textViewTmdbVote,
            textViewMovieTagline,
            textViewCountry;
    private ImageView imageViewPoster;
    private LinearLayout linearLayoutTitle;
    private ScrollView container;
    private ProgressBar progressBar;

    //Similar Tv Shows
    private RecyclerView recyclerViewSimilar;
    private List<Movie> similarTvShowList= new ArrayList<>();
    private  RecyclerView.Adapter adapterSimilarTvShow;
    private RecyclerView.LayoutManager similarTvShowLayoutManager;

    //Seasons of Tv Shows
    private RecyclerView recyclerViewSeasons;
    private List<TvShow> seasonsTvShowList= new ArrayList<>();
    private  RecyclerView.Adapter adapterTvShowSeasons;
    private RecyclerView.LayoutManager layoutManagerTvShowsSeasons;

    //Casting of Tv Shows
    private RecyclerView recyclerViewTvShowCasting;
    private List<TvShow> tvShowCastingList= new ArrayList<>();
    private  RecyclerView.Adapter adapterTvShowCasting;
    private RecyclerView.LayoutManager layoutManagerTvShowCasting;

    //To show or hide title box
    boolean isShown = true;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CheckInternet.getInstance(getApplicationContext()).isNetworkConnected()) {
            setContentView(R.layout.activity_tvshow_details);
        TAG = getClass().getSimpleName();
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle("");
        tvShowId = getIntent().getStringExtra("tvshow_id");


        //Views Initialisation
        container = (ScrollView) findViewById(R.id.container);
        textViewOverview = (TextView) findViewById(R.id.textViewOverview);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewMovieOrTvShow = (TextView) findViewById(R.id.textViewMovieOrTvShow);
        textViewYear = (TextView) findViewById(R.id.textViewYear);
        textViewReleaseDateRuntime = (TextView) findViewById(R.id.textViewReleaseDateRuntime);
        textViewDirector = (TextView) findViewById(R.id.textViewDirector);
        textViewCountry = (TextView) findViewById(R.id.textViewCountry);
        textViewVoteAverage = (TextView) findViewById(R.id.textViewVoteAverage);
        textViewMovieTagline = (TextView) findViewById(R.id.textViewMovieTagline);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.tv_show_accent), android.graphics.PorterDuff.Mode.MULTIPLY);

        linearLayoutTitle = (LinearLayout) findViewById(R.id.linearLayoutTitle);
        imageViewPoster = (ImageView) findViewById(R.id.imageViewPoster);
        imageViewPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShown) {
                    linearLayoutTitle.setVisibility(View.INVISIBLE);
                    isShown = false;
                }
                else {
                    linearLayoutTitle.setVisibility(View.VISIBLE);
                    isShown = true;
                }

            }
        });



        /**
         * Tv Show Details
         */
        tvShowDetailsRequest = API_URL + Contract.API_TV + "/" + tvShowId + "?api_key=" + Contract.API_KEY;
        StringRequest stringRequestTvShowDetails = new StringRequest(Request.Method.GET, tvShowDetailsRequest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse(TvShow Details): " + response);
                        Log.i(TAG, "URL (TvShow Details): " + tvShowDetailsRequest);
                        try {
                            parseAndDisplayData(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Some Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequestTvShowDetails);

        /**
         * Tv Show Casting
         */
        //RecyclerView Tv Show Casting
        layoutManagerTvShowCasting = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTvShowCasting = (RecyclerView) findViewById(R.id.recyclerViewTvShowCasting);
        recyclerViewTvShowCasting.setLayoutManager(layoutManagerTvShowCasting);
        recyclerViewTvShowCasting.setItemAnimator(new DefaultItemAnimator());
        tvShowCastingRequest = API_URL + Contract.API_TV + "/" + tvShowId + "/credits?api_key=" + Contract.API_KEY;
        StringRequest stringRequestTvShowCasting = new StringRequest(Request.Method.GET, tvShowCastingRequest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse(TvShow Casting): " + response);

                        try {
                            JSONObject parentObject= new JSONObject(response);
                            JSONArray parentArray = parentObject.getJSONArray("cast");
                            for(int i=0;i<parentArray.length();i++){
                                JSONObject finalObject = parentArray.getJSONObject(i);
                                TvShow tvShow = new TvShow();
                                tvShow.setTvShowCastCharacter(finalObject.getString("character"));
                                tvShow.setTvShowCastId(finalObject.getString("id"));
                                tvShow.setTvShowCastName(finalObject.getString("name"));
                                tvShow.setTvShowCastProfilePath(Contract.API_IMAGE_URL + finalObject.getString("profile_path"));
                                tvShowCastingList.add(tvShow);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapterTvShowCasting = new TvShowCastingAdapter(getApplicationContext(), tvShowCastingList);
                        recyclerViewTvShowCasting.setAdapter(adapterTvShowCasting);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Some Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequestTvShowCasting);

        /**
         * Tv External Ids
         */
        tvShowExternalIdsRequest = API_URL + Contract.API_TV + "/" + tvShowId + "/external_ids?api_key=" + Contract.API_KEY;
        StringRequest stringRequestTvShowExternalIds = new StringRequest(Request.Method.GET, tvShowExternalIdsRequest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse(TvShow External Ids): " + response);
                        try {
                            final JSONObject parentObject= new JSONObject(response);
                            //Send Request to imdb database using imdb Id
                            StringRequest stringRequestImdb = new StringRequest(Request.Method.GET, OMDB_BASE_URL + parentObject.getString("imdb_id"),
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.i(TAG, "onResponse(IMDb): " + response);
                                            try {
                                                Log.i(TAG, "URL (IMDb): " + OMDB_BASE_URL + parentObject.getString("imdb_id"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                parseAndDisplayDataImdb(response);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "Some Error Occured", Toast.LENGTH_SHORT).show();
                                }
                            });
                            // Add the request to the RequestQueue.
                            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequestImdb);                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Some Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequestTvShowExternalIds);

        /**
         * Similar TvShow
         */
        //RecyclerView Similar TvShow
        similarTvShowLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSimilar = (RecyclerView) findViewById(R.id.recyclerViewSimilar);
        recyclerViewSimilar.setLayoutManager(similarTvShowLayoutManager);
        recyclerViewSimilar.setItemAnimator(new DefaultItemAnimator());
        //Request Similar movies
        String similarTvShowRequest = API_URL + API_TV + "/" + tvShowId + "/similar?api_key=" + API_KEY;
        StringRequest stringRequestSimilar = new StringRequest(Request.Method.GET, similarTvShowRequest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse(Similar): " + response);
                        try {
                            JSONObject parentObject= new JSONObject(response);
                            JSONArray parentArray = parentObject.getJSONArray("results");
                            for(int i=0;i<parentArray.length();i++){
                                JSONObject finalObject = parentArray.getJSONObject(i);
                                Movie movieModel = new Movie();
                                movieModel.setSimilarId(finalObject.getString("id"));
                                movieModel.setSimilarPosterPath(Contract.API_IMAGE_URL + finalObject.getString("poster_path"));
                                similarTvShowList.add(movieModel);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapterSimilarTvShow = new SimilarTvShowAdapter(getApplicationContext(), similarTvShowList);
                        recyclerViewSimilar.setAdapter(adapterSimilarTvShow);
                        container.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Some Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequestSimilar);
    } else {
        setNoInternetView();
    }

}

    /**
     * This method sets the no internet connection layout when internet is not available.
     */
    private void setNoInternetView() {
        setContentView(R.layout.fragment_no_internet);
        TAG = getClass().getSimpleName();
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle("");
        findViewById(R.id.buttonTryAgain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });
    }

    /**
     * Parse and display the data from tmdb
     */
    private void parseAndDisplayData(String response) throws JSONException {
        //ToDo: Add this data for on persistent storage
        JSONObject parentObject = new JSONObject(response);
        Glide.with(getApplicationContext()).load(API_IMAGE_BASE_URL + API_IMAGE_SIZE_XXL + "/" + parentObject.getString("poster_path")).into((ImageView) findViewById(R.id.imageViewPoster));

        textViewOverview.setText(parentObject.getString("overview"));
        textViewTitle.setText(parentObject.getString("original_name"));
        textViewMovieTagline.setText(parentObject.getString("status"));
        String tvName = parentObject.getString("original_name");
        //Showing Seasons
        JSONArray parentArray = parentObject.getJSONArray("seasons");
        for (int i = parentArray.length() - 1; i >= 0; i--) {
            JSONObject finalObject = parentArray.getJSONObject(i);
            TvShow tvShow = new TvShow();
            //Show Episodes
            tvShow.setTvShowSeasonEpisodeCount(finalObject.getString("episode_count"));
            tvShow.setsTvShowId(tvShowId);
            tvShow.setTvShowSeasonNumber(finalObject.getString("season_number"));
            tvShow.setTvShowNetworkName(tvName);
            tvShow.setTvShowSeasonPosterPath(Contract.API_IMAGE_URL + finalObject.getString("poster_path"));
            seasonsTvShowList.add(tvShow);
        }
        //RecyclerView  TvShow Seasons
        layoutManagerTvShowsSeasons = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSeasons = (RecyclerView) findViewById(R.id.recyclerViewSeasons);
        recyclerViewSeasons.setLayoutManager(layoutManagerTvShowsSeasons);
        recyclerViewSeasons.setItemAnimator(new DefaultItemAnimator());
        adapterTvShowSeasons = new TvShowSeasonsAdapter(getApplicationContext(), seasonsTvShowList);
        recyclerViewSeasons.setAdapter(adapterTvShowSeasons);
    }

    /**
     * Parse and display the data from imdb
     */
    private void parseAndDisplayDataImdb(String response) throws JSONException {
        //ToDo: Add this data for on persistent storage
        JSONObject parentObject = new JSONObject(response);
        textViewMovieOrTvShow.setText(parentObject.getString("Type"));
        textViewYear.setText(parentObject.getString("Year"));
        textViewReleaseDateRuntime.setText("• " + parentObject.getString("Runtime") + " • " + parentObject.getString("Released") + " • " + parentObject.getString("Rated") + "\n\n• " +parentObject.getString("Genre"));
        textViewDirector.setText("Writer:  " + parentObject.getString("Writer"));
        textViewCountry.setText( parentObject.getString("Country"));
        textViewVoteAverage.setText( parentObject.getString("imdbRating"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
