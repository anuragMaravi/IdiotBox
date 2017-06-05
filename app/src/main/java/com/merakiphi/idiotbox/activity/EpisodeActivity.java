package com.merakiphi.idiotbox.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.merakiphi.idiotbox.other.CheckError;
import com.merakiphi.idiotbox.R;
import com.merakiphi.idiotbox.adapter.EpisodesAdapter;
import com.merakiphi.idiotbox.adapter.SeasonListAdapter;
import com.merakiphi.idiotbox.model.TvShow;
import com.merakiphi.idiotbox.other.CheckInternet;
import com.merakiphi.idiotbox.other.Contract;
import com.merakiphi.idiotbox.other.DateFormatter;
import com.merakiphi.idiotbox.other.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.merakiphi.idiotbox.other.Contract.API_IMAGE_BASE_URL;
import static com.merakiphi.idiotbox.other.Contract.API_IMAGE_SIZE_XXL;
import static com.merakiphi.idiotbox.other.Contract.API_URL;
import static com.merakiphi.idiotbox.other.Contract.OMDB_BASE_URL;

/**
 * Created by anuragmaravi on 03/02/17.
 */

public class EpisodeActivity extends AppCompatActivity {
    String TAG;
    String episodeId, seasonNumber, tvShowId;

    //to measure the width
    private LinearLayout linearLayout;
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

    //Seasons of Tv Shows
    private RecyclerView recyclerViewSeasons;
    private List<TvShow> seasonsTvShowList= new ArrayList<>();
    private  RecyclerView.Adapter adapterTvShowSeasons;
    private RecyclerView.LayoutManager layoutManagerTvShowsSeasons;

    //Episodes
    private RecyclerView recyclerViewEpisodes;
    private List<TvShow> listEpisodes= new ArrayList<>();
    private  RecyclerView.Adapter adapterEpisodes;
    private RecyclerView.LayoutManager layoutManagerEpisodes;
    private ScrollView container;
    private ProgressBar progressBar;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CheckInternet.getInstance(getApplicationContext()).isNetworkConnected()) {
            setContentView(R.layout.activity_episode);
        TAG = getClass().getSimpleName();
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle("");
        episodeId = getIntent().getStringExtra("episode_number");
        seasonNumber = getIntent().getStringExtra("season_number");
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
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.tv_show_accent), android.graphics.PorterDuff.Mode.MULTIPLY);


            final String episodeUrl = API_URL + Contract.API_TV + "/" + tvShowId + "/season/" + seasonNumber + "/episode/" + episodeId + "?api_key=" + Contract.API_KEY;
        StringRequest stringRequestEpisodes = new StringRequest(Request.Method.GET, episodeUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse(TvShow Episode Details): " + response);
                        Log.i(TAG, "URL (TvShow Episode Details): " + episodeUrl);
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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequestEpisodes);

        //Show Episodes List
        displayTvShowEpisodes();

        //Display Imdb data
        parseAndDisplayImdbData();

        //Show SeasonsList
        try {
            parseAndDisplaySeasonList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        Glide.with(getApplicationContext()).load(API_IMAGE_BASE_URL + API_IMAGE_SIZE_XXL + "/" + parentObject.getString("still_path")).into((ImageView) findViewById(R.id.imageViewPoster));
        textViewOverview.setText(parentObject.getString("overview"));
        textViewTitle.setText(parentObject.getString("name"));
        textViewMovieOrTvShow.setText( "Season " + parentObject.getString("season_number") + " Ep " + parentObject.getString("episode_number"));

    }
    /**
     * Parse and display the data from Imdb
     */
    private void parseAndDisplayImdbData() {
        final String episodeUrl = API_URL + Contract.API_TV + "/" + tvShowId + "/season/" + seasonNumber + "/episode/" + episodeId + "/external_ids?api_key=" + Contract.API_KEY;
        StringRequest stringRequestEpisodes = new StringRequest(Request.Method.GET, episodeUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse(Episode External Ids): " + response);
                        try {
                            final JSONObject parentObject= new JSONObject(response);

                            //Again send the request to the Omdb using imdb_id
                            StringRequest stringRequestImdb = new StringRequest(Request.Method.GET, OMDB_BASE_URL + parentObject.getString("imdb_id"),
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.i(TAG, "onResponse(Episode Details Imdb): " + response);
                                            try {
                                                Log.i(TAG, "URL (IMDB): " + OMDB_BASE_URL + parentObject.getString("imdb_id"));
                                                JSONObject parentObject= new JSONObject(response);
                                                textViewVoteAverage.setText(parentObject.getString("imdbRating"));
                                                textViewYear.setText(parentObject.getString("Year"));
                                                textViewReleaseDateRuntime.setText("• " + parentObject.getString("Runtime") + " • " + parentObject.getString("Released") + " • " + parentObject.getString("Rated") + "\n\n• " +parentObject.getString("Genre"));
                                                textViewCountry.setText( parentObject.getString("Country"));
                                                textViewDirector.setText("Director: " + parentObject.getString("Director"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    new CheckError(getApplicationContext(), error, "IMDb");
                                }
                            });
                            // Add the request to the RequestQueue.
                            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequestImdb);

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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequestEpisodes);
    }


    /**
     * Parse and display Season List
     */
    private void parseAndDisplaySeasonList() throws JSONException {
        String tvShowDetailsRequest = API_URL + Contract.API_TV + "/" + tvShowId + "?api_key=" + Contract.API_KEY;
        StringRequest stringRequestTvShowDetails = new StringRequest(Request.Method.GET, tvShowDetailsRequest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse(TvShow Details): " + response);
                        try {
                            JSONObject parentObject = new JSONObject(response);
                            //Showing Seasons
                            JSONArray parentArray = parentObject.getJSONArray("seasons");
                            for (int i = 0; i < parentArray.length(); i++) {
                                JSONObject finalObject = parentArray.getJSONObject(i);
                                TvShow tvShow = new TvShow();
                                //Show Episodes
                                tvShow.setTvShowSeasonEpisodeCount(finalObject.getString("episode_count"));
                                tvShow.setsTvShowId(tvShowId);
                                tvShow.setTvShowSeasonNumber(finalObject.getString("season_number"));
                                seasonsTvShowList.add(tvShow);
                            }
                            //RecyclerView  TvShow Seasons
                            layoutManagerTvShowsSeasons = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                            recyclerViewSeasons = (RecyclerView) findViewById(R.id.recyclerViewSeasons);
                            recyclerViewSeasons.setLayoutManager(layoutManagerTvShowsSeasons);
                            recyclerViewSeasons.setItemAnimator(new DefaultItemAnimator());
                            adapterTvShowSeasons = new SeasonListAdapter(getApplicationContext(), seasonsTvShowList);
                            recyclerViewSeasons.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            recyclerViewSeasons.setNestedScrollingEnabled(false);
                            recyclerViewSeasons.setAdapter(adapterTvShowSeasons);
                            container.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
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

    }

    /**
     * Parse data for TV Show Episode List and show it.
     */
    private void displayTvShowEpisodes(){
        final String seasonUrl = API_URL + Contract.API_TV + "/" + tvShowId + "/season/" + seasonNumber + "?api_key=" + Contract.API_KEY;
        StringRequest stringRequestEpisodesList = new StringRequest(Request.Method.GET, seasonUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse(TvShow Season Details): " + response);
                        Log.i(TAG, "URL (TvShow Season Details): " + seasonUrl);
                        try {
                            JSONObject parentObject= new JSONObject(response);
                            JSONArray parentArray = parentObject.getJSONArray("episodes");
                            for(int i=0;i<parentArray.length();i++) {
                                JSONObject finalObject = parentArray.getJSONObject(i);
                                TvShow tvShow = new TvShow();
                                Log.i(TAG, "onResponse: " + episodeId + finalObject.getString("episode_number"));
                                if(!episodeId.equals(finalObject.getString("episode_number"))) {
                                    tvShow.setEpisodeId(finalObject.getString("id"));
                                    tvShow.setEpisodeAirDate(DateFormatter.getInstance(getApplicationContext()).formatDate(finalObject.getString("air_date")));
                                    tvShow.setEpisodeNumber(finalObject.getString("episode_number"));
                                    tvShow.setEpisodeName(finalObject.getString("name"));
                                    tvShow.setEpisodeOverview(finalObject.getString("overview"));
                                    tvShow.seteSeasonNumber(finalObject.getString("season_number"));
                                    tvShow.seteTvShowId(tvShowId);
                                    tvShow.setEpisodeStillPath(Contract.API_IMAGE_URL + finalObject.getString("still_path"));
                                    listEpisodes.add(tvShow);
                                }
                            }
                            layoutManagerEpisodes = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                            recyclerViewEpisodes = (RecyclerView) findViewById(R.id.recyclerViewEpisodes);
                            recyclerViewEpisodes.setLayoutManager(layoutManagerEpisodes);
                            recyclerViewEpisodes.setItemAnimator(new DefaultItemAnimator());
                            adapterEpisodes = new EpisodesAdapter(getApplicationContext(), listEpisodes);
                            recyclerViewEpisodes.setNestedScrollingEnabled(false);
                            recyclerViewEpisodes.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                            recyclerViewEpisodes.setAdapter(adapterEpisodes);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequestEpisodesList);

    }
}
