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
import com.merakiphi.idiotbox.activity.CastImageActivity;
import com.merakiphi.idiotbox.model.Movie;

import java.util.List;

/**
 * Created by anuragmaravi on 31/01/17.
 */

public class CastingImagesAdapter extends RecyclerView.Adapter<CastingImagesAdapter.MyViewHolder>  {

    private Context mContext;
    private List<Movie> movieList = null;
    private String profileId;


    @Override
    public CastingImagesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_similar, parent, false);
        return new CastingImagesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CastingImagesAdapter.MyViewHolder holder, final int position) {
        final Movie movie = movieList.get(position);
        Glide.with(mContext).load(movie.getCastingProfilePath()).into(holder.imageViewSimilar);
        holder.imageViewSimilar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CastImageActivity.class);
                intent.putExtra("image_path", movie.getCastingProfilePath());
                intent.putExtra("profileId", profileId);
                intent.putExtra("position", position);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public CastingImagesAdapter(Context mContext, List<Movie> movieList, int profileId) {
        this.mContext = mContext;
        this.movieList = movieList;
        this.profileId = String.valueOf(profileId);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewSimilar;
        public MyViewHolder(View view) {
            super(view);
            imageViewSimilar = (ImageView) view.findViewById(R.id.imageViewSimilar);
        }
    }
}
