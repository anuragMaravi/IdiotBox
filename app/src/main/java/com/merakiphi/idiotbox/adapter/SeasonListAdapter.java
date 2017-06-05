package com.merakiphi.idiotbox.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.merakiphi.idiotbox.R;
import com.merakiphi.idiotbox.activity.SeasonActivity;
import com.merakiphi.idiotbox.model.TvShow;

import java.util.List;

/**
 * Created by anuragmaravi on 05/02/17.
 */

public class SeasonListAdapter extends RecyclerView.Adapter<SeasonListAdapter.MyViewHolder>  {

    private Context mContext;
    private List<TvShow> movieList = null;

    @Override
    public SeasonListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tv_show_season_list, parent, false);
        return new SeasonListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SeasonListAdapter.MyViewHolder holder, int position) {
        final TvShow movie = movieList.get(position);
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

    public SeasonListAdapter(Context mContext, List<TvShow> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewSeasonNumber, textViewEpisodeCount;
        public MyViewHolder(View view) {
            super(view);
            textViewSeasonNumber = (TextView) view.findViewById(R.id.textViewSeasonNumber);
            textViewEpisodeCount = (TextView) view.findViewById(R.id.textViewEpisodeCount);
        }
    }
}
