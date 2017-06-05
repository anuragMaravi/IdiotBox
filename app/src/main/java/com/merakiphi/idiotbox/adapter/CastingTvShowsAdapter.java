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
import com.merakiphi.idiotbox.activity.TvShowDetailsActivity;
import com.merakiphi.idiotbox.model.Cast;

import java.util.List;

/**
 * Created by anuragmaravi on 31/01/17.
 */

public class CastingTvShowsAdapter extends RecyclerView.Adapter<CastingTvShowsAdapter.MyViewHolder>  {

    private Context mContext;
    private List<Cast> castMovieList = null;

    @Override
    public CastingTvShowsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_casting_tvshows, parent, false);
        return new CastingTvShowsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CastingTvShowsAdapter.MyViewHolder holder, int position) {
        final Cast cast = castMovieList.get(position);
        Glide.with(mContext).load(cast.getCastTvShowPosterPath()).into(holder.imageViewCasting);
        holder.textViewCastingAs.setText(cast.getCastTvShowCharacter());
        holder.textViewMovieName.setText(cast.getCastTvShowTitle());
        holder.imageViewCasting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TvShowDetailsActivity.class);
                intent.putExtra("tvshow_id", cast.getCastTvShowId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return castMovieList.size();
    }

    public CastingTvShowsAdapter(Context mContext, List<Cast> castMovieList) {
        this.mContext = mContext;
        this.castMovieList = castMovieList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewCasting;
        TextView textViewCastingAs, textViewMovieName;
        public MyViewHolder(View view) {
            super(view);
            imageViewCasting = (ImageView) view.findViewById(R.id.imageViewCasting);
            textViewCastingAs = (TextView) view.findViewById(R.id.textViewCastingAs);
            textViewMovieName = (TextView) view.findViewById(R.id.textViewMovieName);
        }
    }
}
