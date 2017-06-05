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
import com.merakiphi.idiotbox.activity.SeasonActivity;
import com.merakiphi.idiotbox.model.TvShow;

import java.util.List;

/**
 * Created by anuragmaravi on 30/01/17.
 */

public class TvShowSeasonsAdapter extends RecyclerView.Adapter<TvShowSeasonsAdapter.MyViewHolder>  {

    private Context mContext;
    private List<TvShow> movieList = null;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tvshow_casting, parent, false);
        return new TvShowSeasonsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final TvShow movie = movieList.get(position);
        Glide.with(mContext).load(movie.getTvShowSeasonPosterPath()).into(holder.imageViewPoster);
        holder.textViewSeasonNumber.setText("Season " + movie.getTvShowSeasonNumber());
holder.textViewEpisodeCount.setText(" (" + movie.getTvShowSeasonEpisodeCount() + " episodes)");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SeasonActivity.class);
                intent.putExtra("tvshow_id", movie.getsTvShowId());
                intent.putExtra("tvshow_name", movie.getTvShowNetworkName());
                intent.putExtra("season_number", movie.getTvShowSeasonNumber());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public TvShowSeasonsAdapter(Context mContext, List<TvShow> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
public TextView textViewSeasonNumber, textViewEpisodeCount;
        private ImageView imageViewPoster;
public MyViewHolder(View view) {
            super(view);
    textViewSeasonNumber = (TextView) view.findViewById(R.id.textViewCastingName);
    textViewEpisodeCount = (TextView) view.findViewById(R.id.textViewCastingAs);
    imageViewPoster = (ImageView) view.findViewById(R.id.imageViewCasting);
        }
    }
}
