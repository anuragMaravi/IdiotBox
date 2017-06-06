package com.merakiphi.idiotbox.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.merakiphi.idiotbox.R;
import com.merakiphi.idiotbox.activity.MovieDetailsActivity;
import com.merakiphi.idiotbox.model.SearchResults;

import java.util.List;

/**
 * Created by anuragmaravi on 30/01/17.
 */

public class GenreListAdapter extends RecyclerView.Adapter<GenreListAdapter.MyViewHolder>  {

    private Context mContext;
    private List<SearchResults> movieList = null;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_genre_list, parent, false);
        return new GenreListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final SearchResults movie = movieList.get(position);
        Glide.with(mContext).load(movie.getPosterPath()).into(holder.imageViewSearchPoster);
        holder.textViewName.setText(movie.getOriginalTitle());
        holder.textViewDate.setText(movie.getReleaseDate());
        holder.textViewVoteAverage.setText(movie.getVoteAverage());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MovieDetailsActivity.class);
                    intent.putExtra("movie_id", movie.getId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public GenreListAdapter(Context mContext, List<SearchResults> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewSearchPoster;
        public TextView textViewName, textViewDate, textViewVoteAverage;
        public MyViewHolder(View view) {
            super(view);
            imageViewSearchPoster = (ImageView) view.findViewById(R.id.imageViewSearchPoster);
            textViewName = (TextView) view.findViewById(R.id.textViewName);
            textViewDate = (TextView) view.findViewById(R.id.textViewDate);
            textViewVoteAverage = (TextView) view.findViewById(R.id.textViewVoteAverage);
        }
    }
}
