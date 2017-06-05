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
import com.merakiphi.idiotbox.activity.TvShowDetailsActivity;
import com.merakiphi.idiotbox.model.Movie;

import java.util.List;

/**
 * Created by anuragmaravi on 30/01/17.
 */

public class SimilarTvShowAdapter extends RecyclerView.Adapter<SimilarTvShowAdapter.MyViewHolder>  {

    private Context mContext;
    private List<Movie> movieList = null;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_similar, parent, false);
        return new SimilarTvShowAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Movie movie = movieList.get(position);
        Glide.with(mContext).load(movie.getSimilarPosterPath()).into(holder.imageViewSimilar);
        holder.imageViewSimilar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TvShowDetailsActivity.class);
                intent.putExtra("tvshow_id", movie.getSimilarId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public SimilarTvShowAdapter(Context mContext, List<Movie> movieList) {
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
