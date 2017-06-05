package com.merakiphi.idiotbox.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.merakiphi.idiotbox.R;
import com.merakiphi.idiotbox.activity.MovieDetailsActivity;
import com.merakiphi.idiotbox.model.Movie;

import java.util.List;

/**
 * Created by anuragmaravi on 30/01/17.
 */

public class SimilarAdapter extends RecyclerView.Adapter<SimilarAdapter.MyViewHolder>  {

    private Context mContext;
    private List<Movie> movieList = null;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_similar, parent, false);
        return new SimilarAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Movie movie = movieList.get(position);
        Glide.with(mContext).load(movie.getSimilarPosterPath()).into(holder.imageViewSimilar);
        holder.imageViewSimilar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MovieDetailsActivity.class);
                intent.putExtra("movie_id", movie.getSimilarId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public SimilarAdapter(Context mContext, List<Movie> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewSimilar;
        public MyViewHolder(View view) {
            super(view);
            imageViewSimilar = (ImageView) view.findViewById(R.id.imageViewSimilar);
        }
    }
}
