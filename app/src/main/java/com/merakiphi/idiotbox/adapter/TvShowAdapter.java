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
import com.merakiphi.idiotbox.model.TvShow;

import java.util.List;

/**
 * Created by anuragmaravi on 30/01/17.
 */

public class TvShowAdapter extends RecyclerView.Adapter<TvShowAdapter.MyViewHolder>  {

    private Context mContext;
    private List<TvShow> movieList = null;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tvshow_main, parent, false);
        return new TvShowAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final TvShow movie = movieList.get(position);
        Glide.with(mContext).load(movie.getTvShowPosterPath()).into(holder.imageViewTvShow);
        holder.imageViewTvShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TvShowDetailsActivity.class);
                intent.putExtra("tvshow_id", movie.getTvShowId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public TvShowAdapter(Context mContext, List<TvShow> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewTvShow;
        public MyViewHolder(View view) {
            super(view);
            imageViewTvShow = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }
}
