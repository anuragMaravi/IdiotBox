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
import com.merakiphi.idiotbox.activity.EpisodeActivity;
import com.merakiphi.idiotbox.model.TvShow;

import java.util.List;

/**
 * Created by anuragmaravi on 30/01/17.
 */

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.MyViewHolder>  {

    private Context mContext;
    private List<TvShow> movieList = null;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_episodes, parent, false);
        return new EpisodesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final TvShow movie = movieList.get(position);
        Glide.with(mContext).load(movie.getEpisodeStillPath()).into(holder.imageViewEpisode);
//        Glide.with(mContext).load(movie.getTvShowBackdropPath()).into(holder.imageViewBackdrop);
        holder.textViewEpisodeNumber.setText("EP " +movie.getEpisodeNumber() + " ");
        holder.textViewEpisodeName.setText(movie.getEpisodeName());
        holder.textViewAirDate.setText(movie.getEpisodeAirDate());
        holder.textViewEpisodeOverview.setText(movie.getEpisodeOverview());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EpisodeActivity.class);
                intent.putExtra("episode_number", movie.getEpisodeNumber());
                intent.putExtra("season_number", movie.geteSeasonNumber());
                intent.putExtra("tvshow_id", movie.geteTvShowId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public EpisodesAdapter(Context mContext, List<TvShow> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewEpisode;
        public TextView textViewEpisodeNumber, textViewEpisodeName, textViewAirDate, textViewEpisodeOverview;
        public MyViewHolder(View view) {
            super(view);
            imageViewEpisode = (ImageView) view.findViewById(R.id.imageViewEpisode);
            textViewEpisodeNumber = (TextView) view.findViewById(R.id.textViewEpisodeNumber);
            textViewEpisodeName = (TextView) view.findViewById(R.id.textViewEpisodeName);
            textViewAirDate = (TextView) view.findViewById(R.id.textViewAirDate);
            textViewEpisodeOverview = (TextView) view.findViewById(R.id.textViewEpisodeOverview);
        }
    }
}
