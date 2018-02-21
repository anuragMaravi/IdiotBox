package com.merakiphi.idiotbox.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.merakiphi.idiotbox.R;
import com.merakiphi.idiotbox.adapter.CastingAdapter;
import com.merakiphi.idiotbox.adapter.GenreAdapter;
import com.merakiphi.idiotbox.adapter.SimilarAdapter;
import com.merakiphi.idiotbox.adapter.TrailerAdapter;
import com.merakiphi.idiotbox.model.Movie;
import com.merakiphi.idiotbox.other.CheckInternet;
import com.merakiphi.idiotbox.other.DateFormatter;
import com.merakiphi.idiotbox.other.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.merakiphi.idiotbox.other.Contract.API_IMAGE_BASE_URL;
import static com.merakiphi.idiotbox.other.Contract.API_IMAGE_SIZE_XXL;
import static com.merakiphi.idiotbox.other.Contract.API_KEY;
import static com.merakiphi.idiotbox.other.Contract.API_MOVIE;
import static com.merakiphi.idiotbox.other.Contract.API_URL;
import static com.merakiphi.idiotbox.other.Contract.APPEND;
import static com.merakiphi.idiotbox.other.Contract.CREDITS;
import static com.merakiphi.idiotbox.other.Contract.LANGUAGE;
import static com.merakiphi.idiotbox.other.Contract.REGION;
import static com.merakiphi.idiotbox.other.Contract.SEPARATOR;
import static com.merakiphi.idiotbox.other.Contract.SIMILAR;
import static com.merakiphi.idiotbox.other.Contract.VIDEOS;

/**
 * Created by anuragmaravi on 29/01/17.
 */

public class MovieDetailsActivity  extends AppCompatActivity {

    public static String TAG;
    private static final String PREFS_NAME = "LOGIN";

    String movieId;
    private String movieDetailsRequest;
    private TextView textViewDirector,
            textViewTitle,
            textViewVoteAverage,
            textViewReleaseDateRuntime,
            textViewOverview,
            textViewMovieOrTvShow,
            textViewYear,
            textViewMovieTagline,
            textViewCountry;
    private ImageView imageViewPoster;
    private LinearLayout linearLayoutTitle;
    private ScrollView container;
    private ProgressBar progressBar;

    //Similar Movies
    private RecyclerView recyclerViewSimilar;
    private List<Movie> similarMovieList= new ArrayList<>();
    private  RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager similarLayoutManager;

    //Trailers
    private List<Movie> trailersList= new ArrayList<>();
    private RecyclerView recyclerViewTrailers;
    private  RecyclerView.Adapter adapterTrailers;
    private RecyclerView.LayoutManager layoutManagerTrailers;

    //Casting
    private List<Movie> castingList= new ArrayList<>();
    private RecyclerView recyclerViewCasting;
    private  RecyclerView.Adapter adapterCasting;
    private RecyclerView.LayoutManager layoutManagerCasting;

    //Genres
    private List<Movie> genreList= new ArrayList<>();
    private RecyclerView recyclerViewGenre;
    private  RecyclerView.Adapter adapterGenre;
    private RecyclerView.LayoutManager layoutManagerGenre;

    //To show or hide title box
    boolean isShown = true;

    private SharedPreferences prefs;
    private static SharedPreferences sharedPreferences;


    //Ads
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String region = prefs.getString("country", "US"); //Default: India
        String language = prefs.getString("language", "en"); //Default: English

        if(CheckInternet.getInstance(getApplicationContext()).isNetworkConnected()){
            setContentView(R.layout.activity_movie_details);
            TAG = getClass().getSimpleName();
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            this.getSupportActionBar().setTitle("");
            movieId = getIntent().getStringExtra("movie_id");

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
            linearLayoutTitle = (LinearLayout) findViewById(R.id.linearLayoutTitle);
            imageViewPoster = (ImageView) findViewById(R.id.imageViewPoster);

            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);

            //Toggles movie poster completely visible and half hidden
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
             * Movie Details with videos, images, credits, similar
             */
            movieDetailsRequest = API_URL + API_MOVIE + movieId + "?api_key=" + API_KEY +
                    //Language parameter
                    LANGUAGE + language +
                    //Region parameter
                    REGION + region +
                    //Append to response parameters
                    APPEND + VIDEOS + SEPARATOR + CREDITS + SEPARATOR + SIMILAR;
            Log.i(TAG, "New Request: " + movieDetailsRequest);
            StringRequest stringRequestTmdb = new StringRequest(Request.Method.GET, movieDetailsRequest,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i(TAG, "onResponse(TMDb): " + response);
                            try {
                                parseAndDisplayData(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Some Error Occurred.", Toast.LENGTH_SHORT).show();
                }
            });
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequestTmdb);

            /**
             * Ads
             */
            //Initialising AdMob
            MobileAds.initialize(this, "ca-app-pub-3259009684379327~5979085895");
            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            mAdView = (AdView) findViewById(R.id.adView);

            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);


        } else {
            setNoInternetView();
        }

    }

    /**
     * Parse and display the data from tmdb
     */
    private void parseAndDisplayData(String response) throws JSONException {
        final String poster_quality = prefs.getString("poster_size", "w342/"); //Default: Medium

        final JSONObject parentObject = new JSONObject(response);
        Glide.with(getApplicationContext()).load(API_IMAGE_BASE_URL + API_IMAGE_SIZE_XXL + "/" + parentObject.getString("poster_path")).into(imageViewPoster);
        textViewOverview.setText(parentObject.getString("overview"));
        textViewTitle.setText(parentObject.getString("original_title"));
        textViewMovieTagline.setText(parentObject.getString("tagline"));
        textViewMovieOrTvShow.setText("Movie");
        textViewReleaseDateRuntime.setText("• " + parentObject.getString("runtime") + " min");
        textViewCountry.setText("• Status: " + parentObject.getString("status"));
        textViewVoteAverage.setText( parentObject.getString("vote_average"));
        try {
            textViewDirector.setText("• " + DateFormatter.getInstance(getApplicationContext()).formatDate(parentObject.getString("release_date")));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(parentObject.getString("release_date"));
            String year = (String) DateFormat.format("yyyy", date);
            textViewYear.setText(year);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //Genres
        JSONArray genreArray = parentObject.getJSONArray("genres");
        for (int i = 0; i < genreArray.length(); i++) {
            JSONObject finalObject = genreArray.getJSONObject(i);
            Movie movieModel = new Movie();
            movieModel.setGenreId(finalObject.getString("id"));
            movieModel.setGenreName(finalObject.getString("name"));
            genreList.add(movieModel);
        }
        layoutManagerGenre = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewGenre = (RecyclerView) findViewById(R.id.recyclerViewGenre);
        recyclerViewGenre.setLayoutManager(layoutManagerGenre);
        recyclerViewGenre.setItemAnimator(new DefaultItemAnimator());
        adapterGenre = new GenreAdapter(getApplicationContext(), genreList);
        recyclerViewGenre.setAdapter(adapterGenre);

        //Trailers
        JSONObject videoObject = parentObject.getJSONObject("videos");
        JSONArray videoArray = videoObject.getJSONArray("results");
        for (int i = 0; i < videoArray.length(); i++) {
            JSONObject finalObject = videoArray.getJSONObject(i);
            Movie movieModel = new Movie();
            movieModel.setVideoKey(finalObject.getString("key"));
            movieModel.setVideoName(finalObject.getString("name"));
            movieModel.setVideoType(finalObject.getString("type"));
            trailersList.add(movieModel);
        }
        layoutManagerTrailers = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTrailers = (RecyclerView) findViewById(R.id.recyclerViewTrailers);
        recyclerViewTrailers.setLayoutManager(layoutManagerTrailers);
        recyclerViewTrailers.setItemAnimator(new DefaultItemAnimator());
        adapterTrailers = new TrailerAdapter(getApplicationContext(), trailersList);
        recyclerViewTrailers.setAdapter(adapterTrailers);


        //Similar Movies
        JSONObject similarObject = parentObject.getJSONObject("similar");
        JSONArray similarArray = similarObject.getJSONArray("results");
        for (int i = 0; i < similarArray.length(); i++) {
            JSONObject finalObject = similarArray.getJSONObject(i);
            Movie movieModel = new Movie();
            movieModel.setSimilarId(finalObject.getString("id"));
            movieModel.setSimilarPosterPath(API_IMAGE_BASE_URL + poster_quality + finalObject.getString("poster_path"));
            similarMovieList.add(movieModel);
        }
        similarLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSimilar = (RecyclerView) findViewById(R.id.recyclerViewSimilar);
        recyclerViewSimilar.setLayoutManager(similarLayoutManager);
        recyclerViewSimilar.setItemAnimator(new DefaultItemAnimator());
        adapter = new SimilarAdapter(getApplicationContext(), similarMovieList);
        recyclerViewSimilar.setAdapter(adapter);


        //Credits or casting
        JSONObject castObject= parentObject.getJSONObject("credits");
        JSONArray castArray = castObject.getJSONArray("cast");
        for(int i=0;i<castArray.length();i++){
            JSONObject finalObject = castArray.getJSONObject(i);
            Movie movieModel = new Movie();
            movieModel.setCastingId(finalObject.getString("id"));
            movieModel.setCastingCharacter(finalObject.getString("character"));
            movieModel.setCastingName(finalObject.getString("name"));
            movieModel.setCastingProfilePath(API_IMAGE_BASE_URL + poster_quality + finalObject.getString("profile_path"));
            castingList.add(movieModel);
        }
        layoutManagerCasting = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCasting = (RecyclerView) findViewById(R.id.recyclerViewCasting);
        recyclerViewCasting.setLayoutManager(layoutManagerCasting);
        recyclerViewCasting.setItemAnimator(new DefaultItemAnimator());
        adapterCasting = new CastingAdapter(getApplicationContext(), castingList);
        recyclerViewCasting.setAdapter(adapterCasting);

        //Make the container visible and hide the progressbar
        container.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_favorite, menu);
        return true;
    }

    boolean movieSelected = false;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
            case R.id.action_favorite:

                //To mark the movie as favorite
                if(!movieSelected) {
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_24dp));
                    item.getIcon().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                    item.setTitle("Unfavorite");
                    Toast.makeText(this, "marked", Toast.LENGTH_SHORT).show();
                    movieSelected = true;

                    // Inflate the layout for this fragment
                    sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    String accountId = sharedPreferences.getString("ACCOUNT_ID", null);
                    String sessionId = sharedPreferences.getString("SESSION_ID", null);

                    try {
                        JSONObject jsonBody = new JSONObject("{\"media_type\":\"movie\", \"media_id\":" + movieId + ", \"favorite\":false}");
                        JsonObjectRequest stringRequest = new JsonObjectRequest("https://api.themoviedb.org/3/account/"+ accountId +"/favorite?api_key=" + API_KEY + "&session_id=" + sessionId, jsonBody,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.i("Volley", "onResponse(Maek Favorite): " + response);
                                        JSONObject parentObject = response;


                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        // Add the request to the RequestQueue.
                        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                //To unmark the movie as favorite
                else {
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_white_24dp));
                    item.setTitle("Favorite");
                    Toast.makeText(this, "unmarked", Toast.LENGTH_SHORT).show();

                    movieSelected = false;

                    // Inflate the layout for this fragment
                    sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    String accountId = sharedPreferences.getString("ACCOUNT_ID", null);
                    String sessionId = sharedPreferences.getString("SESSION_ID", null);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.themoviedb.org/3/account/"+ accountId +"/favorite?api_key=" + API_KEY + "&session_id=" + sessionId,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        Log.i("Volley", "onResponse(Maek Favorite): " + response);
                                        JSONObject parentObject = new JSONObject(response);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    {
                        @Override
                        protected Map<String,String> getParams(){
                            Map<String,String> params = new HashMap<String, String>();
                            params.put("media_type","movie");
                            params.put("media_id",movieId);
                            params.put("favorite", "false");
                            return params;
                        }};
                    // Add the request to the RequestQueue.
                    VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                }




        }
        return super.onOptionsItemSelected(item);
    }

    //Ads
    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}

