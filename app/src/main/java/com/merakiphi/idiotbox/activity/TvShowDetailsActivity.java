package com.merakiphi.idiotbox.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
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
import com.merakiphi.idiotbox.other.DateFormatter;
import com.merakiphi.idiotbox.other.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.merakiphi.idiotbox.other.Contract.API_IMAGE_BASE_URL;
import static com.merakiphi.idiotbox.other.Contract.API_IMAGE_SIZE_XXL;
import static com.merakiphi.idiotbox.other.Contract.API_KEY;
import static com.merakiphi.idiotbox.other.Contract.API_URL;
import static com.merakiphi.idiotbox.other.Contract.APPEND;
import static com.merakiphi.idiotbox.other.Contract.CREDITS;
import static com.merakiphi.idiotbox.other.Contract.LANGUAGE;
import static com.merakiphi.idiotbox.other.Contract.REGION;
import static com.merakiphi.idiotbox.other.Contract.SEPARATOR;
import static com.merakiphi.idiotbox.other.Contract.SIMILAR;

/**
 * Created by anuragmaravi on 01/02/17.
 */

public class TvShowDetailsActivity extends AppCompatActivity {
    String TAG, tvShowId, tvShowDetailsRequest;

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

    private SharedPreferences prefs;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String region = prefs.getString("country", "IN"); //Default: India
        String language = prefs.getString("language", "en"); //Default: English

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
        tvShowDetailsRequest = API_URL + Contract.API_TV + "/" + tvShowId + "?api_key=" + API_KEY +
                //Language parameter
                LANGUAGE + language +
                //Region parameter
                REGION + region +
                APPEND + CREDITS + SEPARATOR + SIMILAR;
            Log.i(TAG, "onCreate: " + tvShowDetailsRequest);
            StringRequest stringRequestTvShowDetails = new StringRequest(Request.Method.GET, tvShowDetailsRequest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse(TvShow Details): " + response);
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
        final String poster_quality = prefs.getString("poster_size", "w342/"); //Default: Medium

        JSONObject parentObject = new JSONObject(response);
        Glide.with(getApplicationContext()).load(API_IMAGE_BASE_URL + API_IMAGE_SIZE_XXL + "/" + parentObject.getString("poster_path")).into((ImageView) findViewById(R.id.imageViewPoster));

        textViewOverview.setText(parentObject.getString("overview"));
        textViewTitle.setText(parentObject.getString("original_name"));
        textViewMovieTagline.setText(parentObject.getString("status"));

        textViewMovieOrTvShow.setText("Tv Show");
        textViewReleaseDateRuntime.setText(("• Type: " + parentObject.getString("type")));
        textViewCountry.setText("• Status: " + parentObject.getString("status"));
        textViewVoteAverage.setText(parentObject.getString("vote_average"));
        try {
            textViewDirector.setText("• Last Air Date: " + DateFormatter.getInstance(getApplicationContext()).formatDate(parentObject.getString("last_air_date")));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(parentObject.getString("first_air_date"));
            String year = (String) DateFormat.format("yyyy", date);
            textViewYear.setText(year);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
            tvShow.setTvShowSeasonPosterPath(API_IMAGE_BASE_URL + poster_quality + finalObject.getString("poster_path"));
            seasonsTvShowList.add(tvShow);
        }
        //RecyclerView  TvShow Seasons
        layoutManagerTvShowsSeasons = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSeasons = (RecyclerView) findViewById(R.id.recyclerViewSeasons);
        recyclerViewSeasons.setLayoutManager(layoutManagerTvShowsSeasons);
        recyclerViewSeasons.setItemAnimator(new DefaultItemAnimator());
        adapterTvShowSeasons = new TvShowSeasonsAdapter(getApplicationContext(), seasonsTvShowList);
        recyclerViewSeasons.setAdapter(adapterTvShowSeasons);

        //Casting
        JSONObject castingObject= parentObject.getJSONObject("credits");
        JSONArray castingArray = castingObject.getJSONArray("cast");
        for(int i=0;i<castingArray.length();i++){
            JSONObject finalObject = castingArray.getJSONObject(i);
            TvShow tvShow = new TvShow();
            tvShow.setTvShowCastCharacter(finalObject.getString("character"));
            tvShow.setTvShowCastId(finalObject.getString("id"));
            tvShow.setTvShowCastName(finalObject.getString("name"));
            tvShow.setTvShowCastProfilePath(API_IMAGE_BASE_URL + poster_quality + finalObject.getString("profile_path"));
            tvShowCastingList.add(tvShow);
        }
        layoutManagerTvShowCasting = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTvShowCasting = (RecyclerView) findViewById(R.id.recyclerViewTvShowCasting);
        recyclerViewTvShowCasting.setLayoutManager(layoutManagerTvShowCasting);
        recyclerViewTvShowCasting.setItemAnimator(new DefaultItemAnimator());
        adapterTvShowCasting = new TvShowCastingAdapter(getApplicationContext(), tvShowCastingList);
        recyclerViewTvShowCasting.setAdapter(adapterTvShowCasting);

        //Similar TvShows
        JSONObject similarObject= parentObject.getJSONObject("similar");
        JSONArray similarArray = similarObject.getJSONArray("results");
        for(int i=0;i<similarArray.length();i++){
            JSONObject finalObject = similarArray.getJSONObject(i);
            Movie movieModel = new Movie();
            movieModel.setSimilarId(finalObject.getString("id"));
            movieModel.setSimilarPosterPath(API_IMAGE_BASE_URL + poster_quality + finalObject.getString("poster_path"));
            similarTvShowList.add(movieModel);
        }
        similarTvShowLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSimilar = (RecyclerView) findViewById(R.id.recyclerViewSimilar);
        recyclerViewSimilar.setLayoutManager(similarTvShowLayoutManager);
        recyclerViewSimilar.setItemAnimator(new DefaultItemAnimator());
        adapterSimilarTvShow = new SimilarTvShowAdapter(getApplicationContext(), similarTvShowList);
        recyclerViewSimilar.setAdapter(adapterSimilarTvShow);
        container.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
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
