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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.merakiphi.idiotbox.R;
import com.merakiphi.idiotbox.adapter.GenreListAdapter;
import com.merakiphi.idiotbox.model.SearchResults;
import com.merakiphi.idiotbox.other.CheckInternet;
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
import static com.merakiphi.idiotbox.other.Contract.API_KEY;
import static com.merakiphi.idiotbox.other.Contract.API_URL;
import static com.merakiphi.idiotbox.other.Contract.LANGUAGE;
import static com.merakiphi.idiotbox.other.Contract.REGION;

public class GenreListActivity extends AppCompatActivity {
    String TAG;
    private String genreId;
    private SharedPreferences prefs;
    private String genreListRequest;

    private TextView textViewGenreTitle;
    //Genre List
    private RecyclerView recyclerViewGenreList;
    private List<SearchResults> genreListMovieList= new ArrayList<>();
    private  RecyclerView.Adapter genreListAdapter;
    private RecyclerView.LayoutManager genreListLayoutManager;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(CheckInternet.getInstance(getApplicationContext()).isNetworkConnected()){
        setContentView(R.layout.activity_genre_list);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle("");
        TAG = getClass().getSimpleName();
        genreId = getIntent().getStringExtra("genre_id");
        textViewGenreTitle = (TextView) findViewById(R.id.textViewGenreTitle);
        textViewGenreTitle.setText(getIntent().getStringExtra("genre_name"));
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);

            prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String region = prefs.getString("country", "IN"); //Default: India
        String language = prefs.getString("language", "en"); //Default: English

        /**
         * Genre
         */
        genreListRequest = API_URL + "genre/" + genreId + "/movies?api_key=" + API_KEY +
                //Language parameter
                LANGUAGE + language +
                //Region parameter
                REGION + region +

        Log.i(TAG, "New Request: " + genreListRequest);
        StringRequest stringRequestTmdb = new StringRequest(Request.Method.GET, genreListRequest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse(TMDb): " + response);
                        try {
                            parseAndDisplayData(response);
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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequestTmdb);

        } else {
            setNoInternetView();
        }
    }

    /**
     * Parse and display the data from tmdb
     */
    private void parseAndDisplayData(String response) throws JSONException, ParseException {
        final String poster_quality = prefs.getString("poster_size", "w342/"); //Default: Medium
        JSONObject parentObject= new JSONObject(response);
        JSONArray parentArray = parentObject.getJSONArray("results");
        for(int i=0;i<parentArray.length();i++) {
            JSONObject finalObject = parentArray.getJSONObject(i);
            SearchResults searchResults = new SearchResults();
            searchResults.setOriginalTitle(finalObject.getString("original_title"));
            searchResults.setPosterPath(API_IMAGE_BASE_URL + poster_quality + finalObject.getString("poster_path"));
            searchResults.setVoteAverage(finalObject.getString("vote_average"));
            searchResults.setId(finalObject.getString("id"));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(finalObject.getString("release_date"));
            String year = (String) DateFormat.format("yyyy", date);
            searchResults.setReleaseDate(year);
            genreListMovieList.add(searchResults);
        }
        //RecyclerView  TvShow Seasons
        genreListLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewGenreList = (RecyclerView) findViewById(R.id.recyclerViewGenreList);
        recyclerViewGenreList.setLayoutManager(genreListLayoutManager);
        recyclerViewGenreList.setItemAnimator(new DefaultItemAnimator());
        genreListAdapter = new GenreListAdapter(getApplicationContext(), genreListMovieList);
        recyclerViewGenreList.setVisibility(View.VISIBLE);
        recyclerViewGenreList.setAdapter(genreListAdapter);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
