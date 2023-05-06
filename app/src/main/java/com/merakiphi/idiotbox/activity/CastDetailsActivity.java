package com.merakiphi.idiotbox.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.merakiphi.idiotbox.R;
import com.merakiphi.idiotbox.adapter.CastingImagesAdapter;
import com.merakiphi.idiotbox.adapter.CastingMoviesAdapter;
import com.merakiphi.idiotbox.adapter.CastingTvShowsAdapter;
import com.merakiphi.idiotbox.model.Cast;
import com.merakiphi.idiotbox.model.Movie;
import com.merakiphi.idiotbox.other.CheckInternet;
import com.merakiphi.idiotbox.other.DateFormatter;
import com.merakiphi.idiotbox.other.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.merakiphi.idiotbox.other.Contract.API_CASTING;
import static com.merakiphi.idiotbox.other.Contract.API_IMAGE_BASE_URL;
import static com.merakiphi.idiotbox.other.Contract.API_IMAGE_SIZE_XXL;
import static com.merakiphi.idiotbox.other.Contract.API_KEY;
import static com.merakiphi.idiotbox.other.Contract.API_URL;
import static com.merakiphi.idiotbox.other.Contract.APPEND;
import static com.merakiphi.idiotbox.other.Contract.IMAGES;
import static com.merakiphi.idiotbox.other.Contract.LANGUAGE;
import static com.merakiphi.idiotbox.other.Contract.REGION;
import static com.merakiphi.idiotbox.other.Contract.SEPARATOR;

/**
 * Created by anuragmaravi on 31/01/17.
 */

public class CastDetailsActivity extends AppCompatActivity {
    public static String TAG;
    String castId, castDetailsRequest, castExternalIdsRequest;
    private TextView textViewDirector,
            textViewTitle,
            textViewVoteAverage,
            textViewOverview,
            textViewMovieOrTvShow,
            textViewTmdbVote,
            textViewCountry;
    private ImageView imageViewFollowImdb,
            imageViewFollowTwitter,
            imageViewFollowFacebook,
            imageViewFollowInstagram,
            imageViewPoster;
    private LinearLayout linearLayoutTitle;
    private ScrollView container;
    private ProgressBar progressBar;

    //Cast Images
    private RecyclerView recyclerViewImages;
    private List<Movie> castingList= new ArrayList<>();
    private  RecyclerView.Adapter adapterCasting;
    private RecyclerView.LayoutManager castingLayoutManager;

    //Cast Movies
    private RecyclerView recyclerViewCastingMovies;
    private List<Cast> castingListMovies= new ArrayList<>();
    private  RecyclerView.Adapter adapterCastingMovies;
    private RecyclerView.LayoutManager layoutManagerCastingMovies;

    //Cast Tv Shows
    private RecyclerView recyclerViewCastingTvShows;
    private List<Cast> castingListTvShows = new ArrayList<>();
    private  RecyclerView.Adapter adapterCastingTvShows;
    private RecyclerView.LayoutManager layoutManagerCastingTvShows;

    //To show or hide title box
    boolean isShown = true;

    //Ads
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String region = prefs.getString("country", "US"); //Default: India
        String language = prefs.getString("language", "en"); //Default: English
        final String poster_quality = prefs.getString("poster_size", "w342/"); //Default: Medium

        if(CheckInternet.getInstance(getApplicationContext()).isNetworkConnected()) {
            setContentView(R.layout.activity_cast_details);
            TAG = getClass().getSimpleName();
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            this.getSupportActionBar().setTitle("");
            castId = getIntent().getStringExtra("cast_id");

            //Views Initialisation
            container = (ScrollView) findViewById(R.id.container);
            textViewOverview = (TextView) findViewById(R.id.textViewOverview);
            textViewTitle = (TextView) findViewById(R.id.textViewTitle);
            textViewMovieOrTvShow = (TextView) findViewById(R.id.textViewMovieOrTvShow);
            textViewDirector = (TextView) findViewById(R.id.textViewDirector);
            textViewCountry = (TextView) findViewById(R.id.textViewCountry);
            textViewVoteAverage = (TextView) findViewById(R.id.textViewVoteAverage);
            imageViewFollowFacebook = (ImageView) findViewById(R.id.imageViewFollowFacebook);
            imageViewFollowTwitter = (ImageView) findViewById(R.id.imageViewFollowTwitter);
            imageViewFollowImdb = (ImageView) findViewById(R.id.imageViewFollowImdb);
            imageViewFollowInstagram = (ImageView) findViewById(R.id.imageViewFollowInstagram);

            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);

            linearLayoutTitle = (LinearLayout) findViewById(R.id.linearLayoutTitle);
            imageViewPoster = (ImageView) findViewById(R.id.imageViewPoster);
            imageViewPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isShown) {
                        linearLayoutTitle.setVisibility(View.INVISIBLE);
                        isShown = false;
                    } else {
                        linearLayoutTitle.setVisibility(View.VISIBLE);
                        isShown = true;
                    }

                }
            });

            /**
             * Cast Details
             */
            //Cast Details Request
            castDetailsRequest = API_URL + API_CASTING + "/" + castId + "?api_key=" + API_KEY +
                    //Language parameter
                    LANGUAGE + language +
                    //Region parameter
                    REGION + region +
                    APPEND + IMAGES + SEPARATOR + "external_ids" + SEPARATOR + "movie_credits" + SEPARATOR + "tv_credits";
            Log.i(TAG, "CastURL: " + castDetailsRequest);
            //request movie details
            StringRequest stringRequestCastDetails = new StringRequest(Request.Method.GET, castDetailsRequest,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i(TAG, "onResponse(Cast Details): " + response);
                            JSONObject parentObject = null;
                            try {
                                parentObject = new JSONObject(response);
                                Glide.with(getApplicationContext()).load(API_IMAGE_BASE_URL + API_IMAGE_SIZE_XXL + "/" + parentObject.getString("profile_path")).into((ImageView) findViewById(R.id.imageViewPoster));
                                textViewOverview.setText(parentObject.getString("biography"));
                                textViewTitle.setText(parentObject.getString("name"));
                                try {
                                    textViewCountry.setText("Born: " + parentObject.getString("place_of_birth"));
                                } catch (JSONException e) {
                                    textViewCountry.setText("Born: NA");
                                }

                                try {
                                    textViewDirector.setText("Birthday: " + DateFormatter.getInstance(getApplicationContext()).formatDate(parentObject.getString("birthday")));
                                } catch (JSONException e) {
                                    textViewDirector.setText("Birthday: NA");
                                }
                                if (parentObject.getInt("gender") == 1) {
                                    textViewMovieOrTvShow.setText("Female");
                                } else {
                                    textViewMovieOrTvShow.setText("Male");
                                }


                                //External IDs
                                final JSONObject externalObject = parentObject.getJSONObject("external_ids");

                                //Open Instagram Profile
                                if (externalObject.getString("instagram_id") != "null") {
                                    Log.i(TAG, "InstaId" + externalObject.getString("instagram_id"));
                                    imageViewFollowInstagram.setVisibility(View.VISIBLE);
                                    imageViewFollowInstagram.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setPackage("com.instagram.android");
                                                try {
                                                    intent.setData(Uri.parse("http://instagram.com/_u/" + externalObject.getString("instagram_id")));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                startActivity(intent);
                                            } catch (android.content.ActivityNotFoundException e) {
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                                            Uri.parse("https://www.instagram.com/" + externalObject.getString("instagram_id"))));
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                } else
                                    imageViewFollowInstagram.setVisibility(View.GONE);

                                //Open Twitter Profile
                                if (externalObject.getString("twitter_id") != "null") {
                                    imageViewFollowTwitter.setVisibility(View.VISIBLE);
                                    imageViewFollowTwitter.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = null;
                                            try {
                                                // get the Twitter app if possible
                                                intent.setPackage("com.twitter.android");
                                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=" + externalObject.getString("twitter_id")));
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            } catch (Exception e) {
                                                // no Twitter app, revert to browser
                                                try {
                                                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + externalObject.getString("twitter_id")));
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }
                                            startActivity(intent);
                                        }
                                    });
                                } else
                                    imageViewFollowInstagram.setVisibility(View.GONE);

                                //Open Facebook Profile
                                if (externalObject.getString("facebook_id") != "null") {
                                    imageViewFollowFacebook.setVisibility(View.VISIBLE);
                                    imageViewFollowFacebook.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = null;
                                            try {
                                                // get the Twitter app if possible
                                                intent.setPackage("com.facebook.katana");
                                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + externalObject.getString("facebook_id")));
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            } catch (Exception e) {
                                                // no Twitter app, revert to browser
                                                try {
                                                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + externalObject.getString("facebook_id")));
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }
                                            startActivity(intent);

                                        }
                                    });
                                } else
                                    imageViewFollowFacebook.setVisibility(View.GONE);

                                //Open Imdb Profile
                                if (externalObject.getString("imdb_id") != "null") {
                                    imageViewFollowImdb.setVisibility(View.VISIBLE);
                                    imageViewFollowImdb.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = null;
                                            try {
                                                // get the Twitter app if possible
                                                intent.setPackage("com.imdb.mobile");
                                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("imdb:///name/" + externalObject.getString("imdb_id")));
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            } catch (Exception e) {
                                                // no Twitter app, revert to browser
                                                try {
                                                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.imdb.com/name/" + externalObject.getString("imdb_id")));
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }
                                            startActivity(intent);
                                        }
                                    });
                                } else
                                    imageViewFollowImdb.setVisibility(View.GONE);

                                //Images
                                JSONObject imagesObject = parentObject.getJSONObject("images");
                                JSONArray imagesArray = imagesObject.getJSONArray("profiles");
                                for (int i = 0; i < imagesArray.length(); i++) {
                                    JSONObject finalObject = imagesArray.getJSONObject(i);
                                    Movie movieModel = new Movie();
                                    movieModel.setCastingProfilePath(API_IMAGE_BASE_URL + poster_quality + finalObject.getString("file_path"));
                                    castingList.add(movieModel);
                                }
                                castingLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                                recyclerViewImages = (RecyclerView) findViewById(R.id.recyclerViewImages);
                                recyclerViewImages.setLayoutManager(castingLayoutManager);
                                recyclerViewImages.setItemAnimator(new DefaultItemAnimator());
                                adapterCasting = new CastingImagesAdapter(getApplicationContext(), castingList, parentObject.getInt("id"));
                                recyclerViewImages.setAdapter(adapterCasting);

                                //Movies
                                JSONObject moviesObject = parentObject.getJSONObject("movie_credits");
                                JSONArray moviesArray = moviesObject.getJSONArray("cast");
                                for (int i = moviesArray.length() - 1; i >= 0; i--) {
                                    JSONObject finalObject = moviesArray.getJSONObject(i);
                                    Cast cast = new Cast();
                                    try {
                                        cast.setCastMovieCharacter(finalObject.getString("character"));
                                    } catch (JSONException e) {
                                        cast.setCastMovieCharacter("NA");
                                    }
                                    cast.setCastMovieTitle(finalObject.getString("original_title"));
                                    cast.setCastMovieId(finalObject.getString("id"));
                                    cast.setCastMoviePosterPath(API_IMAGE_BASE_URL + poster_quality + finalObject.getString("poster_path"));
                                    castingListMovies.add(cast);
                                }
                                layoutManagerCastingMovies = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                                recyclerViewCastingMovies = (RecyclerView) findViewById(R.id.recyclerViewCastingMovies);
                                recyclerViewCastingMovies.setLayoutManager(layoutManagerCastingMovies);
                                recyclerViewCastingMovies.setItemAnimator(new DefaultItemAnimator());
                                adapterCastingMovies = new CastingMoviesAdapter(getApplicationContext(), castingListMovies);
                                recyclerViewCastingMovies.setAdapter(adapterCastingMovies);


                                //Tv Shows
                                JSONObject tvShowsObject = parentObject.getJSONObject("tv_credits");
                                JSONArray tvShowsArray = tvShowsObject.getJSONArray("cast");
                                for (int i = tvShowsArray.length() - 1; i >= 0; i--) {
                                    JSONObject finalObject = tvShowsArray.getJSONObject(i);
                                    Cast cast = new Cast();
                                    cast.setCastTvShowCharacter(finalObject.getString("character"));
                                    cast.setCastTvShowTitle(finalObject.getString("original_name"));
                                    cast.setCastTvShowId(finalObject.getString("id"));
                                    cast.setCastTvShowPosterPath(API_IMAGE_BASE_URL + poster_quality + finalObject.getString("poster_path"));
                                    castingListTvShows.add(cast);
                                }
                                layoutManagerCastingTvShows = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                                recyclerViewCastingTvShows = (RecyclerView) findViewById(R.id.recyclerViewCastingTvShows);
                                recyclerViewCastingTvShows.setLayoutManager(layoutManagerCastingTvShows);
                                recyclerViewCastingTvShows.setItemAnimator(new DefaultItemAnimator());
                                adapterCastingTvShows = new CastingTvShowsAdapter(getApplicationContext(), castingListTvShows);
                                recyclerViewCastingTvShows.setAdapter(adapterCastingTvShows);
                                container.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                Log.i(TAG, "Error: " + e.getMessage());
                                e.printStackTrace();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse: ", error);
                    Toast.makeText(getApplicationContext(), "Some Error Occurred", Toast.LENGTH_SHORT).show();
                }
            });
            // Add the request to the RequestQueue.
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequestCastDetails);

            /**
             * Ads
             */
            //Initialising AdMob
            MobileAds.initialize(this, "ca-app-pub-3259009684379327/8932552299");

            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
//            mAdView = (AdView) findViewById(R.id.adView);
//
//            AdRequest adRequest = new AdRequest.Builder().build();
//            mAdView.loadAd(adRequest);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
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
