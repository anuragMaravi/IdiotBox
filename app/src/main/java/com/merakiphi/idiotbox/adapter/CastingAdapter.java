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
import com.merakiphi.idiotbox.activity.CastDetailsActivity;
import com.merakiphi.idiotbox.model.Movie;

import java.util.List;

/**
 * Created by anuragmaravi on 30/01/17.
 */

public class CastingAdapter extends RecyclerView.Adapter<CastingAdapter.MyViewHolder>  {

    private Context mContext;
    private List<Movie> movieList = null;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_casting, parent, false);
        return new CastingAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Movie movie = movieList.get(position);
        Glide.with(mContext).load(movie.getCastingProfilePath()).into(holder.imageViewCasting);
        holder.textViewCastingAs.setText(movie.getCastingCharacter());
        holder.textViewCastingName.setText(movie.getCastingName());
        holder.imageViewCasting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CastDetailsActivity.class);
                intent.putExtra("cast_id", movie.getCastingId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public CastingAdapter(Context mContext, List<Movie> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewCasting;
        public TextView textViewCastingName, textViewCastingAs;
        public MyViewHolder(View view) {
            super(view);
            imageViewCasting = (ImageView) view.findViewById(R.id.imageViewCasting);
            textViewCastingAs = (TextView)view.findViewById(R.id.textViewCastingAs);
            textViewCastingName = (TextView)view.findViewById(R.id.textViewCastingName);
        }
    }
}
