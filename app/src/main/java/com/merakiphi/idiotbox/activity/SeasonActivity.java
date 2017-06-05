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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.merakiphi.idiotbox.R;
import com.merakiphi.idiotbox.adapter.EpisodesAdapter;
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

/**
 * Created by anuragmaravi on 04/02/17.
 */

public class SeasonActivity extends AppCompatActivity {
    String TAG;
    String episodeId, seasonNumber, tvShowId, tvShowName;

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

    //Episodes
    private RecyclerView recyclerViewEpisodes;
    private List<TvShow> listEpisodes= new ArrayList<>();
    private  RecyclerView.Adapter adapterEpisodes;
    private RecyclerView.LayoutManager layoutManagerEpisodes;
    //To show or hide title box
    boolean isShown = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CheckInternet.getInstance(getApplicationContext()).isNetworkConnected()) {
            setContentView(R.layout.activity_season);
        TAG = getClass().getSimpleName();
        tvShowName = getIntent().getStringExtra("tvshow_name");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle("Name");
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
        linearLayoutTitle = (LinearLayout) findViewById(R.id.linearLayoutTitle);

            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.tv_show_accent), android.graphics.PorterDuff.Mode.MULTIPLY);

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

        //Method to show TVshow details
        displayTvShowEpisodes();

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
     * Parse data for TV Show Details and show it.
     */
    private void displayTvShowEpisodes(){
        String seasonUrl = API_URL + Contract.API_TV + "/" + tvShowId + "/season/" + seasonNumber + "?api_key=" + Contract.API_KEY;
        StringRequest stringRequestEpisodes = new StringRequest(Request.Method.GET, seasonUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse(TvShow Season Details): " + response);
                        try {
                            JSONObject parentObject= new JSONObject(response);
                            Glide.with(getApplicationContext()).load(API_IMAGE_BASE_URL + API_IMAGE_SIZE_XXL + "/" + parentObject.getString("poster_path")).into((ImageView) findViewById(R.id.imageViewPoster));
                            textViewOverview.setText(parentObject.getString("overview"));
                            textViewTitle.setText(parentObject.getString("name"));
                            textViewMovieOrTvShow.setText(DateFormatter.getInstance(getApplicationContext()).formatDate(parentObject.getString("air_date")));
                            JSONArray parentArray = parentObject.getJSONArray("episodes");
                            for(int i=0;i<parentArray.length();i++) {
                                JSONObject finalObject = parentArray.getJSONObject(i);
                                TvShow tvShow = new TvShow();
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
                            layoutManagerEpisodes = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                            recyclerViewEpisodes = (RecyclerView) findViewById(R.id.recyclerViewEpisodes);
                            recyclerViewEpisodes.setLayoutManager(layoutManagerEpisodes);
                            recyclerViewEpisodes.setItemAnimator(new DefaultItemAnimator());
                            adapterEpisodes = new EpisodesAdapter(getApplicationContext(), listEpisodes);
                            recyclerViewEpisodes.setNestedScrollingEnabled(false);
                            recyclerViewEpisodes.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                            recyclerViewEpisodes.setAdapter(adapterEpisodes);
                            container.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Some Error Occurred.", Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequestEpisodes);

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
