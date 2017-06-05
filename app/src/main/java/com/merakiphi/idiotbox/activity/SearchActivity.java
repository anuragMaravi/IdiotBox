package com.merakiphi.idiotbox.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.merakiphi.idiotbox.R;
import com.merakiphi.idiotbox.adapter.SearchResultAdapter;
import com.merakiphi.idiotbox.model.SearchResults;
import com.merakiphi.idiotbox.other.CheckInternet;
import com.merakiphi.idiotbox.other.DateFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.merakiphi.idiotbox.other.Contract.API_IMAGE_BASE_URL;
import static com.merakiphi.idiotbox.other.Contract.API_IMAGE_SIZE_M;
import static com.merakiphi.idiotbox.other.Contract.API_KEY;

/**
 * Created by anuragmaravi on 13/02/17.
 */

public class SearchActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener, SearchView.OnCloseListener{
    private SearchView searchView;
    private SearchManager searchManager;
    private MenuItem menuItem;
    private static String TAG;

    //Casting of Tv Shows
    private RecyclerView recyclerViewSearchResults;
    private  RecyclerView.Adapter adapterSearchResults;
    private RecyclerView.LayoutManager layoutManagerSearchResults;

    private ProgressBar progressBar;
    private RequestQueue queue;
    private static final String VOLLEY_TAG = "VT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CheckInternet.getInstance(getApplicationContext()).isNetworkConnected()) {
            setContentView(R.layout.activity_search);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle("");
        TAG = SearchActivity.class.getSimpleName();
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //Initialise RecyclerView
        layoutManagerSearchResults = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewSearchResults = (RecyclerView) findViewById(R.id.recyclerViewSearchResults);
        recyclerViewSearchResults.setLayoutManager(layoutManagerSearchResults);
        recyclerViewSearchResults.setItemAnimator(new DefaultItemAnimator());

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
        queue = Volley.newRequestQueue(this);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        menuItem = menu.findItem(R.id.search);
        menuItem.expandActionView();
        MenuItemCompat.setOnActionExpandListener(menuItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        // Return true to allow the action view to expand
//                        townList.setVisibility(View.VISIBLE);
                        return true;
                    }
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        onBackPressed();
                        return false;
                    }
                });
        if(CheckInternet.getInstance(getApplicationContext()).isNetworkConnected()) {

            searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(true);
            searchView.setOnQueryTextListener(this);
            searchView.setOnCloseListener(this);
            searchView.requestFocus();
        }
        return true;
    }
    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        sendRequest(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(CheckInternet.getInstance(getApplicationContext()).isNetworkConnected()) {

            if (newText.length() != 0) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerViewSearchResults.setVisibility(View.GONE);
                queue.cancelAll(VOLLEY_TAG);
                sendRequest(newText);
            } else {
                progressBar.setVisibility(View.GONE);
                recyclerViewSearchResults.setVisibility(View.GONE);
            }

        }
        return false;
    }

    public void sendRequest(String query){
        query = query.replace(" ", "+");
        final List<SearchResults> searchResultsList= new ArrayList<>();
        String urlSearch =  "https://api.themoviedb.org/3/search/multi?api_key="+ API_KEY + "&language=en-US&page=1&include_adult=false&query=" + query;
        StringRequest stringRequestEpisodes = new StringRequest(Request.Method.GET, urlSearch,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse(Search): " + response);
                        try {
                            JSONObject parentObject= new JSONObject(response);
                            JSONArray parentArray = parentObject.getJSONArray("results");
                            for(int i=0;i<parentArray.length();i++){
                                JSONObject finalObject = parentArray.getJSONObject(i);
                                SearchResults searchResults = new SearchResults();
                                searchResults.setPosterPath(API_IMAGE_BASE_URL + API_IMAGE_SIZE_M + "/" + finalObject.getString("poster_path"));
                                searchResults.setId(finalObject.getString("id"));
                                searchResults.setMediaType(finalObject.getString("media_type"));
                                if(finalObject.getString("media_type").equals("movie")) {
                                    searchResults.setReleaseDate(DateFormatter.getInstance(getApplicationContext()).formatDate(finalObject.getString("release_date")));
                                    searchResults.setOriginalTitle(finalObject.getString("original_title"));
                                }
                                if(finalObject.getString("media_type").equals("tv")){
                                    searchResults.setReleaseDate(DateFormatter.getInstance(getApplicationContext()).formatDate(finalObject.getString("first_air_date")));
                                    searchResults.setOriginalTitle(finalObject.getString("original_name"));
                                }
                                searchResultsList.add(searchResults);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        adapterSearchResults = new SearchResultAdapter(getApplicationContext(), searchResultsList);
                        recyclerViewSearchResults.setVisibility(View.VISIBLE);
                        recyclerViewSearchResults.setAdapter(adapterSearchResults);
                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        stringRequestEpisodes.setTag(VOLLEY_TAG);
        queue.add(stringRequestEpisodes);

    }
}