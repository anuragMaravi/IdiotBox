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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
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
    private List<Object> genreListMovieList= new ArrayList<>();
    private  RecyclerView.Adapter genreListAdapter;
    private RecyclerView.LayoutManager genreListLayoutManager;

    private ProgressBar progressBar;

    // A Native Express ad is placed in every nth position in the RecyclerView.
    public static final int ITEMS_PER_AD = 8;

    // The Native Express ad height.
    private static final int NATIVE_EXPRESS_AD_HEIGHT = 160;

    // The Native Express ad unit ID.
    private static final String AD_UNIT_ID = "ca-app-pub-3259009684379327/6728907881";

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
        String region = prefs.getString("country", "US"); //Default: India
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

        addNativeExpressAds();
        setUpAndLoadNativeExpressAds();

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

    /**
     * Adds Native Express ads to the items list.
     */
    private void addNativeExpressAds() {

        // Loop through the items array and place a new Native Express ad in every ith position in
        // the items List.
        for (int i = 0; i <= genreListMovieList.size(); i += ITEMS_PER_AD) {
            final NativeExpressAdView adView = new NativeExpressAdView(GenreListActivity.this);
            genreListMovieList.add(i, adView);
        }
    }

    /**
     * Sets up and loads the Native Express ads.
     */
    private void setUpAndLoadNativeExpressAds() {
        // Use a Runnable to ensure that the RecyclerView has been laid out before setting the
        // ad size for the Native Express ad. This allows us to set the Native Express ad's
        // width to match the full width of the RecyclerView.
        recyclerViewGenreList.post(new Runnable() {
            @Override
            public void run() {
                final float scale = GenreListActivity.this.getResources().getDisplayMetrics().density;
                // Set the ad size and ad unit ID for each Native Express ad in the items list.
                for (int i = 0; i <= genreListMovieList.size(); i += ITEMS_PER_AD) {
                    final NativeExpressAdView adView =
                            (NativeExpressAdView) genreListMovieList.get(i);
                    final LinearLayout cardView = (LinearLayout) findViewById(R.id.ad_card_view);
                    final int adWidth = cardView.getWidth() - cardView.getPaddingLeft()
                            - cardView.getPaddingRight();
                    AdSize adSize = new AdSize((int) (adWidth / scale), NATIVE_EXPRESS_AD_HEIGHT);
                    adView.setAdSize(adSize);
                    adView.setAdUnitId(AD_UNIT_ID);
                }

                // Load the first Native Express ad in the items list.
                loadNativeExpressAd(0);
            }
        });
    }

    /**
     * Loads the Native Express ads in the items list.
     */
    private void loadNativeExpressAd(final int index) {

        if (index >= genreListMovieList.size()) {
            return;
        }

        Object item = genreListMovieList.get(index);
        if (!(item instanceof NativeExpressAdView)) {
            throw new ClassCastException("Expected item at index " + index + " to be a Native"
                    + " Express ad.");
        }

        final NativeExpressAdView adView = (NativeExpressAdView) item;

        // Set an AdListener on the NativeExpressAdView to wait for the previous Native Express ad
        // to finish loading before loading the next ad in the items list.
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // The previous Native Express ad loaded successfully, call this method again to
                // load the next ad in the items list.
                loadNativeExpressAd(index + ITEMS_PER_AD);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // The previous Native Express ad failed to load. Call this method again to load
                // the next ad in the items list.
                Log.e("MainActivity", "The previous Native Express ad failed to load. Attempting to"
                        + " load the next Native Express ad in the items list.");
                loadNativeExpressAd(index + ITEMS_PER_AD);
            }
        });

        //Initialising AdMob
        MobileAds.initialize(this, "ca-app-pub-3259009684379327~5979085895");
        // Load the Native Express ad.
        adView.loadAd(new AdRequest.Builder().build());
    }

}
